/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.impl.ElementTypeBindingImpl;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementTypeBinding
   extends ElementTypeBindingImpl
{
   public AttributeBinding getAttribute(QName name)
   {
      AttributeBinding attribute = super.getAttribute(name);
      if(attribute == null)
      {
         attribute = new RtAttributeBinding();
      }
      return attribute;
   }

   public boolean hasAttributes()
   {
      return true;
   }

   public TextContentBinding getTextContent()
   {
      TextContentBinding textContent = super.getTextContent();
      if(textContent == null)
      {
         textContent = new RtTextContentBinding();
      }
      return textContent;
   }

   public ElementBinding getElement(QName name)
   {
      ElementBinding element = super.getElement(name);
      if(element == null)
      {
         element = new RtElementBinding();
      }
      return element;
   }
}
