/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MetadataDrivenObjectModelFactory
   implements GenericObjectModelFactory
{
   //private static final Logger log = Logger.getLogger(MetadataDrivenObjectModelFactory.class);

   public Object newChild(Object parent,
                          UnmarshallingContext ctx,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
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
         Collection col = (Collection)getFieldValue(metadata, parent);
         if(col == null)
         {
            col = (Collection)newInstance(metadata);
            setFieldValue(metadata, parent, col);
         }
         child = col;
      }
      else if(!Util.isAttributeType(metadata.getJavaType()))
      {
         child = newInstance(metadata);
         if(parent instanceof Collection)
         {
            ((Collection)parent).add(child);
         }
         else if(metadata.getFieldType() != null && Collection.class.isAssignableFrom(metadata.getFieldType()))
         {
            Collection col = (Collection)getFieldValue(metadata, parent);
            if(col == null)
            {
               col = new ArrayList();
               setFieldValue(metadata, parent, col);
            }
            col.add(child);
         }
         else
         {
            setFieldValue(metadata, parent, child);
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
         Object unmarshalledValue = SimpleTypeBindings.unmarshal(value, metadata.getJavaType());

         if(o instanceof Collection)
         {
            ((Collection)o).add(unmarshalledValue);
         }
         else
         {
            if(Collection.class.isAssignableFrom(metadata.getFieldType()))
            {
               Collection col = (Collection)getFieldValue(metadata, o);
               if(col == null)
               {
                  col = new ArrayList();
                  setFieldValue(metadata, o, col);
               }
               col.add(unmarshalledValue);
            }
            else
            {
               setFieldValue(metadata, o, unmarshalledValue);
            }
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
      return root;
   }

   // Private

   private static final void setFieldValue(ElementBinding metadata, Object parent, Object child)
   {
      if(metadata.getSetter() != null)
      {
         try
         {
            metadata.getSetter().invoke(parent, new Object[]{child});
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to set value (" +
               child.getClass() +
               ":" +
               child +
               ") using setter " +
               metadata.getSetter().getName() +
               " in (" +
               parent.getClass() +
               ":" +
               parent + "): " + e.getMessage(), e
            );
         }
      }
      else if(metadata.getField() != null)
      {
         try
         {
            metadata.getField().set(parent, child);
         }
         catch(IllegalAccessException e)
         {
            throw new JBossXBRuntimeException("Illegal access exception setting value (" +
               child.getClass() +
               ":" +
               child +
               ") using field " +
               metadata.getSetter().getName() +
               " in (" +
               parent.getClass() +
               ":" +
               parent + "): " + e.getMessage(), e
            );
         }
      }
      else
      {
         throw new JBossXBRuntimeException("Element " +
            metadata.getElementName() +
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
            metadata.getElementName() +
            " is not bound to any field!"
         );
      }

      return value;
   }

   private static final Object newInstance(BasicElementBinding metadata)
   {
      try
      {
         return metadata.getJavaType().newInstance();
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException(
            "Failed to create an instance of " + metadata.getElementName() + " of type " + metadata.getJavaType()
         );
      }
   }
}
