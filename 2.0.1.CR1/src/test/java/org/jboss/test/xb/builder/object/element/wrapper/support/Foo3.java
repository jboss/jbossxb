package org.jboss.test.xb.builder.object.element.wrapper.support;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo3
{
   private List<Bar> items;

   public List<Bar> getItems()
   {
      return items;
   }

   @XmlElement(name="bar")
   public void setItems(List<Bar> items)
   {
      this.items = items;
   }
}
