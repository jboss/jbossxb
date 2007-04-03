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
package org.jboss.test.xml.sandbox;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class Choice2
{
   private String c;
   private String d;
   private String e;

   public Choice2()
   {
   }

   public Choice2(String c, String d, String e)
   {
      this.c = c;
      this.d = d;
      this.e = e;
   }

   public String getC()
   {
      return c;
   }

   public void setC(String c)
   {
      this.c = c;
   }

   public String getD()
   {
      return d;
   }

   public void setD(String d)
   {
      this.d = d;
   }

   public String getE()
   {
      return e;
   }

   public void setE(String e)
   {
      this.e = e;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof Choice2))
      {
         return false;
      }

      final Choice2 choice2 = (Choice2)o;

      if(c != null ? !c.equals(choice2.c) : choice2.c != null)
      {
         return false;
      }
      if(d != null ? !d.equals(choice2.d) : choice2.d != null)
      {
         return false;
      }
      if(e != null ? !e.equals(choice2.e) : choice2.e != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (c != null ? c.hashCode() : 0);
      result = 29 * result + (d != null ? d.hashCode() : 0);
      result = 29 * result + (e != null ? e.hashCode() : 0);
      return result;
   }

   public String toString()
   {
      return "[c=" + c + ", d=" + d + ", e=" + e + "]";
   }
}
