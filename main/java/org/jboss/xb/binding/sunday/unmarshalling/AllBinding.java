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

import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AllBinding
   extends ModelGroupBinding
{
   private static final Logger log = Logger.getLogger(AllBinding.class);

   private Map elements = Collections.EMPTY_MAP;

   public AllBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ElementBinding getArrayItem()
   {
      return null;
   }

   public void addParticle(ParticleBinding particle)
   {
      if(!(particle.getTerm() instanceof ElementBinding))
      {
         throw new JBossXBRuntimeException("Model group all may contain only elements!");
      }

      ElementBinding element = (ElementBinding)particle.getTerm();
      switch(elements.size())
      {
         case 0:
            elements = Collections.singletonMap(element.getQName(), particle);
            break;
         case 1:
            elements = new HashMap(elements);
         default:
            elements.put(element.getQName(), particle);
      }
      super.addParticle(particle);
   }

   public Collection getParticles()
   {
      return Collections.unmodifiableCollection(elements.values());
   }

   public Cursor newCursor(ParticleBinding particle)
   {
      return new Cursor(particle)
      {
         private ParticleBinding curParticle;
         private int occurence;

         public ParticleBinding getCurrentParticle()
         {
            if(curParticle == null)
            {
               throw new JBossXBRuntimeException("The cursor in all group has not been positioned yet!");
            }
            return curParticle;
         }

         public ElementBinding getElement()
         {
            return (ElementBinding)getCurrentParticle().getTerm();
         }

         public void endElement(QName qName)
         {
            if(curParticle == null || !getElement().getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + (curParticle == null ? null : getElement().getQName())
               );
            }
         }

         public int getOccurence()
         {
            return occurence;
         }

         protected List startElement(QName qName, Attributes atts, Set passedGroups, List groupStack, boolean required)
         {
            ParticleBinding particle = (ParticleBinding)elements.get(qName);
            if(particle != null)
            {
               if(curParticle == particle)
               {
                  ++occurence;
               }
               else
               {
                  curParticle = particle;
                  occurence = 1;
               }
               groupStack = addItem(groupStack, this);
            }
            else
            {
               log.warn("Element " + qName + " not found in " + elements.keySet());
            }
            return groupStack;
         }

         protected ElementBinding getElement(QName qName, Attributes atts, Set passedGroups, boolean ignoreWildcards)
         {
            ParticleBinding particle = (ParticleBinding)elements.get(qName);
            return particle == null ? null : (ElementBinding)particle.getTerm();
         }
      };
   }

   protected boolean mayStartWith(QName qName, Set set)
   {
      return elements.containsKey(qName);
   }
}
