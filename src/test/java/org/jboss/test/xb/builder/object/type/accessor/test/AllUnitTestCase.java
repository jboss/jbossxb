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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.accessor.support.All;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * AllUnitTestCase.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class AllUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(AllUnitTestCase.class);
   }
   
   public AllUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshal() throws Exception
   {
      All all = unmarshalObject(All.class);
      assertEquals("one", all.getOne());
      assertEquals("two", all.getTwo());
      assertEquals("three", all.getThree());
   }

   @SuppressWarnings("unchecked")
   public void testBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(All.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "all");
      TypeBinding typeBinding = schemaBinding.getType(qName);
      assertNotNull(typeBinding);
      ParticleBinding particle = typeBinding.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof AllBinding);
      AllBinding all = (AllBinding) term;
      Set<QName> elements = new HashSet<QName>();
      Collection<ParticleBinding> particles = all.getParticles();
      for (ParticleBinding p : particles)
      {
         term = p.getTerm();
         assertTrue(term instanceof ElementBinding);
         elements.add(((ElementBinding) term).getQName());
      }
      HashSet<QName> expected = new HashSet();
      expected.add(new QName(XMLConstants.NULL_NS_URI, "one"));
      expected.add(new QName(XMLConstants.NULL_NS_URI, "two"));
      expected.add(new QName(XMLConstants.NULL_NS_URI, "three"));
      assertEquals(expected, elements);
   }
}
