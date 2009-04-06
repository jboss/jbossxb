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
package org.jboss.test.xb.builder.object.type.xmlenum.test;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlenum.support.nondefaults.Root;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.builder.JBossXBBuilder;
import org.jboss.xb.builder.runtime.EnumValueAdapter;

/**
 * NonDefaultsUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class NonDefaultsUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(NonDefaultsUnitTestCase.class);
   }
   
   public NonDefaultsUnitTestCase(String name)
   {
      super(name);
   }

   public void testNonDefaultsUnmarshal() throws Exception
   {
      unmarshalObject(Root.class);
   }

   public void testNonDefaultsTypeBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(Root.class);
      assertNotNull(schemaBinding);
      
      // Check the type
      QName qName = new QName("testNamespace", "nondefaults");
      TypeBinding type = schemaBinding.getType(qName);
      assertNull(type);
      
      QName elementName = new QName(XMLConstants.NULL_NS_URI, "root");
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      TypeBinding typeBinding = elementBinding.getType();
      
      ValueAdapter valueAdapter = typeBinding.getValueAdapter();
      assertNotNull(valueAdapter);
      assertTrue(valueAdapter instanceof EnumValueAdapter);
      EnumValueAdapter enumValueAdapter = (EnumValueAdapter) valueAdapter;

      HashMap<Object, Object> expected = new HashMap<Object, Object>();
      expected.put(new Integer(1), Root.ONE);
      expected.put(new Integer(2), Root.TWO);
      expected.put(new Integer(3), Root.THREE);

      Map<Object, Object> actual = enumValueAdapter.getMapping();
      assertEquals(expected, actual);
   }
}
