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
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AllBinding
   extends ModelGroupBinding
{
   //private static final Logger log = Logger.getLogger(AllBinding.class);

   private Map<QName, ParticleBinding> elements = Collections.emptyMap();

   public AllBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ElementBinding getArrayItem()
   {
      return null;
   }

   public void addParticle(ParticleBinding particle)
   {
      if(!particle.getTerm().isElement())
      {
         throw new JBossXBRuntimeException("Model group all may contain only elements!");
      }

      ElementBinding element = (ElementBinding)particle.getTerm();
      switch(elements.size())
      {
         case 0:
            elements = Collections.singletonMap(element.getQName(), particle);
            break;
         case 1:
            elements = new HashMap<QName, ParticleBinding>(elements);
         default:
            elements.put(element.getQName(), particle);
      }
      super.addParticle(particle);
   }

   public Collection<ParticleBinding> getParticles()
   {
      return elements.values();
   }

   public ModelGroupPosition newPosition(QName qName, Attributes attrs, ParticleBinding allParticle)
   {
      ParticleBinding particle = elements.get(qName);
      if(particle != null)
      {
         return new AllPosition(qName, allParticle, particle);
      }               

      return null;
   }

   @Override
   public String getGroupType()
   {
      return "all";
   }
   
   private final class AllPosition extends ModelGroupPosition
   {
      private AllPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle)
      {
         super(name, particle, currentParticle);
      }

      protected ModelGroupBinding.ModelGroupPosition startElement(QName qName, Attributes atts, boolean required)
      {
         if(currentParticle != null && repeatTerm(qName, atts))
            throw new IllegalStateException("maxOccurs in all model group can only be 1: " + qName);

         ParticleBinding particle = elements.get(qName);
         if(particle != null)
         {
            currentParticle = particle;
            occurrence = 1;
            return this;
         }               

         return null;
      }

      protected ElementBinding getElement(QName qName, Attributes atts, Set<ModelGroupBinding.ModelGroupPosition> passedGroups, boolean ignoreWildcards)
      {
         ParticleBinding particle = elements.get(qName);
         return particle == null ? null : (ElementBinding)particle.getTerm();
      }
   }
}
