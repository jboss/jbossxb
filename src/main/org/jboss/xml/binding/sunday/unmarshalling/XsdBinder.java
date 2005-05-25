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
import org.jboss.xml.binding.metadata.BindingElement;
import org.jboss.xml.binding.metadata.XsdAnnotation;
import org.jboss.xml.binding.metadata.JaxbClass;
import org.jboss.xml.binding.metadata.JaxbSchemaBindings;
import org.jboss.xml.binding.metadata.JaxbPackage;
import org.jboss.xml.binding.metadata.XsdAppInfo;
import org.jboss.xml.binding.metadata.JaxbProperty;
import org.jboss.xml.binding.metadata.JaxbJavaType;
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
      SchemaBinding doc = getXsdBinding().schemaBinding;

      // read annotations. for now just log the ones that are going to be used
      XSObjectList annotations = model.getAnnotations();
      for(int i = 0; i < annotations.getLength(); ++i)
      {
         XSAnnotation annotation = (XSAnnotation)annotations.item(i);
         XsdAnnotation an = XsdAnnotation.unmarshal(annotation.getAnnotationString());
         BindingElement appinfo = an.getAppInfo();
         if(appinfo != null)
         {
            JaxbSchemaBindings schemaBindings = appinfo.getJaxbSchemaBindings();
            if(schemaBindings != null)
            {
               JaxbPackage jaxbPackage = schemaBindings.getPackage();
               if(jaxbPackage != null)
               {
                  if(log.isTraceEnabled())
                  {
                     log.trace("customized binding: default package is " + jaxbPackage.getName());
                  }
                  doc.setJaxbPackage(jaxbPackage);
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
            bindType(doc, type, sharedElements);
         }
      }

      XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      for(int i = 0; i < elements.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)elements.item(i);
         bindElement(doc, element, sharedElements, false);
      }

      return doc;
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
      // todo default simple types
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : doc.getType(typeName);
      if(binding == null)
      {
         if(typeName != null)
         {
            binding = new TypeBinding(typeName);
            doc.addType(binding);
            if(log.isTraceEnabled())
            {
               log.trace("bound simple type: " + typeName);
            }
         }
         else
         {
            binding = new TypeBinding();
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
                  JaxbClass jaxbClass = appInfo.getJaxbClass();
                  if(jaxbClass != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("simple type " +
                           type.getName() +
                           " is bound to " +
                           jaxbClass.getImplClass()
                        );
                     }
                     binding.setJaxbClass(jaxbClass);
                  }

                  JaxbJavaType jaxbJavaType = appInfo.getJaxbJavaType();
                  if(jaxbJavaType != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("simple type " +
                           type.getName() +
                           " is bound to " +
                           jaxbJavaType.getName() +
                           " with parseMethod=" +
                           jaxbJavaType.getParseMethod() +
                           " and printMethod=" +
                           jaxbJavaType.getPrintMethod()
                        );
                     }
                     binding.setJaxbJavaType(jaxbJavaType);
                  }
               }
            }
         }

         binding.setSchemaBinding(getXsdBinding().schemaBinding);
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
         if(type.getName() != null)
         {
            binding = new TypeBinding(typeName);
            doc.addType(binding);
            if(log.isTraceEnabled())
            {
               log.trace("bound complex type: " + typeName);
            }
         }
         else
         {
            binding = new TypeBinding();
         }

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
                  JaxbClass jaxbClass = appInfo.getJaxbClass();
                  if(jaxbClass != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("customized binding: type " +
                           type.getName() +
                           " is bound to " +
                           jaxbClass.getImplClass()
                        );
                     }
                     binding.setJaxbClass(jaxbClass);
                  }

                  JaxbProperty jaxbProperty = appInfo.getJaxbProperty();
                  if(jaxbProperty != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("bound simple content to property " + jaxbProperty.getName());
                     }
                     binding.setJaxbProperty(jaxbProperty);
                  }
               }
            }
         }

         binding.setSchemaBinding(getXsdBinding().schemaBinding);

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
            JaxbProperty jaxbProperty = appInfo.getJaxbProperty();
            if(jaxbProperty != null)
            {
               binding.setJaxbProperty(jaxbProperty);
            }
         }
      }

      if(log.isTraceEnabled())
      {
         if(binding.getJaxbProperty() != null)
         {
            log.trace("customized binding: attribute " +
               new QName(attr.getNamespace(), attr.getName()) +
               " is bound to property " +
               binding.getJaxbProperty().getName() +
               " and type " + binding.getJaxbProperty().getCollectionType()
            );
         }
         else
         {
            log.trace("bound attribute: type=" +
               type.getQName() +
               ", attr=" +
               attr.getName() +
               ", attrType=" +
               attrType.getName()
            );
         }
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
            TypeBinding typeBinding = peekType();
            typeBinding.setSchemaResolver(schema);
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
            binding = new ElementBinding(type);
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
                  JaxbProperty jaxbProperty = appInfo.getJaxbProperty();
                  if(jaxbProperty != null)
                  {
                     if(log.isTraceEnabled())
                     {
                        log.trace("customized binding: element " +
                           new QName(element.getNamespace(), element.getName()) +
                           " is bound to property " +
                           jaxbProperty.getName() +
                           " and type " + jaxbProperty.getCollectionType()
                        );
                     }
                     binding.setJaxbProperty(jaxbProperty);
                  }
               }
            }
         }

         if(parentType != null)
         {
            parentType.addElement(qName, binding);
            if(log.isTraceEnabled())
            {
               log.trace("bound element: complex type=" +
                  parentType.getQName() +
                  ", element=" +
                  qName +
                  ", type=" +
                  type.getQName() +
                  ", multiOccurs=" + binding.isMultiOccurs()
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
