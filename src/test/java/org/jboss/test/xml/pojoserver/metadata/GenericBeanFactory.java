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
package org.jboss.test.xml.pojoserver.metadata;

import java.util.Map;

/**
 * Collection metadata.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class GenericBeanFactory
{
   /** The bean class name */
   protected String bean;
   
   /** The constructor metadata */
   protected ConstructorMetaData constructor;
   
   /** The properties Map<propertyName, ValueMetaData> */
   protected Map properties;

   /**
    * Create a new generic bean factory
    */
   public GenericBeanFactory()
   {
   }

   // Public --------------------------------------------------------

   /**
    * Get the bean name
    * 
    * @return the bean
    */
   public String getBean()
   {
      return bean;
   }
   
   /**
    * Set the bean name
    * 
    * @param bean the bean name
    */
   public void setBean(String bean)
   {
      this.bean = bean;
   }
   
   /**
    * Get the constructor metadata
    * 
    * @return the contructor metadata
    */
   public ConstructorMetaData getConstructor()
   {
      return constructor;
   }
   
   /**
    * Set the constructor metadata
    * 
    * @param constructor the constructor metadata
    */
   public void setConstructor(ConstructorMetaData constructor)
   {
      this.constructor = constructor;
   }
   
   /**
    * Get the properties
    * 
    * @return the properties Map<propertyName, ValueMetaData>
    */
   public Map getProperties()
   {
      return properties;
   }
   
   /**
    * Set the properties
    * 
    * @param properties the properties Map<propertyName, ValueMetaData>
    */
   public void setProperties(Map properties)
   {
      this.properties = properties;
   }
}