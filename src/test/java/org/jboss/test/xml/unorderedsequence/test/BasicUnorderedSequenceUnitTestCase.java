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


import org.jboss.test.xml.unorderedsequence.support.RootWithUnorderedSequence;

/**
 * A BasicUnoderedSequenceUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class BasicUnorderedSequenceUnitTestCase extends AbstractUnorderedSequenceTest<RootWithUnorderedSequence>
{
   public BasicUnorderedSequenceUnitTestCase(String name)
   {
      super(name, RootWithUnorderedSequence.class);
   }

   @Override
   protected String getCorrectName()
   {
      return "BasicUnorderedSequence_correctAB.xml";
   }

   @Override
   protected String getIncorrectName()
   {
      return "BasicUnorderedSequence_incorrectAB.xml";
   }

   @Override
   protected String getValidationError()
   {
      return "a cannot appear in this position. Expected content of root is sequence: a? b?";
   }

   @Override
   protected void assertResult(RootWithUnorderedSequence result)
   {
      assertEquals("a", result.getA());
      assertEquals("b", result.getB());
   }
}
