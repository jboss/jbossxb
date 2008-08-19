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
package org.jboss.test.xml;

import java.util.List;

import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * A ListValueUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ListValueUnitTestCase extends AbstractJBossXBTest
{
   public ListValueUnitTestCase(String name)
   {
      super(name);
   }

   public void testCtor() throws Exception
   {
      Object o = unmarshal();
      assertNotNull(o);
      assertTrue(o instanceof Root);
      Root root = (Root) o;
      assertEquals("attr", root.getAttr());
      int[] ints = root.getInts();
      assertNotNull(ints);
      assertEquals(2, ints.length);
      assertEquals(1, ints[0]);
      assertEquals(2, ints[1]);
      List strs = root.getStrs();
      assertNotNull(strs);
      assertEquals(2, strs.size());
      assertEquals("item1", strs.get(0));
      assertEquals("item2", strs.get(1));
   }
   
   public static class Root
   {
      private String attr;
      private int[] ints;
      private List strs;
      
      
      public Root(String attr, List items, int[] ints)
      {
         this.attr = attr;
         this.ints = ints;
         this.strs = items;
      }

      public String getAttr()
      {
         return attr;
      }
      
      public void setAttr(String attr)
      {
         this.attr = attr;
      }
      
      public int[] getInts()
      {
         return ints;
      }
      
      public void setInts(int[] ints)
      {
         this.ints = ints;
      }
      
      public List getStrs()
      {
         return strs;
      }
      
      public void setStrs(List items)
      {
         this.strs = items;
      }
   }
}
