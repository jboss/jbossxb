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
package org.jboss.xb.builder.runtime;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.logging.Logger;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.AnyAttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AnyAttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.spi.BeanAdapter;

/**
 * A AnyAttributePropertyHandler.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class AnyAttributePropertyHandler extends AnyAttributeHandler
{
   /** The log */
   protected final Logger log = Logger.getLogger(getClass());
   
   /** Whether trace is enabled */
   protected boolean trace = log.isTraceEnabled();
   
   /** The property info */
   private PropertyInfo propertyInfo;

   /** The property type */
   private TypeInfo propertyType;

   public AnyAttributePropertyHandler(PropertyInfo propertyInfo, TypeInfo propertyType)
   {
      if (propertyInfo == null)
         throw new IllegalArgumentException("Null propertyInfo");
      if (propertyType == null)
         throw new IllegalArgumentException("Null propertyType");

      if(!propertyType.isMap())
         throw new IllegalStateException("Current implementation expects property bound to anyAttribute to be of type Map<QName, Object>." +
               " Property name is " + propertyInfo.getName() + ", property type is " + propertyType.getName());

      this.propertyInfo = propertyInfo;
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
   public void attribute(QName elemName, QName attrName, AnyAttributeBinding binding, Object owner, Object value)
   {
      ValueAdapter valueAdapter = binding.getValueAdapter();
      if (valueAdapter != null)
         value = valueAdapter.cast(value, null);
      
      BeanAdapter parent = (BeanAdapter) owner;
      doHandle(parent, value, attrName);
   }

   public void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName name)
   {
      BeanAdapter beanAdapter = (BeanAdapter) parent;

      Map<QName, Object> map = null;
      try
      {
         if (propertyInfo.getGetter() != null)
            map = (Map<QName, Object>) beanAdapter.get(propertyInfo);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Error getting map for property " + propertyInfo.getName()
               + " bound to any attribute from " + BuilderUtil.toDebugString(parent), t);
      }

      // No map so create one
      if (map == null)
      {
         map = new HashMap<QName, Object>();
         try
         {
            beanAdapter.set(propertyInfo, map);
         }
         catch (Throwable t)
         {
            throw new RuntimeException("Error setting map property " + propertyInfo.getName()
                  + " bound to any attribute for " + BuilderUtil.toDebugString(parent) + " with value " + BuilderUtil.toDebugString(map), t);
         }
      }

      map.put(name, child);
   }
}
