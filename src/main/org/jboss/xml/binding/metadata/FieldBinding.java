/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.Immutable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class FieldBinding
   extends ClassBinding
   implements JavaValueBinding
{
   private final String fieldName;

   private final Field field;
   private final Method getter;
   private final Method setter;

   public FieldBinding(Class ownerType, String fieldName, Class newInstanceType)
   {
      super(newInstanceType);

      this.fieldName = fieldName;

      Field field = null;
      Method getter = null;
      Method setter = null;

      String methodNameBase = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
      try
      {
         getter = ownerType.getMethod("get" + methodNameBase, null);

         try
         {
            setter = ownerType.getMethod("set" + methodNameBase, new Class[]{getter.getReturnType()});
         }
         catch(NoSuchMethodException e)
         {
            // immutable...
         }
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            field = ownerType.getField(fieldName);
         }
         catch(NoSuchFieldException endOfWorld)
         {
            throw new IllegalStateException(
               "Throw niether getter/setter pair nor field were found for " + fieldName + " in " + ownerType
            );
         }
      }

      this.field = field;
      this.getter = getter;
      this.setter = setter;
   }

   public Object get(Object owner, String name)
   {
      Object value;
      if(owner instanceof Immutable)
      {
         value = ((Immutable)owner).getChild(name);
      }
      else if(field == null)
      {
         try
         {
            value = getter.invoke(owner, null);
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to get value of " +
               fieldName +
               " in " +
               cls +
               " from " +
               owner +
               " using getter " +
               getter.getName() +
               ": " +
               e.getMessage()
            );
         }
      }
      else
      {
         try
         {
            value = field.get(owner);
         }
         catch(IllegalAccessException e)
         {
            throw new IllegalStateException("Failed to get value of " +
               fieldName +
               " in " +
               cls +
               " from " +
               owner +
               " using field " +
               field.getName() +
               ": " +
               e.getMessage()
            );
         }
      }
      return value;
   }

   public void set(Object owner, Object value, String name)
   {
      if(value instanceof Immutable)
      {
         value = ((Immutable)value).newInstance();
      }

      if(owner instanceof Immutable)
      {
         ((Immutable)owner).addChild(name, value);
      }
      else if(field == null)
      {
         try
         {
            setter.invoke(owner, new Object[]{value});
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to set value of " +
               fieldName +
               " in " +
               cls +
               " to " +
               value +
               " for " +
               owner +
               " using setter " +
               setter.getName() +
               ": " +
               e.getMessage()
            );
         }
      }
      else
      {
         try
         {
            field.set(owner, value);
         }
         catch(IllegalAccessException e)
         {
            throw new IllegalStateException("Failed to set value of " +
               fieldName +
               " in " +
               cls +
               " to " +
               value +
               " for " +
               owner +
               " using field " +
               field.getName() +
               ": " +
               e.getMessage()
            );
         }
      }
   }
}
