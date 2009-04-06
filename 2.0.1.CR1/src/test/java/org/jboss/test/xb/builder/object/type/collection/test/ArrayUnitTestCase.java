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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.collection.support.RootArray;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * ArrayUnitTestCase
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ArrayUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(ArrayUnitTestCase.class);
   }

   public ArrayUnitTestCase(String name)
   {
      super(name);
   }

   @SuppressWarnings("unchecked")
   public void testUnmarshal() throws Exception
   {
      RootArray result = unmarshalObject(RootArray.class);
      String[] array = result.getArray();
      assertNotNull(array);
      assertEquals(new String[] { "ONE", "TWO", "THREE" }, array);
   }

   public void testSimpleBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(RootArray.class);
      assertNotNull(schemaBinding);
      
      QName elementName = new QName(XMLConstants.NULL_NS_URI, "root-array");
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      
      // TODO
      //ModelGroupBinding group = (ModelGroupBinding) elementBinding.getType().getParticle().getTerm();
      //assertRepeatableElementBinding(group);
   }

   protected void assertRepeatableElementBinding(ModelGroupBinding group)
   {
      assertEquals(1, group.getParticles().size());
      ParticleBinding p = group.getParticles().iterator().next();
      assertTrue(p.getTerm().isElement());
      assertTrue(p.getMaxOccursUnbounded());
   }
}
