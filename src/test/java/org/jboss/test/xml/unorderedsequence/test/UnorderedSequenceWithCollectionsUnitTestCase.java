/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xml.unorderedsequence.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xml.unorderedsequence.support.UnorderedSequenceWithCollections;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A UnorderedSequenceWithCollections.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class UnorderedSequenceWithCollectionsUnitTestCase extends AbstractBuilderTest
{
   public UnorderedSequenceWithCollectionsUnitTestCase(String name)
   {
      super(name);
   }

   public void testBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(UnorderedSequenceWithCollections.class, true);
      ElementBinding root = schema.getElement(new QName("root"));
      assertNotNull(root);
      TermBinding term = root.getType().getParticle().getTerm();
      assertTrue(term instanceof UnorderedSequenceBinding);
      UnorderedSequenceBinding group = (UnorderedSequenceBinding) term;
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      ParticleBinding p = i.next();
      assertTrue(p.getMaxOccursUnbounded());
      term = p.getTerm();
      assertTrue(term.isElement());
      List<QName> names = new ArrayList<QName>(2);
      names.add(term.getQName());
      p = i.next();
      assertTrue(p.getMaxOccursUnbounded());
      term = p.getTerm();
      assertTrue(term.isElement());
      names.add(term.getQName());
      assertTrue(Arrays.asList(new QName("strings"), new QName("ints")).containsAll(names));
   }
   
   public void testUnmarshalling() throws Exception
   {
      UnorderedSequenceWithCollections root = unmarshalObject(UnorderedSequenceWithCollections.class);
      List<String> strings = root.getStrings();
      assertNotNull(strings);
      assertEquals(4, strings.size());
      assertTrue(Arrays.asList(new String[]{"1", "2", "3", "4"}).containsAll(strings));
      List<Integer> ints = root.getInts();
      assertNotNull(ints);
      assertEquals(4, ints.size());
      assertTrue(Arrays.asList(new Integer[]{1, 2, 3, 4}).containsAll(ints));
   }
}
