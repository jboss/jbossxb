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
public interface XmlDocument
{
   XmlNamespace getNamespace(String uri);

   XmlElement getTopElement(String namespaceUri, String name);

   XmlNamespace addNamespace(String uri);

   XmlElement addTopElement(XmlNamespace ns, String name, XmlType xmlType);

   XmlElement addTopElement(XmlNamespace ns, String name);
}
