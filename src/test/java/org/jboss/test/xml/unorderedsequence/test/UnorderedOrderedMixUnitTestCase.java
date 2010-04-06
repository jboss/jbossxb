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
package org.jboss.test.xml.unorderedsequence.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xml.unorderedsequence.support.UnorderedOrderedMix;
import org.jboss.test.xml.unorderedsequence.support.UnorderedOrderedMix.SomeBean;
import org.jboss.test.xml.unorderedsequence.support.UnorderedOrderedMix.SomeSequence;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A UnorderedSequenceWithCollections.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class UnorderedOrderedMixUnitTestCase extends AbstractBuilderTest
{
   public UnorderedOrderedMixUnitTestCase(String name)
   {
      super(name);
   }

   private boolean defaultRepeatableHandlers;
   public void setUp() throws Exception
   {
      super.setUp();
      defaultRepeatableHandlers = JBossXBBuilder.isRepeatableParticleHandlers();
      JBossXBBuilder.setRepeatableParticleHandlers(true);
   }
   
   public void tearDown() throws Exception
   {
      super.tearDown();
      JBossXBBuilder.setRepeatableParticleHandlers(defaultRepeatableHandlers);
   }
   
   public void testBinding() throws Exception
   {
      JBossXBBuilder.setUseUnorderedSequence(true);
      SchemaBinding schema = JBossXBBuilder.build(UnorderedOrderedMix.class, true);
      ElementBinding root = schema.getElement(new QName("root"));
      assertNotNull(root);
      TermBinding term = root.getType().getParticle().getTerm();
      assertTrue(term instanceof UnorderedSequenceBinding);
      UnorderedSequenceBinding group = (UnorderedSequenceBinding) term;
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(4, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      while(i.hasNext())
      {
         ParticleBinding p = i.next();
         assertTrue(p.getMaxOccursUnbounded());         
         term = p.getTerm();
         if(term.isElement())
         {
            if(new QName("string").equals(term.getQName()))
               continue;
            assertEquals(new QName("bean"), term.getQName());
            term = ((ElementBinding)term).getType().getParticle().getTerm();
            assertTrue(term instanceof SequenceBinding);
            particles = ((SequenceBinding)term).getParticles();
            assertEquals(1, particles.size());
            term = particles.iterator().next().getTerm();
            assertTrue(term.isElement());
            assertEquals(new QName("string"), term.getQName());
         }
         else if(term instanceof SequenceBinding)
         {
            assertFalse(term.isSkip());
            particles = ((SequenceBinding)term).getParticles();
            assertEquals(3, particles.size());
            Iterator<ParticleBinding> iter = particles.iterator();
            List<QName> expected = Arrays.asList(new QName("a"), new QName("b"), new QName("c"));
            List<QName> actual = Arrays.asList(
                  iter.next().getTerm().getQName(),
                  iter.next().getTerm().getQName(),
                  iter.next().getTerm().getQName());
            assertEquals(expected, actual);
         }
         else if(term instanceof ChoiceBinding)
         {
            assertTrue(term.isSkip());
            particles = ((ChoiceBinding)term).getParticles();
            assertEquals(2, particles.size());
            Iterator<ParticleBinding> iter = particles.iterator();
            List<QName> expected = Arrays.asList(new QName("choice1"), new QName("choice2"));
            List<QName> actual = Arrays.asList(iter.next().getTerm().getQName(), iter.next().getTerm().getQName());
            assertEquals(expected, actual);
         }
      }
   }
   
   public void testUnmarshalling() throws Exception
   {
      UnorderedOrderedMix root = unmarshalObject(UnorderedOrderedMix.class);
      List<String> strings = root.getStrings();
      assertNotNull(strings);
      assertEquals(2, strings.size());
      assertEquals("string1", strings.get(0));
      assertEquals("string2", strings.get(1));
      
      List<String> choices = root.getChoices();
      assertNotNull(choices);
      assertEquals(2, choices.size());
      assertEquals("choice11", choices.get(0));
      assertEquals("choice21", choices.get(1));
      
      List<SomeSequence> sequences = root.getSequences();
      assertNotNull(sequences);
      assertEquals(3, sequences.size());
      SomeSequence sequence = sequences.get(0);
      assertNotNull(sequence);
      assertEquals("a1", sequence.getA());
      assertNull(sequence.getB());
      assertTrue(sequence.getC().isEmpty());
      sequence = sequences.get(1);
      assertNotNull(sequence);
      assertNull(sequence.getA());
      assertEquals("b1", sequence.getB());
      assertTrue(sequence.getC().isEmpty());
      sequence = sequences.get(2);
      assertNotNull(sequence);
      assertEquals("a2", sequence.getA());
      assertEquals("b2", sequence.getB());
      assertEquals(2, sequence.getC().size());
      assertTrue(sequence.getC().contains("c1"));
      assertTrue(sequence.getC().contains("c2"));
      
      List<SomeBean> beans = root.getBeans();
      assertNotNull(beans);
      assertEquals(2, beans.size());
      SomeBean bean = beans.get(0);
      assertNotNull(bean);
      strings = bean.getStrings();
      assertNotNull(strings);
      assertEquals(1, strings.size());
      assertTrue(strings.contains("string1"));
      bean = beans.get(1);
      assertNotNull(bean);
      strings = bean.getStrings();
      assertNotNull(strings);
      assertEquals(2, strings.size());
      assertTrue(strings.contains("string1"));
      assertTrue(strings.contains("string2"));
   }
}
