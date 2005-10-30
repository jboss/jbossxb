/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.util.timeout;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.jboss.logging.Logger;
import org.jboss.util.threadpool.BasicThreadPool;
import org.jboss.util.threadpool.ThreadPool;
import org.jboss.util.threadpool.BlockingMode;

/**
 * The timeout factory.
 * Upon timeout, calls tasks that run on a specific thread pool.
 * Implemented as a wrapper around java.util.Timer.
 *
 * @version $Revision$
 */
public class TimeoutFactory
{

   private static Logger log = Logger.getLogger(TimeoutFactory.class);

   private static BasicThreadPool DEFAULT_TP = new BasicThreadPool("Timeouts");
   {
     DEFAULT_TP.setBlockingMode(BlockingMode.RUN);
   }
   private Timer timer;
   private ThreadPool threadPool;

   static TimeoutFactory singleton;
   static int count = 0;

   /** Constructs a new factory with a specific thread pool. */
   public TimeoutFactory(ThreadPool threadPool)
   {
      this.threadPool = threadPool;
      this.timer = new Timer(true);
      timer.schedule(new TimerTask()
         {
            public void run()
            {
               Thread.currentThread().setName("TimeoutFactory-" + count++);
            }
         }, 0L);
   }

   /** Constructs a new factory with a default thread pool. */
   public TimeoutFactory()
   {
      this(DEFAULT_TP);
   }

   /**
    * Schedules a new timeout.
    * @param time absolute time
    * @param run runnable to run
    */
   public Timeout schedule(long time, Runnable run)
   {
      PooledRunner pr = new PooledRunner(run);
      timer.schedule(pr, new Date(time));
      return pr;
   }

   /**
    * Schedule a new timeout.
    * @param time absolute time
    * @param target target to fire
    */
   public Timeout schedule(long time, final TimeoutTarget target)
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

   /**
    * Cancels all submitted timeout tasks.
    */
   public void cancel()
   {
      timer.cancel();
   }

   private class PooledRunner extends TimerTask implements Timeout
   {

      Runnable run;

      PooledRunner()
      {
      }

      public PooledRunner(Runnable run)
      {
         this.run = run;
      }

      public void run()
      {
         try
         {
            threadPool.run(this.run);
         }
         catch (Throwable t)
         {
            log.warn("Unable to pool timeout: " + run, t);
            try
            {
               this.run.run();
            }
            catch (Throwable t2)
            {
               log.error("Timeout failed to run unpooled: " + run, t2);
            }
         }
      }

   }

   /**
    *  Our private Timeout implementation.
    */
   private class TimeoutImpl extends PooledRunner
   {

      public TimeoutImpl(final TimeoutTarget target) {
         super();
         this.run = new Runnable()
            {
               public void run()
               {
                  target.timedOut(TimeoutImpl.this);
               }
            };
      }

   }

}
