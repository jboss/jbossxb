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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.xb.builder.runtime.AbstractPropertyHandler;

/**
 * BeanAdapterFactory.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class BeanAdapterFactory
{
   /** The properties */
   private Map<QName, AbstractPropertyHandler> properties;
   
   /** The wildcard handler */
   private AbstractPropertyHandler wildcardHandler;

   /**
    * Get the property handler for an element name
    * 
    * @param qName the element name
    * @return the property handler
    */
   public AbstractPropertyHandler getPropertyHandler(QName qName)
   {
      if (properties == null)
         return null;
      return properties.get(qName);
   }

   /**
    * Get the properties
    * 
    * @return the properties
    */
   public Map<QName, AbstractPropertyHandler> getProperties()
   {
      return properties;
   }
   
   /**
    * Add a property
    * 
    * @param qName the qName
    * @param propertyHandler the property handler
    */
   public void addProperty(QName qName, AbstractPropertyHandler propertyHandler)
   {
      if (qName == null)
         throw new IllegalArgumentException("Null qName");
      if (propertyHandler == null)
         throw new IllegalArgumentException("Null property handler");

      if (properties == null)
         properties = new HashMap<QName, AbstractPropertyHandler>();
      properties.put(qName, propertyHandler);
   }
   /**
    * Get the available properties as a string
    * 
    * @return the available properties
    */
   public String getAvailable()
   {
      if (properties == null)
         return "<nothing>";
      else
         return properties.keySet().toString();
   }
   
   /**
    * Get the wildcardHandler.
    * 
    * @return the wildcardHandler.
    */
   public AbstractPropertyHandler getWildcardHandler()
   {
      return wildcardHandler;
   }

   /**
    * Set the wildcardHandler.
    * 
    * @param wildcardHandler the wildcardHandler.
    */
   public void setWildcardHandler(AbstractPropertyHandler wildcardHandler)
   {
      this.wildcardHandler = wildcardHandler;
   }

   /**
    * Create a new BeanAdapter
    * 
    * @return the new bean adapter
    */
   public abstract BeanAdapter newInstance();
}
