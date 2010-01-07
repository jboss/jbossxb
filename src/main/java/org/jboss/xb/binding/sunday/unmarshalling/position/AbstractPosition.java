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
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.NoopParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.PositionStack;
import org.jboss.xb.binding.sunday.unmarshalling.RepeatableParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.UnmarshallingContextImpl;
import org.xml.sax.Attributes;

/**
 * A AbstractPosition.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractPosition implements Position
{
   protected static Logger log = Logger.getLogger(AbstractPosition.class);
   protected static boolean trace;
   
   public static void resetTrace()
   {
      trace = log.isTraceEnabled();
   }
   
   protected PositionStack stack;
   protected final QName qName;
   protected ParticleBinding particle;
   protected ParticleHandler handler;
   protected TypeBinding parentType;
   protected Object o;
   protected Object repeatableParticleValue;
   protected RepeatableParticleHandler repeatableHandler;
   protected boolean ended;
   protected int occurrence;

   protected Position previous;
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

   public void setStack(PositionStack stack)
   {
      this.stack = stack;
   }

   public ParticleBinding getParticle()
   {
      return particle;
   }

   public Position getNext()
   {
      return next;
   }

   public Position getPrevious()
   {
      return previous;
   }

   public void setPrevious(Position previous)
   {
      this.previous = previous;
   }
   
   public RepeatableParticleHandler getRepeatableHandler()
   {
      return repeatableHandler;
   }

   public Object getRepeatableParticleValue()
   {
      return repeatableParticleValue;
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

   public void setParentType(TypeBinding parentType)
   {
      this.parentType = parentType;
   }
   
   public boolean isElement()
   {
      return false;
   }

   public void initValue(Attributes atts)
   {
      if(handler == null)
         handler = getHandler(particle.getTerm());
      Object parent = previous == null ? null : previous.getValue();
      o = handler.startParticle(parent, qName, particle, atts, stack.getNamespaceRegistry());
   }

   public void startRepeatableParticle()
   {
      if(trace)
         log.trace(" start repeatable " + particle.getTerm());

      RepeatableParticleHandler repeatableHandler = particle.getTerm().getRepeatableHandler();
      // the way it is now it's never null
      Object repeatableContainer = repeatableHandler.startRepeatableParticle(previous.getValue(), qName, particle);
      if(repeatableContainer != null)
      {
         if(this.repeatableParticleValue != null)
            throw new IllegalStateException("Previous repeatable particle hasn't been ended yet!");
         this.repeatableParticleValue = repeatableContainer;
         this.repeatableHandler = repeatableHandler;
      }
   }

   public void endRepeatableParticle()
   {
      if (trace)
         log.trace(" end repeatable " + particle.getTerm());
      repeatableHandler.endRepeatableParticle(previous.getValue(), repeatableParticleValue, qName, particle, previous.getParticle());
      repeatableParticleValue = null;
      repeatableHandler = null;
   }

   protected Position notSkippedParent()
   {
      Position position = previous;
      Position wildcardPosition = null;
      while(position != null)
      {
         ParticleBinding particle = position.getParticle();
         if(!particle.getTerm().isSkip() || position.getRepeatableParticleValue() != null)
            return position;
         else if(wildcardPosition != null)
            return wildcardPosition;

         if(particle.getTerm().isWildcard())
            wildcardPosition = position;
         position = position.getPrevious();
      }
      return wildcardPosition;
   }

   protected void setParent(Position parentPosition, ParticleHandler handler)
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
            ctx.parentParticle = notSkippedParent().getParticle();
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
