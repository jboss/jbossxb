/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.metadata.unmarshalling.impl.AbstractElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.impl.BasicElementBindingImpl;
import org.jboss.xml.binding.metadata.unmarshalling.impl.PluggableDocumentBinding;
import org.jboss.xml.binding.metadata.unmarshalling.impl.DelegatingDocumentBinding;
import org.jboss.xml.binding.metadata.unmarshalling.impl.AttributeBindingImpl;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RuntimeDocumentBinding
   implements DocumentBinding, PluggableDocumentBinding
{
   private DocumentBinding doc;

   public RuntimeDocumentBinding()
   {
      this.doc = this;
   }

   public NamespaceBinding getNamespace(String namespaceUri)
   {
      return new NamespaceBindingImpl(doc, namespaceUri);
   }

   public void setDocumentBinding(DelegatingDocumentBinding delegating)
   {
      doc = delegating;
   }

   // Inner

   private static final class NamespaceBindingImpl
      implements NamespaceBinding
   {
      private final DocumentBinding doc;
      private final String namespaceUri;

      public NamespaceBindingImpl(DocumentBinding doc, String namespaceUri)
      {
         this.doc = doc;
         this.namespaceUri = namespaceUri;
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
         return new TopElementBindingImpl(new QName(namespaceUri, elementName), doc);
      }
   }

   private static final class ElementBindingImpl
      extends AbstractElementBinding
   {
      private Field field;
      private Method getter;
      private Method setter;
      private Class fieldType;
      private Class javaType;

      public ElementBindingImpl(QName elementName,
                                BasicElementBinding parent)
      {
         super(elementName, parent);
      }

      private void init()
      {
         Class parentType = parent.getJavaType();
         Class myType = null;
         Field field = null;
         Method getter = null;
         Method setter = null;
         Class fieldType = null;

         String clsName = Util.xmlNameToClassName(elementName.getLocalPart(), true);
         NamespaceBinding ns = doc.getNamespace(elementName.getNamespaceURI());
         String clsQName = ns.getJavaPackage() + "." + Util.xmlNameToClassName(elementName.getLocalPart(), true);
         try
         {
            myType = Thread.currentThread().getContextClassLoader().loadClass(clsQName);
         }
         catch(ClassNotFoundException e)
         {
         }

         if(Collection.class.isAssignableFrom(parentType))
         {
            if(myType == null)
            {
               myType = String.class;
            }
         }
         else
         {
            try
            {
               getter = parentType.getMethod("get" + clsName, null);
               setter = parentType.getMethod("set" + clsName, new Class[]{getter.getReturnType()});
               fieldType = getter.getReturnType();
            }
            catch(NoSuchMethodException e)
            {
               String fieldName = Character.toLowerCase(clsName.charAt(0)) + clsName.substring(1);
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
                  myType == null &&
                  !Modifier.isInterface(fieldType.getModifiers()) &&
                  !Modifier.isAbstract(fieldType.getModifiers()))
               {
                  myType = fieldType;
               }
               else if(fieldType == Collection.class || Collection.class.isAssignableFrom(fieldType))
               {
                  if(myType == null)
                  {
                     // todo: other collection types
                     myType = java.util.ArrayList.class;
                  }
               }
               else if(myType != null)
               {
                  if(myType != fieldType && !fieldType.isAssignableFrom(myType))
                  {
                     myType = null;
                  }
               }
            }
         }

         if(myType == null)
         {
            throw new JBossXBRuntimeException(
               "Failed to bind element " + elementName + " to any non-abstract Java type. Field type is " + fieldType
            );
         }

         this.field = field;
         this.getter = getter;
         this.setter = setter;
         this.fieldType = fieldType;
         this.javaType = myType;
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
         return new ElementBindingImpl(elementName, getSelfReference());
      }

      public AttributeBinding getAttribute(QName attributeName)
      {
         String fieldName = Util.xmlNameToClassName(attributeName.getLocalPart(), true);
         fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
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

      private ElementBinding getSelfReference()
      {
         return parent.getElement(this.elementName);
      }
   }

   private static final class TopElementBindingImpl
      extends BasicElementBindingImpl
      implements TopElementBinding
   {
      public TopElementBindingImpl(QName elementName, DocumentBinding doc)
      {
         super(elementName, doc);
      }

      protected BasicElementBinding getSelfReference()
      {
         return doc.getNamespace(elementName.getNamespaceURI()).getTopElement(this.elementName.getLocalPart());
      }

      public Class getJavaType()
      {
         String clsName = doc.getNamespace(elementName.getNamespaceURI()).getJavaPackage() +
            "." +
            Util.xmlNameToClassName(elementName.getLocalPart(), true);
         try
         {
            return Thread.currentThread().getContextClassLoader().loadClass(clsName);
         }
         catch(ClassNotFoundException e)
         {
            throw new JBossXBRuntimeException("Failed to load " + clsName);
         }
      }

      public ElementBinding getElement(QName elementName)
      {
         return new ElementBindingImpl(elementName, getSelfReference());
      }

      public AttributeBinding getAttribute(QName attributeName)
      {
         String fieldName = Util.xmlNameToClassName(attributeName.getLocalPart(), true);
         fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
      }
   }
}
