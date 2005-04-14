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

   private final DocumentHandler cursor;
   private final StackImpl elementStack = new StackImpl();
   private final StackImpl objectStack = new StackImpl();
   private StringBuffer textContent = new StringBuffer();
   private Object root;

   public SundayContentHandler(DocumentHandler cursor)
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

         TypeBinding typeBinding = elementBinding.getTypeBinding();
         List elementHandlers = elementBinding.getElementHandlers();

         //
         // characters
         //

         if(textContent.length() > 0)
         {
            String dataContent = textContent.toString();
            textContent.delete(0, textContent.length());

            typeBinding.characters(o, endName, dataContent);

            int i = elementHandlers.size();
            while(i-- > 0)
            {
               ElementHandler handler = (ElementHandler)elementHandlers.get(i);
               handler.characters(objectStack.peek(elementHandlers.size() - 1 - i), endName, dataContent);
            }
         }

         //
         // endElement
         //

         Object parent = objectStack.isEmpty() ? null : objectStack.peek();
         o = typeBinding.endElement(parent, o, endName);

         int i = elementHandlers.size();
         while(i-- > 0)
         {
            ElementHandler handler = (ElementHandler)elementHandlers.get(i);
            handler.endElement(objectStack.peek(elementHandlers.size() - 1 - i), endName);
         }

         //
         // setParent
         //

         i = elementHandlers.size();
         while(i-- > 0)
         {
            ElementHandler handler = (ElementHandler)elementHandlers.get(i);
            parent = objectStack.pop();
            handler.add(parent, o, endName);
            o = parent;
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
         binding = cursor.getTypeBinding(startName);
      }
      else
      {
         ElementBinding parentBinding = (ElementBinding)elementStack.peek();
         if(parentBinding != null)
         {
            binding = parentBinding.getTypeBinding().getElement(startName);
         }
      }

      elementStack.push(binding);

      if(binding != null)
      {
         Object o = objectStack.isEmpty() ? null : objectStack.peek();

         List elementHandlers = binding.getElementHandlers();
         for(int i = 0; i < elementHandlers.size(); ++i)
         {
            ElementHandler handler = (ElementHandler)elementHandlers.get(i);
            o = handler.startElement(o, startName);
            objectStack.push(o);
            handler.attributes(o, startName, binding.getTypeBinding(), atts);
         }

         TypeBinding typeBinding = binding.getTypeBinding();
         o = typeBinding.startElement(o, startName);
         objectStack.push(o);

         typeBinding.attributes(o, startName, atts);
      }
      else
      {
         log.warn("Element " + startName + " is ignored.");
      }
   }

   public void startPrefixMapping(String prefix, String uri)
   {
   }

   public void endPrefixMapping(String prefix)
   {
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
