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

import org.jboss.logging.Logger;
import org.jboss.util.NestedRuntimeException;
import org.jboss.xb.binding.introspection.FieldInfo;
import org.xml.sax.Attributes;
import org.apache.xerces.xs.XSTypeDefinition;

import java.lang.reflect.Constructor;
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
   private final Map<String, ElementToClassMapping> elementToClassMapping = new HashMap<String, ElementToClassMapping>();

   /**
    * The field mappings
    */
   private final Map<ElementToFieldMappingKey, ElementToFieldMapping> elementToFieldMapping = new HashMap<ElementToFieldMappingKey, ElementToFieldMapping>();

   // Public

   /**
    * Map an element to a class
    *
    * @param element the element name
    * @param cls     the class
    */
   public void mapElementToClass(String element, Class<?> cls)
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
   public void mapElementToField(String element, Class<?> cls, String field, TypeBinding converter)
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
         ElementToClassMapping mapping = elementToClassMapping.get(localName);
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

      ElementToClassMapping mapping = elementToClassMapping.get(localName);
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
               ElementToFieldMapping fieldMapping = elementToFieldMapping.get(
                  new ElementToFieldMappingKey(localName, o.getClass())
               );

               FieldInfo fieldInfo;
               if(fieldMapping != null)
               {
                  fieldInfo = fieldMapping.fieldInfo;
               }
               else
               {
                  String fieldName = Util.xmlNameToFieldName(localName, true);
                  fieldInfo = FieldInfo.getFieldInfo(o.getClass(), fieldName, true);
               }

               child = get(o, localName, fieldInfo);
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
            Class<?> oCls;
            if(o instanceof Immutable)
            {
               oCls = ((Immutable)o).cls;
            }
            else
            {
               oCls = o.getClass();
            }

            String fieldName = Util.xmlNameToFieldName(localName, true);
            FieldInfo fieldInfo = FieldInfo.getFieldInfo(oCls, fieldName, true);
            if(Collection.class.isAssignableFrom(fieldInfo.getType()))
            {
               child = get(o, localName, fieldInfo);

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
                     child = new ArrayList<Object>();
                  }
               }
            }
            else if(!Util.isAttributeType(fieldInfo.getType()))
            {
               // id there is no field mapping
               ElementToFieldMapping fieldMapping = elementToFieldMapping.get(
                  new ElementToFieldMappingKey(localName, o.getClass())
               );
               TypeBinding converter = fieldMapping == null ? null : fieldMapping.converter;

               // if converter != null it will be used in setValue
               if(converter == null)
               {
                  child = newInstance(fieldInfo.getType());
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
         ((Collection<Object>)parent).add(value);
      }
      else
      {
         final ElementToFieldMapping fieldMapping = elementToFieldMapping.get(
            new ElementToFieldMappingKey(localName, parent.getClass())
         );
         if(fieldMapping != null)
         {
            if(trace)
            {
               log.trace("Add " + value + " to " + parent + " using field mapping " + fieldMapping);
            }
            set(parent, value, localName, fieldMapping.fieldInfo);
         }
         else
         {
            Class<?> parentCls = parent instanceof Immutable ?
               ((Immutable)parent).cls :
               parent.getClass();

            String fieldName = Util.xmlNameToFieldName(localName, true);
            FieldInfo fieldInfo = FieldInfo.getFieldInfo(parentCls, fieldName, false);
            if(trace)
            {
               log.trace("Add " + value + " to property " + fieldName + " of " + parentCls);
            }

            if(fieldInfo != null)
            {
               if(!(child instanceof Collection) && Collection.class.isAssignableFrom(fieldInfo.getType()))
               {
                  Object o = get(parent, localName, fieldInfo);
                  Collection<Object> col = (Collection<Object>)o;
                  if(trace)
                  {
                     log.trace("Add " + value + " to collection " + col + " retrieved from " + fieldName);
                  }
                  col.add(child);
               }
               else
               {
                  set(parent, value, localName, fieldInfo);
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
            ((Collection<String>)o).add(value);
         }
         else
         {
            if(type.getName() == null)
            {
               throw new IllegalStateException("Name is null for simple type?!");
            }

            Object trgValue = SimpleTypeBindings.unmarshal(type.getName(), value, ctx.getNamespaceContext());
            ((Collection<Object>)o).add(trgValue);
         }
      }
      else
      {
         Object fieldValue = null;
         final ElementToFieldMapping fieldMapping = elementToFieldMapping.get(
            new ElementToFieldMappingKey(localName, o.getClass())
         );

         if(fieldMapping != null)
         {
            fieldValue = fieldMapping.converter.unmarshal(value);
            set(o, fieldValue, localName, fieldMapping.fieldInfo);
         }
         else
         {
            Class<?> oCls;
            if(o instanceof Immutable)
            {
               oCls = ((Immutable)o).cls;
            }
            else
            {
               oCls = o.getClass();
            }

            final String fieldName = Util.xmlNameToFieldName(localName, true);
            FieldInfo fieldInfo = FieldInfo.getFieldInfo(oCls, fieldName, true);

            fieldValue = SimpleTypeBindings.unmarshal(value, fieldInfo.getType());
            set(o, fieldValue, localName, fieldInfo);
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

      Class<?> cls = null;
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

   private static Object get(Object o, String localName, FieldInfo fieldInfo)
   {
      if(log.isTraceEnabled())
      {
         log.trace("get object=" + o + " localName=" + localName);
      }

      Object value;
      if(o instanceof Immutable)
      {
         Immutable con = ((Immutable)o);
         value = con.getChild(localName);
      }
      else
      {
         value = fieldInfo.getValue(o);
      }
      return value;
   }

   private static void set(Object parent, Object child, String localName, FieldInfo fieldInfo)
   {
      if(log.isTraceEnabled())
      {
         log.trace("set parent=" + parent + " child=" + child + " localName=" + localName);
      }

      if(fieldInfo.isWritable())
      {
         fieldInfo.setValue(parent, child);
      }
      else if(parent instanceof Immutable)
      {
         ((Immutable)parent).addChild(localName, child);
      }
      else
      {
         throw new IllegalStateException("Neither write method nor field were found for " + fieldInfo.getName() +
            " and the parent object is not an immutable container: parent=" +
            parent.getClass() +
            ", localName=" + localName + ", parent=" + parent + ", child=" + child
         );
      }
   }

   private static Object newInstance(Class<?> cls)
   {
      if(log.isTraceEnabled())
      {
         log.trace("new " + cls.getName());
      }

      Object instance;
      try
      {
         Constructor<?> ctor = cls.getConstructor(null);
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

      public final Class<?> cls;

      public ElementToClassMapping(String element, Class<?> cls)
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

      public final Class<?> cls;

      public ElementToFieldMappingKey(String element, Class<?> cls)
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
      public final Class<?> cls;
      public final TypeBinding converter;
      public final ElementToFieldMappingKey key;
      public final FieldInfo fieldInfo;

      public ElementToFieldMapping(String element, Class<?> cls, String fieldName, TypeBinding converter)
      {
         this.element = element;
         this.cls = cls;
         this.converter = converter;
         key = new ElementToFieldMappingKey(element, cls);
         fieldInfo = FieldInfo.getFieldInfo(cls, fieldName, true);
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
         buffer.append(" field=").append(fieldInfo.getName());
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

         if(!fieldInfo.getName().equals(elementToFieldMapping.fieldInfo.getName()))
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
         result = 29 * result + fieldInfo.getName().hashCode();
         return result;
      }
   }
}
