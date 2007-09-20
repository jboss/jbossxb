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

import org.jboss.ejb.metadata.spec.ActivationConfigMetaData;
import org.jboss.ejb.metadata.spec.MessageDrivenBeanMetaData;
import org.jboss.ejb.metadata.spec.SecurityIdentityMetaData;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * MessageDrivenBeanMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jboss-message-driven-beanType")
public class JBossMessageDrivenBeanMetaData extends JBossEnterpriseBeanMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -4006016148034278681L;
   
   /** The destination jndi name */
   private String destinationJndiName;

   /** The mdb user id */
   private String mdbUser;
   
   /** The mdb password */
   private String mdbPassword;

   /** The mdb client id */
   private String mdbClientId;

   /** The mdb subscription id */
   private String mdbSubscriptionId;

   /** The resource adapter name */
   private String resourceAdapterName;

   /** The ejb timeout identity */
   private SecurityIdentityMetaData ejbTimeoutIdentity;

   /** The default activation config */
   private ActivationConfigMetaData defaultActivationConfig;

   /**
    * Create a new MessageDrivenBeanMetaData.
    */
   public JBossMessageDrivenBeanMetaData()
   {
      // For serialization
   }

   @Override
   public MessageDrivenBeanMetaData getOverridenMetaData()
   {
      return (MessageDrivenBeanMetaData) super.getOverridenMetaData();
   }
   
   @XmlTransient
   public void setOverridenMetaData(MessageDrivenBeanMetaData data)
   {
      super.setOverridenMetaData(data);
   }

   @Override
   public boolean isMessageDriven()
   {
      return true;
   }

   @Override
   public String getDefaultConfigurationName()
   {
      boolean isJMS = true;
      MessageDrivenBeanMetaData overriden = getOverridenMetaData();
      if (overriden != null)
         isJMS = overriden.isJMS();

      if (isJMS == false)
         return ContainerConfigurationMetaData.MESSAGE_INFLOW_DRIVEN;
      else
         return ContainerConfigurationMetaData.MESSAGE_DRIVEN;
   }

   @Override
   public String getDefaultInvokerName()
   {
      return InvokerBindingMetaData.MESSAGE_DRIVEN;
   }

   /**
    * Get the destinationJndiName.
    * 
    * @return the destinationJndiName.
    */
   public String getDestinationJndiName()
   {
      return destinationJndiName;
   }

   /**
    * Set the destinationJndiName.
    * 
    * @param destinationJndiName the destinationJndiName.
    * @throws IllegalArgumentException for a null destinationJndiName
    */
   public void setDestinationJndiName(String destinationJndiName)
   {
      if (destinationJndiName == null)
         throw new IllegalArgumentException("Null destinationJndiName");
      this.destinationJndiName = destinationJndiName;
   }

   /**
    * Get the mdbUser.
    * 
    * @return the mdbUser.
    */
   public String getMdbUser()
   {
      return mdbUser;
   }

   /**
    * Set the mdbUser.
    * 
    * @param mdbUser the mdbUser.
    * @throws IllegalArgumentException for a null mdbUser
    */
   public void setMdbUser(String mdbUser)
   {
      if (mdbUser == null)
         throw new IllegalArgumentException("Null mdbUser");
      this.mdbUser = mdbUser;
   }

   /**
    * Get the mdbPassword.
    * 
    * @return the mdbPassword.
    */
   public String getMdbPassword()
   {
      return mdbPassword;
   }

   /**
    * Set the mdbPassword.
    * 
    * @param mdbPassword the mdbPassword.
    * @throws IllegalArgumentException for a null mdbPassword
    */
   @XmlElement(name="mdb-passwd")
   public void setMdbPassword(String mdbPassword)
   {
      if (mdbPassword == null)
         throw new IllegalArgumentException("Null mdbPassword");
      this.mdbPassword = mdbPassword;
   }

   /**
    * Get the mdbClientId.
    * 
    * @return the mdbClientId.
    */
   public String getMdbClientId()
   {
      return mdbClientId;
   }

   /**
    * Set the mdbClientId.
    * 
    * @param mdbClientId the mdbClientId.
    * @throws IllegalArgumentException for a null mdbClientId
    */
   public void setMdbClientId(String mdbClientId)
   {
      if (mdbClientId == null)
         throw new IllegalArgumentException("Null mdbClientId");
      this.mdbClientId = mdbClientId;
   }

   /**
    * Get the resourceAdapterName.
    * 
    * @return the resourceAdapterName.
    */
   public String getResourceAdapterName()
   {
      return resourceAdapterName;
   }

   /**
    * Set the resourceAdapterName.
    * 
    * @param resourceAdapterName the resourceAdapterName.
    * @throws IllegalArgumentException for a null resourceAdapterName
    */
   public void setResourceAdapterName(String resourceAdapterName)
   {
      if (resourceAdapterName == null)
         throw new IllegalArgumentException("Null resourceAdapterName");
      this.resourceAdapterName = resourceAdapterName;
   }

   /**
    * Get the mdbSubscriptionId.
    * 
    * @return the mdbSubscriptionId.
    */
   public String getMdbSubscriptionId()
   {
      return mdbSubscriptionId;
   }

   /**
    * Set the mdbSubscriptionId.
    * 
    * @param mdbSubscriptionId the mdbSubscriptionId.
    * @throws IllegalArgumentException for a null mdbSubscriptionId
    */
   public void setMdbSubscriptionId(String mdbSubscriptionId)
   {
      if (mdbSubscriptionId == null)
         throw new IllegalArgumentException("Null mdbSubscriptionId");
      this.mdbSubscriptionId = mdbSubscriptionId;
   }

   /**
    * Get the defaultActivationConfig.
    * 
    * @return the defaultActivationConfig.
    */
   public ActivationConfigMetaData getDefaultActivationConfig()
   {
      return defaultActivationConfig;
   }

   /**
    * Set the defaultActivationConfig.
    * 
    * @param defaultActivationConfig the defaultActivationConfig.
    * @throws IllegalArgumentException for a null defaultActivationConfig
    */
   public void setDefaultActivationConfig(ActivationConfigMetaData defaultActivationConfig)
   {
      if (defaultActivationConfig == null)
         throw new IllegalArgumentException("Null defaultActivationConfig");
      this.defaultActivationConfig = defaultActivationConfig;
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
}
