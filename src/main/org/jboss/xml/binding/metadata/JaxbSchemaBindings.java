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
public class JaxbSchemaBindings
   extends XsdElement
{
   static final QName QNAME = new QName(Constants.NS_JAXB, "schemaBindings");

   public JaxbSchemaBindings()
   {
      super(QNAME);
   }

   public JaxbPackage getPackage()
   {
      return (JaxbPackage)getChild(JaxbPackage.QNAME);
   }
}
