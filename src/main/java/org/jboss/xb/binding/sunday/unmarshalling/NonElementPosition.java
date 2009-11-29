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

import org.xml.sax.Attributes;

/**
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class NonElementPosition extends SundayContentHandler.Position
{
   protected int occurrence;
   protected ParticleBinding currentParticle;
   protected NonElementPosition next;
   
   protected NonElementPosition(QName qName, ParticleBinding particle)
   {
      super(qName, particle);
      this.particle = particle;
   }

   protected NonElementPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle)
   {
      this(name, particle);
      this.currentParticle = currentParticle;
      occurrence = 1; 
   }

   protected NonElementPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle, NonElementPosition next)
   {
      this(name, particle);
      this.currentParticle = currentParticle;
      this.next = next;
      occurrence = 1;
   }

   protected boolean isElement()
   {
      return false;
   }
   
   protected boolean isModelGroup()
   {
      return true;
   }

   public ParticleBinding getParticle()
   {
      return particle;
   }

   public NonElementPosition getNext()
   {
      return next;
   }
   
   public ParticleBinding getCurrentParticle()
   {
      return currentParticle;
   }

   public NonElementPosition startElement(QName qName, Attributes attrs)
   {
      return startElement(qName, attrs, true);
   }

   public boolean repeatTerm(QName qName, Attributes atts)
   {
      if(currentParticle == null)
         throw new IllegalStateException("The cursor has not been positioned yet!");
      
      boolean repeated = false;
      if(currentParticle.getMaxOccursUnbounded() ||
         occurrence < currentParticle.getMinOccurs() ||
         occurrence < currentParticle.getMaxOccurs())
      {
         TermBinding item = currentParticle.getTerm();
         if(item.isElement())
         {
            ElementBinding element = (ElementBinding)item;
            repeated = qName.equals(element.getQName());
         }
         else
         {
            NonElementTermBinding ne = (NonElementTermBinding)item;
            next = ne.newPosition(qName, atts, currentParticle);
            repeated = next != null;
         }
      }

      if(repeated)
      {
         ++occurrence;
      }
      else
      {
         currentParticle = null;
         occurrence = 0;
      }

      return repeated;
   }

   protected abstract NonElementPosition startElement(QName qName, Attributes atts, boolean required);
}