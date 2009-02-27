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

import org.jboss.test.xml.initializer.Simple;
import org.jboss.test.xml.initializer.SimpleInitializer;
import org.jboss.test.xml.pojoserver.metadata.AbstractValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.PropertyInitializer;
import org.jboss.test.xml.pojoserver.metadata.PropertyMetaData;
import org.jboss.test.xml.pojoserver.metadata.StringValueMetaData;
import org.jboss.test.xml.pojoserver.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.MultiClassSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;

/**
 * WildcardWrapperUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 40741 $
 */
public class WildcardWrapperUnitTestCase extends AbstractJBossXBTest
{
   public static SchemaBindingResolver initResolver() throws Exception
   {
      Class<?> clazz = WildcardWrapperUnitTestCase.class;
      MultiClassSchemaResolver resolver = new MultiClassSchemaResolver();
      resolver.addSchemaInitializer(PropertyInitializer.NS, PropertyInitializer.class.getName());
      String location = getSchemaLocation(clazz, "WildcardWrapperUnitTestCase.xsd");
      resolver.addSchemaLocation(PropertyInitializer.NS, location);
      resolver.addSchemaInitializer(SimpleInitializer.NS, SimpleInitializer.class.getName());
      location = getSchemaLocation(clazz, "SchemaBindingInitializerUnitTestCaseSimple.xsd");
      resolver.addSchemaLocation(SimpleInitializer.NS, location);
      return resolver;
   }

   public void testSimple() throws Exception
   {
      PropertyMetaData property = (PropertyMetaData) unmarshal(rootName + "Simple.xml", PropertyMetaData.class);
      ValueMetaData value = property.getValue();
      assertNotNull("Should have a value", value);
      assertTrue(value instanceof StringValueMetaData);
      StringValueMetaData stringValue = (StringValueMetaData) value;
      assertEquals("testSimple", stringValue.getValue());
   }

   public void testWildcard() throws Exception
   {
      PropertyMetaData property = (PropertyMetaData) unmarshal(rootName + "Wildcard.xml", PropertyMetaData.class);
      ValueMetaData value = property.getValue();
      assertNotNull("Should have a value", value);
      assertTrue(value instanceof AbstractValueMetaData);
      AbstractValueMetaData abstractValue = (AbstractValueMetaData) value;
      Object anyValue = abstractValue.getValue();
      assertNotNull("AbstractValue should have a value", anyValue);
      assertTrue(anyValue instanceof Simple);
      Simple simple = (Simple) anyValue;
      assertEquals("testWildcard", simple.getValue());
   }

   /**
    * Setup the test
    * 
    * @return the test
    */
   public static Test suite()
   {
      return suite(WildcardWrapperUnitTestCase.class);
   }

   /**
    * Create a new WildcardWrapperUnitTestCase.
    * 
    * @param name the test name
    */
   public WildcardWrapperUnitTestCase(String name)
   {
      super(name);
   }
}
