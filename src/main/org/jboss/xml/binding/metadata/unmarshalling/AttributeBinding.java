/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.JBossXBRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBinding
{
   private final NamespaceBinding ns;
   private final String attributeName;
   private final String fieldName;
   private final Class javaType;
   private final Field javaField;
   private final Method getter;
   private final Method setter;

   public AttributeBinding(NamespaceBinding ns,
                           String attributeName,
                           Class javaType,
                           String fieldName,
                           Class parentClass)
   {
      this.ns = ns;
      this.attributeName = attributeName;
      this.fieldName = fieldName;

      Method tmpGetter = null;
      Method tmpSetter = null;
      Field tmpField = null;

      if(fieldName != null)
      {
         String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
         try
         {
            tmpGetter = parentClass.getMethod(getterName, null);
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            tmpSetter = parentClass.getMethod(setterName, new Class[]{tmpGetter.getReturnType()});
            tmpField = null;
         }
         catch(NoSuchMethodException e)
         {
            tmpGetter = null;
            tmpSetter = null;

            try
            {
               tmpField = parentClass.getField(fieldName);
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException("Failed to bind attribute " +
                  attributeName +
                  ": neither getter/setter pair nor field were found for field " +
                  fieldName +
                  " in " +
                  parentClass
               );
            }
         }
      }
      else
      {
         throw new JBossXBRuntimeException("XML attribute is not bound to a field in a class. What's the case?!");
      }

      getter = tmpGetter;
      setter = tmpSetter;
      javaField = tmpField;

      this.javaType = javaType == null ? getFieldType() : javaType;
   }

   public Method getGetter()
   {
      return getter;
   }

   public Method getSetter()
   {
      return setter;
   }

   public Field getField()
   {
      return javaField;
   }

   public Class getJavaType()
   {
      return javaType;
   }

   public Class getFieldType()
   {
      return fieldName == null ? null : (getField() == null ? getGetter().getReturnType() : getField().getType());
   }

   public NamespaceBinding getNamespace()
   {
      return ns;
   }

   public String getAttributeName()
   {
      return attributeName;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof AttributeBinding))
      {
         return false;
      }

      final AttributeBinding attributeBinding = (AttributeBinding)o;

      if(!attributeName.equals(attributeBinding.attributeName))
      {
         return false;
      }
      if(!ns.equals(attributeBinding.ns))
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = ns.hashCode();
      result = 29 * result + attributeName.hashCode();
      return result;
   }
}
