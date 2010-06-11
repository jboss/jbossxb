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
package org.jboss.test.xb.builder.object.element.wrapper.test;

import java.util.List;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.wrapper.support.Bar;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo2;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo3;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo4;
import org.jboss.test.xb.builder.object.element.wrapper.support.MyNumber;

/**
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class WrapperUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(WrapperUnitTestCase.class);
   }
   
   public WrapperUnitTestCase(String name)
   {
      super(name);
   }

   public void testFooWrapper()
      throws Exception
   {
      //enableTrace("org.jboss.xb");
      Foo foo = unmarshalObject(Foo.class);
      List<Number> items = foo.getItems();
      assertEquals(4, items.size());
      assertEquals(1, items.get(0));
      assertEquals(2, items.get(1));
      assertEquals(new Float(1.1), items.get(2));
      assertEquals(new MyNumber("123456789"), items.get(3));
   }

   public void testFoo2Wrapper()
      throws Exception
   {
      Foo2 foo = unmarshalObject(Foo2.class);
      List<Number> items = foo.getItems();
      assertEquals(4, items.size());
      assertEquals(4, items.size());
      assertEquals(1, items.get(0));
      assertEquals(2, items.get(1));
      assertEquals(new Float(1.1), items.get(2));
      assertEquals(new MyNumber("123456789"), items.get(3));
   }
   
   public void testFoo3Wrapper()
      throws Exception
   {
      Foo3 foo = unmarshalObject(Foo3.class);
      List<Bar> items = foo.getItems();
      assertEquals(4, items.size());
      Bar bar0 = items.get(0);
      assertEquals(bar0.getValue(), 1);
      Bar bar1 = items.get(1);
      assertEquals(bar1.getValue(), 2);
      Bar bar2 = items.get(2);
      assertEquals(bar2.getValue(), new Float(1.1));
      Bar bar3 = items.get(3);
      assertEquals(bar3.getValue(), new MyNumber("123456789"));
   }

   public void testFoo4Wrapper() throws Exception
   {
      Foo4 foo = unmarshalObject(Foo4.class);
      Number[] items = foo.getItems();
      assertEquals(4, items.length);
      assertEquals(1, items[0]);
      assertEquals(2, items[1]);
      assertEquals(new Float(1.1), items[2]);
      assertEquals(new MyNumber("123456789"), items[3]);
   }

   public void testFoo5Wrapper() throws Exception
   {
      Foo4 foo = unmarshalObject(Foo4.class);
      Number[] items = foo.getItems();
      assertEquals(4, items.length);
      assertEquals(1, items[0]);
      assertEquals(2, items[1]);
      assertEquals(new Float(1.1), items[2]);
      assertEquals(new MyNumber("123456789"), items[3]);
   }
}
