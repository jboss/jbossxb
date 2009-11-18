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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ChoiceBinding
   extends ModelGroupBinding
{
   private List<ParticleBinding> choices = Collections.emptyList();

   public ChoiceBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ElementBinding getArrayItem()
   {
      return null;
   }

   public void addParticle(ParticleBinding particle)
   {
      switch(choices.size())
      {
         case 0:
            choices = Collections.singletonList(particle);
            break;
         case 1:
            choices = new ArrayList<ParticleBinding>(choices);
         default:
            choices.add(particle);
      }

      super.addParticle(particle);
   }

   public Collection<ParticleBinding> getParticles()
   {
      return Collections.unmodifiableCollection(choices);
   }

   public Cursor newCursor(ParticleBinding particle)
   {
      return new Cursor(particle)
      {
         private ParticleBinding currentParticle;
         private ElementBinding element;
         private boolean wildcardContent;
         
         public ParticleBinding getCurrentParticle()
         {
            if(currentParticle == null)
               throw new JBossXBRuntimeException("The cursor has not been positioned yet! startElement should be called.");
            return currentParticle;
         }

         public ElementBinding getElement()
         {
            if(currentParticle == null)
               throw new JBossXBRuntimeException("The cursor has not been positioned yet! startElement should be called.");
            return element;
         }

         public boolean isPositioned()
         {
            return currentParticle != null;
         }

         public void endElement(QName qName)
         {
            if(element == null || !element.getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + (element == null ? "null" : element.getQName().toString())
               );
            }

            if(trace)
               log.trace("endElement " + qName + " in " + getModelGroup());
         }

         public boolean isWildcardContent()
         {
            return wildcardContent;
         }

         protected ModelGroupBinding.Cursor startElement(QName qName, Attributes atts, Set<ModelGroupBinding> passedGroups, boolean required)
         {
            if(trace)
            {
               StringBuffer sb = new StringBuffer();
               sb.append("startElement ").append(qName).append(" in ").append(ChoiceBinding.this.toString());
               log.trace(sb.toString());
            }

            next = null;
            
            if(currentParticle != null)
            {
               boolean repeated = false;
               if(currentParticle.getMaxOccursUnbounded() ||
                  occurence < currentParticle.getMinOccurs() ||
                  occurence < currentParticle.getMaxOccurs())
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
                     if(!passedGroups.contains(modelGroup))
                     {
                        switch(passedGroups.size())
                        {
                           case 0:
                              passedGroups = Collections.singleton((ModelGroupBinding)ChoiceBinding.this);
                              break;
                           case 1:
                              passedGroups = new HashSet<ModelGroupBinding>(passedGroups);
                           default:
                              passedGroups.add(ChoiceBinding.this);
                        }

                        boolean isRequired = occurence == 0 ? false : currentParticle.isRequired(occurence);
                        next = modelGroup.newCursor(currentParticle).startElement(qName, atts, passedGroups, isRequired);
                        repeated = next != null;
                     }
                  }
                  else if(item.isWildcard())
                  {
                     WildcardBinding wildcard = (WildcardBinding)item;
                     element = wildcard.getElement(qName, atts);
                     repeated = element != null;
                  }
               }

               if(repeated)
               {
                  ++occurence;
                  if(trace)
                     log.trace("repeated " + qName + " in " + getModelGroup() + ", occurence=" + occurence + ", term=" + currentParticle.getTerm());
                  return this;
               }
               else
               {
                  wildcardContent = false;
                  currentParticle = null;
                  element = null;
                  occurence = 0;
               }
               
               return null;
            }

            for(int i = 0; i < choices.size(); ++i)
            {
               boolean found = false;
               ParticleBinding particle = (ParticleBinding)choices.get(i);
               TermBinding item = particle.getTerm();
               if(item.isElement())
               {
                  ElementBinding element = (ElementBinding)item;
                  if(qName.equals(element.getQName()))
                  {
                     found = true;
                     this.element = element;
                  }
               }
               else if(item.isModelGroup())
               {
                  ModelGroupBinding modelGroup = (ModelGroupBinding)item;
                  if(!passedGroups.contains(modelGroup))
                  {
                     switch(passedGroups.size())
                     {
                        case 0:
                           passedGroups = Collections.singleton((ModelGroupBinding)ChoiceBinding.this);
                           break;
                        case 1:
                           passedGroups = new HashSet<ModelGroupBinding>(passedGroups);
                        default:
                           passedGroups.add(ChoiceBinding.this);
                     }

                     boolean isRequired = occurence == 0 ? false : particle.isRequired(occurence);
                     next = modelGroup.newCursor(particle).startElement(qName, atts, passedGroups, isRequired);
                     found = next != null;
                  }
               }
               else if(item.isWildcard())
               {
                  WildcardBinding wildcard = (WildcardBinding)item;
                  element = wildcard.getElement(qName, atts);
                  if(element != null)
                  {
                     found = true;
                     wildcardContent = true;
                  }
               }
               
               if(found)
               {
                  occurence = 1;
                  currentParticle = particle;
                  if(trace)
                     log.trace("found " + qName + " in " + getModelGroup() + ", term=" + currentParticle.getTerm());
                  return this;
               }
            }

            return null;
         }

         protected ElementBinding getElement(QName qName, Attributes atts, Set<ModelGroupBinding.Cursor> passedGroups, boolean ignoreWildcards)
         {
            return getElement(choices, qName, atts, passedGroups, ignoreWildcards);
         }
      };
   }

   protected boolean mayStartWith(QName qName, Set<ModelGroupBinding> set)
   {
      boolean result = false;
      for(int i = 0; i < choices.size(); ++i)
      {
         ParticleBinding particle = choices.get(i);
         TermBinding item = particle.getTerm();
         if(item.isElement())
         {
            ElementBinding element = (ElementBinding)item;
            if(qName.equals(element.getQName()))
            {
               result = true;
               break;
            }
         }
         else if(item.isModelGroup())
         {
            ModelGroupBinding modelGroup = (ModelGroupBinding)item;
            if(!set.contains(modelGroup))
            {
               switch(set.size())
               {
                  case 0:
                     set = Collections.singleton((ModelGroupBinding)this);
                     break;
                  case 1:
                     set = new HashSet<ModelGroupBinding>(set);
                  default:
                     set.add(this);
               }

               result = modelGroup.mayStartWith(qName, set);

               if(result)
               {
                  break;
               }
            }
         }
         else
         {
            throw new JBossXBRuntimeException("Unexpected item type in model group: " + item);
         }
      }

      return result;
   }

   @Override
   public String getGroupType()
   {
      return "choice";
   }
}
