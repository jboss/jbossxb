/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface XmlType
{
   String getName();

   XmlNamespace getNamespace();

   JavaValue getJavaValue();

   XmlDataContent getDataContent();

   XmlElement getElement(String namespaceUri, String name);

   XmlAttribute getAttribute(String namespaceUri, String name);

   XmlElement addElement(XmlNamespace ns, String name, XmlType xmlType);

   XmlElement addElement(XmlNamespace ns, String name);

   XmlElement addElement(String name, XmlType xmlType);

   XmlElement addElement(String name);

   XmlAttribute addAttribute(XmlNamespace ns, String name, XmlType xmlType);

   XmlAttribute addAttribute(XmlNamespace ns, String name);

   XmlAttribute addAttribute(String name, XmlType xmlType);

   XmlAttribute addAttribute(String name);

   XmlDataContent addDataContent(XmlType xmlType);

   XmlDataContent addDataContent();
}
