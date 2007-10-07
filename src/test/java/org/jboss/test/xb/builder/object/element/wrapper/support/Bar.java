package org.jboss.test.xb.builder.object.element.wrapper.support;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import javax.xml.bind.annotation.XmlType;

import org.jboss.xb.annotations.JBossXmlChild;
import org.jboss.xb.annotations.JBossXmlChildren;

/**
 * Wrapper for numbers used by Foo
 * @author Scott.Stark@jboss.org
 * @version $Revision:$
 */
@XmlType
@JBossXmlChildren
({
   @JBossXmlChild(name="int", type=Integer.class),
   @JBossXmlChild(name="float", type=Float.class),
   @JBossXmlChild(name="x", type=MyNumber.class),
})
public class Bar
{
   private Number value;

   public Number getValue()
   {
      return value;
   }

   public void setValue(Number value)
   {
      this.value = value;
   }
}
