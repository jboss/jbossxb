/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;
import org.apache.xerces.xs.XSTypeDefinition;

/**
 * todo: to improve performance, consider gathering all the necessary binding metadata in startElement and
 * pushing it instead of ElementBinding into elementStack to free the endElement implementation from
 * re-gathering this same metadata again.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SundayContentHandler
   implements JBossXBParser.ContentHandler
{
   private final static Logger log = Logger.getLogger(SundayContentHandler.class);

   private final static Object NIL = new Object();

   private final SchemaBinding schema;
   private final StackImpl elementStack = new StackImpl();
   private final StackImpl objectStack = new StackImpl();
   private StringBuffer textContent = new StringBuffer();
   private Object root;
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   public SundayContentHandler(SchemaBinding cursor)
   {
      this.schema = cursor;
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

         if(o != NIL)
         {
            //
            // characters
            //

            CharactersHandler simpleType = typeBinding.getSimpleType();
            if(textContent.length() > 0 || simpleType != null)
            {
               String dataContent;
               if(textContent.length() == 0)
               {
                  dataContent = null;
               }
               else
               {
                  dataContent = textContent.toString();
                  textContent.delete(0, textContent.length());
               }

               Object unmarshalled;

               if(simpleType == null)
               {
                  if(schema.isStrictSchema())
                  {
                     throw new JBossXBRuntimeException("Element " +
                        endName +
                        " type binding " +
                        typeBinding.getQName() +
                        " does not include text content binding ('" + dataContent
                     );
                  }
                  unmarshalled = dataContent;
               }
               else
               {
                  ValueMetaData valueMetaData = elementBinding.getValueMetaData();
                  if(valueMetaData == null)
                  {
                     valueMetaData = typeBinding.getValueMetaData();
                     if(valueMetaData == null)
                     {
                        CharactersMetaData charactersMetaData = typeBinding.getCharactersMetaData();
                        if(charactersMetaData != null)
                        {
                           valueMetaData = charactersMetaData.getValue();
                        }
                     }
                  }

                  // todo valueMetaData is available from typeBinding
                  unmarshalled = dataContent == null ?
                     simpleType.unmarshalEmpty(endName, typeBinding, nsRegistry, valueMetaData) :
                     simpleType.unmarshal(endName, typeBinding, nsRegistry, valueMetaData, dataContent);
               }

               if(unmarshalled != null)
               {
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
         }
         else
         {
            o = null;
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
            ElementBinding parentElement = elementStack.isEmpty() ? null : (ElementBinding)elementStack.peek();
            if(parent != null)
            {
               typeBinding.getHandler().setParent(parent, o, endName, elementBinding, parentElement);
            }
            else if(parentElement != null)
            {
               if(parentElement.getType().getSchemaResolver() != null)
               {
                  // todo: review this> the parent has anyType, so it gets the value of its child
                  if(!objectStack.isEmpty())
                  {
                     objectStack.pop();
                     objectStack.push(o);
                     if(log.isTraceEnabled())
                     {
                        log.trace("Value of " + endName + " " + o + " is promoted as the value of its parent element.");
                     }
                  }
               }
            }
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

      ElementBinding parentBinding = null;
      if(elementStack.isEmpty())
      {
         binding = schema.getElement(startName);
      }
      else
      {
         parentBinding = (ElementBinding)elementStack.peek();
         if(parentBinding != null)
         {
            binding = parentBinding.getType().getElement(startName, atts);
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
            o = NIL;
         }
         objectStack.push(o);

         if(o != null && o != NIL)
         {
            // Expand the attributes list with any missing attrs with defaults
            atts = typeBinding.expandWithDefaultAttributes(atts);
            typeBinding.attributes(o, startName, binding, atts, nsRegistry);
         }
      }
      else if(schema.isStrictSchema())
      {
         throw new JBossXBRuntimeException("Element " +
            startName +
            " is not bound " +
            (parentBinding == null ? "as a global element." : "in type " + parentBinding.getType().getQName())
         );
      }
      else if(log.isTraceEnabled())
      {
         log.trace("Element " +
            startName +
            " is not bound " +
            (parentBinding == null ? "as a global element." : "in type " + parentBinding.getType().getQName())
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
