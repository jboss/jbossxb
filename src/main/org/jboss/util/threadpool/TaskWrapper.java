/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

/**
 * A task wrapper for a thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public interface TaskWrapper extends Runnable
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the type of wait
    *
    * @return the wait type
    */
   int getTaskWaitType();

   /**
    * The priority of the task
    *
    * @return the task priority
    */
   int getTaskPriority();

   /**
    * The time before the task must be accepted
    *
    * @return the start timeout
    */
   long getTaskStartTimeout();

   /**
    * The time before the task must be completed
    *
    * @return the completion timeout
    */
   long getTaskCompletionTimeout();

   /**
    * Wait according the wait type
    */
   void waitForTask();

   /**
    * Invoked by the threadpool when it wants to stop the task
    */
   void stopTask();

   /**
    * The task has been accepted
    *
    * @param time the time taken to accept the task
    */
   void acceptTask();

   /**
    * The task has been rejected
    *
    * @param e any error associated with the rejection
    */
   void rejectTask(RuntimeException e);

   // Inner classes -------------------------------------------------
}
