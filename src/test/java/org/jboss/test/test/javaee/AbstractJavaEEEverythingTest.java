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
package org.jboss.test.javaee;

import java.util.Set;

import org.jboss.javaee.annotation.Description;
import org.jboss.javaee.annotation.Descriptions;
import org.jboss.javaee.annotation.DisplayName;
import org.jboss.javaee.annotation.DisplayNames;
import org.jboss.javaee.annotation.Icon;
import org.jboss.javaee.annotation.Icons;
import org.jboss.javaee.metadata.jboss.AnnotationMetaData;
import org.jboss.javaee.metadata.jboss.AnnotationPropertiesMetaData;
import org.jboss.javaee.metadata.jboss.AnnotationPropertyMetaData;
import org.jboss.javaee.metadata.jboss.AnnotationsMetaData;
import org.jboss.javaee.metadata.jboss.JndiRefMetaData;
import org.jboss.javaee.metadata.jboss.JndiRefsMetaData;
import org.jboss.javaee.metadata.spec.DescriptionGroupMetaData;
import org.jboss.javaee.metadata.spec.DescriptionsImpl;
import org.jboss.javaee.metadata.spec.DisplayNamesImpl;
import org.jboss.javaee.metadata.spec.EJBLocalReferenceMetaData;
import org.jboss.javaee.metadata.spec.EJBLocalReferencesMetaData;
import org.jboss.javaee.metadata.spec.EJBReferenceMetaData;
import org.jboss.javaee.metadata.spec.EJBReferenceType;
import org.jboss.javaee.metadata.spec.EJBReferencesMetaData;
import org.jboss.javaee.metadata.spec.EmptyMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentEntriesMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentEntryMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentRefsGroupMetaData;
import org.jboss.javaee.metadata.spec.IconsImpl;
import org.jboss.javaee.metadata.spec.LifecycleCallbackMetaData;
import org.jboss.javaee.metadata.spec.LifecycleCallbacksMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationReferenceMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationReferencesMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationUsageType;
import org.jboss.javaee.metadata.spec.MessageDestinationsMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextReferenceMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextReferencesMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextType;
import org.jboss.javaee.metadata.spec.PersistenceUnitReferenceMetaData;
import org.jboss.javaee.metadata.spec.PersistenceUnitReferencesMetaData;
import org.jboss.javaee.metadata.spec.PropertiesMetaData;
import org.jboss.javaee.metadata.spec.PropertyMetaData;
import org.jboss.javaee.metadata.spec.ResourceAuthorityType;
import org.jboss.javaee.metadata.spec.ResourceEnvironmentReferenceMetaData;
import org.jboss.javaee.metadata.spec.ResourceEnvironmentReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceInjectionMetaData;
import org.jboss.javaee.metadata.spec.ResourceInjectionTargetMetaData;
import org.jboss.javaee.metadata.spec.ResourceReferenceMetaData;
import org.jboss.javaee.metadata.spec.ResourceReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceSharingScopeType;
import org.jboss.javaee.metadata.spec.SecurityRoleMetaData;
import org.jboss.javaee.metadata.spec.SecurityRolesMetaData;
import org.jboss.javaee.metadata.support.IdMetaData;
//import org.jboss.metadata.EjbLocalRefMetaData;
//import org.jboss.metadata.EjbRefMetaData;
//import org.jboss.metadata.EnvEntryMetaData;
//import org.jboss.metadata.MessageDestinationRefMetaData;
//import org.jboss.metadata.ResourceEnvRefMetaData;
//import org.jboss.metadata.ResourceRefMetaData;
import org.jboss.test.javaee.metadata.AbstractJavaEEMetaDataTest;

/**
 * AbstractJavaEEEverythingTest.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractJavaEEEverythingTest extends AbstractJavaEEMetaDataTest
{
   public AbstractJavaEEEverythingTest(String name)
   {
      super(name);
   }
   
   protected void assertId(String prefix, Object object)
   {
      assertNotNull(object);
      assertTrue(object instanceof IdMetaData);
      IdMetaData idMetaData = (IdMetaData) object;
      assertEquals(prefix + "-id", idMetaData.getId());
   }
   
   protected void fixUpId(String prefix, Object object)
   {
      assertNotNull(object);
      assertTrue(object instanceof IdMetaData);
      IdMetaData idMetaData = (IdMetaData) object;
      idMetaData.setId(prefix + "-id");
   }
   
   protected void assertDescriptionGroup(String suffix, DescriptionGroupMetaData group)
   {
      assertNotNull(group);
      assertDescriptions(suffix, group.getDescriptions());
      assertDisplayNames(suffix, group.getDisplayNames());
      assertIcons(suffix, group.getIcons());
   }
   
   protected void assertDescriptions(String suffix, Descriptions descriptions)
   {
      assertNotNull(descriptions);
      assertTrue(descriptions instanceof DescriptionsImpl);
      DescriptionsImpl impl = (DescriptionsImpl) descriptions;
      assertEquals(3, impl.size());
      assertDescription("en", suffix, impl);
      assertDescription("fr", suffix, impl);
      assertDescription("de", suffix, impl);
   }
   
   protected void assertDescription(String lang, String suffix, DescriptionsImpl impl)
   {
      Description description = impl.get(lang);
      assertNotNull(description);
      assertEquals(lang + "-" + suffix + "-desc", description.value());
   }
   
   protected void assertDisplayNames(String suffix, DisplayNames displayNames)
   {
      assertNotNull(displayNames);
      assertTrue(displayNames instanceof DisplayNamesImpl);
      DisplayNamesImpl impl = (DisplayNamesImpl) displayNames;
      assertEquals(3, impl.size());
      assertDisplayName("en", suffix, impl);
      assertDisplayName("fr", suffix, impl);
      assertDisplayName("de", suffix, impl);
   }
   
   protected void assertDisplayName(String lang, String suffix, DisplayNamesImpl impl)
   {
      DisplayName displayName = impl.get(lang);
      assertNotNull(displayName);
      assertEquals(lang + "-" + suffix + "-disp", displayName.value());
   }
   
   protected void assertIcons(String suffix, Icons icons)
   {
      assertNotNull(icons);
      assertTrue(icons instanceof IconsImpl);
      IconsImpl impl = (IconsImpl) icons;
      assertEquals(3, impl.size());
      assertIcon("en", suffix, impl);
      assertIcon("fr", suffix, impl);
      assertIcon("de", suffix, impl);
   }
   
   protected void assertIcon(String lang, String suffix, IconsImpl impl)
   {
      Icon icon = impl.get(lang);
      assertNotNull(icon);
      assertEquals(lang + "-" + suffix + "-small-icon", icon.smallIcon());
      assertEquals(lang + "-" + suffix + "-large-icon", icon.largeIcon());
      assertId(lang + "-" + suffix + "-icon", icon);
   }

   protected void assertClass(String prefix, String type, String className)
   {
      assertEquals(prefix + type, className);
   }
   
   protected void assertEmpty(String prefix, String type, EmptyMetaData emptyMetaData)
   {
      assertNotNull(emptyMetaData);
      assertId(prefix + type, emptyMetaData);
   }

   protected void assertSecurityRoles(int size, SecurityRolesMetaData securityRolesMetaData)
   {
      assertNotNull(securityRolesMetaData);
      assertEquals(size, securityRolesMetaData.size());
      int count = 1;
      for (SecurityRoleMetaData securityRoleMetaData : securityRolesMetaData)
      {
         assertSecurityRole("securityRole" + count, securityRoleMetaData);
         ++count;
      }
   }

   protected void assertSecurityRole(String prefix, SecurityRoleMetaData securityRoleMetaData)
   {
      assertNotNull(securityRoleMetaData);
      assertId(prefix, securityRoleMetaData);
      assertDescriptions(prefix, securityRoleMetaData.getDescriptions());
      assertEquals(prefix + "RoleName", securityRoleMetaData.getRoleName());
   }

   protected void assertPrincipals(String prefix, int size, Set<String> principals)
   {
      assertNotNull(principals);
      assertEquals(size, principals.size());
      for(int count = 1; count <= principals.size(); ++count)
      {
         assertTrue(principals.contains(prefix + "Principal" + count));
      }
   }

/*   protected void assertSecurityRoles(int size, Map<String, org.jboss.security.SecurityRoleMetaData> securityRoles)
   {
      assertNotNull(securityRoles);
      assertEquals(size, securityRoles.size());
      int count = 1;
      for (org.jboss.security.SecurityRoleMetaData securityRoleMetaData : securityRoles.values())
      {
         assertSecurityRole("securityRole" + count, securityRoleMetaData);
         ++count;
      }
   }

   protected void assertSecurityRole(String prefix, org.jboss.security.SecurityRoleMetaData securityRole)
   {
      assertNotNull(securityRole);
      assertEquals(prefix + "RoleName", securityRole.getRoleName());
   }
*/
   protected void assertMessageDestinations(int size, MessageDestinationsMetaData messageDestinationsMetaData)
   {
      assertNotNull(messageDestinationsMetaData);
      assertEquals(size, messageDestinationsMetaData.size());
      int count = 1;
      for (MessageDestinationMetaData messageDestinationMetaData : messageDestinationsMetaData)
      {
         assertMessageDestination("messageDestination" + count, messageDestinationMetaData);
         ++count;
      }
   }

   protected void fixUpMessageDestinations(int size, MessageDestinationsMetaData messageDestinationsMetaData)
   {
      assertEmpty(messageDestinationsMetaData);
      for (int i = 1; i <= size; ++i)
      {
         MessageDestinationMetaData messageDestinationMetaData = new MessageDestinationMetaData();
         String prefix = "messageDestination" + i;
         fixUpId(prefix, messageDestinationMetaData);
         messageDestinationMetaData.setMessageDestinationName(prefix + "Name");
         messageDestinationMetaData.setMappedName(prefix + "MappedName");
         messageDestinationsMetaData.add(messageDestinationMetaData);
      }
   }

   protected void assertMessageDestination(String prefix, MessageDestinationMetaData messageDestinationMetaData)
   {
      assertMessageDestination14(prefix, messageDestinationMetaData);
   }

   protected void assertMessageDestination14(String prefix, MessageDestinationMetaData messageDestinationMetaData)
   {
      assertNotNull(messageDestinationMetaData);
      assertId(prefix, messageDestinationMetaData);
      assertDescriptionGroup(prefix, messageDestinationMetaData.getDescriptionGroup());
      assertEquals(prefix + "Name", messageDestinationMetaData.getMessageDestinationName());
   }

   protected void assertMessageDestination50(String prefix, MessageDestinationMetaData messageDestinationMetaData)
   {
      assertMessageDestination14(prefix, messageDestinationMetaData);
      assertEquals(prefix + "MappedName", messageDestinationMetaData.getMappedName());
   }

/*   protected void assertMessageDestination(String prefix, org.jboss.metadata.MessageDestinationMetaData messageDestinationMetaData)
   {
      assertMessageDestination14(prefix, messageDestinationMetaData);
   }

   protected void assertMessageDestination14(String prefix, org.jboss.metadata.MessageDestinationMetaData messageDestinationMetaData)
   {
      assertNotNull(messageDestinationMetaData);
      assertEquals(prefix + "Name", messageDestinationMetaData.getName());
   }

   protected void assertMessageDestination50(String prefix, org.jboss.metadata.MessageDestinationMetaData messageDestinationMetaData)
   {
      assertMessageDestination14(prefix, messageDestinationMetaData);
      assertEquals(prefix + "MappedName", messageDestinationMetaData.getJNDIName());
   }
*/
   protected void assertLifecycleCallbacks(String ejbName, String type, int size, LifecycleCallbacksMetaData lifecycleCallbacksMetaData)
   {
      assertNotNull(lifecycleCallbacksMetaData);
      assertEquals(size, lifecycleCallbacksMetaData.size());
      int count = 1;
      for (LifecycleCallbackMetaData lifecycleCallback : lifecycleCallbacksMetaData)
      {
         assertEquals(ejbName + type + count + "Class", lifecycleCallback.getClassName());
         assertEquals(ejbName + type + count + "Method", lifecycleCallback.getMethodName());
         ++count;
      }
   }

   protected void assertEnvironment(String prefix, EnvironmentRefsGroupMetaData environment, boolean full)
   {
      assertNotNull(environment);
      if (full)
         assertEnvEntries(prefix, 2, environment.getEnvironmentEntries(), full);
      assertEjbRefs(prefix, 2, environment.getEjbReferences(), full);
      assertEjbLocalRefs(prefix, 2, environment.getEjbLocalReferences(), full);
      // TODO service-refGroup 
      assertResourceRefs(prefix, 2, environment.getResourceReferences(), full);
      assertResourceEnvRefs(prefix, 2, environment.getResourceEnvironmentReferences(), full);
      assertMessageDestinationRefs(prefix, 3, environment.getMessageDestinationReferences(), full);
      if (full)
      {
         assertPersistenceContextRefs(prefix, 2, environment.getPersistenceContextRefs());
         assertPersistenceUnitRefs(prefix, 2, environment.getPersistenceUnitRefs());
         assertLifecycleCallbacks(prefix, "PostConstruct", 2, environment.getPostConstructs());
         assertLifecycleCallbacks(prefix, "PreDestroy", 2, environment.getPreDestroys());
      }
   }

   protected void assertNullEnvironment(EnvironmentRefsGroupMetaData environment)
   {
      if (environment != null)
      {
         assertNull(environment.getEnvironmentEntries());
         assertNull(environment.getEjbReferences());
         assertNull(environment.getEjbLocalReferences());
         assertNull(environment.getServiceReferences());
         assertNull(environment.getResourceReferences());
         assertNull(environment.getResourceEnvironmentReferences());
         assertNull(environment.getMessageDestinationReferences());
         assertNull(environment.getPersistenceContextRefs());
         assertNull(environment.getPersistenceUnitRefs());
         assertNull(environment.getPostConstructs());
         assertNull(environment.getPreDestroys());
      }
   }

   private void assertEnvEntries(String prefix, int size, EnvironmentEntriesMetaData environmentEntriesMetaData, boolean full)
   {
      assertNotNull(environmentEntriesMetaData);
      assertEquals(size, environmentEntriesMetaData.size());
      int count = 1;
      for (EnvironmentEntryMetaData environmentEntryMetaData : environmentEntriesMetaData)
      {
         assertId(prefix + "EnvEntry" + count, environmentEntryMetaData);
         assertDescriptions(prefix + "EnvEntry" + count, environmentEntryMetaData.getDescriptions());
         assertEquals(prefix + "EnvEntry" + count + "Name", environmentEntryMetaData.getEnvEntryName());
         assertEquals(prefix + "EnvEntry" + count + "Type", environmentEntryMetaData.getType());
         assertEquals(prefix + "EnvEntry" + count + "Value", environmentEntryMetaData.getValue());
         assertResourceGroup(prefix + "EnvEntry" + count, environmentEntryMetaData, full, count == 1);
         ++count;
      }
   }
/*   
   protected void assertEnvEntries(String prefix, int size, Iterator<EnvEntryMetaData> envEntries, boolean full)
   {
      assertNotNull(envEntries);
      int count = 1;
      while (envEntries.hasNext())
      {
         EnvEntryMetaData envEntry = envEntries.next();
         String pref = prefix + "EnvEntry" + count;
         assertEquals(pref + "Name", envEntry.getName());
         assertEquals(pref + "Type", envEntry.getType());
         assertEquals(pref + "Value", envEntry.getValue());
         ++count;
      }
      assertEquals(size + 1, count);
   }
*/
   protected EJBReferencesMetaData fixUpEjbRefs(EJBReferencesMetaData overriden, EJBReferencesMetaData override)
   {
      if (override == null || override.isEmpty())
         return null;
      
      if (overriden == null)
         overriden = new EJBReferencesMetaData();
      
      for (EJBReferenceMetaData ref : override)
      {
         String name = ref.getName();
         if (overriden.get(name) == null)
         {
            EJBReferenceMetaData r = new EJBReferenceMetaData();
            r.setName(name);
            overriden.add(r);
         }
      }
      
      return overriden;
   }
   
   private void assertEjbRefs(String prefix, int size, EJBReferencesMetaData ejbReferencesMetaData, boolean full)
   {
      assertNotNull(ejbReferencesMetaData);
      assertEquals(size, ejbReferencesMetaData.size());
      int count = 1;
      for (EJBReferenceMetaData ejbReferenceMetaData : ejbReferencesMetaData)
      {
         assertId(prefix + "EjbRef" + count, ejbReferenceMetaData);
         assertDescriptions(prefix + "EjbRef" + count, ejbReferenceMetaData.getDescriptions());
         assertEquals(prefix + "EjbRef" + count + "Name", ejbReferenceMetaData.getEjbRefName());
         if (full)
         {
            if (count == 1)
               assertEquals(EJBReferenceType.Session, ejbReferenceMetaData.getEjbRefType());
            else
               assertEquals(EJBReferenceType.Entity, ejbReferenceMetaData.getEjbRefType());
            assertEquals(prefix + "EjbRef" + count + "Home", ejbReferenceMetaData.getHome());
            assertEquals(prefix + "EjbRef" + count + "Remote", ejbReferenceMetaData.getRemote());
            assertEquals(prefix + "EjbRef" + count + "EjbLink", ejbReferenceMetaData.getLink());
         }
         assertResourceGroup(prefix + "EjbRef" + count, ejbReferenceMetaData, full, count == 1);
         ++count;
      }
   }
/*   
   protected void assertEjbRefs(String prefix, int size, Iterator<EjbRefMetaData> ejbRefs, boolean full)
   {
      assertNotNull(ejbRefs);
      int count = 1;
      while (ejbRefs.hasNext())
      {
         EjbRefMetaData ejbRef = ejbRefs.next();
         String pref = prefix + "EjbRef" + count;
         assertEquals(pref + "Name", ejbRef.getName());
         if (full)
         {
            if (count == 1)
               assertEquals("Session", ejbRef.getType());
            else
               assertEquals("Entity", ejbRef.getType());
            assertEquals(pref + "Home", ejbRef.getHome());
            assertEquals(pref + "Remote", ejbRef.getRemote());
            assertEquals(pref + "EjbLink", ejbRef.getLink());
         }
         assertJndiName(pref, full, ejbRef.getJndiName());
         ++count;
      }
      assertEquals(size + 1, count);
   }
*/
   protected EJBLocalReferencesMetaData fixUpEjbLocalRefs(EJBLocalReferencesMetaData overriden, EJBLocalReferencesMetaData override)
   {
      if (override == null || override.isEmpty())
         return null;
      
      if (overriden == null)
         overriden = new EJBLocalReferencesMetaData();
      
      for (EJBLocalReferenceMetaData ref : override)
      {
         String name = ref.getName();
         if (overriden.get(name) == null)
         {
            EJBLocalReferenceMetaData r = new EJBLocalReferenceMetaData();
            r.setName(name);
            overriden.add(r);
         }
      }
      
      return overriden;
   }

   private void assertEjbLocalRefs(String prefix, int size, EJBLocalReferencesMetaData ejbReferencesMetaData, boolean full)
   {
      assertNotNull(ejbReferencesMetaData);
      assertEquals(size, ejbReferencesMetaData.size());
      int count = 1;
      for (EJBLocalReferenceMetaData ejbReferenceMetaData : ejbReferencesMetaData)
      {
         assertId(prefix + "EjbLocalRef" + count, ejbReferenceMetaData);
         assertDescriptions(prefix + "EjbLocalRef" + count, ejbReferenceMetaData.getDescriptions());
         assertEquals(prefix + "EjbLocalRef" + count + "Name", ejbReferenceMetaData.getEjbRefName());
         if (full)
         {
            if (count == 1)
               assertEquals(EJBReferenceType.Session, ejbReferenceMetaData.getEjbRefType());
            else
               assertEquals(EJBReferenceType.Entity, ejbReferenceMetaData.getEjbRefType());
            assertEquals(prefix + "EjbLocalRef" + count + "LocalHome", ejbReferenceMetaData.getLocalHome());
            assertEquals(prefix + "EjbLocalRef" + count + "Local", ejbReferenceMetaData.getLocal());
            assertEquals(prefix + "EjbLocalRef" + count + "EjbLink", ejbReferenceMetaData.getLink());
         }
         assertResourceGroup(prefix + "EjbLocalRef" + count, ejbReferenceMetaData, full, count == 1);
         ++count;
      }
   }
/*   
   protected void assertEjbLocalRefs(String prefix, int size, Iterator<EjbLocalRefMetaData> ejbLocalRefs, boolean full)
   {
      assertNotNull(ejbLocalRefs);
      int count = 1;
      while (ejbLocalRefs.hasNext())
      {
         EjbLocalRefMetaData ejbLocalRef = ejbLocalRefs.next();
         String pref = prefix + "EjbLocalRef" + count;
         assertEquals(pref + "Name", ejbLocalRef.getName());
         if (full)
         {
            if (count == 1)
               assertEquals("Session", ejbLocalRef.getType());
            else
               assertEquals("Entity", ejbLocalRef.getType());
            assertEquals(pref + "LocalHome", ejbLocalRef.getLocalHome());
            assertEquals(pref + "Local", ejbLocalRef.getLocal());
            assertEquals(pref + "EjbLink", ejbLocalRef.getLink());
         }
         assertJndiName(pref, full, ejbLocalRef.getJndiName());
         ++count;
      }
      assertEquals(size + 1, count);
   }
*/
   protected ResourceReferencesMetaData fixUpResourceRefs(ResourceReferencesMetaData overriden, ResourceReferencesMetaData override)
   {
      if (override == null || override.isEmpty())
         return null;
      
      if (overriden == null)
         overriden = new ResourceReferencesMetaData();
      
      for (ResourceReferenceMetaData ref : override)
      {
         String name = ref.getName();
         if (overriden.get(name) == null)
         {
            ResourceReferenceMetaData r = new ResourceReferenceMetaData();
            r.setName(name);
            overriden.add(r);
         }
      }
      
      return overriden;
   }

   private void assertResourceRefs(String prefix, int size, ResourceReferencesMetaData resourceReferencesMetaData, boolean full)
   {
      assertNotNull(resourceReferencesMetaData);
      assertEquals(size, resourceReferencesMetaData.size());
      int count = 1;
      for (ResourceReferenceMetaData resourceReferenceMetaData : resourceReferencesMetaData)
      {
         assertId(prefix + "ResourceRef" + count, resourceReferenceMetaData);
         assertDescriptions(prefix + "ResourceRef" + count, resourceReferenceMetaData.getDescriptions());
         assertEquals(prefix + "ResourceRef" + count + "Name", resourceReferenceMetaData.getResourceRefName());
         if (full)
         {
            assertEquals(prefix + "ResourceRef" + count + "Type", resourceReferenceMetaData.getType());
            if (count == 1)
            {
               assertEquals(ResourceAuthorityType.Application, resourceReferenceMetaData.getResAuth());
               assertEquals(ResourceSharingScopeType.Shareable, resourceReferenceMetaData.getResSharingScope());
            }
            else
            {
               assertEquals(ResourceAuthorityType.Container, resourceReferenceMetaData.getResAuth());
               assertEquals(ResourceSharingScopeType.Unshareable, resourceReferenceMetaData.getResSharingScope());
            }
         }
         assertResourceGroup(prefix + "ResourceRef" + count, resourceReferenceMetaData, full, count == 1);
         ++count;
      }
   }
/*   
   protected void assertResourceRefs(String prefix, int size, Iterator<ResourceRefMetaData> resRefs, boolean full)
   {
      assertNotNull(resRefs);
      int count = 1;
      while (resRefs.hasNext())
      {
         ResourceRefMetaData resRef = resRefs.next();
         String pref = prefix + "ResourceRef" + count;
         assertEquals(pref + "Name", resRef.getName());
         if (full)
         {
            assertEquals(pref + "Type", resRef.getType());
            if (count == 1)
            {
               assertFalse(pref + "Auth", resRef.isContainerAuth());
               assertTrue(pref + "Scope", resRef.isShareable());
            }
            else
            {
               assertTrue(pref + "Auth", resRef.isContainerAuth());
               assertFalse(pref + "Scope", resRef.isShareable());
            }
         }
         assertJndiName(pref, full, resRef.getJndiName());
         ++count;
      }
      assertEquals(size + 1, count);
   }
*/
   protected ResourceEnvironmentReferencesMetaData fixUpResourceEnvRefs(ResourceEnvironmentReferencesMetaData overriden, ResourceEnvironmentReferencesMetaData override)
   {
      if (override == null || override.isEmpty())
         return null;
      
      if (overriden == null)
         overriden = new ResourceEnvironmentReferencesMetaData();
      
      for (ResourceEnvironmentReferenceMetaData ref : override)
      {
         String name = ref.getName();
         if (overriden.get(name) == null)
         {
            ResourceEnvironmentReferenceMetaData r = new ResourceEnvironmentReferenceMetaData();
            r.setName(name);
            overriden.add(r);
         }
      }
      
      return overriden;
   }

   private void assertResourceEnvRefs(String prefix, int size, ResourceEnvironmentReferencesMetaData resourceEnvReferencesMetaData, boolean full)
   {
      assertNotNull(resourceEnvReferencesMetaData);
      assertEquals(size, resourceEnvReferencesMetaData.size());
      int count = 1;
      for (ResourceEnvironmentReferenceMetaData resourceEnvReferenceMetaData : resourceEnvReferencesMetaData)
      {
         assertId(prefix + "ResourceEnvRef" + count, resourceEnvReferenceMetaData);
         assertDescriptions(prefix + "ResourceEnvRef" + count, resourceEnvReferenceMetaData.getDescriptions());
         assertEquals(prefix + "ResourceEnvRef" + count + "Name", resourceEnvReferenceMetaData.getResourceEnvRefName());
         if (full)
            assertEquals(prefix + "ResourceEnvRef" + count + "Type", resourceEnvReferenceMetaData.getType());
         assertResourceGroup(prefix + "ResourceEnvRef" + count, resourceEnvReferenceMetaData, full, count == 1);
         ++count;
      }
   }
/*   
   protected void assertResourceEnvRefs(String prefix, int size, Iterator<ResourceEnvRefMetaData> resEnvRefs, boolean full)
   {
      assertNotNull(resEnvRefs);
      int count = 1;
      while (resEnvRefs.hasNext())
      {
         ResourceEnvRefMetaData resRef = resEnvRefs.next();
         String pref = prefix + "ResourceEnvRef" + count;
         assertEquals(pref + "Name", resRef.getName());
         if (full)
         {
            assertEquals(pref + "Type", resRef.getType());
         }
         assertJndiName(pref, full, resRef.getJndiName());
         ++count;
      }
      assertEquals(size + 1, count);
   }
*/
   protected MessageDestinationReferencesMetaData fixUpMessageDestinationRefs(MessageDestinationReferencesMetaData overriden, MessageDestinationReferencesMetaData override)
   {
      if (override == null || override.isEmpty())
         return null;
      
      if (overriden == null)
         overriden = new MessageDestinationReferencesMetaData();
      
      for (MessageDestinationReferenceMetaData ref : override)
      {
         String name = ref.getName();
         if (overriden.get(name) == null)
         {
            MessageDestinationReferenceMetaData r = new MessageDestinationReferenceMetaData();
            r.setName(name);
            overriden.add(r);
         }
      }
      
      return overriden;
   }

   private void assertMessageDestinationRefs(String prefix, int size, MessageDestinationReferencesMetaData messageDestinationReferencesMetaData, boolean full)
   {
      assertNotNull(messageDestinationReferencesMetaData);
      assertEquals(size, messageDestinationReferencesMetaData.size());
      int count = 1;
      for (MessageDestinationReferenceMetaData messageDestinationReferenceMetaData : messageDestinationReferencesMetaData)
      {
         assertId(prefix + "MessageDestinationRef" + count, messageDestinationReferenceMetaData);
         assertDescriptions(prefix + "MessageDestinationRef" + count, messageDestinationReferenceMetaData.getDescriptions());
         assertEquals(prefix + "MessageDestinationRef" + count + "Name", messageDestinationReferenceMetaData.getMessageDestinationRefName());
         if (full)
         {
            assertEquals(prefix + "MessageDestinationRef" + count + "Type", messageDestinationReferenceMetaData.getType());
            if (count == 1)
               assertEquals(MessageDestinationUsageType.Consumes, messageDestinationReferenceMetaData.getMessageDestinationUsage());
            else if (count == 2)
               assertEquals(MessageDestinationUsageType.Produces, messageDestinationReferenceMetaData.getMessageDestinationUsage());
            else
               assertEquals(MessageDestinationUsageType.ConsumesProduces, messageDestinationReferenceMetaData.getMessageDestinationUsage());
            assertEquals(prefix + "MessageDestinationRef" + count + "Link", messageDestinationReferenceMetaData.getLink());
         }
         assertResourceGroup(prefix + "MessageDestinationRef" + count, messageDestinationReferenceMetaData, full, count == 1);
         ++count;
      }
   }
/*   
   protected void assertMessageDestinationRefs(String prefix, int size, Iterator<MessageDestinationRefMetaData> refs, boolean full)
   {
      assertNotNull(refs);
      int count = 1;
      while (refs.hasNext())
      {
         MessageDestinationRefMetaData ref = refs.next();
         String pref = prefix + "MessageDestinationRef" + count;
         assertEquals(pref + "Name", ref.getRefName());
         if (full)
         {
            assertEquals(pref + "Type", ref.getType());
            if (count == 1)
               assertEquals(MessageDestinationRefMetaData.CONSUMES, ref.getUsage());
            else if (count == 2)
               assertEquals(MessageDestinationRefMetaData.PRODUCES, ref.getUsage());
            else
               assertEquals(MessageDestinationRefMetaData.CONSUMESPRODUCES, ref.getUsage());
            assertEquals(prefix + "MessageDestinationRef" + count + "Link", ref.getLink());
         }
         ++count;
      }
      assertEquals(size + 1, count);
   }
*/
   protected void assertPersistenceContextRefs(String prefix, int size, PersistenceContextReferencesMetaData persistenceContextReferencesMetaData)
   {
      assertNotNull(persistenceContextReferencesMetaData);
      assertEquals(size, persistenceContextReferencesMetaData.size());
      int count = 1;
      for (PersistenceContextReferenceMetaData persistenceContextReferenceMetaData : persistenceContextReferencesMetaData)
      {
         assertId(prefix + "PersistenceContextRef" + count, persistenceContextReferenceMetaData);
         assertDescriptions(prefix + "PersistenceContextRef" + count, persistenceContextReferenceMetaData.getDescriptions());
         assertEquals(prefix + "PersistenceContextRef" + count + "Name", persistenceContextReferenceMetaData.getPersistenceContextRefName());
         assertEquals(prefix + "PersistenceContextRef" + count + "Unit", persistenceContextReferenceMetaData.getPersistenceUnitName());
         if (count == 1)
            assertEquals(PersistenceContextType.Transaction, persistenceContextReferenceMetaData.getPersistenceContextType());
         else
            assertEquals(PersistenceContextType.Extended, persistenceContextReferenceMetaData.getPersistenceContextType());
         assertProperties(prefix + "PersistenceContextRef" + count, 2, persistenceContextReferenceMetaData.getProperties());
         assertResourceGroup(prefix + "PersistenceContextRef" + count, persistenceContextReferenceMetaData, true, count == 1);
         ++count;
      }
   }

   protected void assertPersistenceUnitRefs(String prefix, int size, PersistenceUnitReferencesMetaData persistenceUnitReferencesMetaData)
   {
      assertNotNull(persistenceUnitReferencesMetaData);
      assertEquals(size, persistenceUnitReferencesMetaData.size());
      int count = 1;
      for (PersistenceUnitReferenceMetaData persistenceUnitReferenceMetaData : persistenceUnitReferencesMetaData)
      {
         assertId(prefix + "PersistenceUnitRef" + count, persistenceUnitReferenceMetaData);
         assertDescriptions(prefix + "PersistenceUnitRef" + count, persistenceUnitReferenceMetaData.getDescriptions());
         assertEquals(prefix + "PersistenceUnitRef" + count + "Name", persistenceUnitReferenceMetaData.getPersistenceUnitRefName());
         assertEquals(prefix + "PersistenceUnitRef" + count + "Unit", persistenceUnitReferenceMetaData.getPersistenceUnitName());
         assertResourceGroup(prefix + "PersistenceUnitRef" + count, persistenceUnitReferenceMetaData, true, count == 1);
         ++count;
      }
   }

   private void assertProperties(String prefix, int size, PropertiesMetaData propertiesMetaData)
   {
      assertNotNull(propertiesMetaData);
      assertEquals(size, propertiesMetaData.size());
      int count = 1;
      for (PropertyMetaData property : propertiesMetaData)
      {
         assertId(prefix + "Property" + count, property);
         assertEquals(prefix + "Property" + count + "Name", property.getName());
         assertEquals(prefix + "Property" + count + "Value", property.getValue());
         ++count;
      }
   }

   protected void assertResourceGroupNoJndiName(String prefix, ResourceInjectionMetaData resourceInjectionMetaData, boolean full, boolean first)
   {
      assertNotNull(resourceInjectionMetaData);
      assertResourceInjectionTargets(prefix, 2, resourceInjectionMetaData.getInjectionTargets());
   }

   protected void assertResourceGroup(String prefix, ResourceInjectionMetaData resourceInjectionMetaData, boolean full, boolean first)
   {
      assertResourceGroupJndiName(prefix, resourceInjectionMetaData, full, first);
   }

   private void assertResourceGroupJndiName(String prefix, ResourceInjectionMetaData resourceInjectionMetaData, boolean full, boolean first)
   {
      assertResourceGroupNoJndiName(prefix, resourceInjectionMetaData, full, first);
      assertJndiName(prefix, full, resourceInjectionMetaData.getMappedName());
   }

   protected void assertResourceInjectionTargets(String prefix, int size, Set<ResourceInjectionTargetMetaData> targets)
   {
      assertNotNull(targets);
      assertEquals(targets.toString(), size, targets.size());
      int count = 0;
      while (++count <= targets.size())
      {
         ResourceInjectionTargetMetaData expected = new ResourceInjectionTargetMetaData();
         expected.setInjectionTargetClass(prefix + "Injection" + count + "Class");
         expected.setInjectionTargetName(prefix + "Injection" + count + "Name");
         assertTrue(targets.contains(expected));
      }
   }

   protected void assertResourceInjectionTarget(String prefix, ResourceInjectionTargetMetaData target)
   {
      assertNotNull(target);
      assertEquals(prefix + "Class", target.getInjectionTargetClass());
      assertEquals(prefix + "Name", target.getInjectionTargetName());
   }
   
   protected void assertJndiName(String prefix, boolean full, String jndiName)
   {
      assertNotNull(jndiName);
      if (full)
         assertEquals(prefix + "MappedName", jndiName);
      else
         assertEquals(prefix + "JndiName", jndiName);
   }

   protected void assertAnnotations(String prefix, int size, AnnotationsMetaData annotationsMetaData)
   {
      assertNotNull(annotationsMetaData);
      assertEquals(size, annotationsMetaData.size());
      int count = 1;
      for (AnnotationMetaData annotation : annotationsMetaData)
      {
         assertId(prefix + "Annotation" + count, annotation);
         assertDescriptions(prefix + "Annotation" + count, annotation.getDescriptions());
         assertEquals(prefix + "Annotation" + count + "Class", annotation.getAnnotationClass());
         assertEquals(prefix + "Annotation" + count + "Impl", annotation.getAnnotationImplementationClass());
         assertResourceInjectionTarget(prefix + "Annotation" + count + "InjectionTarget", annotation.getInjectionTarget());
         assertAnnotationProperties(prefix + "Annotation" + count, 2, annotation.getProperties());
         ++count;
      }
   }

   private void assertAnnotationProperties(String prefix, int size, AnnotationPropertiesMetaData annotationPropertiesMetaData)
   {
      assertNotNull(annotationPropertiesMetaData);
      assertEquals(size, annotationPropertiesMetaData.size());
      int count = 1;
      for (AnnotationPropertyMetaData annotationProperty : annotationPropertiesMetaData)
      {
         assertId(prefix + "Property" + count, annotationProperty);
         assertDescriptions(prefix + "Property" + count, annotationProperty.getDescriptions());
         assertEquals(prefix + "Property" + count + "Name", annotationProperty.getPropertyName());
         assertEquals(prefix + "Property" + count + "Value", annotationProperty.getPropertyValue());
         ++count;
      }
   }

   protected void assertJndiRefs(String prefix, int size, JndiRefsMetaData jndiRefsMetaData)
   {
      assertNotNull(jndiRefsMetaData);
      assertEquals(size, jndiRefsMetaData.size());
      int count = 1;
      for (JndiRefMetaData jndiRef : jndiRefsMetaData)
      {
         assertId(prefix + "JndiRef" + count, jndiRef);
         assertDescriptions(prefix + "JndiRef" + count, jndiRef.getDescriptions());
         assertEquals(prefix + "JndiRef" + count + "Name", jndiRef.getJndiRefName());
         assertResourceGroupJndiName(prefix + "JndiRef" + count, jndiRef, true, count == 1);
         ++count;
      }
   }
}
