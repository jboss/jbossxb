/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlType;
import org.jboss.xml.binding.metadata.XmlNamespace;
import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.metadata.XmlNamespace;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class XmlTypeImpl
   implements XmlType
{
   private final String name;
   protected final XmlNamespace ns;

   public XmlTypeImpl(String name, XmlNamespace ns)
   {
      this.name = name;
      this.ns = ns;
   }

   public String getName()
   {
      return name;
   }

   public XmlNamespace getNs()
   {
      return ns;
   }

   public abstract int getCategory();

   public abstract JavaFieldValue getJavaValue();

   public abstract XmlDataContent getDataContent();
}
