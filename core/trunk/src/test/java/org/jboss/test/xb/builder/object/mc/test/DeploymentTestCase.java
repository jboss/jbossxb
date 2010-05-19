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
package org.jboss.test.xb.builder.object.mc.test;

import java.util.List;
import java.util.Set;

import junit.framework.Test;

import org.jboss.test.xb.builder.object.mc.support.MyObject;
import org.jboss.test.xb.builder.object.mc.support.TestBeanMetaDataFactory;
import org.jboss.test.xb.builder.object.mc.support.TestBeanMetaDataFactory1;
import org.jboss.test.xb.builder.object.mc.support.TestBeanMetaDataFactory2;
import org.jboss.test.xb.builder.object.mc.support.model.AbstractArrayMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.AbstractKernelDeployment;
import org.jboss.test.xb.builder.object.mc.support.model.AbstractValueMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.BeanMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.BeanMetaDataFactory;
import org.jboss.test.xb.builder.object.mc.support.model.PropertyMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.ValueMetaData;

/**
 * DeploymentTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 56476 $
 */
public class DeploymentTestCase extends AbstractMCTest
{
   public void testDeployment() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertNull(deployment.getName());
      assertNull(deployment.getClassLoader());
      assertNull(deployment.getBeans());
   }

   public void testDeploymentWithName() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      assertNull(deployment.getBeans());
   }

   public void testDeploymentWithClassLoader() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNotNull(deployment.getClassLoader());
      assertNull(deployment.getBeans());
   }

   public void testDeploymentWithBean() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(1, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getName());
      assertEquals(Object.class.getName(), bean.getBean());
   }

   public void testDeploymentWithMultipleBeans() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(3, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getName());
      assertEquals(Object.class.getName(), bean.getBean());
      bean = (BeanMetaData) beans.get(1);
      assertNotNull(bean);
      assertEquals("Bean2", bean.getName());
      assertEquals(Object.class.getName(), bean.getBean());
      bean = (BeanMetaData) beans.get(2);
      assertNotNull(bean);
      assertEquals("Bean3", bean.getName());
      assertEquals(Object.class.getName(), bean.getBean());
   }

   public void testDeploymentWithBeanFactory() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(1, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getName());
      assertEquals("GenericBeanFactory", bean.getBean());
   }

   public void testDeploymentWithMultipleBeanFactorys() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(3, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getName());
      assertEquals("GenericBeanFactory", bean.getBean());
      bean = (BeanMetaData) beans.get(1);
      assertNotNull(bean);
      assertEquals("Bean2", bean.getName());
      assertEquals("GenericBeanFactory", bean.getBean());
      bean = (BeanMetaData) beans.get(2);
      assertNotNull(bean);
      assertEquals("Bean3", bean.getName());
      assertEquals("GenericBeanFactory", bean.getBean());
   }

   public void testDeploymentWithMultipleBeanMetaDataFactorys() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beanFactories = deployment.getBeanFactories();
      assertNotNull(beanFactories);
      assertEquals(3, beanFactories.size());
      BeanMetaDataFactory factory = (BeanMetaDataFactory) beanFactories.get(0);
      assertEquals(TestBeanMetaDataFactory.class.getName(), factory.getClass().getName());
      factory = (BeanMetaDataFactory) beanFactories.get(1);
      assertEquals(TestBeanMetaDataFactory1.class.getName(), factory.getClass().getName());
      factory = (BeanMetaDataFactory) beanFactories.get(2);
      assertEquals(TestBeanMetaDataFactory2.class.getName(), factory.getClass().getName());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(6, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getBean());
      bean = (BeanMetaData) beans.get(1);
      assertNotNull(bean);
      assertEquals("Bean2", bean.getBean());
      bean = (BeanMetaData) beans.get(2);
      assertNotNull(bean);
      assertEquals("Bean3", bean.getBean());
      bean = (BeanMetaData) beans.get(3);
      assertNotNull(bean);
      assertEquals("Bean4", bean.getBean());
      bean = (BeanMetaData) beans.get(4);
      assertNotNull(bean);
      assertEquals("Bean5", bean.getBean());
      bean = (BeanMetaData) beans.get(5);
      assertNotNull(bean);
      assertEquals("Bean6", bean.getBean());
   }

   public void testDeploymentWithBeanMetaDataFactory() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beanFactories = deployment.getBeanFactories();
      assertNotNull(beanFactories);
      assertEquals(1, beanFactories.size());
      BeanMetaDataFactory factory = (BeanMetaDataFactory) beanFactories.get(0);
      assertEquals(TestBeanMetaDataFactory.class.getName(), factory.getClass().getName());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(2, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getBean());
      bean = (BeanMetaData) beans.get(1);
      assertNotNull(bean);
      assertEquals("Bean2", bean.getBean());
   }

   public void testDeploymentWithMixed() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      assertEquals("SimpleDeployment", deployment.getName());
      assertNull(deployment.getClassLoader());
      List<?> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(8, beans.size());
      BeanMetaData bean = (BeanMetaData) beans.get(0);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getName());
      assertEquals(Object.class.getName(), bean.getBean());
      bean = (BeanMetaData) beans.get(1);
      assertNotNull(bean);
      assertEquals("Bean2", bean.getName());
      assertEquals("GenericBeanFactory", bean.getBean());
      bean = (BeanMetaData) beans.get(2);
      assertNotNull(bean);
      assertEquals("Bean1", bean.getBean());
      bean = (BeanMetaData) beans.get(3);
      assertNotNull(bean);
      assertEquals("Bean2", bean.getBean());
      bean = (BeanMetaData) beans.get(4);
      assertNotNull(bean);
      assertEquals("Bean3", bean.getName());
      assertEquals(Object.class.getName(), bean.getBean());
      bean = (BeanMetaData) beans.get(5);
      assertNotNull(bean);
      assertEquals("Bean4", bean.getName());
      assertEquals("GenericBeanFactory", bean.getBean());
      bean = (BeanMetaData) beans.get(6);
      assertNotNull(bean);
      assertEquals("Bean3", bean.getBean());
      bean = (BeanMetaData) beans.get(7);
      assertNotNull(bean);
      assertEquals("Bean4", bean.getBean());
   }

   public void testDeploymentWithArrayOfJavaBeans() throws Exception
   {
      AbstractKernelDeployment deployment = unmarshalDeployment();
      List<BeanMetaData> beans = deployment.getBeans();
      assertNotNull(beans);
      assertEquals(1, beans.size());
      BeanMetaData beanMetaData = beans.get(0);
      assertNotNull(beanMetaData);
      Set<PropertyMetaData> properties = beanMetaData.getProperties();
      assertNotNull(properties);
      assertEquals(1, properties.size());
      PropertyMetaData property = properties.iterator().next();
      assertNotNull(property);
      ValueMetaData value = property.getValue();
      assertNotNull(value);
      assertTrue(value instanceof AbstractArrayMetaData);
      AbstractArrayMetaData array = (AbstractArrayMetaData) value;
      assertEquals(4, array.size());
      String keys[] = new String[]{"object1", "object2", "object2", "object1"};
      for(int i = 0; i < keys.length; ++i)
      {
         Object o = array.get(i);
         assertNotNull(o);
         assertTrue(o instanceof AbstractValueMetaData);
         AbstractValueMetaData v = (AbstractValueMetaData) o;
         Object uv = v.getUnderlyingValue();
         assertNotNull(uv);
         assertTrue(uv instanceof MyObject);
         MyObject mo = (MyObject) uv;
         assertEquals(keys[i], mo.getKey());
      }
   }
   
   public static Test suite()
   {
      return suite(DeploymentTestCase.class);
   }

   public DeploymentTestCase(String name)
   {
      super(name);
   }
}
