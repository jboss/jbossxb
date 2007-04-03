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
package org.jboss.test.xml.collections;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class Collections
{
   public static Collections getInstance()
   {
      Collections c = new Collections();
      c.stringArray = new String[]{"str1", "str2", "str3"};
      c.intCol = Arrays.asList(new Integer[]{new Integer(1), new Integer(2), new Integer(3)});
      c.itemArray = new String[]{"item1", "item2", "item3"};
      c.itemCol = Arrays.asList(new String[]{"item4", "item5", "item6"});
      c.intArray = new int[3];
      c.intArray[0] = 1;
      c.intArray[1] = 0;
      c.intArray[2] = -1;
      return c;
   }

   private String[] stringArray;
   private Collection intCol;
   private String[] itemArray;
   private Collection itemCol;
   private int[] intArray;

   public String[] getStringArray()
   {
      return stringArray;
   }

   public void setStringArray(String[] stringArray)
   {
      this.stringArray = stringArray;
   }

   public Collection getIntCol()
   {
      return intCol;
   }

   public void setIntCol(Collection intCol)
   {
      this.intCol = intCol;
   }

   public String[] getItemArray()
   {
      return itemArray;
   }

   public void setItemArray(String[] itemArray)
   {
      this.itemArray = itemArray;
   }

   public Collection getItemCol()
   {
      return itemCol;
   }

   public void setItemCol(Collection itemCol)
   {
      this.itemCol = itemCol;
   }

   public int[] getIntArray()
   {
      return intArray;
   }

   public void setIntArray(int[] intArray)
   {
      this.intArray = intArray;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof Collections))
      {
         return false;
      }

      final Collections collections = (Collections)o;

      if(!Arrays.equals(intArray, collections.intArray))
      {
         return false;
      }
      if(intCol != null ? !intCol.equals(collections.intCol) : collections.intCol != null)
      {
         return false;
      }
      if(!Arrays.equals(itemArray, collections.itemArray))
      {
         return false;
      }
      if(itemCol != null ? !itemCol.equals(collections.itemCol) : collections.itemCol != null)
      {
         return false;
      }
      if(!Arrays.equals(stringArray, collections.stringArray))
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (intCol != null ? intCol.hashCode() : 0);
      result = 29 * result + (itemCol != null ? itemCol.hashCode() : 0);
      return result;
   }

   public String toString()
   {
      Integer[] ia = intArray == null ? null : new Integer[intArray.length];
      if(ia != null)
      {
         for(int i = 0; i < intArray.length; ++i)
         {
            ia[i] = new Integer(intArray[i]);
         }
      }

      return "[collections stringArray=" +
         (stringArray == null ? "null" : Arrays.asList(stringArray).toString()) +
         ", intCol=" +
         intCol +
         ", itemArray=" +
         (itemArray == null ? "null" : Arrays.asList(itemArray).toString()) +
         ", itemCol=" + itemCol +
         ", intArray=" +
         (ia == null ? "null" : Arrays.asList(ia).toString()) +
         "]";
   }
}
