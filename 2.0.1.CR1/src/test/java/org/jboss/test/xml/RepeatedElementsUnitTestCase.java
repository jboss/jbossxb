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

import junit.framework.TestSuite;

/***
 * RepeatedElementUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class RepeatedElementsUnitTestCase extends AbstractJBossXBTest
{  
   public static final TestSuite suite()
   {
      return new TestSuite(RepeatedElementsUnitTestCase.class);
   }

   public RepeatedElementsUnitTestCase(String name)
   {
      super(name);
   }

   public void testRepeatedElements() throws Exception
   {
      Object o = unmarshal();

      assertNotNull(o);
      assertTrue(o instanceof Top);
      Top top = (Top) o;
      assertEquals("one", top.one);
      assertEquals("two", top.two);
   }

   public static final class Top
   {
      public String one;
      public String two;
      
      public String getChild()
      {
         return null;
      }
      
      public void setChild(String string)
      {
         if (one == null)
            one = string;
         else if (two == null)
            two = string;
         else
            throw new IllegalArgumentException("Too many children");
      }
   }
}
