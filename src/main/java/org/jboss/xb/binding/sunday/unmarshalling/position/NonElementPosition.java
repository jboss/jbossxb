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
      next.setPrevious(this);
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
      
      if(previous.getValue() != null)
         setParent(previous, handler);
      
      if(repeatableParticleValue != null)
         endRepeatableParticle();
   }

   public void repeatForChild(Attributes atts)
   {
      if(ended)
         throw new JBossXBRuntimeException("The position has already been ended!");

      o = handler.endParticle(o, qName, particle);

      // model group should always have parent particle
      Position parentPosition = notSkippedParent();
      if(parentPosition.getValue() != null)
         setParent(parentPosition, handler);

      // if it is repeatable then this is the repeatable parent
      if(!particle.isRepeatable())
         previous.repeatForChild(atts);
      
      initValue(atts);
   }
   
   public ElementPosition startParticle(QName startName, Attributes atts)
   {
      if (nextPosition(startName, atts) == null)
         return null;

      // push all except the last one
      Position nextPosition = next;
      while (nextPosition.getNext() != null)
      {
         if (nextPosition.getParticle().isRepeatable())
            nextPosition.startRepeatableParticle();

         nextPosition.setStack(stack);
         nextPosition.initValue(atts);
         nextPosition.setParentType(parentType);
         nextPosition = nextPosition.getNext();
      }

      nextPosition.setStack(stack);
      nextPosition.setParentType(parentType);
      return (ElementPosition) nextPosition;
   }

   protected void nextNotFound()
   {
      endParticle();
      next = null;
      occurrence = 0;
   }
}