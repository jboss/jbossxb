/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xml.jbxb.characters;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 38439 $
 */
public class Binding
{
   private String name;
   private String type;
   private String text;
   private Value2 value2;
   private Value3 value3;
   public Value4 value4;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   public Value2 getValue2()
   {
      return value2;
   }

   public void setValue2(Value2 value2)
   {
      this.value2 = value2;
   }

   public Value3 getValue3()
   {
      return value3;
   }

   public void setValue3(Value3 value3)
   {
      this.value3 = value3;
   }

   // Inner

   public static final class Value2
   {
      public String text;

      public String getText()
      {
         return text;
      }

      public void setText(String text)
      {
         this.text = text;
      }
   }

   public static final class Value3
   {
      public static Object unmarshalChars(String chars)
      {
         return chars;
      }

      public Object chars;
      public String attr;
   }

   public static final class Value4
   {
      public Value4(String value)
      {
         this.value = value;
      }

      public Value4()
      {
      }

      public String value;
   }
}
