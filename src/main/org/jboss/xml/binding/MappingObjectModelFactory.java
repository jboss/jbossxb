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
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

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
         if(mapping != null)
         {
            root = newInstance(mapping.cls);
         }
         else
         {
            root = create(namespaceURI, localName);
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
               //child = getter.invoke(o, null);
               child = get(o, localName, getter);
            }

            if(child == null)
            {
               child = newInstance(mapping.cls);
            }

            if(attrs != null)
            {
               for(int i = 0; i < attrs.getLength(); ++i)
               {
                  setAttribute(child, attrs.getLocalName(i), attrs.getValue(i));
               }
            }
         }
         catch(IllegalStateException e)
         {
            throw e;
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
            child = create(namespaceURI, localName);
         }
         else
         {
            Class oCls;
            if(o instanceof ImmutableContainer)
            {
               oCls = ((ImmutableContainer)o).cls;
            }
            else
            {
               oCls = o.getClass();
            }

            String getterStr = Util.xmlNameToGetMethodName(localName, true);
            Method getter;
            try
            {
               getter = oCls.getMethod(getterStr, null);
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
                  child = get(o, localName, getter);

                  // now does this element really represent a Java collection or is it an element that can appear more than once?
                  // try to load the class and create an instance
                  Object item = create(namespaceURI, localName);
                  if(item != null)
                  {
                     if(child == null)
                     {
                        setChild(new ArrayList(), o, localName);
                     }
                     child = item;
                  }
                  else
                  {
                     if(child == null)
                     {
                        child = new ArrayList();
                     }
                  }
               }
               else
               {
                  child = newInstance(childType);
               }
            }
         }
      }

      return child;
   }

   public void addChild(Object parent,
                        Object child,
                        ContentNavigator navigator,
                        String namespaceURI,
                        String localName)
   {
      if(child instanceof ImmutableContainer)
      {
         child = ((ImmutableContainer)child).newInstance();
      }
      setChild(child, parent, localName);
   }

   public void setValue(Object o,
                        ContentNavigator navigator,
                        String namespaceURI,
                        String localName,
                        String value)
   {
      setAttribute(o, localName, value);
   }

   public Object completedRoot(Object root, ContentNavigator navigator, String namespaceURI, String localName)
   {
      if(root instanceof ImmutableContainer)
      {
         root = ((ImmutableContainer)root).newInstance();
      }
      return root;
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
            set(parent, value, localName, setter);
         }
         else
         {
            final String xmlToCls = Util.xmlNameToClassName(localName, true);
            Method getter = null;
            Class parentCls;
            if(parent instanceof ImmutableContainer)
            {
               parentCls = ((ImmutableContainer)parent).cls;
            }
            else
            {
               parentCls = parent.getClass();
            }

            try
            {
               getter = parentCls.getMethod("get" + xmlToCls, null);
            }
            catch(NoSuchMethodException e)
            {
               log.warn("no getter found for " + localName + " in " + parent);
            }

            if(getter != null)
            {
               if(!(child instanceof Collection) && Collection.class.isAssignableFrom(getter.getReturnType()))
               {
                  Object o = get(parent, localName, getter);
                  Collection col = (Collection)o;
                  col.add(child);
               }
               else
               {
                  try
                  {
                     setter = parentCls.getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
                  }
                  catch(NoSuchMethodException e)
                  {
                     log.warn("No setter for " + localName + " in " + parent);
                  }

                  set(parent, value, localName, setter);
               }
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
            Class oCls;
            if(o instanceof ImmutableContainer)
            {
               oCls = ((ImmutableContainer)o).cls;
            }
            else
            {
               oCls = o.getClass();
            }

            try
            {
               final String xmlToCls = Util.xmlNameToClassName(localName, true);
               Method getter = oCls.getMethod("get" + xmlToCls, null);
               fieldValue = getTypeConverter(getter.getReturnType()).unmarshal(value);
               setter = oCls.getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
            }
            catch(NoSuchMethodException e)
            {
               log.warn("no setter found for " + localName + " in " + o + ", value=" + value);
            }
         }

         set(o, fieldValue, localName, setter);
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

   /**
    * Converts namspace URI and local name into a class name, tries to load the class,
    * create an instance and return it.
    *
    * @param namespaceURI element's namespace URI
    * @param localName    element's local name
    * @return null if the class could not be loaded, otherwise an instance of the loaded class
    */
   private Object create(String namespaceURI, String localName)
   {
      Object o = null;

      String clsName = Util.xmlNameToClassName(namespaceURI, localName, true);

      Class type = null;
      try
      {
         type = Thread.currentThread().getContextClassLoader().loadClass(clsName);
      }
      catch(ClassNotFoundException e)
      {
         log.debug("create: failed to load class " + clsName);
      }

      if(type != null)
      {
         o = newInstance(type);
      }

      return o;
   }

   private static Object get(Object o, String localName, Method getter)
   {
      Object value;
      if(o instanceof ImmutableContainer)
      {
         ImmutableContainer con = ((ImmutableContainer)o);
         value = con.getChild(localName);
      }
      else
      {
         try
         {
            value = getter.invoke(o, null);
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke getter " + getter.getName() + " on " + o);
         }
      }
      return value;
   }

   public static void set(Object parent, Object child, String localName, Method setter)
   {
      if(setter != null)
      {
         try
         {
            setter.invoke(parent, new Object[]{child});
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to set value " +
               child +
               " with setter " +
               setter.getName() +
               " on " +
               parent +
               ": " +
               e.getMessage()
            );
         }
      }
      else if(parent instanceof ImmutableContainer)
      {
         ((ImmutableContainer)parent).addChild(localName, child);
      }
      else
      {
         throw new IllegalStateException(
            "setter is null and it's not an immutable container: parent=" + parent + ", child=" + child
         );
      }
   }

   private static Object newInstance(Class cls)
   {
      Object instance;
      try
      {
         Constructor ctor = cls.getConstructor(null);
         instance = ctor.newInstance(null);
      }
      catch(NoSuchMethodException e)
      {
         log.warn("No no-arg constructor in " + cls);
         instance = new ImmutableContainer(cls);
      }
      catch(Exception e)
      {
         throw new IllegalStateException(
            "Failed to create an instance of " + cls + " with the no-arg constructor: " + e.getMessage()
         );
      }
      return instance;
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

   private static class ImmutableContainer
   {
      private final Class cls;
      private final List names = new ArrayList();
      private final List values = new ArrayList();

      public ImmutableContainer(Class cls)
      {
         this.cls = cls;
         log.info("created immutable container for " + cls);
      }

      public void addChild(String localName, Object child)
      {
         if(!names.isEmpty() && names.get(names.size() - 1).equals(localName))
         {
            throw new IllegalStateException("!!! Ahh!! WTF? localName=" + localName);
         }
         names.add(localName);
         values.add(child);
         log.info("added child " + localName + " for " + cls + ": " + child);
      }

      public Object getChild(String localName)
      {
         return names.get(names.size() - 1).equals(localName) ? values.get(values.size() - 1) : null;
      }

      public Object[] getValues()
      {
         return values.toArray();
      }

      public Class[] getValueTypes()
      {
         Class[] types = new Class[values.size()];
         for(int i = 0; i < values.size(); ++i)
         {
            types[i] = values.get(i).getClass();
         }
         return types;
      }

      public Object newInstance()
      {
         Constructor ctor = null;
         Constructor[] ctors = cls.getConstructors();
         for(int i = 0; i < ctors.length; ++i)
         {
            Class[] types = ctors[i].getParameterTypes();

            if(types == null || types.length == 0)
            {
               throw new IllegalStateException("Found no-arg constructor for immutable " + cls);
            }

            if(types.length == values.size())
            {
               ctor = ctors[i];

               int typeInd = 0;
               while(typeInd < types.length)
               {
                  if(!types[typeInd].isAssignableFrom(values.get(typeInd++).getClass()))
                  {
                     ctor = null;
                     break;
                  }
               }

               if(ctor != null)
               {
                  break;
               }
            }
         }

         if(ctor == null)
         {
            throw new IllegalStateException("No constructor in " + cls + " that would take arguments " + values);
         }

         try
         {
            return ctor.newInstance(values.toArray());
         }
         catch(Exception e)
         {
            throw new IllegalStateException(
               "Failed to create immutable instance of " + cls + " using arguments: " + values + ": " + e.getMessage()
            );
         }
      }
   }
}
