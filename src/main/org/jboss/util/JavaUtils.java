/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util;

// $Id$

import java.lang.reflect.Array;

/** Java utilities
 *
 * @author Thomas.Diesler@jboss.org
 * @since 22-Dec-2004
 */
public class JavaUtils
{
   /** 
    * Load a Java type from a given class loader.
    * 
    * @param typeName maybe the source notation of a primitve, class name, array of both 
    */
   public static Class loadJavaType(String typeName, ClassLoader classLoader) throws ClassNotFoundException
   {
      Class javaType;
      if (isPrimitive(typeName))
      {
         javaType = getPrimitiveType(typeName);
      }
      else if (typeName.endsWith("[]"))
      {
         String compTypeName = typeName.substring(0, typeName.length() - 2);
         if (compTypeName.endsWith("[]"))
            throw new IllegalArgumentException("Multidimensional arrays not supported: " + typeName);

         Class compType = loadJavaType(compTypeName, classLoader);
         javaType = Array.newInstance(compType, 0).getClass();
      }
      else
      {
         javaType = classLoader.loadClass(typeName);
      }

      return javaType;
   }

   /**
    * True if the given type name is the source notation of a primitive or array of which.
    */
   public static boolean isPrimitive(String javaType)
   {
      return getPrimitiveType(javaType) != null;
   }

   /**
    * True if the given class is a primitive or array of which.
    */
   public static boolean isPrimitive(Class javaType)
   {
      return getPrimitiveType(javaType.getName()) != null;
   }

   /**
    * Get the class corresponding to a given type name 
    * The type name is the source notation of a primitive or array of which.
    */
   public static Class getPrimitiveType(String javaType)
   {
      if ("int".equals(javaType))
         return int.class;
      if ("short".equals(javaType))
         return short.class;
      if ("boolean".equals(javaType))
         return boolean.class;
      if ("byte".equals(javaType))
         return byte.class;
      if ("long".equals(javaType))
         return long.class;
      if ("double".equals(javaType))
         return double.class;
      if ("float".equals(javaType))
         return float.class;
      if ("char".equals(javaType))
         return char.class;

      if ("int[]".equals(javaType) || "[I".equals(javaType))
         return int[].class;
      if ("short[]".equals(javaType) || "[S".equals(javaType))
         return short[].class;
      if ("boolean[]".equals(javaType) || "[Z".equals(javaType))
         return boolean[].class;
      if ("byte[]".equals(javaType) || "[B".equals(javaType))
         return byte[].class;
      if ("long[]".equals(javaType) || "[J".equals(javaType))
         return long[].class;
      if ("double[]".equals(javaType) || "[D".equals(javaType))
         return double[].class;
      if ("float[]".equals(javaType) || "[F".equals(javaType))
         return float[].class;
      if ("char[]".equals(javaType) || "[C".equals(javaType))
         return char[].class;

      return null;
   }

   /**
    * Get the corresponding primitive for a give wrapper type.
    * Also handles arrays of which.
    */
   public static Class getPrimitiveType(Class wrapperType)
   {
      if (wrapperType == Integer.class)
         return int.class;
      if (wrapperType == Short.class)
         return short.class;
      if (wrapperType == Boolean.class)
         return boolean.class;
      if (wrapperType == Byte.class)
         return byte.class;
      if (wrapperType == Long.class)
         return long.class;
      if (wrapperType == Double.class)
         return double.class;
      if (wrapperType == Float.class)
         return float.class;
      if (wrapperType == Character.class)
         return char.class;

      if (wrapperType == Integer[].class)
         return int[].class;
      if (wrapperType == Short[].class)
         return short[].class;
      if (wrapperType == Boolean[].class)
         return boolean[].class;
      if (wrapperType == Byte[].class)
         return byte[].class;
      if (wrapperType == Long[].class)
         return long[].class;
      if (wrapperType == Double[].class)
         return double[].class;
      if (wrapperType == Float[].class)
         return float[].class;
      if (wrapperType == Character[].class)
         return char[].class;

      return null;
   }

   /**
    * Get the corresponding primitive value for a give wrapper value.
    * Also handles arrays of which.
    */
   public static Object getPrimitiveValue(Object value)
   {
      if (value == null)
         return null;

      Class javaType = value.getClass();
      if (javaType == Integer.class)
         return ((Integer)value).intValue();
      if (javaType == Short.class)
         return ((Short)value).shortValue();
      if (javaType == Boolean.class)
         return ((Boolean)value).booleanValue();
      if (javaType == Byte.class)
         return ((Byte)value).byteValue();
      if (javaType == Long.class)
         return ((Long)value).longValue();
      if (javaType == Double.class)
         return ((Double)value).doubleValue();
      if (javaType == Float.class)
         return ((Float)value).floatValue();

      if (javaType == Integer[].class)
      {
         Integer[] src = (Integer[])value;
         int[] dest = new int[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].intValue();
         return dest;
      }
      if (javaType == Short[].class)
      {
         Short[] src = (Short[])value;
         short[] dest = new short[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].shortValue();
         return dest;
      }
      if (javaType == Boolean[].class)
      {
         Boolean[] src = (Boolean[])value;
         boolean[] dest = new boolean[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].booleanValue();
         return dest;
      }
      if (javaType == Byte[].class)
      {
         Byte[] src = (Byte[])value;
         byte[] dest = new byte[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].byteValue();
         return dest;
      }
      if (javaType == Long[].class)
      {
         Long[] src = (Long[])value;
         long[] dest = new long[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].longValue();
         return dest;
      }
      if (javaType == Double[].class)
      {
         Double[] src = (Double[])value;
         double[] dest = new double[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].doubleValue();
         return dest;
      }
      if (javaType == Float[].class)
      {
         Float[] src = (Float[])value;
         float[] dest = new float[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].floatValue();
         return dest;
      }
      if (javaType == Character[].class)
      {
         Character[] src = (Character[])value;
         char[] dest = new char[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = src[i].charValue();
         return dest;
      }

      return value;
   }

   /**
    * Get the corresponding wrapper type for a give primitive.
    * Also handles arrays of which.
    */
   public static Class getWrapperType(Class primitiveType)
   {
      if (primitiveType == int.class)
         return Integer.class;
      if (primitiveType == short.class)
         return Short.class;
      if (primitiveType == boolean.class)
         return Boolean.class;
      if (primitiveType == byte.class)
         return Byte.class;
      if (primitiveType == long.class)
         return Long.class;
      if (primitiveType == double.class)
         return Double.class;
      if (primitiveType == float.class)
         return Float.class;
      if (primitiveType == char.class)
         return Character.class;

      if (primitiveType == int[].class)
         return Integer[].class;
      if (primitiveType == short[].class)
         return Short[].class;
      if (primitiveType == boolean[].class)
         return Boolean[].class;
      if (primitiveType == byte[].class)
         return Byte[].class;
      if (primitiveType == long[].class)
         return Long[].class;
      if (primitiveType == double[].class)
         return Double[].class;
      if (primitiveType == float[].class)
         return Float[].class;
      if (primitiveType == char[].class)
         return Character[].class;

      return null;
   }

   /**
    * Get the corresponding wrapper value for a give primitive value.
    * Also handles arrays of which.
    */
   public static Object getWrapperValue(Object value)
   {
      if (value == null)
         return null;

      Class javaType = value.getClass();
      if (javaType == Integer.class)
         return Integer.valueOf("" + value);
      if (javaType == Short.class)
         return Short.valueOf("" + value);
      if (javaType == Boolean.class)
         return Boolean.valueOf("" + value);
      if (javaType == Byte.class)
         return Byte.valueOf("" + value);
      if (javaType == Long.class)
         return Long.valueOf("" + value);
      if (javaType == Double.class)
         return Double.valueOf("" + value);
      if (javaType == Float.class)
         return Float.valueOf("" + value);

      if (javaType == int[].class)
      {
         int[] src = (int[])value;
         Integer[] dest = new Integer[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Integer(src[i]);
         return dest;
      }
      if (javaType == short[].class)
      {
         short[] src = (short[])value;
         Short[] dest = new Short[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Short(src[i]);
         return dest;
      }
      if (javaType == boolean[].class)
      {
         boolean[] src = (boolean[])value;
         Boolean[] dest = new Boolean[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Boolean(src[i]);
         return dest;
      }
      if (javaType == byte[].class)
      {
         byte[] src = (byte[])value;
         Byte[] dest = new Byte[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Byte(src[i]);
         return dest;
      }
      if (javaType == long[].class)
      {
         long[] src = (long[])value;
         Long[] dest = new Long[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Long(src[i]);
         return dest;
      }
      if (javaType == double[].class)
      {
         double[] src = (double[])value;
         Double[] dest = new Double[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Double(src[i]);
         return dest;
      }
      if (javaType == float[].class)
      {
         float[] src = (float[])value;
         Float[] dest = new Float[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Float("" + src[i]);
         return dest;
      }
      if (javaType == char[].class)
      {
         char[] src = (char[])value;
         Character[] dest = new Character[src.length];
         for (int i = 0; i < src.length; i++)
            dest[i] = new Character(src[i]);
         return dest;
      }

      return value;
   }
}
