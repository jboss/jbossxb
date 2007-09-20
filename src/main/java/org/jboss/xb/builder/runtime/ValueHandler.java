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

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.logging.Logger;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.spi.BeanAdapter;

/**
 * ValueHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ValueHandler extends CharactersHandler
{
   /** The log */
   private final Logger log = Logger.getLogger(getClass());
   
   /** Whether trace is enabled */
   private boolean trace = log.isTraceEnabled();
   
   /** The property info */
   private PropertyInfo propertyInfo;
   
   /** The wrapper type */
   private BeanInfo beanInfo;
   
   /** The wrapper property */
   private String property;
   
   /**
    * Create a new AbstractPropertyHandler
    * 
    * @param propertyInfo the property
    * @throws IllegalArgumentException for a null parameter
    */
   public ValueHandler(PropertyInfo propertyInfo)
   {
      if (propertyInfo == null)
         throw new IllegalArgumentException("Null propertyInfo");
      this.propertyInfo = propertyInfo;
   }
   
   /**
    * Create a new AbstractPropertyHandler
    * 
    * @param propertyInfo the property
    * @param beanInfo the wrapper class
    * @param property the wrapper property
    * @throws IllegalArgumentException for a null qName or property
    */
   public ValueHandler(PropertyInfo propertyInfo, BeanInfo beanInfo, String property)
   {
      if (propertyInfo == null)
         throw new IllegalArgumentException("Null propertyInfo");
      if (beanInfo == null)
         throw new IllegalArgumentException("Null beanInfo");
      if (property == null)
         throw new IllegalArgumentException("Null property");
      this.propertyInfo = propertyInfo;
      this.beanInfo = beanInfo;
      this.property = property;
   }

   /**
    * Get the property info
    * 
    * @return the property info
    */
   public PropertyInfo getPropertyInfo()
   {
      return propertyInfo;
   }

   @Override
   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
      if (trace)
         log.trace("QName " + qName + " handle " + BuilderUtil.toDebugString(value) + " to " + BuilderUtil.toDebugString(owner));
      try
      {
         TypeInfo typeInfo = propertyInfo.getType();
         value = typeInfo.convertValue(value, false);
         if (beanInfo != null)
         {
            ClassInfo classInfo = beanInfo.getClassInfo();
            TypeInfo valueType = classInfo.getTypeInfoFactory().getTypeInfo(value.getClass());
            if (classInfo.isAssignableFrom(valueType) == false)
            {
               Object wrapper = beanInfo.newInstance();
               beanInfo.setProperty(wrapper, property, value);
               value = wrapper;
            }
         }
         BeanAdapter beanAdapter = (BeanAdapter) owner;
         beanAdapter.set(propertyInfo, value);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + qName + " error setting characters " + propertyInfo.getName() + " with value " + BuilderUtil.toDebugString(value) + " to " + BuilderUtil.toDebugString(owner) + " property=" + propertyInfo.getName(), t);
      }
   }
}
