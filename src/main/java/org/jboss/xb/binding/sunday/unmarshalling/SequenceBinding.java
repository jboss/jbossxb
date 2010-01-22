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

   public AbstractPosition newPosition(QName qName, Attributes attrs, ParticleBinding seqParticle)
   {
      for(int i = 0; i < sequence.size(); ++i)
      {
         ParticleBinding particle = sequence.get(i);
         AbstractPosition next = particle.getTerm().newPosition(qName, attrs, particle);
         if(next != null)
            return new SequencePosition(qName, seqParticle, i, next);
         
         if(particle.isRequired())
            return null;
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

      protected SequencePosition(QName qName, ParticleBinding particle, int pos, AbstractPosition next)
      {
         super(qName, particle, next);
         this.pos = pos;
      }

      public AbstractPosition nextPosition(QName qName, Attributes atts)
      {
         if(trace)
         {
            StringBuffer sb = new StringBuffer();
            sb.append("startElement ").append(qName).append(" in ").append(SequenceBinding.this.toString());
            log.trace(sb.toString());
         }

         for(++pos; pos < sequence.size(); ++pos)
         {
            ParticleBinding particle = sequence.get(pos);
            next = particle.getTerm().newPosition(qName, atts, particle);

            if (next != null)
            {
               next.previous = this;
               return this;
            }

            if (particle.isRequired())
            {
               nextNotFound();
               return null;
            }
         }

         if(particle.isOccurrenceAllowed(occurrence + 1))
         {
            for(pos = 0; pos < sequence.size(); ++pos)
            {
               ParticleBinding item = sequence.get(pos);
               TermBinding term = item.getTerm();
               next = term.newPosition(qName, atts, item);

               if(next != null)
               {
                  next.previous = this;
                  ++occurrence;

                  o = handler.endParticle(o, qName, particle);
                  if(previous.o != null)
                     setParent(previous, handler);
                  initValue(atts);

                  if(trace)
                     log.trace("found " + qName + " in " + SequenceBinding.this);

                  return this;
               }
               
               if (particle.isRequired())
               {
                  nextNotFound();
                  return null;
               }
            }
         }

         if(trace)
            log.trace(qName + " not found in " + SequenceBinding.this);

         nextNotFound();
         return null;
      }
      
      @Override
      protected void nextNotFound()
      {
         super.nextNotFound();
         pos = -1;
      }

      @Override
      protected ParticleHandler getHandler()
      {
         TermBinding term = particle.getTerm();
         ParticleHandler handler = ((ModelGroupBinding)term).getHandler();
         return handler == null ? DefaultHandlers.ELEMENT_HANDLER : handler;
      }
   }
}
