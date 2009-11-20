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
         @Override
         protected Cursor startElement(QName qName, Attributes atts, Set<ModelGroupBinding> passedGroups, boolean required)
         {
            if(trace)
            {
               StringBuffer sb = new StringBuffer();
               sb.append("startElement ").append(qName).append(" in ").append(UnorderedSequenceBinding.this.toString());
               log.trace(sb.toString());
            }

            next = null;
            
            if(currentParticle != null && repeatTerm(qName, atts))
               return this;               

            currentParticle = elementParticles.get(qName);
            if (currentParticle != null)
            {
               occurence = 1;
               if (trace)
                  log.trace("found " + qName + " in " + UnorderedSequenceBinding.this);
               return this;
            }

            for (ParticleBinding particle : groupParticles)
            {
               ModelGroupBinding modelGroup = (ModelGroupBinding) particle.getTerm();
               if (!passedGroups.contains(modelGroup))
               {
                  switch (passedGroups.size())
                  {
                     case 0 :
                        passedGroups = Collections.singleton((ModelGroupBinding) UnorderedSequenceBinding.this);
                        break;
                     case 1 :
                        passedGroups = new HashSet<ModelGroupBinding>(passedGroups);
                     default :
                        passedGroups.add(UnorderedSequenceBinding.this);
                  }

                  next = modelGroup.newCursor(particle).startElement(qName, atts, passedGroups, particle.isRequired(occurence));

                  if (next != null)
                  {
                     occurence = 1;
                     currentParticle = particle;
                     return this;
                  }
               }
            }

            for (ParticleBinding particle : wildcardParticles)
            {
               WildcardBinding wildcard = (WildcardBinding) particle.getTerm();
               wildcardContent = wildcard.getElement(qName, atts);
               if (wildcardContent != null)
               {
                  occurence = 1;
                  currentParticle = particle;
                  return this;
               }
            }
            
            return null;
         }
      };
   }

   @Override
   public String getGroupType()
   {
      return "unordered_sequence";
   }
}
