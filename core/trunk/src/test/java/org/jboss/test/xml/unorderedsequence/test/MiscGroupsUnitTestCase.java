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

import org.jboss.test.xml.unorderedsequence.support.RootWithMiscGroups;
import org.jboss.test.xml.unorderedsequence.support.RootWithMiscGroups.CDEChoice;
import org.jboss.test.xml.unorderedsequence.support.RootWithMiscGroups.DESequence;
import org.jboss.test.xml.unorderedsequence.support.RootWithMiscGroups.FGSequence;

/**
 * A BasicUnoderedSequenceUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class MiscGroupsUnitTestCase extends AbstractUnorderedSequenceTest<RootWithMiscGroups>
{
   public static Test suite()
   {
      return suite(MiscGroupsUnitTestCase.class);
   }
   
   public MiscGroupsUnitTestCase(String name)
   {
      super(name, RootWithMiscGroups.class);
   }

   @Override
   protected String getCorrectName()
   {
      return "MiscGroups_correct.xml";
   }

   @Override
   protected String getIncorrectName()
   {
      return "MiscGroups_incorrect.xml";
   }

   @Override
   protected String getValidationError()
   {
      return "e cannot appear in this position. Expected content of root is sequence: a? b? {choice cde}? {sequence fg}?";
   }

   @Override
   protected void assertResult(RootWithMiscGroups result)
   {
      assertEquals("a", result.getA());
      assertEquals("b", result.getB());
      CDEChoice cde = result.getCde();
      assertNotNull(cde);
      assertNull(cde.getC());
      DESequence de = cde.getDe();
      assertNotNull(de);
      assertEquals("d", de.getD());
      assertEquals("e", de.getE());
      FGSequence fg = result.getFg();
      assertNotNull(fg);
      assertEquals("f", fg.getF());
      assertEquals("g", fg.getG());
   }
}
