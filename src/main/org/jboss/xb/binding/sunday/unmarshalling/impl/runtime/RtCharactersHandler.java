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

import java.lang.reflect.Method;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.util.Classes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtCharactersHandler
   extends CharactersHandler
{
   public static final RtCharactersHandler INSTANCE = new RtCharactersHandler();

   public Object unmarshal(QName qName,
                           TypeBinding typeBinding,
                           NamespaceContext nsCtx,
                           ValueMetaData valueMetaData,
                           String value)
   {
      Object unmarshalled = null;
      if(valueMetaData != null)
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

         Class cls = loadClass(clsName, true);
         unmarshalled = invokeUnmarshalMethod(cls, methodName, value, String.class, nsCtx, qName);
      }
      else
      {
         unmarshalled = super.unmarshal(qName, typeBinding, nsCtx, valueMetaData, value);

         if(typeBinding.isSimple())
         {
            String clsName = null;
            boolean failIfNotFound = false;
            if(typeBinding.getClassMetaData() != null)
            {
               clsName = typeBinding.getClassMetaData().getImpl();
               failIfNotFound = true;
            }
            else
            {
               QName typeName = typeBinding.getQName();
               if(typeName != null && !Constants.NS_XML_SCHEMA.equals(typeName.getNamespaceURI()))
               {
                  boolean ignoreLowLine = typeBinding.getSchemaBinding() != null ?
                     typeBinding.getSchemaBinding().isIgnoreLowLine() :
                     true;
                  clsName =
                     Util.xmlNameToClassName(typeName.getNamespaceURI(), typeName.getLocalPart(), ignoreLowLine);
               }
            }

            Class cls = clsName == null ? null : loadClass(clsName, failIfNotFound);
            if(cls != null && !cls.isPrimitive())
            {
               // I assume if it doesn't have ctors, there should be static fromValue
               // method like it is defined for enum types in JAXB2.0
               // for java5 cls.isEnum() should be used instead
               if(cls.getConstructors().length == 0)
               {
                  Class valueType = unmarshalled.getClass();
                  // todo: this should be used in combination element.isNillable...
                  if(Classes.isPrimitiveWrapper(valueType))
                  {
                     valueType = Classes.getPrimitive(valueType);
                  }

                  // it should probably invoke fromValue even if unmarshalled is null
                  unmarshalled = unmarshalled == null ? null :
                     invokeUnmarshalMethod(cls, "fromValue", unmarshalled, valueType, nsCtx, qName);
               }
               else
               {
                  throw new JBossXBRuntimeException("This case is not yet supported (create a feature request): " +
                     "simple type (" +
                     typeBinding.getQName() +
                     ") is bound to a class (" +
                     cls +
                     ") with optional property metadata with " +
                     "default value for the property name 'value'."
                  );
               }
            }
         }
      }

      return unmarshalled;
   }

   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
      //todo: assert if type is not null it must simple...

      if(owner != null) // todo: owner should never be null
      {
         if(owner instanceof MapEntry)
         {
            TypeBinding type = element.getType();
            CharactersMetaData characters = type.getCharactersMetaData();
            if(characters != null)
            {
               if(characters.isMapEntryKey())
               {
                  ((MapEntry)owner).setKey(value);
               }
               else if(characters.isMapEntryValue())
               {
                  ((MapEntry)owner).setValue(value);
               }
               else
               {
                  throw new JBossXBRuntimeException("Parent object is a map entry but characters of element " +
                     qName +
                     " of type " +
                     type.getQName() +
                     " were bound to niether key nor value in a map entry."
                  );
               }
            }
            else
            {
               throw new JBossXBRuntimeException("Parent object is a map entry but characters of element " +
                  qName +
                  " of type " +
                  type.getQName() +
                  " were bound to niether key nor value in a map entry."
               );
            }
         }
         else
         {
            String propName = null;
            String colType = null;
            TypeBinding type = element.getType();
            if(type != null && !type.isSimple()/* && type.hasSimpleContent()*/)
            {
               PropertyMetaData propertyMetaData = type.getPropertyMetaData();
               if(propertyMetaData == null)
               {
                  CharactersMetaData charactersMetaData = type.getCharactersMetaData();
                  propertyMetaData = charactersMetaData == null ? null : charactersMetaData.getProperty();
               }

               if(propertyMetaData != null)
               {
                  propName = propertyMetaData.getName();
                  colType = propertyMetaData.getCollectionType();
               }

               if(propName == null)
               {
                  propName = "value";
               }
            }
            else
            {
               PropertyMetaData PropertyMetaData = element.getPropertyMetaData();
               if(PropertyMetaData != null)
               {
                  propName = PropertyMetaData.getName();
                  colType = PropertyMetaData.getCollectionType();
               }

               if(propName == null)
               {
                  propName = Util.xmlNameToFieldName(qName.getLocalPart(), element.getSchema().isIgnoreLowLine());
               }
            }

            RtUtil.set(owner, value, propName, colType,
               element.getSchema().isIgnoreUnresolvedFieldOrClass(),
               element.getValueAdapter()
            );
         }
      }
   }

   private Object invokeUnmarshalMethod(Class cls,
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
            value +
            ": " +
            e.getMessage(),
            e
         );
      }
      return unmarshalled;
   }

   private Class loadClass(String clsName, boolean failIfNotFound)
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
}
