/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.builder.runtime;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.ConstructorInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.spi.BeanAdapter;

/**
 * MapPropertyHandler.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class MapPropertyHandler extends AbstractPropertyHandler
{
   private final MapFactory mapFactory;
   
   /**
    * Create a new MapPropertyHandler.
    * 
    * @param propertyInfo the property
    * @param propertyType the property type
    * @throws IllegalArgumentException for a null qName or property
    */
   public MapPropertyHandler(PropertyInfo propertyInfo, TypeInfo propertyType)
   {
      super(propertyInfo, propertyType);

      ClassInfo classInfo = (ClassInfo) propertyType;
      if (Modifier.isAbstract(classInfo.getModifiers()))
      {
         mapFactory = new HashMapFactory();
      }
      else
      {
         ConstructorInfo constructor = classInfo.getDeclaredConstructor(null);
         if (constructor == null)
         {
            for (ConstructorInfo ctor : classInfo.getDeclaredConstructors())
            {
               if (ctor.getParameterTypes().length == 0)
               {
                  log.warn("ClassInfo.getDeclaredConstructor(null) didn't work for " + classInfo.getName()
                        + ", found the default ctor in ClassInfo.getDeclaredConstructors()");
                  constructor = ctor;
                  break;
               }
            }

            if (constructor == null)
            {
               throw new RuntimeException("Default constructor not found for " + classInfo.getName());
            }
         }
         mapFactory = new CtorMapFactory(constructor);
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName qName)
   {
      BeanAdapter beanAdapter = (BeanAdapter) parent;
      
      Map<Object, Object> m = null;
      try
      {
         if (propertyInfo.getGetter() != null)
            m = (Map<Object, Object>) beanAdapter.get(propertyInfo);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + qName + " error getting map property " + propertyInfo.getName() + " for " + BuilderUtil.toDebugString(parent), t);
      }
      
      // No map so create one
      if (m == null)
      {
         try
         {
            m = mapFactory.createMap();
         }
         catch (Throwable t)
         {
            throw new RuntimeException("QName " + qName + " error creating map: " + propertyType.getName(), t);
         }

         try
         {
            beanAdapter.set(propertyInfo, m);
         }
         catch (Throwable t)
         {
            throw new RuntimeException("QName " + qName + " error setting map property " + propertyInfo.getName() + " for " + BuilderUtil.toDebugString(parent) + " with value " + BuilderUtil.toDebugString(m), t);
         }
      }

      if(!(child instanceof DefaultMapEntry))
         throw new IllegalStateException("Only the DefaultMapEntry is supported at the moment: " + child);
         
      DefaultMapEntry entry = (DefaultMapEntry) child;
      
      // Now add
      try
      {
         m.put(entry.getKey(), entry.getValue());
      }
      catch (Exception e)
      {
         throw new RuntimeException("QName " + qName + " error adding " + BuilderUtil.toDebugString(child) + " to map " + BuilderUtil.toDebugString(m), e);
      }
   }
   
   private static interface MapFactory
   {
      Map<Object, Object> createMap() throws Throwable;
   }
   
   private static class HashMapFactory implements MapFactory
   {
      @SuppressWarnings("unchecked")
      public Map<Object, Object> createMap()
      {
         return new HashMap<Object, Object>();
      }  
   }
   
   private static class CtorMapFactory implements MapFactory
   {
      private final ConstructorInfo ctor;
      
      CtorMapFactory(ConstructorInfo ctor)
      {
         this.ctor = ctor;
      }
      
      @SuppressWarnings("unchecked")
      public Map<Object, Object> createMap() throws Throwable
      {
         return (Map) ctor.newInstance(null);
      }      
   }
}
