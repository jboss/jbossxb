/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.util.NestedRuntimeException;
import org.xml.sax.Attributes;
import org.apache.xerces.xs.XSTypeDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

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
                         UnmarshallingContext navigator,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      if(log.isTraceEnabled())
      {
         log.trace("newRoot root=" +
            root +
            " navigator=" +
            navigator +
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
            root = create(namespaceURI, localName, navigator.getType());
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
                     setAttribute(root, attrs.getLocalName(i), attrs.getValue(i), navigator.getType());
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
                          UnmarshallingContext navigator,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      if(log.isTraceEnabled())
      {
         log.trace("newChild object=" +
            o +
            " navigator=" +
            navigator +
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
      XSTypeDefinition type = navigator.getType();
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
               Method getter;
               ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
                  new ElementToFieldMappingKey(localName, o.getClass())
               );

               if(fieldMapping != null)
               {
                  getter = fieldMapping.getter;
               }
               else
               {
                  String getterStr = Util.xmlNameToGetMethodName(localName, true);
                  getter = o.getClass().getMethod(getterStr, null);
               }
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
                  if(attrs.getLocalName(i).length() > 0)
                  {
                     if(!attrs.getQName(i).startsWith("xsi:")) //todo horrible
                     {
                        setAttribute(child, attrs.getLocalName(i), attrs.getValue(i), type);
                     }
                  }
               }
            }
         }
         catch(IllegalStateException e)
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
                  ", local="
                  + localName + ", attrs=" + attrs + ": no getter"
               );
            }

            Class childType = getter.getReturnType();
            if(Collection.class.isAssignableFrom(childType))
            {
               child = get(o, localName, getter);

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
                        UnmarshallingContext navigator,
                        String namespaceURI,
                        String localName)
   {
      if(log.isTraceEnabled())
      {
         log.trace("addChild parent=" +
            parent +
            " child=" +
            child +
            " navigator=" +
            navigator +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName
         );
      }

      if(child instanceof ImmutableContainer)
      {
         child = ((ImmutableContainer)child).newInstance();
      }
      setChild(child, parent, localName);
   }

   public void setValue(Object o, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      if(log.isTraceEnabled())
      {
         log.trace("setValue object=" +
            o +
            " navigator=" +
            navigator +
            " namespaceURI=" +
            namespaceURI +
            " localName=" +
            localName +
            " value=" +
            value
         );
      }

      setAttribute(o, localName, value, navigator.getType());
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
         Method setter = null;
         final ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
            new ElementToFieldMappingKey(localName, parent.getClass())
         );
         if(fieldMapping != null)
         {
            if(trace)
            {
               log.trace("Add " + value + " to " + parent + " using field mapping " + fieldMapping);
            }
            setter = fieldMapping.setter;
            set(parent, value, localName, setter);
         }
         else
         {
            final String xmlToCls = Util.xmlNameToClassName(localName, true);
            if(trace)
            {
               log.trace("Add " + value + " to xml mapped class " + xmlToCls);
            }
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
                  if(trace)
                  {
                     log.trace("Add " + value + " to collection " + col + " retrieved from getter " + getter);
                  }
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
                     log.warn("No setter for " + localName + " in " + parentCls);
                  }

                  set(parent, value, localName, setter);
               }
            }
         }
      }
   }

   private void setAttribute(Object o, String localName, String value, XSTypeDefinition type)
   {
      if(o instanceof Collection)
      {
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

            Object trgValue = SimpleTypeBindings.unmarshal(type.getName(), value);
            ((Collection)o).add(trgValue);
         }
      }
      else
      {
         Method setter = null;
         Object fieldValue = null;
         final ElementToFieldMapping fieldMapping = (ElementToFieldMapping)elementToFieldMapping.get(
            new ElementToFieldMappingKey(localName, o.getClass())
         );
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
               fieldValue = SimpleTypeBindings.unmarshal(value, getter.getReturnType());
               setter = oCls.getMethod("set" + xmlToCls, new Class[]{getter.getReturnType()});
            }
            catch(NoSuchMethodException e)
            {
               log.warn("no setter found for " + localName + " in " + oCls);
            }
         }

         set(o, fieldValue, localName, setter);
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

   private static Object get(Object o, String localName, Method getter)
   {
      if(log.isTraceEnabled())
      {
         log.trace("get object=" + o + " localName=" + localName + " getter=" + getter);
      }

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
            throw new NestedRuntimeException("Failed to invoke " + getter + " on " + o, e);
         }
      }
      return value;
   }

   private static void set(Object parent, Object child, String localName, Method setter)
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
            throw new NestedRuntimeException("Failed to set attribute value " +
               child +
               " with setter " +
               setter
               + " on " + parent + ": ", e
            );
         }
      }
      else if(parent instanceof ImmutableContainer)
      {
         ((ImmutableContainer)parent).addChild(localName, child);
      }
      else
      {
         throw new IllegalStateException("setter is null and it's not an immutable container: parent=" +
            parent.getClass() +
            ", localName" + localName + ", parent=" + parent + ", child=" + child
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
         instance = new ImmutableContainer(cls);
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

      public final String field;

      public final TypeBinding converter;

      public final ElementToFieldMappingKey key;

      public final Method getter;

      public final Method setter;

      public ElementToFieldMapping(String element, Class cls, String field, TypeBinding converter)
      {
         this.element = element;
         this.cls = cls;
         this.field = field;
         this.converter = converter;
         key = new ElementToFieldMappingKey(element, cls);

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

      public String toString()
      {
         StringBuffer buffer = new StringBuffer();
         buffer.append("ElementToField@").append(System.identityHashCode(this));
         buffer.append("{element=").append(element);
         if(cls != null)
         {
            buffer.append(" class=").append(cls.getName());
         }
         buffer.append(" field=").append(field);
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
         if(log.isTraceEnabled())
         {
            log.trace("created immutable container for " + cls);
         }
      }

      public void addChild(String localName, Object child)
      {
         if(!names.isEmpty() && names.get(names.size() - 1).equals(localName))
         {
            throw new IllegalStateException("Attempt to add duplicate element " + localName);
         }
         names.add(localName);
         values.add(child);

         if(log.isTraceEnabled())
         {
            log.trace("added child " + localName + " for " + cls + ": " + child);
         }
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

         if(ctors == null || ctors.length == 0)
         {
            throw new JBossXBRuntimeException("The class has no declared constructors: " + cls);
         }

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
            throw new IllegalStateException("Failed to create immutable instance of " +
               cls +
               " using arguments: "
               + values + ": " + e.getMessage()
            );
         }
      }
   }
}
