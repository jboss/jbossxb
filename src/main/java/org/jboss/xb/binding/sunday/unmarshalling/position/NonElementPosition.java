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
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.UnmarshallingContextImpl;
import org.xml.sax.Attributes;

/**
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class NonElementPosition extends AbstractPosition
{
   protected NonElementPosition(QName name, ParticleBinding particle, Position next)
   {
      super(name, particle);
      this.particle = particle;
      this.next = next;
   }

   public boolean isElement()
   {
      return false;
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
      Position parentPosition = stack.parent();
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
      Position parentPosition = stack.notSkippedParent(parentIdex);
      if(parentPosition.getValue() != null)
         setParent(parentPosition);
   }

   private void setParent(Position parentPosition)
   {
      if(repeatableParticleValue != null)
      {
         repeatableHandler.addTermValue(repeatableParticleValue, o, qName, particle, parentPosition.getParticle(), handler);
      }
      else if(parentPosition.getRepeatableParticleValue() == null || !parentPosition.getParticle().getTerm().isSkip())
      {
         TermBeforeSetParentCallback beforeSetParent = particle.getTerm().getBeforeSetParentCallback();
         if(beforeSetParent != null)
         {
            UnmarshallingContextImpl ctx = stack.getContext();
            ctx.parent = parentPosition.getValue();
            ctx.particle = particle;
            ctx.parentParticle = stack.notSkippedParent().getParticle();
            o = beforeSetParent.beforeSetParent(o, ctx);
            ctx.clear();
         }
         
         handler.setParent(parentPosition.getValue(), o, qName, particle, parentPosition.getParticle());
      }
      else
         parentPosition.getRepeatableHandler().addTermValue(
               parentPosition.getRepeatableParticleValue(),
               o, qName, particle,
               parentPosition.getParticle(), handler);
   }
   
   public ElementPosition startParticle(QName startName, Attributes atts)
   {
      if (nextPosition(startName, atts) == null)
         return null;

      // push all except the last one
      Object value = o;
      Position newPosition = next;
      while (newPosition.getNext() != null)
      {
         if (newPosition.getParticle().isRepeatable())
            newPosition.startRepeatableParticle(value);

         stack.push(newPosition);
         value = newPosition.initValue(value, atts);
         newPosition.setParentType(parentType);
         newPosition = newPosition.getNext();
      }

      newPosition.setParentType(parentType);
      return (ElementPosition) newPosition;
   }

   protected void nextNotFound()
   {
      endParticle();
      if(particle.isRepeatable() && repeatableParticleValue != null)
         endRepeatableParticle(stack.parent());

      next = null;
      occurrence = 0;
   }
}