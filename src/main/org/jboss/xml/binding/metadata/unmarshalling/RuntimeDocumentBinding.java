/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.metadata.unmarshalling.impl.AttributeBindingImpl;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * DocumentBinding implementation that binds XML namespaces, elements and attributes to Java classes and fields
 * at runtime using default XML name to Java identifier algorithms.
 * todo: cache generated bindings
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RuntimeDocumentBinding
   implements DocumentBinding
{
   public NamespaceBinding getNamespace(String namespaceUri)
   {
      return new NamespaceBindingImpl(namespaceUri);
   }

   // Inner

   private static final class NamespaceBindingImpl
      implements NamespaceBinding
   {
      private final String namespaceUri;
      private final String javaPackage;

      public NamespaceBindingImpl(String namespaceUri)
      {
         this.namespaceUri = namespaceUri;
         javaPackage = Util.xmlNamespaceToJavaPackage(namespaceUri);
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getJavaPackage()
      {
         return javaPackage;
      }

      public DocumentBinding getDocument()
      {
         // todo: implement getDocument
         throw new UnsupportedOperationException("getDocument is not implemented.");
      }

      public TopElementBinding getTopElement(String elementName)
      {
         TopElementBinding topEl;
         String javaTypeName = javaPackage + "." + Util.xmlNameToClassName(elementName, true);
         try
         {
            Class javaType = Thread.currentThread().getContextClassLoader().loadClass(javaTypeName);
            topEl = new TopElementBindingImpl(new QName(namespaceUri, elementName), javaType);
         }
         catch(ClassNotFoundException e)
         {
            // default naming approach didn't work
            topEl = null;
         }
         return topEl;
      }
   }

   private static class BasicElementBindingImpl
      implements BasicElementBinding
   {
      private final QName elementName;
      private final Class javaType;

      public BasicElementBindingImpl(QName elementName, Class javaType)
      {
         this.elementName = elementName;
         this.javaType = javaType;
      }

      public ElementBinding getElement(QName elementName)
      {
         ElementBinding el = null;
         String clsName = Util.xmlNameToClassName(elementName.getLocalPart(), true);

         if(Collection.class.isAssignableFrom(javaType))
         {
            clsName = Util.xmlNamespaceToJavaPackage(elementName.getNamespaceURI()) + "." + clsName;
            Class javaType;
            try
            {
               javaType = Thread.currentThread().getContextClassLoader().loadClass(clsName);
            }
            catch(ClassNotFoundException e)
            {
               // use java.lang.String
               javaType = String.class;
            }
            el = new ElementBindingImpl(elementName, javaType, null, null, null);
         }
         else
         {
            Method getter = null;
            Method setter = null;
            Field field = null;
            Class fieldType = null;
            try
            {
               getter = javaType.getMethod("get" + clsName, null);
               setter = javaType.getMethod("set" + clsName, new Class[]{getter.getReturnType()});
               fieldType = getter.getReturnType();
            }
            catch(NoSuchMethodException e)
            {
               String fieldName = Character.toLowerCase(clsName.charAt(0)) + clsName.substring(1);
               try
               {
                  field = javaType.getField(fieldName);
                  fieldType = field.getType();
               }
               catch(NoSuchFieldException e1)
               {
                  // neither field nor getter/setter pair were found
               }
            }

            if(fieldType != null)
            {
               Class childType = fieldType;
               if(Collection.class.isAssignableFrom(fieldType))
               {
                  clsName = Util.xmlNamespaceToJavaPackage(elementName.getNamespaceURI()) + "." + clsName;
                  try
                  {
                     childType = Thread.currentThread().getContextClassLoader().loadClass(clsName);
                  }
                  catch(ClassNotFoundException e)
                  {
                     // todo: so what is this?
                     //childType = String.class;
                  }
               }

               el = createElementBinding(elementName, getter, setter, field, childType);
            }
         }

         return el;
      }

      public AttributeBinding getAttribute(QName attributeName)
      {
         AttributeBinding attr = null;
         String clsName = Util.xmlNameToClassName(attributeName.getLocalPart(), true);

         Method getter = null;
         Field field = null;
         Class fieldType = null;
         String fieldName = Character.toLowerCase(clsName.charAt(0)) + clsName.substring(1);
         try
         {
            getter = javaType.getMethod("get" + clsName, null);
            fieldType = getter.getReturnType();
         }
         catch(NoSuchMethodException e)
         {
            try
            {
               field = javaType.getField(fieldName);
               fieldType = field.getType();
            }
            catch(NoSuchFieldException e1)
            {
               // neither field nor getter/setter pair were found
            }
         }

         if(fieldType != null)
         {
            clsName = Util.xmlNamespaceToJavaPackage(attributeName.getNamespaceURI()) + "." + clsName;
            Class attrJavaType;
            try
            {
               attrJavaType = Thread.currentThread().getContextClassLoader().loadClass(clsName);
            }
            catch(ClassNotFoundException e)
            {
               attrJavaType = fieldType;
            }

            attr = new AttributeBindingImpl(attributeName, attrJavaType, javaType, fieldName);
         }

         return attr;
      }

      public QName getElementName()
      {
         return elementName;
      }

      public Class getJavaType()
      {
         return javaType;
      }

      public DocumentBinding getDocument()
      {
         // todo: implement getDocument
         throw new UnsupportedOperationException("getDocument is not implemented.");
      }

      private ElementBinding createElementBinding(QName elementName,
                                                  Method getter,
                                                  Method setter,
                                                  Field field,
                                                  Class javaType)
      {
         if(Collection.class.isAssignableFrom(javaType))
         {
            javaType = java.util.ArrayList.class;
         }
         else if(
            (javaType.getModifiers() &
            (java.lang.reflect.Modifier.INTERFACE | java.lang.reflect.Modifier.ABSTRACT)
            ) > 0)
         {
            return null;
         }
         return new ElementBindingImpl(elementName, javaType, getter, setter, field);
      }
   }

   private static final class ElementBindingImpl
      extends BasicElementBindingImpl
      implements ElementBinding
   {
      private final Method getter;
      private final Method setter;
      private final Field field;

      public ElementBindingImpl(QName elementName,
                                Class javaType,
                                Method getter,
                                Method setter,
                                Field field)
      {
         super(elementName, javaType);
         this.getter = getter;
         this.setter = setter;
         this.field = field;
      }

      public Field getField()
      {
         return field;
      }

      public Method getGetter()
      {
         return getter;
      }

      public Method getSetter()
      {
         return setter;
      }

      public Class getFieldType()
      {
         return getter == null ? (field == null ? null : field.getType()) : getter.getReturnType();
      }
   }

   private static final class TopElementBindingImpl
      extends BasicElementBindingImpl
      implements TopElementBinding
   {
      public TopElementBindingImpl(QName elementName, Class javaType)
      {
         super(elementName, javaType);
      }
   }
}
