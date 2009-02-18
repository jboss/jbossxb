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
package org.jboss.test.xml.elementorder.test;

import java.util.List;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xml.elementorder.support.ABSequence;
import org.jboss.test.xml.elementorder.support.CDSequence;
import org.jboss.test.xml.elementorder.support.ElementOrderRoot;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * A ElementOrderUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ElementOrderUnitTestCase extends AbstractBuilderTest
{
   public ElementOrderUnitTestCase(String name)
   {
      super(name);
   }

   public void testValidOneTwoOrder() throws Exception
   {
      ElementOrderRoot o = (ElementOrderRoot) unmarshalObject(ElementOrderRoot.class);
      assertEquals("one", o.getFrist());
      assertEquals("two", o.getSecond());
   }

   public void testWrongOneTwoOrder() throws Exception
   {
      try
      {
         unmarshalObject(ElementOrderRoot.class);
         fail("Element first cannot appear in this position (possibly child elements of root are in the wrong order)");
      }
      catch(JBossXBException e)
      {
         JBossXBRuntimeException re = (JBossXBRuntimeException) e.getCause();
         assertEquals("Element first cannot appear in this position (possibly child elements of root are in the wrong order). " +
               "Correct order of the current sequence group: first? second? {sequence abSequence}? {sequence cdSequence}*", re.getMessage());
      }
   }
   
   public void testValidABSequence() throws Exception
   {
      ElementOrderRoot o = (ElementOrderRoot) unmarshalObject(ElementOrderRoot.class);
      ABSequence ab = o.getABSequence();
      assertNotNull(ab);
      assertEquals("a", ab.getA());
      assertEquals("b", ab.getB());
   }

   public void testWrongABSequence() throws Exception
   {
      try
      {
         unmarshalObject(ElementOrderRoot.class);
         fail("Element a cannot appear in this position (possibly child elements of root are in the wrong order)");
      }
      catch(JBossXBException e)
      {
         JBossXBRuntimeException re = (JBossXBRuntimeException) e.getCause();
         assertEquals("Element a cannot appear in this position (possibly child elements of root are in the wrong order). " +
               "Correct order of the current sequence group: first? second? {sequence abSequence}? {sequence cdSequence}*", re.getMessage());
      }
   }
   
   public void testValidRepeatedCD() throws Exception
   {
      ElementOrderRoot o = (ElementOrderRoot) unmarshalObject(ElementOrderRoot.class);
      List<CDSequence> repeatedCD = o.getRepeatedCD();
      assertNotNull(repeatedCD);
      assertEquals(2, repeatedCD.size());
      CDSequence cd = repeatedCD.get(0);
      assertNotNull(cd);
      assertEquals("c1", cd.getC());
      assertEquals("d1", cd.getD());
      cd = repeatedCD.get(1);
      assertNotNull(cd);
      assertEquals("c2", cd.getC());
      assertEquals("d2", cd.getD());
   }

   // TODO this is a known failure
   // the impl doesn't check XmlElement.required=true and since the sequence is repeatable, the test passes
   public void testWrongRepeatedCD() throws Exception
   {
      try
      {
         unmarshalObject(ElementOrderRoot.class);
         fail("Element d cannot appear in this position (possibly child elements of root are in the wrong order)");
      }
      catch(JBossXBException e)
      {
         JBossXBRuntimeException re = (JBossXBRuntimeException) e.getCause();
         assertEquals("Element d cannot appear in this position (possibly child elements of root are in the wrong order)", re.getMessage());
      }
   }
}
