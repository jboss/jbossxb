/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlType;
import org.jboss.xml.binding.metadata.XmlElement;
import org.jboss.xml.binding.metadata.XmlSimpleType;
import org.jboss.xml.binding.metadata.XmlComplexType;
import org.jboss.xml.binding.metadata.JavaValueFactory;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.metadata.JavaValueFactory;
import org.jboss.xml.binding.metadata.XmlElement;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlSimpleType;
import org.jboss.xml.binding.metadata.XmlType;
import org.jboss.xml.binding.metadata.XmlComplexTypeImpl;
import org.jboss.xml.binding.metadata.XmlElementImpl;

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

   public XmlSimpleType addSimpleType(String name, Class javaType)
   {
      XmlSimpleType type = new XmlSimpleTypeImpl(name, this, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      types.put(name, type);
      return type;
   }

   public XmlComplexType addComplexType(String name, Class javaType)
   {
      XmlComplexType type = new XmlComplexTypeImpl(name, this, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      types.put(name, type);
      return type;
   }

   public XmlElement addElement(String name, XmlType xmlType)
   {
      XmlElement element = new XmlElementImpl(this, name, xmlType);
      elements.put(name, element);
      return element;
   }

   public XmlElement addElement(String name, Class javaType)
   {
      XmlType type;
      if(Util.isAttributeType(javaType))
      {
         type = new XmlSimpleTypeImpl(name + "Type", this, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      }
      else
      {
         type = new XmlComplexTypeImpl(name + "Type", this, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      }
      return addElement(name, type);
   }
}
