/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/** 
 * A custom Log4j Logger wrapper that adds a trace level and
 * is serializable.
 *
 * <p>Only exposes the relevent factory and logging methods.
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
   /** The logger name. */
   private final String name;

   /** The Log4j delegate logger. */
   private transient org.apache.log4j.Logger log;

   /** 
    * Creates new Logger the given logger name.
    *
    * @param name    the logger name.
    */
   protected Logger(final String name)
   {
      this.name = name;

      log = LogManager.getLogger(name);
   }

   /**
    * Expose the raw logger for this logger.
    *
    * @deprecated Use {@link #getLogger} instead.
    */
   public Category getCategory()
   {
      return log;
   }

   /**
    * Exposes the delegate Log4j Logger.
    */
   public org.apache.log4j.Logger getLogger()
   {
      return log;
   }
   
   /**
    * Return the name of this logger.
    *
    * @return The name of this logger.
    */
   public String getName()
   {
      return name;
   }
   
   /** 
    * Check to see if the TRACE level is enabled for this logger.
    *
    * @return true if a {@link #trace(Object)} method invocation would pass
    *         the msg to the configured appenders, false otherwise.
    */
   public boolean isTraceEnabled()
   {
      if (log.isEnabledFor(XLevel.TRACE) == false)
         return false;
      return XLevel.TRACE.isGreaterOrEqual(log.getEffectiveLevel());
   }

   /** 
    * Issue a log msg with a level of TRACE.
    * Invokes log.log(XLevel.TRACE, message);
    */
   public void trace(Object message)
   {
      log.log(XLevel.TRACE, message);
   }

   /** 
    * Issue a log msg and throwable with a level of TRACE.
    * Invokes log.log(XLevel.TRACE, message, t);
    */
   public void trace(Object message, Throwable t)
   {
      log.log(XLevel.TRACE, message, t);
   }

   /**
    * Check to see if the TRACE level is enabled for this logger.
    *
    * @return true if a {@link #trace(Object)} method invocation would pass
    * the msg to the configured appenders, false otherwise.
    */
   public boolean isDebugEnabled()
   {
      Level l = Level.DEBUG;
      if (log.isEnabledFor(l) == false)
         return false;
      return l.isGreaterOrEqual(log.getEffectiveLevel());
   }

   /** 
    * Issue a log msg with a level of DEBUG.
    * Invokes log.log(Level.DEBUG, message);
    */
   public void debug(Object message)
   {
      log.log(Level.DEBUG, message);
   }

   /** 
    * Issue a log msg and throwable with a level of DEBUG.
    * Invokes log.log(Level.DEBUG, message, t);
    */
   public void debug(Object message, Throwable t)
   {
      log.log(Level.DEBUG, message, t);
   }

   /** 
    * Check to see if the INFO level is enabled for this logger.
    *
    * @return true if a {@link #info(Object)} method invocation would pass
    * the msg to the configured appenders, false otherwise.
    */
   public boolean isInfoEnabled()
   {
      Level l = Level.INFO;
      if (log.isEnabledFor(l) == false)
         return false;
      return l.isGreaterOrEqual(log.getEffectiveLevel());
   }

   /** 
    * Issue a log msg with a level of INFO.
    * Invokes log.log(Level.INFO, message);
    */
   public void info(Object message)
   {
      log.log(Level.INFO, message);
   }

   /**
    * Issue a log msg and throwable with a level of INFO.
    * Invokes log.log(Level.INFO, message, t);
    */
   public void info(Object message, Throwable t)
   {
      log.log(Level.INFO, message, t);
   }

   /** 
    * Issue a log msg with a level of WARN.
    * Invokes log.log(Level.WARN, message);
    */
   public void warn(Object message)
   {
      log.log(Level.WARN, message);
   }

   /** 
    * Issue a log msg and throwable with a level of WARN.
    * Invokes log.log(Level.WARN, message, t);
    */
   public void warn(Object message, Throwable t)
   {
      log.log(Level.WARN, message, t);
   }

   /** 
    * Issue a log msg with a level of ERROR.
    * Invokes log.log(Level.ERROR, message);
    */
   public void error(Object message)
   {
      log.log(Level.ERROR, message);
   }

   /** 
    * Issue a log msg and throwable with a level of ERROR.
    * Invokes log.log(Level.ERROR, message, t);
    */
   public void error(Object message, Throwable t)
   {
      log.log(Level.ERROR, message, t);
   }

   /** 
    * Issue a log msg with a level of FATAL.
    * Invokes log.log(Level.FATAL, message);
    */
   public void fatal(Object message)
   {
      log.log(Level.FATAL, message);
   }

   /** 
    * Issue a log msg and throwable with a level of FATAL.
    * Invokes log.log(Level.FATAL, message, t);
    */
   public void fatal(Object message, Throwable t)
   {
      log.log(Level.FATAL, message, t);
   }

   /** 
    * Issue a log msg with the given level.
    * Invokes log.log(p, message);
    *
    * @deprecated  Use Level versions.
    */
   public void log(Priority p, Object message)
   {
      log.log(p, message);
   }

   /** 
    * Issue a log msg with the given priority.
    * Invokes log.log(p, message, t);
    *
    * @deprecated  Use Level versions.
    */
   public void log(Priority p, Object message, Throwable t)
   {
      log.log(p, message, t);
   }

   /** 
    * Issue a log msg with the given level.
    * Invokes log.log(l, message);
    */
   public void log(Level l, Object message)
   {
      log.log(l, message);
   }

   /** 
    * Issue a log msg with the given level.
    * Invokes log.log(l, message, t);
    */
   public void log(Level l, Object message, Throwable t)
   {
      log.log(l, message, t);
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
      log = LogManager.getLogger(name);
   }


   /////////////////////////////////////////////////////////////////////////
   //                            Factory Methods                          //
   /////////////////////////////////////////////////////////////////////////

   /** 
    * Create a Logger instance given the logger name.
    *
    * @param name    the logger name
    */
   public static Logger getLogger(String name)
   {
      return new Logger(name);
   }

   /** 
    * Create a Logger instance given the logger name with the given suffix.
    *
    * <p>This will include a logger seperator between classname and suffix
    *
    * @param name     The logger name
    * @param suffix   A suffix to append to the classname.
    */
   public static Logger getLogger(String name, String suffix)
   {
      return new Logger(name + "." + suffix);
   }

   /** 
    * Create a Logger instance given the logger class. This simply
    * calls create(clazz.getName()).
    *
    * @param clazz    the Class whose name will be used as the logger name
    */
   public static Logger getLogger(Class clazz)
   {
      return new Logger(clazz.getName());
   }

   /** 
    * Create a Logger instance given the logger class with the given suffix.
    *
    * <p>This will include a logger seperator between classname and suffix
    *
    * @param clazz    The Class whose name will be used as the logger name.
    * @param suffix   A suffix to append to the classname.
    */
   public static Logger getLogger(Class clazz, String suffix)
   {
      return new Logger(clazz.getName() + "." + suffix);
   }
}
