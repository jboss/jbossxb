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
package org.jboss.xb.binding.sunday.unmarshalling.position;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.xml.sax.Attributes;

/**
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class NonElementPosition extends AbstractPosition
{
   protected ParticleBinding currentParticle;

   protected NonElementPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle)
   {
      super(name, particle);
      this.particle = particle;
      this.currentParticle = currentParticle;
   }

   protected NonElementPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle, Position next)
   {
      this(name, particle, currentParticle);
      this.currentParticle = currentParticle;
      this.next = next;
   }

   public boolean isElement()
   {
      return false;
   }
   
   public boolean isModelGroup()
   {
      return true;
   }
   
   public ParticleBinding getCurrentParticle()
   {
      return currentParticle;
   }

   public void setCurrentParticle(ParticleBinding currentParticle)
   {
      this.currentParticle = currentParticle;
   }
   
   public void characters(char[] ch, int start, int length)
   {
   }
   
   public void endParticle()
   {
      if(ended)
         throw new JBossXBRuntimeException("The position has already been ended!");
      
      o = handler.endParticle(o, qName, particle);
      ended = true;
      
      // model group should always have parent particle
      Position parentPosition = stack.peek1();
      if(parentPosition.getValue() != null)
         setParent(parentPosition);
   }
   
   public void endParticle(int parentIdex)
   {
      if(ended)
         throw new JBossXBRuntimeException("The position has already been ended!");

      o = handler.endParticle(o, qName, particle);
      ended = true;

      // model group should always have parent particle
      Position parentPosition = stack.getNotSkippedParent(parentIdex);
      if(parentPosition.getValue() != null)
         setParent(parentPosition);
   }

   private void setParent(Position parentPosition)
   {
      if(parentPosition.getRepeatableParticleValue() == null)
      {
         TermBeforeSetParentCallback beforeSetParent = particle.getTerm().getBeforeSetParentCallback();
         if(beforeSetParent != null)
         {
            stack.ctx.parent = parentPosition.getValue();
            stack.ctx.particle = particle;
            stack.ctx.parentParticle = stack.getNotSkippedParent().getParticle();
            o = beforeSetParent.beforeSetParent(o, stack.ctx);
            stack.ctx.clear();
         }
         
         handler.setParent(parentPosition.getValue(), o, qName, particle, parentPosition.getParticle());
      }
      else
         parentPosition.getRepeatableHandler().addTermValue(
               parentPosition.getRepeatableParticleValue(),
               o, qName, particle,
               parentPosition.getParticle(), handler);
   }
   
   public Position startParticle(QName startName, Attributes atts)
   {
      ParticleBinding prevParticle = currentParticle;
      Position newPosition = nextPosition(startName, atts);               
      if(newPosition == null)
      {
         if(!ended)
         {
            endParticle();
            
            if(!particle.isRepeatable() && stack.peek1().isElement())
            {
               TermBinding t = particle.getTerm();
               StringBuffer sb = new StringBuffer(250);
               sb.append(startName).append(" cannot appear in this position. Expected content of ")
               .append(((ElementBinding)stack.peek1().getParticle().getTerm()).getQName())
               .append(" is ").append(t);
               throw new JBossXBRuntimeException(sb.toString());
            }
         }
         return this;
      }
      else
      {
         if(currentParticle != prevParticle)
         {
            if(getRepeatableParticleValue() != null &&
                  prevParticle != null && prevParticle.isRepeatable() && prevParticle.getTerm().isModelGroup())
            {
               stack.endRepeatableParticle(this, qName, prevParticle, particle);
            }

            if(newPosition.getNext() != null && currentParticle.isRepeatable() && !currentParticle.getTerm().isElement())
            {
               stack.startRepeatableParticle(this, o, startName, currentParticle);
            }
         }

         // push all except the last one
         Object value = o;
         newPosition = newPosition.getNext();
         while (newPosition.getNext() != null)
         {
            stack.push(newPosition);
            value = newPosition.initValue(value, atts);
            newPosition.setParentType(parentType);
            newPosition = newPosition.getNext();
         }

         newPosition.setParentType(parentType);
         if(!newPosition.isElement())
            throw new IllegalStateException();
         return newPosition;
      }
   }
}