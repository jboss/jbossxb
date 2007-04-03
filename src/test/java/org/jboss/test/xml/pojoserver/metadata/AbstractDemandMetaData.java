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
 * @author Scott.Stark@jboss.org
 * @version $Revision: 37406 $
 */
public class AbstractDemandMetaData extends AbstractFeatureMetaData
   implements DemandMetaData
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   /** The demand */
   protected Object demand;
   
   /** When the dependency is required */
   protected ControllerState whenRequired = ControllerState.DESCRIBED;

   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------

   /**
    * Create a new demand
    */
   public AbstractDemandMetaData()
   {
   }

   /**
    * Create a new demand
    * 
    * @param demand the demand
    */
   public AbstractDemandMetaData(Object demand)
   {
      this.demand = demand;
   }
   
   /**
    * Set the required state of the dependency
    * 
    * @param whenRequired when the dependecy is required
    */
   public void setWhenRequired(ControllerState whenRequired)
   {
      this.whenRequired = whenRequired;
   }
   
   // Public --------------------------------------------------------
   
   /**
    * Set the demand
    * 
    * @param demand the demand
    */
   public void setDemand(Object demand)
   {
      this.demand = demand;
   }
   
   // DemandMetaData implementation ---------------------------------

   public Object getDemand()
   {
      return demand;
   }

   public ControllerState getWhenRequired()
   {
      return whenRequired;
   }
   
   // JBossObject overrides -----------------------------------------
   
   public void toString(StringBuffer buffer)
   {
      buffer.append("demand=").append(demand);
      if (whenRequired != null)
         buffer.append(" whenRequired").append(whenRequired.getStateString());
   }
   
   public void toShortString(StringBuffer buffer)
   {
      buffer.append(demand);
   }
   
   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
