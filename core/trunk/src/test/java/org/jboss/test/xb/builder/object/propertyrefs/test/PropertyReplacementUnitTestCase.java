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
package org.jboss.test.xb.builder.object.propertyrefs.test;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.propertyrefs.support.Root;

/**
 * A PropertyReplacementUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class PropertyReplacementUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(PropertyReplacementUnitTestCase.class);
   }
   
   public PropertyReplacementUnitTestCase(String name)
   {
      super(name);
   }

   public void testPropertyReplacement() throws Exception
   {
      String minName = getClass().getName() + "_" + getName() + ".min";
      String maxName = getClass().getName() + "_" + getName() + ".max";
      String valName = getClass().getName() + "_" + getName() + ".val";
      
      System.setProperty(minName, "1");
      System.setProperty(maxName, "3");
      System.setProperty(valName, "2");

      Root root = unmarshalObject(Root.class);
      assertEquals(1, root.getMin());
      assertEquals(3, root.getMax());
      assertEquals(2, root.getValue());
   }
}
