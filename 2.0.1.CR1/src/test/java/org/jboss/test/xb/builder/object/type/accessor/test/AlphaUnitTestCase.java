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
import org.jboss.test.xb.builder.object.type.accessor.support.Alpha;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * AlphaUnitTestCase.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class AlphaUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(AlphaUnitTestCase.class);
   }
   
   public AlphaUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshal() throws Exception
   {
      Alpha alpha = unmarshalObject(Alpha.class);
      assertEquals("one", alpha.getOne());
      assertEquals("two", alpha.getTwo());
      assertEquals("three", alpha.getThree());
   }

   @SuppressWarnings("unchecked")
   public void testBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(Alpha.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "alpha");
      TypeBinding typeBinding = schemaBinding.getType(qName);
      assertNotNull(typeBinding);
      ParticleBinding particle = typeBinding.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding || term instanceof UnorderedSequenceBinding);
      ModelGroupBinding sequence = (ModelGroupBinding) term;
      List<QName> elements = new ArrayList<QName>();
      Collection<ParticleBinding> particles = sequence.getParticles();
      for (ParticleBinding p : particles)
      {
         term = p.getTerm();
         assertTrue(term instanceof ElementBinding);
         elements.add(((ElementBinding) term).getQName());
      }
      ArrayList<QName> expected = new ArrayList<QName>();
      expected.add(new QName(XMLConstants.NULL_NS_URI, "one"));
      expected.add(new QName(XMLConstants.NULL_NS_URI, "three"));
      expected.add(new QName(XMLConstants.NULL_NS_URI, "two"));
      
      if(sequence instanceof SequenceBinding)
         assertEquals(expected, elements);
      else
      {
         assertTrue(sequence instanceof UnorderedSequenceBinding);
         assertEquals(expected.size(), elements.size());
         assertTrue(expected.containsAll(elements));
      }
   }
}
