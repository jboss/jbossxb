/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlComplexType;
import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.XmlElement;
import org.jboss.xml.binding.metadata.XmlAttribute;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlType;
import org.jboss.xml.binding.metadata.XmlSimpleType;
import org.jboss.xml.binding.metadata.JavaValueFactory;
import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.metadata.JavaValueFactory;
import org.jboss.xml.binding.metadata.XmlAttribute;
import org.jboss.xml.binding.metadata.XmlComplexType;
import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlType;
import org.jboss.xml.binding.metadata.XmlAttributeImpl;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlComplexTypeImpl
   extends XmlTypeImpl
   implements XmlComplexType
{
   private final JavaFieldValue javaValue;
   private final Map elements = new HashMap();
   private final Map attributes = new HashMap();
   private XmlDataContent dataContent;

   public XmlComplexTypeImpl(String name, XmlNamespace ns, JavaFieldValue javaValue)
   {
      super(name, ns);
      this.javaValue = javaValue;
   }

   public int getCategory()
   {
      return XmlType.COMPLEX;
   }

   public JavaFieldValue getJavaValue()
   {
      return javaValue;
   }

   public XmlDataContent getDataContent()
   {
      return dataContent;
   }

   public XmlElement getElement(String namespaceUri, String name)
   {
      return (XmlElement)elements.get(new QName(namespaceUri, name));
   }

   public XmlAttribute getAttribute(String namespaceUri, String name)
   {
      return (XmlAttribute)attributes.get(new QName(namespaceUri, name));
   }

   public XmlElement addElement(XmlNamespace ns, String name, XmlType xmlType)
   {
      XmlElement element = new XmlElementImpl(ns, name, xmlType);
      elements.put(new QName(ns.getNamespaceUri(), name), element);
      return element;
   }

   public XmlElement addElement(XmlNamespace ns, String name, Class javaType)
   {
      XmlType type;
      if(Util.isAttributeType(javaType))
      {
         type = new XmlSimpleTypeImpl(name + "Type", ns, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      }
      else
      {
         type = new XmlComplexTypeImpl(name + "Type", ns, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      }
      return addElement(ns, name, type);
   }

   public XmlElement addElement(String name, XmlType xmlType)
   {
      return addElement(ns, name, xmlType);
   }

   public XmlElement addElement(String name, Class javaType)
   {
      return addElement(ns, name, javaType);
   }

   public XmlAttribute addAttribute(XmlNamespace ns, String name, XmlSimpleType xmlType)
   {
      XmlAttribute attr = new XmlAttributeImpl(ns, name, xmlType);
      attributes.put(new QName(ns.getNamespaceUri(), name), attr);
      return attr;
   }

   public XmlAttribute addAttribute(XmlNamespace ns, String name, Class javaType)
   {
      XmlSimpleType type;
      if(Util.isAttributeType(javaType))
      {
         type = new XmlSimpleTypeImpl(name + "Type", ns, JavaValueFactory.getInstance().newJavaFieldValue(javaType));
      }
      else
      {
         throw new JBossXBRuntimeException("Attributes must of simple types!");
      }
      return addAttribute(ns, name, type);
   }

   public XmlAttribute addAttribute(String name, XmlSimpleType xmlType)
   {
      return addAttribute(ns, name, xmlType);
   }

   public XmlAttribute addAttribute(String name, Class javaType)
   {
      return addAttribute(ns, name, javaType);
   }

   public XmlDataContent addDataContent(XmlSimpleType xmlType)
   {
      dataContent = new XmlDataContentImpl(xmlType);
      return dataContent;
   }

   public XmlDataContent addDataContent(Class javaType)
   {
      XmlSimpleType dataType = new XmlSimpleTypeImpl("somename",
         ns,
         JavaValueFactory.getInstance().newJavaFieldValue(javaType)
      );
      dataContent = new XmlDataContentImpl(dataType);
      return dataContent;
   }
}
