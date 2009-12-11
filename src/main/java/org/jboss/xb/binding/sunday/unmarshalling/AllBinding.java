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

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.position.NonElementPosition;
import org.jboss.xb.binding.sunday.unmarshalling.position.Position;
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

   public Position newPosition(QName qName, Attributes attrs, ParticleBinding allParticle)
   {
      ParticleBinding particle = elements.get(qName);
      if(particle != null)
      {
         Position next = particle.getTerm().newPosition(qName, attrs, particle);
         return new AllPosition(qName, allParticle, particle, next);
      }

      return null;
   }

   @Override
   public String getGroupType()
   {
      return "all";
   }
   
   private final class AllPosition extends NonElementPosition
   {
      private AllPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle, Position next)
      {
         super(name, particle, currentParticle, next);
      }

      protected Position startElement(QName qName, Attributes atts, boolean required)
      {
         next = null;

/*         if(currentParticle != null)
         {
            if(particle.getMaxOccursUnbounded() ||
               occurrence < particle.getMinOccurs() ||
               occurrence < particle.getMaxOccurs())
            {
               ParticleBinding particle = elements.get(qName);
               if(particle != null)
               {
                  next = particle.getTerm().newPosition(qName, atts, particle);
                  ++occurrence;
                  currentParticle = particle;
                  
                  endParticle();
                  o = initValue(stack.parent().getValue(), atts);
                  ended = false;

                  return this;
               }               
            }

            endParticle();
            if(particle.isRepeatable() && repeatableParticleValue != null)
               endRepeatableParticle(stack.parent());

            currentParticle = null;
            occurrence = 0;
            
            return null;
         }
*/         
         ParticleBinding particle = elements.get(qName);
         if(particle != null)
         {
            next = particle.getTerm().newPosition(qName, atts, particle);
            ++occurrence;
            currentParticle = particle;
            return this;
         }               

         return null;
      }
   }
}
