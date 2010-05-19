package org.jboss.test.xb.builder.object.element.wrapper.support;

import javax.xml.bind.annotation.XmlValue;

public class MyNumber extends Number
{
   private String x;

   public MyNumber()
   {
      this(null);
   }
   public MyNumber(String x)
   {
      this.x = x;
   }

   @Override
   public double doubleValue()
   {
      return 0;
   }

   @Override
   public float floatValue()
   {
      return 0;
   }

   @Override
   public int intValue()
   {
      return 0;
   }

   @Override
   public long longValue()
   {
      return 0;
   }
   public String getX()
   {
      return x;
   }
   @XmlValue
   public void setX(String x)
   {
      this.x = x;
   }
   @Override
   public boolean equals(Object obj)
   {
      if (!(obj instanceof MyNumber))
         return false;
      MyNumber n = (MyNumber) obj;
      return x.equals(n.x);
   }
   @Override
   public int hashCode()
   {
      return x.hashCode();
   }
   @Override
   public String toString()
   {
      return "MyNumber("+x+")";
   }

   
}
