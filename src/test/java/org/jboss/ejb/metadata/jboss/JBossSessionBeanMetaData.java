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

import org.jboss.ejb.metadata.spec.SecurityIdentityMetaData;
import org.jboss.ejb.metadata.spec.SessionBeanMetaData;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * SessionBeanMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jboss-session-beanType")
public class JBossSessionBeanMetaData extends JBossEnterpriseBeanMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 720735017632869718L;
   
   /** The jndi name */
   private String jndiName;
   
   /** Whether to call by value */
   private boolean callByValue;
 
   /** The remote binding */
   private RemoteBindingMetaData remoteBinding;
   
   /** Whether this bean is clustered */
   private boolean clustered;
   
   /** The cluster config */
   private ClusterConfigMetaData clusterConfig;
   
   /** The determined cluster config */
   private transient ClusterConfigMetaData determinedClusterConfig;
   
   // TODO webservice port-component
   
   /** The ejb timeout identity */
   private SecurityIdentityMetaData ejbTimeoutIdentity;

   // TODO DOM cache config
   
   /** Whether the bean is concurrent */
   private boolean concurrent = false;
   
   /**
    * Create a new SessionBeanMetaData.
    */
   public JBossSessionBeanMetaData()
   {
      // For serialization
   }

   @Override
   public SessionBeanMetaData getOverridenMetaData()
   {
      return (SessionBeanMetaData) super.getOverridenMetaData();
   }
   
   @XmlTransient
   public void setOverridenMetaData(SessionBeanMetaData data)
   {
      super.setOverridenMetaData(data);
   }

   @Override
   public boolean isSession()
   {
      return true;
   }

   @Override
   public String getDefaultConfigurationName()
   {
      boolean stateful = false;
      SessionBeanMetaData overriden = getOverridenMetaData();
      if (overriden != null)
         stateful = overriden.isStateful();

      if (stateful)
      {
         if (isClustered())
            return ContainerConfigurationMetaData.CLUSTERED_STATEFUL;
         else
            return ContainerConfigurationMetaData.STATEFUL;
      }
      else
      {
         if (isClustered())
            return ContainerConfigurationMetaData.CLUSTERED_STATELESS;
         else
            return ContainerConfigurationMetaData.STATELESS;
      }
   }

   @Override
   public String getDefaultInvokerName()
   {
      boolean stateful = false;
      SessionBeanMetaData overriden = getOverridenMetaData();
      if (overriden != null)
         stateful = overriden.isStateful();

      if (stateful)
      {
         if (isClustered())
            return InvokerBindingMetaData.CLUSTERED_STATEFUL;
         else
            return InvokerBindingMetaData.STATEFUL;
      }
      else
      {
         if (isClustered())
            return InvokerBindingMetaData.CLUSTERED_STATELESS;
         else
            return InvokerBindingMetaData.STATELESS;
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
      
      SessionBeanMetaData overriden = getOverridenMetaData();
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
      SessionBeanMetaData session = getOverridenMetaData();
      if (session != null && session.getHome() != null)
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
    * Get the concurrent.
    * 
    * @return the concurrent.
    */
   public boolean isConcurrent()
   {
      return concurrent;
   }

   /**
    * Set the concurrent.
    * 
    * @param concurrent the concurrent.
    */
   public void setConcurrent(boolean concurrent)
   {
      this.concurrent = concurrent;
   }

   /**
    * Get the ejbTimeoutIdentity.
    * 
    * @return the ejbTimeoutIdentity.
    */
   public SecurityIdentityMetaData getEjbTimeoutIdentity()
   {
      return ejbTimeoutIdentity;
   }

   /**
    * Set the ejbTimeoutIdentity.
    * 
    * @param ejbTimeoutIdentity the ejbTimeoutIdentity.
    * @throws IllegalArgumentException for a null ejbTimeoutIdentity
    */
   public void setEjbTimeoutIdentity(SecurityIdentityMetaData ejbTimeoutIdentity)
   {
      if (ejbTimeoutIdentity == null)
         throw new IllegalArgumentException("Null ejbTimeoutIdentity");
      this.ejbTimeoutIdentity = ejbTimeoutIdentity;
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
    * Get the remoteBinding.
    * 
    * @return the remoteBinding.
    */
   public RemoteBindingMetaData getRemoteBinding()
   {
      return remoteBinding;
   }

   /**
    * Set the remoteBinding.
    * 
    * @param remoteBinding the remoteBinding.
    * @throws IllegalArgumentException for a null remoteBinding
    */
   public void setRemoteBinding(RemoteBindingMetaData remoteBinding)
   {
      if (remoteBinding == null)
         throw new IllegalArgumentException("Null remoteBinding");
      this.remoteBinding = remoteBinding;
   }
}
