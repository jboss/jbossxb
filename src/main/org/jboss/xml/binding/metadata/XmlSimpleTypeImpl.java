/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.XmlSimpleType;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlType;
import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.XmlDataContentImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlSimpleTypeImpl
   extends XmlTypeImpl
   implements XmlSimpleType
{
   private final JavaFieldValue javaValue;
   private final XmlDataContent dataContent;

   public XmlSimpleTypeImpl(String name, XmlNamespace ns, JavaFieldValue javaValue)
   {
      super(name, ns);
      this.javaValue = javaValue;
      dataContent = new XmlDataContentImpl(this);
   }

   public int getCategory()
   {
      return XmlType.SIMPLE;
   }

   public JavaFieldValue getJavaValue()
   {
      return javaValue;
   }

   public XmlDataContent getDataContent()
   {
      return dataContent;
   }
}
