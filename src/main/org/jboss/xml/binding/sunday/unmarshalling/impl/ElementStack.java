/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementStack
{
   public static final StackItem NULL_ITEM = new StackItem(new QName("jbossxb_internal"), null, null, -1);

   public static class StackItem
   {
      public final QName name;
      public final ElementBinding binding;
      public final Object parent;
      public final int startIndex;
      public int endIndex;

      public StackItem(QName name, ElementBinding binding, Object parent, int objectStackIndex)
      {
         this.name = name;
         this.binding = binding;
         this.parent = parent;
         this.startIndex = objectStackIndex;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof StackItem))
         {
            return false;
         }

         final StackItem stackItem = (StackItem)o;

         if(endIndex != stackItem.endIndex)
         {
            return false;
         }
         if(startIndex != stackItem.startIndex)
         {
            return false;
         }
         if(binding != null ? !binding.equals(stackItem.binding) : stackItem.binding != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (binding != null ? binding.hashCode() : 0);
         result = 29 * result + startIndex;
         result = 29 * result + endIndex;
         return result;
      }
   }

   private List list = new ArrayList();

   public void push(StackItem item)
   {
      list.add(item);
   }

   public StackItem pop()
   {
      if(list.isEmpty())
      {
         throw new IllegalStateException("Stack has no elements!");
      }

      return (StackItem)list.remove(list.size() - 1);
   }

   public StackItem peek()
   {
      if(list.isEmpty())
      {
         throw new IllegalStateException("Stack has no elements!");
      }
      return (StackItem)list.get(list.size() - 1);
   }

   public boolean isEmpty()
   {
      return list.isEmpty();
   }
}
