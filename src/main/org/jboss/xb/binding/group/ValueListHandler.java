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
package org.jboss.xb.binding.group;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.util.Classes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ValueListHandler
{
   ValueListHandler IMMUTABLE = new ValueListHandler()
   {
      public Object newInstance(ValueList valueList)
      {
         Class cls = valueList.getTargetClass();
         Map map = valueList.getNonRequiredValues();

         Collection values = map.values();
         if(values.isEmpty())
         {
            throw new JBossXBRuntimeException("Value list does not contain non-required values.");
         }

         Constructor ctor = null;
         Constructor[] ctors = cls.getConstructors();

         if(ctors == null || ctors.length == 0)
         {
            throw new JBossXBRuntimeException("The class has no declared constructors: " + cls);
         }

         for(int i = 0; i < ctors.length; ++i)
         {
            Class[] types = ctors[i].getParameterTypes();

            if(types == null || types.length == 0)
            {
               throw new IllegalStateException("Found no-arg constructor for immutable " + cls);
            }

            if(types.length == map.size())
            {
               ctor = ctors[i];

               int typeInd = 0;
               Iterator iter = values.iterator();
               while(iter.hasNext())
               {
                  Class type = types[typeInd++];
                  if(type.isPrimitive())
                  {
                     type = Classes.getPrimitiveWrapper(type);
                  }

                  if(!type.isAssignableFrom(iter.next().getClass()))
                  {
                     ctor = null;
                     break;
                  }
               }

               if(ctor != null)
               {
                  break;
               }
            }
         }

         if(ctor == null)
         {
            StringBuffer buf = new StringBuffer();
            buf.append("There is no ctor in ")
               .append(cls)
               .append(" that would take the following arguments:\n");
            int cnt = 0;
            for(Iterator i = values.iterator(); i.hasNext();)
            {
               Object o = i.next();
               buf.append(' ').append(++cnt).append(") ").append(o.getClass()).append(": ").append(o).append('\n');
            }
            throw new IllegalStateException(buf.toString());
         }

         try
         {
            return ctor.newInstance(values.toArray());
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to create immutable instance of " +
               cls +
               " using arguments: "
               + values + ": " + e.getMessage()
            );
         }
      }
   };

   Object newInstance(ValueList valueList);
}
