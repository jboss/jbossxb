/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import org.jboss.logging.Logger;
import org.jboss.util.Classes;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

import javax.xml.namespace.QName;

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
      String mappingKey = cls.getName() + ":" + localName;
      fieldMappings.put(mappingKey, mapping);
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
         children = getJavaValue(localName, o, true);
      }
      return children;
   }

   public Object getElementValue(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      Object value = null;
      if(writeAsValue(o.getClass()))
      {
         value = o;
      }
      else
      {
         value = getJavaValue(localName, o, false);
      }
      return value;
   }

   public Object getAttributeValue(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      Object value = null;
      if(writeAsValue(o.getClass()))
      {
         value = o;
      }
      else
      {
         value = getJavaValue(localName, o, false);
      }
      return value;
   }

   public Object getRoot(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      //String correspCls = Util.xmlNameToClassName(localName, true);
      //String shortName = Classes.stripPackageName(o.getClass());
      //return correspCls.equals(shortName) ? o : null;
      return o;
   }

   // Private

   private Object getJavaValue(String localName, Object o, boolean forComplexType)
   {
      Method getter = null;
      Field field = null;

      String mappingKey = o.getClass().getName() + ":" + localName;
      final FieldToElementMapping mapping = (FieldToElementMapping)fieldMappings.get(mappingKey);
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
      else
      {
         String getterStr = Util.xmlNameToGetMethodName(localName, ignoreLowLine);
         try
         {
            getter = o.getClass().getMethod(getterStr, null);
         }
         catch(NoSuchMethodException e)
         {
            String attr = Util.xmlNameToClassName(localName, ignoreLowLine);
            attr = Character.toLowerCase(attr.charAt(0)) + attr.substring(1);
            try
            {
               field = o.getClass().getField(attr);
            }
            catch(NoSuchFieldException e1)
            {
               if(ignoreNotFoundField)
               {
                  if(log.isTraceEnabled())
                  {
                     log.trace("getChildren: found neither getter nor field for " + localName + " in " + o.getClass());
                  }
               }
               else
               {
                  throw new JBossXBRuntimeException(
                     "getChildren: found neither getter nor field for " + localName + " in " + o.getClass()
                  );
               }
            }
         }
      }

      Object value = null;
      try
      {
         if(getter != null && (!forComplexType || forComplexType && !writeAsValue(getter.getReturnType())))
         {
            value = getter.invoke(o, null);
         }
      }
      catch(Exception e)
      {
         log.error("Cannot invoke getter '" + getter + "' on object: " + o);
         throw new IllegalStateException("Failed to provide value for " + localName + " from " + o, e);
      }
      
      try
      {
         if(field != null && (!forComplexType || forComplexType && !writeAsValue(field.getType())))
         {
            value = field.get(o);
         }
      }
      catch(Exception e)
      {
         log.error("Cannot invoke field '" + field + "' on object: " + o);
         throw new IllegalStateException("Failed to provide value for " + localName + " from " + o, e);
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
         type == java.util.Date.class;
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
         
         if (log.isTraceEnabled())
         {
            log.trace("new ClassToElementMapping: [cls=" + cls.getName() + ",qname=" + new QName(namespaceURI, localName) + "]");
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
      public final Method setter;
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

         if (log.isTraceEnabled())
         {
            log.trace("new FieldToElementMapping: [cls=" + cls.getName() + ",field=" + field + ",qname=" + new QName(namespaceURI, localName) + "]");
         }
         
         Method localGetter = null;
         Method localSetter = null;
         Field localField = null;

         try
         {
            localGetter = Classes.getAttributeGetter(cls, field);
            localSetter = Classes.getAttributeSetter(cls, field, localGetter.getReturnType());
         }
         catch(NoSuchMethodException e)
         {
            try
            {
               localField = cls.getField(field);
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException(
                  "Neither getter/setter pair nor field where found for " + field + " in " + cls
               );
            }
         }

         this.getter = localGetter;
         this.setter = localSetter;
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
         if(fieldName != null ? !fieldName.equals(fieldToElementMapping.fieldName) : fieldToElementMapping.fieldName != null)
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
