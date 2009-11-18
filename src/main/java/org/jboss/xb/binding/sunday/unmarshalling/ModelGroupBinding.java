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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class ModelGroupBinding
   extends TermBinding
   implements Cloneable
{
   protected final Logger log = Logger.getLogger(getClass());

   /** The qualifed name for global groups */
   protected QName qName;

   protected boolean requiredParticle;
   protected ParticleHandler handler = DefaultHandlers.MODEL_GROUP_HANDLER;

   protected ModelGroupBinding(SchemaBinding schema)
   {
      super(schema);
   }

   /**
    * Get the qName.
    * 
    * @return the qName.
    */
   public QName getQName()
   {
      return qName;
   }

   /**
    * Set the qName.
    * 
    * @param name the qName.
    * @throws IllegalArgumentException for a null qName
    */
   public void setQName(QName name)
   {
      if (name == null)
         throw new IllegalArgumentException("Null name");
      qName = name;
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
      {
         requiredParticle = true;
      }
   }

   public abstract Collection<ParticleBinding> getParticles();

   public boolean hasRequiredParticle()
   {
      return requiredParticle;
   }

   /**
    * This method is not actually used during parsing. It's here only for internal tests.
    *
    * @param qName an element name
    * @return true if the model group may start with the specified element
    */
   public boolean mayStartWith(QName qName)
   {
      return mayStartWith(qName, Collections.<ModelGroupBinding>emptySet());
   }

   public abstract Cursor newCursor(ParticleBinding particle);

   public Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }

   // Protected

   protected abstract boolean mayStartWith(QName qName, Set<ModelGroupBinding> set);

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
   public abstract class Cursor
   {
      protected final ParticleBinding particle;
      protected final boolean trace = log.isTraceEnabled();
      protected int occurence;

      protected Cursor next;
      
      protected Cursor(ParticleBinding particle)
      {
         this.particle = particle;
      }

      public ParticleBinding getParticle()
      {
         return particle;
      }

      public ModelGroupBinding getModelGroup()
      {
         return (ModelGroupBinding)particle.getTerm();
      }

      public Cursor getNext()
      {
         return next;
      }
      
      public abstract ParticleBinding getCurrentParticle();

      public abstract ElementBinding getElement();

      public abstract boolean isPositioned();

      public ModelGroupBinding.Cursor startElement(QName qName, Attributes attrs)
      {
         return startElement(qName, attrs, Collections.<ModelGroupBinding>emptySet(), true);
      }

      public ElementBinding getElement(QName qName, Attributes attrs, boolean ignoreWildcards)
      {
         return getElement(qName, attrs, Collections.<Cursor>emptySet(), ignoreWildcards);
      }

      public abstract void endElement(QName qName);

      public int getOccurence()
      {
         return occurence;
      }

      public boolean repeatElement(QName qName)
      {
         ParticleBinding particle = getCurrentParticle();
         if(particle.getMaxOccursUnbounded() ||
            occurence < particle.getMinOccurs() ||
            occurence < particle.getMaxOccurs())
         {
            ++occurence;
            return true;
         }
         return false;
      }
      
      public abstract boolean isWildcardContent();
      
      // Protected

      protected abstract ModelGroupBinding.Cursor startElement(QName qName,
            Attributes atts,
            Set<ModelGroupBinding> passedGroups,
            boolean required);

      protected abstract ElementBinding getElement(QName qName, Attributes atts, Set<ModelGroupBinding.Cursor> passedGroups, boolean ignoreWildcards);

      protected ElementBinding getElement(List<ParticleBinding> group, QName qName, Attributes atts, Set<ModelGroupBinding.Cursor> passedGroups, boolean ignoreWildcards)
      {
         ElementBinding element = null;
         for (ParticleBinding nextParticle : group)
         {
            TermBinding item = nextParticle.getTerm();
            if (item.isElement())
            {
               ElementBinding choice = (ElementBinding)item;
               if (qName.equals(choice.getQName()))
               {
                  element = choice;
                  break;
               }
            }
            else if (item.isModelGroup())
            {
               ModelGroupBinding modelGroup = (ModelGroupBinding)item;
               if (passedGroups.contains(modelGroup) == false) // FIX-ME ... weird set usage
               {
                  switch (passedGroups.size())
                  {
                     case 0:
                        passedGroups = Collections.singleton(this);
                        break;
                     case 1:
                        passedGroups = new HashSet<Cursor>(passedGroups);
                     default:
                        passedGroups.add(this);
                  }

                  ElementBinding e = modelGroup.newCursor(nextParticle).getElement(qName, atts, passedGroups, ignoreWildcards);
                  if (e != null)
                  {
                     element = e;
                     if (!qName.equals(e.getQName()))
                     {
                        throw new JBossXBRuntimeException(
                              "There is a bug in ModelGroupBinding.Cursor.getElement(QName,Attributes) impl"
                        );
                     }
                     break;
                  }
               }
            }
            else if (!ignoreWildcards)
            {
               WildcardBinding wildcard = (WildcardBinding)item;
               ElementBinding e = wildcard.getElement(qName, atts);
               if (e != null)
               {
                  element = e;
                  if (!qName.equals(e.getQName()))
                  {
                     throw new JBossXBRuntimeException(
                           "There is a bug in ModelGroupBinding.Cursor.getElement(QName,Attributes) impl"
                     );
                  }
                  break;
               }
            }
         }
         return element;
      }

      protected List<ModelGroupBinding.Cursor> addItem(List<ModelGroupBinding.Cursor> list, ModelGroupBinding.Cursor o)
      {
         switch(list.size())
         {
            case 0:
               list = Collections.singletonList(o);
               break;
            case 1:
               list = new ArrayList<ModelGroupBinding.Cursor>(list);
            default:
               list.add(o);
         }
         return list;
      }
   }
}
