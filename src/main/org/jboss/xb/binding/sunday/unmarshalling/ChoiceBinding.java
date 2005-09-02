/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ChoiceBinding
   extends ModelGroupBinding
{
   private List choices = Collections.EMPTY_LIST;

   public void addElement(ElementBinding element)
   {
      addChoice(element);
   }

   public void addModelGroup(ModelGroupBinding modelGroup)
   {
      addChoice(modelGroup);
   }

   public Cursor newCursor()
   {
      return new Cursor(this)
      {
         private int pos = -1;

         public ParticleBinding getCurrentParticle()
         {
            if(pos < 0)
            {
               throw new JBossXBRuntimeException(
                  "The cursor has not been positioned yet! startElement should be called."
               );
            }
            return (ParticleBinding)choices.get(pos);
         }

         public void endElement(QName qName)
         {
            ElementBinding element = (ElementBinding)getCurrentParticle();
            if(!element.getQName().equals(qName))
            {
               throw new JBossXBRuntimeException("Failed to process endElement for " + qName +
                  " since the current element is " + element.getQName()
               );
            }
         }

         protected List startElement(QName qName, Set passedGroups, List groupStack, boolean required)
         {
            // i update pos only if the element has been found, though it seems to be irrelevant
            // since the cursor is going to be thrown away in case the element has not been found
            int i = pos;
            while(i < choices.size() - 1)
            {
               Object item = choices.get(++i);
               if(item instanceof ElementBinding)
               {
                  ElementBinding element = (ElementBinding)item;
                  if(qName.equals(element.getQName()))
                  {
                     pos = i;
                     groupStack = addItem(groupStack, this);
                     break;
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
                        qName, passedGroups, groupStack, modelGroup.getMinOccurs() > 0
                     );

                     if(!groupStack.isEmpty())
                     {
                        pos = i;
                        groupStack = addItem(groupStack, this);
                        break;
                     }

                     if(modelGroup.getMinOccurs() > 0)
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

            return groupStack;
         }
      };
   }

   protected boolean mayStartWith(QName qName, Set set)
   {
      boolean result = false;
      for(int i = 0; i < choices.size(); ++i)
      {
         Object item = choices.get(i);
         if(item instanceof ElementBinding)
         {
            ElementBinding element = (ElementBinding)item;
            if(qName.equals(element.getQName()))
            {
               result = true;
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

   // Private

   private void addChoice(Object o)
   {
      switch(choices.size())
      {
         case 0:
            choices = Collections.singletonList(o);
            break;
         case 1:
            choices = new ArrayList(choices);
         default:
            choices.add(o);
      }
   }
}
