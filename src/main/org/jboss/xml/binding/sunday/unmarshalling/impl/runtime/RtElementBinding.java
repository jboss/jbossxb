/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ObjectModelStack;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandlerCallback;
import org.jboss.xml.binding.sunday.unmarshalling.impl.ElementBindingImpl;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementBinding
   extends ElementBindingImpl
{
   public RtElementBinding()
   {
      pushElementHandler(new RtElementHandler());
   }

   public ElementBinding pushElementHandler(ElementHandler handler)
   {
      handler.getElementType().addParent(new RtElementTypeBinding());
      return super.pushElementHandler(handler);
   }

   public List getElementHandlers()
   {
      return super.getElementHandlers();
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
      ElementBinding binding = new RtElementBinding();
      super.addElement(name, binding);
      return binding;
   }

   public void addElement(QName name, ElementBinding binding)
   {
      super.addElement(name, binding);
   }

   public void start(Object parent,
                    QName name,
                    Attributes attrs,
                    ElementHandlerCallback callback,
                    int handlerIndex)
   {
      super.start(parent, name, attrs, callback, handlerIndex);
   }

   public Object end(Object parent,
                     QName name,
                     ObjectModelStack stack,
                     int stackStartIndex,
                     int stackEndIndex,
                     String dataContent)
   {
      return super.end(parent, name, stack, stackStartIndex, stackEndIndex, dataContent);
   }
}
