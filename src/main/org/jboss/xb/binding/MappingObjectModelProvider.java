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
package org.jboss.xb.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MappingObjectModelProvider
   implements GenericObjectModelProvider
{
   private static final Logger log = Logger.getLogger(MappingObjectModelProvider.class);

   private final Map classMappings = new HashMap();
   private final Map fieldMappings = new HashMap();
   private boolean ignoreLowLine = true;
   private boolean ignoreNotFoundField = true;

   public boolean isIgnoreNotFoundField()
   {
      return ignoreNotFoundField;
   }

   public void setIgnoreNotFoundField(boolean ignoreNotFoundField)
   {
      this.ignoreNotFoundField = ignoreNotFoundField;
   }

   public void mapClassToElement(Class cls, String namespaceURI, String localName, ObjectModelProvider provider)
   {
      ClassToElementMapping mapping = new ClassToElementMapping(cls, namespaceURI, localName,
         provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider : new DelegatingObjectModelProvider(provider)
      );
      classMappings.put(mapping.cls, mapping);
   }

   public void mapFieldToElement(Class cls,
                                 String field,
                                 String namespaceURI,
                                 String localName,
                                 TypeBinding converter)
   {
      FieldToElementMapping mapping = new FieldToElementMapping(cls, field, namespaceURI, localName, converter);
      mapFieldToElement(mapping);
   }

   public boolean isIgnoreLowLine()
   {
      return ignoreLowLine;
   }

   public void setIgnoreLowLine(boolean ignoreLowLine)
   {
      this.ignoreLowLine = ignoreLowLine;
   }

   // GenericObjectModelProvider implementation

   public Object getChildren(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      Object children = null;
      if(!writeAsValue(o.getClass()))
      {
         children = getJavaValue(namespaceURI, localName, null, o, true, ignoreNotFoundField);
      }
      return children;
   }

   public Object getElementValue(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      Object value;
      if(writeAsValue(o.getClass()))
      {
         value = o;
      }
      else
      {
         String fieldName = null;
         if(ctx != null && ctx.isTypeComplex())
         {
            // this is how it should be
            fieldName = ctx.getSimpleContentProperty();
         }

         // this is a hack for soap enc
         try
         {
            value = getJavaValue(namespaceURI, localName, fieldName, o, false, false);
         }
         catch(JBossXBRuntimeException e)
         {
            value = getJavaValue(namespaceURI, localName, null, o, false, ignoreNotFoundField);
         }
      }
      return value;
   }

   public Object getAttributeValue(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      boolean optional = ctx == null ? ignoreNotFoundField : !ctx.isAttributeRequired() || ignoreNotFoundField;
      return getJavaValue(namespaceURI, localName, null, o, false, optional);
   }

   public Object getRoot(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      //String correspCls = Util.xmlNameToClassName(localName, true);
      //String shortName = Classes.stripPackageName(o.getClass());
      //return correspCls.equals(shortName) ? o : null;
      return o;
   }

   // Private

   private void mapFieldToElement(FieldToElementMapping mapping)
   {
      String mappingKey = mapping.cls.getName() + ":" + mapping.localName;
      fieldMappings.put(mappingKey, mapping);
   }

   private Object getJavaValue(String namespaceURI, String localName, String fieldName, Object o, boolean forComplexType, boolean optional)
   {
      Method getter = null;
      Field field = null;

      String mappingKey = o.getClass().getName() + ":" + localName;
      FieldToElementMapping mapping = (FieldToElementMapping)fieldMappings.get(mappingKey);
      if(mapping == null)
      {
         if(fieldName == null)
         {
            fieldName = Util.xmlNameToFieldName(localName, ignoreLowLine);
         }

         // cache the fieldmapping
         try
         {
            mapping = new FieldToElementMapping(o.getClass(), fieldName, namespaceURI, localName, null);
            mapFieldToElement(mapping);
         }
         catch(JBossXBRuntimeException e)
         {
            if(optional)
            {
               if(log.isTraceEnabled())
               {
                  log.trace(e.getMessage());
               }
            }
            else
            {
               throw e;
            }
         }
      }

      if(mapping != null)
      {
         if(mapping.getter != null)
         {
            getter = mapping.getter;
         }
         else
         {
            field = mapping.field;
         }
      }

      Object value = null;
      if(getter != null && (!forComplexType || forComplexType && !writeAsValue(getter.getReturnType())))
      {
         try
         {
            value = getter.invoke(o, null);
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException(
               "Failed to provide value for element " + localName + " bound to field " + fieldName + " in " + o, e
            );
         }
      }
      else if(field != null && (!forComplexType || forComplexType && !writeAsValue(field.getType())))
      {
         try
         {
            value = field.get(o);
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException(
               "Failed to provide value for element " + localName + " bound to field " + fieldName + " in " + o, e
            );
         }
      }

      if(value != null && mapping != null && mapping.converter != null)
      {
         value = mapping.converter.marshal(value);
      }

      return value;
   }

   private boolean writeAsValue(final Class type)
   {
      return Classes.isPrimitive(type) ||
         type == String.class ||
         type == java.util.Date.class ||
         type == java.math.BigDecimal.class ||
         type == java.math.BigInteger.class;
   }

   // Inner

   private class ClassToElementMapping
   {
      public final Class cls;
      public final String namespaceURI;
      public final String localName;
      public final GenericObjectModelProvider provider;

      public ClassToElementMapping(Class cls,
                                   String namespaceURI,
                                   String localName,
                                   GenericObjectModelProvider provider)
      {
         this.cls = cls;
         this.namespaceURI = namespaceURI;
         this.localName = localName;
         this.provider = provider;

         if(log.isTraceEnabled())
         {
            log.trace("new ClassToElementMapping: [cls=" +
               cls.getName() +
               ",qname=" +
               new QName(namespaceURI, localName) +
               "]"
            );
         }
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof ClassToElementMapping))
         {
            return false;
         }

         final ClassToElementMapping classToElementMapping = (ClassToElementMapping)o;

         if(cls != null ? !cls.equals(classToElementMapping.cls) : classToElementMapping.cls != null)
         {
            return false;
         }
         if(localName != null ?
            !localName.equals(classToElementMapping.localName) :
            classToElementMapping.localName != null)
         {
            return false;
         }
         if(namespaceURI != null ?
            !namespaceURI.equals(classToElementMapping.namespaceURI) :
            classToElementMapping.namespaceURI != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (cls != null ? cls.hashCode() : 0);
         result = 29 * result + (namespaceURI != null ? namespaceURI.hashCode() : 0);
         result = 29 * result + (localName != null ? localName.hashCode() : 0);
         return result;
      }
   }

   private class FieldToElementMapping
   {
      public final Class cls;
      public final String fieldName;
      public final String namespaceURI;
      public final String localName;
      public final TypeBinding converter;
      public final Method getter;
      public final Field field;

      public FieldToElementMapping(Class cls,
                                   String field,
                                   String namespaceURI,
                                   String localName,
                                   TypeBinding converter)
      {
         this.cls = cls;
         this.fieldName = field;
         this.namespaceURI = namespaceURI;
         this.localName = localName;
         this.converter = converter;

         if(log.isTraceEnabled())
         {
            log.trace("new FieldToElementMapping: [cls=" +
               cls.getName() +
               ",field=" +
               field +
               ",qname=" +
               new QName(namespaceURI, localName) +
               "]"
            );
         }

         Method localGetter = null;
         Field localField = null;

         try
         {
            localGetter = Classes.getAttributeGetter(cls, field);
         }
         catch(NoSuchMethodException e)
         {
            try
            {
               localField = cls.getField(field);
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException("Neither getter nor field where found for " + field + " in " + cls);
            }
         }

         this.getter = localGetter;
         this.field = localField;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof FieldToElementMapping))
         {
            return false;
         }

         final FieldToElementMapping fieldToElementMapping = (FieldToElementMapping)o;

         if(cls != null ? !cls.equals(fieldToElementMapping.cls) : fieldToElementMapping.cls != null)
         {
            return false;
         }
         if(fieldName != null ?
            !fieldName.equals(fieldToElementMapping.fieldName) :
            fieldToElementMapping.fieldName != null)
         {
            return false;
         }
         if(localName != null ?
            !localName.equals(fieldToElementMapping.localName) :
            fieldToElementMapping.localName != null)
         {
            return false;
         }
         if(namespaceURI != null ?
            !namespaceURI.equals(fieldToElementMapping.namespaceURI) :
            fieldToElementMapping.namespaceURI != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (cls != null ? cls.hashCode() : 0);
         result = 29 * result + (fieldName != null ? fieldName.hashCode() : 0);
         result = 29 * result + (namespaceURI != null ? namespaceURI.hashCode() : 0);
         result = 29 * result + (localName != null ? localName.hashCode() : 0);
         return result;
      }
   }
}
