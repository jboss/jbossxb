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
package org.jboss.xb.binding.sunday.unmarshalling;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.SchemaMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.metadata.XsdAnnotation;
import org.jboss.xb.binding.metadata.XsdAppInfo;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdBinder
{
   static final Logger log = Logger.getLogger(XsdBinder.class);

   private static final ThreadLocal xsdBinding = new ThreadLocal();

   private static XsdBinding getXsdBinding()
   {
      XsdBinding local = (XsdBinding)xsdBinding.get();
      if(local == null)
      {
         local = new XsdBinding();
         xsdBinding.set(local);
      }
      return local;
   }

   private XsdBinder()
   {
   }

   /**
    * Create a SchemaBinding from and xsd url/uri.
    *
    * @param xsdUrl
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(String xsdUrl)
   {
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.setBaseURI(xsdUrl);
      return bind(xsdUrl, resolver);
   }

   /**
    * Create a SchemaBinding from and xsd url/uri.
    *
    * @param xsdUrl
    * @param resolver the resolver will be used to resolve imported schemas in the schema being loaded
    *                 and also will be set on the returned instance of SchemaBinding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(String xsdUrl, SchemaBindingResolver resolver)
   {
      XSModel model = Util.loadSchema(xsdUrl, resolver);
      return bind(model, resolver);
   }

   public static SchemaBinding bind(InputStream xsdStream, String encoding)
   {
      return bind(xsdStream, encoding, new DefaultSchemaResolver());
   }

   /**
    * Create a SchemaBinding from and xsd stream.
    *
    * @param xsdStream - the xsd InputStream
    * @param encoding  - optional stream encoding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(InputStream xsdStream, String encoding, String baseURI)
   {
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.setBaseURI(baseURI);
      return bind(xsdStream, encoding, resolver);
   }

   /**
    * Create a SchemaBinding from and xsd stream.
    *
    * @param xsdStream - the xsd InputStream
    * @param encoding  - optional stream encoding
    * @param resolver  the resolver will be used to resolve imported schemas in the schema being loaded
    *                  and also will be set on the returned instance of SchemaBinding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(InputStream xsdStream, String encoding, SchemaBindingResolver resolver)
   {
      XSModel model = Util.loadSchema(xsdStream, encoding, resolver);
      return bind(model, resolver);
   }

   public static SchemaBinding bind(Reader xsdReader, String encoding)
   {
      return bind(xsdReader, encoding, new DefaultSchemaResolver());
   }

   /**
    * Create a SchemaBinding from and xsd reader.
    *
    * @param xsdReader - xsd reader
    * @param encoding  - optional reader encoding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(Reader xsdReader, String encoding, String baseURI)
   {
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.setBaseURI(baseURI);
      return bind(xsdReader, encoding, resolver);
   }

   /**
    * Create a SchemaBinding from and xsd reader.
    *
    * @param xsdReader - xsd reader
    * @param encoding  - optional reader encoding
    * @param resolver  the resolver will be used to resolve imported schemas in the schema being loaded
    *                  and also will be set on the returned instance of SchemaBinding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(Reader xsdReader, String encoding, SchemaBindingResolver resolver)
   {
      XSModel model = Util.loadSchema(xsdReader, encoding, resolver);
      return bind(model, resolver);
   }

   /**
    * Create a SchemaBinding from and xsd string.
    *
    * @param xsd      - xsd string
    * @param encoding - optional string encoding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(String xsd, String encoding)
   {
      return bind(xsd, encoding, new DefaultSchemaResolver());
   }

   /**
    * Create a SchemaBinding from and xsd string.
    *
    * @param xsd      - xsd string
    * @param encoding - optional string encoding
    * @param resolver the resolver will be used to resolve imported schemas in the schema being loaded
    *                 and also will be set on the returned instance of SchemaBinding
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(String xsd, String encoding, SchemaBindingResolver resolver)
   {
      XSModel model = Util.loadSchema(xsd, encoding);
      return bind(model, resolver);
   }

   public static SchemaBinding bind(XSModel model, SchemaBindingResolver resolver)
   {
      boolean trace = log.isTraceEnabled();
      
      SchemaBinding schema = getXsdBinding().schemaBinding;
      schema.setSchemaResolver(resolver);

      // read annotations. for now just log the ones that are going to be used
      XSObjectList annotations = model.getAnnotations();
      if (trace)
         log.trace("Schema annotations: " + annotations.getLength());
      for(int i = 0; i < annotations.getLength(); ++i)
      {
         XSAnnotation annotation = (XSAnnotation)annotations.item(i);
         XsdAnnotation an = XsdAnnotation.unmarshal(annotation.getAnnotationString());
         XsdAppInfo appinfo = an.getAppInfo();
         if(appinfo != null)
         {
            SchemaMetaData schemaBindings = appinfo.getSchemaMetaData();
            if(schemaBindings != null)
            {
               // Get the ignoreUnresolvedFieldOrClass
               schema.setIgnoreUnresolvedFieldOrClass(schemaBindings.isIgnoreUnresolvedFieldOrClass());
               // Get the ignoreUnresolvedFieldOrClass
               schema.setReplacePropertyRefs(schemaBindings.isReplacePropertyRefs());
               // Get the default package
               PackageMetaData packageMetaData = schemaBindings.getPackage();
               if(packageMetaData != null)
               {
                  if (trace)
                     log.trace("schema default package: " + packageMetaData.getName());
                  schema.setPackageMetaData(packageMetaData);
               }
            }
         }
      }

      SharedElements sharedElements = new SharedElements();

      XSNamedMap groups = model.getComponents(XSConstants.MODEL_GROUP_DEFINITION);
      if (trace)
         log.trace("Model groups: " + groups.getLength());
      for(int i = 0; i < groups.getLength(); ++i)
      {
         XSModelGroupDefinition groupDef = (XSModelGroupDefinition)groups.item(i);
         bindGlobalGroup(groupDef.getModelGroup(), sharedElements);
      }

      XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
      if (trace)
         log.trace("Model types: " + types.getLength());
      for(int i = 0; i < types.getLength(); ++i)
      {
         XSTypeDefinition type = (XSTypeDefinition)types.item(i);
         if(!Constants.NS_XML_SCHEMA.equals(type.getNamespace()))
         {
            bindType(schema, type, sharedElements);
         }
      }

      XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      if (trace)
         log.trace("Model elements: " + types.getLength());
      for(int i = 0; i < elements.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)elements.item(i);
         bindElement(schema, element, sharedElements, 1, 0, false);
      }

      xsdBinding.set(null);
      return schema;
   }

   /**
    * @param schema schema binding the type should be added to
    * @param type   type definition to be bound
    * @deprecated <i>This method is added temporary to get anonymous type binding working in JBossWS.
    *             It will be removed when anonymous type binding in JBossWS is implemented properly.
    *             No one else should use this method.</i>
    *
    *             <p>This method binds a type definition and adds it as a global type to the passed in schema binding.
    */
   public static void bindType(SchemaBinding schema, XSTypeDefinition type)
   {
      TypeBinding typeBinding = bindType(schema, type, new SharedElements());
      schema.addType(typeBinding);
   }

   /**
    * @param schema             schema binding the type should be added to
    * @param element            element declaration to be bound
    * @param minOccurs
    * @param maxOccurs
    * @param maxOccursUnbounded
    * @deprecated <i>This method is added temporary to get anonymous type binding working in JBossWS.
    *             It will be removed when anonymous type binding in JBossWS is implemented properly.
    *             No one else should use this method.</i>
    *
    *             <p>This method binds an element declaration and adds it as a global element to the passed in schema binding.
    */
   public static void bindElement(SchemaBinding schema,
                                  XSElementDeclaration element,
                                  int minOccurs,
                                  int maxOccurs,
                                  boolean maxOccursUnbounded)
   {
      ParticleBinding particle = bindElement(schema,
         element,
         new SharedElements(),
         minOccurs,
         maxOccurs,
         maxOccursUnbounded
      );
      schema.addElementParticle(particle);
   }
   
   // Private

   private static TypeBinding bindType(SchemaBinding doc, XSTypeDefinition type, SharedElements sharedElements)
   {
      TypeBinding binding;
      switch(type.getTypeCategory())
      {
         case XSTypeDefinition.SIMPLE_TYPE:
            binding = bindSimpleType(doc, (XSSimpleTypeDefinition)type);
            break;
         case XSTypeDefinition.COMPLEX_TYPE:
            binding = bindComplexType(doc, (XSComplexTypeDefinition)type, sharedElements);
            break;
         default:
            throw new JBossXBRuntimeException("Unexpected type category: " + type.getTypeCategory());
      }
      return binding;
   }

   private static TypeBinding bindSimpleType(SchemaBinding schema, XSSimpleTypeDefinition type)
   {
      boolean trace = log.isTraceEnabled();

      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : schema.getType(typeName);
      if(binding == null)
      {
         if (trace)
         {
            log.trace("binding simple type " + typeName);
         }

         XSTypeDefinition baseTypeDef = type.getBaseType();
         TypeBinding baseType = baseTypeDef == null ? null : bindType(schema, baseTypeDef, null);

         binding = baseType == null ? new TypeBinding(typeName) : new TypeBinding(typeName, baseType);

         StringList strList = type.getLexicalPattern();
         if(strList != null && strList.getLength() > 0)
         {
            for(int i = 0; i < strList.getLength(); ++i)
            {
               binding.addLexicalPattern(strList.item(i));
            }
         }

         strList = type.getLexicalEnumeration();
         if(strList != null && strList.getLength() > 0)
         {
            for(int i = 0; i < strList.getLength(); ++i)
            {
               binding.addEnumValue(strList.item(i));
            }
         }

         if(type.getItemType() != null)
         {
            TypeBinding itemType = bindSimpleType(schema, type.getItemType());
            binding.setItemType(itemType);
         }

         if(typeName != null)
         {
            schema.addType(binding);
         }

         if (trace)
         {
            String msg = typeName == null ? "bound simple anonymous type" : "bound simple type " + typeName;
            if(baseType != null)
            {
               msg += " inherited binding metadata from " + baseType.getQName();
            }
            log.trace(msg);
         }

         // customize binding with annotations
         XSObjectList annotations = type.getAnnotations();
         if(annotations != null)
         {
            if (trace)
               log.trace(typeName + " annotations " + annotations.getLength());
            for(int i = 0; i < annotations.getLength(); ++i)
            {
               XSAnnotation an = (XSAnnotation)annotations.item(i);
               XsdAnnotation xsdAn = XsdAnnotation.unmarshal(an.getAnnotationString());
               XsdAppInfo appInfo = xsdAn.getAppInfo();
               if(appInfo != null)
               {
                  ClassMetaData classMetaData = appInfo.getClassMetaData();
                  if(classMetaData != null)
                  {
                     if (trace)
                     {
                        log.trace("simple type " +
                           type.getName() +
                           ": impl=" +
                           classMetaData.getImpl()
                        );
                     }
                     binding.setClassMetaData(classMetaData);
                  }

                  ValueMetaData valueMetaData = appInfo.getValueMetaData();
                  if(valueMetaData != null)
                  {
                     if (trace)
                     {
                        log.trace("simple type " +
                           type.getName() +
                           ": unmarshalMethod=" +
                           valueMetaData.getUnmarshalMethod() +
                           ", marshalMethod=" +
                           valueMetaData.getMarshalMethod()
                        );
                     }
                     binding.setValueMetaData(valueMetaData);
                  }
               }
            }
         }

         binding.setSchemaBinding(schema);
      }
      return binding;
   }

   private static TypeBinding bindComplexType(SchemaBinding schema,
                                              XSComplexTypeDefinition type,
                                              SharedElements sharedElements)
   {
      boolean trace = log.isTraceEnabled();

      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : schema.getType(typeName);
      if(binding != null)
      {
         return binding;
      }

      if (trace)
         log.trace("binding complex type " + typeName);

      //XSTypeDefinition baseTypeDef = type.getBaseType();
      // anyType is the parent of all the types, even the parent of itself according to xerces :)
      /*TypeBinding baseType = (baseTypeDef == sharedElements.anyType ?
         null :
         bindType(doc, baseTypeDef, sharedElements)
         );
         */
      binding = new TypeBinding(typeName);
      binding.setStartElementCreatesObject(true);
      binding.setSimple(false);

      if(type.getSimpleType() != null)
      {
         TypeBinding simpleType = bindSimpleType(schema, type.getSimpleType());
         binding.setSimpleType(simpleType);
      }
      else if(type.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED)
      {
         TypeBinding stringType = schema.getType(Constants.QNAME_STRING);
         if(stringType == null)
         {
            throw new JBossXBRuntimeException("xsd:string has not been bound yet!");
         }
         binding.setSimpleType(stringType);
      }

      if(typeName != null)
      {
         schema.addType(binding);
      }

      if (trace)
      {
         String msg = typeName == null ? "bound complex anonymous type" : "bound complex type " + typeName;
         log.trace(msg);
      }

      binding.setSchemaBinding(schema);

      XSObjectList attrs = type.getAttributeUses();
      if (trace)
         log.trace(typeName + " attributes " + attrs.getLength());
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         XSAttributeUse attr = (XSAttributeUse)attrs.item(i);
         bindAttributes(schema, binding, attr);
      }

      // customize binding with xsd annotations
      XSObjectList annotations = type.getAnnotations();
      if(annotations != null)
      {
         if (trace)
            log.trace(typeName + " annotations " + annotations.getLength());
         for(int i = 0; i < annotations.getLength(); ++i)
         {
            XSAnnotation an = (XSAnnotation)annotations.item(i);
            XsdAnnotation xsdAn = XsdAnnotation.unmarshal(an.getAnnotationString());
            XsdAppInfo appInfo = xsdAn.getAppInfo();
            if(appInfo != null)
            {
               ClassMetaData classMetaData = appInfo.getClassMetaData();
               if(classMetaData != null)
               {
                  if (trace)
                  {
                     log.trace("complex type " +
                        type.getName() +
                        ": impl=" +
                        classMetaData.getImpl()
                     );
                  }
                  binding.setClassMetaData(classMetaData);
               }

               CharactersMetaData charactersMetaData = appInfo.getCharactersMetaData();
               if(charactersMetaData != null)
               {
                  if (trace)
                  {
                     PropertyMetaData propertyMetaData = charactersMetaData.getProperty();
                     if(propertyMetaData != null)
                     {
                        log.trace("complex type " +
                           type.getName() +
                           ": characters bound to " + propertyMetaData.getName()
                        );
                     }

                     ValueMetaData valueMetaData = charactersMetaData.getValue();
                     if(valueMetaData != null)
                     {
                        log.trace("complex type " +
                           type.getName() +
                           ": characters unmarshalMethod=" +
                           valueMetaData.getUnmarshalMethod() +
                           ", marshalMethod=" + valueMetaData.getMarshalMethod()
                        );
                     }

                     boolean mapEntryKey = appInfo.isMapEntryKey();
                     if(mapEntryKey)
                     {
                        log.trace("complex type " +
                           type.getName() +
                           ": characters are bound as a key in a map entry"
                        );
                     }

                     boolean mapEntryValue = appInfo.isMapEntryValue();
                     if(mapEntryValue)
                     {
                        log.trace("complex type " +
                           type.getName() +
                           ": characters are bound as a value in a map entry"
                        );
                     }
                  }
                  binding.setCharactersMetaData(charactersMetaData);
               }

               MapEntryMetaData mapEntryMetaData = appInfo.getMapEntryMetaData();
               if(mapEntryMetaData != null)
               {
                  if (trace)
                  {
                     log.trace("complex type " +
                        type.getName() +
                        " is bound to a map entry: impl=" +
                        mapEntryMetaData.getImpl() +
                        ", getKeyMethod=" +
                        mapEntryMetaData.getGetKeyMethod() +
                        ", setKeyMethod=" +
                        mapEntryMetaData.getSetKeyMethod() +
                        ", getValueMethod=" +
                        mapEntryMetaData.getGetValueMethod() +
                        ", setValueMethod=" +
                        mapEntryMetaData.getSetValueMethod() +
                        ", valueType=" +
                        mapEntryMetaData.getValueType() +
                        ", nonNullValue=" + mapEntryMetaData.isNonNullValue()
                     );
                  }

                  if(classMetaData != null)
                  {
                     throw new JBossXBRuntimeException("Illegal binding: both jbxb:class and jbxb:mapEntry are specified for complex type " +
                        type.getName()
                     );
                  }
                  binding.setMapEntryMetaData(mapEntryMetaData);
               }

               boolean skip = appInfo.isSkip();
               if(skip)
               {
                  if (trace)
                  {
                     log.trace("complex type " +
                        type.getName() +
                        ": elements of this type will be skipped; their attrs, character content " +
                        "and elements will be set the parent."
                     );
                  }
                  binding.setSkip(skip);
               }

               PropertyMetaData propertyMetaData = appInfo.getPropertyMetaData();
               if(propertyMetaData != null)
               {
                  if (trace)
                  {
                     log.trace("complex type " +
                        type.getName() +
                        ": the content of elements of this type is bound to property " + propertyMetaData.getName()
                     );
                  }
                  binding.setPropertyMetaData(propertyMetaData);
               }

               AddMethodMetaData addMethodMetaData = appInfo.getAddMethodMetaData();
               if(addMethodMetaData != null)
               {
                  if (trace)
                  {
                     log.trace("complex type " +
                        type.getName() +
                        ": elements of this type will be added to parent objects with addMethod=" +
                        addMethodMetaData.getMethodName() + ", valueType=" + addMethodMetaData.getValueType()
                     );
                  }
                  binding.setAddMethodMetaData(addMethodMetaData);
               }
            }
         }
      }

      XSParticle particle = type.getParticle();
      if(particle != null)
      {
         pushType(binding);
         bindParticle(schema, particle, sharedElements);
         popType();
      }

      return binding;
   }

   private static void bindAttributes(SchemaBinding doc,
                                      TypeBinding type,
                                      XSAttributeUse attrUse)
   {
      boolean trace = log.isTraceEnabled();
      
      XSAttributeDeclaration attr = attrUse.getAttrDeclaration();
      QName attrName = new QName(attr.getNamespace(), attr.getName());

      if (trace)
      {
         log.trace("binding attribute " + attrName + " for " + type.getQName() + ", required=" + attrUse.getRequired());
      }

      XSSimpleTypeDefinition attrType = attr.getTypeDefinition();
      TypeBinding typeBinding = bindSimpleType(doc, attrType);
      AttributeBinding binding = type.addAttribute(attrName, typeBinding, DefaultHandlers.ATTRIBUTE_HANDLER);
      binding.setRequired(attrUse.getRequired());
      if(attrUse.getConstraintType() == XSConstants.VC_DEFAULT)
      {
         // Associate the default value with the binding
         binding.setDefaultConstraint(attrUse.getConstraintValue());
      }

      XSAnnotation an = attr.getAnnotation();
      if(an != null)
      {
         if (trace)
         {
            log.trace(attrName + " attribute annotation");
         }

         XsdAnnotation xsdAn = XsdAnnotation.unmarshal(an.getAnnotationString());
         XsdAppInfo appInfo = xsdAn.getAppInfo();
         if(appInfo != null)
         {
            PropertyMetaData propertyMetaData = appInfo.getPropertyMetaData();
            if(propertyMetaData != null)
            {
               binding.setPropertyMetaData(propertyMetaData);
            }

            boolean mapEntryKey = appInfo.isMapEntryKey();
            if(mapEntryKey)
            {
               binding.setMapEntryKey(mapEntryKey);
            }

            boolean mapEntryValue = appInfo.isMapEntryValue();
            if(mapEntryValue)
            {
               binding.setMapEntryValue(mapEntryValue);
            }
         }
      }

      if (trace)
      {
         String msg = "bound attribute " + attrName;

         if(binding.getPropertyMetaData() != null)
         {
            msg += " property=" +
               binding.getPropertyMetaData().getName() +
               ", collectionType=" + binding.getPropertyMetaData().getCollectionType();
         }
         else if(binding.isMapEntryKey())
         {
            msg += "bound as a key in a map entry";
         }
         else if(binding.isMapEntryValue())
         {
            msg += "bound as a value in a map entry";
         }
         else
         {
            msg += " type=" + attrType.getName() + ", owner type=" + type.getQName();
         }

         if(binding.getDefaultConstraint() != null)
         {
            msg += ", default=" + binding.getDefaultConstraint();
         }

         log.trace(msg);
      }
   }

   private static void bindParticle(SchemaBinding schema, XSParticle particle, SharedElements sharedElements)
   {
      boolean trace = log.isTraceEnabled();
      
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            XSModelGroup modelGroup = (XSModelGroup)term;
            // todo: investigate this
            if(modelGroup.getParticles().getLength() > 0)
            {
               ModelGroupBinding groupBinding;
               switch(modelGroup.getCompositor())
               {
                  case XSModelGroup.COMPOSITOR_ALL:
                     groupBinding = new AllBinding(schema);
                     break;
                  case XSModelGroup.COMPOSITOR_CHOICE:
                     groupBinding = new ChoiceBinding(schema);
                     break;
                  case XSModelGroup.COMPOSITOR_SEQUENCE:
                     groupBinding = new SequenceBinding(schema);
                     break;
                  default:
                     throw new JBossXBRuntimeException("Unexpected model group: " + modelGroup.getCompositor());
               }

               ParticleBinding particleBinding = new ParticleBinding(groupBinding);
               particleBinding.setMaxOccursUnbounded(particle.getMaxOccursUnbounded());
               particleBinding.setMinOccurs(particle.getMinOccurs());
               particleBinding.setMaxOccurs(particle.getMaxOccurs());

               if (trace)
               {
                  log.trace("created model group " + groupBinding);
               }

               XSAnnotation annotation = modelGroup.getAnnotation();
               if(annotation != null)
               {
                  customizeTerm(annotation, groupBinding);
               }

               Object o = peekTypeOrGroup();
               if(o instanceof ModelGroupBinding)
               {
                  ModelGroupBinding parentGroup = (ModelGroupBinding)o;
                  parentGroup.addParticle(particleBinding);
                  if (trace)
                  {
                     log.trace("added " + groupBinding + " to " + parentGroup);
                  }
               }
               else if(o instanceof TypeBinding)
               {
                  TypeBinding typeBinding = (TypeBinding)o;
                  typeBinding.setParticle(particleBinding);
                  if (trace)
                  {
                     log.trace("added " + groupBinding + " to type " + typeBinding.getQName());
                  }
               }

               pushModelGroup(groupBinding);
               bindModelGroup(schema, modelGroup, sharedElements);
               popModelGroup();
            }
            break;
         case XSConstants.WILDCARD:
            bindWildcard(schema, particle);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(schema,
               (XSElementDeclaration)term,
               sharedElements,
               particle.getMinOccurs(),
               particle.getMaxOccurs(),
               particle.getMaxOccursUnbounded()
            );
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
   }

   private static void bindWildcard(SchemaBinding schema, XSParticle particle)
   {
      boolean trace = log.isTraceEnabled();
      
      WildcardBinding binding = new WildcardBinding(schema);

      ModelGroupBinding group = (ModelGroupBinding)peekTypeOrGroup();
      ParticleBinding particleBinding = new ParticleBinding(binding);
      particleBinding.setMaxOccurs(particle.getMaxOccurs());
      particleBinding.setMaxOccursUnbounded(particle.getMaxOccursUnbounded());
      particleBinding.setMinOccurs(particle.getMinOccurs());
      group.addParticle(particleBinding);

      TypeBinding type = peekType();
      type.setWildcard(binding);

      if (trace)
      {
         log.trace("added wildcard to " + group);
         log.trace("added wildcard to type " + type.getQName());
      }

      XSWildcard wildcard = (XSWildcard)particle.getTerm();
      if(wildcard.getName() != null)
      {
         binding.setQName(new QName(wildcard.getNamespace(), wildcard.getName()));
      }

      XSAnnotation annotation = wildcard.getAnnotation();
      if(annotation != null)
      {
         if (trace)
            log.trace(group + " annotation");
         XsdAnnotation xsdAn = XsdAnnotation.unmarshal(annotation.getAnnotationString());
         XsdAppInfo appInfo = xsdAn.getAppInfo();
         if(appInfo != null)
         {
            PropertyMetaData propertyMetaData = appInfo.getPropertyMetaData();
            if(propertyMetaData != null)
            {
               if (trace)
               {
                  log.trace("wildcard is bound to property: " +
                     propertyMetaData.getName() +
                     ", collectionType=" + propertyMetaData.getCollectionType()
                  );
               }
            }
            type.setWildcardPropertyMetaData(propertyMetaData);
         }
      }
   }

   private static ParticleBinding bindElement(SchemaBinding schema,
                                              XSElementDeclaration elementDec,
                                              SharedElements sharedElements,
                                              int minOccurs,
                                              int maxOccurs,
                                              boolean maxOccursUnbounded)
   {
      boolean trace = log.isTraceEnabled();
      
      QName qName = new QName(elementDec.getNamespace(), elementDec.getName());

      ModelGroupBinding parentGroup = (ModelGroupBinding)peekTypeOrGroup();

      boolean global = elementDec.getScope() == XSConstants.SCOPE_GLOBAL;
      ElementBinding element = schema.getElement(qName);
      ParticleBinding particle;
      if(global && element != null)
      {
         particle = new ParticleBinding(element);
         if(parentGroup != null)
         {
            parentGroup.addParticle(particle);
         }

         // todo: this is a BAD hack!
         if(maxOccursUnbounded)
         {
            particle.setMaxOccursUnbounded(maxOccursUnbounded);
         }
         return particle;
      }

      TypeBinding type = null;

      boolean shared = sharedElements.isShared(elementDec);
      if(shared)
      {
         type = sharedElements.getTypeBinding(elementDec);
      }

      if(type == null)
      {
         type = bindType(schema, elementDec.getTypeDefinition(), sharedElements);
         if(shared)
         {
            sharedElements.setTypeBinding(elementDec, type);
         }
      }

      element = new ElementBinding(schema, qName, type);
      element.setNillable(elementDec.getNillable());
      particle = new ParticleBinding(element);
      particle.setMinOccurs(minOccurs);
      particle.setMaxOccurs(maxOccurs);
      particle.setMaxOccursUnbounded(maxOccursUnbounded);
      if(global)
      {
         schema.addElementParticle(particle);
      }

      if(parentGroup != null)
      {
         parentGroup.addParticle(particle);
         if (trace)
         {
            log.trace("Element " + element.getQName() + " added to " + parentGroup);
         }
      }

      if (trace)
      {
         TypeBinding parentType = peekType();
         log.trace("element: name=" +
            qName +
            ", type=" +
            type.getQName() +
            ", repeatable=" +
            particle.isRepeatable() +
            ", nillable=" +
            element.isNillable() +
            ", " + (global ? "global scope" : " owner type=" + parentType.getQName())
         );
      }

      // customize element with annotations
      XSAnnotation an = elementDec.getAnnotation();
      if(an != null)
      {
         customizeTerm(an, element);
      }
      return particle;
   }

   private static void bindModelGroup(SchemaBinding doc, XSModelGroup modelGroup, SharedElements sharedElements)
   {
      XSObjectList particles = modelGroup.getParticles();
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         bindParticle(doc, particle, sharedElements);
      }
   }

   // Private

   private static void customizeTerm(XSAnnotation an, TermBinding term)
   {
      XsdAnnotation xsdAn = XsdAnnotation.unmarshal(an.getAnnotationString());
      XsdAppInfo appInfo = xsdAn.getAppInfo();
      if(appInfo != null)
      {
         boolean trace = log.isTraceEnabled();
         Boolean skip = null;

         ClassMetaData classMetaData = appInfo.getClassMetaData();
         if(classMetaData != null)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term + " bound to " : "element: name=" +
                  ((ElementBinding)term).getQName() +
                  ", class=";
               msg += classMetaData.getImpl();
               log.trace(msg);
            }
            term.setClassMetaData(classMetaData);
            skip = Boolean.FALSE;
         }

         PropertyMetaData propertyMetaData = appInfo.getPropertyMetaData();
         if(propertyMetaData != null)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term + " " : "element: name=" +
                  ((ElementBinding)term).getQName() + ", ";
               msg += " property=" +
                  propertyMetaData.getName() +
                  ", collectionType=" + propertyMetaData.getCollectionType();
               log.trace(msg);
            }
            term.setPropertyMetaData(propertyMetaData);
         }

         MapEntryMetaData mapEntryMetaData = appInfo.getMapEntryMetaData();
         if(mapEntryMetaData != null)
         {
            if(propertyMetaData != null)
            {
               String msg = "A term can be bound either as a property or as a map" +
                  " entry but not both: " +
                  (term.isModelGroup() ? term.toString() : ((ElementBinding)term).getQName().toString());
               throw new JBossXBRuntimeException(msg);
            }

            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element name=" +
                  ((ElementBinding)term).getQName();

               msg += " is bound to a map entry: impl=" +
                  mapEntryMetaData.getImpl() +
                  ", getKeyMethod=" +
                  mapEntryMetaData.getGetKeyMethod() +
                  ", setKeyMethod=" +
                  mapEntryMetaData.getSetKeyMethod() +
                  ", getValueMethod=" +
                  mapEntryMetaData.getGetValueMethod() +
                  ", setValueMethod=" +
                  mapEntryMetaData.getSetValueMethod() +
                  ", valueType=" +
                  mapEntryMetaData.getValueType() +
                  ", nonNullValue=" + mapEntryMetaData.isNonNullValue();
               log.trace(msg);
            }

            if(classMetaData != null)
            {
               String msg = "Invalid customization: both jbxb:class and jbxb:mapEntry are specified for term " +
                  (term.isModelGroup() ? term.toString() : ((ElementBinding)term).getQName().toString());
               throw new JBossXBRuntimeException(msg);
            }
            term.setMapEntryMetaData(mapEntryMetaData);
            skip = Boolean.FALSE;
         }

         PutMethodMetaData putMethodMetaData = appInfo.getPutMethodMetaData();
         if(putMethodMetaData != null)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element: name=" +
                  ((ElementBinding)term).getQName() + ",";

               msg += " putMethod=" +
                  putMethodMetaData.getName() +
                  ", keyType=" +
                  putMethodMetaData.getKeyType() +
                  ", valueType=" + putMethodMetaData.getValueType();
               log.trace(msg);
            }
            term.setPutMethodMetaData(putMethodMetaData);
         }

         AddMethodMetaData addMethodMetaData = appInfo.getAddMethodMetaData();
         if(addMethodMetaData != null)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element: name=" +
                  ((ElementBinding)term).getQName() + ",";
               msg += " addMethod=" +
                  addMethodMetaData.getMethodName() +
                  ", valueType=" +
                  addMethodMetaData.getValueType() +
                  ", isChildType=" + addMethodMetaData.isChildType();
               log.trace(msg);
            }
            term.setAddMethodMetaData(addMethodMetaData);
         }

         ValueMetaData valueMetaData = appInfo.getValueMetaData();
         if(valueMetaData != null)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element " +
                  ((ElementBinding)term).getQName();
               msg += ": unmarshalMethod=" + valueMetaData.getUnmarshalMethod();
               log.trace(msg);
            }
            term.setValueMetaData(valueMetaData);
         }

         boolean mapEntryKey = appInfo.isMapEntryKey();
         if(mapEntryKey)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element name=" +
                  ((ElementBinding)term).getQName();
               msg += ": is bound to a key in a map entry";
               log.trace(msg);
            }
            term.setMapEntryKey(mapEntryKey);
            skip = Boolean.FALSE;
         }

         boolean mapEntryValue = appInfo.isMapEntryValue();
         if(mapEntryValue)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element name=" +
                  ((ElementBinding)term).getQName();
               msg += ": is bound to a value in a map entry";
               log.trace(msg);
            }
            term.setMapEntryValue(mapEntryValue);
            skip = Boolean.FALSE;
         }

         boolean skipAnnotation = appInfo.isSkip();
         if(skip != null)
         {
            term.setSkip(skip);
         }
         else if(skipAnnotation)
         {
            if (trace)
            {
               String msg = term.isModelGroup() ? term.toString() : "element name=" +
                  ((ElementBinding)term).getQName();
               msg += ": will be skipped, it's attributes, character content and children will be set on the parent";
               log.trace(msg);
            }
            term.setSkip(skipAnnotation ? Boolean.TRUE : Boolean.FALSE);
         }
      }
   }

   private static void bindGlobalGroup(XSModelGroup group, SharedElements sharedElements)
   {
      XSObjectList particles = group.getParticles();
      for(int j = 0; j < particles.getLength(); ++j)
      {
         XSParticle particle = (XSParticle)particles.item(j);
         XSTerm term = particle.getTerm();
         switch(term.getType())
         {
            case XSConstants.ELEMENT_DECLARATION:
               XSElementDeclaration element = ((XSElementDeclaration)term);
               sharedElements.add(element);
               break;
            case XSConstants.WILDCARD:
               // todo is it actually possible?
               break;
            case XSConstants.MODEL_GROUP:
               bindGlobalGroup((XSModelGroup)term, sharedElements);
         }
      }
   }

   private static void popType()
   {
      Object o = getXsdBinding().typeGroupStack.removeLast();
      if(!(o instanceof TypeBinding))
      {
         throw new JBossXBRuntimeException("Should have poped type binding but got " + o);
      }
   }

   private static void pushType(TypeBinding binding)
   {
      getXsdBinding().typeGroupStack.addLast(binding);
   }

   private static void popModelGroup()
   {
      Object o = getXsdBinding().typeGroupStack.removeLast();
      if(!(o instanceof ModelGroupBinding))
      {
         throw new JBossXBRuntimeException("Should have poped model group binding but got " + o);
      }
   }

   private static void pushModelGroup(ModelGroupBinding binding)
   {
      getXsdBinding().typeGroupStack.addLast(binding);
   }

   private static Object peekTypeOrGroup()
   {
      LinkedList stack = getXsdBinding().typeGroupStack;
      return stack.isEmpty() ? null : stack.getLast();
   }

   private static TypeBinding peekType()
   {
      LinkedList stack = getXsdBinding().typeGroupStack;
      TypeBinding binding = null;
      for(int i = stack.size() - 1; i >= 0; --i)
      {
         Object o = stack.get(i);
         if(o instanceof TypeBinding)
         {
            binding = (TypeBinding)o;
            break;
         }
      }
      return binding;
   }

   // Inner

   private static final class SharedElements
   {
      private Map elements = Collections.EMPTY_MAP;

      public void add(XSElementDeclaration element)
      {
         switch(elements.size())
         {
            case 0:
               elements = Collections.singletonMap(element, null);
               break;
            case 1:
               elements = new HashMap(elements);
            default:
               elements.put(element, null);
         }
      }

      public boolean isShared(XSElementDeclaration element)
      {
         return elements.containsKey(element);
      }

      public TypeBinding getTypeBinding(XSElementDeclaration element)
      {
         return (TypeBinding)elements.get(element);
      }

      public void setTypeBinding(XSElementDeclaration element, TypeBinding type)
      {
         elements.put(element, type);
      }
   }

   private static final class XsdBinding
   {
      public final LinkedList typeGroupStack = new LinkedList();
      public final SchemaBinding schemaBinding = new SchemaBinding();
   }
}
