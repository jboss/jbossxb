/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

import org.jboss.logging.Logger;

/**
 * Makes a runnable a task.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public class RunnableTaskWrapper implements TaskWrapper
{
   // Constants -----------------------------------------------------

   /** The log */
   private static final Logger log = Logger.getLogger(RunnableTaskWrapper.class);

   // Attributes ----------------------------------------------------

   /** The runnable */
   private Runnable runnable;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Create a new RunnableTaskWrapper
    *
    * @param runnable the runnable
    * @throws IllegalArgumentExeption for a null runnable
    */
   public RunnableTaskWrapper(Runnable runnable)
   {
      if (runnable == null)
         throw new IllegalArgumentException("Null runnable");
      this.runnable = runnable;
   }

   // Public --------------------------------------------------------

   // TaskWrapper implementation ---------------------------------------

   public int getTaskWaitType()
   {
      return Task.WAIT_NONE;
   }

   public int getTaskPriority()
   {
      return Thread.NORM_PRIORITY;
   }

   public long getTaskStartTimeout()
   {
      return 0L;
   }

   public long getTaskCompletionTimeout()
   {
      return 0L;
   }

   public void acceptTask()
   {
      // Nothing to do
   }

   public void rejectTask(RuntimeException t)
   {
      throw t;
   }

   public void stopTask()
   {
      // Can't do anything?
   }

   public void waitForTask()
   {
      // Nothing to do
   }

   // Runnable implementation ---------------------------------------

   public void run()
   {
      try
      {
         runnable.run();
      }
      catch (Throwable t)
      {
         log.warn("Unhandled throwable for runnable: " + runnable, t);
      }
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
