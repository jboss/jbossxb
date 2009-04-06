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
package org.jboss.test.xml.unorderedsequence.support;

import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;

/**
 * A OneTwoSequence.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(name="one-two", kind=JBossXmlConstants.MODEL_GROUP_SEQUENCE, propOrder={"one", "two"})
public class OneTwoSequence
{
   private int one;
   private int two;
   
   public int getOne()
   {
      return one;
   }
   
   public void setOne(int one)
   {
      this.one = one;
   }
   
   public int getTwo()
   {
      return two;
   }
   
   public void setTwo(int two)
   {
      this.two = two;
   }
}
