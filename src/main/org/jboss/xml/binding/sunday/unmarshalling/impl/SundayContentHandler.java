/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.sunday.unmarshalling.ObjectModelStack;
import org.jboss.xml.binding.sunday.unmarshalling.DocumentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;
import org.apache.xerces.xs.XSTypeDefinition;

import javax.xml.namespace.QName;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SundayContentHandler
   implements JBossXBParser.ContentHandler
{
   private final static Logger log = Logger.getLogger(SundayContentHandler.class);

   private final DocumentHandler docHandler;

   private ElementStack elementStack = new ElementStack();
   private ObjectModelStack objectStack = new StackImpl();

   private StringBuffer textContent = new StringBuffer();

   private Object root;

   public SundayContentHandler(DocumentHandler docHandler)
   {
      this.docHandler = docHandler;
   }

   public void characters(char[] ch, int start, int length)
   {
      if(elementStack.peek() != ElementStack.NULL_ITEM)
      {
         textContent.append(ch, start, length);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      ElementStack.StackItem stackItem = elementStack.pop();
      if(stackItem != ElementStack.NULL_ITEM)
      {
         String dataContent = null;
         if(textContent.length() > 0)
         {
            dataContent = textContent.toString();
            textContent.delete(0, textContent.length());
         }

         Object child = stackItem.binding.end(stackItem.parent,
            stackItem.name,
            objectStack,
            stackItem.startIndex,
            stackItem.endIndex,
            dataContent
         );

         if(elementStack.isEmpty())
         {
            root = child;
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

      ElementStack.StackItem stackItem = ElementStack.NULL_ITEM;
      if(elementStack.isEmpty())
      {
         ElementBinding element = docHandler.getElement(startName);
         if(element != null)
         {
            stackItem = new ElementStack.StackItem(startName, element, null, objectStack.size());
         }
      }
      else
      {
         ElementStack.StackItem parentItem = elementStack.peek();
         if(parentItem != ElementStack.NULL_ITEM)
         {
            ElementBinding element = null;
            List handlers = parentItem.binding.getElementHandlers();
            for(int i = 0; i < handlers.size(); ++i)
            {
               ElementHandler handler = (ElementHandler)handlers.get(i);
               element = handler.getElement(startName);
               if(element != null)
               {
                  stackItem =
                     new ElementStack.StackItem(startName, element,
                        objectStack.peek(parentItem.startIndex + i),
                        objectStack.size()
                     );
                  break;
               }
            }

            if(log.isTraceEnabled() && element == null)
            {
               log.warn("element not bound: " + startName);
            }
         }
      }

      elementStack.push(stackItem);
      if(stackItem != ElementStack.NULL_ITEM)
      {
         stackItem.endIndex = stackItem.binding.start(stackItem.parent,
            stackItem.name,
            atts,
            objectStack,
            stackItem.startIndex
         );
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
      implements ObjectModelStack
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
         return list.get(i);
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
