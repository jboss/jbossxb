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
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.AbstractChoice;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.ChoiceA;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.ChoiceB;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.PropertiesAll;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.PropertiesChoice;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.PropertiesSequence;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithParticlesChoice;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithParticlesChoiceCollection;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesAll;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesAllCollection;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesChoice;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesChoiceCollection;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesSequence;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithPropertiesSequenceCollection;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A JBossXmlModelGroupUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossXmlModelGroupUnitTestCase extends AbstractBuilderTest
{
   public JBossXmlModelGroupUnitTestCase(String name)
   {
      super(name);
   }

   public void testPropertiesSequenceUnmarshalling() throws Exception
   {
      RootWithPropertiesSequence o = unmarshalObject(RootWithPropertiesSequence.class);
      PropertiesSequence g = o.getGroup();
      assertNotNull(g);
      assertEquals("a", g.getA());
      assertEquals("b", g.getB());
      assertEquals("c", g.getC());
   }

   public void testPropertiesSequenceCollectionUnmarshalling() throws Exception
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

   public void testPropertiesChoiceUnmarshalling() throws Exception
   {
      RootWithPropertiesChoice o = unmarshalObject(RootWithPropertiesChoice.class);
      PropertiesChoice g = o.getGroup();
      assertNotNull(g);
      assertNull(g.getA());
      assertNull(g.getB());
      assertEquals("c", g.getC());
   }

   public void testPropertiesChoiceCollectionUnmarshalling() throws Exception
   {
      RootWithPropertiesChoiceCollection o = unmarshalObject(RootWithPropertiesChoiceCollection.class);
      List<PropertiesChoice> gs = o.getGroups();
      assertNotNull(gs);
      assertEquals(3, gs.size());
      PropertiesChoice g = gs.get(0);
      assertEquals("c", g.getC());
      assertNull(g.getA());
      assertNull(g.getB());
      g = gs.get(1);
      assertNull(g.getC());
      assertNull(g.getA());
      assertEquals("b", g.getB());
      g = gs.get(2);
      assertEquals("a", g.getA());
      assertNull(g.getC());
      assertNull(g.getB());
   }

   public void testPropertiesAllUnmarshalling() throws Exception
   {
      RootWithPropertiesAll o = unmarshalObject(RootWithPropertiesAll.class);
      PropertiesAll g = o.getGroup();
      assertNotNull(g);
      assertEquals("a", g.getA());
      assertEquals("b", g.getB());
      assertEquals("c", g.getC());
   }

   public void testPropertiesAllCollectionUnmarshalling() throws Exception
   {
      RootWithPropertiesAllCollection o = unmarshalObject(RootWithPropertiesAllCollection.class);
      List<PropertiesAll> gs = o.getGroups();
      assertNotNull(gs);
      assertEquals("this is a known issue with repeatable all", 2, gs.size());
      PropertiesAll g = gs.get(0);
      assertEquals("c", g.getC());
      assertEquals("a", g.getA());
      assertEquals("b", g.getB());
      g = gs.get(1);
      assertEquals("cc", g.getC());
      assertEquals("aa", g.getA());
      assertEquals("bb", g.getB());
   }

   public void testPropertiesSequenceBinding() throws Exception
   {
      assertPropertiesSequenceBinding(RootWithPropertiesSequence.class, false);
   }

   public void testPropertiesSequenceCollectionBinding() throws Exception
   {
      assertPropertiesSequenceBinding(RootWithPropertiesSequenceCollection.class, true);
   }

   public void testPropertiesChoiceBinding() throws Exception
   {
      assertPropertiesChoiceBinding(RootWithPropertiesChoice.class, false);
   }

   public void testPropertiesChoiceCollectionBinding() throws Exception
   {
      assertPropertiesChoiceBinding(RootWithPropertiesChoiceCollection.class, true);
   }

   public void testPropertiesAllBinding() throws Exception
   {
      assertPropertiesAllBinding(RootWithPropertiesAll.class, false);
   }

   public void testPropertiesAllCollectionBinding() throws Exception
   {
      assertPropertiesAllBinding(RootWithPropertiesAllCollection.class, true);
   }

   public void testParticlesChoiceUnmarshalling() throws Exception
   {
      RootWithParticlesChoice o = unmarshalObject(RootWithParticlesChoice.class);
      AbstractChoice choice = o.getChoice();
      assertNotNull(choice);
      assertTrue(choice instanceof ChoiceB);
      assertEquals("b", choice.getValue());
   }

   public void testParticlesChoiceCollectionUnmarshalling() throws Exception
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

   public void testParticlesChoiceBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(RootWithParticlesChoice.class);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding);
      SequenceBinding s = (SequenceBinding) t;
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      ParticleBinding p = i.next();
      t = p.getTerm();
      assertTrue(t instanceof ChoiceBinding);
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      
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
      
      t = i.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("e"), ((ElementBinding)t).getQName());
   }

   public void testParticlesChoiceCollectionBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(RootWithParticlesChoiceCollection.class);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding);
      SequenceBinding s = (SequenceBinding) t;
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      ParticleBinding p = i.next();
      t = p.getTerm();
      assertTrue(t instanceof ChoiceBinding);
      assertEquals(1, p.getMaxOccurs());
      assertTrue(p.getMaxOccursUnbounded());
      
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
      
      t = i.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("e"), ((ElementBinding)t).getQName());
   }

   private void assertPropertiesSequenceBinding(Class<?> root, boolean inCollection)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding);
      SequenceBinding s = (SequenceBinding) t;
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(1, particles.size());
      ParticleBinding p = particles.iterator().next();
      t = p.getTerm();
      assertTrue(t instanceof SequenceBinding);
      //assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertEquals(inCollection, p.getMaxOccursUnbounded());
      s = (SequenceBinding) t;
      particles = s.getParticles();
      assertEquals(3, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      t = i.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("c"), ((ElementBinding)t).getQName());
      t = i.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("b"), ((ElementBinding)t).getQName());
      t = i.next().getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("a"), ((ElementBinding)t).getQName());
   }

   private void assertPropertiesChoiceBinding(Class<?> root, boolean inCollection)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding);
      SequenceBinding s = (SequenceBinding) t;
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

   private void assertPropertiesAllBinding(Class<?> root, boolean inCollection)
   {
      SchemaBinding schema = JBossXBBuilder.build(root);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding);
      SequenceBinding s = (SequenceBinding) t;
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(1, particles.size());
      ParticleBinding p = particles.iterator().next();
      t = p.getTerm();
      assertTrue(t instanceof AllBinding);
      //assertEquals(0, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertEquals(inCollection, p.getMaxOccursUnbounded());
      AllBinding a = (AllBinding) t;
      particles = a.getParticles();
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
}
