/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.XmlValueBinding;
import org.jboss.xml.binding.metadata.unmarshalling.XmlValueContainer;
import org.jboss.xml.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MetadataDrivenObjectModelFactory
   implements GenericObjectModelFactory
{
   private static final Logger log = Logger.getLogger(MetadataDrivenObjectModelFactory.class);

   public Object newChild(Object parent,
                          UnmarshallingContext ctx,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      boolean trace = log.isTraceEnabled();
      if(trace)
      {
         log.trace("newChild " + namespaceURI + ":" + localName + " for " + parent);
      }

      Object child;

      ElementBinding metadata = (ElementBinding)ctx.getMetadata();
      if(metadata == null)
      {
         throw new JBossXBRuntimeException(
            "Binding metadata is not available for element {" + namespaceURI + ":" + localName + "}"
         );
      }

      if(Collection.class.isAssignableFrom(metadata.getJavaType()))
      {
         Collection col;
         if(parent instanceof ImmutableContainer)
         {
            ImmutableContainer imm = (ImmutableContainer)parent;
            col = (Collection)imm.getChild(localName);
            if(col == null)
            {
               col = (Collection)newInstance(metadata);
               imm.addChild(localName, col);
            }
         }
         else
         {
            col = (Collection)getFieldValue(metadata, parent);
            if(col == null)
            {
               col = (Collection)newInstance(metadata);
               setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), parent, col);
            }
         }

         child = col;
      }
      else if(!Util.isAttributeType(metadata.getJavaType()))
      {
         child = newInstance(metadata);
         if(!(child instanceof ImmutableContainer))
         {
            if(parent instanceof Collection)
            {
               ((Collection)parent).add(child);
            }
            else if(parent instanceof ImmutableContainer)
            {
               ((ImmutableContainer)parent).addChild(localName, child);
            }
            else if(metadata.getFieldType() != null && Collection.class.isAssignableFrom(metadata.getFieldType()))
            {
               Collection col = (Collection)getFieldValue(metadata, parent);
               if(col == null)
               {
                  if(Set.class.isAssignableFrom(metadata.getFieldType()))
                  {
                     col = new HashSet();
                  }
                  else
                  {
                     col = new ArrayList();
                  }
                  setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), parent, col);
               }
               col.add(child);
            }
            else
            {
               setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), parent, child);
            }

            if(attrs != null && attrs.getLength() > 0)
            {
               for(int i = 0; i < attrs.getLength(); ++i)
               {
                  QName attrName = new QName(attrs.getURI(i), attrs.getLocalName(i));
                  AttributeBinding attrBinding = metadata.getAttribute(attrName);
                  if(attrBinding != null)
                  {
                     Object unmarshalledValue = SimpleTypeBindings.unmarshal(attrs.getValue(i),
                        attrBinding.getJavaType()
                     );
                     setFieldValue(attrBinding.getAttributeName(),
                        attrBinding.getField(),
                        attrBinding.getGetter(),
                        child,
                        unmarshalledValue
                     );
                  }
               }
            }
         }
      }
      else
      {
         child = null;
      }

      return child;
   }

   public void addChild(Object parent, Object child, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      if(child instanceof ImmutableContainer)
      {
         ElementBinding metadata = (ElementBinding)ctx.getMetadata();

         child = ((ImmutableContainer)child).newInstance();
         if(parent instanceof Collection)
         {
            ((Collection)parent).add(child);
         }
         else if(metadata.getFieldType() == null || Collection.class.isAssignableFrom(metadata.getFieldType()))
         {
            Collection col;
            if(parent instanceof ImmutableContainer)
            {
               ImmutableContainer imm = (ImmutableContainer)parent;
               col = (Collection)imm.getChild(localName);
               if(col == null)
               {
                  col = new ArrayList();
                  imm.addChild(localName, col);
               }
            }
            else
            {
               col = (Collection)getFieldValue(metadata, parent);
               if(col == null)
               {
                  col = new ArrayList();
                  setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), parent, col);
               }
            }

            col.add(child);
         }
         else if(parent instanceof ImmutableContainer)
         {
            ((ImmutableContainer)parent).addChild(localName, child);
         }
         else
         {
            setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), parent, child);
         }
      }
   }

   public void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value)
   {
      ElementBinding metadata = (ElementBinding)ctx.getMetadata();

      // todo: this check is a hack! undeterminism when field is of type collection and there is only RuntimeDocumentBinding
      if(Collection.class.isAssignableFrom(metadata.getJavaType()))
      {
         ((Collection)o).add(value);
      }
      else
      {
         if(Util.isAttributeType(metadata.getJavaType()))
         {
            Object unmarshalledValue = SimpleTypeBindings.unmarshal(value, metadata.getJavaType());
            if(o instanceof Collection)
            {
               ((Collection)o).add(unmarshalledValue);
            }
            else if(o instanceof ImmutableContainer)
            {
               ((ImmutableContainer)o).addChild(localName, unmarshalledValue);
            }
            else
            {
               if(Collection.class.isAssignableFrom(metadata.getFieldType()))
               {
                  Collection col = (Collection)getFieldValue(metadata, o);
                  if(col == null)
                  {
                     col = new ArrayList();
                     setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), o, col);
                  }
                  col.add(unmarshalledValue);
               }
               else
               {
                  setFieldValue(metadata.getName(), metadata.getField(), metadata.getSetter(), o, unmarshalledValue);
               }
            }
         }
         else
         {
            XmlValueBinding valueBinding = metadata.getValue();
            if(valueBinding == null)
            {
               throw new JBossXBRuntimeException(
                  "Required value binding is not customized for " + metadata.getName() + ": value=" + value
               );
            }

            unmarshalValue(valueBinding, value, o);
         }
      }
   }

   public Object newRoot(Object root,
                         UnmarshallingContext ctx,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      if(root == null)
      {
         BasicElementBinding metadata = ctx.getMetadata();
         if(metadata == null)
         {
            throw new JBossXBRuntimeException(
               "Binding metadata is not available for top-level element {" + namespaceURI + ":" + localName + "}"
            );
         }
         root = newInstance(metadata);
      }
      return root;
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      return root instanceof ImmutableContainer ? ((ImmutableContainer)root).newInstance() : root;
   }

   // Private

   private static void unmarshalValue(XmlValueBinding valueBinding, String value, Object o)
   {
      Object unmarshalled;
      if(valueBinding.getValue() != null)
      {
         unmarshalled = newInstance(valueBinding);
         unmarshalValue(valueBinding.getValue(), value, unmarshalled);

         if(unmarshalled instanceof ImmutableContainer)
         {
            unmarshalled = ((ImmutableContainer)unmarshalled).newInstance();
         }
      }
      else
      {
         unmarshalled = SimpleTypeBindings.unmarshal(value, valueBinding.getJavaType());
      }

      // todo o instanceof java.util.Collection?
      if(o instanceof ImmutableContainer)
      {
         ((ImmutableContainer)o).addChild(valueBinding.getName().getLocalPart(), unmarshalled);
      }
      else
      {
         setFieldValue(valueBinding.getName(),
            valueBinding.getField(),
            valueBinding.getSetter(),
            o,
            unmarshalled
         );
      }
   }

   private static final void setFieldValue(QName elementName,
                                           Field field,
                                           Method setter,
                                           Object parent,
                                           Object child)
   {
      if(setter != null)
      {
         try
         {
            setter.invoke(parent, new Object[]{child});
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to set value (" +
               child.getClass().getName() +
               ":" +
               child +
               ") using setter " +
               setter.getName() +
               " in (" +
               parent.getClass() +
               ":" +
               parent + "): " + e.getMessage(), e
            );
         }
      }
      else if(field != null)
      {
         try
         {
            field.set(parent, child);
         }
         catch(IllegalAccessException e)
         {
            throw new JBossXBRuntimeException("Illegal access exception setting value (" +
               child.getClass() +
               ":" +
               child +
               ") using field " +
               field.getName() +
               " in (" +
               parent.getClass() +
               ":" +
               parent + "): " + e.getMessage(), e
            );
         }
      }
      else
      {
         throw new JBossXBRuntimeException("Element/attribute " +
            elementName +
            " is not bound to any field!"
         );
      }
   }

   private static final Object getFieldValue(ElementBinding metadata, Object parent)
   {
      Object value;
      if(metadata.getGetter() != null)
      {
         try
         {
            value = metadata.getGetter().invoke(parent, null);
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to get value using getter " +
               metadata.getGetter().getName() +
               " from " +
               parent.getClass() +
               ":" +
               parent + ": " + e.getMessage(), e
            );
         }
      }
      else if(metadata.getField() != null)
      {
         try
         {
            value = metadata.getField().get(parent);
         }
         catch(IllegalAccessException e)
         {
            throw new JBossXBRuntimeException("Illegal access exception getting value using field " +
               metadata.getField().getName() +
               " from " +
               parent.getClass() +
               ":" +
               parent + ": " + e.getMessage(), e
            );
         }
      }
      else
      {
         throw new JBossXBRuntimeException("Element " +
            metadata.getName() +
            " is not bound to any field!"
         );
      }

      return value;
   }

   private static final Object newInstance(XmlValueContainer metadata)
   {
      boolean trace = log.isTraceEnabled();

      Object instance;
      Class javaType = metadata.getJavaType();
      if(trace)
      {
         log.trace("newInstance " + javaType + " for " + metadata.getName());
      }
      try
      {
         Constructor ctor = javaType.getConstructor(null);
         instance = ctor.newInstance(null);
      }
      catch(NoSuchMethodException e)
      {
         instance = new ImmutableContainer(javaType);
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException(
            "Failed to create an instance of " + metadata.getName() + " of type " + metadata.getJavaType()
         );
      }
      if(trace)
      {
         log.trace("newInstance=" + instance);
      }
      return instance;
   }

   // Inner

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
            throw new IllegalStateException("Attempt to add duplicate element " +
               localName +
               ": prev value=" +
               values.get(values.size() - 1) +
               ", new value=" +
               child
            );
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
         return names.isEmpty() ?
            null :
            (names.get(names.size() - 1).equals(localName) ? values.get(values.size() - 1) : null);
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
