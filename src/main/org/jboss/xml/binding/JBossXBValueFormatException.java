/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class JBossXBValueFormatException
   extends JBossXBRuntimeException
{
   public JBossXBValueFormatException()
   {
   }

   public JBossXBValueFormatException(String message)
   {
      super(message);
   }

   public JBossXBValueFormatException(Throwable cause)
   {
      super(cause);
   }

   public JBossXBValueFormatException(String message, Throwable cause)
   {
      super(message, cause);
   }
}