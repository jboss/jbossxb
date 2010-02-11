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
package org.jboss.xb.spi;

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.reflect.spi.MethodInfo;

/**
 * DefaultBeanAdapter.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class DefaultBeanAdapter extends BeanAdapter
{
   /** The bean info */
   private BeanInfo beanInfo;

   /** Any factory  */
   private MethodInfo factory;

   /** The value */
   private Object value;

   /**
    * Create a new bean adapter
    * 
    * @param beanAdapterFactory the bean adapter factory
    * @param beanInfo the bean info
    * @param factory the factory
    * @throws IllegalArgumentException for null bean adapter factory or bean info
    */
   public DefaultBeanAdapter(BeanAdapterFactory beanAdapterFactory, BeanInfo beanInfo, MethodInfo factory)
   {
      super(beanAdapterFactory);
      if (beanInfo == null)
         throw new IllegalArgumentException("Null bean info");
      this.beanInfo = beanInfo;
      this.factory = factory;
      
      try
      {
         if (factory != null)
            value = factory.invoke(null, null);
         else
            value = beanInfo.newInstance();
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Error instantiating bean for " + beanInfo.getName(), t);
      }
   }

   /**
    * Get the bean info
    * 
    * @return the bean info
    */
   public BeanInfo getBeanInfo()
   {
      return beanInfo;
   }

   /**
    * Get the factory
    * 
    * @return the factgory
    */
   public MethodInfo getFactory()
   {
      return factory;
   }
   
   public Object get(PropertyInfo propertyInfo) throws Throwable
   {
      return propertyInfo.get(value);
   }
   
   public void set(PropertyInfo propertyInfo, Object child) throws Throwable
   {
      propertyInfo.set(value, child);
   }

   public Object getValue()
   {
      return value;
   }
}
