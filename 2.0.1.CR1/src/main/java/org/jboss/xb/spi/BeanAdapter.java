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

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.xb.builder.runtime.AbstractPropertyHandler;

/**
 * BeanAdapter.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class BeanAdapter
{
   /** The bean adapter factory */
   private BeanAdapterFactory beanAdapterFactory;
   
   /**
    * Create a new BeanAdapter.
    * 
    * @param beanAdapterFactory the bean adapter factory
    * @throws IllegalArgumentException for a null bean adapter factory
    */
   public BeanAdapter(BeanAdapterFactory beanAdapterFactory)
   {
      if (beanAdapterFactory == null)
         throw new IllegalArgumentException("Null bean adapter factory");
      this.beanAdapterFactory = beanAdapterFactory;
   }
   
   /**
    * Get the bean adapter factory
    * 
    * @return the factory
    */
   protected BeanAdapterFactory getBeanAdapterFactory()
   {
      return beanAdapterFactory;
   }

   /**
    * Get the property handler for an element name
    * 
    * @param qName the element name
    * @return the property handler
    */
   public AbstractPropertyHandler getPropertyHandler(QName qName)
   {
      return beanAdapterFactory.getPropertyHandler(qName);
   }
   
   /**
    * Get the wildcardHandler.
    * 
    * @return the wildcardHandler.
    */
   @XmlTransient
   public AbstractPropertyHandler getWildcardHandler()
   {
      return beanAdapterFactory.getWildcardHandler();
   }

   /**
    * Get the available properties as a string
    * 
    * @return the available properties
    */
   @XmlTransient
   public String getAvailable()
   {
      return beanAdapterFactory.getAvailable();
   }
   
   /**
    * Get a property
    * 
    * @param propertyInfo the property info
    * @return the property value
    * @throws Throwable for any error
    */
   public abstract Object get(PropertyInfo propertyInfo) throws Throwable;

   /**
    * Set a property
    * 
    * @param propertyInfo the property info
    * @param child the value
    * @throws Throwable for any error
    */
   public abstract void set(PropertyInfo propertyInfo, Object child) throws Throwable;
   
   /**
    * Get the value
    * 
    * @return the value
    */
   @XmlTransient
   public abstract Object getValue();
}
