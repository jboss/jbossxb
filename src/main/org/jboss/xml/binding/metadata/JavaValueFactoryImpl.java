/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.JBossXBRuntimeException;

import java.util.Collection;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class JavaValueFactoryImpl
   extends JavaValueFactory
{
   public JavaFieldValue newJavaFieldValue(Class javaType)
   {
      JavaFieldValue value;
      if(Collection.class.isAssignableFrom(javaType))
      {
         value = new CollectionValueImpl(javaType);
      }
      else if(javaType.isArray())
      {
         value = new ArrayValueImpl(javaType);
      }
      else if(Map.class.isAssignableFrom(javaType))
      {
         value = new MapValueImpl(javaType);
      }
      else
      {
         value = new JavaFieldValueImpl(javaType);
      }
      return value;
   }

   public MapEntryValue newMapEntryValue(MapValue mapValue, Class entryType)
   {
      return new MapEntryValueImpl(mapValue, entryType);
   }

   static class JavaValueImpl
      implements JavaValue
   {
      protected final Class type;
      protected JavaValue value;

      public JavaValueImpl(Class type)
      {
         this.type = type;
      }

      public Class getType()
      {
         return type;
      }

      public JavaValue getJavaValue()
      {
         return value;
      }
   }

   static class JavaFieldValueImpl
      extends JavaValueImpl
      implements JavaFieldValue
   {
      private String fieldName;

      private boolean key;

      private Field field;
      private Method getter;
      private Method setter;
      private Class fieldType;

      public JavaFieldValueImpl(Class type)
      {
         super(type);
      }

      public void bindAsField(JavaValue value, String fieldName)
      {
         this.value = value;
         this.fieldName = fieldName;

         Class ownerType = value.getType();
         try
         {
            field = ownerType.getField(fieldName);
            fieldType = field.getType();
         }
         catch(NoSuchFieldException e)
         {
            String methodBase = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try
            {
               getter = ownerType.getMethod("get" + methodBase, null);
               fieldType = getter.getReturnType();
               try
               {
                  setter = ownerType.getMethod("set" + methodBase, new Class[]{getter.getReturnType()});
               }
               catch(NoSuchMethodException e2)
               {
                  // must be immutable...
               }
            }
            catch(NoSuchMethodException e1)
            {
               throw new JBossXBRuntimeException(
                  "Failed to bind value as field " +
                  fieldName +
                  " in " +
                  ownerType +
                  ": neither field nor getter/setter were found."
               );
            }
         }
      }

      public void bindAsCollectionItem(CollectionValue value)
      {
         this.value = value;
      }

      public void bindAsArrayItem(ArrayValue value)
      {
         this.value = value;
      }

      public MapEntryValue bindAsMapEntry(MapValue value)
      {
         this.value = value;
         return getInstance().newMapEntryValue(value, type);
      }

      public void bindAsMapKey(MapEntryValue value)
      {
         key = true;
         this.value = value;
      }

      public void bindAsMapValue(MapEntryValue value)
      {
         key = false;
         this.value = value;
      }

      public Field getField()
      {
         return field;
      }

      public Method getGetter()
      {
         return getter;
      }

      public Method getSetter()
      {
         return setter;
      }

      public Class getFieldType()
      {
         return fieldType;
      }
   }

   static class ArrayValueImpl
      extends JavaFieldValueImpl
      implements ArrayValue
   {
      public ArrayValueImpl(Class type)
      {
         super(type);
      }
   }

   static class CollectionValueImpl
      extends JavaFieldValueImpl
      implements CollectionValue
   {
      public CollectionValueImpl(Class type)
      {
         super(type);
      }
   }

   static class MapValueImpl
      extends JavaFieldValueImpl
      implements MapValue
   {
      public MapValueImpl(Class type)
      {
         super(type);
      }
   }

   static class MapEntryValueImpl
      extends JavaValueImpl
      implements MapEntryValue
   {
      private final MapValue mapValue;

      public MapEntryValueImpl(MapValue mapValue, Class entryType)
      {
         super(entryType);
         this.mapValue = mapValue;
      }
   }
}
