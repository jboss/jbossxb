/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.SimpleTypeBindings;
import org.jboss.logging.Logger;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.dom3.bootstrap.DOMImplementationRegistry;

import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdBinder
{
   private static final Logger log = Logger.getLogger(XsdBinder.class);

   public static DocumentBinding bindXsd(String xsdUrl)
   {
      DocumentBindingFactory factory = DocumentBindingFactory.newInstance();
      DocumentBinding doc = factory.newDocumentBinding(null);

      XSModel model = loadSchema(xsdUrl);
      StringList namespaces = model.getNamespaces();
      for(int i = 0; i < namespaces.getLength(); ++i)
      {
         String ns = namespaces.item(i);
         factory.bindNamespace(doc, ns, Util.xmlNamespaceToJavaPackage(ns));
      }

      XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      for(int i = 0; i < components.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)components.item(i);
         bindTopElement(factory, doc, element);
      }

      return doc;
   }

   private static final void bindTopElement(DocumentBindingFactory factory,
                                            DocumentBinding doc,
                                            XSElementDeclaration element)
   {
      String ns = element.getNamespace();
      NamespaceBinding nsBinding = doc.getNamespace(ns);
      if(nsBinding == null)
      {
         throw new JBossXBRuntimeException("Namespace is not bound: " + ns);
      }

      String baseXmlName = getBaseForClassName(element);
      String javaClassName = nsBinding.getJavaPackage() + "." + Util.xmlNameToClassName(baseXmlName, true);
      Class javaClass;
      try
      {
         javaClass = Thread.currentThread().getContextClassLoader().loadClass(javaClassName);
      }
      catch(ClassNotFoundException e)
      {
         throw new JBossXBRuntimeException(
            "Failed to bind {" + ns + ":" + element.getName() + "}: class not found " + javaClassName
         );
      }

      TopElementBinding topElement = factory.bindTopElement(nsBinding, element.getName(), javaClass);

      bindComplexElement(element, factory, doc, topElement);
   }

   private static void bindComplexElement(XSElementDeclaration elementDecl,
                                          DocumentBindingFactory factory,
                                          DocumentBinding doc,
                                          BasicElementBinding parentBinding)
   {
      XSTypeDefinition type = elementDecl.getTypeDefinition();
      if(type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
      {
         XSComplexTypeDefinition complexType = (XSComplexTypeDefinition)type;
         XSParticle particle = complexType.getParticle();
         if(particle != null)
         {
            bindParticle(factory, doc, parentBinding, particle);
         }

         XSObjectList attributeUses = complexType.getAttributeUses();
         for(int i = 0; i < attributeUses.getLength(); ++i)
         {
            XSAttributeUse attrUse = (XSAttributeUse)attributeUses.item(i);
            XSAttributeDeclaration attrDec = attrUse.getAttrDeclaration();

            String baseFieldName = Util.xmlNameToClassName(elementDecl.getName(), true);
            String fieldName = Character.toLowerCase(baseFieldName.charAt(0)) + baseFieldName.substring(1);

            NamespaceBinding ns = doc.getNamespace(elementDecl.getNamespace());
            String fqClsName = ns.getJavaPackage() +
               "." +
               Util.xmlNameToClassName(getBaseForClassName(elementDecl), true);

            Class javaType;
            try
            {
               javaType = Thread.currentThread().getContextClassLoader().loadClass(fqClsName);
            }
            catch(ClassNotFoundException e)
            {
               javaType = null;
            }

            factory.bindAttribute(parentBinding, attrDec.getNamespace(), attrDec.getName(), fieldName, javaType);
         }

      }
      else
      {
         //bindComplexElement(factory, parentBinding, elementDecl);
      }
   }

   private static void bindParticle(DocumentBindingFactory factory,
                                    DocumentBinding doc,
                                    BasicElementBinding parent,
                                    XSParticle particle)
   {
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            bindModelGroup(factory, doc, parent, (XSModelGroup)term);
            break;
         case XSConstants.WILDCARD:
            // todo bindWildcard((XSWildcard)term);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(factory, doc, parent, (XSElementDeclaration)term);
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
   }

   private static void bindElement(DocumentBindingFactory factory,
                                   DocumentBinding doc,
                                   BasicElementBinding parent,
                                   XSElementDeclaration elementDecl)
   {
      Class javaType = null;

      XSTypeDefinition typeDef = elementDecl.getTypeDefinition();
      if("http://www.w3.org/2001/XMLSchema".equals(typeDef.getNamespace()))
      {
         javaType = SimpleTypeBindings.classForType(typeDef.getName());
      }
      else
      {
         NamespaceBinding nsBinding = doc.getNamespace(typeDef.getNamespace());
         if(nsBinding == null)
         {
            throw new JBossXBRuntimeException("Namespace is not bound: " + typeDef.getName());
         }

         String fqClsName = nsBinding.getJavaPackage() +
            "." +
            Util.xmlNameToClassName(getBaseForClassName(elementDecl), true);

         try
         {
            javaType = Thread.currentThread().getContextClassLoader().loadClass(fqClsName);
         }
         catch(ClassNotFoundException e)
         {
            log.warn("Failed to load class: " + fqClsName);
         }
      }

      String fieldName = null;

      if(Collection.class.isAssignableFrom(parent.getJavaType()))
      {
         // use java.lang.String
         if(javaType == null)
         {
            javaType = String.class;
         }
      }
      else
      {
         String baseFieldName = Util.xmlNameToClassName(elementDecl.getName(), true);
         fieldName = Character.toLowerCase(baseFieldName.charAt(0)) + baseFieldName.substring(1);
         Class fieldType;
         try
         {
            Method getter = parent.getJavaType().getMethod("get" + baseFieldName, null);
            fieldType = getter.getReturnType();
         }
         catch(NoSuchMethodException e)
         {
            try
            {
               Field field = parent.getJavaType().getField(fieldName);
               fieldType = field.getType();
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException("Failed to bind {" +
                  elementDecl.getNamespace() +
                  ":" +
                  elementDecl.getName() +
                  "}: neither getter/setter pair nor field were found in " +
                  parent.getJavaType()
               );
            }
         }

         if(javaType == null)
         {
            javaType = fieldType;
         }
         else if(!Collection.class.isAssignableFrom(fieldType) &&
            javaType != fieldType &&
            !fieldType.isAssignableFrom(javaType))
         {
            log.warn("Field's type in the class (" +
               fieldType +
               ") is not assignable from the type the element is bound to in the XSD (" +
               javaType +
               "). " +
               fieldType +
               " will be used for this element: {" +
               elementDecl.getNamespace() +
               ":" +
               elementDecl.getName() +
               "}"
            );
            javaType = fieldType;
         }
      }

      if((javaType.getModifiers() & (java.lang.reflect.Modifier.INTERFACE | java.lang.reflect.Modifier.ABSTRACT)) > 0)
      {
         if(Collection.class.isAssignableFrom(javaType))
         {
            javaType = java.util.ArrayList.class;
         }
         else
         {
            throw new JBossXBRuntimeException("Failed to bind {" +
               elementDecl.getNamespace() +
               ":" +
               elementDecl.getName() +
               "}: Java typeDef is an interface of an abstract class " +
               parent.getJavaType()
            );
         }
      }

      // todo: should it be element's namespace or its type's one?
      ElementBinding el = factory.bindElement(parent,
         elementDecl.getNamespace(),
         elementDecl.getName(),
         fieldName,
         javaType
      );
      bindComplexElement(elementDecl, factory, doc, el);
   }

   private static void bindModelGroup(DocumentBindingFactory factory,
                                      DocumentBinding doc,
                                      BasicElementBinding parent,
                                      XSModelGroup modelGroup)
   {
      XSObjectList particles = modelGroup.getParticles();
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         bindParticle(factory, doc, parent, particle);
      }
   }

   // Private

   private static String getBaseForClassName(XSElementDeclaration element)
   {
      return element.getTypeDefinition().getName() == null ? element.getName() : element.getTypeDefinition().getName();
   }

   private static XSModel loadSchema(String xsdURL)
   {
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
}
