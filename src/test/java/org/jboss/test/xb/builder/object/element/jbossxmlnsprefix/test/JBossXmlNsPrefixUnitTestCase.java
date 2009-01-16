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
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameFalseComponentContentFalseGroup;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameFalseComponentContentTrue;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameFalseComponentContentTrueGroup;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameTrueComponentContentFalse;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameFalseComponentContentFalse;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameTrueComponentContentFalseGroup;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameTrueComponentContentTrueGroup;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.MissingPrefixMappingException;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.MissingPrefixMappingGoesTarget;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.ComponentQNameTrueComponentContentTrue;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support.RootWithGroupWithJBossXmlNsPrefixProperty;
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
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameTrueComponentContentTrue.class, true);
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
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameTrueComponentContentFalse.class, true);
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
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameFalseComponentContentFalse.class, true);
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
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameFalseComponentContentTrue.class, true);
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
      assertEquals(new QName("ns.child", "name"), e.getQName());
      
      // rootName
      particle = particleIterator.next();
      e = (ElementBinding) particle.getTerm();
      assertEquals(new QName("ns.root", "root-name"), e.getQName());
   }
   
   public void testApplyToElementTrueApplyToTypeTrueGroup() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameTrueComponentContentTrueGroup.class, true);
      
      ElementBinding e = schema.getElement(new QName("ns", "root"));
      assertNotNull(e);
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> iterator = particles.iterator();
      
      group = (ModelGroupBinding) iterator.next().getTerm();
      assertEquals(new QName("anotherNs", "group"), group.getQName());
      particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> groupIterator = particles.iterator();
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("anotherNs", "count"), e.getQName());
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("anotherNs", "text"), e.getQName());
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "id"), e.getQName());
   }

   public void testApplyToElementFalseApplyToTypeTrueGroup() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameFalseComponentContentTrueGroup.class, true);
      
      ElementBinding e = schema.getElement(new QName("ns", "root"));
      assertNotNull(e);
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> iterator = particles.iterator();
      
      group = (ModelGroupBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "group"), group.getQName());
      particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> groupIterator = particles.iterator();
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("anotherNs", "count"), e.getQName());
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("anotherNs", "text"), e.getQName());
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "id"), e.getQName());
   }

   public void testApplyToElementTrueApplyToTypeFalseGroup() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameTrueComponentContentFalseGroup.class, true);
      
      ElementBinding e = schema.getElement(new QName("ns", "root"));
      assertNotNull(e);
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> iterator = particles.iterator();
      
      group = (ModelGroupBinding) iterator.next().getTerm();
      assertEquals(new QName("anotherNs", "group"), group.getQName());
      particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> groupIterator = particles.iterator();
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("ns", "count"), e.getQName());
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("ns", "text"), e.getQName());
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "id"), e.getQName());
   }

   public void testApplyToElementFalseApplyToTypeFalseGroup() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(ComponentQNameFalseComponentContentFalseGroup.class, true);
      
      ElementBinding e = schema.getElement(new QName("ns", "root"));
      assertNotNull(e);
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> iterator = particles.iterator();
      
      group = (ModelGroupBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "group"), group.getQName());
      particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> groupIterator = particles.iterator();
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("ns", "count"), e.getQName());
      e = (ElementBinding) groupIterator.next().getTerm();
      assertEquals(new QName("ns", "text"), e.getQName());
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "id"), e.getQName());
   }
   
   public void testGroupWithJBossXmlNsProperty() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(RootWithGroupWithJBossXmlNsPrefixProperty.class, true);
      
      ElementBinding e = schema.getElement(new QName("ns", "root"));
      ModelGroupBinding group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(1, particles.size());
      group = (ModelGroupBinding) particles.iterator().next().getTerm();
      particles = group.getParticles();
      assertEquals(4, particles.size());
      Iterator<ParticleBinding> iterator = particles.iterator();
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("childNs", "group-true-content-false"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      e = (ElementBinding) particles.iterator().next().getTerm();
      assertEquals(new QName("ns", "name"), e.getQName());
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "group-false-content-false"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      e = (ElementBinding) particles.iterator().next().getTerm();
      assertEquals(new QName("ns", "name"), e.getQName());
      
      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("childNs", "group-true-content-true"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      e = (ElementBinding) particles.iterator().next().getTerm();
      assertEquals(new QName("childNs", "name"), e.getQName());

      e = (ElementBinding) iterator.next().getTerm();
      assertEquals(new QName("ns", "group-false-content-true"), e.getQName());
      group = (ModelGroupBinding) e.getType().getParticle().getTerm();
      particles = group.getParticles();
      assertEquals(1, particles.size());
      e = (ElementBinding) particles.iterator().next().getTerm();
      assertEquals(new QName("childNs", "name"), e.getQName());
   }
}
