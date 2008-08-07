/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.xb.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.joinpoint.plugins.Config;
import org.jboss.logging.Logger;
import org.jboss.reflect.plugins.introspection.ParameterizedClassInfo;
import org.jboss.reflect.spi.ArrayInfo;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.EnumInfo;
import org.jboss.reflect.spi.MethodInfo;
import org.jboss.reflect.spi.PackageInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.reflect.spi.TypeInfoFactory;
import org.jboss.xb.annotations.JBossXmlAdaptedType;
import org.jboss.xb.annotations.JBossXmlAdaptedTypes;
import org.jboss.xb.annotations.JBossXmlAttribute;
import org.jboss.xb.annotations.JBossXmlChild;
import org.jboss.xb.annotations.JBossXmlChildWildcard;
import org.jboss.xb.annotations.JBossXmlChildren;
import org.jboss.xb.annotations.JBossXmlCollection;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlGroup;
import org.jboss.xb.annotations.JBossXmlGroupText;
import org.jboss.xb.annotations.JBossXmlGroupWildcard;
import org.jboss.xb.annotations.JBossXmlMapEntry;
import org.jboss.xb.annotations.JBossXmlMapKeyAttribute;
import org.jboss.xb.annotations.JBossXmlMapKeyElement;
import org.jboss.xb.annotations.JBossXmlMapValueAttribute;
import org.jboss.xb.annotations.JBossXmlMapValueElement;
import org.jboss.xb.annotations.JBossXmlModelGroup;
import org.jboss.xb.annotations.JBossXmlNoElements;
import org.jboss.xb.annotations.JBossXmlNsPrefix;
import org.jboss.xb.annotations.JBossXmlPreserveWhitespace;
import org.jboss.xb.annotations.JBossXmlSchema;
import org.jboss.xb.annotations.JBossXmlType;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.jboss.xb.builder.runtime.AbstractPropertyHandler;
import org.jboss.xb.builder.runtime.ArraySequenceBinding;
import org.jboss.xb.builder.runtime.BeanHandler;
import org.jboss.xb.builder.runtime.BuilderParticleHandler;
import org.jboss.xb.builder.runtime.BuilderSimpleParticleHandler;
import org.jboss.xb.builder.runtime.ChildCollectionInterceptor;
import org.jboss.xb.builder.runtime.ChildCollectionWildcardHandler;
import org.jboss.xb.builder.runtime.ChildWildcardHandler;
import org.jboss.xb.builder.runtime.CollectionPropertyHandler;
import org.jboss.xb.builder.runtime.CollectionPropertyWildcardHandler;
import org.jboss.xb.builder.runtime.DOMHandler;
import org.jboss.xb.builder.runtime.DefaultMapEntry;
import org.jboss.xb.builder.runtime.EnumValueAdapter;
import org.jboss.xb.builder.runtime.MapPropertyHandler;
import org.jboss.xb.builder.runtime.PropertyHandler;
import org.jboss.xb.builder.runtime.PropertyInterceptor;
import org.jboss.xb.builder.runtime.PropertyWildcardHandler;
import org.jboss.xb.builder.runtime.ValueHandler;
import org.jboss.xb.builder.runtime.WrapperBeanAdapterFactory;
import org.jboss.xb.spi.BeanAdapterBuilder;
import org.jboss.xb.spi.BeanAdapterFactory;
import org.jboss.xb.spi.DefaultBeanAdapterBuilder;
import org.w3c.dom.Element;

/**
 * JBossXBNoSchemaBuilder.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class JBossXBNoSchemaBuilder
{
   /** The log */
   private static final Logger log = Logger.getLogger(JBossXBBuilder.class);

   /** Whether trace is enabled */
   private boolean trace = log.isTraceEnabled();

   /** The schema binding */
   private SchemaBinding schemaBinding;

   /** The root type */
   private ClassInfo root;

   /** The namespace */
   private String defaultNamespace;

   /** The attribute form */
   private XmlNsForm attributeForm = XmlNsForm.UNSET;

   /** The element form */
   private XmlNsForm elementForm = XmlNsForm.UNSET;

   /** A cache of types */
   private Map<TypeInfo, TypeBinding> typeCache = new HashMap<TypeInfo, TypeBinding>();

   /** A root elements we have processed */
   private Map<TypeInfo, ElementBinding> rootCache = new HashMap<TypeInfo, ElementBinding>();

   /** The current location */
   private Stack<Location> locations = new Stack<Location>();

   /**
    * Create a new JBossXBNoSchemaBuilder.
    * 
    * @param root the root class info
    * @throws IllegalArgumentException for a null root class info
    */
   public JBossXBNoSchemaBuilder(ClassInfo root)
   {
      if (root == null)
         throw new IllegalArgumentException("Null root");

      this.root = root;
   }

   /**
    * Build the schema
    * 
    * @return the schema
    */
   public SchemaBinding build()
   {
      initSchema();
      createRootElements();
      return schemaBinding;
   }

   /**
    * Initialise the schema
    */
   protected void initSchema()
   {
      // Initialize the schema
      schemaBinding = new SchemaBinding();
      JBossXBBuilder.initSchema(schemaBinding, root);
      if (trace)
         log.trace("Building schema for " + root.getName() + " schemaBinding=" + schemaBinding);

      // Remember the default namespace
      if (defaultNamespace == null)
      {
         defaultNamespace = (String) schemaBinding.getNamespaces().iterator().next();
      }

      JBossXmlSchema jbossXmlSchema = root.getUnderlyingAnnotation(JBossXmlSchema.class);
      if (jbossXmlSchema != null)
      {
         attributeForm = jbossXmlSchema.attributeFormDefault();
         elementForm = jbossXmlSchema.elementFormDefault();
      }

      // Look for an annotation
      PackageInfo packageInfo = root.getPackage();
      if (packageInfo != null)
      {
         jbossXmlSchema = root.getUnderlyingAnnotation(JBossXmlSchema.class);
         if (jbossXmlSchema != null)
         {
            if (attributeForm == XmlNsForm.UNSET)
               attributeForm = jbossXmlSchema.attributeFormDefault();
            if (elementForm == XmlNsForm.UNSET)
               elementForm = jbossXmlSchema.elementFormDefault();
         }

         XmlSchema xmlSchema = packageInfo.getUnderlyingAnnotation(XmlSchema.class);
         if (xmlSchema != null)
         {
            String namespace = xmlSchema.namespace();
            if (JBossXmlConstants.DEFAULT.equals(xmlSchema) == false && XMLConstants.NULL_NS_URI.equals(defaultNamespace))
            {
               defaultNamespace = namespace;
               addNamespace(defaultNamespace, true);
            }

            if (attributeForm == XmlNsForm.UNSET)
               attributeForm = xmlSchema.attributeFormDefault();
            if (elementForm == XmlNsForm.UNSET)
               elementForm = xmlSchema.elementFormDefault();
         }

         // Check for adapted types
         JBossXmlAdaptedTypes adaptedTypes = packageInfo.getUnderlyingAnnotation(JBossXmlAdaptedTypes.class);
         if (adaptedTypes != null)
         {
            for (JBossXmlAdaptedType adaptedType : adaptedTypes.value())
               generateAdaptedType(adaptedType);
         }
         JBossXmlAdaptedType adaptedType = packageInfo.getUnderlyingAnnotation(JBossXmlAdaptedType.class);
         if (adaptedType != null)
            generateAdaptedType(adaptedType);
      }
   }

   /**
    * Create the root elements
    */
   protected void createRootElements()
   {
      // Create the root element
      createRootElementBinding(root);
   }

   /**
    * Create a root element binding
    * 
    * @param typeInfo the type info
    */
   protected void createRootElementBinding(TypeInfo typeInfo)
   {
      // Already done/doing this
      if (rootCache.containsKey(typeInfo))
         return;
      // Put a skeleton marker in the cache so we know not to redo it
      rootCache.put(typeInfo, null);

      // We force the element to be a root element
      push(typeInfo);
      try
      {
         createElementBinding(typeInfo, typeInfo.getSimpleName(), true);
         pop();
      }
      catch (Exception e)
      {
         throw rethrowWithLocation(e);
      }
   }

   /**
    * Create an element binding
    * 
    * @param typeInfo the type info
    * @param name the java element name
    * @param root pass true to force a root element
    * @return the element binding
    */
   private ElementBinding createElementBinding(TypeInfo typeInfo, String name, boolean root)
   {
      // Resolve the type
      TypeBinding typeBinding = resolveTypeBinding(typeInfo);

      // Create the element
      return createElementBinding(typeInfo, typeBinding, name, root);
   }

   /**
    * Create an element binding
    * 
    * @param typeInfo the type info
    * @param typeBinding the type binding
    * @param name the java element name
    * @param root pass true to force a root element
    * @return the element binding
    */
   private ElementBinding createElementBinding(TypeInfo typeInfo, TypeBinding typeBinding, String name, boolean root)
   {
      // Determine the parameters
      String overrideNamespace = null;
      String overrideName = null;
      if (typeInfo instanceof ClassInfo)
      {
         ClassInfo classInfo = (ClassInfo) typeInfo;
         XmlRootElement xmlRootElement = classInfo.getUnderlyingAnnotation(XmlRootElement.class);
         if (xmlRootElement != null)
         {
            overrideNamespace = xmlRootElement.namespace();
            overrideName = xmlRootElement.name();
         }
      }

      // Create the binding
      XmlNsForm form = elementForm;
      if (root)
         form = XmlNsForm.QUALIFIED;
      QName qName = generateXmlName(name, form, overrideNamespace, overrideName);
      return createElementBinding(typeInfo, typeBinding, qName, root);
   }

   /**
    * Create an element binding
    * 
    * @param typeInfo the type info
    * @param typeBinding the type binding
    * @param qName the qualified name
    * @param root pass true to force a root element
    * @return the element binding
    */
   private ElementBinding createElementBinding(TypeInfo typeInfo, TypeBinding typeBinding, QName qName, boolean root)
   {
      if (trace)
         log.trace("creating element " + qName + " with type " + typeInfo.getName());

      if (typeInfo instanceof ClassInfo)
      {
         ClassInfo classInfo = (ClassInfo) typeInfo;
         XmlRootElement xmlRootElement = classInfo.getUnderlyingAnnotation(XmlRootElement.class);
         if (xmlRootElement != null)
            root = true;
      }

      ElementBinding elementBinding = new ElementBinding(schemaBinding, qName, typeBinding);
      if (trace)
         log.trace("created  element " + qName + " element=" + elementBinding + " rootElement=" + root);

      // If we are a root element bind it
      if (root)
      {
         schemaBinding.addElement(elementBinding);
         ParticleBinding particleBinding = schemaBinding.getElementParticle(qName);
         particleBinding.setMinOccurs(1);
         particleBinding.setMaxOccurs(1);
         rootCache.put(typeInfo, elementBinding);
      }

      return elementBinding;
   }

   /**
    * Process a type
    * 
    * @param typeInfo the type info
    */
   protected void process(TypeInfo typeInfo)
   {
      if (typeInfo.isPrimitive() == false && typeInfo.isEnum() && typeInfo.isAnnotation() && Object.class.getName().equals(typeInfo.getName()) == false)
      {
         ClassInfo classInfo = (ClassInfo) typeInfo;

         // Create the type
         resolveTypeBinding(typeInfo);

         // Check wether we need to add it as a root element
         if (rootCache.containsKey(typeInfo) == false)
         {
            XmlRootElement xmlRootElement = classInfo.getUnderlyingAnnotation(XmlRootElement.class);
            if (xmlRootElement != null)
               createRootElementBinding(typeInfo);
         }
      }
   }

   /**
    * Resolve a type binding
    *
    * @param typeInfo the type info
    * @return the type binding
    */
   @SuppressWarnings("unchecked")
   protected TypeBinding resolveTypeBinding(TypeInfo typeInfo)
   {
      if (trace)
         log.trace("resolving type " + typeInfo.getName());

      // Look for a cached value
      TypeBinding result = typeCache.get(typeInfo);

      // No cached value
      if (result == null)
      {
         // Generate it
         result = generateTypeBinding(typeInfo);

         // Cache it
         typeCache.put(typeInfo, result);
      }
      if (trace)
         log.trace("resolved  type " + typeInfo.getName() + " binding=" + result);

      // Return the result 
      return result;
   }

   /**
    * Generate a type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   protected TypeBinding generateTypeBinding(TypeInfo typeInfo)
   {
      try
      {
         if (typeInfo.isEnum())
            return generateEnum((EnumInfo) typeInfo);

         if (typeInfo.isAnnotation())
            return generateAnnotation((ClassInfo) typeInfo);

         if (typeInfo.isArray())
            return generateArray((ArrayInfo) typeInfo);

         if (typeInfo.isCollection())
            return generateCollection((ClassInfo) typeInfo);

         if (typeInfo.isMap())
            return generateMap((ClassInfo) typeInfo);

         TypeBinding typeBinding = isSimpleType(typeInfo);
         if (typeBinding != null)
            return typeBinding;

         return generateBean((ClassInfo) typeInfo);
      }
      finally
      {
         // Not a primitive type
         if (typeInfo.isPrimitive() == false)
         {
            ClassInfo classInfo = (ClassInfo) typeInfo;

            // Process our type args
            TypeInfo[] typeArgs = classInfo.getActualTypeArguments();
            if (typeArgs != null)
            {
               for (int i = 0; i < typeArgs.length; ++i)
                  process(typeArgs[i]);
            }

            // Process the super class
            ClassInfo superClass = classInfo.getGenericSuperclass();
            if (superClass != null)
               process(superClass);
         }
      }
   }

   /**
    * Generate an enum type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   public TypeBinding generateEnum(EnumInfo typeInfo)
   {
      // Determine the parameters
      String overrideNamespace = null;
      String overrideName = null;
      boolean root = false;
      XmlType xmlType = typeInfo.getUnderlyingAnnotation(XmlType.class);
      if (xmlType != null)
      {
         root = true;
         overrideNamespace = xmlType.namespace();
         overrideName = xmlType.name();
      }

      // Determine the enum type 
      Class<?> xmlEnumValue = String.class;
      XmlEnum xmlEnum = typeInfo.getUnderlyingAnnotation(XmlEnum.class);
      if (xmlEnum != null)
         xmlEnumValue = xmlEnum.value();
      TypeInfo enumType = typeInfo.getTypeInfoFactory().getTypeInfo(xmlEnumValue);

      // Resolve the enum type as the parent (must be simple)
      TypeBinding parent = getSimpleType(enumType);

      // Create the enum type
      QName qName = null;
      TypeBinding typeBinding = null;
      if (root)
      {
         qName = generateXmlName(typeInfo, XmlNsForm.QUALIFIED, overrideNamespace, overrideName);
         typeBinding = new TypeBinding(qName, parent);
      }
      else
      {
         typeBinding = new TypeBinding(null, parent);
      }

      typeBinding.setValueAdapter(new EnumValueAdapter(qName, typeInfo, enumType));

      if (trace)
         log.trace("Created enum=" + typeInfo.getName() + " type=" + typeBinding + " rootType=" + root);

      // Bind it as a global type
      if (root)
         schemaBinding.addType(typeBinding);
      else
         typeBinding.setSchemaBinding(schemaBinding);

      return typeBinding;
   }

   /**
    * Generate an adapted type
    * 
    * @param adaptedType the information about the adaption
    * @return the type binding
    */
   public TypeBinding generateAdaptedType(JBossXmlAdaptedType adaptedType)
   {
      if(adaptedType.type() == JBossXmlConstants.DEFAULT.class)
         throw new JBossXBRuntimeException("@JBossXmlAdaptedType used in package-info.java must specify type element.");
      
      // Determine the parameters
      String overrideNamespace = adaptedType.namespace();
      String overrideName = adaptedType.name();
      Class<?> type = adaptedType.type();
      try
      {
         TypeInfo typeInfo = JBossXBBuilder.configuration.getTypeInfo(type);

         QName qName = generateXmlName(typeInfo, XmlNsForm.QUALIFIED, overrideNamespace, overrideName);

         TypeInfo parentType = typeInfo.getTypeInfoFactory().getTypeInfo(String.class);
         TypeBinding parent = getSimpleType(parentType);
         TypeBinding typeBinding = new TypeBinding(qName, parent);

         adaptType(typeBinding, adaptedType);

         typeCache.put(typeInfo, typeBinding);
         schemaBinding.addType(typeBinding);

         return typeBinding;
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Unable to adapt type " + type.getName() + " with " + adaptedType.valueAdapter().getName(), t);
      }
   }

   /**
    * Generate an adapted type
    * 
    * @param adaptedType the information about the adaption
    * @return the type binding
    */
   public void adaptType(TypeBinding adaptedType, JBossXmlAdaptedType annotation)
   {
      // Determine the parameters
      Class<?> type = annotation.type();
      Class<? extends ValueAdapter> adapter = annotation.valueAdapter();
      try
      {
         BeanInfo adapterInfo = JBossXBBuilder.configuration.getBeanInfo(adapter);

         ValueAdapter valueAdapter = (ValueAdapter) adapterInfo.newInstance();

         adaptedType.setValueAdapter(valueAdapter);
         if (trace)
            log.trace("adapted typeBinding=" + adaptedType + " adapter=" + adapter.getName());
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Unable to adapt type " + type.getName() + " with " + adapter.getName(), t);
      }
   }

   /**
    * Generate an annotation type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   public TypeBinding generateAnnotation(ClassInfo typeInfo)
   {
      // TODO generateAnnotation
      throw new UnsupportedOperationException("generateAnnotation");
   }

   /**
    * Generate an array type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   public TypeBinding generateArray(ArrayInfo typeInfo)
   {
      return resolveTypeBinding(typeInfo.getComponentType());
   }

   /**
    * Generate a collection type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   public TypeBinding generateCollection(ClassInfo typeInfo)
   {
      if (typeInfo instanceof ParameterizedClassInfo)
      {
         ParameterizedClassInfo parameterizedClassInfo = (ParameterizedClassInfo) typeInfo;
         TypeInfo[] actualTypes = parameterizedClassInfo.getActualTypeArguments();
         TypeInfo elementType = actualTypes[0];
         return resolveTypeBinding(elementType);
      }
      else
      {
         return generateBean(typeInfo);
      }
   }

   /**
    * Generate a map type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   public TypeBinding generateMap(ClassInfo typeInfo)
   {
      // the map is bound in bindProperty method currently
      return generateBean(typeInfo);
   }

   /**
    * Check whether this is a simple type
    * 
    * @param typeInfo the type info
    * @return the type binding if it is simple
    */
   public TypeBinding isSimpleType(TypeInfo typeInfo)
   {
      QName qName = SimpleTypeBindings.typeQName(typeInfo.getType());
      if (qName == null)
         return null;
      TypeBinding result = schemaBinding.getType(qName);
      if (result == null)
         throw new IllegalStateException("SimpleType is not bound in the schema: " + qName + " for " + typeInfo.getName());
      result.setHandler(BuilderSimpleParticleHandler.SIMPLE_INSTANCE);
      return result;
   }

   /**
    * Get the simple type
    * 
    * @param typeInfo the type info
    * @return the type binding if it is simple
    * @throws IllegalStateException if the type is not bound
    */
   public TypeBinding getSimpleType(TypeInfo typeInfo)
   {
      TypeBinding result = isSimpleType(typeInfo);
      if (result == null)
         throw new IllegalStateException(typeInfo.getName() + " does not map to a simple type.");
      return result;
   }

   /**
    * Generate a bean type binding
    * 
    * @param typeInfo the type info
    * @return the type binding
    */
   public TypeBinding generateBean(ClassInfo typeInfo)
   {
      return generateBean(typeInfo, false);
   }

   /**
    * Generate a bean type binding
    * 
    * @param typeInfo the type info
    * @param root whether to force a root type
    * @return the type binding
    */
   public TypeBinding generateBean(ClassInfo typeInfo, boolean root)
   {
      return generateType(typeInfo, root);
   }

   /**
    * Generate a bean type binding
    * 
    * @param typeInfo the type info
    * @param root whether to force a root type
    * @return the type binding
    */
   public TypeBinding generateType(ClassInfo typeInfo, boolean root)
   {
      // Determine the paremeters
      String overrideNamespace = null;
      String overrideName = null;
      ClassInfo factoryClassInfo = typeInfo;
      String factoryMethod = null;
      String[] propertyOrder = {""};
      XmlAccessOrder accessOrder = XmlAccessOrder.UNDEFINED;
      Class<? extends BeanAdapterBuilder> beanAdapterBuilderClass = DefaultBeanAdapterBuilder.class;
      XmlType xmlType = typeInfo.getUnderlyingAnnotation(XmlType.class);
      if (xmlType != null)
      {
         root = true;
         overrideNamespace = xmlType.namespace();
         overrideName = xmlType.name();
         if (overrideName.length() == 0)
            root = false;

         Class<?> factoryClass = xmlType.factoryClass();
         if (factoryClass != XmlType.DEFAULT.class)
            factoryClassInfo = (ClassInfo) typeInfo.getTypeInfoFactory().getTypeInfo(factoryClass);
         factoryMethod = xmlType.factoryMethod();
         propertyOrder = xmlType.propOrder();
      }
      JBossXmlType jbossXmlType = typeInfo.getUnderlyingAnnotation(JBossXmlType.class);
      if (jbossXmlType != null)
      {
         beanAdapterBuilderClass = jbossXmlType.beanAdapterBuilder();
      }
      // Determine the property access order
      XmlAccessorOrder accessorOrder = typeInfo.getUnderlyingAnnotation(XmlAccessorOrder.class);
      if (accessorOrder == null)
      {
         PackageInfo pkg = typeInfo.getPackage();
         if (pkg != null)
            accessorOrder = pkg.getUnderlyingAnnotation(XmlAccessorOrder.class);
      }
      if (accessorOrder != null)
         accessOrder = accessorOrder.value();

      // Create the binding
      TypeBinding typeBinding = null;

      if (root)
      {
         QName qName = generateXmlName(typeInfo, XmlNsForm.QUALIFIED, overrideNamespace, overrideName);
         typeBinding = new TypeBinding(qName);
      }
      else
      {
         typeBinding = new TypeBinding();
      }

      // Push into the cache early to avoid recursion
      typeCache.put(typeInfo, typeBinding);

      JBossXmlAdaptedType adaptedType = typeInfo.getUnderlyingAnnotation(JBossXmlAdaptedType.class);
      if(adaptedType != null)
      {
         if(adaptedType.type() != JBossXmlConstants.DEFAULT.class)
            throw new JBossXBRuntimeException("@JBossXmlAdaptedType on a type must not specify type element: " + typeInfo.getName());
         adaptType(typeBinding, adaptedType);
      }

      // Determine any factory method
      MethodInfo factory = null;
      if (factoryMethod != null && factoryMethod.length() > 0)
         factory = Config.findMethodInfo(factoryClassInfo, factoryMethod, null, true, true);

      // Create the handler
      BeanInfo beanInfo = JBossXBBuilder.configuration.getBeanInfo(typeInfo);
      BeanAdapterFactory beanAdapterFactory = createAdapterFactory(beanAdapterBuilderClass, beanInfo, factory);
      BeanHandler handler = new BeanHandler(beanInfo.getName(), beanAdapterFactory);
      typeBinding.setHandler(handler);
      if (trace)
         log.trace("Created BeanHandler for type=" + beanInfo.getName() + " factory=" + factory);

      // Look through the properties
      JBossXmlNoElements jbossXmlNoElements = typeInfo.getUnderlyingAnnotation(JBossXmlNoElements.class);
      boolean noElements = jbossXmlNoElements != null;
      PropertyInfo valueProperty = null;
      PropertyInfo wildcardProperty = null;
      boolean allBinding = propertyOrder.length == 0;
      boolean determinePropertyOrder = allBinding || (propertyOrder.length == 1 && propertyOrder[0].length() == 0);
      ArrayList<String> propertyNames = new ArrayList<String>();
      Set<PropertyInfo> properties = beanInfo.getProperties();
      if (properties != null && properties.isEmpty() == false)
      {
         boolean seenXmlAnyElement = false;
         for (PropertyInfo property : properties)
         {
            push(typeInfo, property.getName());

            if (trace)
               log.trace("Checking property " + property.getName() + " for " + beanInfo.getName() + " type=" + property.getType().getName());

            // Is this the value property?
            XmlValue xmlValue = property.getUnderlyingAnnotation(XmlValue.class);
            if (xmlValue != null)
            {
               if (trace)
                  log.trace("Seen @XmlValue for type=" + beanInfo.getName() + " property=" + property.getName());
               if (valueProperty != null)
                  throw new RuntimeException("@XmlValue seen on two properties: " + property.getName() + " and " + valueProperty.getName());
               valueProperty = property;
            }

            // Is this the wildcard property?
            boolean ignoreXmlAnyElement = false;
            XmlAnyElement xmlAnyElement = property.getUnderlyingAnnotation(XmlAnyElement.class);
            if (xmlAnyElement != null)
            {
               if (trace)
                  log.trace("Seen @XmlAnyElement for type=" + beanInfo.getName() + " property=" + property.getName());
               if (wildcardProperty != null && seenXmlAnyElement)
                  throw new RuntimeException("@XmlAnyElement seen on two properties: " + property.getName() + " and " + wildcardProperty.getName());
               wildcardProperty = property;
               seenXmlAnyElement = true;
               
               // should we ignore it
               if(property.getUnderlyingAnnotation(XmlElements.class) == null &&
                  property.getUnderlyingAnnotation(XmlElementRefs.class) == null)
                  ignoreXmlAnyElement = true;
            }
            else if (!seenXmlAnyElement && wildcardProperty == null && property.getType().getName().equals(org.w3c.dom.Element.class.getName()))
            {
               if (trace)
                  log.trace("Using type=" + beanInfo.getName() + " property=" + property.getName() + " as the base wildcard");
               if (wildcardProperty != null)
                  throw new RuntimeException("@XmlAnyElement seen on two properties: " + property.getName() + " and " + wildcardProperty.getName());
               wildcardProperty = property;
            }

            // Is this an attribute
            XmlAttribute xmlAttribute = property.getUnderlyingAnnotation(XmlAttribute.class);
            if (xmlAttribute != null)
            {
               JBossXmlAttribute jbossXmlAttribute = property.getUnderlyingAnnotation(JBossXmlAttribute.class);
               // Determine the name
               QName qName = generateXmlName(property.getName(), attributeForm, xmlAttribute.namespace(), xmlAttribute.name());
               // Resolve the type
               TypeInfo attributeTypeInfo = property.getType();
               if (jbossXmlAttribute != null && jbossXmlAttribute.type() != Object.class)
                  attributeTypeInfo = attributeTypeInfo.getTypeInfoFactory().getTypeInfo(jbossXmlAttribute.type());
               TypeBinding attributeType = resolveTypeBinding(attributeTypeInfo);
               // Create the attribute handler
               AttributeHandler attributeHandler = new PropertyHandler(property, attributeTypeInfo);
               // Create the attributre and bind it to the type
               AttributeBinding attribute = new AttributeBinding(schemaBinding, qName, attributeType, attributeHandler);
               attribute.setRequired(xmlAttribute.required());
               typeBinding.addAttribute(attribute);
               JBossXmlPreserveWhitespace preserveSpace = property.getUnderlyingAnnotation(JBossXmlPreserveWhitespace.class);
               if(preserveSpace != null)
                  attribute.setNormalizeSpace(preserveSpace.preserve() ? false : true);
               if (trace)
                  log.trace("Bound attribute " + qName + " type=" + beanInfo.getName() + " property=" + property.getName() + " propertyType=" + attributeTypeInfo + ", normalizeSpace=" + attribute.isNormalizeSpace());
            }

            // Are we determining the property order?
            if (determinePropertyOrder)
            {
               // Value property
               if (xmlValue != null)
               {
                  if (trace)
                     log.trace("Ignore not element @XmlValue for type=" + beanInfo.getName() + " property=" + property.getName());
                  pop();
                  continue;
               }
               // Wildcard property
               if (ignoreXmlAnyElement)
               {
                  if (trace)
                     log.trace("Ignore not element @XmlAnyElement for type=" + beanInfo.getName() + " property=" + property.getName());
                  pop();
                  continue;
               }
               // Ignore xml attribute
               if (xmlAttribute != null)
               {
                  if (trace)
                     log.trace("Ignore not element @XmlAttribute for type=" + beanInfo.getName() + " property=" + property.getName());
                  pop();
                  continue;
               }
               // Ignore xml tranient
               XmlTransient xmlTransient = property.getUnderlyingAnnotation(XmlTransient.class);
               if (xmlTransient != null)
               {
                  if (trace)
                     log.trace("Ignore not element @XmlTransient for type=" + beanInfo.getName() + " property=" + property.getName());
                  pop();
                  continue;
               }
               // Ignore the class property
               String name = property.getName();
               if ("class".equals(name))
               {
                  pop();
                  continue;
               }

               if (noElements)
               {
                  pop();
                  continue;
               }

               if (trace)
                  log.trace("Element for type=" + beanInfo.getName() + " property=" + property.getName());
               propertyNames.add(property.getName());
            }

            pop();
         }
         // Apply any access order
         if (determinePropertyOrder)
         {
            if (accessOrder == XmlAccessOrder.ALPHABETICAL)
               Collections.sort(propertyNames);
            propertyOrder = propertyNames.toArray(new String[propertyNames.size()]);
         }
      }

      // No value property, see if we have a default one
      //if (valueProperty == null)
      //{
      //   try
      //   {
      //      valueProperty = beanInfo.getProperty("value");
      //   }
      //   catch (Exception ignored)
      //   {
            // Nope.
      //   }
      //}

      // Bind the value
      if (valueProperty != null)
      {
         CharactersHandler charactersHandler = new ValueHandler(valueProperty);
         typeBinding.setSimpleType(charactersHandler);
      }
      else if (trace)
         log.trace("No value for type=" + beanInfo.getName());

      if (trace)
         log.trace("PropertyOrder " + Arrays.asList(propertyOrder) + " for type=" + beanInfo.getName());

      // Determine the model
      // TODO simple types/content when no properties other than @XmlValue and @XmlAttribute
      typeBinding.setSimple(false);
      ModelGroupBinding model = null;
      if (allBinding)
      {
         if (trace)
            log.trace("AllBinding for type=" + beanInfo.getName());
         model = new AllBinding(schemaBinding);
      }
      else
      {
         if (trace)
            log.trace("SequenceBinding for type=" + beanInfo.getName());
         model = new SequenceBinding(schemaBinding);
      }
      model.setHandler(BuilderParticleHandler.INSTANCE);
      ParticleBinding typeParticle = new ParticleBinding(model);
      typeParticle.setMinOccurs(1);
      typeParticle.setMaxOccurs(1);
      typeBinding.setParticle(typeParticle);

      if (typeInfo.isCollection())
      {
         typeParticle.setMinOccurs(0);
         typeParticle.setMaxOccursUnbounded(true);
         TypeInfo memberBaseType = typeInfo.getComponentType();
         JBossXmlModelGroup xmlModelGroup = ((ClassInfo) memberBaseType)
               .getUnderlyingAnnotation(JBossXmlModelGroup.class);
         if (xmlModelGroup != null && xmlModelGroup.particles().length > 0)
         {
            if (trace)
               log.trace("Item base type for " + typeInfo.getName() + " is " + memberBaseType.getName()
                     + " and bound to repeatable choice");

            // it's choice by default based on the idea that the
            // type parameter is a base class for items
            ModelGroupBinding choiceGroup = null;
            QName choiceName = null;
            if(!JBossXmlConstants.DEFAULT.equals(xmlModelGroup.name()))
            {
               choiceName = new QName(defaultNamespace, xmlModelGroup.name());
               choiceGroup = schemaBinding.getGroup(choiceName);
            }
            
            if(choiceGroup == null)
            {
               choiceGroup = new ChoiceBinding(schemaBinding);
               choiceGroup.setHandler(BuilderParticleHandler.INSTANCE);
               if (choiceName != null)
               {
                  choiceGroup.setQName(choiceName);
                  schemaBinding.addGroup(choiceGroup.getQName(), choiceGroup);
               }

               ParticleBinding choiceParticle = new ParticleBinding(choiceGroup, 0, 1, true);
               model.addParticle(choiceParticle);

               for (JBossXmlModelGroup.Particle member : xmlModelGroup.particles())
               {
                  XmlElement element = member.element();
                  QName memberQName = generateXmlName(element.name(), XmlNsForm.QUALIFIED, element.namespace(), null);
                  TypeInfo memberTypeInfo = typeInfo.getTypeInfoFactory().getTypeInfo(member.type());

                  boolean isCol = false;
                  if (memberTypeInfo.isCollection())
                  {
                     memberTypeInfo = ((ClassInfo) memberTypeInfo).getComponentType();
                     isCol = true;
                  }

                  TypeBinding memberTypeBinding = resolveTypeBinding(memberTypeInfo);
                  ElementBinding memberElement = createElementBinding(memberTypeInfo, memberTypeBinding, memberQName, false);
                  memberElement.setNillable(true);
                  ParticleBinding memberParticle = new ParticleBinding(memberElement, 0, 1, isCol);
                  choiceGroup.addParticle(memberParticle);

                  typeBinding.pushInterceptor(memberQName, ChildCollectionInterceptor.SINGLETON);
               }
            }
            else
            {
               ParticleBinding choiceParticle = new ParticleBinding(choiceGroup, 0, 1, true);
               model.addParticle(choiceParticle);
            }
            
            if (trace)
               log.trace("choices for " + typeBinding.getQName() + ": " + choiceGroup.getParticles());
         }
      }

      // Determine the wildcard handler
      AbstractPropertyHandler wildcardHandler = null;
      if (wildcardProperty != null)
      {
         TypeInfo wildcardType = wildcardProperty.getType();
         if (wildcardType.isCollection())
            wildcardHandler = new CollectionPropertyWildcardHandler(wildcardProperty, wildcardType);
         else
            wildcardHandler = new PropertyWildcardHandler(wildcardProperty, wildcardType);
      }

      // Look through the properties
      for (String name : propertyOrder)
      {
         // Setup the error stack
         push(typeInfo, name);
         // Get the property
         PropertyInfo property = beanInfo.getProperty(name);
         bindProperty(property, typeBinding, model, beanAdapterFactory, propertyOrder);
         pop();
      }

      // Bind the children
      JBossXmlChild[] children = null;
      JBossXmlChildren jbossXmlChildren = typeInfo.getUnderlyingAnnotation(JBossXmlChildren.class);
      if (jbossXmlChildren != null)
         children = jbossXmlChildren.value();
      else
      {
         JBossXmlChild jbossXmlChild = typeInfo.getUnderlyingAnnotation(JBossXmlChild.class);
         if (jbossXmlChild != null)
            children = new JBossXmlChild[] { jbossXmlChild };
      }

      if (children != null && children.length > 0)
      {
         for (JBossXmlChild child : children)
         {
            QName qName = generateXmlName(child.name(), elementForm, child.namespace(), child.name());
            TypeInfo childType = JBossXBBuilder.configuration.getTypeInfo(child.type());

            TypeBinding elementTypeBinding = resolveTypeBinding(childType);
            ElementBinding elementBinding = createElementBinding(childType, elementTypeBinding, qName, false);

            // Bind it to the model
            ParticleBinding particle = new ParticleBinding(elementBinding, child.minOccurs(), child.maxOccurs(), child.unbounded());
            model.addParticle(particle);

            if(childType.isMap())
               bindMapProperty(null, (ClassInfo) childType, elementTypeBinding.getQName(), (ModelGroupBinding) elementTypeBinding.getParticle().getTerm());
            
            DefaultElementInterceptor interceptor = null;
            if (typeInfo.isCollection())
               interceptor = ChildCollectionInterceptor.SINGLETON;
            else
            {
               // Expect a type with a value property to accept the child value
               PropertyInfo property = beanInfo.getProperty("value");
               if (property == null)
                  throw new UnsupportedOperationException("Expected a value property for non-collection type with JBossXmlChildren");
               TypeInfo propertyType = property.getType();
               interceptor = new PropertyInterceptor(property, propertyType);
            }
            typeBinding.pushInterceptor(qName, interceptor);
            if (trace)
               log.trace("Added interceptor " + qName + " for type=" + childType + " interceptor=" + interceptor);
         }
      }

      // Bind the wildcard
      if (wildcardProperty != null)
      {
         if (trace)
            log.trace("Processing WildcardProperty for type=" + beanInfo.getName() + " property=" + wildcardProperty.getName());
         ModelGroupBinding localModel = model;
         TypeInfo wildcardType = wildcardProperty.getType();
         TypeInfo type = wildcardType;

         // Setup any new model and determine the wildcard type
         if (wildcardType.isArray())
         {
            localModel = createArray(localModel);
            type = ((ArrayInfo) wildcardType).getComponentType();
            if (trace)
               log.trace("Wildcard " + wildcardProperty.getName() + " is an array of type " + type.getName());
         }
         else if (wildcardType.isCollection())
         {
            localModel = createCollection(localModel);
            type = ((ClassInfo)wildcardProperty.getType()).getComponentType();
            if (trace)
               log.trace("Wildcard " + wildcardProperty.getName() + " is a collection of type " + type.getName());
         }

         XmlAnyElement xmlAnyElement = wildcardProperty.getUnderlyingAnnotation(XmlAnyElement.class);
         boolean isLax = xmlAnyElement == null ? true : xmlAnyElement.lax();
         WildcardBinding wildcard = new WildcardBinding(schemaBinding);
         if (isLax)
            wildcard.setProcessContents((short) 3); // Lax
         else
            wildcard.setProcessContents((short) 1); // Strict

         // Dom element?
         if (Element.class.getName().equals(type.getName()))
         {
            wildcard.setUnresolvedElementHandler(DOMHandler.INSTANCE);
            wildcard.setUnresolvedCharactersHandler(DOMHandler.INSTANCE);
         }

         // Bind the particle to the model
         ParticleBinding particleBinding = new ParticleBinding(wildcard);
         particleBinding.setMinOccurs(0);
         particleBinding.setMaxOccurs(1);
         localModel.addParticle(particleBinding);
         typeBinding.getWildcard().setWildcardHandler((ParticleHandler) wildcardHandler);
         beanAdapterFactory.setWildcardHandler(wildcardHandler);
      }

      JBossXmlChildWildcard childWildcard = typeInfo.getUnderlyingAnnotation(JBossXmlChildWildcard.class);
      if (childWildcard != null)
      {
         if (beanAdapterFactory.getWildcardHandler() != null)
            throw new RuntimeException("Cannot have both @JBossXmlChildWildcard and @XmlAnyElement");

         ParticleHandler childWildcardHandler = null;
         if (typeInfo.isCollection())
         {
            if (childWildcard.wrapper() != Object.class)
            {
               BeanInfo wrapperInfo = JBossXBBuilder.configuration.getBeanInfo(childWildcard.wrapper());
               childWildcardHandler = new ChildCollectionWildcardHandler(wrapperInfo, childWildcard.property());
            }
            else
               childWildcardHandler = ChildCollectionWildcardHandler.SINGLETON;
         }
         else
            throw new UnsupportedOperationException("TODO");

         WildcardBinding wildcard = new WildcardBinding(schemaBinding);
         if (childWildcard.lax())
            wildcard.setProcessContents((short) 3); // Lax
         else
            wildcard.setProcessContents((short) 1); // Strict

         ParticleBinding particleBinding = new ParticleBinding(wildcard);
         particleBinding.setMinOccurs(0);
         particleBinding.setMaxOccurs(1);
         model.addParticle(particleBinding);

         typeBinding.getWildcard().setWildcardHandler(childWildcardHandler);
      }

      if (trace)
         log.trace("Created type=" + typeInfo.getName() + " typeBinding=" + typeBinding + " rootType=" + root);

      // Register as root if required
      if (root)
         schemaBinding.addType(typeBinding);
      else
         typeBinding.setSchemaBinding(schemaBinding);

      return typeBinding;
   }

   private void bindProperty(PropertyInfo property, TypeBinding typeBinding, ModelGroupBinding model,
         BeanAdapterFactory beanAdapterFactory, String[] propertyOrder)
   {
      TypeInfo propertyType = property.getType();
      if (trace)
         log.trace("Processing type=" + property.getBeanInfo().getName() + " property=" + property.getName());

      // This is illegal
      XmlTransient xmlTransient = property.getUnderlyingAnnotation(XmlTransient.class);
      if (xmlTransient != null && propertyOrder != null)
         throw new RuntimeException("Property " + property.getName() + " in property order "
               + Arrays.asList(propertyOrder) + " is marked @XmlTransient");

      // The current model
      ModelGroupBinding localModel = model;

      // Setup any new model
      if (propertyType.isArray())
      {
         if (trace)
            log.trace("Property " + property.getName() + " is an array");
         localModel = createArray(localModel);
      }
      else if (propertyType.isCollection())
      {
         if (trace)
            log.trace("Property " + property.getName() + " is a collection");
         localModel = createCollection(localModel);

         JBossXmlCollection xmlCol = property.getUnderlyingAnnotation(JBossXmlCollection.class);
         if (xmlCol != null)
         {
            // this is the type that should be analyzed
            propertyType = propertyType.getTypeInfoFactory().getTypeInfo(xmlCol.type());
         }
      }
      // Is this property bound to a model group
      else if (!property.getType().isPrimitive())
      {
         ClassInfo propClassInfo = (ClassInfo) property.getType();

         // TODO XmlElement on this property?..
         XmlElement propXmlElement = property.getUnderlyingAnnotation(XmlElement.class);
         if (propXmlElement != null)
         {
            propClassInfo = (ClassInfo) propClassInfo.getTypeInfoFactory().getTypeInfo(propXmlElement.type());
         }

         JBossXmlModelGroup xmlModelGroup = propClassInfo.getUnderlyingAnnotation(JBossXmlModelGroup.class);
         if (xmlModelGroup != null && xmlModelGroup.particles().length == 0)
         {
            if (trace)
               log.trace("Property " + property.getName() + " is bound to " + xmlModelGroup.kind());

            ModelGroupBinding propertyGroup = null;
            QName groupName = null;
            if (!JBossXmlConstants.DEFAULT.equals(xmlModelGroup.name()))
               groupName = new QName(xmlModelGroup.name());

            if (groupName != null)
               propertyGroup = schemaBinding.getGroup(groupName);

            if (propertyGroup == null)
            {
               if (xmlModelGroup.kind().equals(JBossXmlConstants.MODEL_GROUP_SEQUENCE))
               {
                  propertyGroup = new SequenceBinding(schemaBinding);
               }
               else if (xmlModelGroup.kind().equals(JBossXmlConstants.MODEL_GROUP_CHOICE))
               {
                  propertyGroup = new ChoiceBinding(schemaBinding);
               }
               else if (xmlModelGroup.kind().equals(JBossXmlConstants.MODEL_GROUP_ALL))
               {
                  propertyGroup = new AllBinding(schemaBinding);
               }
               else
               {
                  throw new IllegalStateException("Unexpected JBossXmlModelGroup.kind=" + xmlModelGroup.kind());
               }

               if (groupName != null)
               {
                  // TODO what if it doesn't have a name? should an artificial one be created?
                  propertyGroup.setQName(groupName);
                  schemaBinding.addGroup(propertyGroup.getQName(), propertyGroup);
               }

               propertyGroup.setSkip(Boolean.FALSE);

               // handler for the model group members
               BeanInfo propBeanInfo = JBossXBBuilder.configuration.getBeanInfo(propClassInfo);
               BeanAdapterFactory propBeanAdapterFactory = createAdapterFactory(DefaultBeanAdapterBuilder.class, propBeanInfo, null);
               BeanHandler propHandler = new BeanHandler(propBeanInfo.getName(), propBeanAdapterFactory);
               propertyGroup.setHandler(propHandler);

               String[] memberOrder = xmlModelGroup.propOrder();
               if (memberOrder.length == 0 || memberOrder[0].length() == 0)
               {
                  List<String> propNames = new ArrayList<String>();
                  for (PropertyInfo prop : propBeanInfo.getProperties())
                  {
                     propNames.add(prop.getName());
                  }
                  memberOrder = propNames.toArray(new String[propNames.size()]);
               }

               if (trace)
                  log.trace("Property order for " + xmlModelGroup.kind() + " property " + property.getName() + ": "
                        + Arrays.asList(memberOrder));

               // bind model group members
               for (String memberPropName : memberOrder)
               {
                  if ("class".equals(memberPropName))
                  {
                     continue;
                  }

                  PropertyInfo memberProp = propBeanInfo.getProperty(memberPropName);
                  TypeInfo memberTypeInfo = memberProp.getType();
                  String memberNamespace = null;

                  JBossXmlNsPrefix nsPrefix = memberProp.getUnderlyingAnnotation(JBossXmlNsPrefix.class);
                  if (nsPrefix != null)
                  {
                     memberNamespace = schemaBinding.getNamespace(nsPrefix.prefix());
                     if (memberNamespace == null && nsPrefix.schemaTargetIfNotMapped())
                     {
                        throw new IllegalStateException("Prefix '" + nsPrefix.prefix()
                              + "' is not mapped to any namespace!");
                     }
                  }

                  String memberName = null;
                  XmlElement memberXmlElement = memberProp.getUnderlyingAnnotation(XmlElement.class);
                  if (memberXmlElement != null)
                  {
                     if (!XmlElement.DEFAULT.class.equals(memberXmlElement.type()))
                     {
                        memberTypeInfo = memberTypeInfo.getTypeInfoFactory().getTypeInfo(memberXmlElement.type());
                     }

                     if (memberNamespace == null)
                        memberNamespace = memberXmlElement.namespace();
                     memberName = memberXmlElement.name();
                  }

                  if (memberNamespace == null)
                  {
                     memberNamespace = defaultNamespace;
                  }

                  boolean isCol = false;
                  AbstractPropertyHandler memberPropertyHandler = null;
                  if (memberProp.getType().isCollection())
                  {
                     memberPropertyHandler = new CollectionPropertyHandler(memberProp, memberProp.getType());
                     isCol = true;
                     // if memberXmlElement is present then the collection item type is set explicitly
                     if (memberXmlElement == null || XmlElement.DEFAULT.class.equals(memberXmlElement.type()))
                     {
                        JBossXmlCollection jbossXmlCollection = memberProp
                              .getUnderlyingAnnotation(JBossXmlCollection.class);
                        if (jbossXmlCollection != null)
                        {
                           memberTypeInfo = memberTypeInfo.getTypeInfoFactory().getTypeInfo(jbossXmlCollection.type());
                        }
                        memberTypeInfo = ((ClassInfo) memberTypeInfo).getComponentType();
                     }
                  }
                  // if it is bound with XmlElement.type to a collection
                  else if (memberTypeInfo.isCollection())
                  {
                     memberPropertyHandler = new CollectionPropertyHandler(memberProp, memberTypeInfo);
                     isCol = true;
                     memberTypeInfo = ((ClassInfo) memberTypeInfo).getComponentType();
                  }
                  else
                  {
                     memberPropertyHandler = new PropertyHandler(memberProp, memberTypeInfo);
                  }

                  QName memberQName = generateXmlName(memberProp.getName(), elementForm, memberNamespace, memberName);
                  propBeanAdapterFactory.addProperty(memberQName, memberPropertyHandler);

                  XBValueAdapter valueAdapter = null;
                  XmlJavaTypeAdapter xmlTypeAdapter = memberProp.getUnderlyingAnnotation(XmlJavaTypeAdapter.class);
                  if (xmlTypeAdapter != null)
                  {
                     valueAdapter = new XBValueAdapter(xmlTypeAdapter.value(), memberTypeInfo.getTypeInfoFactory());
                     memberTypeInfo = valueAdapter.getAdaptedTypeInfo();
                  }

                  TypeBinding memberTypeBinding = resolveTypeBinding(memberTypeInfo);
                  ElementBinding memberElement = createElementBinding(memberTypeInfo, memberTypeBinding, memberQName,
                        false);
                  memberElement.setNillable(true);
                  memberElement.setValueAdapter(valueAdapter);
                  ParticleBinding memberParticle = new ParticleBinding(memberElement, 0, 1, isCol);
                  propertyGroup.addParticle(memberParticle);

                  if (trace)
                     log.trace("added " + memberParticle + " to " + xmlModelGroup.kind() + ", property "
                           + property.getName());
               }
            }

            model.addParticle(new ParticleBinding(propertyGroup));

            // model group value handler based on the model group name
            // TODO what if it doesn't have a name?
            AbstractPropertyHandler propertyHandler = null;
            if (propertyType.isCollection())
               propertyHandler = new CollectionPropertyHandler(property, propClassInfo);
            else
            {
               propertyHandler = new PropertyHandler(property, propClassInfo);
            }
            beanAdapterFactory.addProperty(propertyGroup.getQName(), propertyHandler);
            return;
         }
      }

      // So this is element(s)
      XmlElement[] elements = null;
      XmlElement xmlElement = property.getUnderlyingAnnotation(XmlElement.class);
      if (xmlElement != null)
      {
         // A single element annotated
         elements = new XmlElement[]
         {xmlElement};
      }
      else
      {
         // Mutlple elements
         XmlElements xmlElements = property.getUnderlyingAnnotation(XmlElements.class);
         if (xmlElements != null)
            elements = xmlElements.value();
      }

      // A single element not annotated
      if (elements == null || elements.length == 0)
         elements = new XmlElement[1];

      // for now support just one JBossXmlNsPrefix
      JBossXmlNsPrefix xmlNsPrefix = property.getUnderlyingAnnotation(JBossXmlNsPrefix.class);

      // support for @XmlElementWrapper
      // the wrapping element is ignored in this case
      XmlElementWrapper xmlWrapper = property.getUnderlyingAnnotation(XmlElementWrapper.class);
      if (xmlWrapper != null)
      {         
         String wrapperNamespace = xmlWrapper.namespace();
         String wrapperName = xmlWrapper.name();
         QName wrapperQName = generateXmlName(property.getName(), elementForm, wrapperNamespace, wrapperName);
         localModel = bindXmlElementWrapper(propertyType, localModel, xmlWrapper.nillable(), wrapperQName);
         beanAdapterFactory.addProperty(wrapperQName, new PropertyHandler(property, propertyType));
         if (trace)
            log.trace("Added property " + wrapperQName + " for type=" + property.getBeanInfo().getName() + " property="
                  + property.getName() + " as a wrapper element");
      }

      // Setup a choice
      if (elements.length > 1)
      {
         ChoiceBinding choice = new ChoiceBinding(schemaBinding);
         choice.setHandler(BuilderParticleHandler.INSTANCE);
         ParticleBinding particleBinding = new ParticleBinding(choice);
         particleBinding.setMinOccurs(0);
         particleBinding.setMaxOccurs(1);
         localModel.addParticle(particleBinding);
         localModel = choice;
         if (trace)
            log.trace("XmlElements seen adding choice for type=" + property.getBeanInfo().getName() + " property="
                  + property.getName());
      }

      for (int i = 0; i < elements.length; ++i)
      {
         XmlElement element = elements[i];
         if (trace)
            log.trace("Processing " + element + " for type=" + property.getBeanInfo().getName() + " property="
                  + property.getName());

         // Determine the parameters
         String overrideNamespace = null;
         String overrideName = null;
         boolean nillable = false;
         boolean required = false;

         TypeInfo localPropertyType = propertyType;

         if (element != null)
         {
            overrideNamespace = element.namespace();
            overrideName = element.name();
            nillable = element.nillable();
            required = element.required();
            Class<?> elementType = element.type();
            if (elementType != XmlElement.DEFAULT.class)
               localPropertyType = propertyType.getTypeInfoFactory().getTypeInfo(elementType);
         }

         if (xmlNsPrefix != null)
         {
            overrideNamespace = schemaBinding.getNamespace(xmlNsPrefix.prefix());
            if (overrideNamespace == null)
            {
               if (xmlNsPrefix.schemaTargetIfNotMapped())
               {
                  overrideNamespace = defaultNamespace;
               }
               else
               {
                  throw new IllegalStateException("Prefix '" + xmlNsPrefix.prefix()
                        + "' is not mapped to any namespace!");
               }
            }
         }

         // Determine the name
         QName propertyQName = generateXmlName(property.getName(), elementForm, overrideNamespace, overrideName);

         // Create the element
         JBossXmlGroup jbossXmlGroup = null;
         if (!propertyType.isPrimitive())
            jbossXmlGroup = ((ClassInfo) propertyType).getUnderlyingAnnotation(JBossXmlGroup.class);
         if (element == null && jbossXmlGroup != null)
         {
            if (trace)
               log.trace("Processing group for property " + property.getName() + " in "
                     + property.getBeanInfo().getName() + " " + jbossXmlGroup);

            JBossXmlChild[] children = jbossXmlGroup.value();
            if (children != null && children.length > 0)
            {
               TypeBinding elementTypeBinding = new TypeBinding();
               JBossXmlGroupText groupText = ((ClassInfo) propertyType).getUnderlyingAnnotation(JBossXmlGroupText.class);
               if (groupText != null && groupText.wrapper() != Object.class)
               {
                  BeanInfo wrapperInfo = JBossXBBuilder.configuration.getBeanInfo(groupText.wrapper());
                  TypeBinding wrapperTypeBinding = resolveTypeBinding(wrapperInfo.getClassInfo());
                  // Steal the attributes
                  Collection<AttributeBinding> otherAttributes = wrapperTypeBinding.getAttributes();
                  if (otherAttributes != null)
                  {
                     for (AttributeBinding other : otherAttributes)
                        elementTypeBinding.addAttribute(other);
                  }
                  ParticleHandler particleHandler = wrapperTypeBinding.getHandler();
                  if (particleHandler instanceof BeanHandler == false)
                     throw new IllegalStateException("Cannot wrap " + wrapperInfo.getName() + " not a bean type " + particleHandler);
                  BeanHandler beanHandler = (BeanHandler) particleHandler;
                  WrapperBeanAdapterFactory wrapperFactory = new WrapperBeanAdapterFactory(beanHandler.getBeanAdapterFactory(), propertyType.getType());
                  elementTypeBinding.setHandler(new BeanHandler(wrapperInfo.getName(), wrapperFactory));
                  elementTypeBinding.setSimpleType(wrapperTypeBinding.getSimpleType());
               }
               else
               {
                  elementTypeBinding.setHandler(BuilderParticleHandler.INSTANCE);
               }
               elementTypeBinding.setSchemaBinding(schemaBinding);
               ElementBinding elementBinding = createElementBinding(localPropertyType, elementTypeBinding, propertyQName, false);

               // Bind it to the model
               ParticleBinding particle = new ParticleBinding(elementBinding, 1, 1, false);
               if (required == false)
                  particle.setMinOccurs(0);
               localModel.addParticle(particle);

               // Setup the child model
               ChoiceBinding childModel = new ChoiceBinding(schemaBinding);
               childModel.setHandler(BuilderParticleHandler.INSTANCE);
               ParticleBinding particleBinding = new ParticleBinding(childModel);
               particleBinding.setMinOccurs(0);
               particleBinding.setMaxOccurs(1);
               elementTypeBinding.setParticle(particleBinding);

               for (JBossXmlChild child : children)
               {
                  QName childName = generateXmlName(child.name(), elementForm, child.namespace(), child.name());
                  TypeInfo childType = JBossXBBuilder.configuration.getTypeInfo(child.type());

                  TypeBinding childTypeBinding = resolveTypeBinding(childType);
                  ElementBinding childBinding = createElementBinding(childType, childTypeBinding, childName, false);
                  childBinding.setNillable(nillable);

                  // Bind it to the model
                  particle = new ParticleBinding(childBinding, child.minOccurs(), child.maxOccurs(), child.unbounded());
                  particle.setMinOccurs(0);
                  childModel.addParticle(particle);

                  if(childType.isMap())
                     bindMapProperty(property, (ClassInfo) childType, childName, (ModelGroupBinding) childTypeBinding.getParticle().getTerm());
                                    
                  DefaultElementInterceptor interceptor = new PropertyInterceptor(property, propertyType);
                  elementTypeBinding.pushInterceptor(childName, interceptor);
                  if (trace)
                     log.trace("Added interceptor " + childName + " for type=" + property.getBeanInfo().getName()
                           + " property=" + property.getName() + " interceptor=" + interceptor + " " + childType.getName());

                  beanAdapterFactory.addProperty(propertyQName, new PropertyHandler(property, propertyType));

                  JBossXmlGroupWildcard groupWildcard = ((ClassInfo) propertyType)
                        .getUnderlyingAnnotation(JBossXmlGroupWildcard.class);

                  if (groupWildcard != null)
                  {
                     ChildWildcardHandler groupWildcardHandler;
                     if (groupWildcard.wrapper() != Object.class)
                     {
                        BeanInfo wrapperInfo = JBossXBBuilder.configuration.getBeanInfo(groupWildcard.wrapper());
                        groupWildcardHandler = new ChildWildcardHandler(property, wrapperInfo, groupWildcard.property());
                     }
                     else
                        groupWildcardHandler = new ChildWildcardHandler(property);

                     WildcardBinding wildcard = new WildcardBinding(schemaBinding);
                     if (groupWildcard.lax())
                        wildcard.setProcessContents((short) 3); // Lax
                     else
                        wildcard.setProcessContents((short) 1); // Strict

                     particleBinding = new ParticleBinding(wildcard);
                     particleBinding.setMinOccurs(0);
                     particleBinding.setMaxOccurs(1);
                     childModel.addParticle(particleBinding);

                     elementTypeBinding.getWildcard().setWildcardHandler(groupWildcardHandler);
                  }
               }
            }
         }
         else
         {
            XBValueAdapter valueAdapter = null;
            XmlJavaTypeAdapter xmlTypeAdapter = property.getUnderlyingAnnotation(XmlJavaTypeAdapter.class);
            if (xmlTypeAdapter != null)
            {
               valueAdapter = new XBValueAdapter(xmlTypeAdapter.value(), propertyType.getTypeInfoFactory());
               localPropertyType = valueAdapter.getAdaptedTypeInfo();
            }

            ModelGroupBinding targetGroup = localModel;
            boolean isCol = false;
            boolean isMap = false;
            AbstractPropertyHandler propertyHandler = null;

            // a collection may be bound as a value of a complex type
            // and this is checked with the XmlType annotation
            if (propertyType.isCollection() && ((ClassInfo) propertyType).getUnderlyingAnnotation(XmlType.class) == null)
            {
               isCol = true;
               propertyHandler = new CollectionPropertyHandler(property, propertyType);
               // here we get the comp type based on the non-overriden property type...
               // which feels like a weak point
               TypeInfo typeArg = ((ClassInfo)property.getType()).getComponentType();

               if (typeArg != null && ((ClassInfo)typeArg).getUnderlyingAnnotation(JBossXmlModelGroup.class) == null)
               {// it may be a model group in which case we don't want to change the type
                  // TODO yes, this is another hack with collections
                  JBossXmlChild xmlChild = ((ClassInfo) propertyType).getUnderlyingAnnotation(JBossXmlChild.class);
                  if (xmlChild == null && localPropertyType.equals(propertyType))
                  { // the localPropertyType was not overriden previously so use the collection parameter type
                     localPropertyType = typeArg;
                  }
               }
            }
            // TODO this shouldn't be here (because localPropertyType should specify an item?)
            // this is to support the Descriptions.class -> DescriptionsImpl.class
            else if (localPropertyType.isCollection()
                  && ((ClassInfo) localPropertyType).getUnderlyingAnnotation(XmlType.class) == null)
            {
               if (valueAdapter != null)
               {
                  propertyHandler = new PropertyHandler(property, localPropertyType);
               }
               else
               {
                  propertyHandler = new CollectionPropertyHandler(property, localPropertyType);
               }
               isCol = true;
               localPropertyType = ((ClassInfo)localPropertyType).getComponentType();
            }
            else if (localPropertyType.isMap())
            {
               TypeBinding wrapperType = null;
               if(elements.length > 1)
               {
                  wrapperType = resolveTypeBinding(localPropertyType);
                  ElementBinding elementBinding = createElementBinding(localPropertyType, wrapperType, propertyQName, false);
                  elementBinding.setNillable(nillable);
                  elementBinding.setValueAdapter(valueAdapter);

                  // Bind it to the model
                  ParticleBinding particle = new ParticleBinding(elementBinding, 0, 1, isCol);
                  if (required == false)
                     particle.setMinOccurs(0);

                  targetGroup.addParticle(particle);
                  targetGroup = (ModelGroupBinding) wrapperType.getParticle().getTerm();
               }
               
               QName boundQName = bindMapProperty(property, (ClassInfo) localPropertyType, propertyQName, targetGroup);
               if(boundQName != null)
               {
                  if(wrapperType != null)
                  {
                     BeanAdapterFactory wrapperBeanFactory = ((BeanHandler)wrapperType.getHandler()).getBeanAdapterFactory();
                     Map<QName, AbstractPropertyHandler> properties = wrapperBeanFactory.getProperties();
                     if(!properties.containsKey(boundQName))
                     {
                        propertyHandler = new MapPropertyHandler(JBossXBBuilder.configuration, property, localPropertyType, true);
                        wrapperBeanFactory.addProperty(boundQName, propertyHandler);
                     }
                     propertyHandler = new PropertyHandler(property, localPropertyType);
                  }
                  else
                  {
                     propertyQName = boundQName;
                     propertyHandler = new MapPropertyHandler(JBossXBBuilder.configuration, property, localPropertyType, false);
                  }
                  isMap = true;
               }
               else
                  propertyHandler = new PropertyHandler(property, localPropertyType);
            }
            else
            {
               propertyHandler = new PropertyHandler(property, localPropertyType);
            }

            ParticleBinding particle;
            // DOM elements are going to be treated as unresolved
            // however having the property registered
            if (!isMap && !Element.class.getName().equals(propertyType.getName()))
            {               
               TypeBinding elementTypeBinding = resolveTypeBinding(localPropertyType);
               ElementBinding elementBinding = createElementBinding(localPropertyType, elementTypeBinding, propertyQName, false);
               elementBinding.setNillable(nillable);
               elementBinding.setValueAdapter(valueAdapter);
               
               JBossXmlPreserveWhitespace preserveSpace = property.getUnderlyingAnnotation(JBossXmlPreserveWhitespace.class);
               if(preserveSpace != null)
               {
                  elementBinding.setNormalizeSpace(preserveSpace.preserve() ? false : true);
                  if(trace)
                     log.trace("@JBossXmlPreserveWhitespace.preserve=" + preserveSpace.preserve() + " for " + elementBinding.getQName());
               }

               // Bind it to the model
               particle = new ParticleBinding(elementBinding, 1, 1, isCol);
               if (required == false)
                  particle.setMinOccurs(0);

               targetGroup.addParticle(particle);
            }

            beanAdapterFactory.addProperty(propertyQName, propertyHandler);
            if (trace)
               log.trace("Added property " + propertyQName + " for type=" + property.getBeanInfo().getName() + " property="
                     + property.getName() + " handler=" + propertyHandler);
         }
      }
   }
   
   private SequenceBinding bindXmlElementWrapper(TypeInfo propertyType, ModelGroupBinding parentModel, boolean wrapperNillable, QName wrapperQName)
   {
      TypeBinding wrapperType = new TypeBinding();
      SequenceBinding seq = new SequenceBinding(schemaBinding);
      seq.setHandler(BuilderParticleHandler.INSTANCE);
      ParticleBinding particle = new ParticleBinding(seq);
      wrapperType.setParticle(particle);
      wrapperType.setHandler(new DefaultElementHandler());

      ElementBinding wrapperElement = createElementBinding(propertyType, wrapperType, wrapperQName, false);
      wrapperElement.setNillable(wrapperNillable);
      wrapperElement.setSkip(Boolean.TRUE);
      particle = new ParticleBinding(wrapperElement, 1, 1, false);
      parentModel.addParticle(particle);
      return seq;
   }

   private BeanAdapterFactory createAdapterFactory(Class<? extends BeanAdapterBuilder> beanAdapterBuilderClass, BeanInfo beanInfo, MethodInfo factory)
   {
      try
      {
         BeanInfo adapterBuilderInfo = JBossXBBuilder.configuration.getBeanInfo(beanAdapterBuilderClass);
         BeanAdapterBuilder adapterBuilder = (BeanAdapterBuilder) adapterBuilderInfo.newInstance();
         return adapterBuilder.newFactory(beanInfo, factory);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Error creating BeanAdapterFactory for "
               + beanAdapterBuilderClass.getName(), t);
      }
   }

   /**
    * Create an array
    * 
    * @param localModel the current model
    * @return the new local model
    */
   private ModelGroupBinding createArray(ModelGroupBinding localModel)
   {
      SequenceBinding sequenceBinding = new SequenceBinding(schemaBinding);
      sequenceBinding.setHandler(BuilderParticleHandler.INSTANCE);
      ArraySequenceBinding arraySequenceBinding = new ArraySequenceBinding(schemaBinding);
      arraySequenceBinding.setHandler(BuilderParticleHandler.INSTANCE);
      ParticleBinding particle = new ParticleBinding(sequenceBinding);
      particle.setMinOccurs(0);
      particle.setMaxOccursUnbounded(true);
      arraySequenceBinding.addParticle(particle);
      particle = new ParticleBinding(arraySequenceBinding);
      localModel.addParticle(particle);
      return sequenceBinding;
   }

   /**
    * Create a collection
    * 
    * @param localModel the current model
    * @return the new local model
    */
   private ModelGroupBinding createCollection(ModelGroupBinding localModel)
   {
      SequenceBinding sequenceBinding = new SequenceBinding(schemaBinding);
      sequenceBinding.setHandler(BuilderParticleHandler.INSTANCE);
      ParticleBinding particle = new ParticleBinding(sequenceBinding);
      particle.setMinOccurs(0);
      particle.setMaxOccursUnbounded(true);
      localModel.addParticle(particle);
      return sequenceBinding;
   }

   /**
    * Add a namespace to the schema
    * 
    * @param namespace the namespace
    * @param erase whether to erase if there was only the default namespace
    */
   @SuppressWarnings("unchecked")
   private void addNamespace(String namespace, boolean erase)
   {
      Set<String> namespaces = schemaBinding.getNamespaces();
      if (erase && namespaces.size() <= 1)
         namespaces = new HashSet<String>(Collections.singleton(namespace));
      namespaces.add(namespace);
      schemaBinding.setNamespaces(namespaces);
   }

   /**
    * Create a new xml name
    * 
    * @param typeInfo the type info
    * @param form the namespace form
    * @param namespace the override namespace
    * @param name the override name
    * @return the xml name
    */
   protected QName generateXmlName(TypeInfo typeInfo, XmlNsForm form, String namespace, String name)
   {
      return generateXmlName(typeInfo.getSimpleName(), form, namespace, name);
   }

   /**
    * Create a new xml name
    * 
    * @param localName the raw local name
    * @param form the namespace form
    * @param namespace the override namespace
    * @param name the override name
    * @return the xml name
    */
   protected QName generateXmlName(String localName, XmlNsForm form, String namespace, String name)
   {
      String nsUri = XMLConstants.NULL_NS_URI;
      if (form == XmlNsForm.QUALIFIED)
         nsUri = defaultNamespace;
      if (namespace != null && JBossXmlConstants.DEFAULT.equals(namespace) == false)
         nsUri = namespace;
      if (name != null && JBossXmlConstants.DEFAULT.equals(name) == false)
         localName = name;
      else
         localName = JBossXBBuilder.generateXMLNameFromJavaName(localName, true, schemaBinding.isIgnoreLowLine());
      return new QName(nsUri, localName);
   }

   private void push(TypeInfo typeInfo)
   {
      push(typeInfo, null);
   }

   private void push(TypeInfo typeInfo, String joinpoint)
   {
      locations.push(new Location(typeInfo, joinpoint));
   }

   private void pop()
   {
      locations.pop();
   }

   private RuntimeException rethrowWithLocation(Throwable t)
   {
      StringBuilder message = new StringBuilder();
      message.append(t.getMessage());
      message.append("\n");
      while (locations.isEmpty() == false)
      {
         Location location = locations.pop();
         location.append(message);
         if (locations.isEmpty() == false)
            message.append('\n');
      }
      throw new RuntimeException(message.toString(), t);
   }

   /** A location */
   private class Location
   {
      /** The type info */
      TypeInfo typeInfo;

      /** The join point */
      String joinpoint;

      Location(TypeInfo typeInfo, String joinpoint)
      {
         this.typeInfo = typeInfo;
         this.joinpoint = joinpoint;
      }

      public void append(StringBuilder builder)
      {
         builder.append("at ");
         builder.append(typeInfo.getName());
         if (joinpoint != null)
            builder.append('.').append(joinpoint);
      }
   }

   private static class XBValueAdapter implements ValueAdapter
   {
      private final XmlAdapter xmlAdapter;

      private final TypeInfo adaptedTypeInfo;
      private final Type adaptedType;
      
      public XBValueAdapter(Class<? extends XmlAdapter> adapterImplClass, TypeInfoFactory factory)
      {
         try
         {
            this.xmlAdapter = adapterImplClass.newInstance();
         }
         catch (Exception e)
         {
            throw new IllegalStateException("Failed to create an instance of " + adapterImplClass.getName(), e);
         }

//         ClassInfo adapterImplInfo = (ClassInfo) factory.getTypeInfo(adapterImplClass);
//         ClassInfo xmlAdapterInfo = adapterImplInfo.getGenericSuperclass();
//         TypeInfo type = xmlAdapterInfo.getActualTypeArguments()[0];
         
         adaptedType = ((ParameterizedType)adapterImplClass.getGenericSuperclass()).getActualTypeArguments()[0];
         adaptedTypeInfo = factory.getTypeInfo(adaptedType);
      }

      public TypeInfo getAdaptedTypeInfo()
      {
         return adaptedTypeInfo;
      }

      public Type getAdaptedType()
      {
         return adaptedType;
      }

      public Object cast(Object o, Class c)
      {
         try
         {
            return xmlAdapter.unmarshal(o);
         }
         catch (Exception e)
         {
            throw new IllegalStateException("Failed to adapt value " + o + " to type " + c, e);
         }
      }
   }
   
   private QName bindMapProperty(PropertyInfo prop, ClassInfo propType, QName propertyQName, ModelGroupBinding targetGroup)
   {
      QName boundQName = null;
      
      JBossXmlMapEntry entryElement = null;
      if(prop != null)
         entryElement = prop.getUnderlyingAnnotation(JBossXmlMapEntry.class);
      if(entryElement == null)
         entryElement = propType.getUnderlyingAnnotation(JBossXmlMapEntry.class);

      JBossXmlMapKeyElement keyElement = null;
      if(prop != null)
         keyElement = prop.getUnderlyingAnnotation(JBossXmlMapKeyElement.class);
      if(keyElement == null)
         keyElement = propType.getUnderlyingAnnotation(JBossXmlMapKeyElement.class);
      
      JBossXmlMapKeyAttribute keyAttribute = null;
      if(prop != null)
         keyAttribute = prop.getUnderlyingAnnotation(JBossXmlMapKeyAttribute.class);
      if(keyAttribute == null)
         keyAttribute = propType.getUnderlyingAnnotation(JBossXmlMapKeyAttribute.class);
      
      if(keyElement != null || keyAttribute != null)
      {
         // further assuming the map is bound

         JBossXmlMapValueElement valueElement = null;
         if(prop != null)
            valueElement = prop.getUnderlyingAnnotation(JBossXmlMapValueElement.class);
         if(valueElement == null)
            valueElement = propType.getUnderlyingAnnotation(JBossXmlMapValueElement.class);
         
         JBossXmlMapValueAttribute valueAttribute = null;
         if(prop != null)
            valueAttribute = prop.getUnderlyingAnnotation(JBossXmlMapValueAttribute.class);
         if(valueAttribute == null)
            valueAttribute = propType.getUnderlyingAnnotation(JBossXmlMapValueAttribute.class);

         TypeInfo keyType = propType.getKeyType();
         TypeInfo valueType = propType.getValueType();

         // entry handler
         BeanAdapterFactory entryAdapterFactory = null;
         BeanInfo entryInfo = JBossXBBuilder.configuration.getBeanInfo(DefaultMapEntry.class);
         entryAdapterFactory = createAdapterFactory(DefaultBeanAdapterBuilder.class, entryInfo, null);
         BeanHandler entryHandler = new BeanHandler(entryInfo.getName(), entryAdapterFactory);

         TypeBinding entryType = null;
         TypeInfo entryTypeInfo = null;

         // bind the entry element if present
         if(entryElement != null && !JBossXmlConstants.DEFAULT.equals(entryElement.name()))
         {
            String ns = entryElement.namespace();
            if(JBossXmlConstants.DEFAULT.equals(ns))
               ns = defaultNamespace;                  
            QName entryName = new QName(ns, entryElement.name());

            entryType = new TypeBinding();
            entryType.setSchemaBinding(schemaBinding);
            entryType.setHandler(entryHandler);

            entryTypeInfo = JBossXBBuilder.configuration.getTypeInfo(DefaultMapEntry.class);                     
            ElementBinding entryElementBinding = createElementBinding(entryTypeInfo, entryType, entryName, false);
            ParticleBinding entryParticle = new ParticleBinding(entryElementBinding, 0, -1, true);
            targetGroup.addParticle(entryParticle);
               
            propertyQName = entryName;
               
            if(keyAttribute != null)
            {
               TypeBinding attributeType = resolveTypeBinding(keyType);
               AttributeHandler attributeHandler = new PropertyHandler(entryInfo.getProperty("key"), keyType);
               QName attrQName = generateXmlName(keyType, attributeForm, keyAttribute.namespace(), keyAttribute.name());
               AttributeBinding keyBinding = new AttributeBinding(schemaBinding, attrQName, attributeType, attributeHandler);
               keyBinding.setRequired(true);
               entryType.addAttribute(keyBinding);
            }

            if(valueAttribute != null)
            {
               TypeBinding attributeType = resolveTypeBinding(valueType);
               AttributeHandler attributeHandler = new PropertyHandler(entryInfo.getProperty("value"), valueType);
               QName attrQName = generateXmlName(valueType, attributeForm, valueAttribute.namespace(), valueAttribute.name());
               AttributeBinding valueBinding = new AttributeBinding(schemaBinding, attrQName, attributeType, attributeHandler);
               valueBinding.setRequired(true);
               entryType.addAttribute(valueBinding);
            }
            else if(valueElement == null)
            {
               CharactersHandler charactersHandler = new ValueHandler(entryInfo.getProperty("value"), valueType);
               entryType.setSimpleType(charactersHandler);
            }
         }
         
         SequenceBinding keyValueSequence = null;
         if(keyElement != null)
         {
            keyValueSequence = new SequenceBinding(schemaBinding);                     
            if(entryType == null)
            {
               keyValueSequence.setSkip(Boolean.FALSE);
               keyValueSequence.setQName(propertyQName);
               schemaBinding.addGroup(keyValueSequence.getQName(), keyValueSequence);
               ParticleBinding keyValueParticle = new ParticleBinding(keyValueSequence, 0, -1, true);
               targetGroup.addParticle(keyValueParticle);
               keyValueSequence.setHandler(entryHandler);
            }
            else
            {
               ParticleBinding keyValueParticle = new ParticleBinding(keyValueSequence, 1, 1, false);
               entryType.setParticle(keyValueParticle);
            }
            
            // key element
            TypeBinding keyTypeBinding = resolveTypeBinding(keyType);                  
            String keyNs = keyElement.namespace();
            if(JBossXmlConstants.DEFAULT.equals(keyNs))
               keyNs = defaultNamespace;                  
            ElementBinding keyElementBinding = createElementBinding(keyType, keyTypeBinding, new QName(keyNs, keyElement.name()), false);
            ParticleBinding particle = new ParticleBinding(keyElementBinding, 1, 1, false);
            keyValueSequence.addParticle(particle);
            PropertyHandler keyHandler = new PropertyHandler(entryInfo.getProperty("key"), keyType);
            entryAdapterFactory.addProperty(keyElementBinding.getQName(), keyHandler);
         }
         
         if(valueElement != null)
         {
            TypeBinding valueTypeBinding = resolveTypeBinding(valueType);                  
            String valueNs = valueElement.namespace();
            if(JBossXmlConstants.DEFAULT.equals(valueNs))
               valueNs = defaultNamespace;                  
            ElementBinding valueElementBinding = createElementBinding(valueType, valueTypeBinding, new QName(valueNs, valueElement.name()), false);
            ParticleBinding particle = new ParticleBinding(valueElementBinding, 1, 1, false);
            keyValueSequence.addParticle(particle);
            PropertyHandler valueHandler = new PropertyHandler(entryInfo.getProperty("value"), valueType);
            entryAdapterFactory.addProperty(valueElementBinding.getQName(), valueHandler);
         }

         // TODO: need to verify correct binding before proceeding
         boundQName = propertyQName;
      }
      else if(entryElement != null && !JBossXmlMapEntry.DEFAULT.class.equals(entryElement.type()))
      {
         if(!JBossXmlConstants.DEFAULT.equals(entryElement.name()))
         {
            String ns = entryElement.namespace();
            if(JBossXmlConstants.DEFAULT.equals(ns))
               ns = propertyQName.getNamespaceURI();
            propertyQName = new QName(ns, entryElement.name());
         }

         TypeInfo entryTypeInfo = JBossXBBuilder.configuration.getTypeInfo(entryElement.type());
         ElementBinding entryElementBinding = createElementBinding(entryTypeInfo, propertyQName.getLocalPart(), false);
         ParticleBinding entryParticle = new ParticleBinding(entryElementBinding, 0, -1, true);
         targetGroup.addParticle(entryParticle);
         boundQName = propertyQName;
      }
      return boundQName;
   }
}
