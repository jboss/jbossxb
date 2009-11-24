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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class ModelGroupBinding
   extends TermBinding
{
   protected final Logger log = Logger.getLogger(getClass());

   protected boolean requiredParticle;
   protected ParticleHandler handler = DefaultHandlers.MODEL_GROUP_HANDLER;

   protected ModelGroupBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ParticleHandler getHandler()
   {
      return handler;
   }

   public void setHandler(ParticleHandler handler)
   {
      this.handler = handler;
   }

   public abstract ElementBinding getArrayItem();

   /**
    * Model group that is passed in as an argument to this method must be fully populated with
    * element, wildcard and child model group bindings.
    *
    * @param particle the particle
    */
   public void addParticle(ParticleBinding particle)
   {
      if(particle.isRequired())
         requiredParticle = true;
   }

   public abstract Collection<ParticleBinding> getParticles();

   public boolean hasRequiredParticle()
   {
      return requiredParticle;
   }

   public abstract ModelGroupPosition newPosition(QName qName, Attributes attrs, ParticleBinding particle);

   public ElementBinding getElement(QName qName, Attributes attrs, boolean ignoreWildcards)
   {
      ElementBinding element = null;
      for (ParticleBinding nextParticle : getParticles())
      {
         TermBinding item = nextParticle.getTerm();
         if (item.isElement())
         {
            ElementBinding choice = (ElementBinding)item;
            if (qName.equals(choice.getQName()))
               element = choice;
         }
         else if (item.isModelGroup())
         {
            ModelGroupBinding modelGroup = (ModelGroupBinding) item;
            element = modelGroup.getElement(qName, attrs, ignoreWildcards);
         }
         else if (!ignoreWildcards)
         {
            WildcardBinding wildcard = (WildcardBinding)item;
            element = wildcard.getElement(qName, attrs);
         }
         
         if (element != null)
            break;
      }
      return element;
   }

   public boolean isSkip()
   {
      return skip == null || skip;
   }

   public boolean isModelGroup()
   {
      return true;
   }

   public boolean isWildcard()
   {
      return false;
   }
   
   public boolean isElement()
   {
      return false;
   }

   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append(getGroupType());
      if(qName != null)
         sb.append(' ').append(qName);
      sb.append(':');
      for(ParticleBinding p : getParticles())
      {
         TermBinding t = p.getTerm();
         sb.append(' ');
         if(t.isElement())
            sb.append(((ElementBinding)t).getQName());
         else if(t.isModelGroup())
         {
            sb.append('{').append(((ModelGroupBinding)t).getGroupType());
            ModelGroupBinding group = (ModelGroupBinding) t;
            if(group.getQName() != null)
               sb.append(' ').append(group.getQName());
            sb.append('}');
         }
         else
            sb.append("{wildcard}");

         if(p.getMaxOccursUnbounded())
            sb.append(p.getMinOccurs() == 0 ? '*' : '!');
         else if(p.getMinOccurs() == 0)
            sb.append('?');
      }
      return sb.toString();
   }

   public abstract String getGroupType();

   // Inner
   public abstract class ModelGroupPosition extends SundayContentHandler.Position
   {
      protected final boolean trace = log.isTraceEnabled();
      protected int occurrence;

      protected ParticleBinding currentParticle;
      protected ElementBinding wildcardContent;

      protected ModelGroupPosition next;
      
      protected ModelGroupPosition(QName qName, ParticleBinding particle)
      {
         super(qName, particle);
         this.cursor = this;
         if(particle.getTerm() != ModelGroupBinding.this)
            throw new IllegalStateException("Particle term " + particle.getTerm() + " is not the model group " + ModelGroupBinding.this);
         this.particle = particle;
      }

      protected ModelGroupPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle)
      {
         this(name, particle);
         this.currentParticle = currentParticle;
         occurrence = 1; 
      }

      protected ModelGroupPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle, ModelGroupPosition next)
      {
         this(name, particle);
         this.currentParticle = currentParticle;
         this.next = next;
         occurrence = 1;
      }

      protected ModelGroupPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle, ElementBinding wildcardContent)
      {
         this(name, particle);
         this.currentParticle = currentParticle;
         this.wildcardContent = wildcardContent;
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

      public ModelGroupPosition getNext()
      {
         return next;
      }
      
      public ParticleBinding getCurrentParticle()
      {
         return currentParticle;
      }

      public boolean isWildcardContent()
      {
         return wildcardContent != null;
      }

      public ElementBinding getWildcardContent()
      {
         return wildcardContent;
      }

      public ModelGroupBinding.ModelGroupPosition startElement(QName qName, Attributes attrs)
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
            else if(item.isModelGroup())
            {
               ModelGroupBinding modelGroup = (ModelGroupBinding)item;
               next = modelGroup.newPosition(qName, atts, currentParticle);
               repeated = next != null;
            }
            else if(item.isWildcard())
            {
               WildcardBinding wildcard = (WildcardBinding)item;
               wildcardContent = wildcard.getElement(qName, atts);
               repeated = wildcardContent != null;
            }
         }

         if(repeated)
         {
            ++occurrence;
            if(trace)
               log.trace("repeated " + qName + " in " + ModelGroupBinding.this + ", occurence=" + occurrence + ", term=" + currentParticle.getTerm());
         }
         else
         {
            wildcardContent = null;
            currentParticle = null;
            occurrence = 0;
         }

         return repeated;
      }

      protected abstract ModelGroupBinding.ModelGroupPosition startElement(QName qName, Attributes atts, boolean required);
   }
}
