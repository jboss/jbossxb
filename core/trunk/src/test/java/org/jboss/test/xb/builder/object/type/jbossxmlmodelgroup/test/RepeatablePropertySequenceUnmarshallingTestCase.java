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
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.PropertiesSequence;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesSequenceCollection;

/**
 * A RepeatablePropertySequenceUnmarshallingTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RepeatablePropertySequenceUnmarshallingTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(RepeatablePropertySequenceUnmarshallingTestCase.class);
   }

   public RepeatablePropertySequenceUnmarshallingTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      RootWithPropertiesSequenceCollection o = unmarshalObject(RootWithPropertiesSequenceCollection.class);
      List<PropertiesSequence> gs = o.getGroups();
      assertNotNull(gs);
      assertEquals(2, gs.size());
      int i = 0;
      for(PropertiesSequence g : gs)
      {
         assertNotNull(g);
         ++i;
         assertEquals("a" + i, g.getA());
         assertEquals("b" + i, g.getB());
         assertEquals("c" + i, g.getC());
      }
   }
}
