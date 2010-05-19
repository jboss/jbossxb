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

import org.jboss.test.xb.builder.object.type.xmlenum.support.matchcase.Root;
import org.jboss.xb.builder.runtime.EnumValueAdapter;
import junit.framework.Test;

/**
 * Match case test case enum.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class MatchCaseUnitTestCase extends AbstractDefaultsTest<Root>
{
   public MatchCaseUnitTestCase(String name)
   {
      super(name, Root.class);
   }

   public static Test suite()
   {
      return suite(MatchCaseUnitTestCase.class);
   }

   protected void fail(EnumValueAdapter enumValueAdapter, String key)
   {
      try
      {
         enumValueAdapter.cast(key, Root.class);
         fail("Should not be here.");
      }
      catch(Exception e)
      {
         assertInstanceOf(e, RuntimeException.class);
      }
   }

   protected void testEnumValueAdapter(EnumValueAdapter enumValueAdapter)
   {
      super.testEnumValueAdapter(enumValueAdapter);

      fail(enumValueAdapter, "one");
      fail(enumValueAdapter, "One");
      assertEquals(Root.ONE, enumValueAdapter.cast("ONE", Root.class));

      fail(enumValueAdapter, "two");
      fail(enumValueAdapter, "Two");
      assertEquals(Root.TWO, enumValueAdapter.cast("TWO", Root.class));

      fail(enumValueAdapter, "three");
      fail(enumValueAdapter, "Three");
      assertEquals(Root.THREE, enumValueAdapter.cast("THREE", Root.class));
   }
}
