/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlAttribute;
import org.jboss.xml.binding.metadata.XmlAttribute;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface XmlComplexType
   extends XmlType
{
   XmlElement getElement(String namespaceUri, String name);
   XmlAttribute getAttribute(String namespaceUri, String name);

   XmlElement addElement(XmlNamespace ns, String name, XmlType xmlType);
   XmlElement addElement(XmlNamespace ns, String name, Class javaType);
   XmlElement addElement(String name, XmlType xmlType);
   XmlElement addElement(String name, Class javaType);

   XmlAttribute addAttribute(XmlNamespace ns, String name, XmlSimpleType xmlType);
   XmlAttribute addAttribute(XmlNamespace ns, String name, Class javaType);
   XmlAttribute addAttribute(String name, XmlSimpleType xmlType);
   XmlAttribute addAttribute(String name, Class javaType);

   XmlDataContent addDataContent(XmlSimpleType xmlType);
   XmlDataContent addDataContent(Class javaType);
}
