/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.GenericValueContainer;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.group.ValueListInitializer;
import org.jboss.xb.binding.group.ValueList;
import org.jboss.xb.binding.group.ValueListHandler;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
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
      if(element.isSkip())
      {
         return parent;
      }

      Object o = null;
      TypeBinding type = element.getType();
      if(type.isStartElementCreatesObject())
      {
         ClassMetaData classMetaData = element.getClassMetaData();
         MapEntryMetaData mapEntryMetaData = element.getMapEntryMetaData();

         // todo: if addMethod is specified, it's probably some collection field
         // but should not be set as a property. Instead, items are added to it using the addMethod
         if(classMetaData == null && type.isArrayWrapper() && element.getAddMethodMetaData() == null)
         {
            if(parent == null)
            {
               ElementBinding item = type.getArrayItem();
               TypeBinding itemType = item.getType();

               Class itemCls;
               QName itemTypeQName = itemType.getQName();
               if(itemTypeQName != null && Constants.NS_XML_SCHEMA.equals(itemTypeQName.getNamespaceURI()))
               {
                  itemCls = SimpleTypeBindings.classForType(itemType.getQName().getLocalPart(), item.isNillable());
               }
               else
               {
                  ClassMetaData itemClsMetaData = itemType.getClassMetaData();
                  String itemClsName = itemClsMetaData == null ? null : itemClsMetaData.getImpl();
                  itemCls = getClass(itemClsName, item, type.getArrayItemQName());
               }

               if(itemCls != null)
               {
                  o = GenericValueContainer.FACTORY.array(itemCls);
               }
            }
            else
            {
               PropertyMetaData propertyMetaData = element.getPropertyMetaData();
               String propName = propertyMetaData == null ? null : propertyMetaData.getName();

               String getterName = propName == null ?
                  Util.xmlNameToGetMethodName(elementName.getLocalPart(), element.getSchema().isIgnoreLowLine()) :
                  "get" + propName.charAt(0) + propName.substring(1);

               Class parentClass;
               if(parent instanceof GenericValueContainer)
               {
                  parentClass = ((GenericValueContainer)parent).getTargetClass();
               }
               else if(parent instanceof ValueList)
               {
                  parentClass = ((ValueList)parent).getTargetClass();
               }
               else
               {
                  parentClass = parent.getClass();
               }

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
                     fieldName = propName == null ?
                        Util.xmlNameToFieldName(elementName.getLocalPart(), element.getSchema().isIgnoreLowLine()) :
                        propName;
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
         else
         {
            if(mapEntryMetaData != null)
            {
               if(mapEntryMetaData.getImpl() != null)
               {
                  Class cls = getClass(mapEntryMetaData.getImpl(), element, elementName);
                  o = newInstance(cls, elementName, type);
               }
               else
               {
                  o = new MapEntry();
               }

               if(mapEntryMetaData.isNonNullValue() && mapEntryMetaData.getValueType() != null)
               {
                  Class mapValueType;
                  try
                  {
                     mapValueType =
                        Thread.currentThread().getContextClassLoader().loadClass(mapEntryMetaData.getValueType());
                  }
                  catch(ClassNotFoundException e)
                  {
                     throw new JBossXBRuntimeException("startElement failed for " +
                        elementName +
                        ": failed to load class " +
                        mapEntryMetaData.getValueType() +
                        " for map entry value."
                     );
                  }

                  Object value;
                  try
                  {
                     value = mapValueType.newInstance();
                  }
                  catch(Exception e)
                  {
                     throw new JBossXBRuntimeException("startElement failed for " +
                        elementName +
                        ": failed to create an instance of " +
                        mapValueType +
                        " for map entry value."
                     );
                  }

                  if(o instanceof MapEntry)
                  {
                     ((MapEntry)o).setValue(value);
                  }
                  else
                  {
                     String getValueMethodName = mapEntryMetaData.getGetValueMethod();
                     if(getValueMethodName == null)
                     {
                        getValueMethodName = "getValue";
                     }

                     String setValueMethodName = mapEntryMetaData.getSetValueMethod();
                     if(setValueMethodName == null)
                     {
                        setValueMethodName = "setValue";
                     }

                     Method getValueMethod;
                     try
                     {
                        getValueMethod = o.getClass().getMethod(getValueMethodName, null);
                     }
                     catch(NoSuchMethodException e)
                     {
                        throw new JBossXBRuntimeException("getValueMethod=" +
                           getValueMethodName +
                           " is not found in map entry " + o.getClass()
                        );
                     }

                     Method setValueMethod;
                     try
                     {
                        setValueMethod =
                           o.getClass().getMethod(setValueMethodName, new Class[]{getValueMethod.getReturnType()});
                     }
                     catch(NoSuchMethodException e)
                     {
                        throw new JBossXBRuntimeException("setValueMethod=" +
                           setValueMethodName +
                           "(" +
                           getValueMethod.getReturnType().getName() +
                           " value) is not found in map entry " + o.getClass()
                        );
                     }

                     try
                     {
                        setValueMethod.invoke(o, new Object[]{value});
                     }
                     catch(Exception e)
                     {
                        throw new JBossXBRuntimeException("setValueMethod=" +
                           setValueMethodName +
                           " failed: owner=" +
                           o +
                           ", value=" + value + ", msg=" + e.getMessage(), e
                        );
                     }
                  }
               }
            }
            else
            {
               String clsName = classMetaData == null ? null : classMetaData.getImpl();
               Class cls = getClass(clsName, element, elementName);
               if(cls != null)
               {
                  o = newInstance(cls, elementName, type);
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
      if(element.isSkip())
      {
         return o;
      }

      if(o instanceof GenericValueContainer)
      {
         try
         {
            o = ((GenericValueContainer)o).instantiate();
         }
         catch(RuntimeException e)
         {
            throw new JBossXBRuntimeException("Container failed to create an instance for " +
               elementName +
               ": " + e.getMessage(), e
            );
         }
      }
      else if(o instanceof ValueList)
      {
         ValueList valueList = (ValueList)o;
         o = valueList.getHandler().newInstance(valueList);
      }

      return o;
   }

   public void setParent(Object parent, Object o, QName qName, ElementBinding element, ElementBinding parentElement)
   {
      if(element.isSkip())
      {
         return;
      }

      if(element.isMapEntryKey())
      {
         if(parent instanceof MapEntry)
         {
            MapEntry mapEntry = (MapEntry)parent;
            mapEntry.setKey(o);
         }
         else if(parentElement != null)
         {
            MapEntryMetaData mapEntryMetaData = getMapEntryMetaData(parentElement, qName);

            String getKeyMethodName = mapEntryMetaData.getGetKeyMethod();
            if(getKeyMethodName == null)
            {
               getKeyMethodName = "getKey";
            }

            String setKeyMethodName = mapEntryMetaData.getSetKeyMethod();
            if(setKeyMethodName == null)
            {
               setKeyMethodName = "setKey";
            }

            Class parentCls = parent.getClass();
            Method setKeyMethod = getSetMethod(parentCls, getKeyMethodName, setKeyMethodName);
            invokeSetter(setKeyMethod, parent, o, setKeyMethodName);
         }
         else
         {
            throw new JBossXBRuntimeException(
               "Element " +
                  qName +
                  " bound as map entry key but parent element is not recognized as map entry and its metadata is not available."
            );
         }
      }
      else if(element.isMapEntryValue())
      {
         if(parent instanceof MapEntry)
         {
            MapEntry mapEntry = (MapEntry)parent;
            mapEntry.setValue(o);
         }
         else if(parentElement != null)
         {
            MapEntryMetaData mapEntryMetaData = getMapEntryMetaData(parentElement, qName);
            setMapEntryValue(mapEntryMetaData, parent, o);
         }
         else
         {
            throw new JBossXBRuntimeException(
               "Element " +
                  qName +
                  " bound as map entry key but parent element is not recognized as map entry and its metadata is not available."
            );
         }
      }
      else
      {
         Object owner = parent;
         if(parent instanceof MapEntry)
         {
            MapEntry mapEntry = (MapEntry)parent;
            owner = mapEntry.getValue();
            if(owner == null)
            {
               if(parentElement == null)
               {
                  throw new JBossXBRuntimeException(
                     "Binding metadata needed for lazy map entry value instantiation is not available " +
                        "for parent element of element " +
                        qName
                  );
               }

               MapEntryMetaData mapEntryMetaData = getMapEntryMetaData(parentElement, qName);
               String valueType = mapEntryMetaData.getValueType();
               if(valueType == null)
               {
                  throw new JBossXBRuntimeException("Element " +
                     qName +
                     " of type " +
                     element.getType() +
                     " is supposed to be bound as map entry value with lazy value instantiation " +
                     "but value type is not specified in its map entry metadata."
                  );
               }

               Class valueCls;
               try
               {
                  valueCls = Thread.currentThread().getContextClassLoader().loadClass(valueType);
               }
               catch(ClassNotFoundException e)
               {
                  throw new JBossXBRuntimeException(
                     "Failed to load value type specified in the map entry metadata: " + valueType
                  );
               }

               try
               {
                  owner = valueCls.newInstance();
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException(
                     "Failed to create an instance of value type " + valueType + ": " + e.getMessage()
                  );
               }

               setMapEntryValue(mapEntryMetaData, parent, owner);
            }
         }

         if(element.getPutMethodMetaData() != null ||
            element.getMapEntryMetaData() != null && owner instanceof Map)
         {
            invokePut(qName, element, owner, o);
         }
         else if(element.getAddMethodMetaData() != null)
         {
            invokeAdd(qName, element, owner, o);
         }
         else
         {
            PropertyMetaData propertyMetaData = null;
            if(parentElement != null && parentElement.getType().isWildcardElement(qName))
            {
               propertyMetaData = parentElement.getType().getWildcardPropertyMetaData();
            }

            if(propertyMetaData == null)
            {
               propertyMetaData = element.getPropertyMetaData();
            }

            /*
            if(propertyMetaData == null)
            {
               propertyMetaData = element.getType().getPropertyMetaData();
            }
            */

            if(propertyMetaData != null)
            {
               String propName = propertyMetaData.getName();
               if(propName == null)
               {
                  propName = Util.xmlNameToFieldName(qName.getLocalPart(), element.getSchema().isIgnoreLowLine());
               }

               RtUtil.set(owner, o, propName, propertyMetaData.getCollectionType(),
                  element.getSchema().isIgnoreUnresolvedFieldOrClass(),
                  element.getValueAdapter()
               );
            }
            else if(owner instanceof GenericValueContainer)
            {
               ((GenericValueContainer)owner).addChild(qName, o);
            }
            else if(owner instanceof ValueList)
            {
               ValueList valueList = (ValueList)owner;
               ValueListInitializer initializer = valueList.getInitializer();
               if(element.isMultiOccurs())
               {
                  Collection col = (Collection)initializer.getElementValue(qName, valueList);
                  if(col == null)
                  {
                     col = new ArrayList();
                     initializer.addElementValue(qName, element, valueList, col);
                  }
                  col.add(o);
               }
               else
               {
                  initializer.addElementValue(qName, element, valueList, o);
               }
            }
            else if(owner instanceof Collection)
            {
               ((Collection)owner).add(o);
            }
            else
            {
               // no metadata available
               String propName = Util.xmlNameToFieldName(qName.getLocalPart(), element.getSchema().isIgnoreLowLine());
               RtUtil.set(owner, o, propName, null,
                  element.getSchema().isIgnoreUnresolvedFieldOrClass(),
                  element.getValueAdapter()
               );
            }
         }
      }
   }

   // Private

   private static void setMapEntryValue(MapEntryMetaData mapEntryMetaData, Object parent, Object o)
   {
      String getValueMethodName = mapEntryMetaData.getGetValueMethod();
      if(getValueMethodName == null)
      {
         getValueMethodName = "getValue";
      }

      String setValueMethodName = mapEntryMetaData.getSetValueMethod();
      if(setValueMethodName == null)
      {
         setValueMethodName = "setValue";
      }

      Class parentCls = parent.getClass();
      Method setValueMethod = getSetMethod(parentCls, getValueMethodName, setValueMethodName);
      invokeSetter(setValueMethod, parent, o, setValueMethodName);
   }

   private static void invokeSetter(Method setValueMethod, Object parent, Object o, String setValueMethodName)
   {
      try
      {
         setValueMethod.invoke(parent, new Object[]{o});
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Failed to invoke " +
            setValueMethodName +
            " on " +
            parent +
            " with parameter " +
            o +
            ": " +
            e.getMessage()
         );
      }
   }

   private static Method getSetMethod(Class cls, String getMethodName, String setMethodName)
   {
      Method getKeyMethod;
      try
      {
         getKeyMethod = cls.getMethod(getMethodName, null);
      }
      catch(NoSuchMethodException e)
      {
         throw new JBossXBRuntimeException("Method " + getMethodName + " not found in " + cls);
      }

      Method setKeyMethod;
      try
      {
         setKeyMethod = cls.getMethod(setMethodName, new Class[]{getKeyMethod.getReturnType()});
      }
      catch(NoSuchMethodException e)
      {
         throw new JBossXBRuntimeException("Method " +
            setMethodName +
            "(" +
            getKeyMethod.getReturnType().getName() +
            " p) not found in " +
            cls
         );
      }
      return setKeyMethod;
   }

   private static MapEntryMetaData getMapEntryMetaData(ElementBinding element, QName qName)
   {
      MapEntryMetaData mapEntryMetaData = element.getMapEntryMetaData();
      if(mapEntryMetaData == null)
      {
         throw new JBossXBRuntimeException("Element " +
            qName +
            " bound as map entry key or value but map entry metadata is not available for its parent element nor its " +
            (element.getType().getQName() == null ?
               "annonymous" :
               element.getType().getQName().toString()
            ) +
            " type."
         );
      }
      return mapEntryMetaData;
   }

   private static Object newInstance(Class cls, QName elementName, TypeBinding type)
   {
      Object o;
      try
      {
         if(cls.isArray())
         {
            o = GenericValueContainer.FACTORY.array(cls.getComponentType());
         }
         else
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
      }
      catch(NoSuchMethodException e)
      {
         o = new ValueListInitializer().newValueList(ValueListHandler.IMMUTABLE, cls);
      }
      return o;
   }

   private static Class getClass(String className, ElementBinding element, QName elementName)
   {
      TypeBinding type = element.getType();
      String localClassName = className;
      if(localClassName == null)
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
         localClassName = Util.xmlNameToClassName(typeBaseQName.getLocalPart(), element.getSchema().isIgnoreLowLine());
         if(pkg != null && pkg.length() > 0)
         {
            localClassName = pkg + '.' + localClassName;
         }
      }

      Class cls = null;
      try
      {
         cls = Thread.currentThread().getContextClassLoader().loadClass(localClassName);
      }
      catch(ClassNotFoundException e)
      {
         if(element.getSchema().isIgnoreUnresolvedFieldOrClass())
         {
            if(log.isTraceEnabled())
            {
               log.trace("Failed to resolve class for element " +
                  elementName +
                  " of type " +
                  type.getQName() +
                  ": " +
                  localClassName
               );
            }
         }
         else
         {
            throw new JBossXBRuntimeException("Failed to resolve class name for " +
               elementName +
               " of type " +
               type.getQName() +
               ": " +
               e.getMessage()
            );
         }
      }
      return cls;
   }

   private void invokeAdd(QName qName, ElementBinding element, Object owner, Object o)
   {
      AddMethodMetaData addMethodMetaData = element.getAddMethodMetaData();
      Class valueType = Object.class;
      if(addMethodMetaData.getValueType() != null)
      {
         try
         {
            valueType = Thread.currentThread().getContextClassLoader().
               loadClass(addMethodMetaData.getValueType());
         }
         catch(ClassNotFoundException e)
         {
            throw new JBossXBRuntimeException("Failed to load value type for addMethod.name=" +
               addMethodMetaData.getMethodName() +
               ", valueType=" +
               addMethodMetaData.getValueType() +
               ": " + e.getMessage(), e
            );
         }
      }
      else if(addMethodMetaData.isChildType())
      {
         if(o == null)
         {
            throw new JBossXBRuntimeException(
               "addMethod=" +
                  addMethodMetaData.getMethodName() +
                  " for element " + qName +
                  " is configured with valueType='child'. The valueType cannot be determined because" +
                  " the child is null"
            );
         }
         valueType = o.getClass();
      }

      Class ownerClass = owner.getClass();
      Method addMethod;
      try
      {
         addMethod = ownerClass.getMethod(addMethodMetaData.getMethodName(), new Class[]{valueType});
      }
      catch(NoSuchMethodException e)
      {
         throw new JBossXBRuntimeException("Failed to find addMethod.name=" +
            addMethodMetaData.getMethodName() +
            ", addMethod.valueType=" +
            valueType.getName() +
            " in class " +
            ownerClass.getName() +
            ": " +
            e.getMessage(), e
         );
      }

      try
      {
         addMethod.invoke(owner, new Object[]{o});
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("setParent failed for " +
            qName +
            "=" +
            o +
            ": addMethod=" +
            addMethodMetaData.getMethodName() +
            " threw an exception for owner=" +
            owner +
            ", value=" +
            o +
            ": " +
            e.getMessage(),
            e
         );
      }
   }

   private void invokePut(QName qName, ElementBinding element, Object owner, Object o)
   {
      PutMethodMetaData putMethodMetaData = element.getPutMethodMetaData();

      MapEntryMetaData mapEntryMetaData = element.getMapEntryMetaData();
      if(mapEntryMetaData == null)
      {
         throw new JBossXBRuntimeException((putMethodMetaData == null ?
            "Parent object is an instance of java.util.Map" :
            "putMethod is specified for element " + qName
         ) +
            " but mapEntry is specified for neither element " +
            qName +
            " nor it's type " +
            element.getType().getQName()
         );
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
      Class valueType = Object.class;
      String putMethodName = "put";
      Class ownerClass = owner.getClass();

      if(putMethodMetaData != null)
      {
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

         if(putMethodMetaData.getValueType() != null)
         {
            try
            {
               valueType = Thread.currentThread().getContextClassLoader().loadClass(putMethodMetaData.getValueType());
            }
            catch(ClassNotFoundException e)
            {
               throw new JBossXBRuntimeException("setParent failed for " + qName + ": " + e.getMessage(), e);
            }
         }

         String name = putMethodMetaData.getName();
         if(name != null)
         {
            putMethodName = name;
         }
      }

      Method putMethod;
      try
      {
         putMethod = ownerClass.getMethod(putMethodName, new Class[]{keyType, valueType});
      }
      catch(NoSuchMethodException e)
      {
         throw new JBossXBRuntimeException("setParent failed for " +
            qName +
            "=" +
            o +
            ": putMethod=" +
            putMethodName +
            "(" + keyType.getName() + " key, " + valueType.getName() + " value) not found in " + ownerClass
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
         putMethod.invoke(owner, new Object[]{key, value});
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("setParent failed for " +
            qName +
            "=" +
            o +
            ": putMethod=" +
            putMethodName +
            " threw an exception for owner=" +
            owner +
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
}
