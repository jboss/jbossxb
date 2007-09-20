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
package org.jboss.ejb.metadata.jboss;

import javax.xml.bind.annotation.XmlType;

import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.SessionBeanMetaData;
import org.jboss.javaee.metadata.support.IdMetaDataImplWithDescriptions;

/**
 * ClusterConfigMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="cluster-configType")
public class ClusterConfigMetaData extends IdMetaDataImplWithDescriptions
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -614188513386783204L;

   /** The jndi prefix for the sesssion state */
   public final static String JNDI_PREFIX_FOR_SESSION_STATE = "/HASessionState/";
   
   /** The default session state  name */
   public final static String DEFAULT_SESSION_STATE_NAME = JNDI_PREFIX_FOR_SESSION_STATE + "Default";

   /** The first available load balancing policy */
   public static final String FIRST_AVAILABLE = "org.jboss.ha.framework.interfaces.FirstAvailable";

   /** The round robin load balancing policy */
   public static final String ROUND_ROBIN = "org.jboss.ha.framework.interfaces.RoundRobin";
   
   /** The partition name */
   private String partitionName;
   
   /** The home load balancing policy */
   private String homeLoadBalancingPolicy;
   
   /** The bean load balancing policy */
   private String beanLoadBalancingPolicy;
   
   /** The state manager jndi */
   private String sessionStateManagerJndiName;

   /** The enterprise bean */
   private JBossEnterpriseBeanMetaData enterpriseBeanMetaData;
   
   /**
    * Set the enterpriseBeanMetaData.
    * 
    * @param enterpriseBeanMetaData the enterpriseBeanMetaData.
    * @throws IllegalArgumentException for a null enterpriseBeanMetaData
    */
   void setEnterpriseBeanMetaData(JBossEnterpriseBeanMetaData enterpriseBeanMetaData)
   {
      if (enterpriseBeanMetaData == null)
         throw new IllegalArgumentException("Null enterpriseBeanMetaData");
      this.enterpriseBeanMetaData = enterpriseBeanMetaData;
   }

   /**
    * Get the beanLoadBalancingPolicy.
    * 
    * @return the beanLoadBalancingPolicy.
    */
   public String getBeanLoadBalancingPolicy()
   {
      return beanLoadBalancingPolicy;
   }

   /**
    * Determine the beanLoadBalancingPolicy.
    * 
    * @return the beanLoadBalancingPolicy.
    */
   public String determineBeanLoadBalancingPolicy()
   {
      if (beanLoadBalancingPolicy == null && enterpriseBeanMetaData != null)
      {
         EnterpriseBeanMetaData ejb = enterpriseBeanMetaData.getOverridenMetaData();
         if (ejb != null)
         {
            if (ejb.isSession())
            {
               SessionBeanMetaData session = (SessionBeanMetaData) ejb;
               if (session.isStateless())
                  return ROUND_ROBIN;
            }
         }
         else
            return FIRST_AVAILABLE;
      }
      return beanLoadBalancingPolicy;
   }

   /**
    * Set the beanLoadBalancingPolicy.
    * 
    * @param beanLoadBalancingPolicy the beanLoadBalancingPolicy.
    * @throws IllegalArgumentException for a null beanLoadBalancingPolicy
    */
   public void setBeanLoadBalancingPolicy(String beanLoadBalancingPolicy)
   {
      if (beanLoadBalancingPolicy == null)
         throw new IllegalArgumentException("Null beanLoadBalancingPolicy");
      this.beanLoadBalancingPolicy = beanLoadBalancingPolicy;
   }

   /**
    * Get the homeLoadBalancingPolicy.
    * 
    * @return the homeLoadBalancingPolicy.
    */
   public String getHomeLoadBalancingPolicy()
   {
      return homeLoadBalancingPolicy;
   }

   /**
    * Determine the homeLoadBalancingPolicy.
    * 
    * @return the homeLoadBalancingPolicy.
    */
   public String determineHomeLoadBalancingPolicy()
   {
      if (homeLoadBalancingPolicy == null && enterpriseBeanMetaData != null)
      {
         EnterpriseBeanMetaData ejb = enterpriseBeanMetaData.getOverridenMetaData();
         if (ejb != null)
            return ROUND_ROBIN;
      }
      return homeLoadBalancingPolicy;
   }

   /**
    * Set the homeLoadBalancingPolicy.
    * 
    * @param homeLoadBalancingPolicy the homeLoadBalancingPolicy.
    * @throws IllegalArgumentException for a null homeLoadBalancingPolicy
    */
   public void setHomeLoadBalancingPolicy(String homeLoadBalancingPolicy)
   {
      if (homeLoadBalancingPolicy == null)
         throw new IllegalArgumentException("Null homeLoadBalancingPolicy");
      this.homeLoadBalancingPolicy = homeLoadBalancingPolicy;
   }

   /**
    * Get the partitionName.
    * 
    * @return the partitionName.
    */
   public String getPartitionName()
   {
      return partitionName;
   }

   /**
    * Determine the partitionName.
    * 
    * @return the partitionName.
    */
   public String determinePartitionName()
   {
      return partitionName;
   }

   /**
    * Set the partitionName.
    * 
    * @param partitionName the partitionName.
    * @throws IllegalArgumentException for a null partitionName
    */
   public void setPartitionName(String partitionName)
   {
      if (partitionName == null)
         throw new IllegalArgumentException("Null partitionName");
      this.partitionName = partitionName;
   }

   /**
    * Get the sessionStateManagerJndiName.
    * 
    * @return the sessionStateManagerJndiName.
    */
   public String getSessionStateManagerJndiName()
   {
      return sessionStateManagerJndiName;
   }

   /**
    * Determine the sessionStateManagerJndiName.
    * 
    * @return the sessionStateManagerJndiName.
    */
   public String determineSessionStateManagerJndiName()
   {
      if (sessionStateManagerJndiName == null)
         return DEFAULT_SESSION_STATE_NAME;
      return sessionStateManagerJndiName;
   }

   /**
    * Set the sessionStateManagerJndiName.
    * 
    * @param sessionStateManagerJndiName the sessionStateManagerJndiName.
    * @throws IllegalArgumentException for a null sessionStateManagerJndiName
    */
   public void setSessionStateManagerJndiName(String sessionStateManagerJndiName)
   {
      if (sessionStateManagerJndiName == null)
         throw new IllegalArgumentException("Null sessionStateManagerJndiName");
      this.sessionStateManagerJndiName = sessionStateManagerJndiName;
   }
}
