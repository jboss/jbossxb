package org.jboss.test.xb.builder.object.element.xmlelements.test;


import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.xmlelements.support.Foo4;

public class XmlElementsUnitTestCase extends AbstractBuilderTest
{
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
