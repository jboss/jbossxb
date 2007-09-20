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
package org.jboss.test.xb.builder.object.type.simple.test;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * AbstractSimpleTest.
 *
 * @param <T> the simple type
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractSimpleTest<T> extends AbstractBuilderTest
{
   /** The root class */
   private Class<T> root;

   /** The expected value */
   private T expected;
   
   public AbstractSimpleTest(String name, Class<T> root, T expected)
   {
      super(name);
      this.root = root;
      this.expected = expected;
   }

   public void testUnmarshal() throws Exception
   {
      T result = unmarshalObject(root);
      assertEquals(expected, result);
   }

   public void testSimpleBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(root);
      assertNotNull(schemaBinding);

      QName qName = SimpleTypeBindings.typeQName(root);
      assertNotNull(qName);
      TypeBinding expectedTypeBinding = schemaBinding.getType(qName);
      
      QName elementName = new QName(XMLConstants.NULL_NS_URI, JBossXBBuilder.generateXMLNameFromJavaName(root.getSimpleName(), true, true));
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      assertTrue(expectedTypeBinding == elementBinding.getType());
   }
}
