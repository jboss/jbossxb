/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementTypeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandlerCallback;
import org.jboss.xml.binding.sunday.unmarshalling.impl.ElementHandlerImpl;
import org.jboss.xml.binding.Immutable;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementHandler
   extends ElementHandlerImpl
{
   private static final Logger log = Logger.getLogger(RtElementHandler.class);

   private static final AttributeBinding ATTRIBUTE_BINDING = new RtAttributeBinding();
   private static final TextContentBinding TC_BINDING = new RtTextContentBinding();

   public RtElementHandler()
   {
      super();
   }

   public RtElementHandler(ElementTypeBinding typeBinding)
   {
      super(typeBinding);
   }

   public AttributeBinding addAttribute(QName name)
   {
      return super.addAttribute(name);
   }

   public void addAttribute(QName name, AttributeBinding binding)
   {
      super.addAttribute(name, binding);
   }

   public ElementHandler pushAttributeHandler(QName name, AttributeHandler handler)
   {
      return super.pushAttributeHandler(name, handler);
   }

   public void setTextContent(TextContentBinding binding)
   {
      super.setTextContent(binding);
   }

   public ElementHandler pushTextContentHandler(TextContentHandler handler)
   {
      return super.pushTextContentHandler(handler);
   }

   public ElementBinding addElement(QName name)
   {
      return super.addElement(name);
   }

   public void addElement(QName name, ElementBinding binding)
   {
      super.addElement(name, binding);
   }

   public ElementBinding getElement(QName name)
   {
      ElementBinding element = super.getElement(name);
      if(element == null)
      {
         element = new RtElementBinding();
         addElement(name, element);
      }
      return element;
   }

   public void start(Object parent, QName name, Attributes attrs, ElementHandlerCallback callback)
   {
      Object child = null;

      Class childCls = RtUtil.resolveClass(name, true);
      if(childCls != null)
      {
         try
         {
            Constructor ctor = childCls.getConstructor(null);
            try
            {
               child = ctor.newInstance(null);
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException(
                  "Failed to create an instance of " + childCls + " for element " + name + " as a child of " + parent
               );
            }
         }
         catch(NoSuchMethodException e)
         {
            child = new Immutable(childCls);
         }

         for(int i = 0; i < attrs.getLength(); ++i)
         {
            String localName = attrs.getLocalName(i);
            QName attrName = new QName(attrs.getURI(i), localName.length() == 0 ? attrs.getQName(i) : localName);

            AttributeBinding attr = getAttribute(attrName);
            if(attr == null)
            {
               attr = ATTRIBUTE_BINDING;
            }

            attr.set(child, attrs.getValue(i), attrName);
         }
      }

      callback.accept(child, true);
   }

   public void end(Object parent, Object child, QName name, String dataContent)
   {
      FieldSetter setter = RtUtil.resolveSetter(parent, name);

      if(dataContent != null && dataContent.trim().length() > 0)
      {
         TextContentBinding textBindingBinding = getTextContent();
         if(textBindingBinding == null)
         {
            textBindingBinding = TC_BINDING;
         }
         textBindingBinding.set(parent, dataContent, name);
      }
      else
      {
         if(child instanceof Immutable)
         {
            child = ((Immutable)child).newInstance();
         }
         Object value = RtUtil.cast(child, setter.getFieldType());
         setter.set(parent, value, name);
      }
   }
}
