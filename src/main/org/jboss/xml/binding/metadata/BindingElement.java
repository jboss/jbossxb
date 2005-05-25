/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class BindingElement
   extends XsdElement
{
   static final QName QNAME_NAME = new QName("name");

   public BindingElement(QName qName)
   {
      super(qName);
   }

   public JaxbSchemaBindings getJaxbSchemaBindings()
   {
      return (JaxbSchemaBindings)getChild(JaxbSchemaBindings.QNAME);
   }

   public String getNameAttribute()
   {
      return getAttribute(QNAME_NAME);
   }

   public void setNameAttribute(String name)
   {
      addAttribute(QNAME_NAME, name);
   }
}
