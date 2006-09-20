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
package org.jboss.xb.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.GenericValueContainer;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.introspection.FieldInfo;
import org.jboss.xb.binding.group.ValueList;
import org.jboss.xb.binding.group.ValueListHandler;
import org.jboss.xb.binding.group.ValueListInitializer;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementHandler
   implements ParticleHandler
{
   private static final Logger log = Logger.getLogger(RtElementHandler.class);

   public static final RtElementHandler INSTANCE = new RtElementHandler();
   
   // ParticleHandler impl

   /**
    * TODO: it seems like for correct type resolution in startParticle
    * I should take into account the way the object is going to be added
    * to the parent in setParent (and, hence, do some steps that are done in setParticle).
    * In setParent then I should reuse the results of what has been done in startParticle.
    */
   public Object startParticle(Object parent,
                               QName elementName,
                               ParticleBinding particle,
                               Attributes attrs,
                               NamespaceContext nsCtx)
   {
      TermBinding term = particle.getTerm();
      Object o = startElement(parent, elementName, particle);
      if(!term.isModelGroup())
      {
         ElementBinding element = (ElementBinding)term;
         if(o != null)
         {
            attrs = element.getType().expandWithDefaultAttributes(attrs);
            attributes(o, elementName, element, attrs, nsCtx);
         }
      }
      return o;
   }

   public void setParent(Object parent,
                         Object o,
                         QName qName,
                         ParticleBinding particle,
                         ParticleBinding parentParticle)
   {
      TermBinding term = particle.getTerm();
      if(term.isSkip())
      {
         return;
      }

      boolean trace = log.isTraceEnabled();
      if(trace)
      {
         log.trace("setParent " + qName + " parent=" + parent + " object=" + o + " term=" + term);
      }

      TermBinding parentTerm = parentParticle.getTerm();

      if(term.isMapEntryKey())
      {
         if(trace)
         {
            log.trace("setParent " + qName + " mapKey");
         }

         if(parent instanceof MapEntry)
         {
            MapEntry mapEntry = (MapEntry)parent;
            mapEntry.setKey(o);
         }
         else if(parentTerm != null)
         {
            MapEntryMetaData mapEntryMetaData = getMapEntryMetaData(parentTerm, qName);

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
      else if(term.isMapEntryValue())
      {
         if(trace)
         {
            log.trace("setParent " + qName + " mapValue");
         }

         if(parent instanceof MapEntry)
         {
            MapEntry mapEntry = (MapEntry)parent;
            mapEntry.setValue(o);
         }
         else if(parentTerm != null)
         {
            MapEntryMetaData mapEntryMetaData = getMapEntryMetaData(parentTerm, qName);
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
            if(trace)
            {
               log.trace("setParent " + qName + " mapEntry");
            }

            MapEntry mapEntry = (MapEntry)parent;
            owner = mapEntry.getValue();
            if(owner == null)
            {
               if(parentTerm == null)
               {
                  throw new JBossXBRuntimeException("Binding metadata needed for lazy map entry value instantiation is not available " +
                     "for parent element of element " +
                     qName
                  );
               }

               MapEntryMetaData mapEntryMetaData = getMapEntryMetaData(parentTerm, qName);
               String valueType = mapEntryMetaData.getValueType();
               if(valueType == null)
               {
                  throw new JBossXBRuntimeException("Element " +
                     qName +
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

         // the wildcard this element is a content of
         WildcardBinding wildcard = null;
         if(parentTerm != null && !parentTerm.isModelGroup())
         {
            ElementBinding parentElement = (ElementBinding)parentTerm;
            TypeBinding parentType = parentElement.getType();
            wildcard = parentType.getWildcard();
            // there should be a better way of checking this
            if(wildcard != null && parentType.getElement(qName) != null)
            {
               wildcard = null;
            }
         }

         if(tryPut(owner, o, qName, term, trace))
         {
         }
         else if(tryAdd(owner, o, qName, term, wildcard, trace))
         {
         }
         else
         {
            PropertyMetaData propertyMetaData = wildcard == null ? null : wildcard.getPropertyMetaData();
            if(propertyMetaData == null)
            {
               propertyMetaData = term.getPropertyMetaData();
            }

            /*
            if(propertyMetaData == null)
            {
               propertyMetaData = element.getType().getPropertyMetaData();
            }
            */

            if(owner instanceof GenericValueContainer)
            {
               if(trace)
               {
                  log.trace("setParent " + qName + " addChild");
               }
               ((GenericValueContainer)owner).addChild(qName, o);
            }
            else if(owner instanceof Collection)
            {
               if(trace)
               {
                  log.trace("setParent " + qName + " collection.add()");
               }
               ((Collection)owner).add(o);
            }
            else
            {
               String propName = null;
               String colType = null;
               if(propertyMetaData != null)
               {
                  propName = propertyMetaData.getName();
                  colType = propertyMetaData.getCollectionType();
               }

               if(propName == null)
               {
                  propName = Util.xmlNameToFieldName(qName.getLocalPart(), term.getSchema().isIgnoreLowLine());
               }

               if(trace)
               {
                  log.trace("setParent " + qName + " metadata set " + propName);
               }

/*               if(particle.isRepeatable())
               {
                  RtUtil.add(owner, o, propName, colType,
                     term.getSchema().isIgnoreUnresolvedFieldOrClass(),
                     term.getValueAdapter()
                  );
               }
               else
               {
*/                  RtUtil.set(owner, o, propName, colType,
                     term.getSchema().isIgnoreUnresolvedFieldOrClass(),
                     term.getValueAdapter()
                  );
//               }
            }
         }
      }
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      TermBinding term = particle.getTerm();
      if(term.isSkip())
      {
         return o;
      }

      boolean trace = log.isTraceEnabled();
      if(trace)
      {
         log.trace("endParticle " + elementName + " object=" + o + " term=" + term);
      }

      if(o instanceof GenericValueContainer)
      {
         try
         {
            if(trace)
            {
               log.trace("endParticle " + elementName + " instantiate()");
            }
            o = ((GenericValueContainer)o).instantiate();
         }
         catch(JBossXBRuntimeException e)
         {
            throw e;
         }
         catch(RuntimeException e)
         {
            throw new JBossXBRuntimeException("Container failed to create an instance for " +
               elementName +
               ": " + e.getMessage(), e
            );
         }
      }

      return o;
   }

   // Private

   private Object startElement(Object parent, QName elementName, ParticleBinding particle)
   {
      TermBinding term = particle.getTerm();
      if(term.isSkip())
      {
         return parent;
      }

      boolean trace = log.isTraceEnabled();
      if(trace)
      {
         log.trace("startElement " + elementName + " parent=" + parent + " term=" + term);
      }

      ClassMetaData classMetaData = term.getClassMetaData();
      MapEntryMetaData mapEntryMetaData = term.getMapEntryMetaData();

      if(!term.isModelGroup())
      {
         TypeBinding type = ((ElementBinding)term).getType();
         if(!type.isStartElementCreatesObject() ||
            classMetaData == null && mapEntryMetaData == null && Constants.QNAME_ANYTYPE.equals(type.getQName()))
         {
            if(trace)
            {
               log.trace("startElement " + elementName + " does not create an object");
            }
            return null;
         }
      }

      Object o = null;

      // if addMethod is specified, it's probably some collection field
      // but should not be set as a property. Instead, items are added to it using the addMethod
      ElementBinding arrayItem = null;
      if(!term.isModelGroup())
      {
         TypeBinding type = ((ElementBinding)term).getType();
         if(type.getAttributes().isEmpty())
         {
            ParticleBinding typeParticle = type.getParticle();
            ModelGroupBinding modelGroup = (ModelGroupBinding)(typeParticle == null ? null : typeParticle.getTerm());
            arrayItem = modelGroup == null ? null : modelGroup.getArrayItem();

            // todo refactor later (move it to modelGroup.getArrayItem()?)
            if(arrayItem != null &&
               (arrayItem.isSkip() ||
               arrayItem.getMapEntryMetaData() != null ||
               arrayItem.getPutMethodMetaData() != null ||
               arrayItem.getAddMethodMetaData() != null
               ))
            {
               arrayItem = null;
            }
         }
      }

      if(arrayItem != null)
      {
         Class wrapperType = null;
         if(classMetaData != null)
         {
            wrapperType = loadClassForTerm(classMetaData.getImpl(),
               term.getSchema().isIgnoreUnresolvedFieldOrClass(),
               elementName
            );

            if(GenericValueContainer.class.isAssignableFrom(wrapperType) ||
               Collection.class.isAssignableFrom(wrapperType) ||
               Map.class.isAssignableFrom(wrapperType))
            {
               return newInstance(wrapperType, elementName, term.getSchema().isUseNoArgCtorIfFound());
            }
         }

         if(wrapperType == null && parent == null)
         {
            Class itemType = classForElement(arrayItem, null);
            if(itemType != null)
            {
               if(trace)
               {
                  log.trace("startElement " + elementName + " new array " + itemType.getName());
               }
               o = GenericValueContainer.FACTORY.array(itemType);
            }
         }
         else
         {
            PropertyMetaData propertyMetaData = wrapperType == null ?
               term.getPropertyMetaData() : arrayItem.getPropertyMetaData();

            String propName;
            if(propertyMetaData == null)
            {
               propName = Util.xmlNameToFieldName(
                  wrapperType == null ? elementName.getLocalPart() : arrayItem.getQName().getLocalPart(),
                  term.getSchema().isIgnoreLowLine()
               );
            }
            else
            {
               propName = propertyMetaData.getName();
            }

            if(trace)
            {
               log.trace("startElement " + elementName + " property=" + propName);
            }

            Class parentClass = wrapperType;
            if(wrapperType == null)
            {
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
            }

            Class fieldType;
            if(parentClass.isArray())
            {
               fieldType = parentClass.getComponentType();
            }
            else
            {
               fieldType = FieldInfo.getFieldInfo(parentClass, propName, true).getType();
               if(particle.isRepeatable() && fieldType.isArray())
               {
                  fieldType = fieldType.getComponentType();
               }
            }

            if(fieldType.isArray())
            {
               o = GenericValueContainer.FACTORY.array(wrapperType, propName, fieldType.getComponentType());
            }
            else if(Collection.class.isAssignableFrom(fieldType))
            {
               //System.out.println("GeenericValueContainer.child: " + elementName);
               o = new ValueListInitializer().newValueList(ValueListHandler.FACTORY.child(), Collection.class);
               //o = new ArrayList();
            }
            else
            {
               o = GenericValueContainer.FACTORY.array(wrapperType, propName, fieldType);
            }
         }
      }
      else
      {
         if(mapEntryMetaData != null)
         {
            if(mapEntryMetaData.getImpl() != null)
            {
               Class cls = loadClassForTerm(mapEntryMetaData.getImpl(),
                  term.getSchema().isIgnoreUnresolvedFieldOrClass(),
                  elementName
               );

               if(trace)
               {
                  log.trace("startElement " + elementName + " new map entry " + cls.getName());
               }

               o = newInstance(cls, elementName, term.getSchema().isUseNoArgCtorIfFound());
            }
            else
            {
               o = new MapEntry();
               if(trace)
               {
                  log.trace("startElement " + elementName + " new map entry");
               }
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
                  if(trace)
                  {
                     log.trace("startElement " + elementName + " map value type " + mapEntryMetaData.getValueType());
                  }
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
            // todo: for now we require metadata for model groups to be bound
            // todo 2: parent.getClass() is not going to work for containers
            Class parentClass = null;
            if(parent != null)
            {
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
            }

            Class cls;
            if(term.isModelGroup())
            {
               if(classMetaData == null)
               {
                  throw new JBossXBRuntimeException(
                     "Model groups should be annotated with 'class' annotation to be bound."
                  );
               }
               cls = loadClassForTerm(classMetaData.getImpl(),
                  term.getSchema().isIgnoreUnresolvedFieldOrClass(),
                  elementName
               );
            }
            else
            {
               ElementBinding element = (ElementBinding)term;
               cls = classForNonArrayItem(element, parentClass);
               if(cls != null)
               {
                  // todo: before that, the type should be checked for required attributes and elements
                  TypeBinding simpleType = element.getType().getSimpleType();
                  if(simpleType != null)
                  {
                     Class simpleCls = classForSimpleType(simpleType, element.isNillable());
                     if(cls.equals(simpleCls) ||
                        cls.isPrimitive() && Classes.getPrimitiveWrapper(cls) == simpleCls ||
                        simpleCls.isPrimitive() && Classes.getPrimitiveWrapper(simpleCls) == cls)
                     {
                        cls = null;
                     }
                  }
               }
            }

            if(cls != null)
            {
               boolean noArgCtor;
               if(classMetaData == null)
               {
                  noArgCtor = term.getSchema().isUseNoArgCtorIfFound();
               }
               else
               {
                  Boolean termUsesNoArgCtor = classMetaData.isUseNoArgCtor();
                  noArgCtor = termUsesNoArgCtor == null ?
                     term.getSchema().isUseNoArgCtorIfFound() : termUsesNoArgCtor.booleanValue();               }

               if(trace)
               {
                  log.trace("startElement " + elementName + " new " + cls.getName() + ", noArgCtor=" + noArgCtor);
               }
               o = newInstance(cls, elementName, noArgCtor);
            }
         }
      }
      return o;
   }

   private void attributes(Object o,
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
               CharactersHandler simpleType = type.getCharactersHandler();
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

   private boolean tryAdd(Object owner,
                          Object o,
                          QName qName,
                          TermBinding term,
                          WildcardBinding wildcard,
                          boolean trace)
   {
      AddMethodMetaData addMetaData = wildcard == null ? null : wildcard.getAddMethodMetaData();
      if(addMetaData == null)
      {
         addMetaData = term.getAddMethodMetaData();
      }

      if(addMetaData == null)
      {
         return false;
      }

      if(trace)
      {
         log.trace("setParent " + qName + " add");
      }
      invokeAdd(qName, addMetaData, owner, o);
      return true;
   }

   private boolean tryPut(Object owner, Object o, QName qName, TermBinding term, boolean trace)
   {
      if(term.getPutMethodMetaData() != null ||
         term.getMapEntryMetaData() != null && owner instanceof Map)
      {
         if(trace)
         {
            log.trace("setParent " + qName + " mapPut");
         }
         invokePut(qName, term, owner, o);
         return true;
      }
      return false;
   }

   private Class classForElement(ElementBinding element, Class parentClass)
   {
      Class cls;
      TypeBinding type = element.getType();
      QName typeQName = type.getQName();
      if(typeQName != null && Constants.NS_XML_SCHEMA.equals(typeQName.getNamespaceURI()))
      {
         cls = SimpleTypeBindings.classForType(type.getQName().getLocalPart(), element.isNillable());
      }
      else
      {
         ElementBinding arrayItem = null;
         if(!type.isSimple() && type.getAttributes().isEmpty())
         {
            ParticleBinding typeParticle = type.getParticle();
            ModelGroupBinding modelGroup = (ModelGroupBinding)(typeParticle == null ? null : typeParticle.getTerm());
            arrayItem = modelGroup == null ? null : modelGroup.getArrayItem();
         }

         if(arrayItem != null)
         {
            cls = classForElement(arrayItem, parentClass);
            // todo: what's the best way to get an array class having the item class
            cls = Array.newInstance(cls, 0).getClass();
         }
         else
         {
            cls = classForNonArrayItem(element, parentClass);
         }
      }
      return cls;
   }

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

   private static MapEntryMetaData getMapEntryMetaData(TermBinding term, QName qName)
   {
      MapEntryMetaData mapEntryMetaData = term.getMapEntryMetaData();
      if(mapEntryMetaData == null)
      {
         String msg;
         if(term.isModelGroup())
         {
            msg = "Term " +
               qName +
               " bound as map entry key or value but map entry metadata is not available for its parent term.";
         }
         else
         {
            ElementBinding element = (ElementBinding)term;
            msg = "Element " +
               qName +
               " bound as map entry key or value but map entry metadata is not available for its parent element nor its " +
               (element.getType().getQName() == null ?
               "annonymous" :
               element.getType().getQName().toString()
               ) +
               " type.";
         }
         throw new JBossXBRuntimeException(msg);
      }
      return mapEntryMetaData;
   }

   private static Object newInstance(Class cls, QName elementName, boolean useNoArgCtorIfFound)
   {
      Object o;
      if(cls.isArray())
      {
         o = GenericValueContainer.FACTORY.array(cls.getComponentType());
      }
      else
      {
         Constructor[] ctors = cls.getConstructors();
         if(ctors.length == 0)
         {
            throw new JBossXBRuntimeException(
               "Class " + cls.getName() + " has no public constructors or the class reflects a primitive type or void"
            );
         }

         if(useNoArgCtorIfFound)
         {
            try
            {
               Constructor ctor = cls.getConstructor(null);
               o = ctor.newInstance(null);
            }
            catch(NoSuchMethodException e)
            {
               o = new ValueListInitializer().newValueList(ValueListHandler.NON_DEFAULT_CTOR, cls);
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException("Failed to create an instance of " +
                  cls +
                  " using default constructor for element " +
                  elementName + ": " + e.getMessage(), e
               );
            }
         }
         else if(ctors.length > 1 || ctors[0].getParameterTypes().length > 0)
         {
            o = new ValueListInitializer().newValueList(ValueListHandler.NON_DEFAULT_CTOR, cls);
         }
         else
         {
            try
            {
               o = ctors[0].newInstance(null);
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException("Failed to create an instance of " +
                  cls +
                  " using default constructor for element " +
                  elementName + ": " + e.getMessage(), e
               );
            }
         }
      }
      return o;
   }

   private static Class loadClassForTerm(String className,
                                         boolean ignoreCNFE,
                                         QName elementName)
   {
      if(className == null)
      {
         throw new JBossXBRuntimeException("No class for " + elementName);
      }

      Class cls = null;
      try
      {
         cls = Thread.currentThread().getContextClassLoader().loadClass(className);
      }
      catch(ClassNotFoundException e)
      {
         if(ignoreCNFE)
         {
            if(log.isTraceEnabled())
            {
               log.trace("Failed to resolve class for element " +
                  elementName +
                  ": " +
                  className
               );
            }
         }
         else
         {
            throw new JBossXBRuntimeException("Failed to resolve class name for " +
               elementName +
               ": " +
               e.getMessage()
            );
         }
      }
      return cls;
   }

   private void invokeAdd(QName qName, AddMethodMetaData addMethodMetaData, Object owner, Object o)
   {
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
            throw new JBossXBRuntimeException("addMethod=" +
               addMethodMetaData.getMethodName() +
               " for element " +
               qName +
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

   private void invokePut(QName qName, TermBinding term, Object owner, Object o)
   {
      PutMethodMetaData putMethodMetaData = term.getPutMethodMetaData();

      MapEntryMetaData mapEntryMetaData = term.getMapEntryMetaData();
      if(mapEntryMetaData == null)
      {
         throw new JBossXBRuntimeException((putMethodMetaData == null ?
            "Parent object is an instance of java.util.Map" :
            "putMethod is specified for element " + qName
            ) +
            " but mapEntry is specified for neither element " +
            qName +
            " nor it's type."
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

   private Class classForNonArrayItem(ElementBinding element, Class parentClass)
   {
      String clsName;

      // first, class metadata and map entry metadata
      ClassMetaData clsMetaData = element.getClassMetaData();
      clsName = clsMetaData == null ? null : clsMetaData.getImpl();
      if(clsName == null)
      {
         MapEntryMetaData mapEntryMetaData = element.getMapEntryMetaData();
         if(mapEntryMetaData != null)
         {
            clsName = mapEntryMetaData.getImpl();
            if(clsName == null)
            {
               clsName = MapEntry.class.getName();
            }
         }
      }

      // second, property metadata and property type
      if(clsName == null)
      {
         if(parentClass == null)
         {
            clsName = classFromQName(element);
         }
         else
         {
            PropertyMetaData propertyMetaData = element.getPropertyMetaData();
            String propName = propertyMetaData == null ? null : propertyMetaData.getName();
            if(propName == null)
            {
               // if there is add or put method metadata then fallback to XML-name-to-class-name algorithm
               if(element.getAddMethodMetaData() == null && element.getPutMethodMetaData() == null)
               {
                  propName =
                     Util.xmlNameToFieldName(element.getQName().getLocalPart(), element.getSchema().isIgnoreLowLine());
               }
            }

            if(propName != null)
            {
               FieldInfo fieldInfo = FieldInfo.getFieldInfo(parentClass, propName, false);
               Class fieldType = fieldInfo == null ? null : fieldInfo.getType();

               if(fieldType == null ||
                  Modifier.isAbstract(fieldType.getModifiers()) ||
                  Modifier.isInterface(fieldType.getModifiers()) ||
                  fieldType.isArray() ||
                  Collection.class.isAssignableFrom(fieldType))
               {
                  clsName = classFromQName(element);
               }
               else
               {
                  return fieldType;
               }
            }
         }
      }

      return loadClassForTerm(clsName, element.getSchema().isIgnoreUnresolvedFieldOrClass(), element.getQName());
   }

   private String classFromQName(ElementBinding element)
   {
      String clsName;
      QName typeBase = element.getType().getQName();
      if(typeBase == null)
      {
         typeBase = element.getQName();
      }

      SchemaBinding schema = element.getSchema();
      PackageMetaData pkgMetaData = schema.getPackageMetaData();
      if(pkgMetaData == null)
      {
         clsName =
            Util.xmlNameToClassName(typeBase.getNamespaceURI(),
               typeBase.getLocalPart(),
               schema.isIgnoreLowLine()
            );
      }
      else
      {
         String pkg = pkgMetaData.getName();
         clsName =
            pkg == null || pkg.length() == 0 ?
            Util.xmlNameToClassName(typeBase.getLocalPart(), schema.isIgnoreLowLine()) :
            pkg + "." + Util.xmlNameToClassName(typeBase.getLocalPart(), schema.isIgnoreLowLine());
      }
      return clsName;
   }

   private static Class classForSimpleType(TypeBinding type, boolean nillable)
   {
      ValueMetaData valueMetaData = type.getValueMetaData();
      if(valueMetaData != null && valueMetaData.getUnmarshalMethod() != null)
      {
         return RtUtil.getUnmarshalMethod(type.getQName(), valueMetaData).getReturnType();
      }
      else if(type.getClassMetaData() != null && type.getClassMetaData().getImpl() != null)
      {
         return RtUtil.loadClass(type.getClassMetaData().getImpl(), true);
      }

      TypeBinding itemType = type.getItemType();
      if(itemType != null)
      {
         if(type.getSchemaBinding().isUnmarshalListsToArrays())
         {
            // todo: nillable not always should be propagated to the item
            Class itemClass = classForSimpleType(itemType, nillable);
            return Array.newInstance(itemClass, 0).getClass();
         }
         else
         {
            return java.util.List.class;
         }
      }
      else
      {
         QName qName = type.getQName();
         if(qName != null && Constants.NS_XML_SCHEMA.equals(qName.getNamespaceURI()))
         {
            return SimpleTypeBindings.classForType(qName.getLocalPart(), nillable);
         }
         else
         {
            TypeBinding baseType = type.getBaseType();
            if(baseType == null)
            {
               throw new JBossXBRuntimeException("Expected a base type here.");
            }

            return classForSimpleType(baseType, nillable);
         }
      }
   }
}
