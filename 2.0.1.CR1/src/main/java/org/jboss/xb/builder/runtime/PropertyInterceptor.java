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

import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.logging.Logger;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.spi.BeanAdapter;

/**
 * PropertyInterceptor.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class PropertyInterceptor extends DefaultElementInterceptor
{
   /** The log */
   private static final Logger log = Logger.getLogger(PropertyInterceptor.class);
   
   /** Whether trace is enabled */
   private boolean trace = log.isTraceEnabled();
   
   /** The property info */
   private PropertyInfo propertyInfo;
   
   /** The property type */
   private TypeInfo propertyType;
   
   /**
    * Create a new PropertyHandler.
    * 
    * @param propertyInfo the property
    * @param propertyType the property type
    * @throws IllegalArgumentException for a null property
    */
   public PropertyInterceptor(PropertyInfo propertyInfo, TypeInfo propertyType)
   {
      if (propertyInfo == null)
         throw new IllegalArgumentException("Null property info");
      if (propertyType == null)
         throw new IllegalArgumentException("Null property type");
      this.propertyInfo = propertyInfo;
      this.propertyType = propertyType;
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public void add(Object parent, Object child, QName qName)
   {
      if (trace)
         log.trace("setParent " + qName + " parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(child) +" property=" + propertyInfo.getName());
      try
      {
         if (propertyType.isArray())
         {
            ArrayWrapper wrapper = (ArrayWrapper) child;
            child = wrapper.getArray(propertyType);
         }
         BeanAdapter beanAdapter = (BeanAdapter) parent;
         beanAdapter.set(propertyInfo, child);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + qName + " error setting property " + propertyInfo.getName() + " with value " + BuilderUtil.toDebugString(child) + " to " + BuilderUtil.toDebugString(parent), t);
      }
   } 
}
