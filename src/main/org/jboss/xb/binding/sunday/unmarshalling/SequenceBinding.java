/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SequenceBinding
   extends ModelGroupBinding
{
   private List sequence = Collections.EMPTY_LIST;
   private ElementBinding arrayItem;

   public ElementBinding getArrayItem()
   {
      return arrayItem;
   }

   public void addElement(ElementBinding element)
   {
      addItem(element);
   }

   public void addModelGroup(ModelGroupBinding modelGroup)
   {
      addItem(modelGroup);
   }

   public void setWildcard(WildcardBinding binding)
   {
      addItem(binding);
   }

   public Cursor newCursor()
   {
      return new Cursor(this)
      {
         private int pos = -1;
         private ElementBinding element;
         private int occurs;

         public ParticleBinding getCurrentParticle()
         {
            if(pos < 0)
            {
               throw new JBossXBRuntimeException(
                  "The cursor has not been positioned yet! startElement should be called."
               );
            }
            return (ParticleBinding)sequence.get(pos);
         }

         public ElementBinding getElement()
         {
            if(pos < 0)
            {
               throw new JBossXBRuntimeException(
                  "The cursor has not been positioned yet! startElement should be called."
               );
            }
            return element;
         }

         public void endElement(QName qName)
         {
            if(element == null || !element.getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + (element == null ? "null" : element.getQName().toString())
               );
            }
            elementStatus = ELEMENT_STATUS_FINISHED;
         }

         protected List startElement(QName qName, Attributes atts, Set passedGroups, List groupStack, boolean required)
         {
            int i = pos;
            if(pos >= 0)
            {
               ParticleBinding particle = getCurrentParticle();
               if(particle.getMaxOccursUnbounded() ||
                  occurs < particle.getMinOccurs() ||
                  occurs < particle.getMaxOccurs())
               {
                  --i;
               }
            }

            // i update pos only if the element has been found, though it seems to be irrelevant
            // since the cursor is going to be thrown away in case the element has not been found
            while(i < sequence.size() - 1)
            {
               Object item = sequence.get(++i);
               if(item instanceof ElementBinding)
               {
                  ElementBinding element = (ElementBinding)item;
                  if(qName.equals(element.getQName()))
                  {
                     if(pos == i)
                     {
                        ++occurs;
                     }
                     else
                     {
                        pos = i;
                        occurs = 1;
                     }
                     groupStack = addItem(groupStack, this);
                     this.element = element;
                     elementStatus = ELEMENT_STATUS_STARTED;
                     break;
                  }

                  if(i != pos && element.getMinOccurs() > 0)
                  {
                     if(required)
                     {
                        throw new JBossXBRuntimeException("Requested element " + qName +
                           " is not allowed in this position in the sequence. The next element should be " +
                           element.getQName()
                        );
                     }
                     else
                     {
                        break;
                     }
                  }
               }
               else if(item instanceof ModelGroupBinding)
               {
                  ModelGroupBinding modelGroup = (ModelGroupBinding)item;
                  if(!passedGroups.contains(modelGroup))
                  {
                     switch(passedGroups.size())
                     {
                        case 0:
                           passedGroups = Collections.singleton(this);
                           break;
                        case 1:
                           passedGroups = new HashSet(passedGroups);
                        default:
                           passedGroups.add(this);
                     }

                     groupStack = modelGroup.newCursor().startElement(
                        qName, atts, passedGroups, groupStack, modelGroup.getMinOccurs() > 0
                     );

                     if(!groupStack.isEmpty())
                     {
                        if(pos != i)
                        {
                           pos = i;
                           occurs = 1;
                        }
                        else
                        {
                           ++occurs;
                        }
                        groupStack = addItem(groupStack, this);
                        element = null;
                        break;
                     }

                     if(i != pos && modelGroup.getMinOccurs() > 0)
                     {
                        if(required)
                        {
                           throw new JBossXBRuntimeException("Requested element " + qName +
                              " is not allowed in this position in the sequence. A model group with minOccurs=" +
                              modelGroup.getMinOccurs() + " that doesn't contain this element must follow."
                           );
                        }
                        else
                        {
                           break;
                        }
                     }
                  }
                  else if(i != pos && modelGroup.getMinOccurs() > 0)
                  {
                     if(required)
                     {
                        throw new JBossXBRuntimeException("Requested element " + qName +
                           " is not allowed in this position in the sequence. A model group with minOccurs=" +
                           modelGroup.getMinOccurs() + " that doesn't contain this element must follow."
                        );
                     }
                     else
                     {
                        break;
                     }
                  }
               }
               else if(item instanceof WildcardBinding)
               {
                  WildcardBinding wildcard = (WildcardBinding)item;
                  element = wildcard.getElement(qName, atts);
                  if(element != null)
                  {
                     if(pos != i)
                     {
                        pos = i;
                        occurs = 1;
                     }
                     else
                     {
                        ++occurs;
                     }
                     groupStack = addItem(groupStack, this);
                     elementStatus = ELEMENT_STATUS_STARTED;
                     break;
                  }

                  if(i != pos && wildcard.getMinOccurs() > 0)
                  {
                     if(required)
                     {
                        throw new JBossXBRuntimeException("Requested element " + qName +
                           " is not allowed in this position in the sequence."
                        );
                     }
                     else
                     {
                        break;
                     }
                  }
               }
            }

            return groupStack;
         }

         protected ElementBinding getElement(QName qName, Attributes atts, Set passedGroups)
         {
            return getElement(sequence, qName, atts, passedGroups);
         }
      };
   }

   protected boolean mayStartWith(QName qName, Set set)
   {
      boolean result = false;
      for(int i = 0; i < sequence.size(); ++i)
      {
         Object item = sequence.get(i);
         if(item instanceof ElementBinding)
         {
            ElementBinding element = (ElementBinding)item;
            if(qName.equals(element.getQName()))
            {
               result = true;
               break;
            }

            if(element.getMinOccurs() > 0)
            {
               break;
            }
         }
         else if(item instanceof ModelGroupBinding)
         {
            ModelGroupBinding modelGroup = (ModelGroupBinding)item;
            if(!set.contains(modelGroup))
            {
               switch(set.size())
               {
                  case 0:
                     set = Collections.singleton(this);
                     break;
                  case 1:
                     set = new HashSet(set);
                  default:
                     set.add(this);
               }

               result = modelGroup.mayStartWith(qName, set);

               if(result || modelGroup.getMinOccurs() > 0)
               {
                  break;
               }
            }
            else if(modelGroup.getMinOccurs() > 0)
            {
               break;
            }
         }
      }
      return result;
   }

   // Private

   private void addItem(ParticleBinding o)
   {
      switch(sequence.size())
      {
         case 0:
            sequence = Collections.singletonList(o);
            if(o instanceof ElementBinding)
            {
               ElementBinding element = (ElementBinding)o;
               if(element.isMultiOccurs())
               {
                  arrayItem = element;
               }
            }
            break;
         case 1:
            sequence = new ArrayList(sequence);
            arrayItem = null;
         default:
            sequence.add(o);
      }
   }
}
