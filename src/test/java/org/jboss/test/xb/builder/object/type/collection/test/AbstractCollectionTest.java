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
package org.jboss.test.xb.builder.object.type.collection.test;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.collection.support.Root;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * AbstractCollectionTest
 *
 * @param <T> the test class
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractCollectionTest<T extends Root<Collection<String>>> extends AbstractBuilderTest
{
   /** The root class */
   private Class<T> root;

   /** The expected value */
   private ArrayList<String> expected;
   
   public AbstractCollectionTest(String name, Class<T> root)
   {
      super(name);
      this.root = root;
      this.expected = new ArrayList<String>();
      expected.add("ONE");
      expected.add("TWO");
      expected.add("THREE");
   }

   @SuppressWarnings("unchecked")
   public void testUnmarshal() throws Exception
   {
      T result = unmarshalObject(root);
      Collection<String> collection = result.getCollection();
      Collection<String> actual = new ArrayList<String>(collection);
      assertEquals(expected, actual);
   }

   public void testSimpleBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(root);
      assertNotNull(schemaBinding);
      
      QName elementName = new QName(XMLConstants.NULL_NS_URI, JBossXBBuilder.generateXMLNameFromJavaName(root.getSimpleName(), true, true));
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      
      // TODO check the collection model
   }
}
