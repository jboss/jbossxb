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
package org.jboss.test.xb.builder.object.attribute.test;

import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.object.attribute.support.IntegerListAttribute;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * IntegerListUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class IntegerListUnitTestCase extends AbstractAttributeTest<List<Integer>>
{
   public static Test suite()
   {
      return suite(IntegerListUnitTestCase.class);
   }
   
   public IntegerListUnitTestCase(String name)
   {
      super(name, IntegerListAttribute.class, Arrays.asList(new Integer[]{new Integer(3), new Integer(2), new Integer(4)}));
   }
   
   @Override
   public void testSimpleAttribute() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(root);
      assertNotNull(schemaBinding);

      QName qName = SimpleTypeBindings.typeQName(Integer.class);
      assertNotNull(qName);
      TypeBinding expectedItemType = schemaBinding.getType(qName);
      
      QName elementName = new QName(XMLConstants.NULL_NS_URI, JBossXBBuilder.generateXMLNameFromJavaName(root.getSimpleName(), true, true));
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      TypeBinding typeBinding = elementBinding.getType();
      assertNotNull(typeBinding);
      QName attributeName = new QName(XMLConstants.NULL_NS_URI, "attribute");
      AttributeBinding attribute = typeBinding.getAttribute(attributeName);
      assertNotNull(attribute);
      TypeBinding attributeType = attribute.getType();
      TypeBinding itemType = attributeType.getItemType();
      assertNotNull(itemType);
      assertTrue("Expected " + expectedItemType + " got " + itemType, expectedItemType == itemType);
   }
}
