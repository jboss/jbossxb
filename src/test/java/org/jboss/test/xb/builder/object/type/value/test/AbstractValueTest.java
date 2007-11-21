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
package org.jboss.test.xb.builder.object.type.value.test;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.value.support.SimpleValue;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * AllUnitTestCase.
 *
 * @param <T> the simple value type
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractValueTest<T extends SimpleValue> extends AbstractBuilderTest
{
   /** The root class */
   private Class<T> root;

   /** The expected value */
   private Object expected;
   
   private String valueElement;
   
   public AbstractValueTest(String name, Class<T> root, Object expected, String valueElement)
   {
      super(name);
      this.root = root;
      this.expected = expected;
      this.valueElement = valueElement;
   }

   public void testUnmarshal() throws Exception
   {
      T result = unmarshalObject(root);
      Object actual = result.getValue();
      assertEquals(expected, actual);
   }

   public void testValueBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(root);
      assertNotNull(schemaBinding);
      
      ElementBinding element = schemaBinding.getElement(new QName(valueElement));
      assertNotNull(element);
      assertNotNull(element.getType().getSimpleType());
   }
}
