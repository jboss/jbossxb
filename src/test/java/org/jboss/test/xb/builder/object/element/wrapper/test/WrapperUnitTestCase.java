package org.jboss.test.xb.builder.object.element.wrapper.test;

import java.util.List;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.wrapper.support.Bar;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo2;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo3;
import org.jboss.test.xb.builder.object.element.wrapper.support.MyNumber;

public class WrapperUnitTestCase extends AbstractBuilderTest
{
   public WrapperUnitTestCase(String name)
   {
      super(name);
   }

   public void testFooWrapper()
      throws Exception
   {
      enableTrace("org.jboss.xb");
      Foo foo = unmarshalObject(Foo.class);
      List<Number> items = foo.getItems();
      assertEquals(3, items.size());
   }
   public void testFoo2Wrapper()
      throws Exception
   {
      Foo2 foo = unmarshalObject(Foo2.class);
      List<Number> items = foo.getItems();
      assertEquals(3, items.size());
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
}
