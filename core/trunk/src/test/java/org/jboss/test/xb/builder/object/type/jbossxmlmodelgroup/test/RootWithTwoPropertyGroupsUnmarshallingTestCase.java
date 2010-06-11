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
package org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test;


import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.PropertiesSequence;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithTwoPropertyGroups;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithTwoPropertyGroups.GroupWrapper1;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithTwoPropertyGroups.GroupWrapper2;

/**
 * A RootWithTwoPropertyGroupsUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RootWithTwoPropertyGroupsUnmarshallingTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(RootWithTwoPropertyGroupsUnmarshallingTestCase.class);
   }

   public RootWithTwoPropertyGroupsUnmarshallingTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      RootWithTwoPropertyGroups o = unmarshalObject(RootWithTwoPropertyGroups.class);
      GroupWrapper1 gw1 = o.getGroup1();
      assertNotNull(gw1);
      PropertiesSequence g = gw1.getGroup();
      assertNotNull(g);
      assertEquals("a", g.getA());
      assertEquals("b", g.getB());
      assertEquals("c", g.getC());
      
      GroupWrapper2 gw2 = o.getGroup2();
      assertNotNull(gw2);
      g = gw2.getGroup();
      assertNotNull(g);
      assertEquals("a", g.getA());
      assertEquals("b", g.getB());
      assertEquals("c", g.getC());
   }
}
