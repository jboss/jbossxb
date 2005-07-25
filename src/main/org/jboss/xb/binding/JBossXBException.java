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
public class JBossXBException
   extends Exception
{  
   static final long serialVersionUID = 8229078720076583113L;

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
