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

import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;

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
}