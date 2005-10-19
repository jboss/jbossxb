/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface GenericValueContainer
{
   class FACTORY
   {
      public static GenericValueContainer array(final Class itemClass)
      {
         return new GenericValueContainer()
         {
            private final Class itemType = itemClass;
            private final List items = new ArrayList();

            public void addChild(QName name, Object value)
            {
               items.add(value);
            }

            public Object instantiate()
            {
               Object arr = Array.newInstance(itemType, items.size());
               for(int i = 0; i < items.size(); ++i)
               {
                  Array.set(arr, i, items.get(i));
               }
               return arr;
            }

            public Class getTargetClass()
            {
               // this method should only be called for multidimansional arrays
               // todo: what's the best way to get a class for array having the item class?
               return Array.newInstance(itemType, 0).getClass();
            }
         };
      }
   }

   void addChild(QName name, Object value);

   Object instantiate();

   Class getTargetClass();
}
