/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBindingImpl
   extends AbstractElementBinding
{
   private final Field field;
   private final Method getter;
   private final Method setter;
   private final Class fieldType;
   private final Class javaType;

   public ElementBindingImpl(AbstractElementBinding element)
   {
      super(element.getElementName(), element.getParent());
      field = element.getField();
      getter = element.getGetter();
      setter = element.getSetter();
      fieldType = element.getFieldType();
      javaType = element.getJavaType();
   }

   public ElementBindingImpl(QName elementName,
                             Class javaType,
                             Class parentClass,
                             String fieldName,
                             DelegatingBasicElementBinding parent)
   {
      super(elementName, parent);
      Field field1 = null;
      Method getter1 = null;
      Method setter1 = null;

      if(fieldName != null)
      {
         try
         {
            field1 = parentClass.getField(fieldName);
         }
         catch(NoSuchFieldException e)
         {
            String baseMethodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try
            {
               getter1 = parentClass.getMethod("get" + baseMethodName, null);
               setter1 = parentClass.getMethod("set" + baseMethodName, new Class[]{getter1.getReturnType()});
            }
            catch(NoSuchMethodException e1)
            {
               throw new JBossXBRuntimeException("Failed to bind element " +
                  elementName +
                  ": neither field nor getter/setter were found for field " +
                  fieldName +
                  " in " +
                  parentClass
               );
            }
         }
      }

      field = field1;
      getter = getter1;
      setter = setter1;

      fieldType = field1 == null ? (getter1 == null ? null : getter1.getReturnType()) : field1.getType();

      this.javaType = (javaType == null ? fieldType : javaType);
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

   public Class getJavaType()
   {
      return javaType;
   }

   public ElementBinding getElement(QName elementName)
   {
      return null;
   }
}
