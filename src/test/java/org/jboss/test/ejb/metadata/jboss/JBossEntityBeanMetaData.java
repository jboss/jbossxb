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

import org.jboss.ejb.metadata.spec.EntityBeanMetaData;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * EntityBeanMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jboss-entity-beanType")
public class JBossEntityBeanMetaData extends JBossEnterpriseBeanMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -6869794514744015588L;
   
   /** The jndi name */
   private String jndiName;
   
   /** Whether to call by value */
   private boolean callByValue;

   /** Whether this bean is clustered */
   private boolean clustered;
   
   /** Read only */
   private boolean readOnly;
   
   /** The cluster config */
   private ClusterConfigMetaData clusterConfig;
   
   /** The determined cluster config */
   private transient ClusterConfigMetaData determinedClusterConfig;
   
   /** Cache invalidation */
   private boolean cacheInvalidation;

   /** The cache invalidation config */
   private CacheInvalidationConfigMetaData cacheInvalidationConfig;
   
   // TODO DOM cache-config

   /**
    * Create a new EntityBeanMetaData.
    */
   public JBossEntityBeanMetaData()
   {
      // For serialization
   }

   @Override
   public EntityBeanMetaData getOverridenMetaData()
   {
      return (EntityBeanMetaData) super.getOverridenMetaData();
   }
   
   @XmlTransient
   public void setOverridenMetaData(EntityBeanMetaData data)
   {
      super.setOverridenMetaData(data);
   }

   @Override
   public boolean isEntity()
   {
      return true;
   }

   @Override
   public String getDefaultConfigurationName()
   {
      boolean isCMP = true;
      boolean isCMP1x = false;
      EntityBeanMetaData overriden = getOverridenMetaData();
      if (overriden != null)
      {
         isCMP = overriden.isCMP();
         isCMP1x = overriden.isCMP1x();
      }

      if (isCMP)
      {
         if (isCMP1x)
         {
            if (isClustered())
               return ContainerConfigurationMetaData.CLUSTERED_CMP_1x;
            else
               return ContainerConfigurationMetaData.CMP_1x;
         }
         else
         {
            if (isClustered())
               return ContainerConfigurationMetaData.CLUSTERED_CMP_2x;
            else
               return ContainerConfigurationMetaData.CMP_2x;
         }
      }
      else
      {
         if (isClustered())
            return ContainerConfigurationMetaData.CLUSTERED_BMP;
         else
            return ContainerConfigurationMetaData.BMP;
      }
   }

   @Override
   public String getDefaultInvokerName()
   {
      boolean isCMP = true;
      boolean isCMP1x = false;
      EntityBeanMetaData overriden = getOverridenMetaData();
      if (overriden != null)
      {
         isCMP = overriden.isCMP();
         isCMP1x = overriden.isCMP1x();
      }

      if (isCMP)
      {
         if (isCMP1x)
         {
            if (isClustered())
               return InvokerBindingMetaData.CLUSTERED_CMP_1x;
            else
               return InvokerBindingMetaData.CMP_1x;
         }
         else
         {
            if (isClustered())
               return InvokerBindingMetaData.CLUSTERED_CMP_2x;
            else
               return InvokerBindingMetaData.CMP_2x;
         }
      }
      else
      {
         if (isClustered())
            return InvokerBindingMetaData.CLUSTERED_BMP;
         else
            return InvokerBindingMetaData.BMP;
      }
   }

   /**
    * Get the jndiName.
    * 
    * @return the jndiName.
    */
   public String getJndiName()
   {
      return jndiName;
   }

   /**
    * Set the jndiName.
    * 
    * @param jndiName the jndiName.
    * @throws IllegalArgumentException for a null jndiName
    */
   public void setJndiName(String jndiName)
   {
      if (jndiName == null)
         throw new IllegalArgumentException("Null jndiName");
      this.jndiName = jndiName;
   }

   /**
    * Determine the jndi name
    * 
    * @return the jndi name
    */
   public String determineJndiName()
   {
      if (jndiName != null)
         return jndiName;
      
      EntityBeanMetaData overriden = getOverridenMetaData();
      if (overriden != null)
      {
         String mapped = overriden.getMappedName();
         if (mapped != null)
            return mapped;
      }
      return getEjbName();
   }

   @Override
   public String getContainerObjectNameJndiName()
   {
      boolean remote = false;
      EntityBeanMetaData entity = getOverridenMetaData();
      if (entity != null && entity.getHome() != null)
         remote = true;
      return remote ? determineJndiName() : getLocalJndiName();
   }
   
   @Override
   protected String getDefaultInvokerJndiName()
   {
      return determineJndiName();
   }

   /**
    * Get the callByValue.
    * 
    * @return the callByValue.
    */
   public boolean isCallByValue()
   {
      return callByValue;
   }

   /**
    * Set the callByValue.
    * 
    * @param callByValue the callByValue.
    */
   public void setCallByValue(boolean callByValue)
   {
      this.callByValue = callByValue;
   }

   /**
    * Get the clustered.
    * 
    * @return the clustered.
    */
   public boolean isClustered()
   {
      return clustered;
   }

   /**
    * Set the clustered.
    * 
    * @param clustered the clustered.
    */
   public void setClustered(boolean clustered)
   {
      this.clustered = clustered;
   }

   /**
    * Get the readOnly.
    * 
    * @return the readOnly.
    */
   public boolean isReadOnly()
   {
      return readOnly;
   }

   /**
    * Set the readOnly.
    * 
    * @param readOnly the readOnly.
    */
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }

   /**
    * Get the clusterConfig.
    * 
    * @return the clusterConfig.
    */
   public ClusterConfigMetaData getClusterConfig()
   {
      return clusterConfig;
   }

   /**
    * Determine the clusterConfig.
    * 
    * @return the clusterConfig.
    */
   public ClusterConfigMetaData determineClusterConfig()
   {
      if (determinedClusterConfig != null)
         return determinedClusterConfig;
      determinedClusterConfig = clusterConfig;
      if (determinedClusterConfig == null)
      {
         ContainerConfigurationMetaData container = determineContainerConfiguration();
         if (container != null)
            determinedClusterConfig = container.getClusterConfig();
      }
      if (determinedClusterConfig == null)
         determinedClusterConfig = new ClusterConfigMetaData();
      return determinedClusterConfig;
   }

   /**
    * Set the clusterConfig.
    * 
    * @param clusterConfig the clusterConfig.
    * @throws IllegalArgumentException for a null clusterConfig
    */
   public void setClusterConfig(ClusterConfigMetaData clusterConfig)
   {
      if (clusterConfig == null)
         throw new IllegalArgumentException("Null clusterConfig");
      clusterConfig.setEnterpriseBeanMetaData(this);
      this.clusterConfig = clusterConfig;
   }

   /**
    * Get the cacheInvalidation.
    * 
    * @return the cacheInvalidation.
    */
   public boolean isCacheInvalidation()
   {
      return cacheInvalidation;
   }

   /**
    * Set the cacheInvalidation.
    * 
    * @param cacheInvalidation the cacheInvalidation.
    */
   public void setCacheInvalidation(boolean cacheInvalidation)
   {
      this.cacheInvalidation = cacheInvalidation;
   }

   /**
    * Get the cacheInvalidationConfig.
    * 
    * @return the cacheInvalidationConfig.
    */
   public CacheInvalidationConfigMetaData getCacheInvalidationConfig()
   {
      return cacheInvalidationConfig;
   }

   /**
    * Set the cacheInvalidationConfig.
    * 
    * @param cacheInvalidationConfig the cacheInvalidationConfig.
    * @throws IllegalArgumentException for a null cacheInvalidationConfig
    */
   public void setCacheInvalidationConfig(CacheInvalidationConfigMetaData cacheInvalidationConfig)
   {
      if (cacheInvalidationConfig == null)
         throw new IllegalArgumentException("Null cacheInvalidationConfig");
      cacheInvalidationConfig.setEntityBean(this);
      this.cacheInvalidationConfig = cacheInvalidationConfig;
   }
}
