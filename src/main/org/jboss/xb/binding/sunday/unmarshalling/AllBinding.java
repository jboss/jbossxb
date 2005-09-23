/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
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

   public Cursor newCursor()
   {
      return new Cursor(this)
      {
         private ParticleBinding curElement;

         public ParticleBinding getCurrentParticle()
         {
            if(curElement == null)
            {
               throw new JBossXBRuntimeException("The cursor in all group has not been positioned yet!");
            }
            return curElement;
         }

         public ElementBinding getElement()
         {
            return (ElementBinding)getCurrentParticle().getTerm();
         }

         public void endElement(QName qName)
         {
            if(curElement == null || !getElement().getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + (curElement == null ? null : getElement().getQName())
               );
            }
            elementStatus = ELEMENT_STATUS_FINISHED;
         }

         protected List startElement(QName qName, Attributes atts, Set passedGroups, List groupStack, boolean required)
         {
            ParticleBinding particle = (ParticleBinding)elements.get(qName);
            if(particle != null)
            {
               curElement = particle;
               groupStack = addItem(groupStack, this);
               elementStatus = ELEMENT_STATUS_STARTED;
            }
            else
            {
               log.warn("Element " + qName + " not found in " + elements.keySet());
            }
            return groupStack;
         }

         protected ElementBinding getElement(QName qName, Attributes atts, Set passedGroups)
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
