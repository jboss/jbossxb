/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.test.xb.builder.object.element.javatypeadapter.test;

import java.util.Map;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.javatypeadapter.support.Root;


/**
 * A JavaTypeAdapterUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JavaTypeAdapterUnitTestCase extends AbstractBuilderTest
{
   public JavaTypeAdapterUnitTestCase(String name)
   {
      super(name);
   }

   @SuppressWarnings("unchecked")
   public void testMap() throws Exception
   {
      Root root = unmarshalObject(Root.class);
      Map<Integer, String> map = root.getMap();
      assertNotNull(map);
      assertEquals(3, map.size());
      assertEquals("value1", map.get(1));
      assertEquals("value22", map.get(22));
      assertEquals("value333", map.get(333));
   }
}
