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
package org.jboss.test.xb.builder.object.type.xmlenum.test;

import java.util.List;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlenum.support.CollectionOfEnum;
import org.jboss.test.xb.builder.object.type.xmlenum.support.EnumGlobalType;


/**
 * A CollectionOfEnumUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class CollectionOfEnumUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(CollectionOfEnumUnitTestCase.class);
   }
   
   public CollectionOfEnumUnitTestCase(String name)
   {
      super(name);
   }
   
   public void testMain() throws Exception
   {
      CollectionOfEnum colOfEnum = unmarshalObject(CollectionOfEnum.class);
      List<EnumGlobalType> enums = colOfEnum.getEnums();
      assertNotNull(enums);
      assertEquals(6, enums.size());
      assertEquals(EnumGlobalType.ONE, enums.get(0));
      assertEquals(EnumGlobalType.TWO, enums.get(1));
      assertEquals(EnumGlobalType.TWO, enums.get(2));
      assertEquals(EnumGlobalType.THREE, enums.get(3));
      assertEquals(EnumGlobalType.THREE, enums.get(4));
      assertEquals(EnumGlobalType.THREE, enums.get(5));
   }
}
