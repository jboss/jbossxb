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

import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class Choice3
{
   private Collection listOfIntegers;
   private Collection listOfStrings;

   public Choice3()
   {
   }

   public Choice3(Collection listOfIntegers, Collection listOfStrings)
   {
      this.listOfIntegers = listOfIntegers;
      this.listOfStrings = listOfStrings;
   }

   public Collection getListOfIntegers()
   {
      return listOfIntegers;
   }

   public void setListOfIntegers(Collection listOfIntegers)
   {
      this.listOfIntegers = listOfIntegers;
   }

   public Collection getListOfStrings()
   {
      return listOfStrings;
   }

   public void setListOfStrings(Collection listOfStrings)
   {
      this.listOfStrings = listOfStrings;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof Choice3))
      {
         return false;
      }

      final Choice3 choice3 = (Choice3)o;

      if(listOfIntegers != null ? !listOfIntegers.equals(choice3.listOfIntegers) : choice3.listOfIntegers != null)
      {
         return false;
      }
      if(listOfStrings != null ? !listOfStrings.equals(choice3.listOfStrings) : choice3.listOfStrings != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (listOfIntegers != null ? listOfIntegers.hashCode() : 0);
      result = 29 * result + (listOfStrings != null ? listOfStrings.hashCode() : 0);
      return result;
   }

   public String toString()
   {
      return "[listOfIntegers=" + listOfIntegers + ", listOfStrigns=" + listOfStrings + "]";
   }
}
