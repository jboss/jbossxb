/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlComplexType;
import org.jboss.xml.binding.metadata.XmlElement;

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

   XmlSimpleType addSimpleType(String name, Class javaType);
   XmlComplexType addComplexType(String name, Class javaType);

   XmlElement addElement(String name, XmlType xmlType);
   XmlElement addElement(String name, Class javaType);
}
