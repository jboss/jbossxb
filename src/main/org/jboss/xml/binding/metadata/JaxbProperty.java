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
public class JaxbProperty
   extends XsdElement
{
   static final QName QNAME = new QName(Constants.NS_JAXB, "property");
   static final QName QNAME_COLLECTION_TYPE = new QName("collectionType");

   public JaxbProperty()
   {
      super(QNAME);
   }

   public String getName()
   {
      return getNameAttribute();
   }

   public String getCollectionType()
   {
      return getAttribute(QNAME_COLLECTION_TYPE);
   }

   public JaxbBaseType getBaseType()
   {
      return (JaxbBaseType)getChild(JaxbBaseType.QNAME);
   }
}
