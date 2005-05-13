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
public class JaxbJavaType
   extends BindingElement
{
   public static final QName QNAME = new QName(Constants.NS_JAXB, "javaType");
   private static final QName QNAME_XML_TYPE = new QName("xmlType");
   private static final QName QNAME_PARSE_METHOD = new QName("parseMethod");
   private static final QName QNAME_PRINT_METHOD = new QName("printMethod");

   public JaxbJavaType()
   {
      super(QNAME);
   }

   public String getName()
   {
      return getNameAttribute();
   }

   public String getXmlType()
   {
      return getAttribute(QNAME_XML_TYPE);
   }

   public String getParseMethod()
   {
      return getAttribute(QNAME_PARSE_METHOD);
   }

   public String getPrintMethod()
   {
      return getAttribute(QNAME_PRINT_METHOD);
   }
}
