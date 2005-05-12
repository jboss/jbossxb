/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import javax.xml.namespace.QName;
import org.jboss.xml.binding.Constants;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class JaxbClass
   extends BindingElement
{
   static final QName QNAME = new QName(Constants.NS_JAXB, "class");
   private static final QName QNAME_IMPL_CLASS = new QName("implClass");

   public JaxbClass()
   {
      super(QNAME);
   }

   public String getName()
   {
      return getNameAttribute();
   }

   public String getImplClass()
   {
      return getAttribute(QNAME_IMPL_CLASS);
   }
}
