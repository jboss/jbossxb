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
                  try
                  {
                     Array.set(arr, i, items.get(i));
                  }
                  catch(IllegalArgumentException e)
                  {
                     throw new JBossXBRuntimeException(
                        "Failed to set " + items.get(i) +
                        " as an item of array " + arr);
                  }
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
