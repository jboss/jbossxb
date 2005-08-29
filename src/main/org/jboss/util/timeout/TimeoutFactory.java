/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.timeout;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.jboss.util.ThrowableHandler;
import org.jboss.util.threadpool.BasicThreadPool;
import org.jboss.util.threadpool.ThreadPool;

/**
 * The timeout factory.
 * Upon timeout, calls a thread pool.
 * Implemented as a wrapper around java.util.Timer.
 *
 * @version $Revision$
 */
public class TimeoutFactory
{

   private static ThreadPool DEFAULT_TP = new BasicThreadPool("Timeouts");
   private Timer timer;
   private ThreadPool threadPool;

   static TimeoutFactory singleton;
   static int count = 0;

   /** Construct a new factory with a specific thread pool. */
   public TimeoutFactory(ThreadPool threadPool)
   {
      this.threadPool = threadPool;
      this.timer = new Timer(true);
      timer.schedule(new TimerTask() {
         public void run() {
            Thread.currentThread().setName("TimeoutFactory-" + count++);
         }
      }, 0L);
   }

   /** Construct a new factory with a default thread pool. */
   public TimeoutFactory()
   {
      this(DEFAULT_TP);
   }

   /**
    * Schedules a new timeout.
    */
   public TimerTask schedule(long time, Runnable run)
   {
      TimerTask t = new PooledRunner(run);
      timer.schedule(t, new Date(time));
      return t;
   }

   /**
    * Schedule a new timeout.
    */
   public Timeout schedule(long time, TimeoutTarget target)
   {
      TimeoutImpl t = new TimeoutImpl(target);
      timer.schedule(t, new Date(time));
      return t;
   }

   private synchronized static TimeoutFactory getSingleton() {
      if (singleton != null)
         return singleton;
      singleton = new TimeoutFactory(DEFAULT_TP);
      return singleton;
   }

   /**
    * Schedule a new timeout.
    */
   public static Timeout createTimeout(long time, TimeoutTarget target)
   {
      return getSingleton().schedule(time, target);
   }

   public void cancel()
   {
      timer.cancel();
   }

   /**
    *  Our private Timeout implementation.
    */
   private class TimeoutImpl extends TimerTask implements Timeout
   {
      private TimeoutTarget target; // target to fire at

      public TimeoutImpl(TimeoutTarget target)
      {
         if (target == null)
            throw new IllegalArgumentException("Null target");
         this.target = target;
      }

      public void run()
      {
         Runnable r = new Runnable() {
            public void run()
            {
               target.timedOut(TimeoutImpl.this);
            }
         };
         threadPool.run(r);
      }

   }

   private class PooledRunner extends TimerTask
   {

      Runnable run;

      public PooledRunner(Runnable run)
      {
         this.run = run;
      }

      public void run()
      {
         threadPool.run(this.run);
      }

   }

}
