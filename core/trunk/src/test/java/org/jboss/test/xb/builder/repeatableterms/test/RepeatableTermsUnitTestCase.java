/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xb.builder.repeatableterms.test;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.repeatableterms.support.Choice;
import org.jboss.test.xb.builder.repeatableterms.support.Sequence;
import org.jboss.test.xb.builder.repeatableterms.support.Top;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A RepeatableTermsUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RepeatableTermsUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(RepeatableTermsUnitTestCase.class);
   }
   
   public RepeatableTermsUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      Top top = unmarshalObject(Top.class);
      
      String[] items = top.getItems();
      assertNotNull(items);
      assertEquals(3, items.length);
      for(int i = 0; i < items.length; ++i)
         assertEquals("item" + (i + 1), items[i]);

      Sequence[] sequences = top.getSequences();
      assertNotNull(sequences);
      
      String[] expected = new String[]{
            "sequenceChoice1_1", "sequenceChoice2_1", "sequenceChoice1_2", "sequenceChoice1_3", "sequenceChoice2_2"};
      assertEquals(expected.length, sequences.length);
      for(int i = 0; i < sequences.length; ++i)
         assertEquals(expected[i], sequences[i].getChoice());
         

      Choice[] choices = top.getChoices();
      assertNotNull(choices);
      
      assertEquals(3, choices.length);
      
      Choice choice = choices[0];
      assertNotNull(choice);
      assertNull(choice.getChoiceChoice2());
      String[] strings = choice.getChoiceChoice1();
      assertNotNull(strings);
      assertEquals(2, strings.length);
      assertEquals("choiceChoice1_1", strings[0]);
      assertEquals("choiceChoice1_2", strings[1]);

      choice = choices[1];
      assertNotNull(choice);
      assertNull(choice.getChoiceChoice1());
      strings = choice.getChoiceChoice2();
      assertNotNull(strings);
      assertEquals(2, strings.length);
      assertEquals("choiceChoice2_1", strings[0]);
      assertEquals("choiceChoice2_2", strings[1]);

      choice = choices[2];
      assertNotNull(choice);
      assertNull(choice.getChoiceChoice2());
      strings = choice.getChoiceChoice1();
      assertNotNull(strings);
      assertEquals(2, strings.length);
      assertEquals("choiceChoice1_3", strings[0]);
      assertEquals("choiceChoice1_4", strings[1]);
   }
   
   public void testBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(Top.class, true);
      
      ElementBinding top = schema.getElement(new QName("top"));
      assertNotNull(top);
      ModelGroupBinding topSequence = (ModelGroupBinding) top.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = topSequence.getParticles();
      assertEquals(3, particles.size());
      
      Iterator<ParticleBinding> i = particles.iterator();
      while(i.hasNext())
      {
         ParticleBinding particle = i.next();
         assertTrue(particle.getMaxOccursUnbounded());
         TermBinding term = particle.getTerm();
         if(term.isElement())
         {
            assertEquals(new QName("item"), ((ElementBinding)term).getQName());
         }
         else if(term instanceof ChoiceBinding)
         {
            ChoiceBinding choice = (ChoiceBinding) term;
            Collection<ParticleBinding> choices = choice.getParticles();
            assertEquals(2, choices.size());
            Iterator<ParticleBinding> ci = choices.iterator();
            while(ci.hasNext())
            {
               ParticleBinding cp = ci.next();
               assertTrue(cp.getMaxOccursUnbounded());
               TermBinding ct = cp.getTerm();
               assertTrue(ct.isElement());
               QName name = ((ElementBinding)ct).getQName();
               assertTrue(name.equals(new QName("choiceChoice1")) || name.equals(new QName("choiceChoice2")));
            }
         }
         else // sequence or unordered sequence
         {
            ModelGroupBinding seq = (ModelGroupBinding)term;
            Collection<ParticleBinding> seqParticles = seq.getParticles();
            assertEquals(1, seqParticles.size());
            ParticleBinding choiceP = seqParticles.iterator().next();
            assertFalse(choiceP.getMaxOccursUnbounded());
            TermBinding choice = choiceP.getTerm();
            assertTrue(choice instanceof ChoiceBinding);
            Collection<ParticleBinding> choices = ((ChoiceBinding)choice).getParticles();
            assertEquals(2, choices.size());
            Iterator<ParticleBinding> ci = choices.iterator();
            while(ci.hasNext())
            {
               ParticleBinding cp = ci.next();
               assertFalse(cp.getMaxOccursUnbounded());
               TermBinding ct = cp.getTerm();
               assertTrue(ct.isElement());
               QName name = ((ElementBinding)ct).getQName();
               assertTrue(name.equals(new QName("sequenceChoice1")) || name.equals(new QName("sequenceChoice2")));
            }
         }
      }
   }
}
