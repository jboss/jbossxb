/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlTypeImpl
   implements XmlType
{
   protected final String name;
   protected final XmlNamespace ns;
   private final JavaValue javaValue;
   private final Map elements = new HashMap();
   private final Map attributes = new HashMap();
   private XmlDataContent dataContent;

   public XmlTypeImpl(String name, XmlNamespace ns)
   {
      this.name = name;
      this.ns = ns;
      this.javaValue = new JavaValue();
   }

   public String getName()
   {
      return name;
   }

   public XmlNamespace getNamespace()
   {
      return ns;
   }


   public JavaValue getJavaValue()
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

   public XmlElement addElement(XmlNamespace ns, String name)
   {
      XmlType type = new XmlTypeImpl(name + "Type", ns);
      return addElement(ns, name, type);
   }

   public XmlElement addElement(String name, XmlType xmlType)
   {
      return addElement(ns, name, xmlType);
   }

   public XmlElement addElement(String name)
   {
      return addElement(ns, name);
   }

   public XmlAttribute addAttribute(XmlNamespace ns, String name, XmlType xmlType)
   {
      XmlAttribute attr = new XmlAttributeImpl(ns, name, xmlType);
      attributes.put(new QName(ns.getNamespaceUri(), name), attr);
      return attr;
   }

   public XmlAttribute addAttribute(XmlNamespace ns, String name)
   {
      XmlType type = new XmlTypeImpl(name + "Type", ns);
      return addAttribute(ns, name, type);
   }

   public XmlAttribute addAttribute(String name, XmlType xmlType)
   {
      return addAttribute(ns, name, xmlType);
   }

   public XmlAttribute addAttribute(String name)
   {
      return addAttribute(ns, name);
   }

   public XmlDataContent addDataContent(XmlType xmlType)
   {
      dataContent = new XmlDataContentImpl(xmlType);
      return dataContent;
   }

   public XmlDataContent addDataContent()
   {
      XmlType dataType = new XmlTypeImpl(name + "Data", ns);
      dataContent = new XmlDataContentImpl(dataType);
      return dataContent;
   }
}
