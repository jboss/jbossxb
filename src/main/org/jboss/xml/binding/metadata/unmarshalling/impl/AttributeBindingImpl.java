/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.JBossXBRuntimeException;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBindingImpl
   implements AttributeBinding
{
   private final QName attributeName;
   private final Class javaType;
   private final Field field;
   private final Method getter;
   private final Method setter;
   private final Class fieldType;

   public AttributeBindingImpl(QName attributeName, Class javaType, Class parentClass, String fieldName)
   {
      this.attributeName = attributeName;

      Field field = null;
      Method getter = null;
      Method setter = null;

      if(fieldName != null)
      {
         try
         {
            field = parentClass.getField(fieldName);
         }
         catch(NoSuchFieldException e)
         {
            String baseMethodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try
            {
               getter = parentClass.getMethod("get" + baseMethodName, null);
               setter = parentClass.getMethod("set" + baseMethodName, new Class[]{getter.getReturnType()});
            }
            catch(NoSuchMethodException e1)
            {
               throw new JBossXBRuntimeException("Failed to bind attribute " +
                  attributeName +
                  ": neither field nor getter/setter were found for field " +
                  fieldName +
                  " in " +
                  parentClass
               );
            }
         }
      }

      this.field = field;
      this.getter = getter;
      this.setter = setter;

      fieldType = field == null ? (getter == null ? null : getter.getReturnType()) : field.getType();
      if(fieldType == null)
      {
         throw new JBossXBRuntimeException("Failed to bind attribute " +
            attributeName +
            " to field " +
            fieldName +
            " in " +
            parentClass +
            ": failed to resolve field's type."
         );
      }

      this.javaType = javaType == null ? fieldType : javaType;
   }

   public QName getAttributeName()
   {
      return attributeName;
   }

   public Class getJavaType()
   {
      return javaType;
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
