/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ObjectModelStack;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentHandler;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBindingImpl
   implements ElementBinding
{
   private List handlers = Collections.EMPTY_LIST;

   public ElementBinding pushElementHandler(ElementHandler handler)
   {
      switch(handlers.size())
      {
         case 0:
            handlers = Collections.singletonList(handler);
            break;
         case 1:
            handlers = new ArrayList(handlers);
         default:
            handlers.add(0, handler);
      }
      return this;
   }

   public List getElementHandlers()
   {
      return handlers;
   }

   public AttributeBinding addAttribute(QName name)
   {
      return getLastHandler().addAttribute(name);
   }

   public void addAttribute(QName name, AttributeBinding binding)
   {
      getLastHandler().addAttribute(name, binding);
   }

   public ElementHandler pushAttributeHandler(QName name, AttributeHandler handler)
   {
      return getLastHandler().pushAttributeHandler(name, handler);
   }

   public ElementBinding addElement(QName name)
   {
      return getLastHandler().addElement(name);
   }

   public void addElement(QName name, ElementBinding binding)
   {
      getLastHandler().addElement(name, binding);
   }

   public void setTextContent(TextContentBinding binding)
   {
      getLastHandler().setTextContent(binding);
   }

   public ElementHandler pushTextContentHandler(TextContentHandler handler)
   {
      return getLastHandler().pushTextContentHandler(handler);
   }

   public int start(Object parent, QName name, Attributes attrs, ObjectModelStack stack, int startIndex)
   {
      if(handlers.isEmpty())
      {
         throw new IllegalStateException("Element binding has no handlers: " + name);
      }

      ElementHandler handler = (ElementHandler)handlers.get(0);
      Object child = handler.start(parent, name, attrs);
      int i = 0;
      if(child != null)
      {
         stack.push(child);
         while(++i < handlers.size())
         {
            handler = (ElementHandler)handlers.get(i);
            child = handler.start(child, name, attrs);
            if(child == null)
            {
               break;
            }
            else
            {
               stack.push(child);
            }
         }
      }
      return startIndex + i;
   }

   public Object end(Object parent,
                     QName name,
                     ObjectModelStack stack,
                     int startIndex,
                     int endIndex,
                     String dataContent)
   {
      Object child = null;
      if(endIndex - startIndex > 0)
      {
         child = stack.pop();
         ElementHandler handler;
         for(int i = endIndex - startIndex - 1; i > 0; --i)
         {
            Object localParent = stack.pop();
            handler = (ElementHandler)handlers.get(i);
            handler.end(localParent, child, name, dataContent);
            child = localParent;
         }

         handler = (ElementHandler)handlers.get(0);
         handler.end(parent, child, name, dataContent);
      }
      return child;
   }

   // Private

   private ElementHandler getLastHandler()
   {
      /*
      if(handlers.isEmpty())
      {
         throw new IllegalStateException("Can't add binding because the element binding has no handlers!");
      }
      */
      if(handlers.isEmpty())
      {
         pushElementHandler(new ElementHandlerImpl());
      }
      return (ElementHandler)handlers.get(0);
   }
}
