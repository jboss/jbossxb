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
package org.jboss.javaee.metadata.spec;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.javaee.metadata.support.AbstractMappedMetaData;
import org.jboss.xb.annotations.JBossXmlModelGroup;

/**
 * EnvironmentRefsGroupMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(name="jndiEnvironmentRefsGroup",
      propOrder={"environmentEntries", "ejbReferences", "ejbLocalReferences",
      "serviceReferences", "resourceReferences", "resourceEnvironmentReferences",
      "messageDestinationReferences", "persistenceContextRefs", "persistenceUnitRefs",
      "postConstructs", "preDestroys"})
public class EnvironmentRefsGroupMetaData implements Serializable, Environment
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1337095770028220349L;

   /** The environment entries */
   private EnvironmentEntriesMetaData environmentEntries;

   /** The ejb references */
   private EJBReferencesMetaData ejbReferences;

   /** The ejb local references */
   private EJBLocalReferencesMetaData ejbLocalReferences;
 
   /** The service references */
   private ServiceReferencesMetaData serviceReferences;
   
   /** The resource references */
   private ResourceReferencesMetaData resourceReferences;
   
   /** The resource environment references */
   private ResourceEnvironmentReferencesMetaData resourceEnvironmentReferences;
   
   /** The message destination references */
   private MessageDestinationReferencesMetaData messageDestinationReferences;
   
   /** The persistence context reference */
   private PersistenceContextReferencesMetaData persistenceContextRefs;

   /** The persistence unit  reference */
   private PersistenceUnitReferencesMetaData persistenceUnitRefs;

   /** The post construct methods */
   private LifecycleCallbacksMetaData postConstructs;
   
   /** The pre destroy methods */
   private LifecycleCallbacksMetaData preDestroys;
   
   /**
    * Create a new EnvironmentRefsGroupMetaData.
    */
   public EnvironmentRefsGroupMetaData()
   {
      // For serialization
   }

   /**
    * Get the environmentEntries.
    * 
    * @return the environmentEntries.
    */
   public EnvironmentEntriesMetaData getEnvironmentEntries()
   {
      return environmentEntries;
   }

   /**
    * Set the environmentEntries.
    * 
    * @param environmentEntries the environmentEntries.
    * @throws IllegalArgumentException for a null environmentEntries
    */
   @XmlElement(name="env-entry")
   public void setEnvironmentEntries(EnvironmentEntriesMetaData environmentEntries)
   {
      if (environmentEntries == null)
         throw new IllegalArgumentException("Null environmentEntries");
      this.environmentEntries = environmentEntries;
   }

   /**
    * Get the ejbLocalReferences.
    * 
    * @return the ejbLocalReferences.
    */
   public EJBLocalReferencesMetaData getEjbLocalReferences()
   {
      return ejbLocalReferences;
   }

   /**
    * Set the ejbLocalReferences.
    * 
    * @param ejbLocalReferences the ejbLocalReferences.
    * @throws IllegalArgumentException for a null ejbLocalReferences
    */
   @XmlElement(name="ejb-local-ref")
   public void setEjbLocalReferences(EJBLocalReferencesMetaData ejbLocalReferences)
   {
      if (ejbLocalReferences == null)
         throw new IllegalArgumentException("Null ejbLocalReferences");
      this.ejbLocalReferences = ejbLocalReferences;
   }

   /**
    * Get the ejbReferences.
    * 
    * @return the ejbReferences.
    */
   public EJBReferencesMetaData getEjbReferences()
   {
      return ejbReferences;
   }

   /**
    * Set the ejbReferences.
    * 
    * @param ejbReferences the ejbReferences.
    * @throws IllegalArgumentException for a null ejbReferences
    */
   @XmlElement(name="ejb-ref")
   public void setEjbReferences(EJBReferencesMetaData ejbReferences)
   {
      if (ejbReferences == null)
         throw new IllegalArgumentException("Null ejbReferences");
      this.ejbReferences = ejbReferences;
   }

   /**
    * Get the resourceReferences.
    * 
    * @return the resourceReferences.
    */
   public ResourceReferencesMetaData getResourceReferences()
   {
      return resourceReferences;
   }

   /**
    * Set the resourceReferences.
    * 
    * @param resourceReferences the resourceReferences.
    * @throws IllegalArgumentException for a null resourceReferences
    */
   @XmlElement(name="resource-ref")
   public void setResourceReferences(ResourceReferencesMetaData resourceReferences)
   {
      if (resourceReferences == null)
         throw new IllegalArgumentException("Null resourceReferences");
      this.resourceReferences = resourceReferences;
   }

   /**
    * Get the resourceEnvironmentReferences.
    * 
    * @return the resourceEnvironmentReferences.
    */
   public ResourceEnvironmentReferencesMetaData getResourceEnvironmentReferences()
   {
      return resourceEnvironmentReferences;
   }

   /**
    * Set the resourceEnvironmentReferences.
    * 
    * @param resourceEnvironmentReferences the resourceEnvironmentReferences.
    * @throws IllegalArgumentException for a null resourceEnvironmentReferences
    */
   @XmlElement(name="resource-env-ref")
   public void setResourceEnvironmentReferences(ResourceEnvironmentReferencesMetaData resourceEnvironmentReferences)
   {
      if (resourceEnvironmentReferences == null)
         throw new IllegalArgumentException("Null resourceEnvironmentReferences");
      this.resourceEnvironmentReferences = resourceEnvironmentReferences;
   }

   /**
    * Get the messageDestinationReferences.
    * 
    * @return the messageDestinationReferences.
    */
   public MessageDestinationReferencesMetaData getMessageDestinationReferences()
   {
      return messageDestinationReferences;
   }

   /**
    * Set the messageDestinationReferences.
    * 
    * @param messageDestinationReferences the messageDestinationReferences.
    * @throws IllegalArgumentException for a null messageDestinationReferences
    */
   @XmlElement(name="message-destination-ref")
   public void setMessageDestinationReferences(MessageDestinationReferencesMetaData messageDestinationReferences)
   {
      if (messageDestinationReferences == null)
         throw new IllegalArgumentException("Null messageDestinationReferences");
      this.messageDestinationReferences = messageDestinationReferences;
   }

   /**
    * Get the postConstructs.
    * 
    * @return the postConstructs.
    */
   public LifecycleCallbacksMetaData getPostConstructs()
   {
      return postConstructs;
   }

   /**
    * Set the postConstructs.
    * 
    * @param postConstructs the postConstructs.
    * @throws IllegalArgumentException for a null postConstructs
    */
   //@SchemaProperty(name="post-construct", noInterceptor=true)
   @XmlElement(name="post-construct")
   public void setPostConstructs(LifecycleCallbacksMetaData postConstructs)
   {
      if (postConstructs == null)
         throw new IllegalArgumentException("Null postConstructs");
      this.postConstructs = postConstructs;
   }

   /**
    * Get the preDestroys.
    * 
    * @return the preDestroys.
    */
   public LifecycleCallbacksMetaData getPreDestroys()
   {
      return preDestroys;
   }

   /**
    * Set the preDestroys.
    * 
    * @param preDestroys the preDestroys.
    * @throws IllegalArgumentException for a null preDestroys
    */
   //@SchemaProperty(name="pre-destroy", noInterceptor=true)
   @XmlElement(name="pre-destroy")
   public void setPreDestroys(LifecycleCallbacksMetaData preDestroys)
   {
      if (preDestroys == null)
         throw new IllegalArgumentException("Null preDestroys");
      this.preDestroys = preDestroys;
   }

   /**
    * Get the persistenceContextRefs.
    * 
    * @return the persistenceContextRefs.
    */
   public PersistenceContextReferencesMetaData getPersistenceContextRefs()
   {
      return persistenceContextRefs;
   }

   /**
    * Set the persistenceContextRefs.
    * 
    * @param persistenceContextRefs the persistenceContextRefs.
    * @throws IllegalArgumentException for a null persistenceContextRefs
    */
   @XmlElement(name="persistence-context-ref")
   public void setPersistenceContextRefs(PersistenceContextReferencesMetaData persistenceContextRefs)
   {
      if (persistenceContextRefs == null)
         throw new IllegalArgumentException("Null persistenceContextRefs");
      this.persistenceContextRefs = persistenceContextRefs;
   }

   /**
    * Get the persistenceUnitRefs.
    * 
    * @return the persistenceUnitRefs.
    */
   public PersistenceUnitReferencesMetaData getPersistenceUnitRefs()
   {
      return persistenceUnitRefs;
   }

   /**
    * Set the persistenceUnitRefs.
    * 
    * @param persistenceUnitRefs the persistenceUnitRefs.
    * @throws IllegalArgumentException for a null persistenceUnitRefs
    */
   @XmlElement(name="persistence-unit-ref")
   public void setPersistenceUnitRefs(PersistenceUnitReferencesMetaData persistenceUnitRefs)
   {
      if (persistenceUnitRefs == null)
         throw new IllegalArgumentException("Null persistenceUnitRefs");
      this.persistenceUnitRefs = persistenceUnitRefs;
   }

   /**
    * Get the serviceReferences.
    * 
    * @return the serviceReferences.
    */
   public ServiceReferencesMetaData getServiceReferences()
   {
      return serviceReferences;
   }

   /**
    * Set the serviceReferences.
    * 
    * @param serviceReferences the serviceReferences.
    * @throws IllegalArgumentException for a null serviceReferences
    */
   @XmlElement(name="service-ref")
   public void setServiceReferences(ServiceReferencesMetaData serviceReferences)
   {
      if (serviceReferences == null)
         throw new IllegalArgumentException("Null serviceReferences");
      this.serviceReferences = serviceReferences;
   }
   
   public EJBLocalReferenceMetaData getEjbLocalReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, ejbLocalReferences);
   }

   public EJBReferenceMetaData getEjbReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, ejbReferences);
   }

   public EnvironmentEntryMetaData getEnvironmentEntryByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, environmentEntries);
   }

   public MessageDestinationReferenceMetaData getMessageDestinationReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, messageDestinationReferences);
   }

   public PersistenceContextReferenceMetaData getPersistenceContextReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, persistenceContextRefs);
   }

   public PersistenceUnitReferenceMetaData getPersistenceUnitReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, persistenceUnitRefs);
   }

   public ResourceEnvironmentReferenceMetaData getResourceEnvironmentReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, resourceEnvironmentReferences);
   }

   public ResourceReferenceMetaData getResourceReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, resourceReferences);
   }

   public ServiceReferenceMetaData getServiceReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, serviceReferences);
   }
}
