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

   /**
    * Retrieve the thread group name
    *
    * @return the thread group name
    */
   String getThreadGroupName();

   /**
    * Set the thread group name
    *
    * @param the thread group name
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
