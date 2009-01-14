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
package org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.test;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ApplyToElementFalseApplyToTypeTrue;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ApplyToElementTrueApplyToTypeFalse;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ApplyToElementFalseApplyToTypeFalse;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.MissingPrefixMappingException;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.MissingPrefixMappingGoesTarget;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ApplyToElementTrueApplyToTypeTrue;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A JBossXmlNsPrefixUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossXmlNsPrefixUnitTestCase extends AbstractBuilderTest
{
   public JBossXmlNsPrefixUnitTestCase(String name)
   {
      super(name);
   }

   public void testMissingPrefixMappingException() throws Exception
   {
      try
      {
         JBossXBBuilder.build(MissingPrefixMappingException.class, true);
         fail("didn't throw an exception for the unmapped prefix");
      }
      catch(RuntimeException e)
      {
         assertTrue(e.getMessage().startsWith("Prefix 'child' is not mapped to any namespace!"));
      }
   }

   public void testMissingPrefixGoesTarget() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(MissingPrefixMappingGoesTarget.class, true);
      Iterator<ElementBinding> elements = schema.getElements();
      assertTrue(elements.hasNext());
      
      //root
      ElementBinding e = elements.next();
      assertFalse(elements.hasNext());
      assertEquals(new QName("ns.root", "root"), e.getQName());
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(1, particles.size());
      ParticleBinding particle = particles.iterator().next();
      
      // child
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "child"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();

      // child/name
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "name"), e.getQName());
   }

   public void testApplyToElementTrueApplyToTypeTrue()
   {
      SchemaBinding schema = JBossXBBuilder.build(ApplyToElementTrueApplyToTypeTrue.class, true);
      assertNotNull(schema);
      Iterator<ElementBinding> elements = schema.getElements();
      assertTrue(elements.hasNext());
      
      //root
      ElementBinding e = elements.next();
      assertFalse(elements.hasNext());
      assertEquals(new QName("ns.root", "root"), e.getQName());
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(1, particles.size());
      ParticleBinding particle = particles.iterator().next();
      
      // child
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.child", "child"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();

      // child/name
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.child", "name"), e.getQName());
   }

   public void testApplyToElementTrueApplyToTypeFalse()
   {
      SchemaBinding schema = JBossXBBuilder.build(ApplyToElementTrueApplyToTypeFalse.class, true);
      assertNotNull(schema);
      Iterator<ElementBinding> elements = schema.getElements();
      assertTrue(elements.hasNext());
      
      //root
      ElementBinding e = elements.next();
      assertFalse(elements.hasNext());
      assertEquals(new QName("ns.root", "root"), e.getQName());
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> particleIterator = particles.iterator();
      ParticleBinding particle = particleIterator.next();
      
      // child
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.child", "child"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();

      // child/name
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "name"), e.getQName());
      
      // rootName
      particle = particleIterator.next();
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "root-name"), e.getQName());
   }

   public void testApplyToElementFalseApplyToTypeFalse()
   {
      SchemaBinding schema = JBossXBBuilder.build(ApplyToElementFalseApplyToTypeFalse.class, true);
      assertNotNull(schema);
      Iterator<ElementBinding> elements = schema.getElements();
      assertTrue(elements.hasNext());
      
      //root
      ElementBinding e = elements.next();
      assertFalse(elements.hasNext());
      assertEquals(new QName("ns.root", "root"), e.getQName());
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> particleIterator = particles.iterator();
      ParticleBinding particle = particleIterator.next();
      
      // child
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "child"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();

      // child/name
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "name"), e.getQName());
      
      // rootName
      particle = particleIterator.next();
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "root-name"), e.getQName());
   }

   public void testApplyToElementFalseApplyToTypeTrue()
   {
      SchemaBinding schema = JBossXBBuilder.build(ApplyToElementFalseApplyToTypeTrue.class, true);
      assertNotNull(schema);
      Iterator<ElementBinding> elements = schema.getElements();
      assertTrue(elements.hasNext());
      
      //root
      ElementBinding e = elements.next();
      assertFalse(elements.hasNext());
      assertEquals(new QName("ns.root", "root"), e.getQName());
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> particleIterator = particles.iterator();
      ParticleBinding particle = particleIterator.next();
      
      // child
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.child", "child"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();

      // child/name
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.child", "name"), e.getQName());
      
      // rootName
      particle = particleIterator.next();
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "root-name"), e.getQName());
   }
}
