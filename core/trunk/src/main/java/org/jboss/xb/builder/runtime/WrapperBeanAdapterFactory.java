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

import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.xb.spi.BeanAdapter;
import org.jboss.xb.spi.BeanAdapterFactory;

/**
 * WrapperBeanAdapterFactory.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class WrapperBeanAdapterFactory extends BeanAdapterFactory
{
   /** The wrapped bean adapter factory */
   private BeanAdapterFactory wrapped;
   
   /** The type to stop wrapping at */
   private Class<?> stopWrapping;
   
   /**
    * Create a new WrapperBeanAdapterFactory.
    * 
    * @param wrapped the wrapped factory
    * @param stopWrapping the stop wrapping type
    */
   public WrapperBeanAdapterFactory(BeanAdapterFactory wrapped, Class<?> stopWrapping)
   {
      this.wrapped = wrapped;
      this.stopWrapping = stopWrapping;
   }

   /**
    * The type to stop wrapping at
    * 
    * @return the stop wrapping type
    */
   public Class<?> getStopWrapping()
   {
      return stopWrapping;
   }

   @Override
   public WrapperBeanAdapter newInstance()
   {
      BeanAdapter adapter = wrapped.newInstance();
      return new WrapperBeanAdapter(this, adapter);
   }

   @Override
   public void addProperty(QName name, AbstractPropertyHandler propertyHandler)
   {
      throw new UnsupportedOperationException("addProperty");
   }

   @Override
   public String getAvailable()
   {
      return wrapped.getAvailable();
   }

   @Override
   public Map<QName, AbstractPropertyHandler> getProperties()
   {
      return wrapped.getProperties();
   }

   @Override
   public AbstractPropertyHandler getPropertyHandler(QName name)
   {
      return wrapped.getPropertyHandler(name);
   }

   @Override
   public AbstractPropertyHandler getWildcardHandler()
   {
      return wrapped.getWildcardHandler();
   }

   @Override
   public void setWildcardHandler(AbstractPropertyHandler wildcardHandler)
   {
      throw new UnsupportedOperationException("addProperty");
   }
}
