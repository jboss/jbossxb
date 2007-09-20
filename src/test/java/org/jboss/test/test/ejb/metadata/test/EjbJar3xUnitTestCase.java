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
package org.jboss.test.ejb.metadata.test;


import junit.framework.Test;

import org.jboss.ejb.metadata.spec.EjbJar30MetaData;
import org.jboss.ejb.metadata.spec.EjbJar3xMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeansMetaData;
import org.jboss.javaee.annotation.Description;
import org.jboss.javaee.annotation.Descriptions;
import org.jboss.javaee.annotation.DisplayName;
import org.jboss.javaee.annotation.DisplayNames;
import org.jboss.javaee.annotation.Icon;
import org.jboss.javaee.annotation.Icons;
import org.jboss.javaee.metadata.spec.DescriptionGroupMetaData;
import org.jboss.javaee.metadata.spec.DescriptionImpl;
import org.jboss.javaee.metadata.spec.DisplayNameImpl;
import org.jboss.javaee.metadata.spec.IconImpl;
//import org.jboss.metadata.ApplicationMetaData;
//import org.jboss.metadata.BeanMetaData;
import org.jboss.test.javaee.metadata.AbstractJavaEEMetaDataTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;

/**
 * EjbJar3xUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class EjbJar3xUnitTestCase extends AbstractJavaEEMetaDataTest
{
   public static Test suite()
   {
      return suite(EjbJar3xUnitTestCase.class);
   }
   
   public static SchemaBindingResolver initResolver()
   {
      return schemaResolverForClass(EjbJar30MetaData.class);
      //return AbstractJavaEEMetaDataTest.initResolverJavaEE(EjbJar30MetaData.class);
   }
   
   public EjbJar3xUnitTestCase(String name)
   {
      super(name);
   }
   
   protected EjbJar3xMetaData unmarshal() throws Exception
   {
      return unmarshal(EjbJar30MetaData.class);
   }

   public void testId() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      assertEquals("ejb-jar-test-id", result.getId());
   }
   
   public void testVersion() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      assertEquals("3.0", result.getVersion());
      assertFalse(result.isEJB1x());
      assertFalse(result.isEJB2x());
      assertFalse(result.isEJB21());
      assertTrue(result.isEJB3x());
      
/*      ApplicationMetaData old = new ApplicationMetaData(result);
      assertFalse(old.isEJB1x());
      assertFalse(old.isEJB2x());
      assertFalse(old.isEJB21());
      assertTrue(old.isEJB3x());
*/   }
   
   public void testDescriptionDefaultLanguage() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      DescriptionGroupMetaData group = result.getDescriptionGroup();
      assertNotNull(group);
      Descriptions descriptions = group.getDescriptions();
      assertNotNull(descriptions);
      
      DescriptionImpl hello = new DescriptionImpl();
      hello.setDescription("Hello");
      assertEquals(new Description[] { hello }, descriptions.value());
   }
   
   public void testDisplayNameDefaultLanguage() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      DescriptionGroupMetaData group = result.getDescriptionGroup();
      assertNotNull(group);
      DisplayNames displayNames = group.getDisplayNames();
      assertNotNull(displayNames);
      
      DisplayNameImpl hello = new DisplayNameImpl();
      hello.setDisplayName("Hello");
      assertEquals(new DisplayName[] { hello }, displayNames.value());
   }
   
   public void testIconDefaultLanguage() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      DescriptionGroupMetaData group = result.getDescriptionGroup();
      assertNotNull(group);
      Icons icons = group.getIcons();
      assertNotNull(icons);
      
      IconImpl icon = new IconImpl();
      icon.setSmallIcon("small");
      icon.setLargeIcon("large");
      assertEquals(new Icon[] { icon }, icons.value());
   }
   
   public void testEjbClientJar() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      assertEquals("some/path/client.jar", result.getEjbClientJar());
   }
   
   public void testEnterpriseBeans() throws Exception
   {
      EjbJar3xMetaData result = unmarshal();
      EnterpriseBeansMetaData beans = result.getEnterpriseBeans();
      assertNotNull(beans);
      
      assertEquals(1, beans.size());
      EnterpriseBeanMetaData bean = beans.iterator().next();
      assertEquals("TestBean", bean.getEjbName());
      
/*      ApplicationMetaData old = new ApplicationMetaData(result);
      Iterator<BeanMetaData> iterator = old.getEnterpriseBeans();
      assertTrue(iterator.hasNext());
      BeanMetaData beanMetaData = iterator.next();
      assertEquals("TestBean", beanMetaData.getEjbName());
      assertFalse(iterator.hasNext());
*/   }
}
