/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBinding;

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
   protected final DelegatingDocumentBinding doc;
   protected final QName elementName;
   //private final Map children = new HashMap();
   private final Map attributes = new HashMap();

   public BasicElementBindingImpl(QName elementName, DelegatingDocumentBinding doc)
   {
      this.elementName = elementName;
      this.doc = doc;
   }

   /*
   void addElement(ElementBinding child)
   {
      children.put(child.getElementName(), child);
   }
   */

   void addAttribute(AttributeBinding attr)
   {
      attributes.put(attr.getAttributeName(), attr);
   }

   public DocumentBinding getDocument()
   {
      return doc;
   }

   public QName getElementName()
   {
      return elementName;
   }

   /*
   public ElementBinding getElement(QName elementName)
   {
      return (ElementBinding)children.get(elementName);
   }
   */

   public AttributeBinding getAttribute(QName attributeName)
   {
      return (AttributeBinding)attributes.get(attributeName);
   }
}
