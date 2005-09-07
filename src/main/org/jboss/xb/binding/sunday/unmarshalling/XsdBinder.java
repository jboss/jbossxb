/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;
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
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtAttributeHandler;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMLocator;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdBinder
{
   // this constant is a temporary one to test model group bindings
   public static boolean MODELGROUPS = true;

   private static final Logger log = Logger.getLogger(XsdBinder.class);

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
      SchemaBinding schema = getXsdBinding().schemaBinding;
      schema.setSchemaResolver(resolver);

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
               // Get the ignoreUnresolvedFieldOrClass
               schema.setIgnoreUnresolvedFieldOrClass(schemaBindings.isIgnoreUnresolvedFieldOrClass());
               // Get the ignoreUnresolvedFieldOrClass
               schema.setReplacePropertyRefs(schemaBindings.isReplacePropertyRefs());
               // Get the default package
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

      SharedElements sharedElements = new SharedElements();

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
         bindElement(schema, element, sharedElements, 1, 0, false);
      }

      xsdBinding.set(null);
      return schema;
   }

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

   private static TypeBinding bindSimpleType(SchemaBinding doc, XSSimpleTypeDefinition type)
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

   private static TypeBinding bindComplexType(SchemaBinding doc,
                                              XSComplexTypeDefinition type,
                                              SharedElements sharedElements)
   {
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : doc.getType(typeName);
      if(binding != null)
      {
         return binding;
      }

      //XSTypeDefinition baseTypeDef = type.getBaseType();
      // anyType is the parent of all the types, even the parent of itself according to xerces :)
      TypeBinding baseType = null; /* todo: review binding inheritance for complex types
         (baseTypeDef == sharedElements.anyType ?
            null :
            bindType(doc, baseTypeDef, sharedElements));*/
      binding = (baseType == null ? new TypeBinding(typeName) : new TypeBinding(typeName, baseType));
      binding.setStartElementCreatesObject(true);

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
         bindAttributes(doc, binding, attr);
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
                     throw new JBossXBRuntimeException(
                        "Illegal binding: both jbxb:class and jbxb:mapEntry are specified for complex type " +
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
         bindParticle(doc, particle, sharedElements);
         popType();
      }

      return binding;
   }

   private static void bindAttributes(SchemaBinding doc,
                                      TypeBinding type,
                                      XSAttributeUse attrUse)
   {
      XSAttributeDeclaration attr = attrUse.getAttrDeclaration();
      XSSimpleTypeDefinition attrType = attr.getTypeDefinition();
      TypeBinding typeBinding = bindSimpleType(doc, attrType);
      QName attrName = new QName(attr.getNamespace(), attr.getName());
      AttributeBinding binding = type.addAttribute(attrName, typeBinding, RtAttributeHandler.INSTANCE);
      if(attrUse.getConstraintType() == XSConstants.VC_DEFAULT)
      {
         // Associate the default value with the binding
         binding.setDefaultConstraint(attrUse.getConstraintValue());
      }

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

         if(binding.getDefaultConstraint() != null)
         {
            msg += ", default=" + binding.getDefaultConstraint();
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
            XSModelGroup modelGroup = (XSModelGroup)term;

            if(MODELGROUPS)
            {
               ModelGroupBinding binding;
               switch(modelGroup.getCompositor())
               {
                  case XSModelGroup.COMPOSITOR_ALL:
                     binding = new AllBinding();
                     break;
                  case XSModelGroup.COMPOSITOR_CHOICE:
                     binding = new ChoiceBinding();
                     break;
                  case XSModelGroup.COMPOSITOR_SEQUENCE:
                     binding = new SequenceBinding();
                     break;
                  default:
                     throw new JBossXBRuntimeException("Unexpected model group: " + modelGroup.getCompositor());
               }

               if(log.isTraceEnabled())
               {
                  log.trace("created model group " + binding);
               }

               Object o = peekTypeOrGroup();
               if(o instanceof ModelGroupBinding)
               {
                  ModelGroupBinding parentGroup = (ModelGroupBinding)o;
                  parentGroup.addModelGroup(binding);
                  if(log.isTraceEnabled())
                  {
                     log.trace("added " + binding + " to type group " + parentGroup);
                  }
               }
               else if(o instanceof TypeBinding)
               {
                  TypeBinding typeBinding = (TypeBinding)o;
                  typeBinding.setModelGroup(binding);
                  if(log.isTraceEnabled())
                  {
                     log.trace("added " + binding + " to type " + typeBinding.getQName());
                  }
               }

               pushModelGroup(binding);
            }

            bindModelGroup(schema, modelGroup, sharedElements);

            if(MODELGROUPS)
            {
               popModelGroup();
            }

            break;
         case XSConstants.WILDCARD:
            bindWildcard(schema, (XSWildcard)term);
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

   private static void bindWildcard(SchemaBinding schema, XSWildcard wildcard)
   {
      WildcardBinding binding = new WildcardBinding();
      binding.setSchema(schema);

      Object o = peekTypeOrGroup();
      TypeBinding type = null;
      ModelGroupBinding group = null;
      if(o instanceof ModelGroupBinding)
      {
         group = (ModelGroupBinding)o;
         group.setWildcard(binding);

         if(log.isTraceEnabled())
         {
            log.trace("added wildcard to " + group);
         }

         type = peekType();
      }
      else
      {
         type = (TypeBinding)o;
      }
      type.setWildcard(binding);

      if(log.isTraceEnabled())
      {
         log.trace("added wildcard to type " + type.getQName().toString());
      }

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
                     ", collectionType=" + propertyMetaData.getCollectionType()
                  );
               }
            }
            type.setWildcardPropertyMetaData(propertyMetaData);
         }
      }
   }

   private static void bindElement(SchemaBinding schema,
                                   XSElementDeclaration element,
                                   SharedElements sharedElements,
                                   int minOccurs,
                                   int maxOccurs,
                                   boolean maxOccursUnbounded)
   {
      QName qName = new QName(element.getNamespace(), element.getName());

      Object o = peekTypeOrGroup();
      TypeBinding parentType = null;
      ModelGroupBinding parentGroup = null;
      if(o instanceof TypeBinding)
      {
         parentType = (TypeBinding)o;
      }
      else
      {
         parentGroup = (ModelGroupBinding)o;
      }

      boolean global = element.getScope() == XSConstants.SCOPE_GLOBAL;
      ElementBinding binding = schema.getElement(qName);
      if(global && binding != null)
      {
         if(parentType != null)
         {
            parentType.addElement(binding);
         }
         else if(parentGroup != null)
         {
            parentGroup.addElement(binding);
         }

         // todo: this is a BAD hack!
         if(maxOccursUnbounded)
         {
            binding.setMaxOccursUnbounded(maxOccursUnbounded);
         }
         return;
      }

      TypeBinding type = null;

      boolean shared = sharedElements.isShared(element);
      if(shared)
      {
         type = sharedElements.getTypeBinding(element);
      }

      if(type == null)
      {
         type = bindType(schema, element.getTypeDefinition(), sharedElements);
         if(shared)
         {
            sharedElements.setTypeBinding(element, type);
         }
      }

      binding = new ElementBinding(schema, qName, type);
      binding.setNillable(element.getNillable());
      binding.setMinOccurs(minOccurs);
      binding.setMaxOccurs(maxOccurs);
      binding.setMaxOccursUnbounded(maxOccursUnbounded);
      if(global)
      {
         schema.addElement(binding);
      }

      if(parentType != null)
      {
         parentType.addElement(binding);
      }
      else if(parentGroup != null)
      {
         if(!MODELGROUPS)
         {
            throw new JBossXBRuntimeException("NO GROUPS!");
         }
         parentGroup.addElement(binding);
         if(log.isTraceEnabled())
         {
            log.trace("Element " + binding.getQName() + " added to " + parentGroup);
         }
      }

      if(log.isTraceEnabled())
      {
         if(parentType == null)
         {
            parentType = peekType();
         }

         log.trace("element: name=" +
            qName +
            ", type=" +
            type.getQName() +
            ", multiOccurs=" +
            binding.isMultiOccurs() +
            ", nillable=" + binding.isNillable() +
            ", " + (global ? "global scope" : " owner type=" + parentType.getQName())
         );
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
                  throw new JBossXBRuntimeException(
                     "Invalid binding: both jbxb:class and jbxb:mapEntry are specified for element " +
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

   private static final class SharedElements
   {
      private Map elements = Collections.EMPTY_MAP;
      //private XSTypeDefinition anyType;

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

   public static class XsdBinderErrorHandler
      implements DOMErrorHandler
   {
      public static final XsdBinderErrorHandler INSTANCE = new XsdBinderErrorHandler();

      private XsdBinderErrorHandler()
      {
      }

      public boolean handleError(DOMError error)
      {
         // todo: i do throw exceptions here instead of returning false to stop parsing immediately
         // since returning false seems to be no different from true (a bug in the parser?)
         // Although, throwing an exception reports the same error twice but the second time with
         // location -1:-1
         switch(error.getSeverity())
         {
            case DOMError.SEVERITY_ERROR:
               throw new JBossXBRuntimeException(formatMessage(error));
            case DOMError.SEVERITY_FATAL_ERROR:
               throw new JBossXBRuntimeException(formatMessage(error));
            case DOMError.SEVERITY_WARNING:
               log.warn(formatMessage(error));
               break;
         }
         return false;
      }

      private String formatMessage(DOMError error)
      {
         StringBuffer buf = new StringBuffer();
         DOMLocator location = error.getLocation();
         if(location != null)
         {
            buf.append(location.getColumnNumber())
               .append(':')
               .append(location.getLineNumber());
         }
         else
         {
            buf.append("[location unavailable]");
         }

         buf.append(' ').append(error.getMessage());
         return buf.toString();
      }
   }

   private static final class XsdBinding
   {
      public final LinkedList typeGroupStack = new LinkedList();
      public final SchemaBinding schemaBinding = new SchemaBinding();
   }
}
