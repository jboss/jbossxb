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

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.config.spi.Configuration;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.ConstructorInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.annotations.JBossXmlMapEntry;
import org.jboss.xb.annotations.JBossXmlMapKey;
import org.jboss.xb.annotations.JBossXmlMapValue;
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
   private final MapPutAdapter mapPutAdapter;
   /**
    * Create a new MapPropertyHandler.
    * 
    * @param propertyInfo the property
    * @param propertyType the property type
    * @throws IllegalArgumentException for a null qName or property
    */
   public MapPropertyHandler(Configuration config, PropertyInfo propertyInfo, TypeInfo propertyType, boolean wrapped)
   {
      super(propertyInfo, propertyType);

      if(wrapped)
      {
         mapFactory = null;
      }
      else
      {
         ClassInfo classInfo = (ClassInfo) propertyType;
         if (Modifier.isAbstract(classInfo.getModifiers()))
         {
            mapFactory = HashMapFactory.INSTANCE;
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
      
      JBossXmlMapEntry entry = propertyInfo.getUnderlyingAnnotation(JBossXmlMapEntry.class);
      if(entry == null)
         entry =((ClassInfo)propertyType).getUnderlyingAnnotation(JBossXmlMapEntry.class);
      
      if(entry != null && !JBossXmlMapEntry.DEFAULT.class.equals(entry.type()))
      {
         BeanInfo entryBean = config.getBeanInfo(entry.type());
         mapPutAdapter = new CustomMapEntryPutAdapter(entryBean);
      }
      else
         mapPutAdapter = DefaultMapEntryPutAdapter.INSTANCE;
   }

   @Override
   @SuppressWarnings("unchecked")
   public void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName qName)
   {
      if(trace)
         log.trace("handle entry " + qName + ", property=" + propertyInfo.getName() + ", parent=" + parent + ", child=" + child);
      
      BeanAdapter beanAdapter = (BeanAdapter) parent;
      
      Map<Object, Object> m = null;
      if(mapFactory == null)
      {
         // it's wrapped, so the parent expected to be a map
         m = (Map<Object, Object>) beanAdapter.getValue();
      }
      else
      {
         try
         {
            if (propertyInfo.getGetter() != null)
               m = (Map<Object, Object>) beanAdapter.get(propertyInfo);
         }
         catch (Throwable t)
         {
            throw new RuntimeException("QName " + qName + " error getting map property " + propertyInfo.getName()
                  + " for " + BuilderUtil.toDebugString(parent), t);
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
               throw new RuntimeException("QName " + qName + " error setting map property " + propertyInfo.getName()
                     + " for " + BuilderUtil.toDebugString(parent) + " with value " + BuilderUtil.toDebugString(m), t);
            }
         }
      }
      
      try
      {
         mapPutAdapter.put(m, child);
      }
      catch (Throwable e)
      {
         throw new RuntimeException("QName " + qName + " error adding " + BuilderUtil.toDebugString(child) + " to map " + BuilderUtil.toDebugString(m), e);
      }
   }
   
   private static interface MapPutAdapter
   {
      void put(Map<Object,Object> map, Object entry) throws Throwable;
   }
   
   private static class CustomMapEntryPutAdapter implements MapPutAdapter
   {
      private final PropertyInfo keyProp;
      private final PropertyInfo valueProp;
      
      CustomMapEntryPutAdapter(BeanInfo entryBean)
      {
         PropertyInfo keyProp = null;
         PropertyInfo valueProp = null;
         for(PropertyInfo prop : entryBean.getProperties())
         {
            JBossXmlMapKey key = prop.getUnderlyingAnnotation(JBossXmlMapKey.class);
            if(key != null)
            {
               if(keyProp != null)
                  throw new IllegalStateException(
                        "Found two properties in entry type " + entryBean.getName() +
                        " annotated with @JBossXmlMapKey: " +
                        keyProp.getName() + " and " + prop.getName());
               keyProp = prop;
            }

            JBossXmlMapValue value = prop.getUnderlyingAnnotation(JBossXmlMapValue.class);
            if(value != null)
            {
               if(valueProp != null)
                  throw new IllegalStateException(
                        "Found two properties in entry type " + entryBean.getName() +
                        " annotated with @JBossXmlMapValue: " +
                        valueProp.getName() + " and " + prop.getName());
               valueProp = prop;
            }
         }

         if(keyProp == null)
            throw new IllegalStateException(
                  "Entry type " + entryBean.getName() +
                  " doesn't have any property annotated with @JBossXmlMapKey.");
         
         this.keyProp = keyProp;
         this.valueProp = valueProp;
      }
      
      public void put(Map<Object, Object> map, Object entry) throws Throwable
      {
         Object key = keyProp.get(entry);
         Object value = entry;
         if(valueProp != null)
            value = valueProp.get(entry);
         map.put(key, value);
      }
   }
   
   private static class DefaultMapEntryPutAdapter implements MapPutAdapter
   {
      static final MapPutAdapter INSTANCE = new DefaultMapEntryPutAdapter();
      
      public void put(Map<Object, Object> map, Object entry)
      {
         if(!(entry instanceof DefaultMapEntry))
            throw new IllegalStateException("Expected DefaultMapEntry but got " + entry);
         DefaultMapEntry defEntry = (DefaultMapEntry) entry;
         map.put(defEntry.getKey(), defEntry.getValue());
      }      
   }
   
   private static interface MapFactory
   {
      Map<Object, Object> createMap() throws Throwable;
   }
   
   private static class HashMapFactory implements MapFactory
   {
      static final MapFactory INSTANCE = new HashMapFactory();
      
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
      public Map createMap() throws Throwable
      {
         return (Map) ctor.newInstance(null);
      }      
   }
}
