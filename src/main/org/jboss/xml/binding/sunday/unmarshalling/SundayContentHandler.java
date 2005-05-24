/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.NamespaceRegistry;
import org.jboss.xml.binding.metadata.JaxbProperty;
import org.jboss.xml.binding.metadata.JaxbBaseType;
import org.jboss.xml.binding.metadata.JaxbJavaType;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;
import org.apache.xerces.xs.XSTypeDefinition;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SundayContentHandler
   implements JBossXBParser.ContentHandler
{
   private final static Logger log = Logger.getLogger(SundayContentHandler.class);

   private final SchemaBinding cursor;
   private final StackImpl elementStack = new StackImpl();
   private final StackImpl objectStack = new StackImpl();
   private StringBuffer textContent = new StringBuffer();
   private Object root;
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   public SundayContentHandler(SchemaBinding cursor)
   {
      this.cursor = cursor;
   }

   public void characters(char[] ch, int start, int length)
   {
      if(elementStack.peek() != null)
      {
         textContent.append(ch, start, length);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      ElementBinding elementBinding = (ElementBinding)elementStack.pop();
      if(elementBinding != null)
      {
         QName endName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
         Object o = objectStack.pop();

         TypeBinding typeBinding = elementBinding.getType();
         List elementHandlers = elementBinding.getInterceptors();

         //
         // characters
         //

         if(textContent.length() > 0)
         {
            String dataContent = textContent.toString();
            textContent.delete(0, textContent.length());

            CharactersHandler simpleType = typeBinding.getSimpleType();
            Object unmarshalled;

            if(simpleType == null)
            {
               unmarshalled = dataContent;
            }
            else
            {
               JaxbJavaType jaxbJavaType = null;
               JaxbProperty jaxbProperty = elementBinding.getJaxbProperty();
               if(jaxbProperty != null)
               {
                  JaxbBaseType baseType = jaxbProperty.getBaseType();
                  jaxbJavaType = baseType == null ? null : baseType.getJavaType();
               }
               unmarshalled = simpleType.unmarshal(endName, typeBinding, nsRegistry, jaxbJavaType, dataContent);
            }
            
            // if startElement returned null, we use characters as the object for this element
            // todo subject to refactoring
            if(o == null)
            {
               o = unmarshalled;
            }
            else if(simpleType != null)
            {
               simpleType.setValue(endName, elementBinding, o, unmarshalled);
            }

            // todo interceptors get dataContent?
            int i = elementHandlers.size();
            while(i-- > 0)
            {
               ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
               interceptor.characters(objectStack.peek(elementHandlers.size() - 1 - i),
                  endName, typeBinding, nsRegistry, dataContent
               );
            }
         }

         //
         // endElement
         //

         Object parent = objectStack.isEmpty() ? null : objectStack.peek();
         o = typeBinding.endElement(parent, o, elementBinding, endName);

         int i = elementHandlers.size();
         while(i-- > 0)
         {
            ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
            interceptor.endElement(objectStack.peek(elementHandlers.size() - 1 - i), endName, typeBinding);
         }

         //
         // setParent
         //

         i = elementHandlers.size();
         // todo yack...
         if(i == 0)
         {
            typeBinding.getHandler().setParent(parent, o, endName, elementBinding);
         }
         else
         {
            while(i-- > 0)
            {
               ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
               parent = objectStack.pop();
               interceptor.add(parent, o, endName);
               o = parent;
            }
         }

         if(objectStack.isEmpty())
         {
            root = o;
         }
      }
   }

   public void startElement(String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts,
                            XSTypeDefinition type)
   {
      QName startName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      ElementBinding binding = null;

      if(elementStack.isEmpty())
      {
         binding = cursor.getElement(startName);
      }
      else
      {
         ElementBinding parentBinding = (ElementBinding)elementStack.peek();
         if(parentBinding != null)
         {
            binding = parentBinding.getType().getElement(startName);
         }
      }

      elementStack.push(binding);

      if(binding != null)
      {
         TypeBinding typeBinding = binding.getType();
         Object o = objectStack.isEmpty() ? null : objectStack.peek();

         List elementHandlers = binding.getInterceptors();
         for(int i = 0; i < elementHandlers.size(); ++i)
         {
            ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
            o = interceptor.startElement(o, startName, typeBinding);
            objectStack.push(o);
            interceptor.attributes(o, startName, typeBinding, atts, nsRegistry);
         }

         // todo xsi:nil handling
         String nil = atts.getValue("xsi:nil");
         if(nil == null || !("1".equals(nil) || "true".equals(nil)))
         {
            o = typeBinding.startElement(o, startName, binding);
         }
         else
         {
            o = null;
         }
         objectStack.push(o);

         if(o != null)
         {
            typeBinding.attributes(o, startName, binding, atts, nsRegistry);
         }
      }
      else if(log.isTraceEnabled())
      {
         log.trace(
            "Element " +
            startName +
            " is not bound as a " +
            (elementStack.isEmpty() ? "global element." : "child element.")
         );
      }
   }

   public void startPrefixMapping(String prefix, String uri)
   {
      nsRegistry.addPrefixMapping(prefix, uri);
   }

   public void endPrefixMapping(String prefix)
   {
      nsRegistry.removePrefixMapping(prefix);
   }

   public void processingInstruction(String target, String data)
   {
   }

   public Object getRoot()
   {
      return root;
   }

   // Inner

   static class StackImpl
   {
      private LinkedList list = new LinkedList();

      public void clear()
      {
         list.clear();
      }

      public void push(Object o)
      {
         list.addLast(o);
      }

      public Object pop()
      {
         return list.removeLast();
      }

      public Object peek()
      {
         return list.getLast();
      }

      public Object peek(int i)
      {
         return list.get(list.size() - 1 - i);
      }

      public boolean isEmpty()
      {
         return list.isEmpty();
      }

      public int size()
      {
         return list.size();
      }
   }
}
