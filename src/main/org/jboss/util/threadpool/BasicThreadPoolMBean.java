/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

/**
 * Management interface for the thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public interface BasicThreadPoolMBean extends ThreadPoolMBean
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the current queue size
   *
    * @return the queue size
    */
   public int getQueueSize();

   /**
    * Get the maximum queue size
    *
    * @return the maximum queue size
    */
   public int getMaximumQueueSize();

   /**
    * Set the maximum queue size
    *
    * @param size the new maximum queue size
    */
   public void setMaximumQueueSize(int size);

   /** Set the behavior of the pool when a task is added and the queue is full.
    * The mode string indicates one of the following modes:
    * abort - a RuntimeException is thrown
    * run - the calling thread executes the task
    * wait - the calling thread blocks until the queue has room
    * discard - the task is silently discarded without being run
    * discardOldest - check to see if a task is about to complete and enque
    *    the new task if possible, else run the task in the calling thread
    * 
    * @param mode one of run, wait, discard, discardOldest or abort without
    *    regard to case.
    */ 
   public void setBlockingMode(String mode);

   /**
    * Retrieve the thread group name
    *
    * @return the thread group name
    */
   String getThreadGroupName();

   /**
    * Set the thread group name
    *
    * @param threadGroupName - the thread group name
    */
   void setThreadGroupName(String threadGroupName);

   /**
    * Get the keep alive time
    *
    * @return the keep alive time
    */
   long getKeepAliveTime();

   /**
    * Set the keep alive time
    *
    * @param time the keep alive time
    */
   void setKeepAliveTime(long time);

   // Inner classes -------------------------------------------------
}
