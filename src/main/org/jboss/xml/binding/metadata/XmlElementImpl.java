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
public class XmlElementImpl
   implements XmlElement
{
   private final XmlNamespace ns;
   private final String name;
   private final XmlType type;
   private final JavaValue javaValue;

   public XmlElementImpl(XmlNamespace ns, String name, XmlType type)
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
      if(!(o instanceof XmlElementImpl))
      {
         return false;
      }

      final XmlElementImpl xmlElement = (XmlElementImpl)o;

      if(!name.equals(xmlElement.name))
      {
         return false;
      }
      if(!ns.equals(xmlElement.ns))
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
      return "[element " + ns.getNamespaceUri() + ":" + name + "]";
   }
}
