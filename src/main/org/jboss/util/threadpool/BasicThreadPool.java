/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */
package org.jboss.util.threadpool;

import java.util.Collections;
import java.util.Map;
import java.util.Comparator;

import org.jboss.util.collection.WeakValueHashMap;

import EDU.oswego.cs.dl.util.concurrent.BoundedLinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.SynchronizedBoolean;
import EDU.oswego.cs.dl.util.concurrent.SynchronizedInt;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import EDU.oswego.cs.dl.util.concurrent.Heap;

/**
 * A basic thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision$
 */
public class BasicThreadPool implements ThreadPool, BasicThreadPoolMBean
{
   // Constants -----------------------------------------------------

   /** The jboss thread group */
   private static final ThreadGroup JBOSS_THREAD_GROUP = new ThreadGroup("JBoss Pooled Threads");

   /** The thread groups */
   private static final Map threadGroups = Collections.synchronizedMap(new WeakValueHashMap());

   /** The internal pool number */
   private static final SynchronizedInt lastPoolNumber = new SynchronizedInt(0);

   // Attributes ----------------------------------------------------

   /** The thread pool name */
   private String name;

   /** The internal pool number */
   private int poolNumber;

   /** The blocking mode */
   private String blockingMode = "abort";
   
   /** The pooled executor */
   private MinPooledExecutor executor;

   /** The queue */
   private BoundedLinkedQueue queue;

   /** The thread group */
   private ThreadGroup threadGroup = JBOSS_THREAD_GROUP;

   /** The last thread number */
   private SynchronizedInt lastThreadNumber = new SynchronizedInt(0);

   /** Has the pool been stopped? */
   private SynchronizedBoolean stopped = new SynchronizedBoolean(false);

   private Heap tasksWithTimeouts = new Heap(13);
   TimeoutMonitor timeoutTask;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   /**
    * Create a new thread pool
    */
   public BasicThreadPool()
   {
      this("ThreadPool");
   }

   /**
    * Create a new thread pool with a default queue size of 1024, min/max pool
    * sizes of 100 and a keep alive of 60 seconds.
    *
    * @param name the pool name
    */
   public BasicThreadPool(String name)
   {
      ThreadFactory factory = new ThreadPoolThreadFactory();

      queue = new BoundedLinkedQueue(1024);

      executor = new MinPooledExecutor(queue, 100);
      executor.setMinimumPoolSize(100);
      executor.setKeepAliveTime(60 * 1000);
      executor.setThreadFactory(factory);
      executor.abortWhenBlocked();

      poolNumber = lastPoolNumber.increment();
      setName(name);
      timeoutTask = new TimeoutMonitor(name);
   }

   // Public --------------------------------------------------------

   // ThreadPool ----------------------------------------------------

   public void stop(boolean immediate)
   {
      stopped.set(true);
      if (immediate)
         executor.shutdownNow();
      else
         executor.shutdownAfterProcessingCurrentlyQueuedTasks();
   }

   public void waitForTasks() throws InterruptedException
   {
      executor.awaitTerminationAfterShutdown();
   }
   public void waitForTasks(long maxWaitTime) throws InterruptedException
   {
      executor.awaitTerminationAfterShutdown(maxWaitTime);
   }

   public void runTaskWrapper(TaskWrapper wrapper)
   {
      if (stopped.get())
      {
         wrapper.rejectTask(new ThreadPoolStoppedException("Thread pool has been stopped"));
         return;
      }

      wrapper.acceptTask();

      long completionTimeout = wrapper.getTaskCompletionTimeout();
      TimeoutInfo info = null;
      if( completionTimeout > 0 )
      {
         // Install the task in the
         info = new TimeoutInfo(wrapper, completionTimeout);
         tasksWithTimeouts.insert(info);
      }
      int waitType = wrapper.getTaskWaitType();
      switch (waitType)
      {
         case Task.WAIT_FOR_COMPLETE:
         {
            executeOnThread(wrapper);
            break;
         }
         default:
         {
            execute(wrapper);
         }
      }
      waitForTask(wrapper);
   }

   public void runTask(Task task)
   {
      BasicTaskWrapper wrapper = new BasicTaskWrapper(task);
      runTaskWrapper(wrapper);
   }

   public void run(Runnable runnable)
   {
      run(runnable, 0, 0);
   }
   public void run(Runnable runnable, long startTimeout, long completeTimeout)
   {
      RunnableTaskWrapper wrapper = new RunnableTaskWrapper(runnable, startTimeout, completeTimeout);
      runTaskWrapper(wrapper);      
   }

   // ThreadPoolMBean implementation --------------------------------

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public int getPoolNumber()
   {
      return poolNumber;
   }

   public String getThreadGroupName()
   {
      return threadGroup.getName();
   }

   public void setThreadGroupName(String threadGroupName)
   {
      ThreadGroup group;
      synchronized(threadGroups)
      {
         group = (ThreadGroup) threadGroups.get(threadGroupName);
         if (group == null)
         {
            group = new ThreadGroup(JBOSS_THREAD_GROUP, threadGroupName);
            threadGroups.put(threadGroupName, group);
         }
      }
      threadGroup = group;
   }

   public int getQueueSize()
   {
      return queue.size();
   }

   public int getMaximumQueueSize()
   {
      return queue.capacity();
   }

   public void setMaximumQueueSize(int size)
   {
      queue.setCapacity(size);
   }

   public int getPoolSize()
   {
      return executor.getPoolSize();
   }

   public int getMinimumPoolSize()
   {
      return executor.getMinimumPoolSize();
   }

   public void setMinimumPoolSize(int size)
   {
      synchronized (executor)
      {
         executor.setKeepAliveSize(size);
         // Don't let the min size > max size
         if (executor.getMaximumPoolSize() < size)
         {
            executor.setMinimumPoolSize(size);
            executor.setMaximumPoolSize(size);
         }
      }
   }

   public int getMaximumPoolSize()
   {
      return executor.getMaximumPoolSize();
   }
   
   public void setMaximumPoolSize(int size)
   {
      synchronized (executor)
      {
         executor.setMinimumPoolSize(size);
         executor.setMaximumPoolSize(size);
         // Don't let the min size > max size
         if (executor.getKeepAliveSize() > size)
            executor.setKeepAliveSize(size);
      }
   }

   public long getKeepAliveTime()
   {
      return executor.getKeepAliveTime();
   }

   public void setKeepAliveTime(long time)
   {
      executor.setKeepAliveTime(time);
   }

   public String getBlockingMode()
   {
      return blockingMode;
   }
   
   public void setBlockingMode(String mode)
   {
      blockingMode = mode;
      
      if( mode.equalsIgnoreCase("run") )
      {
         executor.runWhenBlocked();
      }
      else if( mode.equalsIgnoreCase("wait") )
      {
         executor.waitWhenBlocked();
      }
      else if( mode.equalsIgnoreCase("discard") )
      {
         executor.discardWhenBlocked();
      }
      else if( mode.equalsIgnoreCase("discardOldest") )
      {
         executor.discardOldestWhenBlocked();
      }
      else
      {
         blockingMode = "abort";
         executor.abortWhenBlocked();
      }
   }

   public ThreadPool getInstance()
   {
      return this;
   }

   public void stop()
   {
      stop(false);
   }

   // Object overrides ----------------------------------------------

   public String toString()
   {
      return name + '(' + poolNumber + ')';
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   /**
    * Execute a task on the same thread
    *
    * @param wrapper the task wrapper
    */
   protected void executeOnThread(TaskWrapper wrapper)
   {
      wrapper.run();
   }

   /**
    * Execute a task
    *
    * @param wrapper the task wrapper
    */
   protected void execute(TaskWrapper wrapper)
   {
      try
      {
         executor.execute(wrapper);
      }
      catch (Throwable t)
      {
         wrapper.rejectTask(new ThreadPoolFullException(t.toString()));
      }
   }

   /**
    * Wait for a task
    *
    * @param wrapper the task wrapper
    */
   protected void waitForTask(TaskWrapper wrapper)
   {
      wrapper.waitForTask();
   }

   protected TimeoutInfo getNextTimeout()
   {
      TimeoutInfo info = (TimeoutInfo) this.tasksWithTimeouts.extract();
      return info;
   }

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------

   /**
    * A factory for threads
    */
   private class ThreadPoolThreadFactory implements ThreadFactory
   {
      public Thread newThread(Runnable runnable)
      {
         String threadName = BasicThreadPool.this.toString() + "-" + lastThreadNumber.increment();
         Thread thread = new Thread(threadGroup, runnable, threadName);
         thread.setDaemon(true);
         return thread;
      }
   }

   private static class TimeoutInfo implements Comparable
   {
      long start;
      long timeoutMS;
      TaskWrapper wrapper;
      TimeoutInfo(TaskWrapper wrapper, long timeout)
      {
         this.start = System.currentTimeMillis();
         this.timeoutMS = start + timeout;
         this.wrapper = wrapper;
      }
      public int compareTo(Object o)
      {
         TimeoutInfo ti = (TimeoutInfo) o;
         long to0 = timeoutMS;
         long to1 = ti.timeoutMS;
         int diff = (int) (to0 - to1);
         return diff;
      }
      TaskWrapper getTaskWrapper()
      {
         return wrapper;
      }
      public long getTaskCompletionTimeout()
      {
         return wrapper.getTaskCompletionTimeout();
      }
      public long getTaskCompletionTimeout(long now)
      {
         return timeoutMS - now;
      }
   }
   private class TimeoutMonitor implements Runnable
   {
      TimeoutMonitor(String name)
      {
         Thread t = new Thread(this, name+" TimeoutMonitor");
         t.setDaemon(true);
         t.start();
      }
      public void run()
      {
         boolean isStopped = stopped.get();
         while( isStopped == false )
         {
            try
            {
               TimeoutInfo info = getNextTimeout();
               if( info != null )
               {
                  long now = System.currentTimeMillis();
                  long timeToTimeout = info.getTaskCompletionTimeout(now);
                  if( timeToTimeout < 0 )
                     timeToTimeout = 1;
                  Thread.sleep(timeToTimeout);
                  // Check the status of the task
                  TaskWrapper wrapper = info.getTaskWrapper();
                  if( wrapper.isComplete() == false )
                  {
                     wrapper.stopTask();
                     // Requeue the TimeoutInfo
                     TimeoutInfo recheck = new TimeoutInfo(wrapper, 1000);
                     tasksWithTimeouts.insert(recheck);
                  }
               }
               else
               {
                  Thread.sleep(1000);
               }
            }
            catch(InterruptedException e)
            {
            }
            catch(Throwable e)
            {
               
            }
            isStopped = stopped.get();            
         }
      }
   }
}
