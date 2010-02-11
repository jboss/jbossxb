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

import org.jboss.logging.Logger;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.UnmarshallingContextImpl;
import org.xml.sax.Attributes;

/**
 * A AbstractPosition.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractPosition
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

   protected AbstractPosition previous;
   protected AbstractPosition next;
   protected AbstractPosition notSkippedParent;

   protected boolean skip;
   
   protected AbstractPosition(QName qName, ParticleBinding particle)
   {
      if (particle == null)
         throw new IllegalArgumentException("Null particle");
      
      if(qName == null)
         throw new IllegalArgumentException("Null qName");
      this.qName = qName;

      this.particle = particle;
      this.occurrence = 1;
      
      TermBinding term = particle.getTerm();
      this.skip = term.isSkip();

      if(particle.isRepeatable())
         repeatableHandler = term.getRepeatableHandler();
   }

   public void setStack(PositionStack stack)
   {
      this.stack = stack;
   }

   public ParticleBinding getParticle()
   {
      return particle;
   }

   public AbstractPosition getPrevious()
   {
      return previous;
   }

   public Object getRepeatableParticleValue()
   {
      return repeatableParticleValue;
   }

   public Object getValue()
   {
      return o;
   }

   public boolean isEnded()
   {
      return ended;
   }

   public boolean isElement()
   {
      return false;
   }

   public void endRepeatableParticle()
   {
      if (trace)
         log.trace(" end repeatable " + particle.getTerm());
      repeatableHandler.endRepeatableParticle(previous.o, repeatableParticleValue, qName, particle, previous.particle);
      repeatableParticleValue = null;
   }

   public abstract void endParticle();
   
   public abstract void characters(char[] ch, int start, int length);
   
   public abstract ElementPosition startParticle(QName startName, Attributes atts);

   protected void initValue(Attributes atts)
   {
      if(skip)
      {
         o = previous == null ? null : previous.o;
         return;
      }
      
      if(handler == null)
         handler = getHandler();
      Object parent = previous == null ? null : previous.o;
      o = handler.startParticle(parent, qName, particle, atts, stack.getNamespaceRegistry());
   }

   protected void startRepeatableParticle()
   {
      if(trace)
         log.trace(" start repeatable " + particle.getTerm());

      RepeatableParticleHandler repeatableHandler = particle.getTerm().getRepeatableHandler();
      // the way it is now it's never null
      Object repeatableContainer = repeatableHandler.startRepeatableParticle(previous.o, qName, particle);
      if(repeatableContainer != null)
      {
         if(this.repeatableParticleValue != null)
            throw new IllegalStateException("Previous repeatable particle hasn't been ended yet!");
         this.repeatableParticleValue = repeatableContainer;
      }
   }

   protected AbstractPosition getLastNotSkipped()
   {
      return !skip || repeatableParticleValue != null ? this : notSkippedParent;
   }

   protected void setParent(AbstractPosition parentPosition, ParticleHandler handler)
   {
      if(skip)
         return;
      
      if(repeatableParticleValue != null)
      {
         repeatableHandler.addTermValue(repeatableParticleValue, o, qName, particle, parentPosition.particle, handler);
      }
      else if(parentPosition.repeatableParticleValue == null || !parentPosition.skip)
      {
         TermBeforeSetParentCallback beforeSetParent = particle.getTerm().getBeforeSetParentCallback();
         if (beforeSetParent != null)
         {
            UnmarshallingContextImpl ctx = stack.getContext();
            ctx.parent = parentPosition.o;
            ctx.particle = particle;
            ctx.parentParticle = notSkippedParent.particle;
            o = beforeSetParent.beforeSetParent(o, ctx);
            ctx.clear();
         }

         handler.setParent(parentPosition.o, o, qName, particle, parentPosition.particle);
      }
      else
      {
         parentPosition.repeatableHandler.addTermValue(
               parentPosition.repeatableParticleValue,
               o, qName, particle,
               parentPosition.particle, handler);
      }
   }

   protected abstract ParticleHandler getHandler();
   
   protected abstract void repeatForChild(Attributes atts);
   
   protected abstract AbstractPosition nextPosition(QName startName, Attributes atts);
}
