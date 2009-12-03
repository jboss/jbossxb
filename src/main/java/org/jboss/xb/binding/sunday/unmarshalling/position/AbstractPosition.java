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

import org.jboss.logging.Logger;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.NoopParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.RepeatableParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.xml.sax.Attributes;

/**
 * A AbstractPosition.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractPosition implements Position
{
   protected final Logger log = Logger.getLogger(getClass());
   protected boolean trace;
   
   protected final QName qName;
   protected ParticleBinding particle;
   protected ParticleHandler handler;
   protected TypeBinding parentType;
   protected Object o;
   protected Object repeatableParticleValue;
   protected RepeatableParticleHandler repeatableHandler;
   protected boolean ended;
   protected int occurrence;

   protected Position next;

   protected AbstractPosition(QName qName, ParticleBinding particle)
   {
      if (particle == null)
         throw new IllegalArgumentException("Null particle");
      
      if(qName == null)
         throw new IllegalArgumentException("Null qName");
      this.qName = qName;

      this.particle = particle;
      this.occurrence = 1;
   }

   public QName getQName()
   {
      return qName;
   }

   public ParticleBinding getParticle()
   {
      return particle;
   }

   public void setParticle(ParticleBinding particle)
   {
      this.particle = particle;
   }

   public Position getNext()
   {
      return next;
   }

   public void setNext(Position next)
   {
      this.next = next;
   }

   public ParticleHandler getHandler()
   {
      return handler;
   }

   public void setHandler(ParticleHandler handler)
   {
      this.handler = handler;
   }

   public RepeatableParticleHandler getRepeatableHandler()
   {
      return repeatableHandler;
   }

   public void setRepeatableHandler(RepeatableParticleHandler repeatableHandler)
   {
      this.repeatableHandler = repeatableHandler;
   }

   public Object getRepeatableParticleValue()
   {
      return repeatableParticleValue;
   }

   public void setRepeatableParticleValue(Object repeatableParticleValue)
   {
      this.repeatableParticleValue = repeatableParticleValue;
   }

   public Object getValue()
   {
      return o;
   }

   public void setValue(Object value)
   {
      this.o = value;
   }

   public boolean isEnded()
   {
      return ended;
   }

   public void setEnded(boolean ended)
   {
      this.ended = ended;
   }

   public TypeBinding getParentType()
   {
      return parentType;
   }

   public void setParentType(TypeBinding parentType)
   {
      this.parentType = parentType;
   }
   
   public boolean isElement()
   {
      return false;
   }

   public boolean isModelGroup()
   {
      return false;
   }
   
   public void setCurrentParticle(ParticleBinding currentParticle)
   {
      this.particle = currentParticle;
   }

   public ParticleBinding getCurrentParticle()
   {
      return particle;
   }
   
   public boolean repeatTerm(QName qName, Attributes atts)
   {
      ParticleBinding currentParticle = getCurrentParticle();
      if(currentParticle == null)
         throw new IllegalStateException("The cursor has not been positioned yet!");

      boolean repeated = false;
      if(currentParticle.getMaxOccursUnbounded() ||
         occurrence < currentParticle.getMinOccurs() ||
         occurrence < currentParticle.getMaxOccurs())
      {
         TermBinding term = currentParticle.getTerm();
         if(term.isElement())
         {
            ElementBinding element = (ElementBinding)term;
            repeated = qName.equals(element.getQName());
         }
         else
         {
            next = term.newPosition(qName, atts, currentParticle);
            repeated = next != null;
         }
      }

      if(repeated)
      {
         ++occurrence;
      }
      else
      {
         setCurrentParticle(null);
         occurrence = 0;
      }

      return repeated;
   }

   public Position startElement(QName qName, Attributes attrs)
   {
      return startElement(qName, attrs, true);
   }

   protected Position startElement(QName qName, Attributes atts, boolean required)
   {
      throw new UnsupportedOperationException();
   }

   public Object startParticle(Object parent, Attributes atts, NamespaceRegistry nsRegistry)
   {
      if(handler == null)
         handler = getHandler(particle.getTerm());
      o = handler.startParticle(parent, qName, particle, atts, nsRegistry);
      return o;
   }
   
   public void endParticle()
   {
      o = handler.endParticle(o, qName, particle);
      ended = true;
   }
 
   public void reset()
   {
      if(!ended)
         throw new JBossXBRuntimeException("Attempt to reset a particle that has already been reset: " + particle.getTerm());
      ended = false;
      o = null;
   }      

   public void flushIgnorableCharacters()
   {
   }
   
   private ParticleHandler getHandler(TermBinding term)
   {
      ParticleHandler handler = null;
      if(term.isModelGroup())
         handler = ((ModelGroupBinding)term).getHandler();
      else if(term.isWildcard())
         //handler = ((WildcardBinding)term).getWildcardHandler();
         handler = NoopParticleHandler.INSTANCE;
      else
         throw new IllegalArgumentException("Unexpected term " + term);
      return handler == null ? DefaultHandlers.ELEMENT_HANDLER : handler;
   }
}
