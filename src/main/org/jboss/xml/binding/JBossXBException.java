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
public class JBossXBException
   extends Exception
{
   public JBossXBException()
   {
   }

   public JBossXBException(String message)
   {
      super(message);
   }

   public JBossXBException(Throwable cause)
   {
      super(cause);
   }

   public JBossXBException(String message, Throwable cause)
   {
      super(message, cause);
   }
}