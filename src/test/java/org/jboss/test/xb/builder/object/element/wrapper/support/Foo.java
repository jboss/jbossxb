package org.jboss.test.xb.builder.object.element.wrapper.support;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo
{
   private List<Number> items;

   public List getItems()
   {
      return items;
   }

   @XmlElementWrapper(name="bar")
   @XmlElements({
       @XmlElement(name="int", required=false, type=Integer.class),
       @XmlElement(name="float", required=false, type=Float.class)
   })
   public void setItems(List items)
   {
      this.items = items;
   }

   
}