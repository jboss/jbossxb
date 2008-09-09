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
package org.jboss.test.xml;

import junit.framework.Test;

import org.jboss.test.xml.initializer.Container;
import org.jboss.test.xml.initializer.ContainerInitializer;
import org.jboss.test.xml.initializer.Simple;
import org.jboss.test.xml.initializer.SimpleInitializer;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;

/**
 * SchemaBindingInitializerUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 40498 $
 */
public class SchemaBindingInitializerUnitTestCase extends AbstractJBossXBTest
{
   public static SchemaBindingResolver initResolver() throws Exception
   {
      Class<?> clazz = SchemaBindingInitializerUnitTestCase.class;
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.addSchemaInitializer(SimpleInitializer.NS, SimpleInitializer.class.getName());
      String location = getSchemaLocation(clazz, "SchemaBindingInitializerUnitTestCaseSimple.xsd");
      resolver.addSchemaLocation(SimpleInitializer.NS, location);
      resolver.addSchemaInitializer(ContainerInitializer.NS, ContainerInitializer.class.getName());
      location = getSchemaLocation(clazz, "SchemaBindingInitializerUnitTestCaseContainer.xsd");
      resolver.addSchemaLocation(ContainerInitializer.NS, location);
      return resolver;
   }

   public void testSimple() throws Exception
   {
      Simple simple = (Simple) unmarshal(rootName + "Simple.xml", Simple.class);
      assertEquals("SimpleTest", simple.getValue());
   }
   
   public void testContainer() throws Exception
   {
      Container container = (Container) unmarshal(rootName + "Container.xml", Container.class);
      assertNull(container.getValue());
   }
   
   public void testContainerStrictSimple() throws Exception
   {
      Container container = (Container) unmarshal(rootName + "ContainerStrictSimple.xml", Container.class);
      Object value = container.getValue();
      assertNotNull("Should have a value", value);
      assertTrue("Should be a simple", value instanceof Simple);
      Simple simple = (Simple) value;
      assertEquals("ContainerStrict", simple.getValue());
   }
   
   public void testContainerStrictNotSimple() throws Exception
   {
      try
      {
         unmarshal(rootName + "ContainerStrictNotSimple.xml");
         fail("Should not be here!");
      }
      catch (Exception e)
      {
         checkThrowable(JBossXBException.class, e);
      }
   }
   
   public void testContainerLaxSimple() throws Exception
   {
      Container container = (Container) unmarshal(rootName + "ContainerLaxSimple.xml", Container.class);
      Object value = container.getValue();
      assertNotNull("Should have a value", value);
      assertTrue("Should be a simple", value instanceof Simple);
      Simple simple = (Simple) value;
      assertEquals("ContainerLax", simple.getValue());
   }
   
   public void testContainerLaxNotSimple() throws Exception
   {
      Container container = (Container) unmarshal(rootName + "ContainerLaxNotSimple.xml", Container.class);
      Object value = container.getValue();
      assertNull("Should NOT have a value", value);
   }
   
   public void testContainerSkipSimple() throws Exception
   {
      Container container = (Container) unmarshal(rootName + "ContainerSkipSimple.xml", Container.class);
      Object value = container.getValue();
      assertNull("Should NOT have a value", value);
   }
   
   public void testContainerSkipNotSimple() throws Exception
   {
      Container container = (Container) unmarshal(rootName + "ContainerSkipNotSimple.xml", Container.class);
      Object value = container.getValue();
      assertNull("Should NOT have a value", value);
   }

   /**
    * Setup the test
    * 
    * @return the test
    */
   public static Test suite()
   {
      return suite(SchemaBindingInitializerUnitTestCase.class);
   }
   
   /**
    * Create a new SchemaBindingInitializerUnitTestCase.
    * 
    * @param name the test name
    */
   public SchemaBindingInitializerUnitTestCase(String name)
   {
      super(name);
   }
}
