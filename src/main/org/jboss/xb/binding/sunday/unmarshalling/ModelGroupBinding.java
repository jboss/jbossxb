/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.QName;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class ModelGroupBinding
   implements ParticleBinding
{
   protected int minOccurs;
   protected int maxOccurs;
   protected boolean maxOccursUnbounded;

   public abstract void addElement(ElementBinding element);

   public abstract void addModelGroup(ModelGroupBinding modelGroup);

   public int getMinOccurs()
   {
      return minOccurs;
   }

   public int getMaxOccurs()
   {
      return maxOccurs;
   }

   public boolean getMaxOccursUnbounded()
   {
      return maxOccursUnbounded;
   }

   /**
    * This method is not actually used during parsing. It's here only for internal tests.
    * @param qName  an element name
    * @return  true if the model group may start with the specified element
    */
   public boolean mayStartWith(QName qName)
   {
      return mayStartWith(qName, Collections.EMPTY_SET);
   }

   public abstract Cursor newCursor();

   // Protected

   protected abstract boolean mayStartWith(QName qName, Set set);

   // Inner

   public static abstract class Cursor
   {
      protected final ModelGroupBinding group;

      protected Cursor(ModelGroupBinding theGroup)
      {
         this.group = theGroup;
      }

      public ModelGroupBinding getModelGroup()
      {
         return group;
      }

      public abstract ParticleBinding getCurrentParticle();

      public List startElement(QName qName)
      {
         return startElement(qName, Collections.EMPTY_SET, Collections.EMPTY_LIST, true);
      }

      public abstract void endElement(QName qName);

      // Protected

      protected abstract List startElement(QName qName, Set passedGroups, List groupStack, boolean required);

      protected List addItem(List list, Object o)
      {
         switch(list.size())
         {
            case 0:
               list = Collections.singletonList(o);
               break;
            case 1:
               list = new ArrayList(list);
            default:
               list.add(o);
         }
         return list;
      }
   }
}
