package org.jboss.test.xb.builder.object.element.xmlelements.support;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo4
{
   private Number[] items;

   public Number[] getItems()
   {
      return items;
   }

   @XmlElements({
      @XmlElement(name="int", required=false, type=Integer.class),
      @XmlElement(name="float", required=false, type=Float.class)
   })
   public void setItems(Number[] items)
   {
      this.items = items;
   }
}
