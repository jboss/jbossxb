package org.jboss.test.xb.builder.object.element.wrapper.test;

import java.util.List;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo;
import org.jboss.test.xb.builder.object.element.wrapper.support.Foo2;

public class WrapperUnitTestCase extends AbstractBuilderTest
{
   public WrapperUnitTestCase(String name)
   {
      super(name);
   }

   public void testFooWrapper()
      throws Exception
   {
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
}
