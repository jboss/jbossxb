/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.AttributeBinding;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class BasicElementBindingImpl
   implements BasicElementBinding
{
   private final QName elementName;
   private final Map children = new HashMap();
   private final Map attributes = new HashMap();

   public BasicElementBindingImpl(QName elementName)
   {
      this.elementName = elementName;
   }

   void addElement(ElementBinding child)
   {
      children.put(child.getElementName(), child);
   }

   void addAttribute(AttributeBinding attr)
   {
      attributes.put(attr.getAttributeName(), attr);
   }

   public QName getElementQName()
   {
      return elementName;
   }

   public QName getElementName()
   {
      return elementName;
   }

   public ElementBinding getElement(QName elementName)
   {
      return (ElementBinding)children.get(elementName);
   }

   public AttributeBinding getAttribute(QName attributeName)
   {
      return (AttributeBinding)attributes.get(attributeName);
   }
}
