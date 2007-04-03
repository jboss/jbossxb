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
package org.jboss.xb.binding.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.jboss.util.Classes;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class FieldInfo
{
   private static final Object[] NO_ARGS = new Object[0];

   private interface GetValueAccess
   {
      Object get(Object owner) throws IllegalAccessException, InvocationTargetException;
   }

   public static class GetValueAccessFactory
   {
      public static GetValueAccess fieldAccess(final Field field)
      {
         return new GetValueAccess()
         {
            public Object get(Object owner) throws IllegalAccessException
            {
               return field.get(owner);
            }
         };
      }

      public static GetValueAccess methodAccess(final Method m)
      {
         return new GetValueAccess()
         {
            public Object get(Object owner) throws IllegalAccessException, InvocationTargetException
            {
               return m.invoke(owner, NO_ARGS);
            }
         };
      }
   }

   private interface SetValueAccess
   {
      void set(Object owner, Object value) throws IllegalAccessException, InvocationTargetException;
   }

   public static class SetValueAccessFactory
   {
      public static SetValueAccess fieldAccess(final Field field)
      {
         return new SetValueAccess()
         {
            public void set(Object owner, Object value) throws IllegalAccessException
            {
               field.set(owner, value);
            }
         };
      }

      public static SetValueAccess methodAccess(final Method m)
      {
         return new SetValueAccess()
         {
            public void set(Object owner, Object value) throws IllegalAccessException, InvocationTargetException
            {
               Object[] arguments = new Object[] { value };
               try
               {
                  m.invoke(owner, new Object[]{value});
               }
               catch (IllegalArgumentException e)
               {
                  if (owner == null)
                     throw new IllegalArgumentException("Null target for " + m.getName());
                  ArrayList expected = new ArrayList();
                  Class[] parameters = m.getParameterTypes();
                  if (parameters != null)
                  {
                     for (int i = 0; i < parameters.length; ++i)
                        expected.add(parameters[i].getName());
                  }
                  ArrayList actual = new ArrayList();
                  if (arguments != null)
                  {
                     for (int i = 0; i < arguments.length; ++i)
                     {
                        if (arguments[i] == null)
                           actual.add(null);
                        else
                           actual.add(arguments[i].getClass().getName());
                     }
                  }
                  throw new IllegalArgumentException("Wrong arguments. " + m.getName() + " for target " + owner + " expected=" + expected + " actual=" + actual);
               }
            }
         };
      }
   }

   static FieldInfo getFieldInfo(ClassInfo clsInfo, String name)
   {
      FieldInfo fieldInfo = null;
      try
      {
         Method getter = Classes.getAttributeGetter(clsInfo.getType(), name);
         fieldInfo = new FieldInfo(clsInfo.getType(), name, getter);
         clsInfo.addFieldInfo(fieldInfo);
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            Field field = clsInfo.getType().getField(name);
            fieldInfo = new FieldInfo(clsInfo.getType(), field);
            clsInfo.addFieldInfo(fieldInfo);
         }
         catch(NoSuchFieldException e1)
         {
            fieldInfo = clsInfo.introspect(name);
         }
      }
      return fieldInfo;
   }

   public static FieldInfo getFieldInfo(Class cls, String fieldName, boolean required)
   {
      return ClassInfos.getClassInfo(cls).getFieldInfo(fieldName, required);
   }

   private final Class owner;
   private final String name;
   private final Class type;
   private final GetValueAccess getter;
   private SetValueAccess setter;
   private boolean setterInitialized;

   public FieldInfo(Class owner, String name, Method getter)
   {
      this.owner = owner;
      this.name = name;
      this.type = getter.getReturnType();
      this.getter = GetValueAccessFactory.methodAccess(getter);
   }

   public FieldInfo(Class owner, String name, Method getter, Method setter)
   {
      this.owner = owner;
      this.name = name;
      this.type = getter.getReturnType();
      this.getter = GetValueAccessFactory.methodAccess(getter);
      this.setter = SetValueAccessFactory.methodAccess(setter);
      setterInitialized = true;
   }

   public FieldInfo(Class owner, Field field)
   {
      this.owner = owner;
      this.name = field.getName();
      this.type = field.getType();
      this.getter = GetValueAccessFactory.fieldAccess(field);
      this.setter = SetValueAccessFactory.fieldAccess(field);
      setterInitialized = true;
   }

   public Class getOwner()
   {
      return owner;
   }

   public String getName()
   {
      return name;
   }

   public Class getType()
   {
      return type;
   }

   public boolean isReadable()
   {
      return true;
   }

   public boolean isWritable()
   {
      if(!setterInitialized)
      {
         initializeSetter();
      }
      return setter != null;
   }

   public Object getValue(Object owner)
   {
      try
      {
         return getter.get(owner);
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException(
            "Failed to get value of the property '" + name + "' defined in " + owner + " from instance " + owner, e
         );
      }
   }

   public void setValue(Object owner, Object value)
   {
      if(!isWritable())
      {
         throw new JBossXBRuntimeException(
            "Failed to find setter or field for property '" + name + "' in " + owner
         );
      }

      try
      {
         setter.set(owner, value);
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException(
            "Failed to set value '" + value + "' for property '" + name + "' defined in " +
            owner.getClass().getName() + " on instance " + owner, e
         );
      }
   }

   private void initializeSetter()
   {
      try
      {
         setter = SetValueAccessFactory.methodAccess(Classes.getAttributeSetter(owner, name, type));
      }
      catch(NoSuchMethodException e)
      {
      }
      setterInitialized = true;
   }
}
