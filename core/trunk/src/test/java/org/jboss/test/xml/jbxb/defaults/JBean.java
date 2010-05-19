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
package org.jboss.test.xml.jbxb.defaults;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 37406 $
 */
public class JBean
{
   private String attr1;
   private int attr2;
   private boolean attr3;

   public String getAttr1()
   {
      return attr1;
   }
   public void setAttr1(String attr1)
   {
      this.attr1 = attr1;
   }

   public int getAttr2()
   {
      return attr2;
   }
   public void setAttr2(int attr2)
   {
      this.attr2 = attr2;
   }

   public boolean getAttr3()
   {
      return attr3;
   }
   public void setAttr3(boolean attr3)
   {
      this.attr3 = attr3;
   }
}
