/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Immutable;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface FieldSetter
{
   class FieldBasedSetter
      implements FieldSetter
   {
      private final Field field;

      public FieldBasedSetter(Field field)
      {
         this.field = field;
      }

      public Class getFieldType()
      {
         return field.getType();
      }

      public void set(Object owner, Object value, QName name)
      {
         value = RtUtil.cast(value, owner.getClass());
         try
         {
            field.set(owner, value);
         }
         catch(IllegalAccessException e)
         {
            throw new JBossXBRuntimeException("Failed to set field value '" +
               value +
               "' using field " +
               field.getName() +
               " declared in class " +
               field.getDeclaringClass() +
               " on " + owner + ": " + e.getMessage(), e
            );
         }
      }
   }

   class MethodBasedSetter
      implements FieldSetter
   {
      private final Method setter;

      public MethodBasedSetter(Method setter)
      {
         this.setter = setter;
      }

      public Class getFieldType()
      {
         return setter.getParameterTypes()[0];
      }

      public void set(Object owner, Object value, QName name)
      {
         value = RtUtil.cast(value, owner.getClass());
         try
         {
            setter.invoke(owner, new Object[]{value});
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to set field value '" +
               value +
               "' using method " +
               setter.getName() +
               " declared in class " +
               setter.getDeclaringClass() +
               " on " + owner + ": " + e.getMessage(), e
            );
         }
      }
   }

   class ImmutableSetter
      implements FieldSetter
   {
      private final Class fieldType;

      public ImmutableSetter(Class fieldType)
      {
         this.fieldType = fieldType;
      }

      public Class getFieldType()
      {
         return fieldType;
      }

      public void set(Object owner, Object value, QName name)
      {
         ((Immutable)owner).addChild(name.getLocalPart(), value);
      }
   }

   class CollectionItemSetter
      implements FieldSetter
   {

      public Class getFieldType()
      {
         return Object.class;
      }

      public void set(Object owner, Object value, QName name)
      {
         ((Collection)owner).add(value);
      }
   }

   FieldSetter COLLECTION_ITEM = new CollectionItemSetter();

   Class getFieldType();

   void set(Object owner, Object value, QName name);
}
