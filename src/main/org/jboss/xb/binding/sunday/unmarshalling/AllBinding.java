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
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AllBinding
   extends ModelGroupBinding
{
   private Map elements = Collections.EMPTY_MAP;

   public void addElement(ElementBinding element)
   {
      switch(elements.size())
      {
         case 0:
            elements = Collections.singletonMap(element.getQName(), element);
            break;
         case 1:
            elements = new HashMap(elements);
         default:
            elements.put(element.getQName(), element);
      }
   }

   public void addModelGroup(ModelGroupBinding modelGroup)
   {
      throw new JBossXBRuntimeException("Model group all may contain only elements!");
   }

   public Cursor newCursor()
   {
      return new Cursor(this)
      {
         private ElementBinding curElement;

         public ParticleBinding getCurrentParticle()
         {
            if(curElement == null)
            {
               throw new JBossXBRuntimeException("The cursor in all group has not been positioned yet!");
            }
            return curElement;
         }

         public void endElement(QName qName)
         {
            if(!curElement.getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + curElement.getQName()
               );
            }
         }

         protected List startElement(QName qName, Set passedGroups, List groupStack, boolean required)
         {
            ElementBinding element = (ElementBinding)elements.get(qName);
            if(element != null)
            {
               curElement = element;
               groupStack = addItem(groupStack, this);
            }
            return groupStack;
         }
      };
   }

   protected boolean mayStartWith(QName qName, Set set)
   {
      return elements.containsKey(qName);
   }
}
