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
public interface XmlNamespace
{
   String getNamespaceUri();

   String getJavaPackage();

   void setJavaPackage(String javaPackage);

   XmlType getType(String name);

   XmlElement getElement(String name);

   XmlType addType(String name);

   XmlElement addElement(String name, XmlType xmlType);

   XmlElement addElement(String name);
}
