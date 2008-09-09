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

import org.jboss.logging.Logger;

import java.util.List;
import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class Immutable
{
   private static final Logger log = Logger.getLogger(Immutable.class);

   public final Class<?> cls;

   final List<String> names = new java.util.ArrayList<String>();

   final List<Object> values = new java.util.ArrayList<Object>();

   public Immutable(Class<?> cls)
   {
      this.cls = cls;
      if(log.isTraceEnabled())
      {
         log.trace("created immutable container for " + cls);
      }
   }

   public void addChild(String localName, Object child)
   {
      if(!names.isEmpty() && names.get(names.size() - 1).equals(localName))
      {
         throw new IllegalStateException("Attempt to add duplicate element " +
            localName +
            ": prev value=" +
            values.get(values.size() - 1) +
            ", new value=" +
            child
         );
      }
      names.add(localName);
      values.add(child);

      if(log.isTraceEnabled())
      {
         log.trace("added child " + localName + " for " + cls + ": " + child);
      }
   }

   public Object getChild(String localName)
   {
      return names.isEmpty() ?
         null :
         (names.get(names.size() - 1).equals(localName) ? values.get(values.size() - 1) : null);
   }

   public Object newInstance()
   {
      Constructor<?> ctor = null;
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

         if(types.length == values.size())
         {
            ctor = ctors[i];

            int typeInd = 0;
            while(typeInd < types.length)
            {
               if(!types[typeInd].isAssignableFrom(values.get(typeInd++).getClass()))
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
         throw new IllegalStateException("No constructor in " + cls + " that would take arguments " + values);
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
}
