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

import org.jboss.javaee.metadata.spec.MessageDestinationMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationsMetaData;
import org.jboss.javaee.metadata.spec.SecurityRolesMetaData;
import org.jboss.javaee.metadata.support.IdMetaDataImpl;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * AssemblyDescriptorMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="assembly-descriptorType")
public class AssemblyDescriptorMetaData extends IdMetaDataImpl
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 7634431073492003512L;

   /** The security roles */
   private SecurityRolesMetaData securityRoles;

   /** The method permissions */
   private MethodPermissionsMetaData methodPermissions;
   
   /** The container transactions */
   private ContainerTransactionsMetaData containerTransactions;
   
   /** The interceptor bindings */
   private InterceptorBindingsMetaData interceptorBindings;

   /** The message destinations */
   private MessageDestinationsMetaData messageDestinations;
   
   /** The exclude list */
   private ExcludeListMetaData excludeList;
   
   /** The application exceptions */
   private ApplicationExceptionsMetaData applicationExceptions;

   /**
    * Create a new AssemblyDescriptorMetaData
    */
   public AssemblyDescriptorMetaData()
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
    * Get the methodPermissions.
    * 
    * @return the methodPermissions.
    */
   public MethodPermissionsMetaData getMethodPermissions()
   {
      return methodPermissions;
   }

   /**
    * Set the methodPermissions.
    * 
    * @param methodPermissions the methodPermissions.
    * @throws IllegalArgumentException for a null methodPermissions
    */
   @XmlElement(name="method-permission")
   public void setMethodPermissions(MethodPermissionsMetaData methodPermissions)
   {
      if (methodPermissions == null)
         throw new IllegalArgumentException("Null methodPermissions");
      this.methodPermissions = methodPermissions;
   }

   /**
    * Get the methods permissions for an ejb
    * 
    * @param ejbName the ejb name
    * @return the method permissions or null for no result
    * @throws IllegalArgumentException for a null ejb name
    */
   public MethodPermissionsMetaData getMethodPermissionsByEjbName(String ejbName)
   {
      if (ejbName == null)
         throw new IllegalArgumentException("Null ejbName");

      if (methodPermissions == null)
         return null;
      return methodPermissions.getMethodPermissionsByEjbName(ejbName);
   }

   /**
    * Get the containerTransactions.
    * 
    * @return the containerTransactions.
    */
   public ContainerTransactionsMetaData getContainerTransactions()
   {
      return containerTransactions;
   }

   /**
    * Set the containerTransactions.
    * 
    * @param containerTransactions the containerTransactions.
    * @throws IllegalArgumentException for a null containerTransactions
    */
   @XmlElement(name="container-transaction")
   public void setContainerTransactions(ContainerTransactionsMetaData containerTransactions)
   {
      if (containerTransactions == null)
         throw new IllegalArgumentException("Null containerTransactions");
      this.containerTransactions = containerTransactions;
   }

   /**
    * Get the container transactions for an ejb
    * 
    * @param ejbName the ejb name
    * @return the container transactions or null for no result
    * @throws IllegalArgumentException for a null ejb name
    */
   public ContainerTransactionsMetaData getContainerTransactionsByEjbName(String ejbName)
   {
      if (ejbName == null)
         throw new IllegalArgumentException("Null ejbName");

      if (containerTransactions == null)
         return null;
      return containerTransactions.getContainerTransactionsByEjbName(ejbName);
   }

   /**
    * Get the interceptorBindings.
    * 
    * @return the interceptorBindings.
    */
   public InterceptorBindingsMetaData getInterceptorBindings()
   {
      return interceptorBindings;
   }

   /**
    * Set the interceptorBindings.
    * 
    * @param interceptorBindings the interceptorBindings.
    * @throws IllegalArgumentException for a null interceptorBindings
    */
   @XmlElement(name="interceptor-binding", required=false)
   public void setInterceptorBindings(InterceptorBindingsMetaData interceptorBindings)
   {
      if (interceptorBindings == null)
         throw new IllegalArgumentException("Null interceptorBindings");
      this.interceptorBindings = interceptorBindings;
   }

   /**
    * Get the interceptor binding for an ejb
    * 
    * @param ejbName the ejb name
    * @return the interceptor binding or null for no result
    * @throws IllegalArgumentException for a null ejb name
    */
   public InterceptorBindingMetaData getInterceptorBindingByEjbName(String ejbName)
   {
      if (ejbName == null)
         throw new IllegalArgumentException("Null ejbName");

      if (interceptorBindings == null)
         return null;
      return interceptorBindings.get(ejbName);
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
   
   /**
    * Get the excludeList.
    * 
    * @return the excludeList.
    */
   public ExcludeListMetaData getExcludeList()
   {
      return excludeList;
   }

   /**
    * Set the excludeList.
    * 
    * @param excludeList the excludeList.
    * @throws IllegalArgumentException for a null excludeList
    */
   public void setExcludeList(ExcludeListMetaData excludeList)
   {
      if (excludeList == null)
         throw new IllegalArgumentException("Null excludeList");
      this.excludeList = excludeList;
   }

   /**
    * Get the exclude list for an ejb
    * 
    * @param ejbName the ejb name
    * @return the exclude list or null for no result
    * @throws IllegalArgumentException for a null ejb name
    */
   public ExcludeListMetaData getExcludeListByEjbName(String ejbName)
   {
      if (ejbName == null)
         throw new IllegalArgumentException("Null ejbName");

      if (excludeList == null)
         return null;
      return excludeList.getExcludeListByEjbName(ejbName);
   }

   /**
    * Get the applicationExceptions.
    * 
    * @return the applicationExceptions.
    */
   public ApplicationExceptionsMetaData getApplicationExceptions()
   {
      return applicationExceptions;
   }

   /**
    * Set the applicationExceptions.
    * 
    * @param applicationExceptions the applicationExceptions.
    * @throws IllegalArgumentException for a null applicationExceptions
    */
   @XmlElement(name="application-exception", required=false)
   public void setApplicationExceptions(ApplicationExceptionsMetaData applicationExceptions)
   {
      if (applicationExceptions == null)
         throw new IllegalArgumentException("Null applicationExceptions");
      this.applicationExceptions = applicationExceptions;
   }
}
