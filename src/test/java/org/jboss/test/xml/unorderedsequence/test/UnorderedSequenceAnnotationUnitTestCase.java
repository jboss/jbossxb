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
package org.jboss.test.xml.unorderedsequence.test;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnorderedSequenceBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A UnorderedSequenceAnnotationUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class UnorderedSequenceAnnotationUnitTestCase extends AbstractBuilderTest
{
   public UnorderedSequenceAnnotationUnitTestCase(String name)
   {
      super(name);
   }

   public void testBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(Root.class);
      ElementBinding e = schema.getElement(new QName("root"));
      assertNotNull(e);
      TermBinding t = e.getType().getParticle().getTerm();
      assertTrue(t instanceof SequenceBinding);
      Collection<ParticleBinding> particles = ((SequenceBinding)t).getParticles();
      assertEquals(3, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      
      t = i.next().getTerm();
      assertTrue(t instanceof SequenceBinding);
      
      t = i.next().getTerm();
      assertTrue(t instanceof SequenceBinding);

      t = i.next().getTerm();
      assertTrue(t instanceof UnorderedSequenceBinding);
   }
   
   public static class BaseGroup
   {
      private String a;
      private String b;
      
      public String getA()
      {
         return a;
      }
      
      public void setA(String a)
      {
         this.a = a;
      }
      
      public String getB()
      {
         return b;
      }
      
      public void setB(String b)
      {
         this.b = b;
      }
   }

   @JBossXmlModelGroup(name="defaultGroup")
   public static class DefaultGroup extends BaseGroup
   {
   }
   
   @JBossXmlModelGroup(name="orderedSequence", kind = JBossXmlConstants.MODEL_GROUP_SEQUENCE)
   public static class OrderedSequence extends BaseGroup
   {
   }
   
   @JBossXmlModelGroup(name="unorderedSequence", kind = JBossXmlConstants.MODEL_GROUP_UNORDERED_SEQUENCE)
   public static class UnorderedSequence extends BaseGroup
   {
   }
   
   @XmlType(propOrder={"defaultGroup", "orderedSequence", "unorderedSequence"})
   public static class Root
   {
      private DefaultGroup defaultGroup;
      private OrderedSequence orderedSequence;
      private UnorderedSequence unorderedSequence;
      
      public DefaultGroup getDefaultGroup()
      {
         return defaultGroup;
      }
      
      public void setDefaultGroup(DefaultGroup defaultGroup)
      {
         this.defaultGroup = defaultGroup;
      }
      
      public OrderedSequence getOrderedSequence()
      {
         return orderedSequence;
      }
      
      public void setOrderedSequence(OrderedSequence orderedSequence)
      {
         this.orderedSequence = orderedSequence;
      }

      public UnorderedSequence getUnorderedSequence()
      {
         return unorderedSequence;
      }
      
      public void setUnorderedSequence(UnorderedSequence unorderedSequence)
      {
         this.unorderedSequence = unorderedSequence;
      }
   }
}
