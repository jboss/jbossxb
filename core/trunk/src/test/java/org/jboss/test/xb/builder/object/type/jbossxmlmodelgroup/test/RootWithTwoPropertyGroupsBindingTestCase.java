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
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithTwoPropertyGroups;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A RootWithTwoPropertyGroupsBindingTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RootWithTwoPropertyGroupsBindingTestCase extends AbstractJBossXmlModelGroupTest
{
   public RootWithTwoPropertyGroupsBindingTestCase(String name)
   {
      super(name);
   }

   public void testBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(RootWithTwoPropertyGroups.class);
      ElementBinding e = schema.getElement(new QName("main-root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding || t instanceof UnorderedSequenceBinding);
      Collection<ParticleBinding> particles = ((ModelGroupBinding)t).getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      
      ParticleBinding group1Particle;
      ParticleBinding group2Particle;
      if(t instanceof SequenceBinding)
      {
         group1Particle = i.next();
         group2Particle = i.next();
      }
      else
      {
         ParticleBinding p = i.next();
         if(((ElementBinding)p.getTerm()).getQName().equals(new QName("group1")))
         {
            group1Particle = p;
            group2Particle = i.next();
         }
         else
         {
            group2Particle = p;
            group1Particle = i.next();
         }
      }
      
      t = group1Particle.getTerm();
      assertTrue(t.isElement());
      assertEquals(0, group1Particle.getMinOccurs());
      assertEquals(1, group1Particle.getMaxOccurs());
      assertFalse(group1Particle.getMaxOccursUnbounded());
      ElementBinding e1 = (ElementBinding) t;
      assertEquals(new QName("group1"), e1.getQName());
      assertPropertiesSequenceBinding((ModelGroupBinding)e1.getType().getParticle().getTerm(), false);
      
      t = group2Particle.getTerm();
      assertTrue(t.isElement());
      assertEquals(0, group2Particle.getMinOccurs());
      assertEquals(1, group2Particle.getMaxOccurs());
      assertFalse(group2Particle.getMaxOccursUnbounded());
      e1 = (ElementBinding) t;
      assertEquals(new QName("group2"), e1.getQName());
      assertPropertiesSequenceBinding((ModelGroupBinding)e1.getType().getParticle().getTerm(), false);
   }
}
