/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.util.NestedRuntimeException;
import org.xml.sax.Attributes;
import org.apache.xerces.xs.XSTypeDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

/**
 * An ObjectModelFactory that uses mappings
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @author <a href="mailto:adrian@jboss.com">Adrian Brock</a>
 * @version <tt>$Revision$</tt>
 */
public class MappingObjectModelFactory
   implements GenericObjectModelFactory
{
   private final static Logger log = Logger.getLogger(MappingObjectModelFactory.class);

   /**
    * The class mappings
    */
   private final Map elementToClassMapping = new HashMap();

   /**
    * The field mappings
    */
   private final Map elementToFieldMapping = new HashMap();

   // Public

   /**
    * Map an element to a class
    *
    * @param element the element name
    * @param cls     the class
    */
   public void mapElementToClass(String element, Class cls)
   {
      ElementToClassMapping mapping = new ElementToClassMapping(element, cls);
      addElementToClassMapping(mapping);
      if(log.isTraceEnabled())
      {
         log.trace(mapping);
      }
   }

   /**
    * Map an element to a field
    *
    * @param element   the element name
    * @param cls       the class
    * @param field     the field name
    * @param converter the type convertor
    */
   public void mapElementToField(String element, Class cls, String field, TypeBinding converter)
   {
      ElementToFieldMapping mapping = new ElementToFieldMapping(element, cls, field, converter);
      addElementToFieldMapping(mapping);
      if(log.isTraceEnabled())
      {
         log.trace(mapping);
      }
   }

   // ObjectModelFactory implementation

   public Object newRoot(Object root,
                         UnmarshallingContext ctx,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      if(log.isTraceEnabled())
      {
         log.trace("newRoot root=" +
            root +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName +
            " attributes=" +
            attrs
         );
      }

      if(root == null)
      {
         ElementToClassMapping mapping = (ElementToClassMapping)elementToClassMapping.get(localName);
         if(mapping != null)
         {
            if(log.isTraceEnabled())
            {
               log.trace("creating root using " + mapping);
            }
            root = newInstance(mapping.cls);
         }
         else
         {
            root = create(namespaceURI, localName, ctx.getType());
         }

         if(root == null)
         {
            throw new IllegalStateException(
               "Failed to resolve Java type binding for root element: ns=" + namespaceURI + ", local=" + localName
            );
         }
      }

      if(attrs != null)
      {
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            try
            {
               if(attrs.getLocalName(i).length() > 0)
               {
                  if(!attrs.getQName(i).startsWith("xsi:")) //todo horrible
                  {
                     setAttribute(root, attrs.getLocalName(i), attrs.getValue(i), ctx);
                  }
               }
            }
            catch(Exception e)
            {
               String msg = "Failed to set attribute " + attrs.getQName(i) + "=" + attrs.getValue(i);
               log.error(msg, e);
               throw new IllegalStateException(msg + ": " + e.getMessage());
            }
         }
      }

      return root;
   }

   // GenericObjectModelFactory implementation

   public Object newChild(Object o,
                          UnmarshallingContext ctx,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      if(log.isTraceEnabled())
      {
         log.trace("newChild object=" +
            o +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName +
            " attributes=" +
            attrs
         );
      }

      if(o == null)
      {
         throw new RuntimeException("Attempt to add a new child to a null parent localName=" + localName);
      }

      Object child = null;

      ElementToClassMapping mapping = (ElementToClassMapping)elementToClassMapping.get(localName);
      XSTypeDefinition type = ctx.getType();
      if(mapping != null)
      {
         if(log.isTraceEnabled())
         {
            log.trace("newChild using mapping " + mapping);
         }

         try
         {
            if(!(o instanceof Collection))
            {
               ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
                  new ElementToFieldMappingKey(localName, o.getClass())
               );

               if(fieldMapping != null)
               {
                  child = get(o, localName, fieldMapping.getter, fieldMapping.field);
               }
               else
               {
                  String xmlToCls = Util.xmlNameToClassName(localName, true);
                  Method getter = null;
                  Field field = null;
                  try
                  {
                     getter = o.getClass().getMethod("get" + xmlToCls, null);
                  }
                  catch(NoSuchMethodException e)
                  {
                     try
                     {
                        field =
                           o.getClass().getField(Character.toLowerCase(xmlToCls.charAt(0)) + xmlToCls.substring(1));
                     }
                     catch(NoSuchFieldException ee)
                     {
                        throw new JBossXBException(
                           "Neither field nor its getter were found for " + localName + " in " + o.getClass()
                        );
                     }
                  }
                  child = get(o, localName, getter, field);
               }
            }

            if(child == null)
            {
               child = newInstance(mapping.cls);
            }

            if(attrs != null)
            {
               for(int i = 0; i < attrs.getLength(); ++i)
               {
                  if(attrs.getLocalName(i).length() > 0)
                  {
                     if(!attrs.getQName(i).startsWith("xsi:")) //todo horrible
                     {
                        setAttribute(child, attrs.getLocalName(i), attrs.getValue(i), ctx);
                     }
                  }
               }
            }
         }
         catch(RuntimeException e)
         {
            throw e;
         }
         catch(Exception e)
         {
            throw new NestedRuntimeException("newChild failed for o=" +
               o +
               ", uri=" +
               namespaceURI +
               ", local="
               + localName + ", attrs=" + attrs, e
            );
         }
      }
      else
      {
         if(o instanceof Collection)
         {
            child = create(namespaceURI, localName, type);
         }
         else
         {
            Class oCls;
            if(o instanceof Immutable)
            {
               oCls = ((Immutable)o).cls;
            }
            else
            {
               oCls = o.getClass();
            }

            String xmlToCls = Util.xmlNameToClassName(localName, true);
            Method getter = null;
            Field field = null;
            Class childType;
            try
            {
               getter = oCls.getMethod("get" + xmlToCls, null);
               childType = getter.getReturnType();
            }
            catch(NoSuchMethodException e)
            {
               try
               {
                  field = oCls.getField(Character.toLowerCase(xmlToCls.charAt(0)) + xmlToCls.substring(1));
                  childType = field.getType();
               }
               catch(NoSuchFieldException e1)
               {
                  throw new IllegalStateException("newChild failed for o=" +
                     o +
                     ", uri=" +
                     namespaceURI +
                     ", local="
                     + localName + ", attrs=" + attrs + ": neither field nor its getter were found"
                  );
               }
            }

            if(Collection.class.isAssignableFrom(childType))
            {
               child = get(o, localName, getter, field);

               // now does this element really represent a Java collection or is it an element that can appear more than once?
               // try to load the class and create an instance
               Object item = null;
               if(type == null || type != null && type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
               {
                  item = create(namespaceURI, localName, type);
               }

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
            else if(!Util.isAttributeType(childType))
            {
               // id there is no field mapping
               ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
                  new ElementToFieldMappingKey(localName, o.getClass())
               );
               TypeBinding converter = fieldMapping == null ? null : fieldMapping.converter;

               // if converter != null it will be used in setValue
               if(converter == null)
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
                        UnmarshallingContext ctx,
                        String namespaceURI,
                        String localName)
   {
      if(log.isTraceEnabled())
      {
         log.trace("addChild parent=" +
            parent +
            " child=" +
            child +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName
         );
      }

      if(child instanceof Immutable)
      {
         child = ((Immutable)child).newInstance();
      }
      setChild(child, parent, localName);
   }

   public void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value)
   {
      if(log.isTraceEnabled())
      {
         log.trace("setValue object=" +
            o +
            " ctx=" +
            ctx +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName +
            " value=" +
            value
         );
      }

      setAttribute(o, localName, value, ctx);
   }

   public Object completeRoot(Object root, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      if(log.isTraceEnabled())
      {
         log.trace("completeRoot root=" +
            root +
            " navigator=" +
            navigator +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName
         );
      }

      if(root instanceof Immutable)
      {
         root = ((Immutable)root).newInstance();
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
      elementToFieldMapping.put(mapping.key, mapping);
   }

   private void setChild(Object child, Object parent, String localName)
   {
      boolean trace = log.isTraceEnabled();
      Object value = child;
      if(parent instanceof Collection)
      {
         if(trace)
         {
            log.trace("Add " + value + " to collection " + parent);
         }
         ((Collection)parent).add(value);
      }
      else
      {
         final ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
            new ElementToFieldMappingKey(localName, parent.getClass())
         );
         if(fieldMapping != null)
         {
            if(trace)
            {
               log.trace("Add " + value + " to " + parent + " using field mapping " + fieldMapping);
            }
            set(parent, value, localName, fieldMapping.setter, fieldMapping.field);
         }
         else
         {
            final String xmlToCls = Util.xmlNameToClassName(localName, true);
            if(trace)
            {
               log.trace("Add " + value + " to xml mapped class " + xmlToCls);
            }

            Class parentCls = parent instanceof Immutable ?
               ((Immutable)parent).cls :
               parent.getClass();
            Method getter = null;
            Field field = null;
            Class fieldType = null;

            try
            {
               getter = parentCls.getMethod("get" + xmlToCls, null);
               fieldType = getter.getReturnType();
            }
            catch(NoSuchMethodException e)
            {
               try
               {
                  field = parentCls.getField(Character.toLowerCase(xmlToCls.charAt(0)) + xmlToCls.substring(1));
                  fieldType = field.getType();
               }
               catch(NoSuchFieldException e1)
               {
                  log.warn("neither field nor its getter were found for " + localName + " in " + parent);
               }
            }

            if(fieldType != null)
            {
               if(!(child instanceof Collection) && Collection.class.isAssignableFrom(fieldType))
               {
                  Object o = get(parent, localName, getter, field);
                  Collection col = (Collection)o;
                  if(trace)
                  {
                     log.trace("Add " + value + " to collection " + col + " retrieved from getter " + getter);
                  }
                  col.add(child);
               }
               else
               {
                  Method setter = null;
                  if(field == null)
                  {
                     try
                     {
                        setter = parentCls.getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
                     }
                     catch(NoSuchMethodException e)
                     {
                        log.warn("No setter for " + localName + " in " + parentCls);
                     }
                  }
                  set(parent, value, localName, setter, field);
               }
            }
         }
      }
   }

   private void setAttribute(Object o, String localName, String value, UnmarshallingContext ctx)
   {
      if(o instanceof Collection)
      {
         XSTypeDefinition type = ctx.getType();
         if(type == null)
         {
            log.warn("Type is not available for collection item " + localName + "=" + value + " -> adding as string.");
            ((Collection)o).add(value);
         }
         else
         {
            if(type.getName() == null)
            {
               throw new IllegalStateException("Name is null for simple type?!");
            }

            Object trgValue = SimpleTypeBindings.unmarshal(type.getName(), value, ctx.getNamespaceContext());
            ((Collection)o).add(trgValue);
         }
      }
      else
      {
         Object fieldValue = null;
         final ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
            new ElementToFieldMappingKey(localName, o.getClass())
         );

         if(fieldMapping != null)
         {
            fieldValue = fieldMapping.converter.unmarshal(value);
            set(o, fieldValue, localName, fieldMapping.setter, fieldMapping.field);
         }
         else
         {
            Class oCls;
            if(o instanceof Immutable)
            {
               oCls = ((Immutable)o).cls;
            }
            else
            {
               oCls = o.getClass();
            }

            Method setter = null;
            Field field = null;
            Class fieldType = null;
            final String xmlToCls = Util.xmlNameToClassName(localName, true);

            try
            {
               Method getter = oCls.getMethod("get" + xmlToCls, null);
               fieldType = getter.getReturnType();
               setter = oCls.getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
            }
            catch(NoSuchMethodException e)
            {
               try
               {
                  field = oCls.getField(Character.toLowerCase(xmlToCls.charAt(0)) + xmlToCls.substring(1));
                  fieldType = field.getType();
               }
               catch(NoSuchFieldException e1)
               {
                  if(fieldType == null)
                  {
                     throw new JBossXBRuntimeException(
                        "Failed to discover field's type: niether field nor its getter were found for " +
                        localName +
                        " in " +
                        oCls
                     );
                  }
               }
            }

            fieldValue = SimpleTypeBindings.unmarshal(value, fieldType);
            set(o, fieldValue, localName, setter, field);
         }
      }
   }

   /**
    * Converts namspace URI and local name into a class name, tries to load the class,
    * create an instance and return it.
    *
    * @param namespaceURI element's namespace URI
    * @param localName    element's local name
    * @return null if the class could not be loaded, otherwise an instance of the loaded class
    */
   private static Object create(String namespaceURI, String localName, XSTypeDefinition type)
   {
      Object o = null;

      String clsName = type != null && type.getName() != null ?
         Util.xmlNameToClassName(namespaceURI, type.getName(), true) :
         Util.xmlNameToClassName(namespaceURI, localName, true);

      Class cls = null;
      try
      {
         cls = Thread.currentThread().getContextClassLoader().loadClass(clsName);
      }
      catch(ClassNotFoundException e)
      {
         if(log.isTraceEnabled())
         {
            log.trace("create: failed to load class " + clsName);
         }
      }

      if(cls != null)
      {
         o = newInstance(cls);
      }

      return o;
   }

   private static Object get(Object o, String localName, Method getter, Field field)
   {
      if(log.isTraceEnabled())
      {
         log.trace("get object=" + o + " localName=" + localName + " getter=" + getter + " field=" + field);
      }

      Object value;
      if(o instanceof Immutable)
      {
         Immutable con = ((Immutable)o);
         value = con.getChild(localName);
      }
      else
      {
         try
         {
            value = getter != null ? getter.invoke(o, null) : field.get(o);
         }
         catch(Exception e)
         {
            throw new NestedRuntimeException(
               "Failed to get field value " + (getter != null ? getter.getName() : field.getName()) + " on " + o, e
            );
         }
      }
      return value;
   }

   private static void set(Object parent, Object child, String localName, Method setter, Field field)
   {
      if(log.isTraceEnabled())
      {
         log.trace("set parent=" + parent + " child=" + child + " localName=" + localName + " setter=" + setter);
      }

      if(setter != null)
      {
         try
         {
            setter.invoke(parent, new Object[]{child});
         }
         catch(Exception e)
         {
            throw new NestedRuntimeException("Failed to set field value " +
               child +
               " with setter " +
               setter
               + " on " + parent + ": ", e
            );
         }
      }
      else if(field != null)
      {
         if(!field.isAccessible())
         {
            field.setAccessible(true);
         }

         try
         {
            field.set(parent, child);
         }
         catch(IllegalArgumentException e)
         {
            throw new NestedRuntimeException("Failed to set field value " +
               child +
               " with field " +
               field.getName() +
               " on " +
               parent +
               ": field type is " +
               field.getType() + ", value type is " + (child == null ? null : child.getClass()), e
            );
         }
         catch(IllegalAccessException e)
         {
            throw new NestedRuntimeException("Failed to set field value " +
               child +
               " with field " +
               field.getName()
               + " on " + parent + ": ", e
            );
         }
      }
      else if(parent instanceof Immutable)
      {
         ((Immutable)parent).addChild(localName, child);
      }
      else
      {
         throw new IllegalStateException("Field and setter are null and it's not an immutable container: parent=" +
            parent.getClass() +
            ", localName=" + localName + ", parent=" + parent + ", child=" + child
         );
      }
   }

   private static Object newInstance(Class cls)
   {
      if(log.isTraceEnabled())
      {
         log.trace("new " + cls.getName());
      }

      Object instance;
      try
      {
         Constructor ctor = cls.getConstructor(null);
         instance = ctor.newInstance(null);
      }
      catch(NoSuchMethodException e)
      {
         log.warn("No no-arg constructor in " + cls);
         instance = new Immutable(cls);
      }
      catch(Exception e)
      {
         throw new IllegalStateException("Failed to create an instance of " +
            cls +
            " with the no-arg constructor: "
            + e.getMessage()
         );
      }
      return instance;
   }

   // Inner classes

   private class ElementToClassMapping
   {
      public final String element;

      public final Class cls;

      public ElementToClassMapping(String element, Class cls)
      {
         this.element = element;
         this.cls = cls;
      }

      public String toString()
      {
         StringBuffer buffer = new StringBuffer();
         buffer.append("ElementToClass@").append(System.identityHashCode(this));
         buffer.append("{element=").append(element);
         if(cls != null)
         {
            buffer.append(" class=").append(cls.getName());
         }
         buffer.append("}");
         return buffer.toString();
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

   private class ElementToFieldMappingKey
   {
      public final String element;

      public final Class cls;

      public ElementToFieldMappingKey(String element, Class cls)
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
         if(!(o instanceof ElementToFieldMappingKey))
         {
            return false;
         }

         final ElementToFieldMappingKey elementToFieldMappingKey = (ElementToFieldMappingKey)o;

         if(cls != null ? !cls.equals(elementToFieldMappingKey.cls) : elementToFieldMappingKey.cls != null)
         {
            return false;
         }
         if(element != null ?
            !element.equals(elementToFieldMappingKey.element) :
            elementToFieldMappingKey.element != null)
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
         return result;
      }
   }

   private class ElementToFieldMapping
   {
      public final String element;

      public final Class cls;

      public final String fieldName;

      public final TypeBinding converter;

      public final ElementToFieldMappingKey key;

      public final Method getter;

      public final Method setter;

      public final Field field;

      public ElementToFieldMapping(String element, Class cls, String fieldName, TypeBinding converter)
      {
         this.element = element;
         this.cls = cls;
         this.fieldName = fieldName;
         this.converter = converter;
         key = new ElementToFieldMappingKey(element, cls);

         Field field = null;
         Method getter = null;
         Method setter = null;
         try
         {
            field = cls.getField(fieldName);
            if(!field.isAccessible())
            {
               field.setAccessible(true);
            }
         }
         catch(NoSuchFieldException e)
         {
            try
            {
               getter = Classes.getAttributeGetter(cls, fieldName);
            }
            catch(NoSuchMethodException e1)
            {
               throw new JBossXBRuntimeException(
                  "Neither field nor its getter method was found for " + fieldName + " in " + cls
               );
            }

            try
            {
               setter = Classes.getAttributeSetter(cls, fieldName, getter.getReturnType());
            }
            catch(NoSuchMethodException e1)
            {
               throw new JBossXBRuntimeException(
                  "Neither field nor its setter method was found for " + fieldName + " in " + cls
               );
            }
         }

         this.field = field;
         this.getter = getter;
         this.setter = setter;
      }

      public String toString()
      {
         StringBuffer buffer = new StringBuffer();
         buffer.append("ElementToField@").append(System.identityHashCode(this));
         buffer.append("{element=").append(element);
         if(cls != null)
         {
            buffer.append(" class=").append(cls.getName());
         }
         buffer.append(" field=").append(fieldName);
         buffer.append(" getter=").append(getter);
         buffer.append(" setter=").append(setter);
         if(converter != null)
         {
            buffer.append(" convertor=").append(converter.getClass().getName());
         }
         buffer.append("}");
         return buffer.toString();
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
         if(fieldName != null ?
            !fieldName.equals(elementToFieldMapping.fieldName) :
            elementToFieldMapping.fieldName != null)
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
         result = 29 * result + (fieldName != null ? fieldName.hashCode() : 0);
         return result;
      }
   }
}
