/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

/**
 * The thread pool is full.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public class ThreadPoolFullException extends RuntimeException
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Create a new ThreadPoolFullException
    */
   public ThreadPoolFullException()
   {
      super();
   }

   /**
    * Create a new ThreadPoolFullException
    *
    * @param message the message
    */
   public ThreadPoolFullException(String message)
   {
      super(message);
   }

   // Public --------------------------------------------------------


   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
