/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.jboss.logging.Logger;
import org.jboss.xml.binding.sunday.unmarshalling.DocumentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ObjectModelStack;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DocumentHandlerImpl
   implements ContentHandler, DocumentHandler
{
   private static final Logger log = Logger.getLogger(DocumentHandlerImpl.class);

   private Map topElements = new HashMap();
   private ElementStack elementStack = new ElementStack();
   private ObjectModelStack objectStack = new StackImpl();

   private StringBuffer textContent = new StringBuffer();

   public Object root;

   public ElementBinding addElement(QName name)
   {
      ElementBinding binding = new ElementBindingImpl();
      addElement(name, binding);
      return binding;
   }

   public void addElement(QName name, ElementBinding binding)
   {
      topElements.put(name, binding);
   }

   public void endDocument() throws SAXException
   {
   }

   public void startDocument() throws SAXException
   {
   }

   public void characters(char ch[], int start, int length) throws SAXException
   {
      if(elementStack.peek() != ElementStack.NULL_ITEM)
      {
         textContent.append(ch, start, length);
      }
   }

   public void ignorableWhitespace(char ch[], int start, int length) throws SAXException
   {
   }

   public void endPrefixMapping(String prefix) throws SAXException
   {
   }

   public void skippedEntity(String name) throws SAXException
   {
   }

   public void setDocumentLocator(Locator locator)
   {
   }

   public void processingInstruction(String target, String data) throws SAXException
   {
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException
   {
   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException
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

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
   {
      QName startName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);

      ElementStack.StackItem stackItem = ElementStack.NULL_ITEM;
      if(elementStack.isEmpty())
      {
         ElementBinding element = (ElementBinding)topElements.get(startName);
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
