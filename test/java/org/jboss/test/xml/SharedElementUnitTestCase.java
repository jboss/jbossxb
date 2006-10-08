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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

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
public class SharedElementUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/sharedElement";
   
   public static final TestSuite suite()
   {
      return new TestSuite(SharedElementUnitTestCase.class);
   }

   public SharedElementUnitTestCase(String name)
   {
      super(name);
   }

   public void testSharedElementForwardsAndBackwards() throws Exception
   {
      SchemaBinding schema = bind("SharedElement.xsd");
      ElementBinding element1 = schema.getElement(new QName(NS, "element1"));
      assertNotNull(element1);
      ElementBinding element2 = schema.getElement(new QName(NS, "element2"));
      assertNotNull(element2);
      ElementBinding element3 = schema.getElement(new QName(NS, "element3"));
      assertNotNull(element3);
      
      TypeBinding type = element2.getType();
      assertNotNull(type);
      ParticleBinding particle = type.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding);
      SequenceBinding sequence = (SequenceBinding) term;
      Collection particles = sequence.getParticles();
      assertNotNull(particles);
      assertEquals(2, particles.size());
      Iterator iterator = particles.iterator();
      particle = (ParticleBinding) iterator.next();
      term = particle.getTerm();
      assertTrue(element1 == term);
      particle = (ParticleBinding) iterator.next();
      term = particle.getTerm();
      assertTrue(element3 == term);
   }

   public void testSharedElementGroupForwardsAndBackwards() throws Exception
   {
      SchemaBinding schema = bind("SharedElement.xsd");
      ElementBinding element1 = schema.getElement(new QName(NS, "element1"));
      assertNotNull(element1);
      ModelGroupBinding group = schema.getGroup(new QName(NS, "globalGroup"));
      assertNotNull(group);
      ElementBinding element3 = schema.getElement(new QName(NS, "element3"));
      assertNotNull(element3);
      
      Collection particles = group.getParticles();
      assertNotNull(particles);
      assertEquals(2, particles.size());
      Iterator iterator = particles.iterator();
      ParticleBinding particle = (ParticleBinding) iterator.next();
      TermBinding term = particle.getTerm();
      assertTrue(element1 == term);
      particle = (ParticleBinding) iterator.next();
      term = particle.getTerm();
      assertTrue(element3 == term);
   }
}
