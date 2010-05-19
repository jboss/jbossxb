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

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.test.BaseTestCase;
import org.jboss.test.xml.pojoserver.deployment.AbstractKernelDeployment;
import org.jboss.test.xml.pojoserver.metadata.AbstractBeanMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractCollectionMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractDemandMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractDependencyValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractListMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractMapMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractParameterMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractPropertyMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractSetMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractSupplyMetaData;
import org.jboss.test.xml.pojoserver.metadata.AbstractValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.ConstructorMetaData;
import org.jboss.test.xml.pojoserver.metadata.ControllerState;
import org.jboss.test.xml.pojoserver.metadata.GenericBeanFactory;
import org.jboss.test.xml.pojoserver.metadata.StringValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.ValueMetaData;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 57517 $</tt>
 */
public abstract class PojoServerTestBase extends BaseTestCase
{
   /** The schema name */
   protected static final String SCHEMA_NAME = "/xml/bean-deployer_1_0.xsd";

   /** The namespace */
   protected static final String BEAN_DEPLOYER_NS = "urn:jboss:bean-deployer";

   /** The deployment binding */
   protected static final QName deploymentTypeQName = new QName(BEAN_DEPLOYER_NS, "deploymentType");

   /** The bean binding */
   protected static final QName beanTypeQName = new QName(BEAN_DEPLOYER_NS, "beanType");

   /** The bean element name */
   protected static final QName beanQName = new QName(BEAN_DEPLOYER_NS, "bean");

   /** The beanfactory binding */
   protected static final QName beanFactoryTypeQName = new QName(BEAN_DEPLOYER_NS, "beanfactoryType");

   /** The beanfactory element name */
   protected static final QName beanFactoryQName = new QName(BEAN_DEPLOYER_NS, "beanfactory");

   /** The constructor binding */
   protected static final QName constructorTypeQName = new QName(BEAN_DEPLOYER_NS, "constructorType");

   /** The constructor element name */
   protected static final QName constructorQName = new QName(BEAN_DEPLOYER_NS, "constructor");

   /** The factory element name */
   protected static final QName factoryQName = new QName(BEAN_DEPLOYER_NS, "factory");

   /** The parameter binding */
   protected static final QName parameterTypeQName = new QName(BEAN_DEPLOYER_NS, "parameterType");

   /** The parameter element name */
   protected static final QName parameterQName = new QName(BEAN_DEPLOYER_NS, "parameter");

   /** The lifecycle binding */
   protected static final QName lifecycleTypeQName = new QName(BEAN_DEPLOYER_NS, "lifecycleType");

   /** The create element name */
   protected static final QName createQName = new QName(BEAN_DEPLOYER_NS, "create");

   /** The start element name */
   protected static final QName startQName = new QName(BEAN_DEPLOYER_NS, "start");

   /** The stop element name */
   protected static final QName stopQName = new QName(BEAN_DEPLOYER_NS, "stop");

   /** The destroy element name */
   protected static final QName destroyQName = new QName(BEAN_DEPLOYER_NS, "destroy");

   /** The property binding */
   protected static final QName propertyTypeQName = new QName(BEAN_DEPLOYER_NS, "propertyType");

   /** The property element name */
   protected static final QName propertyQName = new QName(BEAN_DEPLOYER_NS, "property");

   /** The depends binding */
   protected static final QName dependsTypeQName = new QName(BEAN_DEPLOYER_NS, "dependsType");

   /** The depends element name */
   protected static final QName dependsQName = new QName(BEAN_DEPLOYER_NS, "depends");

   /** The demand binding */
   protected static final QName demandTypeQName = new QName(BEAN_DEPLOYER_NS, "demandType");

   /** The demand element name */
   protected static final QName demandQName = new QName(BEAN_DEPLOYER_NS, "demand");

   /** The supply binding */
   protected static final QName supplyTypeQName = new QName(BEAN_DEPLOYER_NS, "supplyType");

   /** The supply element name */
   protected static final QName supplyQName = new QName(BEAN_DEPLOYER_NS, "supply");

   /** The dependency binding */
   protected static final QName dependencyTypeQName = new QName(BEAN_DEPLOYER_NS, "dependencyType");

   /** The inject element name */
   protected static final QName injectQName = new QName(BEAN_DEPLOYER_NS, "inject");

   /** The plain value binding */
   protected static final QName plainValueTypeQName = new QName(BEAN_DEPLOYER_NS, "plainValueType");

   /** The value binding */
   protected static final QName valueTypeQName = new QName(BEAN_DEPLOYER_NS, "valueType");

   /** The value element name */
   protected static final QName valueQName = new QName(BEAN_DEPLOYER_NS, "value");

   /** The null element name */
   protected static final QName nullQName = new QName(BEAN_DEPLOYER_NS, "null");

   /** The collection binding */
   protected static final QName collectionTypeQName = new QName(BEAN_DEPLOYER_NS, "collectionType");

   /** The collection element name */
   protected static final QName collectionQName = new QName(BEAN_DEPLOYER_NS, "collection");

   /** The list binding */
   protected static final QName listTypeQName = new QName(BEAN_DEPLOYER_NS, "listType");

   /** The list element name */
   protected static final QName listQName = new QName(BEAN_DEPLOYER_NS, "list");

   /** The set binding */
   protected static final QName setTypeQName = new QName(BEAN_DEPLOYER_NS, "setType");

   /** The set element name */
   protected static final QName setQName = new QName(BEAN_DEPLOYER_NS, "set");

   /** The array binding */
   protected static final QName arrayTypeQName = new QName(BEAN_DEPLOYER_NS, "arrayType");

   /** The array element name */
   protected static final QName arrayQName = new QName(BEAN_DEPLOYER_NS, "array");

   /** The map binding */
   protected static final QName mapTypeQName = new QName(BEAN_DEPLOYER_NS, "mapType");

   /** The map element name */
   protected static final QName mapQName = new QName(BEAN_DEPLOYER_NS, "map");

   /** The entry binding */
   protected static final QName entryTypeQName = new QName(BEAN_DEPLOYER_NS, "entryType");

   /** The entry element name */
   protected static final QName entryQName = new QName(BEAN_DEPLOYER_NS, "entry");

   /** The key element name */
   protected static final QName keyQName = new QName(BEAN_DEPLOYER_NS, "key");

   /** The valueGroup name */
   protected static final QName valueGroupQName = new QName(BEAN_DEPLOYER_NS, "valueGroup");

   /** The schema binding */
   protected static SchemaBinding schemaBinding;
   
   /** The value handler */
   protected static ValueMetaDataElementInterceptor VALUES = new ValueMetaDataElementInterceptor();
   
   /** The null handler */
   protected static NullValueElementInterceptor NULLVALUES = new NullValueElementInterceptor();

   public PojoServerTestBase(String localName)
   {
      super(localName);
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (schemaBinding == null)
      {
         log.debug("================ Getting Schema Binding");
         long start = System.currentTimeMillis();
         schemaBinding = getSchemaBinding();
         log.debug("================ Got Schema Binding in " + (System.currentTimeMillis() - start) + "ms");
         /** TODO assertSchemaBinding(SCHEMA); */
      }
   }

   public void configureLogging()
   {
      //enableTrace("org.jboss.xb");
   }
   
   /*
   public void testGenericBeanFactory() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment);
      assertEquals(2, deployment.getBeans().size());

      AbstractBeanMetaData genericBeanFactory = (AbstractBeanMetaData)deployment.getBeans().get(0);
      assertNotNull(genericBeanFactory);
      assertEquals("GenericBeanFactory", genericBeanFactory.getName());
      assertEquals(GenericBeanFactory.class.getName(), genericBeanFactory.getBean());
      assertNotNull(genericBeanFactory.getProperties());
      assertEquals(3, genericBeanFactory.getProperties().size());

      AbstractPropertyMetaData beanProp = null;
      AbstractPropertyMetaData ctorProp = null;
      AbstractPropertyMetaData propsProp = null;

      for(Iterator i = genericBeanFactory.getProperties().iterator(); i.hasNext();)
      {
         AbstractPropertyMetaData prop = (AbstractPropertyMetaData)i.next();
         if("bean".equals(prop.getName()))
         {
            beanProp = prop;
         }
         else if("constructor".equals(prop.getName()))
         {
            ctorProp = prop;
         }
         else if("properties".equals(prop.getName()))
         {
            propsProp = prop;
         }
         else
         {
            fail("Unexpected property: " + prop.getName());
         }
      }

      assertEquals("bean", beanProp.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", beanProp.getValue().getUnderlyingValue());

      // todo validation of property/parameter
      assertEquals("constructor", ctorProp.getName());

      assertEquals("properties", propsProp.getName());
      AbstractMapMetaData beanProps = (AbstractMapMetaData) propsProp.getValue();
      assertNotNull(beanProps);
      assertEquals(1, beanProps.size());
      Map.Entry entry = (Map.Entry) beanProps.entrySet().iterator().next();
      AbstractValueMetaData otherKey = (AbstractValueMetaData) entry.getKey();
      assertNotNull(otherKey);
      assertEquals("other", otherKey.getValue());
      AbstractValueMetaData otherValue = (AbstractValueMetaData) entry.getValue();
      assertNotNull(otherValue);
      AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)otherValue.getValue();
      assertNotNull(dependency);
      assertEquals("SimpleBean2", dependency.getValue());

      AbstractBeanMetaData simpleBean2 = (AbstractBeanMetaData)deployment.getBeans().get(1);
      assertEquals("SimpleBean2", simpleBean2.getName());
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBeanImpl", simpleBean2.getBean());
      ConstructorMetaData ctor = simpleBean2.getConstructor();
      assertNotNull(ctor);
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      AbstractParameterMetaData param = (AbstractParameterMetaData)ctor.getParameters().get(0);
      assertEquals(String.class.getName(), param.getType());
      assertEquals("Bean2", param.getValue().getUnderlyingValue());
   }
   */

   public void testAttributeDependency() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment.getBeans());
      assertEquals(2, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getProperties());
      assertEquals(1, bean.getProperties().size());
      AbstractPropertyMetaData prop = bean.getProperties().iterator().next();
      assertNotNull(prop);
      assertEquals("other", prop.getName());
      assertTrue(prop.getValue() instanceof AbstractDependencyValueMetaData);
      AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)prop.getValue();
      assertEquals("SimpleBean2", dependency.getValue());

      bean = deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getConstructor());
      ConstructorMetaData ctor = bean.getConstructor();
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      AbstractParameterMetaData param = ctor.getParameters().get(0);
      assertEquals("java.lang.String", param.getType());
      assertEquals("Bean2", param.getValue().getUnderlyingValue());
   }

   public void testAttributeDependencyDependentState() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment.getBeans());
      assertEquals(2, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getProperties());
      assertEquals(1, bean.getProperties().size());
      AbstractPropertyMetaData prop = bean.getProperties().iterator().next();
      assertNotNull(prop);
      assertEquals("other", prop.getName());
      assertTrue(prop.getValue() instanceof AbstractDependencyValueMetaData);
      AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)prop.getValue();
      assertEquals("SimpleBean2", dependency.getValue());

      bean = deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getConstructor());
      ConstructorMetaData ctor = bean.getConstructor();
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      AbstractParameterMetaData param = ctor.getParameters().get(0);
      assertEquals("java.lang.String", param.getType());
      assertEquals("Bean2", param.getValue().getUnderlyingValue());
   }

   public void testConfigure() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment.getBeans());
      assertEquals(1, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getProperties());
      assertEquals(18, bean.getProperties().size());

      for(Iterator<AbstractPropertyMetaData> i = bean.getProperties().iterator(); i.hasNext();)
      {
         AbstractPropertyMetaData prop = i.next();
         String name = prop.getName();
         Object value = prop.getValue().getUnderlyingValue();
         assertConfigureProperty(name, value);
      }
   }

   public void testConfigureNested() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment.getBeans());
      assertEquals(1, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getProperties());
      assertEquals(19, bean.getProperties().size());

      for(Iterator<AbstractPropertyMetaData> i = bean.getProperties().iterator(); i.hasNext();)
      {
         AbstractPropertyMetaData prop = i.next();
         String name = prop.getName();
         if("other".equals(name))
         {
            /** TODO AbstractBeanMetaData nested = (AbstractBeanMetaData)prop.getValue("NestedSimpleBean1");
            assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
            assertNotNull(nested);
            assertNotNull(bean.getProperties());
            assertEquals(19, bean.getProperties().size());
            for(Iterator j = nested.getProperties().iterator(); j.hasNext();)
            {
               AbstractPropertyMetaData nestedProp = (AbstractPropertyMetaData)j.next();
               String nestedName = nestedProp.getName();
               if("other".equals(nestedName))
               {
                  assertNull(nestedProp.getValue());
               }
               else
               {
                  Object value = nestedProp.getValue().getUnderlyingValue();
                  assertConfigureProperty(nestedName, value);
               }
            } */
         }
         else
         {
            Object value = prop.getValue().getUnderlyingValue();
            assertConfigureProperty(name, value);
         }
      }
   }

   public void testConstructorDependency() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment.getBeans());
      assertEquals(2, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getConstructor());
      ConstructorMetaData ctor = bean.getConstructor();
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      Object obj = ctor.getParameters().get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof AbstractParameterMetaData);
      AbstractParameterMetaData param = (AbstractParameterMetaData) obj;
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBean", param.getType());
      assertTrue(param.getValue() instanceof AbstractDependencyValueMetaData);
      AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)param.getValue();
      assertEquals("SimpleBean2", dependency.getValue());

      bean = deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBeanImpl", bean.getBean());
   }

   /*
   public void testDemandSupply() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment);
      assertNotNull(deployment.getBeans());
      assertEquals(3, deployment.getBeans().size());

      AbstractBeanMetaData bean = (AbstractBeanMetaData)deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getDemands());
      assertEquals(2, bean.getDemands().size());
      for(Iterator i = bean.getDemands().iterator(); i.hasNext();)
      {
         Object object = i.next();
         assertTrue(object instanceof AbstractDemandMetaData);
         AbstractDemandMetaData demand = (AbstractDemandMetaData) object;
         Object value = demand.getDemand();
         if(!"SimpleBean2".equals(value) && !"XYZZY".equals(value))
         {
            fail("expected SimpleBean2 or XYZZY but got " + value);
         }
      }

      bean = (AbstractBeanMetaData)deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());

      bean = (AbstractBeanMetaData)deployment.getBeans().get(2);
      assertEquals("SimpleBean3", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getSupplies());
      assertEquals(1, bean.getSupplies().size());
      AbstractSupplyMetaData supply = (AbstractSupplyMetaData)bean.getSupplies().iterator().next();
      assertEquals("XYZZY", supply.getSupply());
   }

   public void testDemandSupplyWhenRequired() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment);
      assertNotNull(deployment.getBeans());
      assertEquals(2, deployment.getBeans().size());

      AbstractBeanMetaData bean = (AbstractBeanMetaData)deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getDemands());
      assertEquals(1, bean.getDemands().size());
      Object object = bean.getDemands().iterator().next();
      assertTrue(object instanceof AbstractDemandMetaData);
      AbstractDemandMetaData demand = (AbstractDemandMetaData) object;
      assertEquals("SimpleBean2", demand.getDemand());

      bean = (AbstractBeanMetaData)deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getConstructor());
      ConstructorMetaData ctor = bean.getConstructor();
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      AbstractParameterMetaData param = (AbstractParameterMetaData)ctor.getParameters().get(0);
      assertEquals("java.lang.String", param.getType());
      assertEquals("whenRequired", param.getValue().getUnderlyingValue());
   }
   */

   public void testFactoryDependency() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment);
      assertNotNull(deployment.getBeans());
      assertEquals(3, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getConstructor());
      ConstructorMetaData ctor = bean.getConstructor();
      assertEquals("createSimpleBean", ctor.getFactoryMethod());
      assertNotNull(ctor.getFactory());
      assertEquals("SimpleBeanFactory", ctor.getFactory().getUnderlyingValue());
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      AbstractParameterMetaData param = ctor.getParameters().get(0);
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBean", param.getType());
      assertTrue(param.getValue() instanceof AbstractDependencyValueMetaData);
      AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)param.getValue();
      assertEquals("SimpleBean2", dependency.getValue());

      bean = deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());

      bean = deployment.getBeans().get(2);
      assertEquals("SimpleBeanFactory", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanFactory", bean.getBean());
   }

   /*
   public void testSimpleCollection() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment);
      assertNotNull(deployment.getBeans());
      assertEquals(1, deployment.getBeans().size());
      AbstractBeanMetaData bean = (AbstractBeanMetaData)deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());

      assertNotNull(bean.getProperties());
      assertEquals(4, bean.getProperties().size());

      for(Iterator i = bean.getProperties().iterator(); i.hasNext();)
      {
         AbstractPropertyMetaData prop = (AbstractPropertyMetaData)i.next();
         String name = prop.getName();
         if("aList".equals(name))
         {
            AbstractListMetaData list = (AbstractListMetaData) prop.getValue();
            assertNotNull(list);
            assertEquals(2, list.size());
            assertEquals("value0", ((StringValueMetaData) list.get(0)).getValue());
            assertEquals("value1", ((StringValueMetaData) list.get(1)).getValue());
         }
         else if("aSet".equals(name))
         {
            AbstractSetMetaData set = (AbstractSetMetaData) prop.getValue();
            assertNotNull(set);
            assertEquals(2, set.size());
            boolean found0 = false;
            boolean found1 = false;
            for (Iterator j = set.iterator(); j.hasNext();)
            {
               StringValueMetaData value = (StringValueMetaData) j.next();
               assertNull(value.getType());
               String stringValue = (String) value.getValue();
               if ("value0".equals(stringValue))
                  found0 = true;
               else if ("value1".equals(stringValue))
                  found1 = true;
               else
                  fail("Unexpected value " + stringValue);
            }
            assertTrue(found0);
            assertTrue(found1);
         }
         else if("aMap".equals(name))
         {
            AbstractMapMetaData map = (AbstractMapMetaData) prop.getValue();
            assertNotNull(map);
            assertEquals("testMapClass", map.getType());
            assertEquals("testKeyClass", map.getKeyType());
            assertEquals("testValueClass", map.getValueType());
            assertEquals(2, map.size());
            for (Iterator j = map.entrySet().iterator(); j.hasNext();)
            {
               Map.Entry entry = (Map.Entry) j.next();
               Object key = entry.getKey();
               assertTrue(key.getClass().getName(), key instanceof StringValueMetaData);
               StringValueMetaData keyValue = (StringValueMetaData) key;
               assertNull(keyValue.getType());
               String realKey = (String) keyValue.getValue();
               if ("nullKey".equals(realKey))
               {
                  Object value = entry.getValue();
                  assertTrue(value instanceof AbstractValueMetaData);
                  AbstractValueMetaData valueValue = (AbstractValueMetaData) value;
                  assertNull(valueValue.getValue());
               }
               else if ("other".equals(realKey))
               {
                  Object value = entry.getValue();
                  assertTrue(value instanceof AbstractDependencyValueMetaData);
                  AbstractDependencyValueMetaData valueValue = (AbstractDependencyValueMetaData) value;
                  assertEquals("SimpleBean2", valueValue.getValue());
                  assertEquals(ControllerState.INSTALLED, valueValue.getDependentState());
               }
               else
                  fail("Unexpected key " + realKey);
            }
         }
         else if("props".equals(name))
         {
            AbstractMapMetaData map = (AbstractMapMetaData) prop.getValue();
            assertNotNull(map);
            assertEquals("java.util.Properties", map.getType());
            assertEquals("java.lang.String", map.getKeyType());
            assertEquals("java.lang.String", map.getValueType());
            assertEquals(2, map.size());
            for (Iterator j = map.entrySet().iterator(); j.hasNext();)
            {
               Map.Entry entry = (Map.Entry) j.next();
               Object key = entry.getKey();
               assertTrue(key.getClass().getName(), key instanceof StringValueMetaData);
               StringValueMetaData keyValue = (StringValueMetaData) key;
               assertNull(keyValue.getType());
               String realKey = (String) keyValue.getValue();
               String expectedValue = null;
               if ("prop0".equals(realKey))
                  expectedValue = "value0";
               else if ("prop1".equals(realKey))
                  expectedValue = "value1";
               else
                  fail("Unexpected key " + realKey);
               Object value = entry.getValue();
               assertTrue(value instanceof StringValueMetaData);
               StringValueMetaData valueValue = (StringValueMetaData) value;
               assertNull(valueValue.getType());
               String realValue = (String) valueValue.getValue();
               assertEquals(expectedValue, realValue);
            }
         }
         else
         {
            fail("Unexpected property: expected aList, aSet, collection or props but got " + name);
         }
      }
   }
   */

   public void testStaticFactoryDependency() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshal();

      assertNotNull(deployment);
      assertNotNull(deployment.getBeans());
      assertEquals(2, deployment.getBeans().size());

      AbstractBeanMetaData bean = deployment.getBeans().get(0);
      assertEquals("SimpleBean1", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
      assertNotNull(bean.getConstructor());
      ConstructorMetaData ctor = bean.getConstructor();
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBeanFactory", ctor.getFactoryClass());
      assertEquals("staticCreateSimpleBean", ctor.getFactoryMethod());
      assertNotNull(ctor.getParameters());
      assertEquals(1, ctor.getParameters().size());
      AbstractParameterMetaData param = ctor.getParameters().get(0);
      assertNotNull(param);
      assertEquals("org.jboss.test.kernel.xml.support.SimpleBean", param.getType());
      assertNotNull(param.getValue());
      assertTrue(param.getValue() instanceof AbstractDependencyValueMetaData);
      AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)param.getValue();
      assertEquals("SimpleBean2", dependency.getValue());

      bean = deployment.getBeans().get(1);
      assertEquals("SimpleBean2", bean.getName());
      assertEquals("org.jboss.test.xml.pojoserver.SimpleBeanImpl", bean.getBean());
   }

   /** TODO
   protected static void assertSchemaBinding(SchemaBinding doc)
   {
      // types
      assertNotNull(doc.getType(classNameTypeQName));
      assertNotNull(doc.getType(kernelControllerStateTypeQName));

      TypeBinding deploymentType = doc.getType(deploymentTypeQName);
      assertNotNull(deploymentType);
      assertHasElement(deploymentType, beanQName, beanTypeQName);

      TypeBinding valueType = doc.getType(valueTypeQName);
      assertNotNull(valueType);
      assertHasElement(valueType, beanQName, beanTypeQName);
      assertValueGroupBinding(valueType);

      TypeBinding mixedValueType = doc.getType(mixedValueTypeQName);
      assertNotNull(mixedValueType);
      assertHasElement(mixedValueType, beanQName, beanTypeQName);
      assertValueGroupBinding(mixedValueType);

      TypeBinding namedValueType = doc.getType(namedValueTypeQName);
      assertNotNull(namedValueType);
      assertHasElement(namedValueType, beanQName, beanTypeQName);
      assertHasElement(namedValueType, annotationQName, annotationTypeQName);
      assertHasAttribute(namedValueType, nameAttrQName, Constants.QNAME_STRING);
      assertValueGroupBinding(namedValueType);

      TypeBinding valueWithClassType = doc.getType(valueWithClassTypeQName);
      assertNotNull(valueWithClassType);
      assertHasElement(valueWithClassType, beanQName, beanTypeQName);
      assertHasAttribute(valueWithClassType, classAttrQName, classNameTypeQName);
      assertValueGroupBinding(valueWithClassType);

      TypeBinding listType = doc.getType(listTypeQName);
      assertNotNull(listType);
      assertHasElement(listType, valueQName, mixedValueTypeQName);
      assertHasAttribute(listType, classAttrQName, classNameTypeQName);

      TypeBinding mapType = doc.getType(mapTypeQName);
      assertNotNull(mapType);
      assertHasElement(mapType, mapEntryQName, namedValueTypeQName);
      assertHasAttribute(mapType, classAttrQName, classNameTypeQName);

      TypeBinding propsType = doc.getType(propsTypeQName);
      assertNotNull(propsType);
      assertHasElement(propsType, propsEntryQName, null);
      TypeBinding propsEntryType = propsType.getElement(propsEntryQName).getType();
      assertHasAttribute(propsEntryType, nameAttrQName, Constants.QNAME_STRING);

      TypeBinding constructorType = doc.getType(constructorTypeQName);
      assertNotNull(constructorType);
      assertHasElement(constructorType, annotationQName, annotationTypeQName);
      assertHasElement(constructorType, factoryQName, factoryTypeQName);
      assertHasElement(constructorType, parameterQName, valueWithClassTypeQName);
      assertHasAttribute(constructorType, factoryClassAttrQName, classNameTypeQName);
      assertHasAttribute(constructorType, factoryMethodAttrQName, Constants.QNAME_TOKEN);

      TypeBinding factoryType = doc.getType(factoryTypeQName);
      assertNotNull(factoryType);
      assertHasAttribute(factoryType, beanAttrQName, Constants.QNAME_STRING);

      TypeBinding demandType = doc.getType(demandTypeQName);
      assertNotNull(demandType);
      assertHasAttribute(demandType, whenRequiredAttrQName, kernelControllerStateTypeQName);

      TypeBinding beanType = doc.getType(beanTypeQName);
      assertNotNull(beanType);
      assertHasElement(beanType, annotationQName, annotationTypeQName);
      assertHasElement(beanType, constructorQName, constructorTypeQName);
      assertHasElement(beanType, propertyQName, namedValueTypeQName);
      assertHasElement(beanType, demandQName, demandTypeQName);
      assertHasElement(beanType, supplyQName, Constants.QNAME_STRING);
      assertHasAttribute(beanType, nameAttrQName, Constants.QNAME_STRING);
      assertHasAttribute(beanType, classAttrQName, Constants.QNAME_TOKEN);

      TypeBinding annotationType = doc.getType(annotationTypeQName);
      assertNotNull(annotationType);
      assertHasElement(annotationType, attributeQName, annotationAttributeTypeQName);
      assertHasAttribute(annotationType, nameAttrQName, Constants.QNAME_STRING);

      TypeBinding annotAttrType = doc.getType(annotationAttributeTypeQName);
      assertNotNull(annotAttrType);
      assertHasAttribute(annotAttrType, nameAttrQName, Constants.QNAME_STRING);
      assertHasAttribute(annotAttrType, valueAttrQName, Constants.QNAME_STRING);

      // elements
      ElementBinding deployment = doc.getElement(deploymentQName);
      assertNotNull(deployment);
      assertNotNull(deployment.getType());
      assertEquals(deploymentTypeQName, deployment.getType().getQName());
   }

   private static void assertHasElement(TypeBinding type, QName elementQName, QName typeQName)
   {
      ElementBinding element = type.getElement(elementQName);
      assertNotNull(element);
      assertNotNull(element.getType());
      assertEquals(typeQName, element.getType().getQName());
   }

   private static void assertHasAttribute(TypeBinding type, QName attrQName, QName typeQName)
   {
      AttributeBinding attr = type.getAttribute(attrQName);
      assertNotNull(attr);
      assertNotNull(attr.getType());
      assertEquals(typeQName, attr.getType().getQName());
   }

   private static void assertValueGroupBinding(TypeBinding valueType)
   {
      assertNotNull(valueType.getElement(listQName));
      assertNotNull(valueType.getElement(setQName));
      assertNotNull(valueType.getElement(mapQName));
      assertNotNull(valueType.getElement(propsQName));
      assertNotNull(valueType.getElement(nullQName));

      ElementBinding dependency = valueType.getElement(dependencyQName);
      assertNotNull(dependency);
      TypeBinding dependencyType = dependency.getType();
      assertNotNull(dependencyType);
      assertHasAttribute(dependencyType, valueAttrQName, Constants.QNAME_STRING);
      assertHasAttribute(dependencyType, stateAttrQName, kernelControllerStateTypeQName);

      assertNotNull(valueType.getElement(parameterQName));
      assertNotNull(valueType.getElement(propertyQName));
   } */

   private void assertConfigureProperty(String name, Object value)
   {
      if("aString".equals(name))
      {
         assertEquals("StringValue", value);
      }
      else if("aByte".equals(name))
      {
         assertEquals("12", value);
      }
      else if("aBoolean".equals(name))
      {
         assertEquals("true", value);
      }
      else if("aShort".equals(name))
      {
         assertEquals("123", value);
      }
      else if("anInt".equals(name))
      {
         assertEquals("1234", value);
      }
      else if("aLong".equals(name))
      {
         assertEquals("12345", value);
      }
      else if("aFloat".equals(name))
      {
         assertEquals("3.14", value);
      }
      else if("aDouble".equals(name))
      {
         assertEquals("3.14e12", value);
      }
      else if("aDate".equals(name))
      {
         assertEquals("12/12/12", value);
      }
      else if("aBigDecimal".equals(name))
      {
         assertEquals("12e4", value);
      }
      else if("aBigInteger".equals(name))
      {
         assertEquals("123456", value);
      }
      else if("abyte".equals(name))
      {
         assertEquals("12", value);
      }
      else if("aboolean".equals(name))
      {
         assertEquals("true", value);
      }
      else if("ashort".equals(name))
      {
         assertEquals("123", value);
      }
      else if("anint".equals(name))
      {
         assertEquals("1234", value);
      }
      else if("along".equals(name))
      {
         assertEquals("12345", value);
      }
      else if("afloat".equals(name))
      {
         assertEquals("3.14", value);
      }
      else if("adouble".equals(name))
      {
         assertEquals("3.14e12", value);
      }
      else
      {
         fail("Unexpected property: " + name + "=" + value);
      }
   }

   private AbstractKernelDeployment unmarshal() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      return (AbstractKernelDeployment)unmarshaller.unmarshal(getXmlUrl("xml/pojoserver/" + getName() + ".xml"),
         schemaBinding
      );
   }

   protected abstract SchemaBinding getSchemaBinding();

   protected abstract String getXsd();

   protected SchemaBinding readXsd()
   {
      return XsdBinder.bind(getXmlUrl(getXsd()));
   }

   private static String getXmlUrl(String name)
   {
      URL xmlUrl = Thread.currentThread().getContextClassLoader().getResource(name);
      if(xmlUrl == null)
      {
         throw new IllegalStateException(name + " not found");
      }
      return xmlUrl.getFile();
   }
   
   private static class NullValueElementInterceptor extends DefaultElementInterceptor
   {
      public void add(Object parent, Object child, QName name)
      {
         if (parent instanceof AbstractCollectionMetaData)
         {
            AbstractCollectionMetaData collection = (AbstractCollectionMetaData) parent;
            collection.add(new AbstractValueMetaData());
         }
         else if (parent instanceof AbstractParameterMetaData)
         {
            AbstractParameterMetaData valueMetaData = (AbstractParameterMetaData) parent;
            valueMetaData.setValue(new AbstractValueMetaData());
         }
         else if (parent instanceof AbstractPropertyMetaData)
         {
            AbstractPropertyMetaData valueMetaData = (AbstractPropertyMetaData) parent;
            valueMetaData.setValue(new AbstractValueMetaData());
         }
         else
         {
            AbstractValueMetaData valueMetaData = (AbstractValueMetaData) parent;
            valueMetaData.setValue(new AbstractValueMetaData());
         }
      }
   }
   
   private static class ValueMetaDataElementInterceptor extends DefaultElementInterceptor
   {
      public void add(Object parent, Object child, QName name)
      {
         if (parent instanceof AbstractCollectionMetaData)
         {
            AbstractCollectionMetaData collection = (AbstractCollectionMetaData) parent;
            ValueMetaData value = (ValueMetaData) child;
            collection.add(value);
         }
         else if (parent instanceof AbstractParameterMetaData)
         {
            AbstractParameterMetaData valueMetaData = (AbstractParameterMetaData) parent;
            ValueMetaData value = (ValueMetaData) child;
            valueMetaData.setValue(value);
         }
         else if (parent instanceof AbstractPropertyMetaData)
         {
            AbstractPropertyMetaData valueMetaData = (AbstractPropertyMetaData) parent;
            ValueMetaData value = (ValueMetaData) child;
            valueMetaData.setValue(value);
         }
         else
         {
            AbstractValueMetaData valueMetaData = (AbstractValueMetaData) parent;
            ValueMetaData value = (ValueMetaData) child;
            valueMetaData.setValue(value);
         }
      }
   }
}
