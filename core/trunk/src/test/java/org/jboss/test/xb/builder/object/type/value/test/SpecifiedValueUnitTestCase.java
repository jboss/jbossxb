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

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.value.support.SpecifiedValue;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * SpecifiedValueUnitTestCase.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class SpecifiedValueUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(SpecifiedValueUnitTestCase.class);
   }

   public SpecifiedValueUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshal() throws Exception
   {
      SpecifiedValue result = unmarshalObject(SpecifiedValue.class);
      Object actual = result.getNotValue();
      assertEquals("Hello", actual);
   }

   public void testValueBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(SpecifiedValue.class);
      assertNotNull(schemaBinding);
      
      // TODO check the model
   }
}
