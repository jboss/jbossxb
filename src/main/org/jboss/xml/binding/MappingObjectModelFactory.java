/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.xml.sax.Attributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MappingObjectModelFactory
   implements GenericObjectModelFactory
{
   private final static Logger log = Logger.getLogger(MappingObjectModelFactory.class);

   private final Map elementToClassMapping = new HashMap();
   private final Map elementToFieldMapping = new HashMap();

   public void mapElementToClass(String element, Class cls)
   {
      ElementToClassMapping mapping = new ElementToClassMapping(element, cls);
      addElementToClassMapping(mapping);
   }

   public void mapElementToField(String element, Class cls, String field, TypeConverter converter)
   {
      ElementToFieldMapping mapping = new ElementToFieldMapping(element, cls, field, converter);
      addElementToFieldMapping(mapping);
   }

   // GenericObjectModelFactory implementation

   public Object newRoot(Object root,
                         ContentNavigator navigator,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      if(root == null)
      {
         ElementToClassMapping mapping = (ElementToClassMapping)elementToClassMapping.get(localName);
         try
         {
            root = mapping.cls.newInstance();
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to instantiate root element: " + e.getMessage());
         }
      }

      if(attrs != null)
      {
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            try
            {
               setAttribute(root, attrs.getLocalName(i), attrs.getValue(i));
            }
            catch(Exception e)
            {
               throw new IllegalStateException("Failed to set attributes: " + e.getMessage());
            }
         }
      }

      return root;
   }

   public Object newChild(Object o,
                          ContentNavigator navigator,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      Object child = null;

      ElementToClassMapping mapping = (ElementToClassMapping)elementToClassMapping.get(localName);
      if(mapping != null)
      {
         try
         {
            if(!(o instanceof Collection))
            {
               Method getter;
               ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(localName);
               if(fieldMapping != null)
               {
                  getter = fieldMapping.getter;
               }
               else
               {
                  String getterStr = Util.xmlNameToGetMethodName(localName, true);
                  getter = o.getClass().getMethod(getterStr, null);
               }
               child = getter.invoke(o, null);
            }

            if(child == null)
            {
               child = mapping.cls.newInstance();
            }

            if(attrs != null)
            {
               for(int i = 0; i < attrs.getLength(); ++i)
               {
                  setAttribute(child, attrs.getLocalName(i), attrs.getValue(i));
               }
            }
         }
         catch(Exception e)
         {
            throw new IllegalStateException(
               "newChild failed for o=" + o + ", uri=" + namespaceURI + ", local=" + localName + ", attrs=" + attrs
            );
         }
      }
      else
      {
         if(o instanceof Collection)
         {
            String clsName = Util.xmlNameToClassName(namespaceURI, localName, true);

            Class childType = null;
            try
            {
               childType = Thread.currentThread().getContextClassLoader().loadClass(clsName);
            }
            catch(ClassNotFoundException e)
            {
               log.debug("newChild: no mapping for " +
                  localName +
                  " and failed to load class " +
                  clsName +
                  " -> child must be a standard Java immutable type."
               );
            }

            if(childType != null)
            {
               try
               {
                  child = childType.newInstance();
               }
               catch(Exception e)
               {
                  throw new IllegalStateException("newChild failed for o=" +
                     o +
                     ", uri=" +
                     namespaceURI +
                     ", local=" +
                     localName +
                     ", attrs=" +
                     attrs +
                     ": failed to create child instance of type " + childType.getName() + ": " + e.getMessage()
                  );
               }
            }
         }
         else
         {
            String getterStr = Util.xmlNameToGetMethodName(localName, true);
            Method getter;
            try
            {
               getter = o.getClass().getMethod(getterStr, null);
            }
            catch(NoSuchMethodException e)
            {
               throw new IllegalStateException("newChild failed for o=" +
                  o +
                  ", uri=" +
                  namespaceURI +
                  ", local=" +
                  localName +
                  ", attrs=" +
                  attrs +
                  ": no getter"
               );
            }

            Class childType = getter.getReturnType();
            if(!Util.isAttributeType(childType))
            {
               if(Collection.class.isAssignableFrom(childType))
               {
                  try
                  {
                     child = getter.invoke(o, null);
                  }
                  catch(Exception e)
                  {
                     throw new IllegalStateException(
                        "Failed to invoke getter " + getter.getName() + ": " + e.getMessage()
                     );
                  }

                  if(child == null)
                  {
                     child = new ArrayList();
                  }
               }
               else
               {
                  try
                  {
                     child = childType.newInstance();
                  }
                  catch(Exception e)
                  {
                     throw new IllegalStateException("newChild failed for o=" +
                        o +
                        ", uri=" +
                        namespaceURI +
                        ", local=" +
                        localName +
                        ", attrs=" +
                        attrs +
                        ": failed to create child instance of type " + childType.getName() + ": " + e.getMessage()
                     );
                  }
               }
            }
         }
      }

      if(child != null)
      {
         // optimize this
         setChild(child, o, localName);
      }

      return child;
   }

   public void addChild(Object parent,
                        Object child,
                        ContentNavigator navigator,
                        String namespaceURI,
                        String localName)
   {
      //setChild(child, parent, localName);
   }

   public void setValue(Object o,
                        ContentNavigator navigator,
                        String namespaceURI,
                        String localName,
                        String value)
   {
      setAttribute(o, localName, value);
   }

   // Private

   private void addElementToClassMapping(ElementToClassMapping mapping)
   {
      elementToClassMapping.put(mapping.element, mapping);
   }

   private void addElementToFieldMapping(ElementToFieldMapping mapping)
   {
      elementToFieldMapping.put(mapping.element, mapping);
   }

   private void setChild(Object child, Object parent, String localName)
   {
      Object value = child;
      if(parent instanceof Collection)
      {
         ((Collection)parent).add(value);
      }
      else
      {
         Method setter = null;
         final ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(localName);
         if(fieldMapping != null)
         {
            setter = fieldMapping.setter;
         }
         else
         {
            try
            {
               final String xmlToCls = Util.xmlNameToClassName(localName, true);
               Method getter = parent.getClass().getMethod("get" + xmlToCls, null);
               setter = parent.getClass().getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
            }
            catch(NoSuchMethodException e)
            {
               log.warn("no setter found for " + localName + " in " + parent);
            }
         }

         if(setter != null)
         {
            try
            {
               setter.invoke(parent, new Object[]{value});
            }
            catch(Exception e)
            {
               throw new IllegalStateException("Failed to addChild for o=" +
                  parent +
                  ", local=" +
                  localName +
                  ", value=" +
                  value +
                  ": " +
                  e.getMessage()
               );
            }
         }
      }
   }

   private final void setAttribute(Object o, String localName, String value)
   {
      if(o instanceof Collection)
      {
         // todo type convertion
         ((Collection)o).add(value);
      }
      else
      {
         Method setter = null;
         Object fieldValue = null;
         final ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(localName);
         if(fieldMapping != null)
         {
            fieldValue = fieldMapping.converter.unmarshal(value);
            setter = fieldMapping.setter;
         }
         else
         {
            try
            {
               final String xmlToCls = Util.xmlNameToClassName(localName, true);
               Method getter = o.getClass().getMethod("get" + xmlToCls, null);
               setter = o.getClass().getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
               fieldValue = getTypeConverter(getter.getReturnType()).unmarshal(value);
            }
            catch(NoSuchMethodException e)
            {
               log.warn("no setter found for " + localName + " in " + o);
            }
         }

         if(setter != null)
         {
            try
            {
               setter.invoke(o, new Object[]{fieldValue});
            }
            catch(Exception e)
            {
               throw new IllegalStateException("Failed to set attribute for o=" +
                  o +
                  ", local=" +
                  localName +
                  ", value=" +
                  value +
                  ": " +
                  e.getMessage()
               );
            }
         }
      }
   }

   private static final TypeConverter getTypeConverter(Class type)
   {
      TypeConverter result;
      if(String.class == type)
      {
         result = TypeConverter.STRING;
      }
      else if(int.class == type || Integer.class == type)
      {
         result = TypeConverter.INT;
      }
      else if(long.class == type || Long.class == type)
      {
         result = TypeConverter.LONG;
      }
      else if(double.class == type || Double.class == type)
      {
         result = TypeConverter.DOUBLE;
      }
      else if(float.class == type || Float.class == type)
      {
         result = TypeConverter.FLOAT;
      }
      else if(short.class == type || Short.class == type)
      {
         result = TypeConverter.SHORT;
      }
      else if(byte.class == type || Byte.class == type)
      {
         result = TypeConverter.BYTE;
      }
      else if(char.class == type || Character.class == type)
      {
         result = TypeConverter.CHAR;
      }
      else if(java.util.Date.class == type)
      {
         result = TypeConverter.JAVA_UTIL_DATE;
      }
      else
      {
         //todo do something
         throw new IllegalStateException("Unexpected field type " + type);
      }
      return result;
   }

   // Inner

   private class ElementToClassMapping
   {
      public final String element;
      public final Class cls;

      public ElementToClassMapping(String element, Class cls)
      {
         this.element = element;
         this.cls = cls;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof ElementToClassMapping))
         {
            return false;
         }

         final ElementToClassMapping classMapping = (ElementToClassMapping)o;

         if(cls != null ? !cls.equals(classMapping.cls) : classMapping.cls != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return (cls != null ? cls.hashCode() : 0);
      }
   }

   private class ElementToFieldMapping
   {
      public final String element;
      public final Class cls;
      public final String field;
      public final TypeConverter converter;
      public final Method getter;
      public final Method setter;

      public ElementToFieldMapping(String element, Class cls, String field, TypeConverter converter)
      {
         this.element = element;
         this.cls = cls;
         this.field = field;
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
         if(!(o instanceof ElementToFieldMapping))
         {
            return false;
         }

         final ElementToFieldMapping elementToFieldMapping = (ElementToFieldMapping)o;

         if(cls != null ? !cls.equals(elementToFieldMapping.cls) : elementToFieldMapping.cls != null)
         {
            return false;
         }
         if(element != null ? !element.equals(elementToFieldMapping.element) : elementToFieldMapping.element != null)
         {
            return false;
         }
         if(field != null ? !field.equals(elementToFieldMapping.field) : elementToFieldMapping.field != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (element != null ? element.hashCode() : 0);
         result = 29 * result + (cls != null ? cls.hashCode() : 0);
         result = 29 * result + (field != null ? field.hashCode() : 0);
         return result;
      }
   }
}
