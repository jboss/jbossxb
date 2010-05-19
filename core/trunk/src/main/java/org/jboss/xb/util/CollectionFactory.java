/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.ConstructorInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * A CollectionFactory.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class CollectionFactory
{
   private static TypeInfo SET_INFO;
   
   public abstract Collection<Object> createCollection() throws Throwable;
   
   public static CollectionFactory LIST = new CollectionFactory()
   {
      public Collection<Object> createCollection() throws Throwable
      {
         return new ArrayList<Object>();
      }      
   };

   public static CollectionFactory SET = new CollectionFactory()
   {
      public Collection<Object> createCollection() throws Throwable
      {
         return new HashSet<Object>();
      }      
   };
   
   public static CollectionFactory getFactory(ClassInfo collectionType)
   {
      if (Modifier.isAbstract(collectionType.getModifiers()))
      {
         if(SET_INFO == null)
            SET_INFO = collectionType.getTypeInfoFactory().getTypeInfo(Set.class);
         if (SET_INFO.isAssignableFrom(collectionType))
            return SET;
         else
            return LIST;
      }

      ConstructorInfo constructor = collectionType.getDeclaredConstructor(null);
      if (constructor == null)
      {
         for (ConstructorInfo ctor : collectionType.getDeclaredConstructors())
         {
            if (ctor.getParameterTypes().length == 0)
            {
               // TODO for org.jboss.reflect
//               log.warn("ClassInfo.getDeclaredConstructor(null) didn't work for " + collectionType.getName()
//                     + ", found the default ctor in ClassInfo.getDeclaredConstructors()");
               constructor = ctor;
               break;
            }
         }

         if (constructor == null)
            throw new JBossXBRuntimeException("Default constructor not found for " + collectionType.getName());
      }

      final ConstructorInfo ctor = constructor;
      return new CollectionFactory()
      {
         @SuppressWarnings("unchecked")
         @Override
         public Collection<Object> createCollection() throws Throwable
         {
            return (Collection<Object>) ctor.newInstance(null);
         }
      };
   }
}
