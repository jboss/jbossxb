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
package org.jboss.xb.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;

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

   public static void add(Object o,
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

      boolean arrType;
      if(fieldType.isArray())
      {
         arrType = true;
      }
      else if(Collection.class.isAssignableFrom(fieldType))
      {
         arrType = false;
      }
      else
      {
         throw new JBossXBRuntimeException(
            "Expected type for " + prop + " in " + cls + " is an array or java.util.Collection but was " + fieldType
         );
      }


      if(valueAdapter != null)
      {
         value = valueAdapter.cast(value, fieldType);
      }

      if(!arrType || colType != null)
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
         getter = Classes.getAttributeGetter(cls, prop);
         fieldType = getter.getReturnType();
         setter = Classes.getAttributeSetter(cls, prop, fieldType);
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
               log.warn("Neither field '" + prop + "' nor its getter/setter were found in " + cls);
               return;
            }

            throw new JBossXBRuntimeException("Neither field '" + prop + "' nor its getter/setter were found in " + cls);
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
      else if(fieldType.isArray() &&
         value != null &&
         (fieldType.getComponentType().isAssignableFrom(value.getClass()) ||
         fieldType.getComponentType().isPrimitive() &&
         Classes.getPrimitiveWrapper(fieldType.getComponentType()) == value.getClass()
         ))
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
         // todo: unmarshalling should produce the right type instead
         Class valueClass = value == null ? null : value.getClass();
         if(valueClass != null && !fieldType.isAssignableFrom(valueClass))
         {
            if(fieldType.isArray() && Collection.class.isAssignableFrom(valueClass))
            {
               Collection col = (Collection)value;
               Class compType = fieldType.getComponentType();
               value = Array.newInstance(compType, col.size());
               if(compType.isPrimitive())
               {
                  int i = 0;
                  for(Iterator iter = col.iterator(); iter.hasNext();)
                  {
                     Array.set(value, i++, iter.next());
                  }
               }
               else
               {
                  value = col.toArray((Object[])value);
               }
            }
            else if(Collection.class.isAssignableFrom(fieldType) && valueClass.isArray())
            {
               int length = Array.getLength(value);
               Collection col = new ArrayList(length);
               for(int i = 0; i < length; ++i)
               {
                  col.add(Array.get(value, i));
               }
               value = col;
            }
            // else hopefully it's a primitive/wrapper case
         }

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
            (o == null ? "null" : o.getClass().getName() + '@' + o.hashCode() + " (toString=" + o + ")") +
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

   public static Class loadClass(String clsName, boolean failIfNotFound)
   {
      Class cls = null;
      try
      {
         cls = Classes.loadClass(clsName);
      }
      catch(ClassNotFoundException e)
      {
         if(failIfNotFound)
         {
            throw new JBossXBRuntimeException("Failed to load class " + clsName);
         }
      }
      return cls;
   }

   public static Method getUnmarshalMethod(QName qName, ValueMetaData valueMetaData)
   {
      String unmarshalMethod = valueMetaData.getUnmarshalMethod();
      if(unmarshalMethod == null)
      {
         throw new JBossXBRuntimeException(
            "javaType annotation is specified for " + qName + " but does not contain parseMethod attribute"
         );
      }

      int lastDot = unmarshalMethod.lastIndexOf('.');
      String clsName = unmarshalMethod.substring(0, lastDot);
      String methodName = unmarshalMethod.substring(lastDot + 1);

      Class cls = RtUtil.loadClass(clsName, true);

      try
      {
         return cls.getMethod(methodName, new Class[]{String.class});
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            return cls.getMethod(methodName, new Class[]{String.class, NamespaceContext.class});
         }
         catch(NoSuchMethodException e1)
         {
            throw new JBossXBRuntimeException("Neither " +
               methodName +
               "(" +
               String.class.getName() +
               " p) nor " +
               methodName +
               "(" +
               String.class.getName() +
               " p1, " +
               NamespaceContext.class.getName() +
               " p2) were found in " + cls
            );
         }
      }
   }

   public static Object invokeUnmarshalMethod(Class cls,
                                              String methodName,
                                              Object value,
                                              Class valueType,
                                              NamespaceContext nsCtx,
                                              QName qName)
   {
      Method method;
      Object[] args;
      try
      {
         method = cls.getMethod(methodName, new Class[]{valueType});
         args = new Object[]{value};
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            method = cls.getMethod(methodName, new Class[]{valueType, NamespaceContext.class});
            args = new Object[]{value, nsCtx};
         }
         catch(NoSuchMethodException e1)
         {
            throw new JBossXBRuntimeException("Neither " +
               methodName +
               "(" +
               valueType.getName() +
               " p) nor " +
               methodName +
               "(" +
               valueType.getName() +
               " p1, " +
               NamespaceContext.class.getName() +
               " p2) were found in " + cls
            );
         }
      }

      return invokeUnmarshalMethod(method, args, qName);
   }

   public static Object invokeUnmarshalMethod(Method method, Object[] args, QName qName)
   {
      Object unmarshalled;
      try
      {
         unmarshalled = method.invoke(null, args);
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Failed to invoke unmarshalMethod " +
            method.getDeclaringClass().getName() +
            "." +
            method.getName() +
            " for element " +
            qName +
            " and value " +
            args[0] +
            ": " +
            e.getMessage(),
            e
         );
      }
      return unmarshalled;
   }
}
