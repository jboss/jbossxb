/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling.impl.runtime;

import java.util.Collection;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtUtil
{
   private static final Logger log = Logger.getLogger(RtUtil.class);

   private RtUtil()
   {
   }

   public static void set(Object o,
                          Object value,
                          String prop,
                          String colType,
                          boolean ignoreNotFoundField,
                          ValueAdapter valueAdapter)
   {
      Class cls = o.getClass();
      Method getter = null;
      Method setter = null;
      Field field = null;
      Class fieldType;
      try
      {
         String methodBase = Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
         try
         {
            getter = cls.getMethod("get" + methodBase, null);
         }
         catch(NoSuchMethodException e)
         {
            getter = cls.getMethod("is" + methodBase, null);
         }

         fieldType = getter.getReturnType();
         setter = cls.getMethod("set" + methodBase, new Class[]{fieldType});
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            field = cls.getField(prop);
            fieldType = field.getType();
         }
         catch(NoSuchFieldException e1)
         {
            if(ignoreNotFoundField)
            {
               log.warn("Neither getter/setter nor field were found for field " + prop + " in " + cls);
               return;
            }

            throw new JBossXBRuntimeException(
               "Neither getter/setter nor field were found for field " + prop + " in " + cls
            );
         }
      }

      if(valueAdapter != null)
      {
         value = valueAdapter.cast(value, fieldType);
      }

      if(colType != null ||
         // todo collections of collections
         Collection.class.isAssignableFrom(fieldType) &&
         !Collection.class.isAssignableFrom(value.getClass()))
      {
         Collection col = (Collection)get(o, getter, field);
         if(col == null)
         {
            if(colType == null)
            {
               col = new ArrayList();
            }
            else
            {
               Class colCls;
               try
               {
                  colCls = Thread.currentThread().getContextClassLoader().loadClass(colType);
               }
               catch(ClassNotFoundException e)
               {
                  throw new JBossXBRuntimeException("Failed to load collection type: " + colType);
               }

               try
               {
                  col = (Collection)colCls.newInstance();
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException("Failed to create an instance of " + colCls);
               }
            }

            set(o, col, setter, field);
         }

         col.add(value);
      }
      else if(fieldType.isArray() && value != null && !value.getClass().isArray())
      {
         Object arr = get(o, getter, field);
         int length = 0;
         if(arr == null)
         {
            arr = Array.newInstance(fieldType.getComponentType(), 1);
         }
         else
         {
            Object tmp = arr;
            length = Array.getLength(arr);
            arr = Array.newInstance(fieldType.getComponentType(), length + 1);
            System.arraycopy(tmp, 0, arr, 0, length);
         }
         Array.set(arr, length, value);
         set(o, arr, setter, field);
      }
      else
      {
         set(o, value, setter, field);
      }
   }

   public static void set(Object o, QName elementName, Object value, boolean ignoreLowLine)
   {
      if(o instanceof Collection)
      {
         ((Collection)o).add(value);
      }
      else
      {
         Class cls = o.getClass();
         String methodBase = Util.xmlNameToClassName(elementName.getLocalPart(), ignoreLowLine);
         Method setter = null;
         Field field = null;
         try
         {
            Method getter = cls.getMethod("get" + methodBase, null);
            setter = cls.getMethod("set" + methodBase, new Class[]{getter.getReturnType()});
         }
         catch(NoSuchMethodException e)
         {
            try
            {
               field = cls.getField(Util.xmlNameToFieldName(elementName.getLocalPart(), ignoreLowLine));
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException(
                  "Neither getter/setter nor field were found for " + elementName + " in " + cls
               );
            }
         }

         set(o, value, setter, field);
      }
   }

   private static void set(Object o, Object value, Method setter, Field field)
   {
      try
      {
         if(setter != null)
         {
            setter.invoke(o, new Object[]{value});
         }
         else if(field != null)
         {
            field.set(o, value);
         }
         else
         {
            throw new JBossXBRuntimeException("Neither setter nor field is available!");
         }
      }
      catch(JBossXBRuntimeException e)
      {
         throw e;
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Failed to set value " +
            (value == null ? "null" : value.getClass().getName() + '@' + value.hashCode() + "[" + value + "]")
            +
            (field == null ?
            (setter == null ? "" : " using setter " + setter.getName()) :
            " using field " + field.getName()
            ) +
            " on " +
            (o == null ? "null" : o.getClass().getName() + '@' + o.hashCode() + "[" + o + "]") +
            " : " +
            e.getMessage(),
            e
         );
      }
   }

   private static Object get(Object o, Method getter, Field field)
   {
      Object result;
      try
      {
         if(getter != null)
         {
            result = getter.invoke(o, null);
         }
         else if(field != null)
         {
            result = field.get(o);
         }
         else
         {
            throw new JBossXBRuntimeException("Neither getter nor field is available!");
         }
      }
      catch(JBossXBRuntimeException e)
      {
         throw e;
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Failed to get value " +
            (field == null ?
            (getter == null ? "" : " using getter " + getter.getName()) :
            " using field " + field.getName()
            ) +
            " on " +
            (o == null ? "null" : o.getClass().getName() + '@' + o.hashCode() + "[" + o + "]") +
            " : " +
            e.getMessage(),
            e
         );
      }

      return result;
   }

   // Inner

}
