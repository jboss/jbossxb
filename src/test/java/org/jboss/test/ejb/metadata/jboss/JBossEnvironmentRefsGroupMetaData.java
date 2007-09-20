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
import org.jboss.javaee.metadata.spec.EJBLocalReferencesMetaData;
import org.jboss.javaee.metadata.spec.EJBReferencesMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentEntriesMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentRefsGroupMetaData;
import org.jboss.javaee.metadata.spec.LifecycleCallbacksMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationReferencesMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextReferencesMetaData;
import org.jboss.javaee.metadata.spec.PersistenceUnitReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceEnvironmentReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceReferencesMetaData;
import org.jboss.xb.annotations.JBossXmlModelGroup;

/**
 * JBossEnvironmentRefsGroupMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(name="jbossJndiEnvironmentRefsGroup",
      propOrder={"environmentEntries", "ejbReferences", "ejbLocalReferences",
      "serviceReferences", "securityIdentity", "resourceReferences", "resourceEnvironmentReferences",
      "messageDestinationReferences", "persistenceContextRefs", "persistenceUnitRefs",
      "postConstructs", "preDestroys"})
public class JBossEnvironmentRefsGroupMetaData extends EnvironmentRefsGroupMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 4642263968653845579L;

   /** The security identity */
   // This is only here because historically this was mixed in the environment xml
   private SecurityIdentityMetaData securityIdentity;

   /**
    * Merge an environment
    * 
    * @param jbossEnvironmentRefsGroup the override environment
    * @param environmentRefsGroup the overriden environment
    * @param overridenFile the overriden file name
    * @param overrideFile the override file
    * @return the merged environment
    */
   public static JBossEnvironmentRefsGroupMetaData merge(JBossEnvironmentRefsGroupMetaData jbossEnvironmentRefsGroup, EnvironmentRefsGroupMetaData environmentRefsGroup, String overridenFile, String overrideFile)
   {
      JBossEnvironmentRefsGroupMetaData merged = new JBossEnvironmentRefsGroupMetaData();
      
      if (jbossEnvironmentRefsGroup == null && environmentRefsGroup == null)
         return merged;

      EnvironmentEntriesMetaData envEntries = null;
      EJBReferencesMetaData ejbRefs = null;
      EJBReferencesMetaData jbossEjbRefs = null;
      EJBLocalReferencesMetaData ejbLocalRefs = null;
      EJBLocalReferencesMetaData jbossEjbLocalRefs = null;
      ResourceReferencesMetaData resRefs = null;
      ResourceReferencesMetaData jbossResRefs = null;
      ResourceEnvironmentReferencesMetaData resEnvRefs = null;
      ResourceEnvironmentReferencesMetaData jbossResEnvRefs = null;
      MessageDestinationReferencesMetaData messageDestinationRefs = null;
      MessageDestinationReferencesMetaData jbossMessageDestinationRefs = null;
      PersistenceContextReferencesMetaData persistenceContextRefs = null;
      PersistenceUnitReferencesMetaData persistenceUnitRefs = null;
      LifecycleCallbacksMetaData postConstructs = null;
      LifecycleCallbacksMetaData preDestroys = null;
      
      if (environmentRefsGroup != null)
      {
         envEntries = environmentRefsGroup.getEnvironmentEntries();
         ejbRefs = environmentRefsGroup.getEjbReferences();
         ejbLocalRefs = environmentRefsGroup.getEjbLocalReferences();
         // ServiceRefs
         resRefs = environmentRefsGroup.getResourceReferences();
         resEnvRefs = environmentRefsGroup.getResourceEnvironmentReferences();
         messageDestinationRefs = environmentRefsGroup.getMessageDestinationReferences();
         persistenceContextRefs = environmentRefsGroup.getPersistenceContextRefs();
         persistenceUnitRefs = environmentRefsGroup.getPersistenceUnitRefs();
         postConstructs = environmentRefsGroup.getPostConstructs();
         preDestroys = environmentRefsGroup.getPreDestroys();
      }
      
      if (jbossEnvironmentRefsGroup != null)
      {
         jbossEjbRefs = jbossEnvironmentRefsGroup.getEjbReferences();
         jbossEjbLocalRefs = jbossEnvironmentRefsGroup.getEjbLocalReferences();
         jbossResRefs = jbossEnvironmentRefsGroup.getResourceReferences();
         jbossResEnvRefs = jbossEnvironmentRefsGroup.getResourceEnvironmentReferences();
         jbossMessageDestinationRefs = jbossEnvironmentRefsGroup.getMessageDestinationReferences();
      }
      
      EJBReferencesMetaData mergedEjbRefs = EJBReferencesMetaData.merge(jbossEjbRefs, ejbRefs, overridenFile, overrideFile);
      if (mergedEjbRefs != null)
         merged.setEjbReferences(mergedEjbRefs);
      
      EJBLocalReferencesMetaData mergedEjbLocalRefs = EJBLocalReferencesMetaData.merge(jbossEjbLocalRefs, ejbLocalRefs, overridenFile, overrideFile);
      if (mergedEjbLocalRefs != null)
         merged.setEjbLocalReferences(mergedEjbLocalRefs);
      
      ResourceReferencesMetaData mergedResRefs = ResourceReferencesMetaData.merge(jbossResRefs, resRefs, overridenFile, overrideFile);
      if (mergedResRefs != null)
         merged.setResourceReferences(mergedResRefs);

      ResourceEnvironmentReferencesMetaData mergedResEnvRefs = ResourceEnvironmentReferencesMetaData.merge(jbossResEnvRefs, resEnvRefs, overridenFile, overrideFile);
      if (mergedResEnvRefs != null)
         merged.setResourceEnvironmentReferences(mergedResEnvRefs);

      MessageDestinationReferencesMetaData mergedMessageDestinationRefs = MessageDestinationReferencesMetaData.merge(jbossMessageDestinationRefs, messageDestinationRefs, overridenFile, overrideFile);
      if (mergedMessageDestinationRefs != null)
         merged.setMessageDestinationReferences(mergedMessageDestinationRefs);
      
      if (envEntries != null)
         merged.setEnvironmentEntries(envEntries);
      
      if (persistenceContextRefs != null)
         merged.setPersistenceContextRefs(persistenceContextRefs);
      
      if (persistenceUnitRefs != null)
         merged.setPersistenceUnitRefs(persistenceUnitRefs);
      
      if (postConstructs != null)
         merged.setPostConstructs(postConstructs);
      
      if (preDestroys != null)
         merged.setPreDestroys(preDestroys);
      
      return merged;
   }

   /**
    * Get the securityIdentity.
    * 
    * @return the securityIdentity.
    */
   public SecurityIdentityMetaData getSecurityIdentity()
   {
      return securityIdentity;
   }

   /**
    * Set the securityIdentity.
    * 
    * @param securityIdentity the securityIdentity.
    * @throws IllegalArgumentException for a null securityIdentity
    */
   public void setSecurityIdentity(SecurityIdentityMetaData securityIdentity)
   {
      if (securityIdentity == null)
         throw new IllegalArgumentException("Null securityIdentity");
      this.securityIdentity = securityIdentity;
   }
}
