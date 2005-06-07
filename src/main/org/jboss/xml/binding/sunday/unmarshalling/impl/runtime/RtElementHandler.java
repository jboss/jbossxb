/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.ArrayList;
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
import org.jboss.xml.binding.metadata.PackageMetaData;
import org.jboss.xml.binding.metadata.ClassMetaData;
import org.jboss.xml.binding.metadata.PropertyMetaData;
import org.jboss.xml.binding.metadata.MapEntryMetaData;
import org.jboss.xml.binding.metadata.PutMethodMetaData;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementHandler
   implements ElementHandler
{
   private static final Logger log = Logger.getLogger(RtElementHandler.class);

   public static final RtElementHandler INSTANCE = new RtElementHandler();

   public Object startElement(Object parent, QName elementName, ElementBinding element)
   {
      Object o = null;
      TypeBinding type = element.getType();
      if(!type.isSimple())
      {
         ClassMetaData classMetaData = element.getClassMetaData();
         if(classMetaData == null)
         {
            classMetaData = type.getClassMetaData();
         }

         if(classMetaData == null && type.isArrayWrapper())
         {
            if(parent == null)
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
                  itemCls = getClass(itemType.getClassMetaData(), item, type.getArrayItemQName());
               }

               o = GenericValueContainer.FACTORY.array(itemCls);
            }
            else
            {
               PropertyMetaData propertyMetaData = element.getPropertyMetaData();
               String jaxbPropName = propertyMetaData == null ? null : propertyMetaData.getName();

               String getterName = jaxbPropName == null ?
                  Util.xmlNameToGetMethodName(elementName.getLocalPart(), element.getSchema().isIgnoreLowLine()) :
                  "get" + jaxbPropName.charAt(0) + jaxbPropName.substring(1);
               Class parentClass = parent.getClass();
               Class fieldType;
               try
               {
                  Method getter = parentClass.getMethod(getterName, null);
                  fieldType = getter.getReturnType();
               }
               catch(NoSuchMethodException e)
               {
                  String fieldName = null;
                  try
                  {
                     fieldName = jaxbPropName == null ?
                        Util.xmlNameToFieldName(elementName.getLocalPart(), element.getSchema().isIgnoreLowLine()) :
                        jaxbPropName;
                     Field field = parentClass.getField(fieldName);
                     fieldType = field.getType();
                  }
                  catch(NoSuchFieldException e1)
                  {
                     throw new JBossXBRuntimeException("Failed to find field " +
                        fieldName +
                        " and getter " +
                        getterName +
                        " for element " +
                        elementName +
                        " in " +
                        parentClass
                     );
                  }
               }

               if(fieldType.isArray())
               {
                  o = GenericValueContainer.FACTORY.array(fieldType.getComponentType());
               }
               else if(Collection.class.isAssignableFrom(fieldType))
               {
                  o = new ArrayList();
               }
            }
         }
         else if(classMetaData == null && type.getMapEntryMetaData() != null)
         {
            o = new MapEntry();
         }
         else
         {
            Class cls = getClass(classMetaData, element, elementName);

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
                        type.getQName() + ": " + e.getMessage(), e
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

   public void attributes(Object o,
                          QName elementName,
                          ElementBinding element,
                          Attributes attrs,
                          NamespaceContext nsCtx)
   {
      TypeBinding type = element.getType();
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
                  RtUtil.set(o, attrName, value, element.getSchema().isIgnoreLowLine());
               }
            }
         }
      }
   }

   public Object endElement(Object o, QName elementName, ElementBinding element)
   {
      if(o instanceof GenericValueContainer)
      {
         o = ((GenericValueContainer)o).instantiate();
      }
      return o;
   }

   public void setParent(Object parent, Object o, QName qName, ElementBinding element)
   {
      if(parent instanceof GenericValueContainer)
      {
         ((GenericValueContainer)parent).addChild(qName, o);
      }
      else if(parent instanceof Collection)
      {
         ((Collection)parent).add(o);
      }
      else if(element.isMapEntryKey())
      {
         MapEntry mapEntry = (MapEntry)parent;
         mapEntry.setKey(o);
      }
      else if(element.isMapEntryValue())
      {
         try
         {
         MapEntry mapEntry = (MapEntry)parent;
         mapEntry.setValue(o);
         }
         catch(ClassCastException cce)
         {
            throw new JBossXBRuntimeException(
               "expected parent for " + qName + " was MapEntry but got: "
               + parent.getClass().getName() + ": " + parent
            );
         }
      }
      else
      {
         PutMethodMetaData putMethodMetaData = element.getPutMethodMetaData();
         if(putMethodMetaData != null)
         {
            MapEntryMetaData mapEntryMetaData = element.getMapEntryMetaData();
            if(mapEntryMetaData == null)
            {
               mapEntryMetaData = element.getType().getMapEntryMetaData();
               if(mapEntryMetaData == null)
               {
                  throw new JBossXBRuntimeException("putMethod is specified for element " +
                     qName +
                     " but mapEntry is specified for niether the element nor it's type " +
                     element.getType().getQName()
                  );
               }
            }

            Class oClass = o.getClass();
            String getKeyMethodName = mapEntryMetaData.getGetKeyMethod();
            if(getKeyMethodName == null)
            {
               getKeyMethodName = "getKey";
            }

            Method keyMethod;
            try
            {
               keyMethod = oClass.getMethod(getKeyMethodName, null);
            }
            catch(NoSuchMethodException e)
            {
               throw new JBossXBRuntimeException("setParent failed for " +
                  qName +
                  "=" +
                  o +
                  ": getKeyMethod=" +
                  getKeyMethodName +
                  " not found in " + oClass
               );
            }

            Object key;
            try
            {
               key = keyMethod.invoke(o, null);
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException("setParent failed for " +
                  qName +
                  "=" +
                  o +
                  ": getKeyMethod=" +
                  getKeyMethodName +
                  " threw an exception: " + e.getMessage(), e
               );
            }

            Class keyType = Object.class;
            if(putMethodMetaData.getKeyType() != null)
            {
               try
               {
                  keyType = Thread.currentThread().getContextClassLoader().loadClass(putMethodMetaData.getKeyType());
               }
               catch(ClassNotFoundException e)
               {
                  throw new JBossXBRuntimeException("setParent failed for " + qName + ": " + e.getMessage(), e);
               }
            }

            Class valueType = Object.class;
            if(putMethodMetaData.getValueType() != null)
            {
               try
               {
                  valueType =
                     Thread.currentThread().getContextClassLoader().loadClass(putMethodMetaData.getValueType());
               }
               catch(ClassNotFoundException e)
               {
                  throw new JBossXBRuntimeException("setParent failed for " + qName + ": " + e.getMessage(), e);
               }
            }

            Class parentClass = parent.getClass();
            String putMethodName = putMethodMetaData.getName();
            if(putMethodName == null)
            {
               putMethodName = "put";
            }

            Method putMethod;
            try
            {
               putMethod = parentClass.getMethod(putMethodName, new Class[]{keyType, valueType});
            }
            catch(NoSuchMethodException e)
            {
               throw new JBossXBRuntimeException("setParent failed for " +
                  qName +
                  "=" +
                  o +
                  ": putMethod=" +
                  putMethodName +
                  "(" + keyType.getName() + " key, " + valueType.getName() + " value) not found in " + parentClass
               );
            }

            Object value = o;
            String valueMethodName = mapEntryMetaData.getGetValueMethod();
            if(valueMethodName != null)
            {
               Method valueMethod;
               try
               {
                  valueMethod = oClass.getMethod(valueMethodName, null);
               }
               catch(NoSuchMethodException e)
               {
                  throw new JBossXBRuntimeException("setParent failed for " +
                     qName +
                     "=" +
                     o +
                     ": getValueMethod=" +
                     mapEntryMetaData.getGetValueMethod() +
                     " not found in " + oClass
                  );
               }

               try
               {
                  value = valueMethod.invoke(o, null);
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException("setParent failed for " +
                     qName +
                     "=" +
                     o +
                     ": getValueMethod=" +
                     mapEntryMetaData.getGetValueMethod() +
                     " threw an exception: " + e.getMessage(), e
                  );
               }
            }
            else if(o instanceof MapEntry)
            {
               value = ((MapEntry)o).getValue();
            }

            try
            {
               putMethod.invoke(parent, new Object[]{key, value});
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException(
                  "setParent failed for " +
                  qName +
                  "=" +
                  o +
                  ": putMethod=" +
                  putMethodName +
                  " threw an exception for parent=" +
                  parent +
                  ", key=" +
                  key +
                  ", value=" +
                  value +
                  ": " +
                  e.getMessage(),
                  e
               );
            }
         }
         else
         {
            PropertyMetaData propertyMetaData = element.getPropertyMetaData();
            String propName = propertyMetaData == null ? null : propertyMetaData.getName();
            if(propName == null)
            {
               propName = Util.xmlNameToFieldName(qName.getLocalPart(), element.getSchema().isIgnoreLowLine());
            }

            String colType = propertyMetaData == null ? null : propertyMetaData.getCollectionType();
            RtUtil.set(parent, o, propName, colType, true);
         }
      }
   }

   // Private

   private Class getClass(ClassMetaData classMetaData, ElementBinding element, QName elementName)
   {
      TypeBinding type = element.getType();
      String className = classMetaData == null ? null : classMetaData.getImpl();
      if(className == null)
      {
         QName typeBaseQName = type.getQName();
         if(typeBaseQName == null)
         {
            typeBaseQName = elementName;
         }

         SchemaBinding schemaBinding = type.getSchemaBinding();
         PackageMetaData jaxbPackage = schemaBinding == null ? null : schemaBinding.getPackageMetaData();
         String pkg = jaxbPackage == null ?
            Util.xmlNamespaceToJavaPackage(typeBaseQName.getNamespaceURI()) :
            jaxbPackage.getName();
         className = Util.xmlNameToClassName(typeBaseQName.getLocalPart(), element.getSchema().isIgnoreLowLine());
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
         if(classMetaData != null && classMetaData.getImpl() != null)
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
         else if(log.isTraceEnabled())
         {
            log.trace(
               "Failed to resolve class for element " + elementName + " of type " + type.getQName() + ": " + className
            );
         }
      }
      return cls;
   }
}
