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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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
import org.jboss.xb.binding.sunday.xop.XOPIncludeHandler;
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

   /**
    * Creates a new instance of the binder the user can use to tune
    * configuration before parsing the XSD.
    * 
    * @return  a new instance of the XsdBinder
    */
   public static XsdBinder newInstance()
   {
     return new XsdBinder();
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
      return bind(xsdStream, encoding, baseURI, true);
   }

   /**
    * Create a SchemaBinding from and xsd stream.
    *
    * @param xsdStream - the xsd InputStream
    * @param encoding  - optional stream encoding
    * @param processAnnotations - process annotations
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(InputStream xsdStream, String encoding, String baseURI, boolean processAnnotations)
   {
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.setBaseURI(baseURI);
      return bind(xsdStream, encoding, resolver, processAnnotations);
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
      return bind(xsdStream, encoding, resolver, true);
   }

   /**
    * Create a SchemaBinding from and xsd stream.
    *
    * @param xsdStream - the xsd InputStream
    * @param encoding  - optional stream encoding
    * @param resolver  the resolver will be used to resolve imported schemas in the schema being loaded
    *                  and also will be set on the returned instance of SchemaBinding
    * @param processAnnotations whether to process annotations
    * @return SchemaBinding mapping
    */
   public static SchemaBinding bind(InputStream xsdStream, String encoding, SchemaBindingResolver resolver, boolean processAnnotations)
   {
      XSModel model = Util.loadSchema(xsdStream, encoding, resolver);
      return bind(model, resolver, processAnnotations);
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
      return bind(model,resolver, true);
   }

   public static SchemaBinding bind(XSModel model, SchemaBindingResolver resolver, boolean processAnnotations)
   {
      XsdBinder binder = new XsdBinder();
      binder.setProcessAnnotations(processAnnotations);
      binder.setSchemaResolver(resolver);      
      return binder.parse(model);
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
      XsdBinder binder = new XsdBinder(schema);
      TypeBinding typeBinding = binder.bindType(type);
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
      XsdBinder binder = new XsdBinder(schema);
      ParticleBinding particle = binder.bindElement(element,
         minOccurs,
         maxOccurs,
         maxOccursUnbounded
      );
      schema.addElementParticle(particle);
   }

   // Exposed attributes
   
   private boolean processAnnotations = true;
   private SchemaBindingResolver resolver;
   private boolean simpleContentWithIdAsSimpleType = true;
   
   // Internal attributes
   
   private boolean trace = log.isTraceEnabled();
   private final SchemaBinding schema;
   private SharedElements sharedElements = new SharedElements();
   private final List typeGroupStack = new ArrayList();

   // Ctors
   
   private XsdBinder()
   {
      this(new SchemaBinding());
   }

   private XsdBinder(SchemaBinding schema)
   {
      this.schema = schema;
   }

   // Public
   
   public void setProcessAnnotations(boolean processAnnotations)
   {
      this.processAnnotations = processAnnotations;
   }

   public boolean isProcessAnnotations()
   {
      return processAnnotations;
   }

   public void setSchemaResolver(SchemaBindingResolver resolver)
   {
      this.resolver = resolver;
   }

   public SchemaBindingResolver getSchemaResolver()
   {
      return resolver;
   }

   public void setSimpleContentWithIdAsSimpleType(boolean simpleContentWithIdAsSimpleType)
   {
      this.simpleContentWithIdAsSimpleType = simpleContentWithIdAsSimpleType;
   }

   public boolean isSimpleContentWithIdAsSimpleType()
   {
      return simpleContentWithIdAsSimpleType;
   }

   public SchemaBinding parse(String xsdUrl)
   {
      if(resolver == null)
      {
         resolver = new DefaultSchemaResolver();
      }

      XSModel model = Util.loadSchema(xsdUrl, resolver);
      return parse(model);
   }
   
   public SchemaBinding parse(InputStream xsdStream, String encoding)
   {
      if(resolver == null)
      {
         resolver = new DefaultSchemaResolver();
      }

      XSModel model = Util.loadSchema(xsdStream, encoding, resolver);
      return parse(model);
   }
   
   public SchemaBinding parse(Reader xsdReader, String encoding)
   {
      if(resolver == null)
      {
         resolver = new DefaultSchemaResolver();
      }
  
      XSModel model = Util.loadSchema(xsdReader, encoding, resolver);
      return parse(model);
   }

   
   // Private

   private SchemaBinding parse(XSModel model)
   {      
      schema.setSchemaResolver(resolver);

      // read annotations. for now just log the ones that are going to be used
      if (processAnnotations)
      {
         XSObjectList annotations = model.getAnnotations();
         if (trace)
         {
            log.trace("started binding schema " + schema);
            log.trace("Schema annotations: " + annotations.getLength());
         }

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
      }

      StringList namespaceList = model.getNamespaces();
      Set namespaces = new LinkedHashSet(namespaceList.getLength());
      for (int i = 0; i < namespaceList.getLength(); ++i)
         namespaces.add(namespaceList.item(i));
      schema.setNamespaces(namespaces);
      
      XSNamedMap groups = model.getComponents(XSConstants.MODEL_GROUP_DEFINITION);
      if (trace)
         log.trace("Model groups: " + groups.getLength());
      // First make sure we bind all the groups
      for(int i = 0; i < groups.getLength(); ++i)
      {
         XSModelGroupDefinition groupDef = (XSModelGroupDefinition)groups.item(i);
         bindGlobalGroup(groupDef);
      }
      // Now bind the particles which may have references to the other groups
      for(int i = 0; i < groups.getLength(); ++i)
      {
         XSModelGroupDefinition groupDef = (XSModelGroupDefinition)groups.item(i);
         bindGlobalGroupParticles(groupDef.getModelGroup());
      }

      XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
      if (trace)
         log.trace("Model types: " + types.getLength());
      for(int i = 0; i < types.getLength(); ++i)
      {
         XSTypeDefinition type = (XSTypeDefinition)types.item(i);
         if(!Constants.NS_XML_SCHEMA.equals(type.getNamespace()))
         {
            bindType(type);
         }
      }

      XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      if (trace)
         log.trace("Model elements: " + types.getLength());
      for(int i = 0; i < elements.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)elements.item(i);
         bindElement(element, 1, 0, false);
      }

      if (trace)
      {
         log.trace("finished binding schema " + schema);
      }

      return schema;
   }

   // Private
   
   private TypeBinding bindType(XSTypeDefinition type)
   {
      TypeBinding binding;
      switch(type.getTypeCategory())
      {
         case XSTypeDefinition.SIMPLE_TYPE:
            binding = bindSimpleType((XSSimpleTypeDefinition)type);
            break;
         case XSTypeDefinition.COMPLEX_TYPE:
            binding = bindComplexType((XSComplexTypeDefinition)type);
            break;
         default:
            throw new JBossXBRuntimeException("Unexpected type category: " + type.getTypeCategory());
      }
      return binding;
   }

   private TypeBinding bindSimpleType(XSSimpleTypeDefinition type)
   {
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : schema.getType(typeName);
      if(binding != null)
      {
         return binding;
      }

      if(trace)
      {
         log.trace("binding simple type " + typeName);
      }

      XSTypeDefinition baseTypeDef = type.getBaseType();
      TypeBinding baseType = baseTypeDef == null ? null : bindType(baseTypeDef);

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
         TypeBinding itemType = bindSimpleType(type.getItemType());
         binding.setItemType(itemType);
      }

      if(typeName != null)
      {
         schema.addType(binding);
      }

      if(trace)
      {
         String msg = typeName == null ? "bound simple anonymous type" : "bound simple type " + typeName;
         if(baseType != null)
         {
            msg += " inherited binding metadata from " + baseType.getQName();
         }
         log.trace(msg);
      }

      // customize binding with annotations
      if(processAnnotations)
      {
         XSObjectList annotations = type.getAnnotations();
         if(annotations != null)
         {
            if(trace)
            {
               log.trace(typeName + " annotations " + annotations.getLength());
            }
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
                     if(trace)
                     {
                        log.trace("simple type " +
                           type.getName() +
                           ": impl=" +
                           classMetaData.getImpl());
                     }
                     binding.setClassMetaData(classMetaData);
                  }

                  ValueMetaData valueMetaData = appInfo.getValueMetaData();
                  if(valueMetaData != null)
                  {
                     if(trace)
                     {
                        log.trace("simple type " +
                           type.getName() +
                           ": unmarshalMethod=" +
                           valueMetaData.getUnmarshalMethod() +
                           ", marshalMethod=" +
                           valueMetaData.getMarshalMethod());
                     }
                     binding.setValueMetaData(valueMetaData);
                  }
               }
            }
         }
      }

      binding.setSchemaBinding(schema);

      return binding;
   }

   private TypeBinding bindComplexType(XSComplexTypeDefinition type)
   {
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : schema.getType(typeName);
      if(binding != null)
      {
         return binding;
      }

      XSTypeDefinition baseTypeDef = type.getBaseType();
      // anyType is the parent of all the types, even the parent of itself according to xerces :)
      TypeBinding baseType = null;
      if(baseTypeDef != null && !Constants.QNAME_ANYTYPE.equals(typeName))
      {
         baseType = bindType(baseTypeDef);
         // sometimes binding the base type can lead to another request
         // to bind the type being bound here
         if(typeName != null)
         {
            binding = schema.getType(typeName);
            if(binding != null)
            {
               return binding;
            }
         }
      }

      if (trace)
         log.trace("binding complex " + (typeName == null ? "anonymous type" : "type " + typeName));

      binding = new TypeBinding(typeName);
      binding.setBaseType(baseType);
      binding.setSimple(false);

      if(type.getSimpleType() != null)
      {
         TypeBinding simpleType = bindSimpleType(type.getSimpleType());
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

      binding.setSchemaBinding(schema);

      XSObjectList attrs = type.getAttributeUses();
      if (trace)
         log.trace(typeName + " attributes " + attrs.getLength());

      AttributeBinding attrBinding = null;
      boolean hasOnlyIdAttrs = true;
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         XSAttributeUse attr = (XSAttributeUse)attrs.item(i);
         attrBinding = bindAttribute(attr);
         binding.addAttribute(attrBinding);
         if(hasOnlyIdAttrs && !Constants.QNAME_ID.equals(attrBinding.getType().getQName()))
         {
            hasOnlyIdAttrs = false;
         }
      }
      
      // customize binding with xsd annotations
      if (processAnnotations)
      {
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
      }

      XSParticle particle = type.getParticle();
      if(particle != null)
      {
         pushType(binding);
         bindParticle(particle);
         popType();
      }

      if(binding.getClassMetaData() == null &&
            simpleContentWithIdAsSimpleType &&
            particle == null && hasOnlyIdAttrs)
      {
         binding.setStartElementCreatesObject(false);
      }
      else
      {
         binding.setStartElementCreatesObject(true);
      }
      
      if(binding.hasOnlyXmlMimeAttributes())
      {
         addXOPInclude(binding, schema);
      }

      if(trace)
      {
         log.trace(typeName == null ? "bound complex anonymous type" : "bound complex type " + typeName);
      }

      return binding;
   }

   private AttributeBinding bindAttribute(XSAttributeUse attrUse)
   {
      XSAttributeDeclaration attr = attrUse.getAttrDeclaration();
      QName attrName = new QName(attr.getNamespace(), attr.getName());

      XSSimpleTypeDefinition attrType = attr.getTypeDefinition();
      TypeBinding typeBinding = bindSimpleType(attrType);

      if (trace)
      {
         log.trace("binding attribute " + attrName + ", required=" + attrUse.getRequired());
      }

      AttributeBinding binding = new AttributeBinding(schema, attrName, typeBinding, DefaultHandlers.ATTRIBUTE_HANDLER);
      binding.setRequired(attrUse.getRequired());
      if(attrUse.getConstraintType() == XSConstants.VC_DEFAULT)
      {
         // Associate the default value with the binding
         binding.setDefaultConstraint(attrUse.getConstraintValue());
      }

      if (processAnnotations)
      {
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
            msg += " type=" + attrType.getName();
         }

         if(binding.getDefaultConstraint() != null)
         {
            msg += ", default=" + binding.getDefaultConstraint();
         }

         log.trace(msg);
      }
      
      return binding;
   }

   private void bindParticle(XSParticle particle)
   {
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            XSModelGroup modelGroup = (XSModelGroup)term;
            // todo: investigate this
            if(modelGroup.getParticles().getLength() > 0)
            {
               ModelGroupBinding groupBinding = bindModelGroup(modelGroup);

               ParticleBinding particleBinding = new ParticleBinding(groupBinding);
               particleBinding.setMaxOccursUnbounded(particle.getMaxOccursUnbounded());
               particleBinding.setMinOccurs(particle.getMinOccurs());
               particleBinding.setMaxOccurs(particle.getMaxOccurs());

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

               if (groupBinding.getParticles().isEmpty())
               {
                  pushModelGroup(groupBinding);
                  bindModelGroupParticles(modelGroup);
                  popModelGroup();
               }
            }
            break;
         case XSConstants.WILDCARD:
            bindWildcard(particle);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(
               (XSElementDeclaration)term,
               particle.getMinOccurs(),
               particle.getMaxOccurs(),
               particle.getMaxOccursUnbounded()
            );
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
   }

   private ModelGroupBinding bindModelGroup(XSModelGroup modelGroup)
   {
      // Is this a global group?
      ModelGroupBinding groupBinding = sharedElements.getGlobalGroup(modelGroup);
      if (groupBinding != null)
         return groupBinding;
      
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

      if (trace)
         log.trace("created model group " + groupBinding);

      if (processAnnotations)
      {
         XSAnnotation annotation = modelGroup.getAnnotation();
         if (annotation != null)
            customizeTerm(annotation, groupBinding, trace);
      }
      return groupBinding;
   }

   private void bindWildcard(XSParticle particle)
   {
      WildcardBinding binding = new WildcardBinding(schema);

      ModelGroupBinding group = (ModelGroupBinding)peekTypeOrGroup();
      ParticleBinding particleBinding = new ParticleBinding(binding);
      particleBinding.setMaxOccurs(particle.getMaxOccurs());
      particleBinding.setMaxOccursUnbounded(particle.getMaxOccursUnbounded());
      particleBinding.setMinOccurs(particle.getMinOccurs());
      group.addParticle(particleBinding);

      XSWildcard wildcard = (XSWildcard)particle.getTerm();
      if(wildcard.getName() != null)
      {
         binding.setQName(new QName(wildcard.getNamespace(), wildcard.getName()));
      }

      binding.setProcessContents(wildcard.getProcessContents());

      if (processAnnotations)
      {
         XSAnnotation annotation = wildcard.getAnnotation();
         if(annotation != null)
         {
            customizeTerm(annotation, binding, trace);
         }
      }
   }

   private ParticleBinding bindElement(XSElementDeclaration elementDec,
                                       int minOccurs,
                                       int maxOccurs,
                                       boolean maxOccursUnbounded)
   {
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

         particle.setMinOccurs(minOccurs);
         if(maxOccursUnbounded)
         {
            particle.setMaxOccursUnbounded(maxOccursUnbounded);
         }
         else
         {
            particle.setMaxOccurs(maxOccurs);
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
         type = bindType(elementDec.getTypeDefinition());
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
         QName parentQName = null;
         if (parentType != null)
            parentQName = parentType.getQName();
         log.trace("element: name=" +
            qName +
            ", type=" +
            type.getQName() +
            ", repeatable=" +
            particle.isRepeatable() +
            ", nillable=" +
            element.isNillable() +
            ", minOccurs=" + minOccurs +
            ", maxOccurs=" + (maxOccursUnbounded ? "unbounded" : "" + maxOccurs) +
            ", " + (global ? "global scope" : " owner type=" + parentQName)
         );
      }

      // customize element with annotations
      if (processAnnotations)
      {
         XSAnnotation an = elementDec.getAnnotation();
         if(an != null)
         {
            customizeTerm(an, element, trace);
         }
      }
      return particle;
   }

   private void bindModelGroupParticles(XSModelGroup modelGroup)
   {
      XSObjectList particles = modelGroup.getParticles();
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         bindParticle(particle);
      }
   }

   // Private

   private static void addXOPInclude(TypeBinding binding, SchemaBinding schema)
   {
      binding.setHandler(DefaultHandlers.XOP_HANDLER);
      if(binding.getParticle() != null)
      {
         throw new JBossXBRuntimeException(
            "XOP optimizable type has a particle which is unexpected, please, open a JIRA issue!"
         );
      }

      TypeBinding anyUriType = schema.getType(Constants.QNAME_ANYURI);
      if(anyUriType == null)
      {
         log.warn("Type " + Constants.QNAME_ANYURI + " not bound.");
      }

      TypeBinding xopIncludeType = new TypeBinding(new QName(Constants.NS_XOP_INCLUDE, "Include"));
      xopIncludeType.setSchemaBinding(schema);
      xopIncludeType.addAttribute(new QName("href"), anyUriType, DefaultHandlers.ATTRIBUTE_HANDLER);
      xopIncludeType.setHandler(new XOPIncludeHandler(binding));

      ElementBinding xopInclude = new ElementBinding(schema, new QName(Constants.NS_XOP_INCLUDE, "Include"), xopIncludeType);

      ParticleBinding particleBinding = new ParticleBinding(xopInclude);
      particleBinding.setMinOccurs(0);

      binding.addParticle(particleBinding);
   }

   private static void customizeTerm(XSAnnotation an, TermBinding term, boolean trace)
   {
      XsdAnnotation xsdAn = XsdAnnotation.unmarshal(an.getAnnotationString());
      XsdAppInfo appInfo = xsdAn.getAppInfo();
      if(appInfo != null)
      {
         Boolean skip = null;

         ClassMetaData classMetaData = appInfo.getClassMetaData();
         if(classMetaData != null)
         {
            if (trace)
            {
               String msg;
               if(term.isModelGroup())
               {
                  msg = term + " bound to ";
               }
               else if(term.isWildcard())
               {
                  msg = " wildcard bound to ";
               }
               else
               {
                  msg = "element: name=" + ((ElementBinding)term).getQName() + ", class=";
               }

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
               String msg = term.isWildcard() || term.isModelGroup() ? term + " " : "element: name=" +
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element name=" +
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
                  (term.isWildcard() || term.isModelGroup() ? term.toString() : ((ElementBinding)term).getQName().toString());
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element: name=" +
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element: name=" +
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element " +
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element name=" +
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element name=" +
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
               String msg = term.isWildcard() || term.isModelGroup() ? term.toString() : "element name=" +
                  ((ElementBinding)term).getQName();
               msg += ": will be skipped, it's attributes, character content and children will be set on the parent";
               log.trace(msg);
            }
            term.setSkip(skipAnnotation ? Boolean.TRUE : Boolean.FALSE);
         }
      }
   }

   private void bindGlobalGroup(XSModelGroupDefinition groupDef)
   {
      QName groupName = new QName(groupDef.getNamespace(), groupDef.getName());
      XSModelGroup group = groupDef.getModelGroup();
      ModelGroupBinding groupBinding = bindModelGroup(group);
      groupBinding.setQName(groupName);
      sharedElements.addGlobalGroup(group, groupBinding);
      schema.addGroup(groupName, groupBinding);
   }

   private void bindGlobalGroupParticles(XSModelGroup group)
   {
      ModelGroupBinding groupBinding = sharedElements.getGlobalGroup(group);
      if (groupBinding.getParticles().isEmpty())
      {
         pushModelGroup(groupBinding);
         bindModelGroupParticles(group);
         popModelGroup();
      }
   }

   private void popType()
   {
      Object o = typeGroupStack.remove(typeGroupStack.size() - 1);
      if(!(o instanceof TypeBinding))
      {
         throw new JBossXBRuntimeException("Should have poped type binding but got " + o);
      }
   }

   private void pushType(TypeBinding binding)
   {
      typeGroupStack.add(binding);
   }

   private void popModelGroup()
   {
      Object o = typeGroupStack.remove(typeGroupStack.size() - 1);
      if(!(o instanceof ModelGroupBinding))
      {
         throw new JBossXBRuntimeException("Should have poped model group binding but got " + o);
      }
   }

   private void pushModelGroup(ModelGroupBinding binding)
   {
      typeGroupStack.add(binding);
   }

   private Object peekTypeOrGroup()
   {
      return typeGroupStack.isEmpty() ? null : typeGroupStack.get(typeGroupStack.size() - 1);
   }

   private TypeBinding peekType()
   {
      TypeBinding binding = null;
      for(ListIterator i = typeGroupStack.listIterator(typeGroupStack.size()); i.hasPrevious();)
      {
         Object o = i.previous();
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

      private Map globalGroups = Collections.EMPTY_MAP;
      
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
         switch(elements.size())
         {
            case 0:
               elements = Collections.singletonMap(element, type);
               break;
            case 1:
               elements = new HashMap(elements);
            default:
               elements.put(element, type);
         }
      }
      
      public void addGlobalGroup(XSModelGroup group, ModelGroupBinding groupBinding)
      {
         switch(globalGroups.size())
         {
            case 0:
               globalGroups = Collections.singletonMap(group, groupBinding);
               break;
            case 1:
               globalGroups = new HashMap(globalGroups);
            default:
               globalGroups.put(group, groupBinding);
         }
      }

      public ModelGroupBinding getGlobalGroup(XSModelGroup group)
      {
         return (ModelGroupBinding) globalGroups.get(group);
      }
   }
}
