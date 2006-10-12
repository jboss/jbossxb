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

/**
 * Dependency value.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class AbstractDependencyValueMetaData extends AbstractValueMetaData
{
   /** The property name */
   protected String property;

   /** The required state of the dependency or null to look in the registry */
   protected ControllerState dependentState = ControllerState.INSTALLED;

   /**
    * Create a new dependency value
    */
   public AbstractDependencyValueMetaData()
   {
   }

   /**
    * Create a new dependency value
    * 
    * @param value the value
    */
   public AbstractDependencyValueMetaData(Object value)
   {
      super(value);
   }

   /**
    * Create a new dependency value
    * 
    * @param value the value
    * @param property the property
    */
   public AbstractDependencyValueMetaData(Object value, String property)
   {
      super(value);
      this.property = property;
   }

   /**
    * Set the value
    * 
    * @param value the value
    */
   public void setValue(Object value)
   {
      super.setValue(value);
   }
   
   /**
    * Set the property
    * 
    * @param property the property name
    */
   public void setProperty(String property)
   {
      this.property = property;
   }
   
   /**
    * Set the required state of the dependency
    * 
    * @param dependentState the required state or null if it must be in the registry
    */
   public void setDependentState(ControllerState dependentState)
   {
      this.dependentState = dependentState;
   }

   public ControllerState getDependentState()
   {
      return dependentState;
   }
}
