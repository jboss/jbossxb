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

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.Position;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ChoiceBinding
   extends ModelGroupBinding
{
   private List<ParticleBinding> choices = Collections.emptyList();

   public ChoiceBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ElementBinding getArrayItem()
   {
      return null;
   }

   public void addParticle(ParticleBinding particle)
   {
      switch(choices.size())
      {
         case 0:
            choices = Collections.singletonList(particle);
            break;
         case 1:
            choices = new ArrayList<ParticleBinding>(choices);
         default:
            choices.add(particle);
      }

      super.addParticle(particle);
   }

   public Collection<ParticleBinding> getParticles()
   {
      return choices;
   }

   public Position newPosition(QName qName, Attributes attrs, ParticleBinding choiceParticle)
   {
      for(int i = 0; i < choices.size(); ++i)
      {
         ParticleBinding particle = (ParticleBinding)choices.get(i);
         Position next = particle.getTerm().newPosition(qName, attrs, particle);
         if(next != null)
            return new ChoicePosition(qName, choiceParticle, particle, next);
      }

      return null;
   }

   @Override
   public String getGroupType()
   {
      return "choice";
   }
   
   private final class ChoicePosition extends NonElementPosition
   {
      private ChoicePosition(QName name, ParticleBinding particle, ParticleBinding currentParticle)
      {
         super(name, particle, currentParticle);
      }

      private ChoicePosition(QName name, ParticleBinding particle, ParticleBinding currentParticle, Position next)
      {
         super(name, particle, currentParticle, next);
      }

      protected Position startElement(QName qName, Attributes atts, boolean required)
      {
         if(trace)
         {
            StringBuffer sb = new StringBuffer();
            sb.append("startElement ").append(qName).append(" in ").append(ChoiceBinding.this.toString());
            log.trace(sb.toString());
         }

         next = null;

         if(currentParticle != null)
         {
            if(repeatTerm(qName, atts))
               return this;
            else
               return null;
         }
         
         for(int i = 0; i < choices.size(); ++i)
         {
            ParticleBinding particle = (ParticleBinding)choices.get(i);
            TermBinding term = particle.getTerm();
            next = term.newPosition(qName, atts, particle);

            if(next != null)
            {
               occurrence = 1;
               currentParticle = particle;
               if(trace)
                  log.trace("found " + qName + " in " + ChoiceBinding.this + ", term=" + currentParticle.getTerm());
               return this;
            }
         }

         return null;
      }
   }
}
