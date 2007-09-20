/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xb.builder.object.type.accessor.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.accessor.support.Sequence;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * SequenceUnitTestCase.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class SequenceUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(SequenceUnitTestCase.class);
   }
   
   public SequenceUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshal() throws Exception
   {
      Sequence sequence = unmarshalObject(Sequence.class);
      assertEquals("one", sequence.getOne());
      assertEquals("two", sequence.getTwo());
      assertEquals("three", sequence.getThree());
   }

   @SuppressWarnings("unchecked")
   public void testBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(Sequence.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "sequence");
      TypeBinding typeBinding = schemaBinding.getType(qName);
      assertNotNull(typeBinding);
      ParticleBinding particle = typeBinding.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding);
      SequenceBinding sequence = (SequenceBinding) term;
      List<QName> elements = new ArrayList<QName>();
      Collection<ParticleBinding> particles = sequence.getParticles();
      for (ParticleBinding p : particles)
      {
         term = p.getTerm();
         assertTrue(term instanceof ElementBinding);
         elements.add(((ElementBinding) term).getQName());
      }
      ArrayList<QName> expected = new ArrayList();
      expected.add(new QName(XMLConstants.NULL_NS_URI, "three"));
      expected.add(new QName(XMLConstants.NULL_NS_URI, "two"));
      expected.add(new QName(XMLConstants.NULL_NS_URI, "one"));
      assertEquals(expected, elements);
   }
}
