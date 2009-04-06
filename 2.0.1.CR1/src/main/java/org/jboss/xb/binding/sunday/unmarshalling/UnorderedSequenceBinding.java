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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;

/**
 * A UnorderedSequenceBinding.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class UnorderedSequenceBinding extends ModelGroupBinding
{
   private Map<QName, ParticleBinding> elementParticles = Collections.emptyMap();
   private List<ParticleBinding> groupParticles = Collections.emptyList();
   private List<ParticleBinding> wildcardParticles = Collections.emptyList();
   private ElementBinding arrayItem;
   private List<ParticleBinding> allParticles = null;

   public UnorderedSequenceBinding(SchemaBinding schema)
   {
      super(schema);
   }

   @Override
   public ElementBinding getArrayItem()
   {
      return arrayItem;
   }

   @Override
   public void addParticle(ParticleBinding particle)
   {
      TermBinding term = particle.getTerm();
      if(term.isElement())
      {
         if(elementParticles.isEmpty())
            elementParticles = new HashMap<QName, ParticleBinding>();
         elementParticles.put(((ElementBinding)term).getQName(), particle);
      }
      else if(term.isModelGroup())
      {
         if(groupParticles.isEmpty())
            groupParticles = new ArrayList<ParticleBinding>();
         groupParticles.add(particle);
      }
      else if(term.isWildcard())
      {
         if(wildcardParticles.isEmpty())
            wildcardParticles = new ArrayList<ParticleBinding>();
         wildcardParticles.add(particle);
      }
      else
         throw new JBossXBRuntimeException("Unexpected term type: " + term);
      super.addParticle(particle);
      allParticles = null;
   }

   @Override
   public Collection<ParticleBinding> getParticles()
   {
      if(allParticles == null)
      {
         allParticles = new ArrayList<ParticleBinding>(elementParticles.size() + groupParticles.size() + wildcardParticles.size());
         allParticles.addAll(elementParticles.values());
         allParticles.addAll(groupParticles);
         allParticles.addAll(wildcardParticles);
         allParticles = Collections.unmodifiableList(allParticles);
      }
      return allParticles;
   }

   @Override
   protected boolean mayStartWith(QName name, Set<ModelGroupBinding> set)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Cursor newCursor(ParticleBinding particle)
   {
      return new Cursor(particle)
      {
         private ElementBinding element;
         private ParticleBinding curParticle;
         private int occurence;
         private boolean wildcardContent;

         @Override
         public void endElement(QName name)
         {
            ElementBinding element = getElement();
            if(element == null || !element.getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + (element == null ? "null" : element.getQName().toString())
               );
            }

            if(trace)
               log.trace("endElement " + qName + " in " + getModelGroup());
         }

         @Override
         public ParticleBinding getCurrentParticle()
         {
            if(curParticle == null)
            {
               throw new JBossXBRuntimeException("The cursor in all group has not been positioned yet!");
            }
            return curParticle;
         }

         @Override
         public ElementBinding getElement()
         {
            if(curParticle == null)
            {
               throw new JBossXBRuntimeException("The cursor in all group has not been positioned yet!");
            }
            return element;
         }

         @Override
         protected ElementBinding getElement(QName name, Attributes atts, Set<Cursor> passedGroups, boolean ignoreWildcards)
         {
            return getElement((List<ParticleBinding>) getParticles(), name, atts, passedGroups, ignoreWildcards);
         }

         @Override
         public int getOccurence()
         {
            return occurence;
         }

         @Override
         public boolean isPositioned()
         {
            return curParticle != null;
         }

         @Override
         public boolean isWildcardContent()
         {
            return wildcardContent;
         }
         
         @Override
         protected List<Cursor> startElement(QName qName, Attributes atts, Set<Cursor> passedGroups, List<Cursor> groupStack, boolean required)
         {
            if(trace)
            {
               StringBuffer sb = new StringBuffer();
               sb.append("startElement ").append(qName).append(" in ").append(UnorderedSequenceBinding.this.toString());
               log.trace(sb.toString());
            }

            if(curParticle != null &&
                  (curParticle.getMaxOccursUnbounded() || occurence < curParticle.getMinOccurs() || occurence < curParticle.getMaxOccurs()))
            {
               TermBinding term = curParticle.getTerm();
               if(term.isElement() && ((ElementBinding)term).getQName().equals(qName))
               {
                  ++occurence;
                  groupStack = addItem(groupStack, this);
                  if(trace)
                     log.trace("found " + qName + " in " + getModelGroup());
                  return groupStack;
               }
               else if(term.isModelGroup())
               {
                  ModelGroupBinding modelGroup = (ModelGroupBinding)term;
                  if(!passedGroups.contains(modelGroup))
                  {
                     switch(passedGroups.size())
                     {
                        case 0:
                           passedGroups = Collections.singleton((ModelGroupBinding.Cursor)this);
                           break;
                        case 1:
                           passedGroups = new HashSet<ModelGroupBinding.Cursor>(passedGroups);
                        default:
                           passedGroups.add(this);
                     }

                     int groupStackSize = groupStack.size();
                     groupStack = modelGroup.newCursor(curParticle).startElement(
                        qName, atts, passedGroups, groupStack, curParticle.isRequired(occurence)
                     );

                     if(groupStackSize != groupStack.size())
                     {
                        ++occurence;
                        groupStack = addItem(groupStack, this);
                        return groupStack;
                     }
                  }
               }
               // wildcard should probably be checked last even though it is repeatable
               /*
               else
               {
                  WildcardBinding wildcard = (WildcardBinding) term;
                  ElementBinding e = wildcard.getElement(name, atts);
                  if(e != null)
                  {
                     ++occurence;
                     groupStack = addItem(groupStack, this);
                     wildcardContent = true;
                  }
               }*/
            }
            
            wildcardContent = false;
            occurence = 0;

            curParticle = elementParticles.get(qName);
            if (curParticle != null)
            {
               ++occurence;
               element = (ElementBinding) curParticle.getTerm();
               groupStack = addItem(groupStack, this);
               if (trace)
                  log.trace("found " + qName + " in " + getModelGroup());
               return groupStack;
            }

            for (ParticleBinding particle : groupParticles)
            {
               ModelGroupBinding modelGroup = (ModelGroupBinding) particle.getTerm();
               if (!passedGroups.contains(modelGroup))
               {
                  switch (passedGroups.size())
                  {
                     case 0 :
                        passedGroups = Collections.singleton((ModelGroupBinding.Cursor) this);
                        break;
                     case 1 :
                        passedGroups = new HashSet<ModelGroupBinding.Cursor>(passedGroups);
                     default :
                        passedGroups.add(this);
                  }

                  int groupStackSize = groupStack.size();
                  groupStack = modelGroup.newCursor(particle).startElement(qName, atts, passedGroups, groupStack, particle.isRequired(occurence));

                  if (groupStackSize != groupStack.size())
                  {
                     ++occurence;
                     element = null;
                     curParticle = particle;
                     groupStack = addItem(groupStack, this);
                     return groupStack;
                  }
               }
            }

            for (ParticleBinding particle : wildcardParticles)
            {
               WildcardBinding wildcard = (WildcardBinding) particle.getTerm();
               ElementBinding e = wildcard.getElement(qName, atts);
               if (e != null)
               {
                  ++occurence;
                  curParticle = particle;
                  element = e;
                  wildcardContent = true;
                  groupStack = addItem(groupStack, this);
                  return groupStack;
               }
            }
            
            return groupStack;
         }
      };
   }

   @Override
   public String getGroupType()
   {
      return "unordered_sequence";
   }
}
