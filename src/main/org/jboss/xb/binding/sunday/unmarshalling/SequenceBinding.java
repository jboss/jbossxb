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
import java.util.Collection;
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

   public SequenceBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ElementBinding getArrayItem()
   {
      return arrayItem;
   }

   public void addParticle(ParticleBinding particle)
   {
      switch(sequence.size())
      {
         case 0:
            sequence = Collections.singletonList(particle);
            if(particle.isRepeatable() && particle.getTerm() instanceof ElementBinding)
            {
               ElementBinding element = (ElementBinding)particle.getTerm();
               if(particle.isRepeatable())
               {
                  arrayItem = element;
               }
            }
            break;
         case 1:
            sequence = new ArrayList(sequence);
            arrayItem = null;
         default:
            sequence.add(particle);
      }
      super.addParticle(particle);
   }

   public Collection getParticles()
   {
      return Collections.unmodifiableCollection(sequence);
   }

   public Cursor newCursor(ParticleBinding particle)
   {
      return new Cursor(particle)
      {
         private int pos = -1;
         private ElementBinding element;
         private int occurence;

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

            if(log.isTraceEnabled())
            {
               log.trace("endElement " + qName + " in " + getModelGroup());
            }
         }

         public int getOccurence()
         {
            return occurence;
         }

         protected List startElement(QName qName, Attributes atts, Set passedGroups, List groupStack, boolean required)
         {
            if(log.isTraceEnabled())
            {
               StringBuffer sb = new StringBuffer();
               sb.append("startElement " + qName + " in " + getModelGroup() + ", " + sequence.size() + ": ");

               for(int i = 0; i < sequence.size(); ++i)
               {
                  ParticleBinding particle = (ParticleBinding)sequence.get(i);
                  Object o = particle.getTerm();
                  if(o instanceof ElementBinding)
                  {
                     sb.append(((ElementBinding)o).getQName());
                  }
                  else if(o instanceof SequenceBinding)
                  {
                     sb.append("sequence");
                  }
                  else if(o instanceof ChoiceBinding)
                  {
                     sb.append("choice");
                  }
                  else if(o instanceof AllBinding)
                  {
                     sb.append("all");
                  }

                  sb.append(" ");
               }
               sb.append("]");
               log.trace(sb.toString());
            }

            int i = pos;
            if(pos >= 0)
            {
               ParticleBinding particle = getCurrentParticle();
               if(particle.getMaxOccursUnbounded() ||
                  occurence < particle.getMinOccurs() ||
                  occurence < particle.getMaxOccurs())
               {
                  --i;
               }
            }

            // i update pos only if the element has been found, though it seems to be irrelevant
            // since the cursor is going to be thrown away in case the element has not been found
            while(i < sequence.size() - 1)
            {
               ParticleBinding particle = (ParticleBinding)sequence.get(++i);
               Object item = particle.getTerm();
               if(item instanceof ElementBinding)
               {
                  ElementBinding element = (ElementBinding)item;
                  if(qName.equals(element.getQName()))
                  {
                     if(pos == i)
                     {
                        ++occurence;
                     }
                     else
                     {
                        pos = i;
                        occurence = 1;
                     }
                     groupStack = addItem(groupStack, this);
                     this.element = element;

                     if(log.isTraceEnabled())
                     {
                        log.trace("found " + qName + " in " + getModelGroup());
                     }
                     break;
                  }

                  if(i != pos && particle.getMinOccurs() > 0)
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

                     int groupStackSize = groupStack.size();
                     groupStack = modelGroup.newCursor(particle).startElement(
                        qName, atts, passedGroups, groupStack, particle.isRequired(occurence)
                     );

                     if(groupStackSize != groupStack.size())
                     {
                        if(pos != i)
                        {
                           pos = i;
                           occurence = 1;
                        }
                        else
                        {
                           ++occurence;
                        }
                        groupStack = addItem(groupStack, this);
                        element = null;
                        break;
                     }

                     if(i != pos && particle.isRequired())
                     {
                        if(required)
                        {
                           throw new JBossXBRuntimeException("Requested element " + qName +
                              " is not allowed in this position in the sequence. A model group with minOccurs=" +
                              particle.getMinOccurs() + " that doesn't contain this element must follow."
                           );
                        }
                        else
                        {
                           break;
                        }
                     }
                  }
                  else if(i != pos && particle.isRequired())
                  {
                     if(required)
                     {
                        throw new JBossXBRuntimeException("Requested element " + qName +
                           " is not allowed in this position in the sequence. A model group with minOccurs=" +
                           particle.getMinOccurs() + " that doesn't contain this element must follow."
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
                        occurence = 1;
                     }
                     else
                     {
                        ++occurence;
                     }
                     groupStack = addItem(groupStack, this);
                     break;
                  }

                  if(i != pos && particle.getMinOccurs() > 0)
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

            if(log.isTraceEnabled() && i == sequence.size())
            {
               log.trace(qName + " not found in " + getModelGroup());
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
         ParticleBinding particle = (ParticleBinding)sequence.get(i);
         Object item = particle.getTerm();
         if(item instanceof ElementBinding)
         {
            ElementBinding element = (ElementBinding)item;
            if(qName.equals(element.getQName()))
            {
               result = true;
               break;
            }

            if(particle.getMinOccurs() > 0)
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

               if(result || particle.getMinOccurs() > 0)
               {
                  break;
               }
            }
            else if(particle.getMinOccurs() > 0)
            {
               break;
            }
         }
      }
      return result;
   }
}
