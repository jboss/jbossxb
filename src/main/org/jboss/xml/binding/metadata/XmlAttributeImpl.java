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
public class XmlAttributeImpl
   implements XmlAttribute
{
   private final XmlNamespace ns;
   private final String name;
   private final XmlType type;
   private final JavaValue javaValue;

   public XmlAttributeImpl(XmlNamespace ns, String name, XmlType type)
   {
      this.ns = ns;
      this.name = name;
      this.type = type;
      this.javaValue = (JavaValue)type.getJavaValue().clone();
   }

   public XmlNamespace getNamespace()
   {
      return ns;
   }

   public String getName()
   {
      return name;
   }

   public XmlType getType()
   {
      return type;
   }

   public JavaValue getJavaValue()
   {
      return javaValue;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof XmlAttributeImpl))
      {
         return false;
      }

      final XmlAttributeImpl xmlAttribute = (XmlAttributeImpl)o;

      if(!name.equals(xmlAttribute.name))
      {
         return false;
      }
      if(!ns.equals(xmlAttribute.ns))
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = ns.hashCode();
      result = 29 * result + name.hashCode();
      return result;
   }

   public String toString()
   {
      return "[attribute " + ns.getNamespaceUri() + ":" + name + "]";
   }
}
