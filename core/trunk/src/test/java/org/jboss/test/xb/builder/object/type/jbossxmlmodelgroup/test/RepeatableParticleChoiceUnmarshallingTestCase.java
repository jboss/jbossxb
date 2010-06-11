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
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.AbstractChoice;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.ChoiceA;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.ChoiceB;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithParticlesChoiceCollection;

/**
 * A RepeatableParticleChoiceUnmarshalling.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RepeatableParticleChoiceUnmarshallingTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(RepeatableParticleChoiceUnmarshallingTestCase.class);
   }

   /**
    * Create a new RepeatableParticleChoiceUnmarshalling.
    * 
    * @param name
    */
   public RepeatableParticleChoiceUnmarshallingTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      RootWithParticlesChoiceCollection o = unmarshalObject(RootWithParticlesChoiceCollection.class);
      List<AbstractChoice> choices = o.getChoices();
      assertNotNull(choices);
      assertEquals(2, choices.size());
      AbstractChoice choice = choices.get(0);
      assertTrue(choice instanceof ChoiceA);
      assertEquals("a", choice.getValue());
      choice = choices.get(1);
      assertTrue(choice instanceof ChoiceB);
      assertEquals("b", choice.getValue());
   }
}
