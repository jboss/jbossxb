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
public class JaxbBaseType
   extends BindingElement
{
   static final QName QNAME = new QName(Constants.NS_JAXB, "baseType");

   public JaxbBaseType()
   {
      super(QNAME);
   }

   public JaxbJavaType getJavaType()
   {
      return (JaxbJavaType)getChild(JaxbJavaType.QNAME);
   }
}
