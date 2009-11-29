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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.Position;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SequenceBinding
   extends ModelGroupBinding
{
   private List<ParticleBinding> sequence = Collections.emptyList();
   private ElementBinding arrayItem;

   public SequenceBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ElementBinding getArrayItem()
   {
      return arrayItem;
   }

   public void addParticle(ParticleBinding particle)
   {
      switch(sequence.size())
      {
         case 0:
            sequence = Collections.singletonList(particle);
            if(particle.isRepeatable() && particle.getTerm().isElement())
            {
               ElementBinding element = (ElementBinding)particle.getTerm();
               if(particle.isRepeatable())
               {
                  arrayItem = element;
               }
            }
            break;
         case 1:
            sequence = new ArrayList<ParticleBinding>(sequence);
            arrayItem = null;
         default:
            sequence.add(particle);
      }
      super.addParticle(particle);
   }

   public Collection<ParticleBinding> getParticles()
   {
      return sequence;
   }

   public Position newPosition(QName qName, Attributes attrs, ParticleBinding seqParticle)
   {
      for(int i = 0; i < sequence.size(); ++i)
      {
         ParticleBinding particle = sequence.get(i);
         TermBinding term = particle.getTerm();
         if(term.isElement())
         {
            ElementBinding element = (ElementBinding)term;
            if(qName.equals(element.getQName()))
               return new SequencePosition(qName, seqParticle, i, particle);
         }
         else
         {
            NonElementTermBinding ne = (NonElementTermBinding)term;
            Position next = ne.newPosition(qName, attrs, particle);
            if(next != null)
               return new SequencePosition(qName, seqParticle, i, particle, next);
         }
         
         if(particle.isRequired())
         {
/*            StringBuffer sb = new StringBuffer(250);
            sb.append(qName).append(" cannot appear in this position in group ")
            .append(SequenceBinding.this.toString());
            throw new JBossXBRuntimeException(sb.toString());
*/            break;
         }
      }
      
      return null;
   }

   @Override
   public String getGroupType()
   {
      return "sequence";
   }
   
   private final class SequencePosition extends NonElementPosition
   {
      private int pos = -1;

      protected SequencePosition(QName qName, ParticleBinding particle, int pos, ParticleBinding currentParticle)
      {
         super(qName, particle, currentParticle);
         this.pos = pos;
      }

      protected SequencePosition(QName qName, ParticleBinding particle, int pos, ParticleBinding currentParticle, Position next)
      {
         super(qName, particle, currentParticle, next);
         this.pos = pos;
      }

      protected NonElementPosition startElement(QName qName, Attributes atts, boolean required)
      {
         if(trace)
         {
            StringBuffer sb = new StringBuffer();
            sb.append("startElement ").append(qName).append(" in ").append(SequenceBinding.this.toString());
            log.trace(sb.toString());
         }

         next = null;

         // if positioned try repeating
         if(currentParticle != null && repeatTerm(qName, atts))
            return this;
         
         // this will be the first occurrence
         
         int i = pos;
         while(i < sequence.size() - 1)
         {
            ParticleBinding particle = sequence.get(++i);
            TermBinding item = particle.getTerm();
            if(item.isElement())
            {
               ElementBinding element = (ElementBinding)item;
               if(qName.equals(element.getQName()))
               {
                  pos = i;
                  occurrence = 1;
                  currentParticle = particle;

                  if(trace)
                     log.trace("found " + qName + " in " + SequenceBinding.this);
                  return this;
               }

               if(particle.getMinOccurs() > 0)
               {
                  if(required)
                  {
                     StringBuffer sb = new StringBuffer(250);
                     sb.append(qName).append(" cannot appear in this position in group ")
                     .append(SequenceBinding.this.toString());
                     throw new JBossXBRuntimeException(sb.toString());
                  }
                  else
                  {
                     break;
                  }
               }
            }
            else
            {
               NonElementTermBinding ne = (NonElementTermBinding) item;
               next = ne.newPosition(qName, atts, particle);

               if (next != null)
               {
                  pos = i;
                  occurrence = 1;
                  currentParticle = particle;
                  return this;
               }

               if (particle.isRequired())
               {
                  if (required)
                  {
                     throw new JBossXBRuntimeException("Requested element " + qName
                           + " is not allowed in this position in the sequence. A model group with minOccurs="
                           + particle.getMinOccurs() + " that doesn't contain this element must follow.");
                  }
                  else
                  {
                     break;
                  }
               }
            }
         }

         if(trace && i == sequence.size())
            log.trace(qName + " not found in " + SequenceBinding.this);

         return null;
      }
   }
}
