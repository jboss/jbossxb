/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A collection of <code>Class</code> utilities.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class Classes
{
   /** The string used to separator packages */
   public static final String PACKAGE_SEPARATOR = ".";

   /** The characther used to separator packages */
   public static final char PACKAGE_SEPARATOR_CHAR = '.';

   /** The default package name. */
   public static final String DEFAULT_PACKAGE_NAME = "<default>";

   /**
    * Get the short name of the specified class by striping off the package
    * name.
    *
    * @param classname  Class name.
    * @return           Short class name.
    */
   public static String stripPackageName(final String classname) {
      int idx = classname.lastIndexOf(PACKAGE_SEPARATOR);

      if (idx != -1)
         return classname.substring(idx + 1, classname.length());
      return classname;
   }

   /**
    * Get the short name of the specified class by striping off the package
    * name.
    *
    * @param type    Class name.
    * @return        Short class name.
    */
   public static String stripPackageName(final Class type) {
      return stripPackageName(type.getName());
   }

   /**
    * Get the package name of the specified class.
    *
    * @param classname  Class name.
    * @return           Package name or "" if the classname is in the
    *                   <i>default</i> package.
    *
    * @throws EmptyStringException     Classname is an empty string.
    */
   public static String getPackageName(final String classname) {
      if (classname.length() == 0)
          throw new EmptyStringException();
      
      int index = classname.lastIndexOf(PACKAGE_SEPARATOR);
      if (index != -1)
         return classname.substring(0, index);
      return "";
   }

   /**
    * Get the package name of the specified class.
    *
    * @param type    Class.
    * @return        Package name.
    */
   public static String getPackageName(final Class type) {
      return getPackageName(type.getName());
   }

   /**
    * Force the given class to be loaded fully.
    *
    * <p>This method attempts to locate a static method on the given class
    *    the attempts to invoke it with dummy arguments in the hope that
    *    the virtual machine will prepare the class for the method call and
    *    call all of the static class initializers.
    *
    * @param type    Class to force load.
    *
    * @throws NullArgumentException    Type is <i>null</i>.
    */
   public static void forceLoad(final Class type) {
      if (type == null)
         throw new NullArgumentException("type");
      
      // don't attempt to force primitives to load
      if (type.isPrimitive()) return;

      // don't attempt to force java.* classes to load
      String packageName = Classes.getPackageName(type);
      // System.out.println("package name: " + packageName);

      if (packageName.startsWith("java.") || 
          packageName.startsWith("javax.")) {
         return;
      }

      // System.out.println("forcing class to load: " + type);

      try {
         Method methods[] = type.getDeclaredMethods();
         Method method = null;
         for (int i=0; i<methods.length; i++) {
            int modifiers = methods[i].getModifiers();
            if (Modifier.isStatic(modifiers)) {
               method = methods[i];
               break;
            }
         }

         if (method != null) {
            method.invoke(null, null);
         }
         else {
            type.newInstance();
         }
      }
      catch (Exception ignore) {
         ThrowableHandler.add(ignore);
      }
   }


   /////////////////////////////////////////////////////////////////////////
   //                               Primitives                            //
   /////////////////////////////////////////////////////////////////////////

   /** Map of primitive types to their wrapper classes */
   private static final Class[] PRIMITIVE_WRAPPER_MAP = {
      Boolean.TYPE,     Boolean.class,
      Byte.TYPE,        Byte.class,
      Character.TYPE,   Character.class,
      Double.TYPE,      Double.class,
      Float.TYPE,       Float.class,
      Integer.TYPE,     Integer.class,
      Long.TYPE,        Long.class,
      Short.TYPE,       Short.class,
   };

   /**
    * Get the wrapper class for the given primitive type.
    *
    * @param type    Primitive class.
    * @return        Wrapper class for primitive.
    *
    * @exception IllegalArgumentException    Type is not a primitive class
    */
   public static Class getPrimitiveWrapper(final Class type) {
      if (! type.isPrimitive()) {
         throw new IllegalArgumentException("type is not a primitive class");
      }

      for (int i=0; i < PRIMITIVE_WRAPPER_MAP.length; i += 2) {
         if (type.equals(PRIMITIVE_WRAPPER_MAP[i]))
            return PRIMITIVE_WRAPPER_MAP[i + 1];
      }

      // should never get here, if we do then PRIMITIVE_WRAPPER_MAP
      // needs to be updated to include the missing mapping
      throw new UnreachableStatementException();
   }

   /**
    * Check if the given class is a primitive wrapper class.
    *
    * @param type    Class to check.
    * @return        True if the class is a primitive wrapper.
    */
   public static boolean isPrimitiveWrapper(final Class type) {
      for (int i=0; i < PRIMITIVE_WRAPPER_MAP.length; i += 2) {
         if (type.equals(PRIMITIVE_WRAPPER_MAP[i + 1])) {
            return true;
         }
      }

      return false;
   }

   /**
    * Check if the given class is a primitive class or a primitive 
    * wrapper class.
    *
    * @param type    Class to check.
    * @return        True if the class is a primitive or primitive wrapper.
    */
   public static boolean isPrimitive(final Class type) {
      if (type.isPrimitive() || isPrimitiveWrapper(type)) {
         return true;
      }

      return false;
   }

   /** Do not allow public instantiation of this class. */
   private Classes() {}
}
