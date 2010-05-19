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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A JBossXmlModelGroupUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractJBossXmlModelGroupTest extends AbstractBuilderTest
{
   public AbstractJBossXmlModelGroupTest(String name)
   {
      super(name);
   }

   protected void assertParticleChoiceBinding(ModelGroupBinding s)
   {
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      
      ParticleBinding choiceParticle;
      ParticleBinding elementParticle;
      if(s instanceof SequenceBinding)
      {
         choiceParticle = i.next();
         elementParticle = i.next();
      }
      else
      {
         ParticleBinding p = i.next();
         if(p.getTerm().isModelGroup())
         {
            choiceParticle = p;
            elementParticle = i.next();
         }
         else
         {
            elementParticle = p;
            choiceParticle = i.next();
         }
      }
      
      TermBinding t = choiceParticle.getTerm();
      assertTrue(t instanceof ChoiceBinding);
      assertEquals(1, choiceParticle.getMaxOccurs());
      assertFalse(choiceParticle.getMaxOccursUnbounded());
      
      ChoiceBinding c = (ChoiceBinding) t;
      particles = c.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> choiceIter = particles.iterator();
      t = choiceIter.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("a"), ((ElementBinding)t).getQName());
      t = choiceIter.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("b"), ((ElementBinding)t).getQName());
      
      t = elementParticle.getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("e"), ((ElementBinding)t).getQName());
   }

   protected void assertPropertiesSequenceBinding(Class<?> root, boolean inCollection)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding || t instanceof UnorderedSequenceBinding);
      ModelGroupBinding s = (ModelGroupBinding) t;
      assertPropertiesSequenceBinding(s, inCollection);
   }

   protected void assertPropertiesSequenceBinding(ModelGroupBinding s, boolean inCollection)
   {
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(1, particles.size());
      ParticleBinding p = particles.iterator().next();
      if(p.getTerm() instanceof SequenceBinding)
         assertABCGroupParticle(p, true, inCollection);
      else
         assertABCGroupParticle(p, false, inCollection);
   }

   protected void assertPropertiesChoiceBinding(Class<?> root, boolean inCollection)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding || t instanceof UnorderedSequenceBinding);
      ModelGroupBinding s = (ModelGroupBinding) t;
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(1, particles.size());
      ParticleBinding p = particles.iterator().next();
      t = p.getTerm();
      assertTrue(t instanceof ChoiceBinding);
      //assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertEquals(inCollection, p.getMaxOccursUnbounded());
      ChoiceBinding c = (ChoiceBinding) t;
      particles = c.getParticles();
      assertEquals(3, particles.size());
      
      Set<QName> set = new HashSet<QName>();
      set.add(new QName("c"));
      set.add(new QName("b"));
      set.add(new QName("a"));

      for(ParticleBinding cp : particles)
      {
         t = cp.getTerm();
         assertTrue(t.isElement());
         assertTrue(set.contains(((ElementBinding) t).getQName()));
      }
   }

   protected void assertPropertiesAllBinding(Class<?> root, boolean inCollection)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding || t instanceof UnorderedSequenceBinding);
      ModelGroupBinding s = (ModelGroupBinding) t;

      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();

      ParticleBinding groupParticle = null;
      ParticleBinding elementParticle = null;
      if(s instanceof SequenceBinding)
      {
         groupParticle = i.next();
         elementParticle = i.next();
      }
      else
      {
         groupParticle = i.next();
         if(groupParticle.getTerm().isElement())
         {
            elementParticle = groupParticle;
            groupParticle = i.next();
         }
         else
            elementParticle = i.next();
      }

      assertTrue(groupParticle.getTerm() instanceof AllBinding);
      assertABCGroupParticle(groupParticle, false, inCollection);
      
      t = elementParticle.getTerm();
      assertTrue(t.isElement());
      assertEquals(0, elementParticle.getMinOccurs());
      assertEquals(1, elementParticle.getMaxOccurs());
      assertFalse(elementParticle.getMaxOccursUnbounded());
      assertEquals(new QName("prop"), ((ElementBinding)t).getQName());
   }

   protected void assertABCGroupParticle(ParticleBinding p, boolean ordered, boolean inCollection)
   {
      Collection<ParticleBinding> particles;
      assertEquals(1, p.getMaxOccurs());
      assertEquals(inCollection, p.getMaxOccursUnbounded());
      ModelGroupBinding group = (ModelGroupBinding) p.getTerm();
      particles = group.getParticles();
      assertEquals(3, particles.size());

      if (ordered)
      {
         Iterator<ParticleBinding> i = particles.iterator();
         TermBinding t = i.next().getTerm();
         assertTrue(t.isElement());
         assertEquals(new QName("c"), ((ElementBinding) t).getQName());
         t = i.next().getTerm();
         assertTrue(t.isElement());
         assertEquals(new QName("b"), ((ElementBinding) t).getQName());
         t = i.next().getTerm();
         assertTrue(t.isElement());
         assertEquals(new QName("a"), ((ElementBinding) t).getQName());
      }
      else
      {
         Set<QName> set = new HashSet<QName>();
         set.add(new QName("c"));
         set.add(new QName("b"));
         set.add(new QName("a"));

         for (ParticleBinding cp : particles)
         {
            TermBinding t = cp.getTerm();
            assertTrue(t.isElement());
            assertTrue(set.contains(((ElementBinding) t).getQName()));
         }
      }
   }
}
