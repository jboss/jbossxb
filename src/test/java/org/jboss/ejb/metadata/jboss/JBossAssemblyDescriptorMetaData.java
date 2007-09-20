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

import java.util.Collections;
import java.util.Set;

import org.jboss.ejb.metadata.spec.AssemblyDescriptorMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationsMetaData;
import org.jboss.javaee.metadata.spec.SecurityRoleMetaData;
import org.jboss.javaee.metadata.spec.SecurityRolesMetaData;
import org.jboss.javaee.metadata.support.IdMetaDataImplWithOverride;
import org.jboss.javaee.metadata.support.JavaEEMetaDataUtil;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * JBossAssemblyDescriptorMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jboss-assembly-descriptorType")
public class JBossAssemblyDescriptorMetaData extends IdMetaDataImplWithOverride<AssemblyDescriptorMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 5638920200035141015L;

   /** The security roles */
   private SecurityRolesMetaData securityRoles;

   /** The message destinations */
   private MessageDestinationsMetaData messageDestinations;

   /**
    * Merge the assembly descriptors
    * 
    * @param jbossAssemblyDescriptorMetaData the override
    * @param assemblyDescriptorMetaData the overriden
    * @return the merged data
    */
   public static JBossAssemblyDescriptorMetaData merge(JBossAssemblyDescriptorMetaData jbossAssemblyDescriptorMetaData, AssemblyDescriptorMetaData assemblyDescriptorMetaData)
   {
      JBossAssemblyDescriptorMetaData merged = new JBossAssemblyDescriptorMetaData();

      SecurityRolesMetaData securityRolesMetaData = null;
      SecurityRolesMetaData jbossSecurityRolesMetaData = null;
      MessageDestinationsMetaData messageDestinationsMetaData = null;
      MessageDestinationsMetaData jbossMessageDestinationsMetaData = null;
      if (assemblyDescriptorMetaData != null)
      {
         merged.setOverridenMetaData(assemblyDescriptorMetaData);
         securityRolesMetaData = assemblyDescriptorMetaData.getSecurityRoles();
         messageDestinationsMetaData = assemblyDescriptorMetaData.getMessageDestinations();
      }
      if (jbossAssemblyDescriptorMetaData != null)
      {
         jbossSecurityRolesMetaData = jbossAssemblyDescriptorMetaData.getSecurityRoles();
         jbossMessageDestinationsMetaData = jbossAssemblyDescriptorMetaData.getMessageDestinations();
      }

      if (jbossSecurityRolesMetaData == null || jbossSecurityRolesMetaData.isEmpty())
      {
         if (securityRolesMetaData != null)
            merged.setSecurityRoles(securityRolesMetaData);
      }
      else
      {
         SecurityRolesMetaData mergedSecurityRolesMetaData = new SecurityRolesMetaData();
         mergedSecurityRolesMetaData = JavaEEMetaDataUtil.mergeJBossXml(mergedSecurityRolesMetaData, securityRolesMetaData, jbossSecurityRolesMetaData, "security-role", false);
         if (mergedSecurityRolesMetaData != null && mergedSecurityRolesMetaData.isEmpty() == false)
            merged.setSecurityRoles(mergedSecurityRolesMetaData);
      }
      
      if (jbossMessageDestinationsMetaData == null || jbossMessageDestinationsMetaData.isEmpty())
      {
         if (messageDestinationsMetaData != null && jbossMessageDestinationsMetaData == null)
         merged.setMessageDestinations(messageDestinationsMetaData);
      }
      else
      {
         MessageDestinationsMetaData mergedMessageDestinationsMetaData = new MessageDestinationsMetaData();
         mergedMessageDestinationsMetaData = JavaEEMetaDataUtil.mergeJBossXml(mergedMessageDestinationsMetaData, messageDestinationsMetaData, jbossMessageDestinationsMetaData, "message-destination", true);
         if (mergedMessageDestinationsMetaData != null && mergedMessageDestinationsMetaData.isEmpty() == false)
            merged.setMessageDestinations(mergedMessageDestinationsMetaData);
      }

      return merged;
   }

   /**
    * Create a new JBossAssemblyDescriptorMetaData
    */
   public JBossAssemblyDescriptorMetaData()
   {
      // For serialization
   }

   /**
    * Get the securityRoles.
    * 
    * @return the securityRoles.
    */
   public SecurityRolesMetaData getSecurityRoles()
   {
      return securityRoles;
   }

   /**
    * Set the securityRoles.
    * 
    * @param securityRoles the securityRoles.
    * @throws IllegalArgumentException for a null securityRoles
    */
   @XmlElement(name="security-role")
   public void setSecurityRoles(SecurityRolesMetaData securityRoles)
   {
      if (securityRoles == null)
         throw new IllegalArgumentException("Null securityRoles");
      this.securityRoles = securityRoles;
   }

   /**
    * Get a security role
    * 
    * @param name the role name
    * @return the security role or null if not found
    */
   public SecurityRoleMetaData getSecurityRole(String name)
   {
      if (securityRoles == null)
         return null;
      return securityRoles.get(name);
   }

   /**
    * Get a security role's principals
    * 
    * @param name the role name
    * @return the security role principals or null if not found
    */
   public Set<String> getSecurityRolePrincipals(String name)
   {
      if (securityRoles == null)
         return null;
      SecurityRoleMetaData securityRole = securityRoles.get(name);
      if (securityRole == null)
         return null;
      return securityRole.getPrincipals();
   }

   /**
    * Get the security roles by principal
    * 
    * @param userName the principal name
    * @return the security roles containing the principal or null for no roles
    * @throws IllegalArgumentException for a null user name
    */
   public SecurityRolesMetaData getSecurityRolesByPrincipal(String userName)
   {
      if (userName == null)
         throw new IllegalArgumentException("Null userName");
      if (securityRoles == null)
         return null;
      return securityRoles.getSecurityRolesByPrincipal(userName);
   }

   /**
    * Get the security role names by principal
    * 
    * @param userName the principal name
    * @return the security role names containing the principal
    * @throws IllegalArgumentException for a null user name
    */
   public Set<String> getSecurityRoleNamesByPrincipal(String userName)
   {
      if (userName == null)
         throw new IllegalArgumentException("Null userName");
      if (securityRoles == null)
         return Collections.emptySet();
      return securityRoles.getSecurityRoleNamesByPrincipal(userName);
   }

   /**
    * Get the messageDestinations.
    * 
    * @return the messageDestinations.
    */
   public MessageDestinationsMetaData getMessageDestinations()
   {
      return messageDestinations;
   }

   /**
    * Set the messageDestinations.
    * 
    * @param messageDestinations the messageDestinations.
    * @throws IllegalArgumentException for a null messageDestinations
    */
   @XmlElement(name="message-destination")
   public void setMessageDestinations(MessageDestinationsMetaData messageDestinations)
   {
      if (messageDestinations == null)
         throw new IllegalArgumentException("Null messageDestinations");
      this.messageDestinations = messageDestinations;
   }

   /**
    * Get a message destination
    * 
    * @param name the name of the destination
    * @return the destination or null if not found
    */
   public MessageDestinationMetaData getMessageDestination(String name)
   {
      if (messageDestinations == null)
         return null;
      return messageDestinations.get(name);
   }
}
