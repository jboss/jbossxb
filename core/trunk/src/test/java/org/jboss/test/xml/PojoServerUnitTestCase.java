/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.test.xml.pojoserver.deployment.AbstractKernelDeployment;
import org.jboss.test.xml.pojoserver.metadata.AbstractArrayMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractBeanMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractCollectionMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractConstructorMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractDemandMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractDependencyMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractDependencyValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractLifecycleMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractListMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractMapMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractParameterMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractPropertyMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractSetMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractSupplyMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.ControllerMode;
import org.jboss.test.xml.pojoserver.metadata.ControllerState;
import org.jboss.test.xml.pojoserver.metadata.GenericBeanFactory;
import org.jboss.test.xml.pojoserver.metadata.StringValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 57581 $</tt>
 */
public class PojoServerUnitTestCase
   extends PojoServerTestBase
{
   public PojoServerUnitTestCase(String localName)
   {
      super(localName);
   }

   public void testXsdBinder() throws Exception
   {
      SchemaBinding doc = readXsd();
      /* TODO assertSchemaBinding(doc); */
   }

   public void testManualBinding() throws Exception
   {
      SchemaBinding doc = createBinding();
      /* TODO assertSchemaBinding(doc); */
   }

   protected SchemaBinding getSchemaBinding()
   {
      if (schemaBinding != null)
         return schemaBinding;
      
      long start = System.currentTimeMillis();
      
      schemaBinding = readXsd();

      long now = System.currentTimeMillis();
      log.debug("Reading xsd took " + (now - start) + " milliseconds");
      
      // deployment binding
      TypeBinding deploymentType = schemaBinding.getType(deploymentTypeQName);
      deploymentType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractKernelDeployment();
         }
      });

      // deployment has a list beans
      deploymentType.pushInterceptor(beanQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractKernelDeployment deployment = (AbstractKernelDeployment) parent;
            AbstractBeanMetaData bean = (AbstractBeanMetaData) child;
            List<AbstractBeanMetaData> beans = deployment.getBeans();
            if (beans == null)
            {
               beans = new ArrayList<AbstractBeanMetaData>();
               deployment.setBeans(beans);
            }
            beans.add(bean);
         }
      });

      // deployment has a list beanfactorys
      deploymentType.pushInterceptor(beanFactoryQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractKernelDeployment deployment = (AbstractKernelDeployment) parent;
            AbstractBeanMetaData bean = (AbstractBeanMetaData) child;
            List<AbstractBeanMetaData> beans = deployment.getBeans();
            if (beans == null)
            {
               beans = new ArrayList<AbstractBeanMetaData>();
               deployment.setBeans(beans);
            }
            beans.add(bean);
         }
      });

      // bean binding
      TypeBinding beanType = schemaBinding.getType(beanTypeQName);
      beanType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractBeanMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("name".equals(localName))
                  bean.setName(attrs.getValue(i));
               else if ("class".equals(localName))
                  bean.setBean(attrs.getValue(i));
               else if ("mode".equals(localName))
                  bean.setMode(new ControllerMode(attrs.getValue(i)));
            }
         }
      });

      // beanfactory binding
      TypeBinding beanFactoryType = schemaBinding.getType(beanFactoryTypeQName);
      beanFactoryType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            AbstractBeanMetaData beanMetaData = new AbstractBeanMetaData();
            beanMetaData.setBean(GenericBeanFactory.class.getName());
            beanMetaData.setProperties(new HashSet<AbstractPropertyMetaData>());
            return beanMetaData;
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) o;
            Set<AbstractPropertyMetaData> properties = bean.getProperties();
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("name".equals(localName))
                  bean.setName(attrs.getValue(i));
               else if ("class".equals(localName))
                  properties.add(new AbstractPropertyMetaData("bean", attrs.getValue(i)));
            }
         }
      });

      // bean has a constructor
      beanType.pushInterceptor(constructorQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) child;
            bean.setConstructor(constructor);
         }
      });

      // beanfactory has a constructor
      beanFactoryType.pushInterceptor(constructorQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) child;
            Set<AbstractPropertyMetaData> properties = bean.getProperties();
            properties.add(new AbstractPropertyMetaData("constructor", new AbstractValueMetaData(constructor)));
         }
      });

      // constructor binding
      TypeBinding constructorType = schemaBinding.getType(constructorTypeQName);
      constructorType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractConstructorMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("factoryClass".equals(localName))
                  constructor.setFactoryClass(attrs.getValue(i));
               else if ("factoryMethod".equals(localName))
                  constructor.setFactoryMethod(attrs.getValue(i));
            }
         }
      });

      // constructor has a factory
      constructorType.pushInterceptor(factoryQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) parent;
            AbstractDependencyValueMetaData factory = (AbstractDependencyValueMetaData) child;
            constructor.setFactory(factory);
         }
      });

      // constructor has a list parameters
      constructorType.pushInterceptor(parameterQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) parent;
            AbstractParameterMetaData parameter = (AbstractParameterMetaData) child;
            List<AbstractParameterMetaData> parameters = constructor.getParameters();
            if (parameters == null)
            {
               parameters = new ArrayList<AbstractParameterMetaData>();
               constructor.setParameters(parameters);
            }
            parameters.add(parameter);
         }
      });

      ModelGroupBinding valueGroup = schemaBinding.getGroup(valueGroupQName);
      for(Iterator<?> i = valueGroup.getParticles().iterator(); i.hasNext();)
      {
         TermBinding term = ((ParticleBinding)i.next()).getTerm();
         if(!term.isWildcard())
         {
            ElementBinding e = (ElementBinding) term;
            if(e.getQName().equals(nullQName))
            {
               e.pushInterceptor(NULLVALUES);
            }
            else
            {
               e.pushInterceptor(VALUES);
            }
         }
      }
      
      // parameter binding
      TypeBinding parameterType = schemaBinding.getType(parameterTypeQName);
      parameterType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractParameterMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractParameterMetaData parameter = (AbstractParameterMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("class".equals(localName))
                  parameter.setType(attrs.getValue(i));
            }
         }
      });
      
      // parameter can take a value
      parameterType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return new StringValueMetaData(value);
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            AbstractParameterMetaData parameter = (AbstractParameterMetaData) owner;
            parameter.setValue((StringValueMetaData) value);
         }
      });

      // bean has a create
      beanType.pushInterceptor(createQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractLifecycleMetaData lifecycle = (AbstractLifecycleMetaData) child;
            bean.setCreate(lifecycle);
         }
      });

      // bean has a start
      beanType.pushInterceptor(startQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractLifecycleMetaData lifecycle = (AbstractLifecycleMetaData) child;
            bean.setStart(lifecycle);
         }
      });

      // bean has a stop
      beanType.pushInterceptor(stopQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractLifecycleMetaData lifecycle = (AbstractLifecycleMetaData) child;
            bean.setStop(lifecycle);
         }
      });

      // bean has a destroy
      beanType.pushInterceptor(destroyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractLifecycleMetaData lifecycle = (AbstractLifecycleMetaData) child;
            bean.setDestroy(lifecycle);
         }
      });

      // lifecycle binding
      TypeBinding lifecycleType = schemaBinding.getType(lifecycleTypeQName);
      lifecycleType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractLifecycleMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractLifecycleMetaData lifecycle = (AbstractLifecycleMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("method".equals(localName))
                  lifecycle.setMethodName(attrs.getValue(i));
            }
         }
      });

      // lifecycle has a list parameters
      lifecycleType.pushInterceptor(parameterQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractLifecycleMetaData lifecycle = (AbstractLifecycleMetaData) parent;
            AbstractParameterMetaData parameter = (AbstractParameterMetaData) child;
            List<AbstractParameterMetaData> parameters = lifecycle.getParameters();
            if (parameters == null)
            {
               parameters = new ArrayList<AbstractParameterMetaData>();
               lifecycle.setParameters(parameters);
            }
            parameters.add(parameter);
         }
      });

      // bean has a set of properties
      beanType.pushInterceptor(propertyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractPropertyMetaData property = (AbstractPropertyMetaData) child;
            Set<AbstractPropertyMetaData> properties = bean.getProperties();
            if (properties == null)
            {
               properties = new HashSet<AbstractPropertyMetaData>();
               bean.setProperties(properties);
            }
            properties.add(property);
         }
      });

      // beanfactory has a set of properties
      beanFactoryType.pushInterceptor(propertyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            Set<AbstractPropertyMetaData> properties = bean.getProperties();
            AbstractPropertyMetaData props = null;
            for (Iterator<AbstractPropertyMetaData> i = properties.iterator(); i.hasNext();)
            {
               AbstractPropertyMetaData prop = i.next();
               if ("properties".equals(prop.getName()))
               {
                  props = prop;
                  break;
               }
            }
            AbstractMapMetaData map = null;
            if (props == null)
            {
               map = new AbstractMapMetaData();
               props = new AbstractPropertyMetaData("properties", map);
               properties.add(props);
            }
            else
            {
               map = (AbstractMapMetaData) props.getValue(); 
            }

            AbstractPropertyMetaData property = (AbstractPropertyMetaData) child;
            ValueMetaData valueMetaData = property.getValue();
            valueMetaData = new AbstractValueMetaData(valueMetaData);
            map.put(new AbstractValueMetaData(property.getName()), valueMetaData);
         }
      });

      // bean has a set of depends
      beanType.pushInterceptor(dependsQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractDependencyMetaData dependency = (AbstractDependencyMetaData) child;
            Set<Object> demands = bean.getDemands();
            if (demands == null)
            {
               demands = new HashSet<Object>();
               bean.setDemands(demands);
            }
            demands.add(dependency);
         }
      });

      // bean has a set of demands
      beanType.pushInterceptor(demandQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractDemandMetaData demand = (AbstractDemandMetaData) child;
            Set<Object> demands = bean.getDemands();
            if (demands == null)
            {
               demands = new HashSet<Object>();
               bean.setDemands(demands);
            }
            demands.add(demand);
         }
      });

      // bean has a set of supplies
      beanType.pushInterceptor(supplyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractBeanMetaData bean = (AbstractBeanMetaData) parent;
            AbstractSupplyMetaData supply = (AbstractSupplyMetaData) child;
            Set<AbstractSupplyMetaData> supplies = bean.getSupplies();
            if (supplies == null)
            {
               supplies = new HashSet<AbstractSupplyMetaData>();
               bean.setSupplies(supplies);
            }
            supplies.add(supply);
         }
      });

      // property binding
      TypeBinding propertyType = schemaBinding.getType(propertyTypeQName);
      propertyType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractPropertyMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractPropertyMetaData property = (AbstractPropertyMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("name".equals(localName))
                  property.setName(attrs.getValue(i));
               else if ("class".equals(localName))
               {
                  StringValueMetaData svmd = new StringValueMetaData();
                  svmd.setType(attrs.getValue(i));
                  property.setValue(svmd);
               }
            }
         }
         
         public Object endElement(Object o, QName qName, ElementBinding element)
         {
            AbstractPropertyMetaData x = (AbstractPropertyMetaData) o;
            String name = x.getName();
            if (name == null || name.trim().length() == 0)
               throw new IllegalArgumentException("Null or empty property name.");
            return o;
         }
      });

      // property can take a value
      propertyType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return new StringValueMetaData(value);
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            AbstractPropertyMetaData property = (AbstractPropertyMetaData) owner;
            StringValueMetaData svmd = (StringValueMetaData) value;
            ValueMetaData vmd = property.getValue();
            if (vmd != null && vmd instanceof StringValueMetaData)
            {
               StringValueMetaData previous = (StringValueMetaData) vmd;
               String type = previous.getType();
               if (type != null)
                  svmd.setType(type);
            }
            property.setValue(svmd);
         }
      });

      // dependency binding
      TypeBinding dependsType = schemaBinding.getType(dependsTypeQName);
      dependsType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractDependencyMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
         }
         
         public Object endElement(Object o, QName qName, ElementBinding element)
         {
            AbstractDependencyMetaData x = (AbstractDependencyMetaData) o;
            String name = (String) x.getDependency();
            if (name == null || name.trim().length() == 0)
               throw new IllegalArgumentException("Null or empty dependency.");
            return o;
         }
      });

      // depends can take a value
      dependsType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return value;
         }

         public void setValue(QName qname, ElementBinding element, Object owner, Object value)
         {
            AbstractDependencyMetaData depends = (AbstractDependencyMetaData) owner;
            depends.setDependency(value);
         }
      });

      // demand binding
      TypeBinding demandType = schemaBinding.getType(demandTypeQName);
      demandType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractDemandMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractDemandMetaData demand = (AbstractDemandMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("state".equals(localName))
                  demand.setWhenRequired(new ControllerState(attrs.getValue(i)));
            }
         }
         
         public Object endElement(Object o, QName qName, ElementBinding element)
         {
            AbstractDemandMetaData x = (AbstractDemandMetaData) o;
            String name = (String) x.getDemand();
            if (name == null || name.trim().length() == 0)
               throw new IllegalArgumentException("Null or empty demand.");
            return o;
         }
      });

      // demand can take a value
      demandType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return value;
         }

         public void setValue(QName qname, ElementBinding element, Object owner, Object value)
         {
            AbstractDemandMetaData demand = (AbstractDemandMetaData) owner;
            demand.setDemand(value);
         }
      });

      // supply binding
      TypeBinding supplyType = schemaBinding.getType(supplyTypeQName);
      supplyType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractSupplyMetaData();
         }
         
         public Object endElement(Object o, QName qName, ElementBinding element)
         {
            AbstractSupplyMetaData x = (AbstractSupplyMetaData) o;
            String name = (String) x.getSupply();
            if (name == null || name.trim().length() == 0)
               throw new IllegalArgumentException("Null or empty supply.");
            return o;
         }
      });

      // supply can take a value
      supplyType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return value;
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            AbstractSupplyMetaData supply = (AbstractSupplyMetaData) owner;
            supply.setSupply(value);
         }
      });

      // dependency binding
      TypeBinding dependencyType = schemaBinding.getType(dependencyTypeQName);
      dependencyType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractDependencyValueMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("bean".equals(localName))
                  dependency.setValue(attrs.getValue(i));
               else if ("property".equals(localName))
                  dependency.setProperty(attrs.getValue(i));
               else if ("state".equals(localName))
                  dependency.setDependentState(new ControllerState(attrs.getValue(i)));
            }
         }
         
         public Object endElement(Object o, QName qName, ElementBinding element)
         {
            AbstractDependencyValueMetaData x = (AbstractDependencyValueMetaData) o;
            String name = (String) x.getUnderlyingValue();
            if (name == null || name.trim().length() == 0)
               throw new IllegalArgumentException("Null or empty bean in injection/factory.");
            return o;
         }
      });

      // value binding
      TypeBinding plainValueType = schemaBinding.getType(plainValueTypeQName);
      plainValueType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new StringValueMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            StringValueMetaData value = (StringValueMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("class".equals(localName))
                  value.setType(attrs.getValue(i));
            }
         }
      });

      // value can take a value
      plainValueType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return value;
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            StringValueMetaData valueMetaData = (StringValueMetaData) owner;
            valueMetaData.setValue(value);
         }
      });

      // value binding
      TypeBinding valueType = schemaBinding.getType(valueTypeQName);
      valueType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractValueMetaData(new StringValueMetaData());
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractValueMetaData value = (AbstractValueMetaData) o;
            StringValueMetaData string = (StringValueMetaData) value.getValue();
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("class".equals(localName))
                  string.setType(attrs.getValue(i));
            }
         }
      });

      // value can take a value
      valueType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, org.jboss.xb.binding.metadata.ValueMetaData valueMetaData, String value)
         {
            return value;
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            AbstractValueMetaData valueMetaData = (AbstractValueMetaData) owner;
            StringValueMetaData string = (StringValueMetaData) valueMetaData.getValue();
            string.setValue(value);
         }
      });

      // collection binding
      configureCollection(collectionTypeQName);

      // list binding
      configureCollection(listTypeQName);

      // set binding
      configureCollection(setTypeQName);

      // array binding
      configureCollection(arrayTypeQName);

      // map binding
      TypeBinding mapType = schemaBinding.getType(mapTypeQName);
      mapType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractMapMetaData();
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractMapMetaData collection = (AbstractMapMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("class".equals(localName))
                  collection.setType(attrs.getValue(i));
               else if ("keyClass".equals(localName))
                  collection.setKeyType(attrs.getValue(i));
               else if ("valueClass".equals(localName))
                  collection.setValueType(attrs.getValue(i));
            }
         }
      });

      // map has a map entries
      mapType.pushInterceptor(entryQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractMapMetaData map = (AbstractMapMetaData) parent;
            MapEntry entry = (MapEntry) child;
            AbstractValueMetaData entryKey = (AbstractValueMetaData) entry.key;
            if (entryKey == null)
               throw new IllegalArgumentException("No key in map entry");
            AbstractValueMetaData entryValue = (AbstractValueMetaData) entry.value; 
            if (entryValue == null)
               throw new IllegalArgumentException("No value in map entry");
            map.put(entryKey.getValue(), entryValue.getValue());
         }
      });

      // entry binding
      TypeBinding entryType = schemaBinding.getType(entryTypeQName);
      entryType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new MapEntry();
         }
      });

      // entry has a key
      entryType.pushInterceptor(keyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            MapEntry entry = (MapEntry) parent;
            ValueMetaData value = (ValueMetaData) child;
            entry.key = value;
         }
      });

      // entry has a value
      entryType.pushInterceptor(valueQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            MapEntry entry = (MapEntry) parent;
            ValueMetaData value = (ValueMetaData) child;
            entry.value = value;
         }
      });

      now = System.currentTimeMillis();

      log.debug("Creating schema binding took " + (now - start) + " milliseconds");
      
      return schemaBinding;
   }

   protected String getXsd()
   {
      return "xml/bean-deployer_1_0.xsd";
   }

   // Private

   private static SchemaBinding createBinding()
   {
      SchemaBinding cursor = new SchemaBinding();

      //
      // Declare all the types
      //
/* TODO
      TypeBinding kernelControllerStateType = new TypeBinding(kernelControllerStateTypeQName);
      cursor.addType(kernelControllerStateType);
      TypeBinding classNameType = new TypeBinding(classNameTypeQName);
      cursor.addType(classNameType);
      TypeBinding deploymentType = new TypeBinding(deploymentTypeQName);
      cursor.addType(deploymentType);
      TypeBinding beanType = new TypeBinding(beanTypeQName);
      cursor.addType(beanType);
      TypeBinding annotationType = new TypeBinding(annotationTypeQName);
      cursor.addType(annotationType);
      TypeBinding constructorType = new TypeBinding(constructorTypeQName);
      cursor.addType(constructorType);
      TypeBinding namedValueType = new TypeBinding(namedValueTypeQName);
      cursor.addType(namedValueType);
      TypeBinding demandType = new TypeBinding(demandTypeQName);
      cursor.addType(demandType);
      TypeBinding stringType = new TypeBinding(Constants.QNAME_STRING);
      cursor.addType(stringType);
      TypeBinding tokenType = new TypeBinding(Constants.QNAME_TOKEN);
      cursor.addType(tokenType);
      TypeBinding factoryType = new TypeBinding(factoryTypeQName);
      cursor.addType(factoryType);
      TypeBinding valueWithClassType = new TypeBinding(valueWithClassTypeQName);
      cursor.addType(valueWithClassType);
      TypeBinding dependencyType = new TypeBinding();
      TypeBinding listType = new TypeBinding(listTypeQName);
      cursor.addType(listType);
      TypeBinding mapType = new TypeBinding(mapTypeQName);
      cursor.addType(mapType);
      TypeBinding propsType = new TypeBinding(propsTypeQName);
      cursor.addType(propsType);
      TypeBinding mixedValueType = new TypeBinding(mixedValueTypeQName);
      cursor.addType(mixedValueType);
      TypeBinding valueType = new TypeBinding(valueTypeQName);
      cursor.addType(valueType);
      TypeBinding nullType = new TypeBinding();
      TypeBinding annotationAttributeType = new TypeBinding(annotationAttributeTypeQName);
      cursor.addType(annotationAttributeType);

      //
      // Assemble schema
      //

      deploymentType.addElement(beanQName, beanType);

      beanType.addElement(annotationQName, annotationType);
      beanType.addElement(constructorQName, constructorType);
      beanType.addElement(propertyQName, namedValueType);
      beanType.addElement(demandQName, demandType);
      beanType.addElement(supplyQName, stringType);
      beanType.addAttribute(nameAttrQName, stringType, AttributeHandler.NOOP);
      beanType.addAttribute(classAttrQName, tokenType, AttributeHandler.NOOP);

      constructorType.addElement(annotationQName, annotationType);
      constructorType.addElement(factoryQName, factoryType);
      constructorType.addElement(parameterQName, valueWithClassType);
      constructorType.addAttribute(factoryClassAttrQName, classNameType, AttributeHandler.NOOP);
      constructorType.addAttribute(factoryMethodAttrQName, tokenType, AttributeHandler.NOOP);

      // valueGroup
      Map valueGroup = new HashMap();
      valueGroup.put(listQName, listType);
      valueGroup.put(setQName, listType);
      valueGroup.put(listQName, listType);
      valueGroup.put(mapQName, mapType);
      valueGroup.put(propsQName, propsType);
      valueGroup.put(dependencyQName, dependencyType);
      valueGroup.put(parameterQName, valueWithClassType);
      valueGroup.put(propertyQName, namedValueType);
      valueGroup.put(nullQName, nullType);

      mixedValueType.addGroup(valueGroup);
      mixedValueType.addElement(beanQName, beanType);

      // todo type extensions
      namedValueType.addGroup(valueGroup);
      namedValueType.addElement(beanQName, beanType);
      namedValueType.addElement(annotationQName, annotationType);
      namedValueType.addAttribute(nameAttrQName, stringType, AttributeHandler.NOOP);

      valueType.addGroup(valueGroup);
      valueType.addElement(beanQName, beanType);

      valueWithClassType.addGroup(valueGroup);
      valueWithClassType.addElement(beanQName, beanType);
      valueWithClassType.addAttribute(classAttrQName, classNameType, AttributeHandler.NOOP);

      listType.addElement(valueQName, mixedValueType);
      listType.addAttribute(classAttrQName, classNameType, AttributeHandler.NOOP);

      mapType.addElement(mapEntryQName, namedValueType);
      mapType.addAttribute(classAttrQName, classNameType, AttributeHandler.NOOP);

      TypeBinding propsEntryType = new TypeBinding();
      propsType.addElement(propsEntryQName, propsEntryType);
      propsEntryType.addAttribute(nameAttrQName, stringType, AttributeHandler.NOOP);

      factoryType.addAttribute(beanAttrQName, stringType, AttributeHandler.NOOP);

      demandType.addAttribute(whenRequiredAttrQName, kernelControllerStateType, AttributeHandler.NOOP);

      annotationType.addElement(attributeQName, annotationAttributeType);
      annotationType.addAttribute(nameAttrQName, stringType, AttributeHandler.NOOP);

      annotationAttributeType.addAttribute(nameAttrQName, stringType, AttributeHandler.NOOP);
      annotationAttributeType.addAttribute(valueAttrQName, stringType, AttributeHandler.NOOP);

      dependencyType.addAttribute(valueAttrQName, stringType, AttributeHandler.NOOP);
      dependencyType.addAttribute(stateAttrQName, kernelControllerStateType, AttributeHandler.NOOP);

      // global elements
      cursor.addElement(deploymentQName, deploymentType);
*/
      return cursor;
   }
   
   /**
    * Configure a collection.
    */
   private static void configureCollection(QName qname)
   {
      TypeBinding collectionType = schemaBinding.getType(qname);
      collectionType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            if (collectionQName.equals(name))
               return new AbstractCollectionMetaData();
            else if (listQName.equals(name))
               return new AbstractListMetaData();
            else if (setQName.equals(name))
               return new AbstractSetMetaData();
            else if (arrayQName.equals(name))
               return new AbstractArrayMetaData();
            else
               throw new IllegalArgumentException("Unknown collection qname=" + name);
         }

         public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractCollectionMetaData collection = (AbstractCollectionMetaData) o;
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String localName = attrs.getLocalName(i);
               if ("class".equals(localName))
                  collection.setType(attrs.getValue(i));
               else if ("elementClass".equals(localName))
                  collection.setElementType(attrs.getValue(i));
            }
         }
      });
   }
   
   private static class MapEntry
   {
      public Object key;
      public Object value;
   }
}
