/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.threadpool;

/**
 * A thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public interface ThreadPool
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Stop the pool
    *
    * @param immediate whether to shutdown immediately
    */
   public void stop(boolean immediate);

   /**
    * Run a task wrapper
    *
    * @param wrapper the task wrapper
    */
   public void runTaskWrapper(TaskWrapper wrapper);

   /**
    * Run a task
    *
    * @param task the task
    * @throws IllegalArgumentException for a null task
    */
   public void runTask(Task task);

   /**
    * Run a runnable
    *
    * @param runnable the runnable
    * @throws IllegalArgumentException for a null runnable
    */
   public void run(Runnable runnable);
}
