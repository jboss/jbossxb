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

   public void mapClassToElement(Class cls, String namespaceURI, String localName, ObjectModelProvider provider)
   {
      ClassToElementMapping mapping = new ClassToElementMapping(cls, namespaceURI, localName,
         provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider) provider : new DelegatingObjectModelProvider(provider));
      classMappings.put(mapping.cls, mapping);
   }

   public void mapFieldToElement(Class cls,
                                 String field,
                                 String namespaceURI,
                                 String localName,
                                 TypeConverter converter)
   {
      FieldToElementMapping mapping = new FieldToElementMapping(cls, field, namespaceURI, localName, converter);
      fieldMappings.put(mapping.localName, mapping);
   }

   // GenericObjectModelProvider implementation

   public Object getChildren(Object o, String namespaceURI, String localName)
   {
      Object children = null;
      final Class cls = o.getClass();
      if(!writeAsValue(cls))
      {
         try
         {
            Method getter;

            final FieldToElementMapping mapping = (FieldToElementMapping) fieldMappings.get(localName);
            if(mapping != null)
            {
               getter = mapping.getter;
            }
            else
            {
               getter = Classes.getAttributeGetter(cls, localName);
            }

            if(!writeAsValue(getter.getReturnType()))
            {
               children = getter.invoke(o, null);
            }
         }
         catch(NoSuchMethodException e)
         {
            if(log.isDebugEnabled())
            {
               log.debug("getChildren: no getter for " + localName + " in " + cls);
            }
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke getter for field " + localName + " in " + cls);
         }
      }
      return children;
   }

   public Object getElementValue(Object o, String namespaceURI, String localName)
   {
      Object value = null;
      if(writeAsValue(o.getClass()))
      {
         value = o;
      }
      else
      {
         final Class cls = o.getClass();

         try
         {
            Method getter;

            final FieldToElementMapping mapping = (FieldToElementMapping) fieldMappings.get(localName);
            if(mapping != null)
            {
               getter = mapping.getter;
            }
            else
            {
               getter = Classes.getAttributeGetter(cls, localName);
            }

            value = getter.invoke(o, null);

            if(mapping != null)
            {
               value = mapping.converter.marshal(value);
            }
         }
         catch(NoSuchMethodException e)
         {
            if(log.isDebugEnabled())
            {
               log.debug("getElementValue: no getter for " + localName + " in " + cls);
            }
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke getter for field " + localName + " in " + cls);
         }
      }
      return value;
   }

   public Object getAttributeValue(Object o, String namespaceURI, String localName)
   {
      Object value = null;
      if(writeAsValue(o.getClass()))
      {
         value = o;
      }
      else
      {
         final Class cls = o.getClass();
         try
         {
            Method getter;

            final FieldToElementMapping mapping = (FieldToElementMapping) fieldMappings.get(localName);
            if(mapping != null)
            {
               getter = mapping.getter;
            }
            else
            {
               getter = Classes.getAttributeGetter(cls, localName);
            }

            value = getter.invoke(o, null);

            if(mapping != null)
            {
               value = mapping.converter.marshal(value);
            }
         }
         catch(NoSuchMethodException e)
         {
            if(log.isDebugEnabled())
            {
               log.debug("getElementValue: no getter for " + localName + " in " + cls);
            }
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke getter for field " + localName + " in " + cls);
         }
      }
      return value;
   }

   public Object getRoot(Object o, String namespaceURI, String localName)
   {
      return o;
   }

   // Private

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
         if(this == o) return true;
         if(!(o instanceof ClassToElementMapping)) return false;

         final ClassToElementMapping classToElementMapping = (ClassToElementMapping) o;

         if(cls != null ? !cls.equals(classToElementMapping.cls) : classToElementMapping.cls != null) return false;
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
      public final TypeConverter converter;
      public final Method getter;
      public final Method setter;

      public FieldToElementMapping(Class cls,
                                   String field,
                                   String namespaceURI,
                                   String localName,
                                   TypeConverter converter)
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
         if(this == o) return true;
         if(!(o instanceof FieldToElementMapping)) return false;

         final FieldToElementMapping fieldToElementMapping = (FieldToElementMapping) o;

         if(cls != null ? !cls.equals(fieldToElementMapping.cls) : fieldToElementMapping.cls != null) return false;
         if(field != null ? !field.equals(fieldToElementMapping.field) : fieldToElementMapping.field != null) return false;
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
