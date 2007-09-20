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
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.spi.BeanAdapter;

/**
 * AbstractPropertyHandler
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractPropertyHandler extends AttributeHandler
{
   /** The log */
   protected final Logger log = Logger.getLogger(getClass());
   
   /** Whether trace is enabled */
   protected boolean trace = log.isTraceEnabled();
   
   /** The property info */
   private PropertyInfo propertyInfo;
   
   /** The property type */
   private TypeInfo propertyType;
   
   /**
    * Create a new AbstractPropertyHandler
    * 
    * @param propertyInfo the property
    * @param propertyType the property type
    * @throws IllegalArgumentException for a null parameteers
    */
   public AbstractPropertyHandler(PropertyInfo propertyInfo, TypeInfo propertyType)
   {
      if (propertyInfo == null)
         throw new IllegalArgumentException("Null propertyInfo");
      if (propertyType == null)
         throw new IllegalArgumentException("Null propertyType");
      this.propertyInfo = propertyInfo;
      this.propertyType = propertyType;
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

   /**
    * Get the property type
    * 
    * @return the property type
    */
   public TypeInfo getPropertyType()
   {
      return propertyType;
   }
   
   public void doHandle(Object parent, Object child, QName qName)
   {
      if (trace)
         log.trace("QName " + qName + " handle " + BuilderUtil.toDebugString(child) + " to " + BuilderUtil.toDebugString(parent) + " property=" + propertyInfo.getName());
      
      try
      {
         handle(propertyInfo, propertyType, parent, child, qName);
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + qName + "error setting property " + propertyInfo.getName() + " with value " + BuilderUtil.toDebugString(child) + " to " + BuilderUtil.toDebugString(parent));
      }
   }

   @Override
   public void attribute(QName elemName, QName attrName, AttributeBinding binding, Object owner, Object value)
   {
      ValueAdapter valueAdapter = binding.getValueAdapter();
      if (valueAdapter != null)
         value = valueAdapter.cast(value, null);
      
      BeanAdapter parent = (BeanAdapter) owner;
      doHandle(parent, value, attrName);
   }

   /**
    * Handle the property
    * 
    * @param propertyInfo the property
    * @param parent the parent
    * @param propertyType the property type
    * @param child the child
    * @param qName the qName
    */
   public abstract void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName qName);
}
