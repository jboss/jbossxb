/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.util;

/*
 * ClassUtils.java
 *
 * Created on May 5, 2002, 8:11 PM
 */
import java.lang.reflect.Array;


/**
 * 
 * @author  Peter Levart <plevart@users.sourceforge.net>
 */
public class ClassUtils
{
   /**
    * This method acts equivalently to invoking <code>Thread.currentThread().getContextClassLoader().loadClass(className);</code> but it also
    * supports primitive types and array classes of object types or primitive types.
    *
    * @param className the qualified name of the class or the name of primitive type or array in the same format
    *        as returned by the <code>java.lang.Class.getName()</code> method.
    *
    * @returns the Class object for the requested className
    *
    * @throws ClassNotFoundException when the <code>classLoader</code> can not find the requested class
    */
   public static Class loadClass(String className) throws ClassNotFoundException {
      return loadClass( className, Thread.currentThread().getContextClassLoader() );
   }
   
   /**
    * This method acts equivalently to invoking <code>classLoader.loadClass(className);</code> but it also
    * supports primitive types and array classes of object types or primitive types.
    *
    * @param className the qualified name of the class or the name of primitive type or array in the same format
    *        as returned by the <code>java.lang.Class.getName()</code> method.
    *
    * @param classLoader the ClassLoader used to load classes
    *
    * @returns the Class object for the requested className
    *
    * @throws ClassNotFoundException when the <code>classLoader</code> can not find the requested class
    */
   public static Class loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException
   {
      // ClassLoader.loadClass() does not handle primitive types:
      //
      //   B            byte
      //   C            char
      //   D            double
      //   F            float
      //   I            int
      //   J            long
      //   S            short
      //   Z            boolean
      //   V	         void
      //
      if (className.length() == 1)
      {
         char type = className.charAt(0);
         if (type == 'B') return Byte.TYPE;
         if (type == 'C') return Character.TYPE;
         if (type == 'D') return Double.TYPE;
         if (type == 'F') return Float.TYPE;
         if (type == 'I') return Integer.TYPE;
         if (type == 'J') return Long.TYPE;
         if (type == 'S') return Short.TYPE;
         if (type == 'Z') return Boolean.TYPE;
         if (type == 'V') return Void.TYPE;
         // else throw...
         throw new ClassNotFoundException(className);
      }
      
      // ...nore does this special notation:
      //
      //   Lclassname;  class or interface
      //
      if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';')
         return classLoader.loadClass(className.substring(1, className.length() - 1));
      
      // first try - be optimistic
      // this will succeed for all non-array classes and array classes that have already been resolved
      //
      try
      {
         return classLoader.loadClass(className);
      }
      catch (ClassNotFoundException e)
      {
         // if it was non-array class then throw it
         if (className.charAt(0) != '[')
            throw e;
      }
   
      // we are now resolving array class for the first time
      
      // count opening braces
      int arrayDimension = 0;
      while (className.charAt(arrayDimension) == '[')
         arrayDimension++;
            
      // resolve component type - use recursion so that we can resolve primitive types also
      Class componentType = loadClass(className.substring(arrayDimension), classLoader);
      
      // construct array class
      return Array.newInstance(componentType, new int[arrayDimension]).getClass();
   }
      
}

