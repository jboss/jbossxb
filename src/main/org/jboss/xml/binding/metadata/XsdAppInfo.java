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
public class XsdAppInfo
   extends BindingElement
{
   static final QName QNAME = new QName(Constants.NS_XML_SCHEMA, "appinfo");

   public XsdAppInfo()
   {
      super(QNAME);
   }

   public JaxbClass getJaxbClass()
   {
      return (JaxbClass)getChild(JaxbClass.QNAME);
   }

   public JaxbProperty getJaxbProperty()
   {
      return (JaxbProperty)getChild(JaxbProperty.QNAME);
   }

   public JaxbJavaType getJaxbJavaType()
   {
      return (JaxbJavaType)getChild(JaxbJavaType.QNAME);
   }
}
