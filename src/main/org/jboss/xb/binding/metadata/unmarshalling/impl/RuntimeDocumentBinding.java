/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata.unmarshalling.impl;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xb.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.NamespaceBinding;
import org.jboss.xb.binding.metadata.unmarshalling.TopElementBinding;
import org.jboss.xb.binding.metadata.unmarshalling.XmlValueBinding;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RuntimeDocumentBinding
   extends DocumentBindingFactoryImpl.AbstractDocumentBinding
{
   public RuntimeDocumentBinding(DocumentBinding doc)
   {
      super(doc);
   }

   protected NamespaceBinding getNamespaceLocal(String namespaceUri)
   {
      return new RuntimeNamespaceBinding(this, namespaceUri);
   }

   // Inner

   private static class RuntimeNamespaceBinding
      extends DocumentBindingFactoryImpl.AbstractNamespaceBinding
   {
      private String pkg;

      public RuntimeNamespaceBinding(DocumentBinding doc, String namespaceUri)
      {
         super(doc, namespaceUri);
      }

      protected String getJavaPackageLocal()
      {
         if(pkg == null)
         {
            pkg = Util.xmlNamespaceToJavaPackage(namespaceUri);
         }
         return pkg;
      }

      protected TopElementBinding getTopElementLocal(String elementName)
      {
         return new RuntimeTopElementBinding(this, elementName);
      }
   }

   static class RuntimeTopElementBinding
      extends DocumentBindingFactoryImpl.AbstractTopElementBinding
   {
      private Class javaType;

      public RuntimeTopElementBinding(NamespaceBinding ns, String elementName)
      {
         super(ns, elementName);
      }

      protected Class getJavaTypeLocal()
      {
         if(javaType == null)
         {
            javaType = loadDefaultClass(this);
            if(javaType == null)
            {
               throw new IllegalStateException(
                  "Failed to bind top element " + name + ": class not found " + getDefaultClassName(this)
               );
            }
         }
         return javaType;
      }

      protected ElementBinding getElementLocal(QName elementName)
      {
         return new RuntimeElementBinding(this, elementName);
      }

      protected AttributeBinding getAttributeLocal(QName attributeName)
      {
         String fieldName = Util.xmlNameToFieldName(attributeName.getLocalPart(), true);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
      }

      protected XmlValueBinding getValueLocal()
      {
         // todo: implement getValueLocal
         throw new UnsupportedOperationException("getValueLocal is not implemented.");
      }
   }

   static class RuntimeElementBinding
      extends DocumentBindingFactoryImpl.AbstractElementBinding
   {
      private Field field;
      private Method getter;
      private Method setter;
      private Class fieldType;
      private Class javaType;

      public RuntimeElementBinding(BasicElementBinding parent, QName elementName)
      {
         super(parent, elementName);
      }

      private void init()
      {
         Class parentType = parent.getJavaType();
         javaType = loadDefaultClass(this);

         if(Collection.class.isAssignableFrom(parentType))
         {
            if(javaType == null)
            {
               javaType = String.class;
            }
         }
         else
         {
            String baseMethodName = Util.xmlNameToClassName(name.getLocalPart(), true);
            try
            {
               getter = parentType.getMethod("get" + baseMethodName, null);
               setter = parentType.getMethod("set" + baseMethodName, new Class[]{getter.getReturnType()});
               fieldType = getter.getReturnType();
            }
            catch(NoSuchMethodException e)
            {
               try
               {
                  field = parentType.getField(Util.xmlNameToFieldName(name.getLocalPart(), true));
                  fieldType = field.getType();
               }
               catch(NoSuchFieldException e1)
               {
                  throw new JBossXBRuntimeException("Failed to bind " + name + " to any field in " + parentType);
               }
            }
         }

         if(javaType == null)
         {
            javaType = fieldType;
         }
         else if(fieldType != null && fieldType != javaType && fieldType.isAssignableFrom(javaType))
         {
            javaType = fieldType;
         }

         if(javaType == null)
         {
            throw new JBossXBRuntimeException("Failed to bind " +
               name +
               " to any Java type: field=" +
               field +
               ", getter=" +
               getter +
               ", parent=" +
               parentType
            );
         }
         else if(Collection.class == javaType || Collection.class.isAssignableFrom(javaType))
         {
            // todo support other collections
            javaType = ArrayList.class;
         }
         else if(javaType.isInterface() || Modifier.isAbstract(javaType.getModifiers()))
         {
            throw new JBossXBRuntimeException("Failed to bind " +
               name +
               " to a non-abstract Java type: field=" +
               field +
               ", getter=" +
               getter +
               ", parent=" +
               parentType
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
         return new RuntimeElementBinding(this, elementName);
      }

      protected AttributeBinding getAttributeLocal(QName attributeName)
      {
         String fieldName = Util.xmlNameToFieldName(attributeName.getLocalPart(), true);
         return new AttributeBindingImpl(attributeName, null, getJavaType(), fieldName);
      }

      protected XmlValueBinding getValueLocal()
      {
         // todo: implement getValueLocal
         throw new UnsupportedOperationException("getValueLocal is not implemented.");
      }
   }

   private static Class loadDefaultClass(BasicElementBinding element)
   {
      Class javaType;
      String clsName = getDefaultClassName(element);
      try
      {
         javaType = Thread.currentThread().getContextClassLoader().loadClass(clsName);
      }
      catch(ClassNotFoundException e)
      {
         javaType = null;
      }
      return javaType;
   }

   private static String getDefaultClassName(BasicElementBinding element)
   {
      QName elementName = element.getName();
      NamespaceBinding ns = element.getDocument().getNamespace(elementName.getNamespaceURI());
      return ns.getJavaPackage() + "." + Util.xmlNameToClassName(elementName.getLocalPart(), true);
   }
}
