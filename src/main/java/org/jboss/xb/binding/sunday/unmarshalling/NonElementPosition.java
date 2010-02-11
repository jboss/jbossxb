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
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;

/**
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class NonElementPosition extends AbstractPosition
{
   protected NonElementPosition(QName name, ParticleBinding particle, AbstractPosition next)
   {
      super(name, particle);
      this.next = next;
      next.previous = this;
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
      
      if(!skip)
      {
         o = handler.endParticle(o, qName, particle);
         ended = true;

         if (previous.o != null)
            setParent(previous, handler);
      }
      
      if(repeatableParticleValue != null)
         endRepeatableParticle();
   }

   public void repeatForChild(Attributes atts)
   {
      if(ended)
         throw new JBossXBRuntimeException("The position has already been ended!");

      if (!skip)
      {
         o = handler.endParticle(o, qName, particle);

         // model group should always have parent particle
         AbstractPosition parentPosition = notSkippedParent;
         if (parentPosition.o != null)
            setParent(parentPosition, handler);
      }
      
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
      AbstractPosition nextPosition = next;
      while (nextPosition.next != null)
      {
         nextPosition.notSkippedParent = nextPosition.previous.getLastNotSkipped();
         if (nextPosition.repeatableHandler != null)
            nextPosition.startRepeatableParticle();
         nextPosition.stack = stack;
         nextPosition.initValue(atts);
         nextPosition.parentType = parentType;
         nextPosition = nextPosition.next;
      }

      nextPosition.stack = stack;
      nextPosition.parentType = parentType;
      nextPosition.notSkippedParent = nextPosition.previous.getLastNotSkipped();
      return (ElementPosition) nextPosition;
   }

   protected void nextNotFound()
   {
      endParticle();
      next = null;
      occurrence = 0;
   }
}