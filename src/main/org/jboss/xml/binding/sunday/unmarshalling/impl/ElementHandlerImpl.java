/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TextContent;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentHandler;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementHandlerImpl
   implements ElementHandler
{
   private static final Logger log = Logger.getLogger(ElementHandlerImpl.class);

   private Map attrBindings = Collections.EMPTY_MAP;
   private Map childBindings = Collections.EMPTY_MAP;
   protected TextContent contentBinding;

   public AttributeBinding addAttribute(QName name)
   {
      AttributeBinding attr = new AttributeBindingImpl();
      addAttribute(name, attr);
      return attr;
   }

   public void addAttribute(QName name, AttributeBinding binding)
   {
      switch(attrBindings.size())
      {
         case 0:
            attrBindings = Collections.singletonMap(name, binding);
            break;
         case 1:
            attrBindings = new HashMap(attrBindings);
         default:
            attrBindings.put(name, binding);
      }
   }

   public ElementHandler pushAttributeHandler(QName name, AttributeHandler handler)
   {
      AttributeBinding binding = (AttributeBinding)attrBindings.get(name);
      if(binding == null)
      {
         binding = new AttributeBindingImpl();
         addAttribute(name, binding);
      }
      binding.pushHandler(handler);
      return this;
   }

   public void addElement(QName name, ElementBinding binding)
   {
      switch(childBindings.size())
      {
         case 0:
            childBindings = Collections.singletonMap(name, binding);
            break;
         case 1:
            childBindings = new HashMap(childBindings);
         default:
            childBindings.put(name, binding);
      }
   }

   public void setTextContentBinding(TextContent contentBinding)
   {
      this.contentBinding = contentBinding;
   }

   public ElementHandler pushTextContentHandler(TextContentHandler handler)
   {
      if(contentBinding == null)
      {
         contentBinding = new TextContentImpl();
      }
      contentBinding.pushHandler(handler);
      return this;
   }

   public ElementBinding addElement(QName name)
   {
      ElementBinding childBinding = new ElementBindingImpl();
      addElement(name, childBinding);
      return childBinding;
   }

   public Object start(Object parent, QName name, Attributes attrs)
   {
      Object child = newChild(parent, name);
      if(child != null && !attrBindings.isEmpty())
      {
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            String localName = attrs.getLocalName(i);
            QName attrName = new QName(attrs.getURI(i), localName.length() == 0 ? attrs.getQName(i) : localName);
            AttributeBinding attr = (AttributeBinding)attrBindings.get(attrName);
            if(attr != null)
            {
               attr.set(child, attrs.getValue(i), attrName);
            }
            else
            {
               log.warn("no binding for attribute " + attrName);
            }
         }
      }
      return child;
   }

   public void end(Object parent, Object child, QName name, String dataContent)
   {
      if(dataContent != null)
      {
         if(contentBinding != null)
         {
            contentBinding.set(child, dataContent, name);
         }
      }
      addChild(parent, child, name);
   }

   public ElementBinding getElement(QName name)
   {
      return (ElementBinding)childBindings.get(name);
   }

   // Protected

   protected Object newChild(Object parent, QName name)
   {
      return parent;
   }

   protected void addChild(Object parent, Object child, QName name)
   {
   }
}
