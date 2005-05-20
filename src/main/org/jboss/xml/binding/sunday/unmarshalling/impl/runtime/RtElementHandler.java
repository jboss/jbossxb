/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Constructor;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xml.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Constants;
import org.jboss.xml.binding.SimpleTypeBindings;
import org.jboss.xml.binding.GenericValueContainer;
import org.jboss.xml.binding.metadata.JaxbPackage;
import org.jboss.xml.binding.metadata.JaxbProperty;
import org.jboss.xml.binding.metadata.JaxbClass;
import org.jboss.xml.binding.metadata.JaxbJavaType;
import org.jboss.xml.binding.metadata.JaxbBaseType;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementHandler
   implements ElementHandler
{
   public static final RtElementHandler INSTANCE = new RtElementHandler();

   public Object startElement(Object parent, QName elementName, TypeBinding type)
   {
      Object o = null;
      if(!type.isSimple())
      {
         JaxbClass jaxbClass = type.getJaxbClass();
         if(jaxbClass == null && type.isArrayWrapper())
         {
            ElementBinding item = type.getArrayItem();
            TypeBinding itemType = item.getType();

            Class itemCls;
            if(Constants.NS_XML_SCHEMA.equals(itemType.getQName().getNamespaceURI()))
            {
               itemCls = SimpleTypeBindings.classForType(itemType.getQName().getLocalPart());
            }
            else
            {
               itemCls = getClass(itemType, type.getArrayItemQName());
            }

            o = GenericValueContainer.FACTORY.array(itemCls);
         }
         else
         {
            Class cls = getClass(type, elementName);

            if(cls != null)
            {
               try
               {
                  Constructor ctor = cls.getConstructor(null);

                  try
                  {
                     o = ctor.newInstance(null);
                  }
                  catch(Exception e)
                  {
                     throw new JBossXBRuntimeException("Failed to create an instance of " +
                        cls +
                        " using default constructor for element " +
                        elementName +
                        " of type " +
                        type.getQName()
                     );
                  }
               }
               catch(NoSuchMethodException e)
               {
                  throw new JBossXBRuntimeException("" +
                     cls +
                     " doesn't declare no-arg constructor: element=" +
                     elementName +
                     ", type=" +
                     type.getQName()
                  );
               }
            }
         }
      }
      return o;
   }

   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         QName attrName = new QName(attrs.getURI(i), attrs.getLocalName(i));
         AttributeBinding binding = type.getAttribute(attrName);
         if(binding != null)
         {
            AttributeHandler handler = binding.getHandler();
            if(handler != null)
            {
               Object value = handler.unmarshal(elementName, attrName, binding, nsCtx, attrs.getValue(i));
               handler.attribute(elementName, attrName, binding, o, value);
            }
            else
            {
               throw new JBossXBRuntimeException(
                  "Attribute binding present but has no handler: element=" + elementName + ", attrinute=" + attrName
               );
            }
         }
         else
         {
            if(!Constants.NS_XML_SCHEMA_INSTANCE.equals(attrs.getURI(i)))
            {
               CharactersHandler simpleType = type.getSimpleType();
               Object value;
               if(simpleType == null)
               {
                  value = attrs.getValue(i);
               }
               else
               {
                  TypeBinding attrType = binding.getType();
                  JaxbJavaType jaxbJavaType = null;
                  JaxbProperty jaxbProperty = binding.getJaxbProperty();
                  if(jaxbProperty != null)
                  {
                     JaxbBaseType baseType = jaxbProperty.getBaseType();
                     jaxbJavaType = baseType == null ? null : baseType.getJavaType();
                  }
                  else if(attrType != null)
                  {
                     jaxbJavaType = attrType.getJaxbJavaType();
                  }
                  value = simpleType.unmarshal(attrName, attrType, nsCtx, jaxbJavaType, attrs.getValue(i));
               }

               RtUtil.set(o, attrName, value);
            }
         }
      }
   }

   public Object endElement(Object o, QName elementName, TypeBinding type)
   {
      if(o instanceof GenericValueContainer)
      {
         o = ((GenericValueContainer)o).instantiate();
      }
      return o;
   }

   public void setParent(Object parent, Object o, QName qName, ElementBinding element)
   {
      if(parent != null)
      {
         if(parent instanceof GenericValueContainer)
         {
            ((GenericValueContainer)parent).addChild(qName, o);
         }
         else
         {
            JaxbProperty jaxbProperty = element.getJaxbProperty();

            String propName = jaxbProperty == null ? null : jaxbProperty.getName();
            if(propName == null)
            {
               propName = Util.xmlNameToFieldName(qName.getLocalPart(), true);
            }

            String colType = jaxbProperty == null ? null : jaxbProperty.getCollectionType();
            RtUtil.set(parent, o, propName, colType, true);
         }
      }
   }

   // Private

   private Class getClass(TypeBinding type, QName elementName)
   {
      JaxbClass jaxbClass = type.getJaxbClass();
      String className = jaxbClass == null ? null : jaxbClass.getImplClass();
      if(className == null)
      {
         QName typeBaseQName = type.getQName();
         if(typeBaseQName == null)
         {
            typeBaseQName = elementName;
         }

         SchemaBinding schemaBinding = type.getSchemaBinding();
         JaxbPackage jaxbPackage = schemaBinding == null ? null : schemaBinding.getJaxbPackage();
         String pkg = jaxbPackage == null ?
            Util.xmlNamespaceToJavaPackage(typeBaseQName.getNamespaceURI()) :
            jaxbPackage.getName();
         className = Util.xmlNameToClassName(typeBaseQName.getLocalPart(), true);
         if(pkg != null && pkg.length() > 0)
         {
            className = pkg + '.' + className;
         }
      }

      Class cls = null;
      try
      {
         cls = Thread.currentThread().getContextClassLoader().loadClass(className);
      }
      catch(ClassNotFoundException e)
      {
         if(jaxbClass != null && jaxbClass.getImplClass() != null)
         {
            throw new JBossXBRuntimeException("Failed to resolve class name for " +
               elementName +
               " of type " +
               type.getQName() +
               ": " +
               e.getMessage()
            );
         }
         // todo complex element may contain just data content...
      }
      return cls;
   }
}
