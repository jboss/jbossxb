/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.TopElementBinding;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TopElementBindingImpl
   extends BasicElementBindingImpl
   implements TopElementBinding
{
   private final Class javaType;

   public TopElementBindingImpl(QName elementName, Class javaType)
   {
      super(elementName);
      this.javaType = javaType;
   }

   public TopElementBindingImpl(TopElementBinding top)
   {
      super(top.getElementName());
      this.javaType = top.getJavaType();
   }

   public Class getJavaType()
   {
      return javaType;
   }
}
