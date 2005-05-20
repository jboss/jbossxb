/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;
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

   public static ArrayContainer createArrayContainer(Class itemType)
   {
      return new ArrayContainer(itemType);
   }

   public static Object createArray(ArrayContainer container)
   {
      Object[] o = (Object[])Array.newInstance(container.itemType, container.items.size());
      return container.items.toArray(o);
   }

   public static void set(Object o, Object value, String prop, String colType, boolean ignoreNotFoundField)
   {
      Class cls = o.getClass();
      Method getter = null;
      Method setter = null;
      Field field = null;
      Class fieldType;
      try
      {
         String methodBase = Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
         getter = cls.getMethod("get" + methodBase, null);
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
      else
      {
         set(o, value, setter, field);
      }
   }

   public static void set(Object o, QName elementName, TypeBinding type, Object value)
   {
      if(o instanceof Collection)
      {
         ((Collection)o).add(value);
      }
      else
      {
         Class cls = o.getClass();
         String methodBase = Util.xmlNameToClassName(elementName.getLocalPart(), true);
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
               field = cls.getField(Util.xmlNameToFieldName(elementName.getLocalPart(), true));
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

   public static class ArrayContainer
   {
      public final Class itemType;
      public final List items = new ArrayList();

      public ArrayContainer(Class itemType)
      {
         this.itemType = itemType;
      }
   }
}
