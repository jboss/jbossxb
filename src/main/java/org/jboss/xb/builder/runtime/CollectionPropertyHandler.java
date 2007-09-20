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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.ConstructorInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.spi.BeanAdapter;

/**
 * CollectionPropertyHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class CollectionPropertyHandler extends AbstractPropertyHandler
{
   /** Whether this is a set */
   private boolean isSet = false;
   
   /**
    * Create a new CollectionPropertyHandler.
    * 
    * @param propertyInfo the property
    * @param propertyType the property type
    * @throws IllegalArgumentException for a null qName or property
    */
   public CollectionPropertyHandler(PropertyInfo propertyInfo, TypeInfo propertyType)
   {
      super(propertyInfo, propertyType);
      
      TypeInfo set = propertyType.getTypeInfoFactory().getTypeInfo(Set.class);
      if (set.isAssignableFrom(propertyType))
         isSet = true;
   }

   @Override
   @SuppressWarnings("unchecked")
   public void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName qName)
   {
      BeanAdapter beanAdapter = (BeanAdapter) parent;
      
      Collection<Object> c = null;
      try
      {
         if (propertyInfo.getGetter() != null)
            c = (Collection) beanAdapter.get(propertyInfo);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + qName + " error getting collection property " + propertyInfo.getName() + " for " + BuilderUtil.toDebugString(parent), t);
      }
      
      // No collection so create one
      if (c == null)
      {
         ClassInfo collectionType = (ClassInfo) propertyType; 
         if (Modifier.isAbstract(collectionType.getModifiers()) == false)
         {
            try
            {
               ConstructorInfo constructor = collectionType.getDeclaredConstructor(null);
               if(constructor == null)
               {
                  for(ConstructorInfo ctor : collectionType.getDeclaredConstructors())
                  {
                     if(ctor.getParameterTypes().length == 0)
                     {
                        log.warn("ClassInfo.getDeclaredConstructor(null) didn't work for " + collectionType.getName() + ", found the default ctor in ClassInfo.getDeclaredConstructors()");
                        constructor = ctor;
                        break;
                     }
                  }
                  
                  if(constructor == null)
                  {
                     throw new NoSuchMethodException("Default constructor not found for " + collectionType.getName());
                  }
               }
               c = (Collection) constructor.newInstance(null);
            }
            catch (Throwable t)
            {
               throw new RuntimeException("QName " + qName + " error creating collection: " + propertyType.getName(), t);
            }
         }
         else if (isSet)
            c = new HashSet<Object>();
         else
            c = new ArrayList<Object>();

         try
         {
            beanAdapter.set(propertyInfo, c);
         }
         catch (Throwable t)
         {
            throw new RuntimeException("QName " + qName + " error setting collection property " + propertyInfo.getName() + " for " + BuilderUtil.toDebugString(parent) + " with value " + BuilderUtil.toDebugString(c), t);
         }
      }
      
      // Now add
      try
      {
         c.add(child);
      }
      catch (Exception e)
      {
         throw new RuntimeException("QName " + qName + " error adding " + BuilderUtil.toDebugString(child) + " to collection " + BuilderUtil.toDebugString(c), e);
      }
   }
}
