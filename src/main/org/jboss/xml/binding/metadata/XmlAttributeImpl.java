/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlAttribute;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlSimpleType;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlSimpleType;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlAttributeImpl
   implements XmlAttribute
{
   private final XmlNamespace ns;
   private final String name;
   private final XmlSimpleType type;

   public XmlAttributeImpl(XmlNamespace ns, String name, XmlSimpleType type)
   {
      this.ns = ns;
      this.name = name;
      this.type = type;
   }

   public XmlNamespace getNamespace()
   {
      return ns;
   }

   public String getName()
   {
      return name;
   }

   public XmlSimpleType getType()
   {
      return type;
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
