/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;


import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlNamespaceImpl
   implements XmlNamespace
{
   private final String namespaceUri;
   private String javaPackage;
   private final Map elements = new HashMap();
   private final Map types = new HashMap();

   public XmlNamespaceImpl(String namespaceUri, String javaPackage)
   {
      this.namespaceUri = namespaceUri;
      this.javaPackage = javaPackage;
   }

   public String getNamespaceUri()
   {
      return namespaceUri;
   }

   public String getJavaPackage()
   {
      return javaPackage;
   }

   public void setJavaPackage(String javaPackage)
   {
      this.javaPackage = javaPackage;
   }

   public XmlType getType(String name)
   {
      return (XmlType)types.get(name);
   }

   public XmlElement getElement(String name)
   {
      return (XmlElement)elements.get(name);
   }

   public XmlType addType(String name)
   {
      XmlType type = new XmlTypeImpl(name, this);
      types.put(name, type);
      return type;
   }

   public XmlElement addElement(String name, XmlType xmlType)
   {
      XmlElement element = new XmlElementImpl(this, name, xmlType);
      elements.put(name, element);
      return element;
   }

   public XmlElement addElement(String name)
   {
      XmlType type = new XmlTypeImpl(name + "Type", this);
      return addElement(name, type);
   }
}
