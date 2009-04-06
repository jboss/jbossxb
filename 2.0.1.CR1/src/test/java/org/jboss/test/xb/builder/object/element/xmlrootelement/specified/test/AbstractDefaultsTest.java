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
package org.jboss.test.xb.builder.object.element.xmlrootelement.specified.test;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * SimpleObjectWildcardUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractDefaultsTest extends AbstractBuilderTest
{
   /** The root class */
   private Class<?> root;
   
   public AbstractDefaultsTest(String name, Class<?> root)
   {
      super(name);
      this.root = root;
   }

   public void testDefaultsUnmarshal() throws Exception
   {
      unmarshalObject(root);
   }

   public void testDefaultsElementBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(root);
      testDefaults(schemaBinding);
   }
   
   protected void testDefaults(SchemaBinding schemaBinding)
   {
      assertNotNull(schemaBinding);
      
      // Check the element
      QName qName = new QName(XMLConstants.NULL_NS_URI, "root");
      ElementBinding elementBinding = schemaBinding.getElement(qName);
      assertNotNull(elementBinding);
      assertEquals(qName, elementBinding.getQName());
   }
}
