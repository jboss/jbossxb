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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.spi.BeanAdapter;
import org.jboss.xb.util.CollectionFactory;

/**
 * CollectionPropertyHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision: 1.1 $
 */
public class CollectionPropertyHandler extends AbstractPropertyHandler
{
   private final CollectionFactory colFactory;

   /** The component type info */
   private TypeInfo componentType;

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
      componentType = ((ClassInfo) propertyType).getComponentType();
      colFactory = CollectionFactory.getFactory((ClassInfo) propertyType);
   }

   @Override
   @SuppressWarnings("unchecked")
   public void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName qName)
   {
      if (componentType != null && child != null)
      {
         if(!componentType.isInstance(child))
            throw new IllegalArgumentException("Child is not an instance of " + componentType + ", child: " + child);
      }

      BeanAdapter beanAdapter = (BeanAdapter) parent;
      
      Collection c = null;
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
         try
         {
            c = colFactory.createCollection();
         }
         catch (Throwable t)
         {
            throw new RuntimeException("QName " + qName + " error creating collection: " + propertyType.getName(), t);
         }

         try
         {
            beanAdapter.set(propertyInfo, c);
         }
         catch (Throwable t)
         {
            throw new RuntimeException("QName " + qName + " error setting collection property " + propertyInfo.getName() + " for " + BuilderUtil.toDebugString(parent) + " with value " + BuilderUtil.toDebugString(c), t);
         }
      }
      
      c.add(child);
   }
}
