/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata.unmarshalling.impl;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xb.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.NamespaceBinding;
import org.jboss.xb.binding.metadata.unmarshalling.TopElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.XmlValueBinding;
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
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

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

   private XsdBinder()
   {
   }

   public static DocumentBinding bindXsd(String xsdUrl)
   {
      return bindXsd(xsdUrl, null);
   }

   public static DocumentBinding bindXsd(String xsdUrl, DocumentBinding delegate)
   {
      DocumentBindingImpl localDoc;
      if(delegate instanceof DocumentBindingImpl)
      {
         localDoc = (DocumentBindingImpl)delegate;
      }
      else
      {
         localDoc = new DocumentBindingImpl(delegate);
      }

      XSModel model = loadSchema(xsdUrl);
      StringList namespaces = model.getNamespaces();
      for(int i = 0; i < namespaces.getLength(); ++i)
      {
         String namespaceUri = namespaces.item(i);
         NamespaceBindingImpl ns = localDoc.bindNamespace(namespaceUri);

         XSNamedMap components = model.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, namespaceUri);
         for(int j = 0; j < components.getLength(); ++j)
         {
            XSElementDeclaration element = (XSElementDeclaration)components.item(j);
            bindTopElement(localDoc, ns, element);
         }
      }

      return localDoc;
   }

   private static final void bindTopElement(DocumentBinding doc,
                                            NamespaceBindingImpl ns,
                                            XSElementDeclaration element)
   {
      TopElementBindingImpl top = new TopElementBindingImpl(ns, element);
      ns.addTopElement(top);

      bindComplexElement(element, doc, top);
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
      ElementBindingImpl child = new ElementBindingImpl(parent, elementDecl);
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
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Try the 2.6.2 version
         String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
         Class c = loader.loadClass(name);
         System.setProperty(DOMImplementationRegistry.PROPERTY, name);
      }
      catch(ClassNotFoundException e)
      {
         // Try the 2.7.0 version
         String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
         System.setProperty(DOMImplementationRegistry.PROPERTY, name);
      }

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

   public static final class DocumentBindingImpl
      extends DocumentBindingFactoryImpl.AbstractDocumentBinding
   {
      private final Map namespaces = new HashMap();

      public DocumentBindingImpl(DocumentBinding doc)
      {
         super(doc);
      }

      NamespaceBindingImpl bindNamespace(String namespaceUri)
      {
         NamespaceBindingImpl ns = new NamespaceBindingImpl(doc, namespaceUri);
         namespaces.put(namespaceUri, ns);
         return ns;
      }

      protected NamespaceBinding getNamespaceLocal(String namespaceUri)
      {
         return (NamespaceBinding)namespaces.get(namespaceUri);
      }
   }

   private static final class NamespaceBindingImpl
      extends DocumentBindingFactoryImpl.AbstractNamespaceBinding
   {
      private final Map tops = new HashMap();

      public NamespaceBindingImpl(DocumentBinding doc, String namespaceUri)
      {
         super(doc, namespaceUri);
      }

      protected String getJavaPackageLocal()
      {
         return Util.xmlNamespaceToJavaPackage(namespaceUri);
      }

      protected TopElementBinding getTopElementLocal(String elementName)
      {
         return (TopElementBinding)tops.get(elementName);
      }

      void addTopElement(TopElementBindingImpl top)
      {
         tops.put(top.getName().getLocalPart(), top);
      }
   }

   private static final class ElementBindingImpl
      extends DocumentBindingFactoryImpl.AbstractElementBinding
      implements ParentElement
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
         super(parent, new QName(elementDecl.getNamespace(), elementDecl.getName()));
         this.elementDecl = elementDecl;
      }

      private void init()
      {
         DocumentBinding doc = parent.getDocument();

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
               fieldType = getter.getReturnType();
               try
               {
                  setter = parentType.getMethod("set" + elBasedClsName, new Class[]{getter.getReturnType()});
               }
               catch(NoSuchMethodException e)
               {
                  // todo immutable!!!
                  setter = null;
               }
            }
            catch(NoSuchMethodException e)
            {
               String fieldName = Util.xmlNameToFieldName(elementDecl.getName(), true);
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
            throw new JBossXBRuntimeException("Failed to bind element " +
               name +
               " to any non-abstract Java type. Parent is " +
               parentType +
               ", field is " +
               fieldType
               + ", base=" + elBasedClsName
            );
         }
      }

      protected Field getFieldLocal()
      {
         if(javaType == null)
         {
            init();
         }
         return field;
      }

      protected Method getGetterLocal()
      {
         if(javaType == null)
         {
            init();
         }
         return getter;
      }

      protected Method getSetterLocal()
      {
         if(javaType == null)
         {
            init();
         }
         return setter;
      }

      protected Class getFieldTypeLocal()
      {
         if(javaType == null)
         {
            init();
         }
         return fieldType;
      }

      protected Class getJavaTypeLocal()
      {
         if(javaType == null)
         {
            init();
         }
         return javaType;
      }

      protected ElementBinding getElementLocal(QName elementName)
      {
         return (ElementBinding)children.get(elementName);
      }

      protected AttributeBinding getAttributeLocal(QName attributeName)
      {
         String fieldName = Util.xmlNameToClassName(attributeName.getLocalPart(), true);
         fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
      }

      protected XmlValueBinding getValueLocal()
      {
         // todo: implement getValueLocal
         throw new UnsupportedOperationException("getValueLocal is not implemented.");
      }

      public void addChild(BasicElementBinding child)
      {
         children.put(child.getName(), child);
      }
   }

   private static class TopElementBindingImpl
      extends DocumentBindingFactoryImpl.AbstractTopElementBinding
      implements ParentElement
   {
      private final XSElementDeclaration elementDecl;
      private final Map children = new HashMap();
      protected Class javaType;

      public TopElementBindingImpl(NamespaceBinding ns, XSElementDeclaration elementDecl)
      {
         super(ns, elementDecl.getName());
         this.elementDecl = elementDecl;
      }

      private void init()
      {
         DocumentBinding doc = ns.getDocument();

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
                  name +
                  " using XSD type (" +
                  typeBasedClsName +
                  ") and element name (" +
                  elBasedClsName +
                  "): classes not found."
               );
            }
         }
      }

      protected Class getJavaTypeLocal()
      {
         if(javaType == null)
         {
            init();
         }
         return javaType;
      }

      protected ElementBinding getElementLocal(QName elementName)
      {
         return (ElementBinding)children.get(elementName);
      }

      protected AttributeBinding getAttributeLocal(QName attributeName)
      {
         String fieldName = Util.xmlNameToClassName(attributeName.getLocalPart(), true);
         fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
      }

      protected XmlValueBinding getValueLocal()
      {
         // todo: implement getValueLocal
         throw new UnsupportedOperationException("getValueLocal is not implemented.");
      }

      public void addChild(BasicElementBinding child)
      {
         children.put(child.getName(), child);
      }
   }

   interface ParentElement
      extends BasicElementBinding
   {
      void addChild(BasicElementBinding child);
   }
}
