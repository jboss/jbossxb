/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.JBossXBRuntimeException;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * This class is a factory for document binding unmarshalling metadata artifacts.
 * <p/>
 * Binding metadata supports chaining, i.e. metadata can be defined as a stack of document metadata bindings.
 * <br/>Topper level bindings in the stack override lower level bindings.
 * <br/>Topper level bindings (if present) do not have to override all the bindings present in lower levels.
 * <br/>If requested binding is not found in topper level bindings, the call will be delegated to lower level bindings.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class DocumentBindingFactory
{
   /**
    * @return new instance of the factory.
    */
   public static DocumentBindingFactory newInstance()
   {
      return new DocumentBindingFactoryImpl();
   }

   /**
    * Creates a new document binding.
    *
    * @return new instance of document binding metadata
    */
   public abstract DocumentBinding newDocumentBinding();

   /**
    * Creates a new document binding.
    *
    * @param delegate document binding that will be used to delegate calls to get binding metadata subartifacts
    *                 (such as namespace and element bindings) when requested subartifacts are not found
    *                 in this document binding
    * @return new instance of document binding metadata
    */
   public abstract DocumentBinding newDocumentBinding(DocumentBinding delegate);

   /**
    * Binds namespace URI to Java package name under specific document binding instance.
    *
    * @param doc         document binding this namespace binding belongs to
    * @param nsUri       namespace URI to bind
    * @param javaPackage Java package name to bind the namespace URI to
    * @return namespace binding
    */
   public abstract NamespaceBinding bindNamespace(DocumentBinding doc,
                                                  String nsUri,
                                                  String javaPackage);

   /**
    * Binds top level XML element under specific namespace binding.
    *
    * @param ns          namespace binding the top element will belong to
    * @param elementName top level element name
    * @param javaClass   Java class to bind top level element to
    * @return top element binding
    */
   public abstract TopElementBinding bindTopElement(NamespaceBinding ns,
                                                    String elementName,
                                                    Class javaClass);

   /**
    * Binds XML element to Java field or class under specific element binding
    * (maybe top level element binding or another element binding).
    *
    * @param parent      parent element binding
    * @param elementName element name to bind
    * @param fieldName   Java field name to bind element to
    *                    (in case this element in Java represents an item in a collection, i.e. parent element is bound
    *                    to a Java collection, this parameter must be null)
    * @param javaType    Java type to bind element to (required if Java field's type this element is bound to is
    *                    an interface or an abstract class or this element represents an item in a Java collection,
    *                    otherwise, might be null and will be discovered by introspection)
    * @return element binding
    */
   public abstract ElementBinding bindElement(BasicElementBinding parent,
                                              String elementName,
                                              String fieldName,
                                              Class javaType);

   /**
    * Binds XML attribute to Java field.
    *
    * @param element       the element the attribute belongs to
    * @param attributeName attribute's name
    * @param fieldName     field in a class the attribute is bound to
    * @param javaType      Java class attribute is bound to
    * @return attribute binding
    */
   public abstract AttributeBinding bindAttribute(BasicElementBinding element,
                                                  String attributeName,
                                                  String fieldName,
                                                  Class javaType);

   // Inner

   /**
    * Default implementation of document binding factory
    */
   private static class DocumentBindingFactoryImpl
      extends DocumentBindingFactory
   {
      public DocumentBinding newDocumentBinding()
      {
         return new DocumentBindingImpl(null);
      }

      public DocumentBinding newDocumentBinding(DocumentBinding delegate)
      {
         return new DocumentBindingImpl(delegate);
      }

      public NamespaceBinding bindNamespace(DocumentBinding doc,
                                            String nsUri,
                                            String javaPackage)
      {
         return ((DocumentBindingImpl)doc).addNamespace(doc.getNamespace(nsUri), nsUri, javaPackage);
      }

      /**
       * WARN: if the binding for the element already exists in the namespace binding,
       * this implementation won't remove the previous binding
       * but will use it as a delegate for the new binding instead.
       * This is on purpose.
       */
      public TopElementBinding bindTopElement(NamespaceBinding ns,
                                              String elementName,
                                              Class javaClass)
      {
         TopElementBinding el = new TopElementBindingImpl(ns,
            elementName,
            javaClass,
            ns.getTopElement(elementName)
         );
         ((ExposedNamespaceBinding)ns).ns.addElement(el);
         return el;
      }

      /**
       * WARN: if the binding for the element already exists in the parent binding,
       * this implementation won't remove the previous binding
       * but will use it as a delegate for the new binding instead.
       * This is on purpose.
       */
      public ElementBinding bindElement(BasicElementBinding parent,
                                        String elementName,
                                        String fieldName,
                                        Class javaType)
      {
         ElementBinding child = new ElementBindingImpl(parent.getNamespace(),
            elementName,
            parent.getJavaType(),
            fieldName,
            javaType,
            parent.getChildElement(elementName)
         );
         ((BasicElementBindingImpl)parent).addChild(child);
         return child;
      }

      public AttributeBinding bindAttribute(BasicElementBinding element,
                                            String attributeName,
                                            String fieldName,
                                            Class javaType)
      {
         AttributeBinding attr = new AttributeBinding(element.getNamespace(),
            attributeName,
            javaType,
            fieldName,
            element.getJavaType()
         );
         ((BasicElementBindingImpl)element).addAttribute(attr);
         return attr;
      }
   }

   /**
    * Default implementation of document binding
    */
   private static class DocumentBindingImpl
      extends DocumentBinding
   {
      private final Map nsBindings = new HashMap();

      public DocumentBindingImpl(DocumentBinding delegate)
      {
         super(delegate);
      }

      NamespaceBinding addNamespace(NamespaceBinding delegate, String nsUri, String pkg)
      {
         ExposedNamespaceBinding topNs = (ExposedNamespaceBinding)nsBindings.get(nsUri);

         if(topNs == null)
         {
            if(delegate != null)
            {
               topNs = (ExposedNamespaceBinding)delegate;
               topNs.createNewDelegate(nsUri, pkg);
            }
            else
            {
               topNs = new ExposedNamespaceBinding(nsUri, pkg);
            }
            nsBindings.put(topNs.getNamespaceURI(), topNs);
         }
         else
         {
            if(topNs != delegate)
            {
               throw new JBossXBRuntimeException(
                  "Document binding already has an instance of ExposedNamespaceBinding!"
               );
            }
            topNs.createNewDelegate(nsUri, pkg);
         }

         return topNs;
      }

      protected NamespaceBinding getNamespaceLocal(String nsUri)
      {
         return (NamespaceBinding)nsBindings.get(nsUri);
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof DocumentBindingImpl))
         {
            return false;
         }

         final DocumentBindingImpl documentBinding = (DocumentBindingImpl)o;

         if(nsBindings != null ? !nsBindings.equals(documentBinding.nsBindings) : documentBinding.nsBindings != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return (nsBindings != null ? nsBindings.hashCode() : 0);
      }
   }

   /**
    * An implementation of NamespaceBinding which is always on the top of the stack, i.e. the one a client sees.
    * The aim is to make any element binding on any level reference the same top level namespace binding.
    */
   private static class ExposedNamespaceBinding
      extends NamespaceBinding
   {
      NamespaceBindingImpl ns;

      public ExposedNamespaceBinding(String namespaceUri, String pkg)
      {
         super(null);
         ns = createNewDelegate(namespaceUri, pkg);
      }

      NamespaceBindingImpl createNewDelegate(String nsUri, String pkg)
      {
         ns = new NamespaceBindingImpl(nsUri, pkg, ns);
         return ns;
      }

      public String getNamespaceURI()
      {
         return ns.getNamespaceURI();
      }

      public String getJavaPackage()
      {
         return ns.getJavaPackage();
      }

      public TopElementBinding getTopElement(String elementName)
      {
         return ns.getTopElement(elementName);
      }

      protected TopElementBinding getTopElementLocal(String elementName)
      {
         throw new UnsupportedOperationException("getTopElementLocal is not implemented.");
      }
   }

   /**
    * Default implementation of namespace binding
    */
   private static class NamespaceBindingImpl
      extends NamespaceBinding
   {
      private final String nsUri;
      private final String pkg;
      private final Map elements = new HashMap();

      public NamespaceBindingImpl(String nsUri, String pkg, NamespaceBinding delegate)
      {
         super(delegate);
         this.nsUri = nsUri;
         this.pkg = pkg;
      }

      void addElement(TopElementBinding element)
      {
         elements.put(element.getElementName(), element);
      }

      public String getNamespaceURI()
      {
         return nsUri;
      }

      public String getJavaPackage()
      {
         return pkg;
      }

      protected TopElementBinding getTopElementLocal(String elementName)
      {
         return (TopElementBinding)elements.get(elementName);
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof NamespaceBindingImpl))
         {
            return false;
         }

         final NamespaceBindingImpl namespaceBinding = (NamespaceBindingImpl)o;

         if(!nsUri.equals(namespaceBinding.nsUri))
         {
            return false;
         }
         if(!pkg.equals(namespaceBinding.pkg))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = nsUri.hashCode();
         result = 29 * result + pkg.hashCode();
         return result;
      }
   }

   /**
    * Default implementation of basic element binding
    */
   private static abstract class BasicElementBindingImpl
      extends AbstractBasicElementBinding
   {
      protected final String elementName;
      private final Map children = new HashMap();
      private final Map attributes = new HashMap();

      public BasicElementBindingImpl(NamespaceBinding ns, String elementName, AbstractBasicElementBinding delegate)
      {
         super(ns, delegate);
         this.elementName = elementName;
      }

      void addChild(ElementBinding child)
      {
         children.put(child.getElementName(), child);
      }

      void addAttribute(AttributeBinding attr)
      {
         attributes.put(attr.getAttributeName(), attr);
      }

      public String getElementName()
      {
         return elementName;
      }

      protected AttributeBinding getAttributeLocal(String attributeName)
      {
         return (AttributeBinding)attributes.get(attributeName);
      }

      protected ElementBinding getChildElementLocal(String elementName)
      {
         return (ElementBinding)children.get(elementName);
      }
   }

   /**
    * Default implementation of top level element binding
    */
   private static class TopElementBindingImpl
      extends BasicElementBindingImpl
      implements TopElementBinding
   {
      private final Class javaClass;

      public TopElementBindingImpl(NamespaceBinding ns,
                                   String elementName,
                                   Class javaClass,
                                   TopElementBinding delegate)
      {
         super(ns, elementName, (AbstractBasicElementBinding)delegate);
         this.javaClass = javaClass;
      }

      public Class getJavaType()
      {
         return javaClass;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof TopElementBindingImpl))
         {
            return false;
         }

         final TopElementBindingImpl topElementBinding = (TopElementBindingImpl)o;

         if(!javaClass.equals(topElementBinding.javaClass) || !elementName.equals(topElementBinding.elementName))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result = javaClass.hashCode();
         result = 29 * result + elementName.hashCode();
         return result;
      }
   }

   /**
    * Default implementation of non-top level element binding
    */
   private static class ElementBindingImpl
      extends BasicElementBindingImpl
      implements ElementBinding
   {
      private final String fieldName;
      private final Method getter;
      private final Method setter;
      private final Field field;
      private final Class javaType;

      public ElementBindingImpl(NamespaceBinding ns,
                                String elementName,
                                Class parentClass,
                                String fieldName,
                                Class javaType,
                                ElementBinding delegate)
      {
         super(ns, elementName, (AbstractBasicElementBinding)delegate);

         this.fieldName = fieldName;

         Method tmpGetter = null;
         Method tmpSetter = null;
         Field tmpField = null;

         if(fieldName != null)
         {
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try
            {
               tmpGetter = parentClass.getMethod(getterName, null);
               String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
               tmpSetter = parentClass.getMethod(setterName, new Class[]{tmpGetter.getReturnType()});
               tmpField = null;
            }
            catch(NoSuchMethodException e)
            {
               tmpGetter = null;
               tmpSetter = null;

               try
               {
                  tmpField = parentClass.getField(fieldName);
               }
               catch(NoSuchFieldException e1)
               {
                  throw new JBossXBRuntimeException(
                     "Neither getter/setter pair nor field were found for " + fieldName + " in " + parentClass
                  );
               }
            }
         }
         else
         {
            // it's expected to be a collection item
            if(!Collection.class.isAssignableFrom(parentClass))
            {
               throw new JBossXBRuntimeException("Failed to bind collection item: field name is not specified for element " +
                  elementName +
                  " and parent class (" + parentClass + ") does not implement java.util.Collection."
               );
            }
         }

         getter = tmpGetter;
         setter = tmpSetter;
         field = tmpField;

         this.javaType = javaType == null ? getFieldType() : javaType;
      }

      public Method getGetter()
      {
         return getter;
      }

      public Method getSetter()
      {
         return setter;
      }

      public Field getField()
      {
         return field;
      }

      public Class getJavaType()
      {
         return javaType;
      }

      public Class getFieldType()
      {
         return fieldName == null ? null : (getField() == null ? getGetter().getReturnType() : getField().getType());
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof ElementBindingImpl))
         {
            return false;
         }

         final ElementBindingImpl elementBinding = (ElementBindingImpl)o;

         if(!fieldName.equals(elementBinding.fieldName) || !elementName.equals(elementBinding.elementName))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result = fieldName.hashCode();
         result = 29 * result + elementName.hashCode();
         return result;
      }
   }
}
