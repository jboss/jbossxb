/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

/**
 * The task was stopped.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public class TaskStoppedException extends RuntimeException
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Create a new TaskStoppedException
    */
   public TaskStoppedException()
   {
      super();
   }

   /**
    * Create a new TaskStoppedException
    *
    * @param message the message
    */
   public TaskStoppedException(String message)
   {
      super(message);
   }

   // Public --------------------------------------------------------


   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
