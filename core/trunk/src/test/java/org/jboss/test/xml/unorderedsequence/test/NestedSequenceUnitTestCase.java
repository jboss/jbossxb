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
package org.jboss.test.xml.unorderedsequence.test;

import junit.framework.Test;

import org.jboss.test.xml.unorderedsequence.support.OneTwoSequence;
import org.jboss.test.xml.unorderedsequence.support.RootWithOneTwoSequence;

/**
 * A NestedSequenceUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class NestedSequenceUnitTestCase extends AbstractUnorderedSequenceTest<RootWithOneTwoSequence>
{
   public static Test suite()
   {
      return suite(NestedSequenceUnitTestCase.class);
   }
   
   public NestedSequenceUnitTestCase(String name)
   {
      super(name, RootWithOneTwoSequence.class);
   }

   @Override
   protected String getCorrectName()
   {
      return "NestedSequence_correct.xml";
   }

   @Override
   protected String getIncorrectName()
   {
      return "NestedSequence_incorrect.xml";
   }

   @Override
   protected void assertResult(RootWithOneTwoSequence result)
   {
      assertEquals("a", result.getA());
      assertEquals("b", result.getB());
      assertEquals("c", result.getC());
      OneTwoSequence oneTwo = result.getOneTwo();
      assertNotNull(oneTwo);
      assertEquals(1, oneTwo.getOne());
      assertEquals(2, oneTwo.getTwo());
   }

   @Override
   protected String getValidationError()
   {
      return "two cannot appear in this position. Expected content of root is sequence: a? {sequence one-two}? b? c?";
   }
}
