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
public interface ThreadPoolMBean
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the thread pool name
    *
    * @return the thread pool name
    */
   String getName();

   /**
    * Set the thread pool name
    *
    * @param name the name
    */
   void setName(String name);

   /**
    * Get the internal pool number
    *
    * @return the internal pool number
    */
   int getPoolNumber();

   /**
    * Get the maximum pool size
    *
    * @return the maximum pool size
    */
   int getMaximumPoolSize();

   /**
    * Set the maximum pool size
    *
    * @param size the maximum pool size
    */
   void setMaximumPoolSize(int size);

   /**
    * Get the instance
    */
   ThreadPool getInstance();

   /**
    * Stop the thread pool
    */
   void stop();

   // Inner classes -------------------------------------------------
}
