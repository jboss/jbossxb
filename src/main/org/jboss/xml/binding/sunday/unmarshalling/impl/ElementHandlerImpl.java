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
import org.jboss.xml.binding.sunday.unmarshalling.TextContentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementTypeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandlerCallback;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementHandlerImpl
   implements ElementHandler
{
   private static final Logger log = Logger.getLogger(ElementHandlerImpl.class);

   private ElementTypeBinding typeBinding;
   private boolean invokeNext = true;

   public ElementHandlerImpl()
   {
      this(new ElementTypeBindingImpl());
   }

   public ElementHandlerImpl(boolean invokeNext)
   {
      this(new ElementTypeBindingImpl(), invokeNext);
   }

   public ElementHandlerImpl(ElementTypeBinding typeBinding)
   {
      this.typeBinding = typeBinding;
   }

   public ElementHandlerImpl(ElementTypeBinding typeBinding, boolean invokeNext)
   {
      this.typeBinding = typeBinding;
      this.invokeNext = invokeNext;
   }

   public AttributeBinding addAttribute(QName name)
   {
      return typeBinding.addAttribute(name);
   }

   public void addAttribute(QName name, AttributeBinding binding)
   {
      typeBinding.addAttribute(name, binding);
   }

   public AttributeBinding getAttribute(QName name)
   {
      return typeBinding.getAttribute(name);
   }

   public ElementHandler pushAttributeHandler(QName name, AttributeHandler handler)
   {
      typeBinding.pushAttributeHandler(name, handler);
      return this;
   }

   public void addElement(QName name, ElementBinding binding)
   {
      typeBinding.addElement(name, binding);
   }

   public void setTextContent(TextContentBinding contentBinding)
   {
      typeBinding.setTextContent(contentBinding);
   }

   public TextContentBinding getTextContent()
   {
      return typeBinding.getTextContent();
   }

   public ElementHandler pushTextContentHandler(TextContentHandler handler)
   {
      typeBinding.pushTextContentHandler(handler);
      return this;
   }

   public ElementBinding addElement(QName name)
   {
      return typeBinding.addElement(name);
   }

   public ElementBinding getElement(QName name)
   {
      return typeBinding.getElement(name);
   }

   public void start(Object parent, QName name, Attributes attrs, ElementHandlerCallback callback)
   {
      Object child = newChild(parent, name);
      if(child != null && typeBinding.hasAttributes())
      {
         setAttributes(child, attrs);
      }

      callback.accept(child, invokeNext);
   }

   public void end(Object parent, Object child, QName name, String dataContent)
   {
      if(dataContent != null && typeBinding.getTextContent() != null)
      {
         typeBinding.getTextContent().set(child, dataContent, name);
      }
      addChild(parent, child, name);
   }

   // Protected

   protected Object newChild(Object parent, QName name)
   {
      return parent;
   }

   protected void setAttributes(Object child, Attributes attrs)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         String localName = attrs.getLocalName(i);
         QName attrName = new QName(attrs.getURI(i), localName.length() == 0 ? attrs.getQName(i) : localName);
         AttributeBinding attr = (AttributeBinding)typeBinding.getAttribute(attrName);
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

   protected void addChild(Object parent, Object child, QName name)
   {
   }
}
