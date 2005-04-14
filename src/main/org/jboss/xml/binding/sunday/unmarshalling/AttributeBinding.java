/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBinding
{
   private final SimpleTypeBinding type;
   private final AttributeHandler handler;

   public AttributeBinding(SimpleTypeBinding type, AttributeHandler handler)
   {
      this.type = type;
      this.handler = handler;
   }

   public SimpleTypeBinding getType()
   {
      return type;
   }

   public AttributeHandler getHandler()
   {
      return handler;
   }
}
