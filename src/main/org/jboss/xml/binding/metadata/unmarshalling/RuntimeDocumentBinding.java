/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.Util;

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
   extends DocumentBinding
{
   public RuntimeDocumentBinding()
   {
      super(null);
   }

   protected NamespaceBinding getNamespaceLocal(String nsUri)
   {
      return new NamespaceBindingImpl(nsUri);
   }

   // Inner

   private static final class NamespaceBindingImpl
      extends NamespaceBinding
   {
      private final String namespaceUri;
      private final String javaPackage;

      public NamespaceBindingImpl(String namespaceUri)
      {
         super(null);
         this.namespaceUri = namespaceUri;
         javaPackage = Util.xmlNamespaceToJavaPackage(namespaceUri);
      }

      public String getNamespaceURI()
      {
         return namespaceUri;
      }

      public String getJavaPackage()
      {
         return javaPackage;
      }

      protected TopElementBinding getTopElementLocal(String elementName)
      {
         TopElementBinding topEl;
         String javaTypeName = javaPackage + "." + Util.xmlNameToClassName(elementName, true);
         try
         {
            Class javaType = Thread.currentThread().getContextClassLoader().loadClass(javaTypeName);
            topEl = new TopElementBindingImpl(this, elementName, javaType);
         }
         catch(ClassNotFoundException e)
         {
            // default naming approach didn't work
            topEl = null;
         }
         return topEl;
      }
   }

   private static abstract class BasicElementBindingImpl
      extends AbstractBasicElementBinding
   {
      private final String elementName;
      private final Class javaType;

      public BasicElementBindingImpl(NamespaceBinding ns, String elementName, Class javaType)
      {
         super(ns, null);
         this.elementName = elementName;
         this.javaType = javaType;
      }

      protected ElementBinding getChildElementLocal(String elementName)
      {
         ElementBinding el = null;
         String clsName = Util.xmlNameToClassName(elementName, true);

         if(Collection.class.isAssignableFrom(javaType))
         {
            clsName = ns.getJavaPackage() + "." + clsName;
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
            el = new ElementBindingImpl(ns, elementName, javaType, null, null, null);
         }
         else
         {
            try
            {
               Method getter = javaType.getMethod("get" + clsName, null);
               Method setter = javaType.getMethod("set" + clsName, new Class[]{getter.getReturnType()});
               el = createElementBinding(elementName, getter, setter, null);
            }
            catch(NoSuchMethodException e)
            {
               String fieldName = Character.toLowerCase(clsName.charAt(0)) + clsName.substring(1);
               try
               {
                  Field field = javaType.getField(fieldName);
                  el = createElementBinding(elementName, null, null, field);
               }
               catch(NoSuchFieldException e1)
               {
                  // neither field nor getter/setter pair were found
               }
            }
         }

         return el;
      }

      public String getElementName()
      {
         return elementName;
      }

      public Class getJavaType()
      {
         return javaType;
      }

      private ElementBinding createElementBinding(String elementName, Method getter, Method setter, Field field)
      {
         Class javaType = getter == null ? field.getType() : getter.getReturnType();
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
         return new ElementBindingImpl(ns, elementName, javaType, getter, setter, field);
      }
   }

   private static final class ElementBindingImpl
      extends BasicElementBindingImpl
      implements ElementBinding
   {
      private final Method getter;
      private final Method setter;
      private final Field field;

      public ElementBindingImpl(NamespaceBinding ns,
                                String elementName,
                                Class javaType,
                                Method getter,
                                Method setter,
                                Field field)
      {
         super(ns, elementName, javaType);
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
      public TopElementBindingImpl(NamespaceBinding ns, String elementName, Class javaType)
      {
         super(ns, elementName, javaType);
      }
   }
}
