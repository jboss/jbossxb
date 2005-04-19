/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.LinkedList;
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

      // todo bind top elements

      XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
      for(int i = 0; i < types.getLength(); ++i)
      {
         XSTypeDefinition type = (XSTypeDefinition)types.item(i);
         if(!XML_SCHEMA_NS.equals(type.getNamespace()))
         {
            bindType(doc, type);
         }
      }

      return doc;
   }

   private static final TypeBinding bindType(DocumentHandler doc, XSTypeDefinition type)
   {
      TypeBinding binding;
      switch(type.getTypeCategory())
      {
         case XSTypeDefinition.SIMPLE_TYPE:
            binding = bindSimpleType(doc, (XSSimpleTypeDefinition)type);
            break;
         case XSTypeDefinition.COMPLEX_TYPE:
            binding = bindComplexType(doc, (XSComplexTypeDefinition)type);
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
            doc.addType(typeName, binding);
         }
         else
         {
            binding = new TypeBinding();
         }

         //todo add simple type binding
      }
      return binding;
   }

   private static final TypeBinding bindComplexType(DocumentHandler doc, XSComplexTypeDefinition type)
   {
      QName typeName = type.getName() == null ? null : new QName(type.getNamespace(), type.getName());
      TypeBinding binding = typeName == null ? null : doc.getType(typeName);
      if(binding == null)
      {
         if(type.getName() != null)
         {
            binding = new TypeBinding(typeName);
            doc.addType(typeName, binding);
         }
         else
         {
            binding = new TypeBinding();
         }

         XSParticle particle = type.getParticle();
         if(particle != null)
         {
            pushType(binding);
            bindParticle(doc, particle);
            popType();
         }
      }
      return binding;
   }

   private static void bindParticle(DocumentHandler doc, XSParticle particle)
   {
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            bindModelGroup(doc, (XSModelGroup)term);
            break;
         case XSConstants.WILDCARD:
            // todo bindWildcard((XSWildcard)term);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(doc, (XSElementDeclaration)term);
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }

   }

   private static void bindElement(DocumentHandler doc, XSElementDeclaration element)
   {
      QName qName = new QName(element.getNamespace(), element.getName());
      boolean global = element.getScope() == XSConstants.SCOPE_GLOBAL;

      TypeBinding type = bindType(doc, element.getTypeDefinition());
      TypeBinding parentType = peekType();
      ElementBinding binding = parentType == null ? null : parentType.getElement(qName);

      if(binding == null)
      {
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
            log.debug("added element " + qName + " of type " + type.getQName() + " to type " + parentType.getQName());
         }
      }
   }

   private static void bindModelGroup(DocumentHandler doc, XSModelGroup modelGroup)
   {
      XSObjectList particles = modelGroup.getParticles();
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         bindParticle(doc, particle);
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
}
