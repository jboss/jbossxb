/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class JBossXBValueFormatException
   extends JBossXBRuntimeException
{
   static final long serialVersionUID = -3196504305414545949L;

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
