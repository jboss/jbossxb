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
import org.jboss.xml.binding.metadata.unmarshalling.impl.BasicElementBindingImpl;
import org.jboss.xml.binding.metadata.unmarshalling.impl.AttributeBindingImpl;
import org.jboss.xml.binding.metadata.unmarshalling.impl.AbstractElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.impl.PluggableDocumentBinding;
import org.jboss.xml.binding.metadata.unmarshalling.impl.DelegatingDocumentBinding;
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
import org.apache.xerces.dom3.bootstrap.DOMImplementationRegistry;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdBinder
{
   private static final Logger log = Logger.getLogger(XsdBinder.class);

   public static DocumentBinding bindXsd(String xsdUrl)
   {
      DocumentBindingImpl localDoc = new DocumentBindingImpl();
      DocumentBinding doc = DocumentBindingFactory.newInstance().newDocumentBinding(localDoc);

      XSModel model = loadSchema(xsdUrl);
      StringList namespaces = model.getNamespaces();
      for(int i = 0; i < namespaces.getLength(); ++i)
      {
         String ns = namespaces.item(i);
         localDoc.bindNamespace(ns);
      }

      XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      for(int i = 0; i < components.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)components.item(i);
         bindTopElement(localDoc, element);
      }

      return doc;
   }

   private static final void bindTopElement(DocumentBindingImpl doc,
                                            XSElementDeclaration element)
   {
      String ns = element.getNamespace();
      NamespaceBindingImpl nsBinding = (NamespaceBindingImpl)doc.getNamespace(ns);
      if(nsBinding == null)
      {
         throw new JBossXBRuntimeException("Namespace is not bound: " + ns);
      }
      TopElementBindingImpl top = new TopElementBindingImpl(doc.proxy, element);
      nsBinding.addTopElement(top);

      bindComplexElement(element, doc.proxy, top);
   }

   private static void bindComplexElement(XSElementDeclaration elementDecl,
                                          DocumentBinding doc,
                                          ParentElement parentBinding)
   {
      XSTypeDefinition type = elementDecl.getTypeDefinition();
      if(type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
      {
         XSComplexTypeDefinition complexType = (XSComplexTypeDefinition)type;
         XSParticle particle = complexType.getParticle();
         if(particle != null)
         {
            bindParticle(doc, parentBinding, particle);
         }

         /* todo attributes
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
         */
      }
   }

   private static void bindParticle(DocumentBinding doc,
                                    ParentElement parent,
                                    XSParticle particle)
   {
      XSTerm term = particle.getTerm();
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            bindModelGroup(doc, parent, (XSModelGroup)term);
            break;
         case XSConstants.WILDCARD:
            // todo bindWildcard((XSWildcard)term);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            bindElement(doc, parent, (XSElementDeclaration)term);
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
   }

   private static void bindElement(DocumentBinding doc,
                                   ParentElement parent,
                                   XSElementDeclaration elementDecl)
   {
      ElementBindingImpl child = new ElementBindingImpl(parent.getSelfReference(), elementDecl);
      parent.addChild(child);
      bindComplexElement(elementDecl, doc, child);
   }

   private static void bindModelGroup(DocumentBinding doc,
                                      ParentElement parent,
                                      XSModelGroup modelGroup)
   {
      XSObjectList particles = modelGroup.getParticles();
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         bindParticle(doc, parent, particle);
      }
   }

   // Private

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

   // Private

   private static final class DocumentBindingImpl
      implements DocumentBinding, PluggableDocumentBinding
   {
      private DocumentBinding proxy;
      private DocumentBinding delegate;
      private final Map namespaces = new HashMap();

      public DocumentBindingImpl()
      {
         this.delegate = this;
         this.proxy = new DocumentBinding()
         {
            public NamespaceBinding getNamespace(String namespaceUri)
            {
               return delegate.getNamespace(namespaceUri);
            }
         };
      }

      NamespaceBinding bindNamespace(String namespaceUri)
      {
         NamespaceBinding ns = new NamespaceBindingImpl(proxy, namespaceUri);
         namespaces.put(namespaceUri, ns);
         return ns;
      }

      public NamespaceBinding getNamespace(String namespaceUri)
      {
         return (NamespaceBinding)namespaces.get(namespaceUri);
      }

      public void setDocumentBinding(final DelegatingDocumentBinding delegate)
      {
         this.delegate = delegate;
      }
   }

   private static final class NamespaceBindingImpl
      implements NamespaceBinding
   {
      private final DocumentBinding doc;
      private final String namespaceUri;
      private final Map tops = new HashMap();

      public NamespaceBindingImpl(DocumentBinding doc, String namespaceUri)
      {
         this.doc = doc;
         this.namespaceUri = namespaceUri;
      }

      void addTopElement(TopElementBinding top)
      {
         tops.put(top.getElementName().getLocalPart(), top);
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getJavaPackage()
      {
         return Util.xmlNamespaceToJavaPackage(namespaceUri);
      }

      public DocumentBinding getDocument()
      {
         return doc;
      }

      public TopElementBinding getTopElement(String elementName)
      {
         return (TopElementBinding)tops.get(elementName);
      }
   }

   private static final class ElementBindingImpl
      extends AbstractElementBinding
      implements ElementBinding, ParentElement
   {
      private final XSElementDeclaration elementDecl;
      private final Map children = new HashMap();
      private Class javaType;
      private Class fieldType;
      private Method getter;
      private Method setter;
      private Field field;

      public ElementBindingImpl(BasicElementBinding parent, XSElementDeclaration elementDecl)
      {
         super(new QName(elementDecl.getNamespace(), elementDecl.getName()), parent);
         this.elementDecl = elementDecl;
      }

      private void init()
      {
         // first try to use XSD type
         XSTypeDefinition typeDef = elementDecl.getTypeDefinition();
         String typeBasedClsName = null;
         if("http://www.w3.org/2001/XMLSchema".equals(typeDef.getNamespace()))
         {
            javaType = SimpleTypeBindings.classForType(typeDef.getName());
         }
         else if(typeDef.getName() != null)
         {
            NamespaceBinding ns = doc.getNamespace(typeDef.getNamespace());
            typeBasedClsName = ns.getJavaPackage() + "." + Util.xmlNameToClassName(typeDef.getName(), true);
            try
            {
               javaType = Thread.currentThread().getContextClassLoader().loadClass(typeBasedClsName);
            }
            catch(ClassNotFoundException e)
            {
            }
         }

         String elBasedClsName = Util.xmlNameToClassName(elementDecl.getName(), true);
         if(javaType == null)
         {
            NamespaceBinding ns = doc.getNamespace(elementDecl.getNamespace());
            // using type didn't help, let's try element's name
            // note: here we use element's namespace, not type's one
            try
            {
               javaType =
                  Thread.currentThread().getContextClassLoader().loadClass(ns.getJavaPackage() + "." + elBasedClsName);
            }
            catch(ClassNotFoundException e1)
            {
               // this also didn't work
            }
         }

         Class parentType = parent.getJavaType();
         if(Collection.class.isAssignableFrom(parentType))
         {
            if(javaType == null)
            {
               javaType = String.class;
            }
         }
         else
         {
            try
            {
               getter = parentType.getMethod("get" + elBasedClsName, null);
               setter = parentType.getMethod("set" + elBasedClsName, new Class[]{getter.getReturnType()});
               fieldType = getter.getReturnType();
            }
            catch(NoSuchMethodException e)
            {
               String fieldName = Character.toLowerCase(elBasedClsName.charAt(0)) + elBasedClsName.substring(1);
               try
               {
                  field = parentType.getField(fieldName);
                  fieldType = field.getType();
               }
               catch(NoSuchFieldException e1)
               {
               }
            }

            if(fieldType != null)
            {
               if(Modifier.isFinal(fieldType.getModifiers()) ||
                  javaType == null &&
                  !Modifier.isInterface(fieldType.getModifiers()) &&
                  !Modifier.isAbstract(fieldType.getModifiers()))
               {
                  javaType = fieldType;
               }
               else if(fieldType == Collection.class || Collection.class.isAssignableFrom(fieldType))
               {
                  if(javaType == null)
                  {
                     // todo: other collection types
                     javaType = java.util.ArrayList.class;
                  }
               }
               else if(javaType != null)
               {
                  if(javaType != fieldType && !fieldType.isAssignableFrom(javaType))
                  {
                     javaType = null;
                  }
               }
            }
         }

         if(javaType == null)
         {
            throw new JBossXBRuntimeException(
               "Failed to bind element " + elementName + " to any non-abstract Java type. Field type is " + fieldType
            );
         }
      }

      public void addChild(ElementBinding child)
      {
         children.put(child.getElementName(), child);
      }

      public BasicElementBinding getSelfReference()
      {
         return parent.getElement(elementName);
      }

      public Class getJavaType()
      {
         if(javaType == null)
         {
            init();
         }
         return javaType;
      }

      public ElementBinding getElement(QName elementName)
      {
         return (ElementBinding)children.get(elementName);
      }

      public Field getField()
      {
         if(javaType == null)
         {
            init();
         }
         return field;
      }

      public Method getGetter()
      {
         if(javaType == null)
         {
            init();
         }
         return getter;
      }

      public Method getSetter()
      {
         if(javaType == null)
         {
            init();
         }
         return setter;
      }

      public Class getFieldType()
      {
         if(javaType == null)
         {
            init();
         }
         return fieldType;
      }
   }

   private static final class TopElementBindingImpl
      extends BasicElementBindingImpl
      implements ParentElement, TopElementBinding
   {
      private final XSElementDeclaration elementDecl;
      private final Map children = new HashMap();
      protected Class javaType;

      public TopElementBindingImpl(DocumentBinding doc, XSElementDeclaration elementDecl)
      {
         super(new QName(elementDecl.getNamespace(), elementDecl.getName()), doc);
         this.elementDecl = elementDecl;
      }

      protected void init()
      {
         // first try to use XSD type
         XSTypeDefinition typeDef = elementDecl.getTypeDefinition();
         String typeBasedClsName = null;
         if("http://www.w3.org/2001/XMLSchema".equals(typeDef.getNamespace()))
         {
            javaType = SimpleTypeBindings.classForType(typeDef.getName());
         }
         else if(typeDef.getName() != null)
         {
            NamespaceBinding ns = doc.getNamespace(typeDef.getNamespace());
            typeBasedClsName = ns.getJavaPackage() + "." + Util.xmlNameToClassName(typeDef.getName(), true);
            try
            {
               javaType = Thread.currentThread().getContextClassLoader().loadClass(typeBasedClsName);
            }
            catch(ClassNotFoundException e)
            {
            }
         }

         if(javaType == null)
         {
            // using type didn't help, let's try element's name
            // note: here we use element's namespace, not type's one
            NamespaceBinding ns = doc.getNamespace(elementDecl.getNamespace());
            String elBasedClsName = ns.getJavaPackage() + "." + Util.xmlNameToClassName(elementDecl.getName(), true);
            try
            {
               javaType = Thread.currentThread().getContextClassLoader().loadClass(elBasedClsName);
            }
            catch(ClassNotFoundException e1)
            {
               throw new JBossXBRuntimeException("Failed to bind element " +
                  elementName +
                  " using XSD type (" +
                  typeBasedClsName +
                  ") and element name (" +
                  elBasedClsName +
                  "): classes not found."
               );
            }
         }
      }

      public BasicElementBinding getSelfReference()
      {
         return doc.getNamespace(elementDecl.getNamespace()).getTopElement(elementDecl.getName());
      }

      public void addChild(ElementBinding child)
      {
         children.put(child.getElementName(), child);
      }

      public Class getJavaType()
      {
         if(javaType == null)
         {
            init();
         }
         return javaType;
      }

      public ElementBinding getElement(QName elementName)
      {
         return (ElementBinding)children.get(elementName);
      }

      public AttributeBinding getAttribute(QName attributeName)
      {
         String fieldName = Util.xmlNameToClassName(attributeName.getLocalPart(), true);
         fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
      }
   }

   private interface ParentElement
      extends BasicElementBinding
   {
      void addChild(ElementBinding el);

      BasicElementBinding getSelfReference();
   }
}
