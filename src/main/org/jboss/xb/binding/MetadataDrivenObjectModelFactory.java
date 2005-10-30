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

import org.jboss.xb.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.XmlValueBinding;
import org.jboss.xb.binding.metadata.unmarshalling.XmlValueContainer;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
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
         if(parent instanceof Immutable)
         {
            Immutable imm = (Immutable)parent;
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
         if(!(child instanceof Immutable))
         {
            if(parent instanceof Collection)
            {
               ((Collection)parent).add(child);
            }
            else if(parent instanceof Immutable)
            {
               ((Immutable)parent).addChild(localName, child);
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

                  if(child instanceof Immutable)
                  {
                     ((Immutable)child).addChild(attrName.getLocalPart(), unmarshalledValue);
                  }
                  else
                  {
                     setFieldValue(attrBinding.getAttributeName(),
                        attrBinding.getField(),
                        attrBinding.getSetter(),
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
      if(child instanceof Immutable)
      {
         ElementBinding metadata = (ElementBinding)ctx.getMetadata();

         child = ((Immutable)child).newInstance();
         if(parent instanceof Collection)
         {
            ((Collection)parent).add(child);
         }
         else if(metadata.getFieldType() == null || Collection.class.isAssignableFrom(metadata.getFieldType()))
         {
            Collection col;
            if(parent instanceof Immutable)
            {
               Immutable imm = (Immutable)parent;
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
         else if(parent instanceof Immutable)
         {
            ((Immutable)parent).addChild(localName, child);
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
            else if(o instanceof Immutable)
            {
               ((Immutable)o).addChild(localName, unmarshalledValue);
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
         BasicElementBinding metadata = (BasicElementBinding)ctx.getMetadata();
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
      return root instanceof Immutable ? ((Immutable)root).newInstance() : root;
   }

   // Private

   private static void unmarshalValue(XmlValueBinding valueBinding, String value, Object o)
   {
      Object unmarshalled;
      if(valueBinding.getValue() != null)
      {
         unmarshalled = newInstance(valueBinding);
         unmarshalValue(valueBinding.getValue(), value, unmarshalled);

         if(unmarshalled instanceof Immutable)
         {
            unmarshalled = ((Immutable)unmarshalled).newInstance();
         }
      }
      else
      {
         unmarshalled = SimpleTypeBindings.unmarshal(value, valueBinding.getJavaType());
      }

      // todo o instanceof java.util.Collection?
      if(o instanceof Immutable)
      {
         ((Immutable)o).addChild(valueBinding.getName().getLocalPart(), unmarshalled);
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
         instance = new Immutable(javaType);
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
}
