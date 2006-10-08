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
package org.jboss.test.xml;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;

/**
 * GlobalGroupUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class GlobalGroupUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/globalGroup";
   
   public static final TestSuite suite()
   {
      return new TestSuite(GlobalGroupUnitTestCase.class);
   }
   
   public GlobalGroupUnitTestCase(String name)
   {
      super(name);
   }

   public void testGlobalGroup() throws Exception
   {
      SchemaBinding schema = bind("GlobalGroup.xsd");
      ModelGroupBinding group = schema.getGroup(new QName(NS, "global1"));
      assertNotNull(group);
      
      ElementBinding element = schema.getElement(new QName(NS, "parent"));
      assertNotNull(element);
      TypeBinding type = element.getType();
      assertNotNull(type);
      ParticleBinding particle = type.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding);
      SequenceBinding sequence = (SequenceBinding) term;
      Collection particles = sequence.getParticles();
      assertNotNull(particles);
      assertEquals(1, particles.size());
      particle = (ParticleBinding) particles.iterator().next();
      term = particle.getTerm();
      assertTrue(group == term);
   }

   public void testGlobalGroupForwardsAndBackwardsRefs() throws Exception
   {
      SchemaBinding schema = bind("GlobalGroup.xsd");
      ModelGroupBinding group2 = schema.getGroup(new QName(NS, "global2"));
      assertNotNull(group2);
      ModelGroupBinding group3 = schema.getGroup(new QName(NS, "global3"));
      assertNotNull(group3);
      ModelGroupBinding group4 = schema.getGroup(new QName(NS, "global4"));
      assertNotNull(group4);
      
      // Forwards
      Collection particles = group2.getParticles();
      assertNotNull(particles);
      assertEquals(1, particles.size());
      ParticleBinding particle = (ParticleBinding) particles.iterator().next();
      TermBinding term = particle.getTerm();
      assertTrue(term == group3);
      
      // Backwards
      particles = group4.getParticles();
      assertNotNull(particles);
      assertEquals(1, particles.size());
      particle = (ParticleBinding) particles.iterator().next();
      term = particle.getTerm();
      assertTrue(term == group3);
   }

   public void testGlobalGroupSchemaBindingModel() throws Exception
   {
      SchemaBinding schema = bind("GlobalGroup.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Parent.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "parent"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);

      classMetaData = new ClassMetaData();
      classMetaData.setImpl(Global1.class.getName());
      ModelGroupBinding group = schema.getGroup(new QName(NS, "global1"));
      assertNotNull(group);
      group.setClassMetaData(classMetaData);
      group.setSkip(Boolean.FALSE);

      // TODO should be able to determine this from the global group name!
      PropertyMetaData prop = new PropertyMetaData();
      prop.setName("global1");
      group.setPropertyMetaData(prop);
      
      Parent parent = (Parent) unmarshal("GlobalGroup.xml", schema, Parent.class);
      assertNotNull(parent.global1);
      assertEquals(2, parent.global1.child.size());
      
      ArrayList expected = new ArrayList();
      expected.add("Hello");
      expected.add("Goodbye");
      assertEquals(expected, parent.global1.child);
   }

   public static class Parent
   {
      public Global1 global1;
   }
   
   public static class Global1
   {
      public Collection child = new ArrayList();
   }
}
