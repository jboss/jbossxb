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
public class XmlDataContentImpl
   implements XmlDataContent
{
   private final XmlType type;
   private final JavaValue javaValue;

   public XmlDataContentImpl(XmlType type)
   {
      this.type = type;
      this.javaValue = (JavaValue)type.getJavaValue().clone();
   }

   public XmlType getType()
   {
      return type;
   }

   public JavaValue getJavaValue()
   {
      return javaValue;
   }
}
