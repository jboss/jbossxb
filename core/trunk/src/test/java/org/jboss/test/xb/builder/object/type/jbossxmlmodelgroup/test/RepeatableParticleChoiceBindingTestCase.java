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

import junit.framework.Test;

import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support.RootWithParticlesChoiceCollection;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A RepeatableParticleChoiceBindingTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RepeatableParticleChoiceBindingTestCase extends AbstractJBossXmlModelGroupTest
{
   public static Test suite()
   {
      return suite(RepeatableParticleChoiceBindingTestCase.class);
   }

   /**
    * Create a new RepeatableParticleChoiceBindingTestCase.
    * 
    * @param name
    */
   public RepeatableParticleChoiceBindingTestCase(String name)
   {
      super(name);
   }

   public void testBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(RootWithParticlesChoiceCollection.class);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding || t instanceof UnorderedSequenceBinding);
      ModelGroupBinding s = (ModelGroupBinding) t;
      Collection<ParticleBinding> particles = s.getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      

      ParticleBinding choiceParticle;
      ParticleBinding elementParticle;
      if(t instanceof SequenceBinding)
      {
         choiceParticle = i.next();
         elementParticle = i.next();
      }
      else
      {
         ParticleBinding p = i.next();
         if (p.getTerm().isElement())
         {
            elementParticle = p;
            choiceParticle = i.next();
         }
         else
         {
            choiceParticle = p;
            elementParticle = i.next();
         }
      }
      
      t = choiceParticle.getTerm();
      assertTrue(t instanceof ChoiceBinding);
      assertEquals(1, choiceParticle.getMaxOccurs());
      assertTrue(choiceParticle.getMaxOccursUnbounded());
      
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
      
      t = elementParticle.getTerm();
      assertTrue(t.isElement());
      assertEquals(new QName("e"), ((ElementBinding)t).getQName());
   }
}
