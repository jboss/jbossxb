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

import java.util.List;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.PropertiesChoice;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesChoiceCollection;

/**
 * A RepeatablePropertyChoiceUnmarshallingTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RepeatablePropertyChoiceUnmarshallingTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(RepeatablePropertyChoiceUnmarshallingTestCase.class);
   }

   /**
    * Create a new RepeatablePropertyChoiceUnmarshallingTestCase.
    * 
    * @param name
    */
   public RepeatablePropertyChoiceUnmarshallingTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      RootWithPropertiesChoiceCollection o = unmarshalObject(RootWithPropertiesChoiceCollection.class);
      List<PropertiesChoice> gs = o.getGroups();
      assertNotNull(gs);
      assertEquals(3, gs.size());
      PropertiesChoice g = gs.get(0);
      assertEquals("c", g.getC());
      assertNull(g.getA());
      assertNull(g.getB());
      g = gs.get(1);
      assertNull(g.getC());
      assertNull(g.getA());
      assertEquals("b", g.getB());
      g = gs.get(2);
      assertEquals("a", g.getA());
      assertNull(g.getC());
      assertNull(g.getB());
   }
}
