/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

/**
 * The thread pool was stopped.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public class ThreadPoolStoppedException extends RuntimeException
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Create a new ThreadPoolStoppedException
    */
   public ThreadPoolStoppedException()
   {
      super();
   }

   /**
    * Create a new ThreadPoolStoppedException
    *
    * @param message the message
    */
   public ThreadPoolStoppedException(String message)
   {
      super(message);
   }

   // Public --------------------------------------------------------


   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
