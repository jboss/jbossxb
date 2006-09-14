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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtUtil;

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
         return array(null, null, itemClass);
      }

      public static GenericValueContainer child(final Class childClass)
      {
         return new GenericValueContainer()
         {
            private Object child;
            
            public void addChild(QName name, Object value)
            {
               this.child = value;
            }

            public Class getTargetClass()
            {
               return childClass;
            }

            public Object instantiate()
            {
               return child;
            }
            
         };
      }
      
      public static GenericValueContainer array(final Class wrapperClass,
                                                final String itemProperty,
                                                final Class itemClass)
      {
         return new GenericValueContainer()
         {
            private final Class wrapperType = wrapperClass;
            private final String itemProp = itemProperty;
            private final Class itemType = itemClass;
            private final List items = new ArrayList();

            public void addChild(QName name, Object value)
            {
               items.add(value);
            }

            public Object instantiate()
            {
/* for collected repeatable particles  
               Object arr;
               if(items.isEmpty())
               {
                  arr = Array.newInstance(itemType, 0);
               }
               else
               {
                  java.util.Collection col = (java.util.Collection)items.get(0);
                  arr = Array.newInstance(itemType, col.size());
                  if(itemType.isPrimitive())
                  {
                     int i = 0;
                     for(java.util.Iterator iter = col.iterator(); iter.hasNext();)
                     {
                        Object item = iter.next();
                        try
                        {
                           Array.set(arr, i++, item);
                        }
                        catch(IllegalArgumentException e)
                        {
                           throw new JBossXBRuntimeException("Failed to set " +
                              item +
                              " as an item of array " + arr
                           );
                        }
                     }
                  }
                  else
                  {
                     col.toArray((Object[])arr);
                  }
               }             
*/
               Object arr = Array.newInstance(itemType, items.size());
               for(int i = 0; i < items.size(); ++i)
               {
                  try
                  {
                     Array.set(arr, i, items.get(i));
                  }
                  catch(IllegalArgumentException e)
                  {
                     throw new JBossXBRuntimeException("Failed to set " +
                        items.get(i) +
                        " as an item of array " + arr
                     );
                  }
               }

               Object result = arr;
               // wrap
               if(wrapperType != null)
               {
                  Constructor ctor = null;
                  try
                  {
                     try
                     {
                        ctor = wrapperType.getConstructor(null);
                        result = ctor.newInstance(null);
                        RtUtil.set(result, arr, itemProp, null, false, null);
                     }
                     catch(NoSuchMethodException e)
                     {
                        Constructor[] ctors = wrapperType.getConstructors();
                        for(int i = 0; i < ctors.length; ++i)
                        {
                           Class[] types = ctors[i].getParameterTypes();
                           if(types.length == 1 && types[0].isAssignableFrom(arr.getClass()))
                           {
                              ctor = ctors[i];
                              break;
                           }
                        }

                        if(ctor == null)
                        {
                           throw new JBossXBRuntimeException("Failed to find an appropriate ctor in " +
                              wrapperType +
                              " to wrap " + arr
                           );
                        }

                        result = ctor.newInstance(new Object[]{arr});
                     }
                  }
                  catch(JBossXBRuntimeException e)
                  {
                     throw e;
                  }
                  catch(Exception e)
                  {
                     throw new JBossXBRuntimeException(
                        "Failed to initialize array wrapper " + wrapperType + " for " + arr, e
                     );
                  }
               }
               return result;
            }

            public Class getTargetClass()
            {
               // this method should only be called for multidimansional arrays
               // todo: what's the best way to get a class for array having the item class?
               return Array.newInstance(itemType, 0).getClass();
            }
            
            public String toString()
            {
               return super.toString() + "array";
            }
         };
      }
   }

   void addChild(QName name, Object value);

   Object instantiate();

   Class getTargetClass();
}
