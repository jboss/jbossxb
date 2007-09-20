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

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.builder.JBossXBBuilder;
import org.jboss.xb.builder.runtime.EnumValueAdapter;

/**
 * AbstractDefaultsTest.
 *
 * @param <T> the enumeration type
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractDefaultsTest<T extends Enum<T>> extends AbstractBuilderTest
{
   /** The root class */
   private Class<T> root;
   
   /** The expected mapping */
   private Map<Object, Object> expected = new HashMap<Object, Object>();
   
   public AbstractDefaultsTest(String name, Class<T> root)
   {
      super(name);
      this.root = root;
      
      T[] constants = root.getEnumConstants();
      for (int i = 0; i < constants.length; ++i)
         expected.put(constants[i].name(), constants[i]);
   }

   public void testDefaultsUnmarshal() throws Exception
   {
      T result = unmarshalObject(root);
      assertTrue(expected.get("ONE") == result);
   }

   public void testDefaultsEnumBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(root);
      testDefaults(schemaBinding);
   }
   
   protected void testDefaults(SchemaBinding schemaBinding)
   {
      assertNotNull(schemaBinding);
      
      // Check the type
      QName qName = new QName(XMLConstants.NULL_NS_URI, "root");
      QName elementName = qName;
      TypeBinding type = schemaBinding.getType(qName);
      assertNull(type);
      
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      TypeBinding typeBinding = elementBinding.getType();
      
      ValueAdapter valueAdapter = typeBinding.getValueAdapter();
      assertNotNull(valueAdapter);
      assertTrue(valueAdapter instanceof EnumValueAdapter);
      EnumValueAdapter enumValueAdapter = (EnumValueAdapter) valueAdapter;
      
      Map<Object, Object> actual = enumValueAdapter.getMapping();
      assertEquals(expected, actual);
   }
}
