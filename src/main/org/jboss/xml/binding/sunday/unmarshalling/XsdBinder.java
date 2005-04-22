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
import org.apache.xerces.dom3.bootstrap.DOMImplementationRegistry;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdBinder
{
   private static final Logger log = Logger.getLogger(XsdBinder.class);

   private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

   private static final ThreadLocal typeStack = new ThreadLocal()
   {
      protected Object initialValue()
      {
         return new LinkedList();
      }
   };

   private static LinkedList getTypeStack()
   {
      return (LinkedList)typeStack.get();
   }

   private XsdBinder()
   {
   }

   public static final DocumentHandler bind(String xsdUrl)
   {
      XSModel model = loadSchema(xsdUrl);
      DocumentHandler doc = new DocumentHandler();

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
                     "For now we don't support anything but elements and wildcards in global model groups"
                  );
            }

         }
      }

      XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
      for(int i = 0; i < types.getLength(); ++i)
      {
         XSTypeDefinition type = (XSTypeDefinition)types.item(i);
         if(!XML_SCHEMA_NS.equals(type.getNamespace()))
         {
            bindType(doc, type, sharedElements);
         }
      }

      XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      for(int i = 0; i < elements.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)elements.item(i);
         bindElement(doc, element, sharedElements);
      }

      return doc;
   }

   private static final TypeBinding bindType(DocumentHandler doc,
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

   private static final TypeBinding bindSimpleType(DocumentHandler doc, XSSimpleTypeDefinition type)
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
      }
      return binding;
   }

   private static final TypeBinding bindComplexType(DocumentHandler doc,
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

   private static void bindAttributes(DocumentHandler doc,
                                      TypeBinding type,
                                      XSAttributeDeclaration attr)
   {
      XSSimpleTypeDefinition attrType = attr.getTypeDefinition();
      TypeBinding typeBinding = bindSimpleType(doc, attrType);
      QName attrName = new QName(attr.getNamespace(), attr.getName());
      type.addAttribute(attrName, typeBinding, AttributeHandler.NOOP);

      if(log.isTraceEnabled())
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

   private static void bindParticle(DocumentHandler doc, XSParticle particle, SharedElements sharedElements)
   {
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            bindModelGroup(doc, (XSModelGroup)term, sharedElements);
            break;
         case XSConstants.WILDCARD:
            // todo bindWildcard((XSWildcard)term);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(doc, (XSElementDeclaration)term, sharedElements);
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
   }

   private static void bindElement(DocumentHandler doc, XSElementDeclaration element, SharedElements sharedElements)
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
            if(global)
            {
               doc.addElement(qName, binding);
            }
         }

         if(parentType != null)
         {
            parentType.addElement(qName, binding);
            if(log.isTraceEnabled())
            {
               log.trace(
                  "bound element: type=" +
                  parentType.getQName() +
                  ", element=" +
                  qName +
                  ", elementType " +
                  type.getQName()
               );
            }
         }
      }
   }

   private static void bindModelGroup(DocumentHandler doc, XSModelGroup modelGroup, SharedElements sharedElements)
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
      getTypeStack().removeLast();
   }

   private static void pushType(TypeBinding binding)
   {
      getTypeStack().addLast(binding);
   }

   private static TypeBinding peekType()
   {
      LinkedList typeStack = getTypeStack();
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
}
