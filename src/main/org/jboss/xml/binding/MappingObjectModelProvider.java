/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;
import org.jboss.util.Classes;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

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
      fieldMappings.put(mapping.localName, mapping);
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

      final FieldToElementMapping mapping = (FieldToElementMapping)fieldMappings.get(localName);
      if(mapping != null)
      {
         getter = mapping.getter;
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
               if(log.isDebugEnabled())
               {
                  log.debug("getChildren: found neither getter nor field for " + localName + " in " + o.getClass());
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
         else if(field != null && (!forComplexType || forComplexType && !writeAsValue(field.getType())))
         {
            value = field.get(o);
         }
      }
      catch(Exception e)
      {
         log.error("Failed to provide value for " + localName + " from " + o, e);
      }

      if(value != null && mapping != null)
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
      public final String field;
      public final String namespaceURI;
      public final String localName;
      public final TypeBinding converter;
      public final Method getter;
      public final Method setter;

      public FieldToElementMapping(Class cls,
                                   String field,
                                   String namespaceURI,
                                   String localName,
                                   TypeBinding converter)
      {
         this.cls = cls;
         this.field = field;
         this.namespaceURI = namespaceURI;
         this.localName = localName;
         this.converter = converter;

         try
         {
            getter = Classes.getAttributeGetter(cls, field);
         }
         catch(NoSuchMethodException e)
         {
            throw new IllegalStateException("Getter not found for " + field + " in class " + cls.getName());
         }

         try
         {
            setter = Classes.getAttributeSetter(cls, field, getter.getReturnType());
         }
         catch(NoSuchMethodException e)
         {
            throw new IllegalStateException("Setter not found for " + field + " in class " + cls.getName());
         }
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
         if(field != null ? !field.equals(fieldToElementMapping.field) : fieldToElementMapping.field != null)
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
         result = 29 * result + (field != null ? field.hashCode() : 0);
         result = 29 * result + (namespaceURI != null ? namespaceURI.hashCode() : 0);
         result = 29 * result + (localName != null ? localName.hashCode() : 0);
         return result;
      }
   }
}
