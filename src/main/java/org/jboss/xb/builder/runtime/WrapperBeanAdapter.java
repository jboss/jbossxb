/*
* JBoss, Home of Professional Open Source
* Copyright 2007, JBoss Inc., and individual contributors as indicated
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

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.xb.spi.BeanAdapter;

/**
 * WrapperBeanAdapter.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class WrapperBeanAdapter extends BeanAdapter
{
   /** The wrapped bean adapter */
   private BeanAdapter wrapped;
   
   /** The not wrapped value */
   private Object notWrapped;
   
   /**
    * Create a new WrapperBeanAdapter.
    * 
    * @param beanAdapterFactory our factory
    * @param wrapped the wrapped adapter
    */
   public WrapperBeanAdapter(WrapperBeanAdapterFactory beanAdapterFactory, BeanAdapter wrapped)
   {
      super(beanAdapterFactory);
      this.wrapped = wrapped;
   }

   @Override
   protected WrapperBeanAdapterFactory getBeanAdapterFactory()
   {
      return (WrapperBeanAdapterFactory) super.getBeanAdapterFactory();
   }

   @Override
   public Object get(PropertyInfo propertyInfo) throws Throwable
   {
      return wrapped.get(propertyInfo);
   }

   @Override
   public Object getValue()
   {
      if (notWrapped != null)
         return notWrapped;
      else
         return wrapped.getValue();
   }

   @Override
   public void set(PropertyInfo propertyInfo, Object child) throws Throwable
   {
      Class<?> stopWrapping = getBeanAdapterFactory().getStopWrapping(); 
      if (child != null && stopWrapping != null && stopWrapping.isInstance(child))
         notWrapped = child;
      else
         wrapped.set(propertyInfo, child);
   }
}
