/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xb.builder.object.element.xmlelements.test;


import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.xmlelements.support.Foo4;

/**
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class XmlElementsUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(XmlElementsUnitTestCase.class);
   }
   
   public XmlElementsUnitTestCase(String name)
   {
      super(name);
   }

   public void testArray() throws Exception
   {
      Foo4 foo = unmarshalObject(Foo4.class);
      Number[] items = foo.getItems();
      assertEquals(4, items.length);
      assertEquals(1, items[0]);
      assertEquals(2, items[1]);
      assertEquals(new Float(1.1), items[2]);
   }
}
