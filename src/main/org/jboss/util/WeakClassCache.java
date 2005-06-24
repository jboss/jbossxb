/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A weak class cache that instantiates does not a hold a
 * strong reference to either the classloader or class.<p>
 * 
 * It creates the class specific data in two stages
 * to avoid recursion.<p>
 * 
 * instantiate - creates the data<br>
 * generate - fills in the details
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 */
public abstract class WeakClassCache
{
   // Constants -----------------------------------------------------
   
   // Attributes ----------------------------------------------------
   
   /** The cache */
   protected Map cache = new WeakHashMap(); 

   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the information for a class
    * 
    * @param clazz the class
    * @return the info
    */
   public Object get(Class clazz)
   {
      Map classLoaderCache = getClassLoaderCache(clazz.getClassLoader());

      WeakReference weak = (WeakReference) classLoaderCache.get(clazz.getName());
      if (weak != null)
      {
         Object result = weak.get();
         if (result != null)
            return result;
      }

      Object result = instantiate(clazz);

      weak = new WeakReference(result);
      classLoaderCache.put(clazz.getName(), weak);
      
      generate(clazz, result);
      
      return result;
   }
   
   /**
    * Get the information for a class
    * 
    * @param name the name
    * @param cl the classloader
    * @return the info
    * @throws ClassNotFoundException when the class cannot be found
    */
   public Object get(String name, ClassLoader cl) throws ClassNotFoundException
   {
      Class clazz = cl.loadClass(name);
      return get(clazz);
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   /**
    * Instantiate for a class
    * 
    * @param clazz the class
    * @return the result
    */
   protected abstract Object instantiate(Class clazz);
   
   /**
    * Fill in the result
    * 
    * @param clazz the class
    * @param the result
    */
   protected abstract void generate(Class clazz, Object result);
   
   /**
    * Get the cache for the classloader
    * 
    * @param cl the classloader
    * @return the map
    */
   protected Map getClassLoaderCache(ClassLoader cl)
   {
      synchronized (cache)
      {
         Map result = (Map) cache.get(cl);
         if (result == null)
         {
            result = CollectionsFactory.createConcurrentReaderMap();
            cache.put(cl, result);
         }
         return result;
      }
   }
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
