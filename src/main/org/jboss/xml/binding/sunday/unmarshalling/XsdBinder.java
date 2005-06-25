/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Constants;
import org.jboss.xml.binding.sunday.unmarshalling.impl.runtime.RtAttributeHandler;
import org.jboss.xml.binding.metadata.XsdAnnotation;
import org.jboss.xml.binding.metadata.XsdAppInfo;
import org.jboss.xml.binding.metadata.SchemaMetaData;
import org.jboss.xml.binding.metadata.PackageMetaData;
import org.jboss.xml.binding.metadata.ClassMetaData;
import org.jboss.xml.binding.metadata.ValueMetaData;
import org.jboss.xml.binding.metadata.PropertyMetaData;
import org.jboss.xml.binding.metadata.MapEntryMetaData;
import org.jboss.xml.binding.metadata.PutMethodMetaData;
import org.jboss.xml.binding.metadata.AddMethodMetaData;
import org.jboss.xml.binding.metadata.CharactersMetaData;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSWildcard;
import org.apache.xerces.dom3.bootstrap.DOMImplementationRegistry;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdBinder
{
   private static final Logger log = Logger.getLogger(XsdBinder.class);

   private static final ThreadLocal xsdBinding = new ThreadLocal()
   {
      protected Object initialValue()
      {
         return new XsdBinding();
      }
   };

   private static XsdBinding getXsdBinding()
   {
      return (XsdBinding)xsdBinding.get();
   }

   private XsdBinder()
   {
   }

   public static final SchemaBinding bind(String xsdUrl)
   {
      XSModel model = loadSchema(xsdUrl);
      SchemaBinding schema = getXsdBinding().schemaBinding;

      // read annotations. for now just log the ones that are going to be used
      XSObjectList annotations = model.getAnnotations();
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
               PackageMetaData packageMetaData = schemaBindings.getPackage();
               if(packageMetaData != null)
               {
                  if(log.isTraceEnabled())
                  {
                     log.trace("schema default package: " + packageMetaData.getName());
                  }
                  schema.setPackageMetaData(packageMetaData);
               }
            }
         }
      }

      XSTypeDefinition anyType = model.getTypeDefinition(Constants.QNAME_ANYTYPE.getLocalPart(),
         Constants.QNAME_ANYTYPE.getNamespaceURI()
      );
      if(anyType == null)
      {
         throw new JBossXBRuntimeException("Unable to get a refence to " + Constants.QNAME_ANYTYPE);
      }

      SharedElements sharedElements = new SharedElements();
      // this is just caching of the reference to easier compare types at runtime
      sharedElements.anyType = anyType;

      XSNamedMap groups = model.getComponents(XSConstants.MODEL_GROUP_DEFINITION);
      for(int i = 0; i < groups.getLength(); ++i)
      {
         XSModelGroupDefinition groupDef = (XSModelGroupDefinition)groups.item(i);
         XSModelGroup group = groupDef.getModelGroup();
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
                  break;
               case XSConstants.MODEL_GROUP:
               default:
                  throw new JBossXBRuntimeException(
                     "For now we don't support anything but elements in global model groups"
                  );
            }

         }
      }

      XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
      for(int i = 0; i < types.getLength(); ++i)
      {
         XSTypeDefinition type = (XSTypeDefinition)types.item(i);
         if(!Constants.NS_XML_SCHEMA.equals(type.getNamespace()))
         {
            bindType(schema, type, sharedElements);
         }
      }

      XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      for(int i = 0; i < elements.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)elements.item(i);
         bindElement(schema, element, sharedElements, false);
      }

      return schema;
   }

   private static final TypeBinding bindType(SchemaBinding doc,
                                             XSTypeDefinition type,
                                             SharedElements sharedElements)
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

   private static final TypeBinding bindSimpleType(SchemaBinding doc, XSSimpleTypeDefinition type)
   {
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : doc.getType(typeName);
      if(binding == null)
      {
         XSTypeDefinition baseTypeDef = type.getBaseType();
         TypeBinding baseType = baseTypeDef == null ? null : bindType(doc, baseTypeDef, null);

         binding = baseType == null ? new TypeBinding(typeName) : new TypeBinding(typeName, baseType);

         if(typeName != null)
         {
            doc.addType(binding);
         }

         if(log.isTraceEnabled())
         {
            String msg = typeName == null ? "simple anonymous type" : "simple type " + typeName;
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
                     if(log.isTraceEnabled())
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
                     if(log.isTraceEnabled())
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

         binding.setSchemaBinding(doc);
      }
      return binding;
   }

   private static final TypeBinding bindComplexType(SchemaBinding doc,
                                                    XSComplexTypeDefinition type,
                                                    SharedElements sharedElements)
   {
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : doc.getType(typeName);
      if(binding == null)
      {
         //XSTypeDefinition baseTypeDef = type.getBaseType();
         // anyType is the parent of all the types, even the parent of itself according to xerces :)
         TypeBinding baseType = null; /* todo: review binding inheritance for complex types
         (baseTypeDef == sharedElements.anyType ?
            null :
            bindType(doc, baseTypeDef, sharedElements));*/
         binding = (baseType == null ? new TypeBinding(typeName) : new TypeBinding(typeName, baseType));

         if(typeName != null)
         {
            doc.addType(binding);
         }

         if(log.isTraceEnabled())
         {
            String msg = typeName == null ? "complex anonymous type" : "complex type " + typeName;
            if(baseType != null)
            {
               msg += " inherited binding metadata from " + baseType.getQName();
            }
            log.trace(msg);
         }

         binding.setSchemaBinding(doc);

         XSObjectList attrs = type.getAttributeUses();
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            XSAttributeUse attr = (XSAttributeUse)attrs.item(i);
            bindAttributes(doc, binding, attr.getAttrDeclaration());
         }

         // customize binding with xsd annotations
         XSObjectList annotations = type.getAnnotations();
         if(annotations != null)
         {
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
                     if(log.isTraceEnabled())
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
                     if(log.isTraceEnabled())
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
                     if(log.isTraceEnabled())
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
                     if(log.isTraceEnabled())
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
                     if(log.isTraceEnabled())
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
                     if(log.isTraceEnabled())
                     {
                        log.trace("complex type " + type.getName() +
                           ": elements of this type will be added to parent objects with addMethod=" +
                           addMethodMetaData.getMethodName() + ", valueType=" + addMethodMetaData.getValueType());
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
            bindParticle(doc, particle, sharedElements);
            popType();
         }
      }
      return binding;
   }

   private static void bindAttributes(SchemaBinding doc,
                                      TypeBinding type,
                                      XSAttributeDeclaration attr)
   {
      XSSimpleTypeDefinition attrType = attr.getTypeDefinition();
      TypeBinding typeBinding = bindSimpleType(doc, attrType);
      QName attrName = new QName(attr.getNamespace(), attr.getName());
      AttributeBinding binding = type.addAttribute(attrName, typeBinding, RtAttributeHandler.INSTANCE);

      XSAnnotation an = attr.getAnnotation();
      if(an != null)
      {
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

      if(log.isTraceEnabled())
      {
         String msg = "attribute " +
            new QName(attr.getNamespace(), attr.getName()) +
            ": ";

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

         log.trace(msg);
      }
   }

   private static void bindParticle(SchemaBinding schema, XSParticle particle, SharedElements sharedElements)
   {
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            bindModelGroup(schema, (XSModelGroup)term, sharedElements);
            break;
         case XSConstants.WILDCARD:
            bindWildcard(schema, (XSWildcard)term);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(schema,
               (XSElementDeclaration)term,
               sharedElements,
               particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1
            );
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
   }

   private static void bindWildcard(SchemaBinding schema, XSWildcard wildcard)
   {
      TypeBinding typeBinding = peekType();
      typeBinding.setSchemaResolver(schema);

      XSAnnotation annotation = wildcard.getAnnotation();
      if(annotation != null)
      {
         XsdAnnotation xsdAn = XsdAnnotation.unmarshal(annotation.getAnnotationString());
         XsdAppInfo appInfo = xsdAn.getAppInfo();
         if(appInfo != null)
         {
            PropertyMetaData propertyMetaData = appInfo.getPropertyMetaData();
            if(propertyMetaData != null)
            {
               if(log.isTraceEnabled())
               {
                  log.trace("wildcard is bound to property: " + propertyMetaData.getName() +
                     ", collectionType=" + propertyMetaData.getCollectionType());
               }
            }
            typeBinding.setWildcardPropertyMetaData(propertyMetaData);
         }
      }
   }

   private static void bindElement(SchemaBinding doc,
                                   XSElementDeclaration element,
                                   SharedElements sharedElements,
                                   boolean multiOccurs)
   {
      QName qName = new QName(element.getNamespace(), element.getName());

      TypeBinding parentType = peekType();
      ElementBinding binding = parentType == null ? null : parentType.getElement(qName);

      if(binding == null)
      {
         TypeBinding type = null;

         boolean shared = sharedElements.isShared(element);
         if(shared)
         {
            type = sharedElements.getTypeBinding(element);
         }

         if(type == null)
         {
            type = bindType(doc, element.getTypeDefinition(), sharedElements);
            if(shared)
            {
               sharedElements.setTypeBinding(element, type);
            }
         }

         boolean global = element.getScope() == XSConstants.SCOPE_GLOBAL;
         if(global)
         {
            binding = doc.getElement(qName);
         }

         if(binding == null)
         {
            binding = new ElementBinding(doc, type);
            binding.setMultiOccurs(multiOccurs);
            if(global)
            {
               doc.addElement(qName, binding);
            }

            // customize binding with annotations
            XSAnnotation an = element.getAnnotation();
            if(an != null)
            {
               XsdAnnotation xsdAn = XsdAnnotation.unmarshal(an.getAnnotationString());
               XsdAppInfo appInfo = xsdAn.getAppInfo();
               if(appInfo != null)
               {
                  ClassMetaData classMetaData = appInfo.getClassMetaData();
                  if(classMetaData != null)
                  {
                     log.trace("element: name=" +
                        new QName(element.getNamespace(), element.getName()) +
                        ", class=" +
                        classMetaData.getImpl()
                     );
                     binding.setClassMetaData(classMetaData);
                  }

                  PropertyMetaData propertyMetaData = appInfo.getPropertyMetaData();
                  if(propertyMetaData != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("element: name=" +
                           new QName(element.getNamespace(), element.getName()) +
                           ", property=" +
                           propertyMetaData.getName() +
                           ", collectionType=" + propertyMetaData.getCollectionType()
                        );
                     }
                     binding.setPropertyMetaData(propertyMetaData);
                  }

                  MapEntryMetaData mapEntryMetaData = appInfo.getMapEntryMetaData();
                  if(mapEntryMetaData != null)
                  {
                     if(propertyMetaData != null)
                     {
                        throw new JBossXBRuntimeException("An element can be bound either as a property or as a map" +
                           " entry but not both: " +
                           new QName(element.getNamespace(), element.getName())
                        );
                     }

                     if(log.isTraceEnabled())
                     {
                        log.trace("element name=" +
                           new QName(element.getNamespace(), element.getName()) +
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
                        throw new JBossXBRuntimeException("Invalid binding: both jbxb:class and jbxb:mapEntry are specified for element " +
                           new QName(element.getNamespace(), element.getName())
                        );
                     }
                     binding.setMapEntryMetaData(mapEntryMetaData);
                  }

                  PutMethodMetaData putMethodMetaData = appInfo.getPutMethodMetaData();
                  if(putMethodMetaData != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("element: name=" +
                           new QName(element.getNamespace(), element.getName()) +
                           ", putMethod=" +
                           putMethodMetaData.getName() +
                           ", keyType=" +
                           putMethodMetaData.getKeyType() +
                           ", valueType=" + putMethodMetaData.getValueType()
                        );
                     }
                     binding.setPutMethodMetaData(putMethodMetaData);
                  }

                  AddMethodMetaData addMethodMetaData = appInfo.getAddMethodMetaData();
                  if(addMethodMetaData != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("element: name=" +
                           new QName(element.getNamespace(), element.getName()) +
                           ", addMethod=" +
                           addMethodMetaData.getMethodName() +
                           ", valueType=" +
                           addMethodMetaData.getValueType() +
                           ", isChildType=" + addMethodMetaData.isChildType()
                        );
                     }
                     binding.setAddMethodMetaData(addMethodMetaData);
                  }

                  ValueMetaData valueMetaData = appInfo.getValueMetaData();
                  if(valueMetaData != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("element " +
                           new QName(element.getNamespace(), element.getName()) +
                           ": unmarshalMethod=" + valueMetaData.getUnmarshalMethod()
                        );
                     }
                     binding.setValueMetaData(valueMetaData);
                  }

                  boolean mapEntryKey = appInfo.isMapEntryKey();
                  if(mapEntryKey)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("element name=" +
                           new QName(element.getNamespace(), element.getName()) +
                           ": is bound to a key in a map entry"
                        );
                     }
                     binding.setMapEntryKey(mapEntryKey);
                  }

                  boolean mapEntryValue = appInfo.isMapEntryValue();
                  if(mapEntryValue)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("element name=" +
                           new QName(element.getNamespace(), element.getName()) +
                           ": is bound to a value in a map entry"
                        );
                     }
                     binding.setMapEntryValue(mapEntryValue);
                  }

                  boolean skip = appInfo.isSkip();
                  if(skip)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace(
                           "element name=" +
                           new QName(element.getNamespace(), element.getName()) +
                           ": will be skipped, it's attributes, character content and children will be set on the parent"
                        );
                     }
                     binding.setSkip(skip);
                  }
               }
            }
         }

         if(parentType != null)
         {
            parentType.addElement(qName, binding);
            if(log.isTraceEnabled())
            {
               log.trace("element: name=" +
                  qName +
                  ", type=" +
                  type.getQName() +
                  ", multiOccurs=" +
                  binding.isMultiOccurs() +
                  ", owner type=" +
                  parentType.getQName()
               );
            }
         }
      }
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

   private static XSModel loadSchema(String xsdURL)
   {
      log.debug("loading xsd: " + xsdURL);

      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      XSModel model = schemaLoader.loadURI(xsdURL);
      if(model == null)
      {
         throw new IllegalArgumentException("Invalid URI for schema: " + xsdURL);
      }

      return model;
   }

   private static XSImplementation getXSImplementation()
   {
      // Get DOM Implementation using DOM Registry
      System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl");

      XSImplementation impl;
      try
      {
         DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
         impl = (XSImplementation)registry.getDOMImplementation("XS-Loader");
      }
      catch(Exception e)
      {
         log.error("Failed to create schema loader.", e);
         throw new IllegalStateException("Failed to create schema loader: " + e.getMessage());
      }
      return impl;
   }

   private static void popType()
   {
      getXsdBinding().typeStack.removeLast();
   }

   private static void pushType(TypeBinding binding)
   {
      getXsdBinding().typeStack.addLast(binding);
   }

   private static TypeBinding peekType()
   {
      LinkedList typeStack = getXsdBinding().typeStack;
      return (TypeBinding)(typeStack.isEmpty() ? null : typeStack.getLast());
   }

   private static final class SharedElements
   {
      private Map elements = Collections.EMPTY_MAP;
      private XSTypeDefinition anyType;

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

   // Inner

   private static final class XsdBinding
   {
      public final LinkedList typeStack = new LinkedList();
      public final SchemaBinding schemaBinding = new SchemaBinding();
   }
}
