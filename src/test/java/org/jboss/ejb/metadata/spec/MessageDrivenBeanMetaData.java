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
package org.jboss.ejb.metadata.spec;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * MessageDrivenBeanMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="message-driven-beanType")
public class MessageDrivenBeanMetaData extends EnterpriseBeanMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -7353017099819838715L;
   
   /** The messaging type */
   private String messagingType;
   
   /** The timeout method */
   private NamedMethodMetaData timeoutMethod;

   /** The transaction type */
   private TransactionType transactionType;
   
   /** The message destination type */
   private String messageDestinationType;
   
   /** The message destination link */
   private String messageDestinationLink;

   /** The activation config */
   private ActivationConfigMetaData activationConfig;
   
   /** The around invokes */
   private AroundInvokesMetaData aroundInvokes;

   /** The message selector */
   private String messageSelector;
   
   /** The acknowledge mode */
   private String acknowledgeMode;
   
   /** The subscription durability */
   private SubscriptionDurability subscriptionDurability = SubscriptionDurability.NonDurable;

   /**
    * Create a new MessageDrivenBeanMetaData.
    */
   public MessageDrivenBeanMetaData()
   {
      // For serialization
   }

   @Override
   public boolean isMessageDriven()
   {
      return true;
   }


   /**
    * Get the messagingType.
    * 
    * @return the messagingType.
    */
   public String getMessagingType()
   {
      return messagingType;
   }

   /**
    * Is this JMS
    * 
    * @return true for jms
    */
   public boolean isJMS()
   {
      return "javax.jms.MessageListener".equals(getMessagingType());
   }


   /**
    * Set the messagingType.
    * 
    * @param messagingType the messagingType.
    * @throws IllegalArgumentException for a null messagingType
    */
   public void setMessagingType(String messagingType)
   {
      if (messagingType == null)
         throw new IllegalArgumentException("Null messagingType");
      this.messagingType = messagingType;
   }


   /**
    * Get the timeoutMethod.
    * 
    * @return the timeoutMethod.
    */
   public NamedMethodMetaData getTimeoutMethod()
   {
      return timeoutMethod;
   }


   /**
    * Set the timeoutMethod.
    * 
    * @param timeoutMethod the timeoutMethod.
    * @throws IllegalArgumentException for a null timeoutMethod
    */
   @XmlElement(required=false)
   public void setTimeoutMethod(NamedMethodMetaData timeoutMethod)
   {
      if (timeoutMethod == null)
         throw new IllegalArgumentException("Null timeoutMethod");
      this.timeoutMethod = timeoutMethod;
   }


   @Override
   public TransactionType getTransactionType()
   {
      return transactionType;
   }


   /**
    * Set the transactionType.
    * 
    * @param transactionType the transactionType.
    * @throws IllegalArgumentException for a null transactionType
    */
   public void setTransactionType(TransactionType transactionType)
   {
      if (transactionType == null)
         throw new IllegalArgumentException("Null transactionType");
      this.transactionType = transactionType;
   }


   /**
    * Get the messageDestinationType.
    * 
    * @return the messageDestinationType.
    */
   public String getMessageDestinationType()
   {
      return messageDestinationType;
   }


   /**
    * Set the messageDestinationType.
    * 
    * @param messageDestinationType the messageDestinationType.
    * @throws IllegalArgumentException for a null messageDestinationType
    */
   public void setMessageDestinationType(String messageDestinationType)
   {
      if (messageDestinationType == null)
         throw new IllegalArgumentException("Null messageDestinationType");
      this.messageDestinationType = messageDestinationType;
   }


   /**
    * Get the aroundInvokes.
    * 
    * @return the aroundInvokes.
    */
   public AroundInvokesMetaData getAroundInvokes()
   {
      return aroundInvokes;
   }


   /**
    * Set the aroundInvokes.
    * 
    * @param aroundInvokes the aroundInvokes.
    * @throws IllegalArgumentException for a null aroundInvokes
    */
   @XmlElement(name="around-invoke", required=false)
   public void setAroundInvokes(AroundInvokesMetaData aroundInvokes)
   {
      if (aroundInvokes == null)
         throw new IllegalArgumentException("Null aroundInvokes");
      this.aroundInvokes = aroundInvokes;
   }


   /**
    * Get the messageDestinationLink.
    * 
    * @return the messageDestinationLink.
    */
   public String getMessageDestinationLink()
   {
      return messageDestinationLink;
   }


   /**
    * Set the messageDestinationLink.
    * 
    * @param messageDestinationLink the messageDestinationLink.
    * @throws IllegalArgumentException for a null messageDestinationLink
    */
   public void setMessageDestinationLink(String messageDestinationLink)
   {
      if (messageDestinationLink == null)
         throw new IllegalArgumentException("Null messageDestinationLink");
      this.messageDestinationLink = messageDestinationLink;
   }


   /**
    * Get the activationConfig.
    * 
    * @return the activationConfig.
    */
   public ActivationConfigMetaData getActivationConfig()
   {
      return activationConfig;
   }


   /**
    * Set the activationConfig.
    * 
    * @param activationConfig the activationConfig.
    * @throws IllegalArgumentException for a null activationConfig
    */
   public void setActivationConfig(ActivationConfigMetaData activationConfig)
   {
      if (activationConfig == null)
         throw new IllegalArgumentException("Null activationConfig");
      this.activationConfig = activationConfig;
   }

   /**
    * Get the messageSelector.
    * 
    * @return the messageSelector.
    */
   public String getMessageSelector()
   {
      return messageSelector;
   }

   /**
    * Set the messageSelector.
    * 
    * @param messageSelector the messageSelector.
    * @throws IllegalArgumentException for a null messageSelector
    */
   @XmlElement(required=false)
   public void setMessageSelector(String messageSelector)
   {
      if (messageSelector == null)
         throw new IllegalArgumentException("Null messageSelector");
      this.messageSelector = messageSelector;
   }

   /**
    * Get the acknowledgeMode.
    * 
    * @return the acknowledgeMode.
    */
   public String getAcknowledgeMode()
   {
      return acknowledgeMode;
   }

   /**
    * Set the acknowledgeMode.
    * 
    * @param acknowledgeMode the acknowledgeMode.
    * @throws IllegalArgumentException for a null acknowledgeMode
    */
   @XmlElement(required=false)
   public void setAcknowledgeMode(String acknowledgeMode)
   {
      if (acknowledgeMode == null)
         throw new IllegalArgumentException("Null acknowledgeMode");
      this.acknowledgeMode = acknowledgeMode;
   }

   /**
    * Get the subscriptionDurability.
    * 
    * @return the subscriptionDurability.
    */
   public SubscriptionDurability getSubscriptionDurability()
   {
      return subscriptionDurability;
   }

   /**
    * Set the subscriptionDurability.
    * 
    * @param subscriptionDurability the subscriptionDurability.
    * @throws IllegalArgumentException for a null subscriptionDurability
    */
   @XmlElement(required=false)
   public void setSubscriptionDurability(SubscriptionDurability subscriptionDurability)
   {
      if (subscriptionDurability == null)
         throw new IllegalArgumentException("Null subscriptionDurability");
      this.subscriptionDurability = subscriptionDurability;
   }
}
