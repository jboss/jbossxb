/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.XmlSimpleType;
import org.jboss.xml.binding.metadata.XmlSimpleType;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlDataContentImpl
   implements XmlDataContent
{
   private final XmlSimpleType type;

   public XmlDataContentImpl(XmlSimpleType type)
   {
      this.type = type;
   }

   public XmlSimpleType getType()
   {
      return type;
   }
}
