/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata.unmarshalling.impl;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinder;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBindingFactory;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBindingStack;
import org.jboss.xb.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.NamespaceBinding;
import org.jboss.xb.binding.metadata.unmarshalling.TopElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.XmlValueBinding;
import org.jboss.xb.binding.metadata.unmarshalling.XmlValueContainer;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DocumentBindingFactoryImpl
   extends DocumentBindingFactory
{
   public DocumentBindingStack newDocumentBindingStack()
   {
      return new DocumentBindingStackImpl();
   }

   public NamespaceBinding bindNamespace(DocumentBinding doc, String namespaceUri, String javaPackage)
   {
      DocumentBindingStackImpl docStack = (DocumentBindingStackImpl)doc;
      return docStack.bindNamespace(new DefaultNamespaceBinding(docStack, namespaceUri, javaPackage));
   }

   public TopElementBinding bindTopElement(NamespaceBinding ns, String elementName, Class javaClass)
   {
      NamespaceBindingStack nsStack = (NamespaceBindingStack)ns;
      return nsStack.bindTopElement(new DefaultTopElementBinding(nsStack, elementName, javaClass));
   }

   public ElementBinding bindElement(BasicElementBinding parent,
                                     String namespaceUri,
                                     String elementName,
                                     String fieldName,
                                     Class javaType)
   {
      BasicElementBindingStack basicStack = (BasicElementBindingStack)parent;
      return basicStack.bindChild(
         new DefaultElementBinding(parent, new QName(namespaceUri, elementName), fieldName, javaType)
      );
   }

   public XmlValueBinding bindValue(BasicElementBinding element, String fieldName, Class javaType)
   {
      ElementBindingStack basicStack = (ElementBindingStack)element;
      return basicStack.bindValue(new DefaultXmlValueBinding(element, fieldName, javaType));
   }

   public XmlValueBinding bindValue(XmlValueContainer container, String fieldName, Class javaType)
   {
      XmlValueContainerStack stack = (XmlValueContainerStack)container;
      return stack.bindValue(new DefaultXmlValueBinding(container, fieldName, javaType));
   }

   public AttributeBinding bindAttribute(BasicElementBinding parent,
                                         String namespaceUri,
                                         String attributeName,
                                         String fieldName,
                                         Class javaType)
   {
      BasicElementBindingStack stack = (BasicElementBindingStack)parent;
      AttributeBinding attr = new AttributeBindingImpl(new QName(namespaceUri, attributeName),
         javaType,
         parent.getJavaType(),
         fieldName
      );
      stack.bindAttribute(attr);
      return attr;
   }

   public DocumentBinding newDocumentBinding()
   {
      return new DocumentBindingStackImpl();
   }

   //
   // Inner
   //

   // Abstract classes for this stack implementation

   public static abstract class AbstractDocumentBinding
      implements DocumentBinding
   {
      protected final DocumentBinding doc;

      protected AbstractDocumentBinding(DocumentBinding doc)
      {
         if(doc == null)
         {
            // todo
            doc = DocumentBindingFactory.newInstance().newDocumentBindingStack();
            ((DocumentBindingStackImpl)doc).push(this);
         }
         this.doc = doc;
      }

      public NamespaceBinding getNamespace(String namespaceUri)
      {
         return doc.getNamespace(namespaceUri);
      }

      protected abstract NamespaceBinding getNamespaceLocal(String namespaceUri);
   }

   public static abstract class AbstractNamespaceBinding
      implements NamespaceBinding
   {
      protected final String namespaceUri;
      private final DocumentBinding doc;

      protected AbstractNamespaceBinding(DocumentBinding doc, String namespaceUri)
      {
         this.namespaceUri = namespaceUri;
         this.doc = doc;
      }

      public DocumentBinding getDocument()
      {
         return doc;
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getJavaPackage()
      {
         return doc.getNamespace(namespaceUri).getJavaPackage();
      }

      public TopElementBinding getTopElement(String elementName)
      {
         return doc.getNamespace(namespaceUri).getTopElement(elementName);
      }

      protected abstract String getJavaPackageLocal();

      protected abstract TopElementBinding getTopElementLocal(String elementName);
   }

   public static abstract class AbstractXmlValueContainer
      implements XmlValueContainer
   {
      protected final QName name;

      public AbstractXmlValueContainer(QName name)
      {
         this.name = name;
      }

      public QName getName()
      {
         return name;
      }

      public XmlValueBinding getValue()
      {
         return getValueStackReference().getValue();
      }

      public Class getJavaType()
      {
         return getValueStackReference().getJavaType();
      }

      protected abstract XmlValueContainer getValueStackReference();

      protected abstract XmlValueBinding getValueLocal();

      protected abstract Class getJavaTypeLocal();
   }

   public static abstract class AbstractBasicElementBinding
      extends AbstractXmlValueContainer
      implements BasicElementBinding
   {
      protected AbstractBasicElementBinding(QName elementName)
      {
         super(elementName);
      }

      public DocumentBinding getDocument()
      {
         return getStackReference().getDocument();
      }

      public ElementBinding getElement(QName elementName)
      {
         return getStackReference().getElement(elementName);
      }

      public AttributeBinding getAttribute(QName attributeName)
      {
         return getStackReference().getAttribute(attributeName);
      }

      public XmlValueBinding getValue()
      {
         return getStackReference().getValue();
      }

      protected XmlValueContainer getValueStackReference()
      {
         return getStackReference();
      }

      protected abstract Class getJavaTypeLocal();

      protected abstract ElementBinding getElementLocal(QName elementName);

      protected abstract AttributeBinding getAttributeLocal(QName attributeName);

      protected abstract XmlValueBinding getValueLocal();

      protected abstract BasicElementBinding getStackReference();
   }

   public static abstract class AbstractTopElementBinding
      extends AbstractBasicElementBinding
      implements TopElementBinding
   {
      protected final NamespaceBinding ns;

      protected AbstractTopElementBinding(NamespaceBinding ns, String elementName)
      {
         super(new QName(ns.getNamespaceUri(), elementName));
         this.ns = ns;
      }

      protected BasicElementBinding getStackReference()
      {
         return ns.getTopElement(name.getLocalPart());
      }
   }

   public static abstract class AbstractElementBinding
      extends AbstractBasicElementBinding
      implements ElementBinding
   {
      protected final BasicElementBinding parent;

      protected AbstractElementBinding(BasicElementBinding parent, QName elementName)
      {
         super(elementName);
         this.parent = parent;
      }

      public Field getField()
      {
         return ((ElementBinding)getStackReference()).getField();
      }

      public Method getGetter()
      {
         return ((ElementBinding)getStackReference()).getGetter();
      }

      public Method getSetter()
      {
         return ((ElementBinding)getStackReference()).getSetter();
      }

      public Class getFieldType()
      {
         return ((ElementBinding)getStackReference()).getFieldType();
      }

      protected BasicElementBinding getStackReference()
      {
         return parent.getElement(name);
      }

      protected abstract Field getFieldLocal();

      protected abstract Method getGetterLocal();

      protected abstract Method getSetterLocal();

      protected abstract Class getFieldTypeLocal();
   }

   public static abstract class AbstractXmlValueBinding
      extends AbstractXmlValueContainer
      implements XmlValueBinding
   {
      private final XmlValueContainer container;

      protected AbstractXmlValueBinding(XmlValueContainer container)
      {
         super(container.getName());
         this.container = container;
      }

      public Field getField()
      {
         return container.getValue().getField();
      }

      public Method getGetter()
      {
         return container.getValue().getGetter();
      }

      public Method getSetter()
      {
         return container.getValue().getSetter();
      }

      public Class getFieldType()
      {
         return container.getValue().getFieldType();
      }

      public XmlValueBinding getValue()
      {
         return container.getValue().getValue();
      }

      protected XmlValueContainer getValueStackReference()
      {
         return container.getValue();
      }

      protected abstract Field getFieldLocal();

      protected abstract Method getGetterLocal();

      protected abstract Method getSetterLocal();

      protected abstract Class getFieldTypeLocal();
   }

   // Default impl used internally

   private static class DefaultNamespaceBinding
      extends AbstractNamespaceBinding
   {
      private final String javaPackage;

      public DefaultNamespaceBinding(DocumentBinding doc, String namespaceUri, String javaPackage)
      {
         super(doc, namespaceUri);
         this.javaPackage = javaPackage;
      }

      protected String getJavaPackageLocal()
      {
         return javaPackage;
      }

      protected TopElementBinding getTopElementLocal(String elementName)
      {
         return null;
      }
   }

   private static class DefaultTopElementBinding
      extends AbstractTopElementBinding
   {
      private final Class javaType;

      DefaultTopElementBinding(NamespaceBinding ns, String elementName, Class javaType)
      {
         super(ns, elementName);
         this.javaType = javaType;
      }

      protected Class getJavaTypeLocal()
      {
         return javaType;
      }

      protected ElementBinding getElementLocal(QName elementName)
      {
         return null;
      }

      protected AttributeBinding getAttributeLocal(QName attributeName)
      {
         return null;
      }

      protected XmlValueBinding getValueLocal()
      {
         return null;
      }
   }

   private static class DefaultElementBinding
      extends AbstractElementBinding
   {
      private final Field field;
      private final Method getter;
      private final Method setter;
      private final Class fieldType;
      private final Class javaType;

      public DefaultElementBinding(BasicElementBinding parent, QName elementName, String fieldName, Class javaType)
      {
         super(parent, elementName);

         Class parentType = parent.getJavaType();
         if(Collection.class.isAssignableFrom(parentType))
         {
            field = null;
            getter = null;
            setter = null;
            fieldType = null;
         }
         else
         {
            Field tmpField = null;
            Method tmpGetter = null;
            Method tmpSetter = null;
            Class tmpFieldType;
            try
            {
               tmpField = parentType.getField(fieldName);
               tmpFieldType = tmpField.getType();
            }
            catch(NoSuchFieldException e)
            {
               String baseMethodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
               try
               {
                  tmpGetter = parentType.getMethod("get" + baseMethodName, null);
                  tmpFieldType = tmpGetter.getReturnType();
                  try
                  {
                     tmpSetter = parentType.getMethod("set" + baseMethodName, new Class[]{tmpGetter.getReturnType()});
                  }
                  catch(NoSuchMethodException nosetter)
                  {
                     // this one is immutable
                  }
               }
               catch(NoSuchMethodException e1)
               {
                  throw new JBossXBRuntimeException("Failed to bind " +
                     elementName +
                     " to field " +
                     fieldName +
                     " in " +
                     parentType +
                     ": neither field nor getter/setter were found."
                  );
               }
            }

            field = tmpField;
            getter = tmpGetter;
            setter = tmpSetter;
            fieldType = tmpFieldType;
         }

         if(fieldType == null)
         {
            this.javaType = javaType == null ? String.class : javaType;
         }
         else
         {
            if(javaType == null)
            {
               this.javaType = fieldType;
            }
            else if(Collection.class == fieldType ||
               Collection.class.isAssignableFrom(fieldType) ||
               fieldType.isAssignableFrom(javaType))
            {
               this.javaType = javaType;
            }
            else
            {
               throw new JBossXBRuntimeException("Failed to bind " +
                  elementName +
                  " to field " +
                  fieldName +
                  " in " +
                  parentType +
                  ": field type " + fieldType + " is not assignable from the specified Java type " + javaType
               );
            }

            if(this.javaType.isInterface() || Modifier.isAbstract(this.javaType.getModifiers()))
            {
               throw new JBossXBRuntimeException("Failed to bind " +
                  elementName +
                  " to field " +
                  fieldName +
                  " in " +
                  parentType +
                  ": Java type is abstract class or interface."
               );
            }
         }
      }

      protected Field getFieldLocal()
      {
         return field;
      }

      protected Method getGetterLocal()
      {
         return getter;
      }

      protected Method getSetterLocal()
      {
         return setter;
      }

      protected Class getFieldTypeLocal()
      {
         return fieldType;
      }

      protected Class getJavaTypeLocal()
      {
         return javaType;
      }

      protected ElementBinding getElementLocal(QName elementName)
      {
         return null;
      }

      protected AttributeBinding getAttributeLocal(QName attributeName)
      {
         return null;
      }

      protected XmlValueBinding getValueLocal()
      {
         return null;
      }
   }

   private static class DefaultXmlValueBinding
      extends AbstractXmlValueBinding
   {
      private final Field field;
      private final Method getter;
      private final Method setter;
      private final Class fieldType;
      private final Class javaType;

      public DefaultXmlValueBinding(XmlValueContainer container, String fieldName, Class javaType)
      {
         super(container);

         Class parentType = container.getJavaType();
         if(Collection.class.isAssignableFrom(parentType))
         {
            field = null;
            getter = null;
            setter = null;
            fieldType = null;
         }
         else
         {
            Field tmpField = null;
            Method tmpGetter = null;
            Method tmpSetter = null;
            Class tmpFieldType = null;
            try
            {
               tmpField = parentType.getField(fieldName);
               tmpFieldType = tmpField.getType();
            }
            catch(NoSuchFieldException e)
            {
               String baseMethodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
               try
               {
                  tmpGetter = parentType.getMethod("get" + baseMethodName, null);
                  tmpFieldType = tmpGetter.getReturnType();
                  try
                  {
                     tmpSetter = parentType.getMethod("set" + baseMethodName, new Class[]{tmpGetter.getReturnType()});
                  }
                  catch(NoSuchMethodException nosetter)
                  {
                     // this one is immutable
                  }
               }
               catch(NoSuchMethodException e1)
               {
                  throw new JBossXBRuntimeException("Failed to bind value of " +
                     container.getName() +
                     " to field " +
                     fieldName +
                     " in " +
                     parentType +
                     ": neither field nor getter/setter were found."
                  );
               }
            }

            field = tmpField;
            getter = tmpGetter;
            setter = tmpSetter;
            fieldType = tmpFieldType;
         }

         if(fieldType == null)
         {
            this.javaType = javaType == null ? String.class : javaType;
         }
         else
         {
            if(javaType == null)
            {
               this.javaType = fieldType;
            }
            else if(Collection.class == fieldType ||
               Collection.class.isAssignableFrom(fieldType) ||
               fieldType.isAssignableFrom(javaType))
            {
               this.javaType = javaType;
            }
            else
            {
               throw new JBossXBRuntimeException("Failed to bind value of " +
                  container.getName() +
                  " to field " +
                  fieldName +
                  " in " +
                  parentType +
                  ": field type " + fieldType + " is not assignable from the specified Java type " + javaType
               );
            }

            if(this.javaType.isInterface() || Modifier.isAbstract(this.javaType.getModifiers()))
            {
               throw new JBossXBRuntimeException("Failed to bind value of " +
                  container.getName() +
                  " to field " +
                  fieldName +
                  " in " +
                  parentType +
                  ": Java type is abstract class or interface."
               );
            }
         }
      }

      protected Field getFieldLocal()
      {
         return field;
      }

      protected Method getGetterLocal()
      {
         return getter;
      }

      protected Method getSetterLocal()
      {
         return setter;
      }

      protected Class getFieldTypeLocal()
      {
         return fieldType;
      }

      protected XmlValueBinding getValueLocal()
      {
         return null;
      }

      protected Class getJavaTypeLocal()
      {
         return javaType;
      }
   }

   // Stack impl

   class DocumentBindingStackImpl
      implements DocumentBindingStack
   {
      private final List stack = new ArrayList();
      private final Map namespaces = new HashMap();

      public DocumentBindingStackImpl()
      {
      }

      public DocumentBindingStackImpl(DocumentBinding doc)
      {
         if(doc != null)
         {
            push(doc);
         }
      }

      void push(DocumentBinding doc)
      {
         stack.add(doc);
      }

      NamespaceBindingStack bindNamespace(NamespaceBinding ns)
      {
         NamespaceBindingStack stack = (NamespaceBindingStack)getNamespace(ns.getNamespaceUri());
         if(stack == null)
         {
            stack = new NamespaceBindingStack(this, ns.getNamespaceUri());
            namespaces.put(ns.getNamespaceUri(), stack);
         }
         stack.push(ns);
         return stack;
      }

      public NamespaceBinding getNamespace(String namespaceUri)
      {
         NamespaceBindingStack nsStack = (NamespaceBindingStack)namespaces.get(namespaceUri);
         if(nsStack == null)
         {
            nsStack = new NamespaceBindingStack(this, namespaceUri);
            for(int i = 0; i < stack.size(); ++i)
            {
               AbstractDocumentBinding doc = (AbstractDocumentBinding)stack.get(i);
               NamespaceBinding local = doc.getNamespaceLocal(namespaceUri);
               if(local != null)
               {
                  nsStack.push(local);
               }
            }

            if(nsStack.delegates.size() > 0)
            {
               namespaces.put(namespaceUri, nsStack);
            }
            else
            {
               nsStack = null;
            }
         }
         return nsStack;
      }

      public DocumentBindingStack push(Class documentBindingClass)
      {
         pushNewDocumentBinding(documentBindingClass);
         return this;
      }

      public DocumentBindingStack push(Class documentBindingClass, DocumentBinder binder)
      {
         DocumentBinding newDoc = pushNewDocumentBinding(documentBindingClass);
         binder.bind(newDoc);
         return this;
      }

      // Private

      private DocumentBinding pushNewDocumentBinding(Class documentBindingClass)
      {
         if(!DocumentBinding.class.isAssignableFrom(documentBindingClass))
         {
            throw new JBossXBRuntimeException("The class must implement " + DocumentBinding.class);
         }

         DocumentBinding doc;
         try
         {
            Constructor ctor = documentBindingClass.getConstructor(new Class[]{DocumentBinding.class});
            doc = (DocumentBinding)ctor.newInstance(new Object[]{this});
            push(doc);
         }
         catch(NoSuchMethodException e)
         {
            throw new JBossXBRuntimeException(
               "The class must have a constructor with one parameter of type " + DocumentBinding.class
            );
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to create an instance of " +
               documentBindingClass +
               " using constructor which takes one parameter of type " +
               DocumentBinding.class +
               ": " +
               e.getMessage()
            );
         }
         return doc;
      }
   }

   class NamespaceBindingStack
      implements NamespaceBinding
   {
      private final DocumentBinding doc;
      private final String namespaceUri;
      private final List delegates = new ArrayList();
      private final Map tops = new HashMap();

      public NamespaceBindingStack(DocumentBinding doc, String namespaceUri)
      {
         this.doc = doc;
         this.namespaceUri = namespaceUri;
      }

      TopElementBinding bindTopElement(TopElementBinding top)
      {
         TopElementBindingStack stack = (TopElementBindingStack)getTopElement(top.getName().getLocalPart());
         if(stack == null)
         {
            stack = new TopElementBindingStack(doc, top.getName());
            tops.put(stack.getName().getLocalPart(), stack);
         }
         stack.push(top);
         return stack;
      }

      void push(NamespaceBinding ns)
      {
         delegates.add(ns);
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getJavaPackage()
      {
         return ((AbstractNamespaceBinding)delegates.get(delegates.size() - 1)).getJavaPackageLocal();
      }

      public DocumentBinding getDocument()
      {
         return doc;
      }

      public TopElementBinding getTopElement(String elementName)
      {
         TopElementBindingStack stack = (TopElementBindingStack)tops.get(elementName);
         if(stack == null)
         {
            stack = new TopElementBindingStack(doc, new QName(namespaceUri, elementName));
            for(int i = 0; i < delegates.size(); ++i)
            {
               AbstractNamespaceBinding ns = (AbstractNamespaceBinding)delegates.get(i);
               TopElementBinding local = ns.getTopElementLocal(elementName);
               if(local != null)
               {
                  stack.push(local);
               }
            }

            if(stack.delegates.size() > 0)
            {
               tops.put(elementName, stack);
            }
            else
            {
               stack = null;
            }
         }
         return stack;
      }
   }

   class XmlValueContainerStack
      implements XmlValueContainer
   {
      protected final QName name;
      final List delegates = new ArrayList();
      private XmlValueBindingStack value;

      public XmlValueContainerStack(QName name)
      {
         this.name = name;
      }

      void push(XmlValueContainer container)
      {
         delegates.add(container);
      }

      XmlValueBinding bindValue(XmlValueBinding value)
      {
         getValue();
         if(this.value == null)
         {
            this.value = new XmlValueBindingStack(this);
         }
         this.value.push(value);
         return this.value;
      }

      public QName getName()
      {
         return name;
      }

      public XmlValueBinding getValue()
      {
         if(value == null)
         {
            for(int i = 0; i < delegates.size(); ++i)
            {
               AbstractXmlValueContainer basic = (AbstractXmlValueContainer)delegates.get(i);
               XmlValueBinding localValue = basic.getValueLocal();
               if(localValue != null)
               {
                  if(value == null)
                  {
                     value = new XmlValueBindingStack(this);
                  }
                  value.push(localValue);
               }
            }
         }
         return value;
      }

      public Class getJavaType()
      {
         return ((AbstractXmlValueContainer)delegates.get(delegates.size() - 1)).getJavaTypeLocal();
      }
   }

   class BasicElementBindingStack
      extends XmlValueContainerStack
      implements BasicElementBinding
   {
      private final DocumentBinding doc;
      private final Map children = new HashMap();
      private final Map attributes = new HashMap();

      public BasicElementBindingStack(DocumentBinding doc, QName elementName)
      {
         super(elementName);
         this.doc = doc;
      }

      ElementBinding bindChild(ElementBinding element)
      {
         ElementBindingStack stack = (ElementBindingStack)getElement(element.getName());
         if(stack == null)
         {
            stack = new ElementBindingStack(doc, element.getName());
            children.put(stack.getName(), stack);
         }
         stack.push(element);
         return stack;
      }

      void bindAttribute(AttributeBinding attr)
      {
         attributes.put(attr.getAttributeName(), attr);
      }

      public DocumentBinding getDocument()
      {
         return doc;
      }

      public ElementBinding getElement(QName elementName)
      {
         ElementBindingStack stack = (ElementBindingStack)children.get(elementName);
         if(stack == null)
         {
            stack = new ElementBindingStack(doc, elementName);
            for(int i = 0; i < delegates.size(); ++i)
            {
               AbstractBasicElementBinding el = (AbstractBasicElementBinding)delegates.get(i);
               ElementBinding local = el.getElementLocal(elementName);
               if(local != null)
               {
                  stack.push(local);
               }
            }

            if(stack.delegates.size() > 0)
            {
               children.put(elementName, stack);
            }
            else
            {
               stack = null;
            }
         }
         return stack;
      }

      public AttributeBinding getAttribute(QName attributeName)
      {
         AttributeBinding attr = (AttributeBinding)attributes.get(attributeName);
         if(attr == null)
         {
            for(int i = delegates.size() - 1; i >= 0; --i)
            {
               AbstractBasicElementBinding basic = (AbstractBasicElementBinding)delegates.get(i);
               attr = basic.getAttributeLocal(attributeName);
               if(attr != null)
               {
                  break;
               }
            }
         }
         return attr;
      }
   }

   class TopElementBindingStack
      extends BasicElementBindingStack
      implements TopElementBinding
   {
      public TopElementBindingStack(DocumentBinding doc, QName elementName)
      {
         super(doc, elementName);
      }
   }

   class ElementBindingStack
      extends BasicElementBindingStack
      implements ElementBinding
   {
      public ElementBindingStack(DocumentBinding doc, QName elementName)
      {
         super(doc, elementName);
      }

      public Field getField()
      {
         return getLatestBinding().getFieldLocal();
      }

      public Method getGetter()
      {
         return getLatestBinding().getGetterLocal();
      }

      public Method getSetter()
      {
         return getLatestBinding().getSetterLocal();
      }

      public Class getFieldType()
      {
         return getLatestBinding().getFieldTypeLocal();
      }

      // Private

      protected AbstractElementBinding getLatestBinding()
      {
         return (AbstractElementBinding)delegates.get(delegates.size() - 1);
      }
   }

   class XmlValueBindingStack
      extends XmlValueContainerStack
      implements XmlValueBinding
   {
      public XmlValueBindingStack(XmlValueContainer container)
      {
         super(container.getName());
      }

      public Field getField()
      {
         return getLatestBinding().getFieldLocal();
      }

      public Method getGetter()
      {
         return getLatestBinding().getGetterLocal();
      }

      public Method getSetter()
      {
         return getLatestBinding().getSetterLocal();
      }

      public Class getFieldType()
      {
         return getLatestBinding().getFieldTypeLocal();
      }

      private AbstractXmlValueBinding getLatestBinding()
      {
         return (AbstractXmlValueBinding)delegates.get(delegates.size() - 1);
      }
   }
}
