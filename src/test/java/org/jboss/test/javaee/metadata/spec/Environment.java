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

/**
 * Environment.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public interface Environment
{
   /**
    * Get the environmentEntries.
    * 
    * @return the environmentEntries.
    */
   EnvironmentEntriesMetaData getEnvironmentEntries();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   EnvironmentEntryMetaData getEnvironmentEntryByName(String name);

   /**
    * Get the ejbLocalReferences.
    * 
    * @return the ejbLocalReferences.
    */
   EJBLocalReferencesMetaData getEjbLocalReferences();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   EJBLocalReferenceMetaData getEjbLocalReferenceByName(String name);

   /**
    * Get the ejbReferences.
    * 
    * @return the ejbReferences.
    */
   EJBReferencesMetaData getEjbReferences();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   EJBReferenceMetaData getEjbReferenceByName(String name);

   /**
    * Get the resourceReferences.
    * 
    * @return the resourceReferences.
    */
   ResourceReferencesMetaData getResourceReferences();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   ResourceReferenceMetaData getResourceReferenceByName(String name);

   /**
    * Get the resourceEnvironmentReferences.
    * 
    * @return the resourceEnvironmentReferences.
    */
   ResourceEnvironmentReferencesMetaData getResourceEnvironmentReferences();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   ResourceEnvironmentReferenceMetaData getResourceEnvironmentReferenceByName(String name);

   /**
    * Get the messageDestinationReferences.
    * 
    * @return the messageDestinationReferences.
    */
   MessageDestinationReferencesMetaData getMessageDestinationReferences();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   MessageDestinationReferenceMetaData getMessageDestinationReferenceByName(String name);

   /**
    * Get the postConstructs.
    * 
    * @return the postConstructs.
    */
   LifecycleCallbacksMetaData getPostConstructs();

   /**
    * Get the preDestroys.
    * 
    * @return the preDestroys.
    */
   LifecycleCallbacksMetaData getPreDestroys();

   /**
    * Get the persistenceContextRefs.
    * 
    * @return the persistenceContextRefs.
    */
   PersistenceContextReferencesMetaData getPersistenceContextRefs();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   PersistenceContextReferenceMetaData getPersistenceContextReferenceByName(String name);

   /**
    * Get the persistenceUnitRefs.
    * 
    * @return the persistenceUnitRefs.
    */
   PersistenceUnitReferencesMetaData getPersistenceUnitRefs();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   PersistenceUnitReferenceMetaData getPersistenceUnitReferenceByName(String name);

   /**
    * Get the serviceReferences.
    * 
    * @return the serviceReferences.
    */
   ServiceReferencesMetaData getServiceReferences();

   /**
    * Get by name
    * 
    * @param name the name
    * @return the result or null if not found
    * @throws IllegalArgumentException for a null name
    */
   ServiceReferenceMetaData getServiceReferenceByName(String name);
}