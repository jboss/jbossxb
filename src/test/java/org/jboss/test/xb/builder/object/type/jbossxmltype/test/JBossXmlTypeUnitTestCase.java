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
package org.jboss.test.xb.builder.object.type.jbossxmltype.test;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.jbossxmltype.support.RootWithDefaults;
import org.jboss.test.xb.builder.object.type.jbossxmltype.support.RootWithModelGroupAll;
import org.jboss.test.xb.builder.object.type.jbossxmltype.support.RootWithModelGroupChoice;
import org.jboss.test.xb.builder.object.type.jbossxmltype.support.RootWithXmlTypePropOrder;
import org.jboss.test.xb.builder.object.type.jbossxmltype.support.RootWithModelGroupSequence;
import org.jboss.test.xb.builder.object.type.jbossxmltype.support.RootWithModelGroupUnorderedSequence;
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
 * A JBossXmlTypeUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossXmlTypeUnitTestCase extends AbstractBuilderTest
{
   public JBossXmlTypeUnitTestCase(String name)
   {
      super(name);
   }

   public void testModelGroupWithDefaults() throws Exception
   {
      ModelGroupBinding group = getTypeModelGroup(RootWithDefaults.class);
      assertTrue(group instanceof SequenceBinding);
      // but it's unpredictable
      assertUnorderedGroup(group);
   }

   public void testModelGroupWithXmlTypePropOrder() throws Exception
   {
      ModelGroupBinding group = getTypeModelGroup(RootWithXmlTypePropOrder.class);
      assertTrue(group instanceof SequenceBinding);
      assertOrderedGroup(group);
   }

   public void testModelGroupSequence() throws Exception
   {
      ModelGroupBinding group = getTypeModelGroup(RootWithModelGroupSequence.class);
      assertTrue(group instanceof SequenceBinding);
      assertOrderedGroup(group);
   }
   
   public void testModelGroupChoice() throws Exception
   {
      ModelGroupBinding group = getTypeModelGroup(RootWithModelGroupChoice.class);
      assertTrue(group instanceof ChoiceBinding);
      assertUnorderedGroup(group);
   }

   public void testModelGroupAll() throws Exception
   {
      ModelGroupBinding group = getTypeModelGroup(RootWithModelGroupAll.class);
      assertTrue(group instanceof AllBinding);
      assertUnorderedGroup(group);
   }

   public void testModelGroupUnorderedSequence() throws Exception
   {
      ModelGroupBinding group = getTypeModelGroup(RootWithModelGroupUnorderedSequence.class);
      assertTrue(group instanceof UnorderedSequenceBinding);
      assertUnorderedGroup(group);
   }

   private void assertOrderedGroup(ModelGroupBinding group)
   {
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      ParticleBinding p = i.next();
      assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      TermBinding t = p.getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("a"), ((ElementBinding)t).getQName());
      
      p = i.next();
      assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      t = p.getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("b"), ((ElementBinding)t).getQName());
   }

   private void assertUnorderedGroup(ModelGroupBinding group)
   {
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      ParticleBinding p = i.next();
      assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      TermBinding t = p.getTerm();
      assertTrue(t.isElement());
      
      QName eName = ((ElementBinding)t).getQName();
      if(!new QName("a").equals(eName))
         assertEquals(new QName("b"), eName);
      
      p = i.next();
      assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      t = p.getTerm();
      assertTrue(t.isElement());

      eName = ((ElementBinding)t).getQName();
      if(!new QName("a").equals(eName))
         assertEquals(new QName("b"), eName);
   }

   private ModelGroupBinding getTypeModelGroup(Class<?> root)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t.isModelGroup());
      return (ModelGroupBinding) t;
   }
}
