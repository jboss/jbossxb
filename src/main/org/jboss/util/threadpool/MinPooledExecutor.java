package org.jboss.util.threadpool;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.Channel;

/** A pooled executor where the minimum pool size threads are kept alive. This
is needed in order for the waitWhenBlocked option to work because of a
race condition inside the Executor. The race condition goes something like:

RT - Requesting Thread wanting to use the pool
LT - Last Thread in the pool

RT: Check there are enough free threads to process,
   yes LT is there, so no need to create a new thread.
LT: Times out on the keep alive, LT is destroyed.
RT: Try to execute, blocks because there are no available threads.
   In fact, the pool is now empty which the executor mistakenly
   inteprets as all of them being in use.

Doug Lea says he isn't going to fix. In fact, the version in j2se 
1.5 doesn't have this option. In order for this to work, the min pool
size must be > 0.

@author Scott.Stark@jboss.org
@author adrian@jboss.org
@version $Revision$
 */
public class MinPooledExecutor extends PooledExecutor
{
   // Constants -----------------------------------------------------


   // Attributes ----------------------------------------------------

   /** The number of threads to keep alive threads */
   protected int keepAliveSize;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Construct a new executor
    * 
    * @param poolSize the minimum pool size
    */
   public MinPooledExecutor(int poolSize)
   {
      super(poolSize);
   }
   
   /**
    * Construct a new executor
    * 
    * @param channel the queue for any requests
    * @param poolSize the minimum pool size
    */
   public MinPooledExecutor(Channel channel, int poolSize)
   {
      super(channel, poolSize);
   }

   // Public --------------------------------------------------------

   /**
    * @return the number of threads to keep alive
    */
   public int getKeepAliveSize()
   {
      return keepAliveSize;
   }

   /**
    * @param keepAliveSize the number of threads to keep alive
    */
   public void setKeepAliveSize(int keepAliveSize)
   {
      this.keepAliveSize = keepAliveSize;
   }

   // PooledExecutor overrides --------------------------------------
   
   protected Runnable getTask() throws InterruptedException
   {
      Runnable task = super.getTask();
      while (task == null && keepAlive())
      {
         task = super.getTask();
      }
      return task;
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   /**
    * We keep alive unless we are told to shutdown
    * or there are more than keepAliveSize threads in the pool
    *
    * @return whether to keep alive
    */
   protected synchronized boolean keepAlive()
   {
      if (shutdown_)
         return false;

      return poolSize_ <= keepAliveSize;
   }

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}