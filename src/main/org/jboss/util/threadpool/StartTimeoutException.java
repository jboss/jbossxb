/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

/**
 * The start timeout was exceeded.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public class StartTimeoutException extends RuntimeException
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Create a new StartTimeoutException
    */
   public StartTimeoutException()
   {
      super();
   }

   /**
    * Create a new StartTimeoutException
    *
    * @param message the message
    */
   public StartTimeoutException(String message)
   {
      super(message);
   }

   // Public --------------------------------------------------------


   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
