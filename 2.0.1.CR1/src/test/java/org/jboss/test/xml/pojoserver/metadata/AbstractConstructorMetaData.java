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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 37406 $
 */
public class AbstractConstructorMetaData extends AbstractFeatureMetaData
   implements ConstructorMetaData
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------
   
   /** The paramaters List<ParameterMetaData> */
   protected List<AbstractParameterMetaData> parameters;

   /** The factory */
   protected ValueMetaData factory;

   /** The factory class name */
   protected String factoryClassName;

   /** The factory method */
   protected String factoryMethod;
   
   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------

   /**
    * Create a new constructor meta data
    */
   public AbstractConstructorMetaData()
   {
   }
   
   // Public --------------------------------------------------------
   
   /**
    * Set the parameters
    * 
    * @param parameters List<ParameterMetaData>
    */
   public void setParameters(List<AbstractParameterMetaData> parameters)
   {
      this.parameters = parameters;
   }
   
   /**
    * Set the factory
    * 
    * @param factory the factory
    */
   public void setFactory(ValueMetaData factory)
   {
      this.factory = factory;
   }
   
   /**
    * Set the factory class name
    * 
    * @param name the factory class name
    */
   public void setFactoryClass(String name)
   {
      this.factoryClassName = name;
   }
   
   /**
    * Set the factory method
    * 
    * @param name the factory method
    */
   public void setFactoryMethod(String name)
   {
      this.factoryMethod = name;
   }
   
   // ConstructorMetaData implementation ----------------------------
   
   public List<AbstractParameterMetaData> getParameters()
   {
      return parameters;
   }

   public ValueMetaData getFactory()
   {
      return factory;
   }
   
   public String getFactoryClass()
   {
      return factoryClassName;
   }
   
   public String getFactoryMethod()
   {
      return factoryMethod;
   }
   
   // MetaDataVisitorNode overrides ----------------------------------
   
   public Iterator<?> getChildren()
   {
      ArrayList list = new ArrayList();
      if (parameters != null)
         list.addAll(parameters);
      if (factory != null)
         list.add(factory);
      return list.iterator();
   }

   // JBossObject overrides ------------------------------------------
   
   public void toString(StringBuffer buffer)
   {
      buffer.append("parameters=").append(parameters);
      if (factory != null)
         buffer.append(" factory=").append(factory);
      if (factoryClassName != null)
         buffer.append(" factoryClass=").append(factoryClassName);
      if (factoryMethod != null)
         buffer.append(" factoryMethod=").append(factoryMethod);
      super.toString(buffer);
   }
   
   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
