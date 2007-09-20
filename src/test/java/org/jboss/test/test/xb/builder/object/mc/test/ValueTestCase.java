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

import java.util.Set;

import junit.framework.Test;

import org.jboss.test.xb.builder.object.mc.support.model.AbstractBeanMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.PropertyMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.StringValueMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.ValueMetaData;

/**
 * ValueTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 40781 $
 */
public class ValueTestCase extends AbstractMCTest
{
   protected StringValueMetaData getValue() throws Exception
   {
      AbstractBeanMetaData bean = unmarshalBean();
      Set properties = bean.getProperties();
      assertNotNull(properties);
      assertEquals(1, properties.size());
      PropertyMetaData property = (PropertyMetaData) properties.iterator().next();
      assertNotNull(property);
      ValueMetaData value = property.getValue();
      assertNotNull(property);
      assertTrue(value instanceof StringValueMetaData);
      return (StringValueMetaData) value;
   }
   
   public void testValue() throws Exception
   {
      StringValueMetaData value = getValue();
      assertNull(value.getType());
      assertNull(value.getUnderlyingValue());
   }
   
   public void testValueWithClass() throws Exception
   {
      StringValueMetaData value = getValue();
      assertEquals("ValueClass", value.getType());
      assertNull(value.getUnderlyingValue());
   }
   
   public void testValueWithValue() throws Exception
   {
      StringValueMetaData value = getValue();
      assertNull(value.getType());
      assertEquals("Value", value.getUnderlyingValue());
   }
   
   public static Test suite()
   {
      return suite(ValueTestCase.class);
   }

   public ValueTestCase(String name)
   {
      super(name);
   }
}
