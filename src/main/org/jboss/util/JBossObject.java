/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util;

import java.util.Collection;
import java.util.Iterator;

import org.jboss.logging.Logger;

/**
 * Utility Class
 * 
 * Utility Class that provides a Logger instance (log) and
 * caching of toString() and hashCode() values.
 * 
 * You most probably want to override the method that
 * comes from JBossInterface:
 * 
 *    public void toShortString(StringBuffer buffer)
 *
 * to append to the buffer the key class properties, and
 * also override the following methods to provide the
 * hashCode and the class properties that should be cached:
 * 
 *    protected void toString(StringBuffer buffer)
 *    protected int getHashCode()
 * 
 * Cached values can be flushed using flushJBossObjectCache()
 * 
 * Caching can be disabled by simply overriding toString()
 * and hashCode(), or returning false from methods:
 * 
 *    protected boolean cacheToString()
 *    protected boolean cacheGetHashCode()  
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class JBossObject implements JBossInterface
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------
   
   protected Logger log;
   
   /** Cached toString */
   protected transient String toString;
   
   /** Cached hashCode */
   protected transient int hashCode = Integer.MIN_VALUE;
   
   // Static --------------------------------------------------------

   /**
    * Safe equality check
    * 
    * @param one an object
    * @param two another object
    */
   public static boolean equals(Object one, Object two)
   {
      if (one == null && two != null)
         return false;
      if (one != null && one.equals(two) == false)
         return false;
      return true;
   }

   /**
    * Safe inequality check
    * 
    * @param one an object
    * @param two another object
    */
   public static boolean notEqual(Object one, Object two)
   {
      return equals(one, two) == false;
   }

   /**
    * List the set of JBossObjects
    *
    * @param buffer the buffer
    * @param objects the collection of objects
    */
   public static void list(StringBuffer buffer, Collection objects)
   {
      if (objects == null)
         return;

      buffer.append('[');
      if (objects.isEmpty() == false)
      {
         for (Iterator i = objects.iterator(); i.hasNext();)
         {
            Object object = i.next();
            if (object instanceof JBossObject)
               ((JBossObject) object).toShortString(buffer);
            else
               buffer.append(object.toString());
            if (i.hasNext())
               buffer.append(", ");
         }
      }
      buffer.append(']');
   }
   
   // Constructors --------------------------------------------------

   /**
    * Create a new object
    */
   public JBossObject()
   {
      log = Logger.getLogger(getClass());
   }
   
   /**
    * Create a new object using the specified Logger instace
    * 
    * @param log the Logger instance to use
    */
   public JBossObject(Logger log)
   {
      this.log = (log != null) ? log : Logger.getLogger(getClass());
   }
   
   // Object overrides ----------------------------------------------
   
   /**
    * Override toString to cache the value
    * 
    * @return the String
    */
   public String toString()
   {
      if (toString == null || cacheToString() == false)
         toString = toStringImplementation();
      return toString;
   }
   
   /**
    * Override hashCode to cache the value
    * 
    * @return the hashCode
    */
   public int hashCode()
   {
      if (hashCode == Integer.MIN_VALUE || cacheGetHashCode() == false)
         hashCode = getHashCode();
      return hashCode;
   }
   
   // JBossCloneable implementation ---------------------------------
   
   public Object clone()
   {
      try
      {
         return super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public String toShortString()
   {
      StringBuffer buffer = new StringBuffer();
      toShortString(buffer);
      return buffer.toString();
   }
   
   /**
    * Append the key class properties to the buffer
    * 
    * @param buffer the buffer
    */
   public void toShortString(StringBuffer buffer)
   {
   }
   
   // Public --------------------------------------------------------
   
   /**
    * Get the class short name
    * 
    * @return the short name of the class
    */
   public String getClassShortName()
   {
      String longName = getClass().getName();
      int dot = longName.lastIndexOf('.');
      if (dot != -1)
         return longName.substring(dot + 1);
      return longName;
   }
   
   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   /**
    * Implementation of String
    * 
    * @return the string
    */
   protected String toStringImplementation()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClassShortName()).append('@');
      buffer.append(Integer.toHexString(System.identityHashCode(this)));
      buffer.append('{');
      toString(buffer);
      buffer.append('}');
      return buffer.toString();
   }
   
   /**
    * Flush the JBossObject cached values
    */
   protected void flushJBossObjectCache()
   {
      toString = null;
      hashCode = Integer.MIN_VALUE;
   }
   
   // Possible Overrides --------------------------------------------
   
   /**
    * Append the class properties to the buffer
    * 
    * @param buffer the buffer
    */
   protected void toString(StringBuffer buffer)
   {
   }
   
   /**
    * Calculate the hashcode
    * 
    * @return the hash code
    */
   protected int getHashCode()
   {
      return super.hashCode();
   }
   
   /**
    * Whether we should cache the result toString()
    * 
    * @return true by default
    */
   protected boolean cacheToString()
   {
      return true;
   }
   
   /**
    * Whether we should cache the result hashCode()
    * 
    * @return true by default
    */
   protected boolean cacheGetHashCode()
   {
      return true;
   }
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}