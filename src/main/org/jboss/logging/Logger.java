/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.logging;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/** 
 * A custom log4j Category wrapper that adds a trace level priority, 
 * is serializable and only exposes the relevent factory and logging 
 * methods.
 *
 * @see #isTraceEnabled
 * @see #trace(Object)
 * @see #trace(Object,Throwable)
 *
 * @version <tt>$Revision$</tt>
 * @author  Scott.Stark@jboss.org
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Logger
   implements java.io.Serializable
{
   /** The category name. */
   private final String name;

   /** The Log4j delegate logger. */
   private transient Category log;

   /** 
    * Creates new JBossCategory with the given category name.
    *
    * @param name    the category name.
    */
   protected Logger(final String name)
   {
      this.name = name;
      log = Category.getInstance(name);
   }

   /**
    * Expose the raw category for this logger.
    */
   public Category getCategory()
   {
      return log;
   }

   /**
    * Return the category name of this logger.
    *
    * @return The category name of this logger.
    */
   public String getName()
   {
      return name;
   }
   
   /** 
    * Check to see if the TRACE priority is enabled for this category.
    *
    * @return true if a {@link #trace(Object)} method invocation would pass
    * the msg to the configured appenders, false otherwise.
    */
   public boolean isTraceEnabled()
   {
      if (log.isEnabledFor(XPriority.TRACE) == false)
         return false;
      return XPriority.TRACE.isGreaterOrEqual(log.getChainedPriority());
   }

   /** 
    * Issue a log msg with a priority of TRACE.
    * Invokes log.log(XPriority.TRACE, message);
    */
   public void trace(Object message)
   {
      log.log(XPriority.TRACE, message);
   }

   /** 
    * Issue a log msg and throwable with a priority of TRACE.
    * Invokes log.log(XPriority.TRACE, message, t);
    */
   public void trace(Object message, Throwable t)
   {
      log.log(XPriority.TRACE, message, t);
   }

   /**
    * Check to see if the TRACE priority is enabled for this category.
    *
    * @return true if a {@link #trace(Object)} method invocation would pass
    * the msg to the configured appenders, false otherwise.
    */
   public boolean isDebugEnabled()
   {
      Priority p = Priority.DEBUG;
      if (log.isEnabledFor(p) == false)
         return false;
      return p.isGreaterOrEqual(log.getChainedPriority());
   }

   /** 
    * Issue a log msg with a priority of DEBUG.
    * Invokes log.log(Priority.DEBUG, message);
    */
   public void debug(Object message)
   {
      log.log(Priority.DEBUG, message);
   }

   /** 
    * Issue a log msg and throwable with a priority of DEBUG.
    * Invokes log.log(Priority.DEBUG, message, t);
    */
   public void debug(Object message, Throwable t)
   {
      log.log(Priority.DEBUG, message, t);
   }

   /** 
    * Check to see if the INFO priority is enabled for this category.
    *
    * @return true if a {@link #info(Object)} method invocation would pass
    * the msg to the configured appenders, false otherwise.
    */
   public boolean isInfoEnabled()
   {
      Priority p = Priority.INFO;
      if (log.isEnabledFor(p) == false)
         return false;
      return p.isGreaterOrEqual(log.getChainedPriority());
   }

   /** 
    * Issue a log msg with a priority of INFO.
    * Invokes log.log(Priority.INFO, message);
    */
   public void info(Object message)
   {
      log.log(Priority.INFO, message);
   }

   /**
    * Issue a log msg and throwable with a priority of INFO.
    * Invokes log.log(Priority.INFO, message, t);
    */
   public void info(Object message, Throwable t)
   {
      log.log(Priority.INFO, message, t);
   }

   /** 
    * Issue a log msg with a priority of WARN.
    * Invokes log.log(Priority.WARN, message);
    */
   public void warn(Object message)
   {
      log.log(Priority.WARN, message);
   }

   /** 
    * Issue a log msg and throwable with a priority of WARN.
    * Invokes log.log(Priority.WARN, message, t);
    */
   public void warn(Object message, Throwable t)
   {
      log.log(Priority.WARN, message, t);
   }

   /** 
    * Issue a log msg with a priority of ERROR.
    * Invokes log.log(Priority.ERROR, message);
    */
   public void error(Object message)
   {
      log.log(Priority.ERROR, message);
   }

   /** 
    * Issue a log msg and throwable with a priority of ERROR.
    * Invokes log.log(Priority.ERROR, message, t);
    */
   public void error(Object message, Throwable t)
   {
      log.log(Priority.ERROR, message, t);
   }

   /** 
    * Issue a log msg with a priority of FATAL.
    * Invokes log.log(Priority.FATAL, message);
    */
   public void fatal(Object message)
   {
      log.log(Priority.FATAL, message);
   }

   /** 
    * Issue a log msg and throwable with a priority of FATAL.
    * Invokes log.log(Priority.FATAL, message, t);
    */
   public void fatal(Object message, Throwable t)
   {
      log.log(Priority.FATAL, message, t);
   }

   /** 
    * Issue a log msg with the given priority.
    * Invokes log.log(p, message);
    */
   public void log(Priority p, Object message)
   {
      log.log(p, message);
   }

   /** 
    * Issue a log msg with the given priority.
    * Invokes log.log(p, message, t);
    */
   public void log(Priority p, Object message, Throwable t)
   {
      log.log(p, message, t);
   }


   /////////////////////////////////////////////////////////////////////////
   //                         Custom Serialization                        //
   /////////////////////////////////////////////////////////////////////////

   private void writeObject(java.io.ObjectOutputStream stream)
      throws java.io.IOException
   {
      // nothing
   }

   private void readObject(java.io.ObjectInputStream stream)
      throws java.io.IOException, ClassNotFoundException
   {
      // Restore logging
      log = Category.getInstance(name);
   }


   /////////////////////////////////////////////////////////////////////////
   //                            Factory Methods                          //
   /////////////////////////////////////////////////////////////////////////

   /** 
    * Create a Logger instance given the category name.
    *
    * @param name    the category name
    */
   public static Logger getLogger(String name)
   {
      Logger logger = new Logger(name);
      return logger;
   }

   /** 
    * Create a Logger instance given the category name with the given suffix.
    *
    * <p>This will include a category seperator between classname and suffix
    *
    * @param name     The category name
    * @param suffix   A suffix to append to the classname.
    */
   public static Logger getLogger(String name, String suffix)
   {
      return new Logger(name + "." + suffix);
   }

   /** 
    * Create a Logger instance given the category class. This simply
    * calls create(clazz.getName()).
    *
    * @param clazz    the Class whose name will be used as the category name
    */
   public static Logger getLogger(Class clazz)
   {
      Logger logger = new Logger(clazz.getName());
      return logger;
   }

   /** 
    * Create a Logger instance given the category class with the given suffix.
    *
    * <p>This will include a category seperator between classname and suffix
    *
    * @param clazz    The Class whose name will be used as the category name.
    * @param suffix   A suffix to append to the classname.
    */
   public static Logger getLogger(Class clazz, String suffix)
   {
      return new Logger(clazz.getName() + "." + suffix);
   }
}
