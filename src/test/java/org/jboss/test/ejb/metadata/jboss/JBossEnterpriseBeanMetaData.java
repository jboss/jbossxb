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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.ExcludeListMetaData;
import org.jboss.ejb.metadata.spec.MethodInterfaceType;
import org.jboss.ejb.metadata.spec.MethodPermissionMetaData;
import org.jboss.ejb.metadata.spec.MethodPermissionsMetaData;
import org.jboss.ejb.metadata.spec.SecurityIdentityMetaData;
import org.jboss.javaee.metadata.jboss.AnnotationsMetaData;
import org.jboss.javaee.metadata.jboss.IgnoreDependencyMetaData;
import org.jboss.javaee.metadata.jboss.JndiRefsMetaData;
import org.jboss.javaee.metadata.spec.EJBLocalReferenceMetaData;
import org.jboss.javaee.metadata.spec.EJBLocalReferencesMetaData;
import org.jboss.javaee.metadata.spec.EJBReferenceMetaData;
import org.jboss.javaee.metadata.spec.EJBReferencesMetaData;
import org.jboss.javaee.metadata.spec.Environment;
import org.jboss.javaee.metadata.spec.EnvironmentEntriesMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentEntryMetaData;
import org.jboss.javaee.metadata.spec.LifecycleCallbacksMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationReferenceMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationReferencesMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextReferenceMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextReferencesMetaData;
import org.jboss.javaee.metadata.spec.PersistenceUnitReferenceMetaData;
import org.jboss.javaee.metadata.spec.PersistenceUnitReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceEnvironmentReferenceMetaData;
import org.jboss.javaee.metadata.spec.ResourceEnvironmentReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceReferenceMetaData;
import org.jboss.javaee.metadata.spec.ResourceReferencesMetaData;
import org.jboss.javaee.metadata.spec.SecurityRoleMetaData;
import org.jboss.javaee.metadata.spec.ServiceReferenceMetaData;
import org.jboss.javaee.metadata.spec.ServiceReferencesMetaData;
import org.jboss.javaee.metadata.support.AbstractMappedMetaData;
import org.jboss.javaee.metadata.support.NamedMetaDataWithDescriptionGroupWithOverride;
import org.jboss.javaee.metadata.support.NonNullLinkedHashSet;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * EnterpriseBean.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(
      kind=JBossXmlConstants.MODEL_GROUP_CHOICE,
      particles={
            @JBossXmlModelGroup.Particle(element=@XmlElement(name="session"), type=JBossSessionBeanMetaData.class),
            @JBossXmlModelGroup.Particle(element=@XmlElement(name="entity"), type=JBossEntityBeanMetaData.class),
            @JBossXmlModelGroup.Particle(element=@XmlElement(name="message-driven"), type=JBossMessageDrivenBeanMetaData.class)})
public abstract class JBossEnterpriseBeanMetaData extends NamedMetaDataWithDescriptionGroupWithOverride<EnterpriseBeanMetaData> implements Environment
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 6909774842926430193L;

   /** The enterprise bean container */
   private JBossEnterpriseBeansMetaData enterpriseBeansMetaData;
   
   /** The local jndi name */
   private String localJndiName;
   
   /** Whether to throw an exception if the transaction is marked for rollback */
   private boolean exceptionOnRollback;
   
   /** Whether to persist timers */
   private boolean timerPersistence = true;
   
   /** The configuration name */
   private String configurationName;
   
   /** The invokers */
   private InvokerBindingsMetaData invokers;
   
   /** The determined invokers */
   private transient InvokerBindingsMetaData determinedInvokers;

   /** The ior security config */
   private IORSecurityConfigMetaData iorSecurityConfig;
   
   /** The security proxy */
   private String securityProxy;

   /** The environment */
   private JBossEnvironmentRefsGroupMetaData jndiEnvironmentRefsGroup;

   /** The merged environment */
   private transient JBossEnvironmentRefsGroupMetaData mergedEnvironment;
   
   /** The method attributes */
   private MethodAttributesMetaData methodAttributes;
   
   /** The security domain */
   private String securityDomain;
   
   /** The dependencies */
   private Set<String> depends;
   
   /** The annotations */
   private AnnotationsMetaData annotations;

   /** Ignore dependency */
   private IgnoreDependencyMetaData ignoreDependency;

   /** The aop domain name */
   private String aopDomainName;
   
   // TODO DOM pool config
   
   /** The jndi refs */
   private JndiRefsMetaData jndiRefs;
   
   /**
    * Create a new EnterpriseBeanMetaData.
    */
   public JBossEnterpriseBeanMetaData()
   {
      // For serialization
   }

   /**
    * Get the enterpriseBeansMetaData.
    * 
    * @return the enterpriseBeansMetaData.
    */
   JBossEnterpriseBeansMetaData getEnterpriseBeansMetaData()
   {
      return enterpriseBeansMetaData;
   }

   /**
    * Set the enterpriseBeansMetaData.
    * 
    * @param enterpriseBeansMetaData the enterpriseBeansMetaData.
    */
   void setEnterpriseBeansMetaData(JBossEnterpriseBeansMetaData enterpriseBeansMetaData)
   {
      this.enterpriseBeansMetaData = enterpriseBeansMetaData;
   }

   /**
    * Get the jbossMetaData.
    * 
    * @return the jbossMetaData.
    */
   @XmlTransient
   public JBossMetaData getJBossMetaData()
   {
      if (enterpriseBeansMetaData == null)
         return null;
      return enterpriseBeansMetaData.getJBossMetaData();
   }
   
   /**
    * Get the jbossMetaData.
    * 
    * @return the jbossMetaData with check
    */
   @XmlTransient
   public JBossMetaData getJBossMetaDataWithCheck()
   {
      JBossMetaData jbossMetaData = getJBossMetaData();
      if (jbossMetaData == null)
         throw new IllegalStateException("This bean is not a part of a deployment " + this);
      return jbossMetaData;
   }

   /**
    * Get the ejbName.
    * 
    * @return the ejbName.
    */
   public String getEjbName()
   {
      return getName();
   }

   /**
    * Set the ejbName.
    * 
    * @param ejbName the ejbName.
    * @throws IllegalArgumentException for a null ejbName
    */
   public void setEjbName(String ejbName)
   {
      setName(ejbName);
   }

   /**
    * Whether this is a session bean
    * 
    * @return true when a session bean
    */
   public boolean isSession()
   {
      return false;
   }

   /**
    * Whether this is a message driven bean
    * 
    * @return true when a message driven bean
    */
   public boolean isMessageDriven()
   {
      return false;
   }

   /**
    * Whether this is an entity bean
    * 
    * @return true when an entity bean
    */
   public boolean isEntity()
   {
      return false;
   }

   /**
    * Get the localJndiName.
    * 
    * @return the localJndiName.
    */
   public String getLocalJndiName()
   {
      return localJndiName;
   }

   /**
    * Set the localJndiName.
    * 
    * @param localJndiName the localJndiName.
    * @throws IllegalArgumentException for a null localJndiName
    */
   public void setLocalJndiName(String localJndiName)
   {
      if (localJndiName == null)
         throw new IllegalArgumentException("Null localJndiName");
      this.localJndiName = localJndiName;
   }

   /**
    * Determine the localJndiName.
    * 
    * @return the localJndiName.
    */
   public String determineLocalJndiName()
   {
      if (localJndiName != null)
         return localJndiName;
      
      String ejbName = getEjbName();
      // Generate a unique name based on ejbName + identityHashCode
      return "local/" + ejbName + '@' + System.identityHashCode(ejbName);
   }

   /**
    * Determine the container jndi name used in the object name
    * 
    * @return the jndi name suitable for use in the object name
    */
   public String getContainerObjectNameJndiName()
   {
      return getLocalJndiName();
   }

   /**
    * Get the exceptionOnRollback.
    * 
    * @return the exceptionOnRollback.
    */
   public boolean isExceptionOnRollback()
   {
      return exceptionOnRollback;
   }

   /**
    * Set the exceptionOnRollback.
    * 
    * @param exceptionOnRollback the exceptionOnRollback.
    */
   public void setExceptionOnRollback(boolean exceptionOnRollback)
   {
      this.exceptionOnRollback = exceptionOnRollback;
   }

   /**
    * Get the timerPersistence.
    * 
    * @return the timerPersistence.
    */
   public boolean isTimerPersistence()
   {
      return timerPersistence;
   }

   /**
    * Set the timerPersistence.
    * 
    * @param timerPersistence the timerPersistence.
    */
   public void setTimerPersistence(boolean timerPersistence)
   {
      this.timerPersistence = timerPersistence;
   }

   /**
    * Get the configurationName.
    * 
    * @return the configurationName.
    */
   public String getConfigurationName()
   {
      return configurationName;
   }

   /**
    * Set the configurationName.
    * 
    * @param configurationName the configurationName.
    * @throws IllegalArgumentException for a null configurationName
    */
   public void setConfigurationName(String configurationName)
   {
      if (configurationName == null)
         throw new IllegalArgumentException("Null configurationName");
      this.configurationName = configurationName;
   }
   
   /**
    * Determine the configuration name
    * 
    * @return the configuration name
    */
   public String determineConfigurationName()
   {
      if (configurationName != null)
         return configurationName;
      
      return getDefaultConfigurationName();
   }

   /**
    * Get the container configuration
    * 
    * @return the container configuration
    */
   public ContainerConfigurationMetaData determineContainerConfiguration()
   {
      String name = determineConfigurationName();
      ContainerConfigurationMetaData result = getJBossMetaDataWithCheck().getContainerConfiguration(name);
      if (result == null)
         throw new IllegalStateException("Container configuration not found: " + name + " available: " +  getJBossMetaDataWithCheck().getContainerConfigurations());
      return result;
   }
   
   /**
    * Get the default configuration name
    * 
    * @return the default name
    */
   public abstract String getDefaultConfigurationName();

   /**
    * Get the securityProxy.
    * 
    * @return the securityProxy.
    */
   public String getSecurityProxy()
   {
      return securityProxy;
   }

   /**
    * Set the securityProxy.
    * 
    * @param securityProxy the securityProxy.
    * @throws IllegalArgumentException for a null securityProxy
    */
   public void setSecurityProxy(String securityProxy)
   {
      if (securityProxy == null)
         throw new IllegalArgumentException("Null securityProxy");
      this.securityProxy = securityProxy;
   }

   /**
    * Get the securityDomain.
    * 
    * @return the securityDomain.
    */
   public String getSecurityDomain()
   {
      return securityDomain;
   }

   /**
    * Set the securityDomain.
    * 
    * @param securityDomain the securityDomain.
    * @throws IllegalArgumentException for a null securityDomain
    */
   @XmlElement(required=false)
   public void setSecurityDomain(String securityDomain)
   {
      if (securityDomain == null)
         throw new IllegalArgumentException("Null securityDomain");
      this.securityDomain = securityDomain;
   }

   /**
    * Get the depends.
    * 
    * @return the depends.
    */
   public Set<String> getDepends()
   {
      return depends;
   }

   /**
    * Set the depends.
    * 
    * @param depends the depends.
    * @throws IllegalArgumentException for a null depends
    */
   @XmlElement(type=NonNullLinkedHashSet.class)
   public void setDepends(Set<String> depends)
   {
      if (depends == null)
         throw new IllegalArgumentException("Null depends");
      this.depends = depends;
   }

   /**
    * Get the depends.
    * 
    * @return the depends.
    */
   public Set<String> determineAllDepends()
   {
      NonNullLinkedHashSet<String> result = new NonNullLinkedHashSet<String>();

      Set<String> depends = getDepends();
      if (depends != null)
         result.addAll(depends);
      
      ContainerConfigurationMetaData containerConfigurationMetaData = determineContainerConfiguration();
      if (containerConfigurationMetaData != null)
      {
         depends = containerConfigurationMetaData.getDepends();
         if (depends != null)
            result.addAll(depends);
      }
      
      return result;
   }

   /**
    * Get the invokers.
    * 
    * @return the invokers.
    */
   public InvokerBindingsMetaData getInvokerBindings()
   {
      return invokers;
   }

   /**
    * Set the invokers.
    * 
    * @param invokers the invokers.
    * @throws IllegalArgumentException for a null invokers
    */
   public void setInvokerBindings(InvokerBindingsMetaData invokers)
   {
      if (invokers == null)
         throw new IllegalArgumentException("Null invokers");
      this.invokers = invokers;
   }

   /**
    * Determine the invokers
    * 
    * @return the invokers.
    */
   public InvokerBindingsMetaData determineInvokerBindings()
   {
      // We already worked it out
      if (determinedInvokers != null)
         return determinedInvokers;
      
      // Use anything configured
      if (invokers != null)
      {
         determinedInvokers = invokers;
         return determinedInvokers;
      }
      
      // Look at the container configuration
      ContainerConfigurationMetaData containerConfiguration = determineContainerConfiguration();
      Set<String> invokerProxyBindingNames = containerConfiguration.getInvokerProxyBindingNames();
      if (invokerProxyBindingNames != null && invokerProxyBindingNames.isEmpty() == false)
      {
         determinedInvokers = new InvokerBindingsMetaData();
         
         // Like the original code, they all get bound with the same name?
         String jndiName = getDefaultInvokerJndiName();
         for (String name : invokerProxyBindingNames)
         {
            InvokerBindingMetaData invoker = new InvokerBindingMetaData();
            invoker.setInvokerProxyBindingName(name);
            if (jndiName != null)
               invoker.setJndiName(jndiName);
         }
         return determinedInvokers;
      }
      
      determinedInvokers = getDefaultInvokers();
      return determinedInvokers;
   }

   /**
    * Determine an invoker binding
    * 
    * @param invokerName the invoker proxy binding name
    * @return the invoke binding
    * @throws IllegalStateException if there is no such binding
    */
   public InvokerBindingMetaData determineInvokerBinding(String invokerName)
   {
      InvokerBindingMetaData binding = determineInvokerBindings().get(invokerName);
      if (binding == null)
          throw new IllegalStateException("No such binding: " + invokerName + " available: " + determinedInvokers);
      return binding;
   }

   /**
    * Determine the jndi name for invoker bindings that come from the container configuration
    * 
    * @return the jndi name suitable for use on the default invoker
    */
   protected String getDefaultInvokerJndiName()
   {
      return null;
   }

   /**
    * Get the default invokers
    * 
    * @return the default invokers
    */
   protected InvokerBindingsMetaData getDefaultInvokers()
   {
      InvokerBindingsMetaData bindings = new InvokerBindingsMetaData();
      InvokerBindingMetaData binding = new InvokerBindingMetaData();
      binding.setInvokerProxyBindingName(getDefaultInvokerName());
      String jndiName = getDefaultInvokerJndiName();
      if (jndiName != null)
         binding.setJndiName(getDefaultInvokerJndiName());
      bindings.add(binding);
      return bindings;
   }

   /**
    * Get the default invokers
    * 
    * @return the default invokers
    */
   protected abstract String getDefaultInvokerName();

   /**
    * Get the jndiEnvironmentRefsGroup.
    * 
    * @return the jndiEnvironmentRefsGroup.
    */
   public JBossEnvironmentRefsGroupMetaData getJbossJndiEnvironmentRefsGroup()
   {
      return jndiEnvironmentRefsGroup;
   }

   /**
    * Set the jndiEnvironmentRefsGroup.
    * 
    * @param jndiEnvironmentRefsGroup the jndiEnvironmentRefsGroup.
    * @throws IllegalArgumentException for a null jndiEnvironmentRefsGroup
    */
   public void setJbossJndiEnvironmentRefsGroup(JBossEnvironmentRefsGroupMetaData jndiEnvironmentRefsGroup)
   {
      if (jndiEnvironmentRefsGroup == null)
         throw new IllegalArgumentException("Null jndiEnvironmentRefsGroup");
      this.jndiEnvironmentRefsGroup = jndiEnvironmentRefsGroup;
   }

   /**
    * Get the mergedEnvironment.
    * 
    * @return the mergedEnvironment.
    */
   @XmlTransient
   public JBossEnvironmentRefsGroupMetaData getMergedEnvironment()
   {
      if (mergedEnvironment != null)
         return mergedEnvironment;
      
      try
      {
         mergedEnvironment = JBossEnvironmentRefsGroupMetaData.merge(jndiEnvironmentRefsGroup, getOverridenMetaDataWithCheck().getJndiEnvironmentRefsGroup(), "ejb-jar.xml", "jboss.xml");

         // Fixup the invoker binding references on ejb refs
         InvokerBindingsMetaData invokerBindings = getInvokerBindings();
         if (invokerBindings != null && invokerBindings.isEmpty() == false)
         {
            for (InvokerBindingMetaData invokerBinding : invokerBindings)
            {
               String ejbRefName = invokerBinding.getEjbRefName();
               if (ejbRefName != null)
               {
                  EJBReferenceMetaData ejbRef = mergedEnvironment.getEjbReferenceByName(ejbRefName);
                  if (ejbRef == null)
                     throw new IllegalStateException("ejb-ref " + ejbRefName + " found on invoker " + invokerBinding.getName() + " but it does not exist for ejb: " + getName());
                  ejbRef.addInvokerBinding(invokerBinding.getName(), invokerBinding.getJndiName());
               }
            }
         }
         
         // Fixup the security identity
         SecurityIdentityMetaData jbossSecurityIdentity = null;
         if (jndiEnvironmentRefsGroup != null)
            jbossSecurityIdentity = jndiEnvironmentRefsGroup.getSecurityIdentity();
         SecurityIdentityMetaData originalSecurityIdentity = getOverridenMetaDataWithCheck().getSecurityIdentity();
         SecurityIdentityMetaData mergedSecurityIdentity = jbossSecurityIdentity;
         if (jbossSecurityIdentity == null)
            mergedSecurityIdentity = originalSecurityIdentity;
         else if  (originalSecurityIdentity != null)
            mergedSecurityIdentity = jbossSecurityIdentity.merge(originalSecurityIdentity);
         if (mergedSecurityIdentity != null)
            mergedEnvironment.setSecurityIdentity(mergedSecurityIdentity);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error merging jndi environment for " + getEjbName(), e);
      }
      return mergedEnvironment;
   }

   public EJBLocalReferenceMetaData getEjbLocalReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getEjbLocalReferences());
   }

   public EJBLocalReferencesMetaData getEjbLocalReferences()
   {
      return getMergedEnvironment().getEjbLocalReferences();
   }

   public EJBReferenceMetaData getEjbReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getEjbReferences());
   }

   public EJBReferencesMetaData getEjbReferences()
   {
      return getMergedEnvironment().getEjbReferences();
   }

   public EnvironmentEntriesMetaData getEnvironmentEntries()
   {
      return getMergedEnvironment().getEnvironmentEntries();
   }

   public EnvironmentEntryMetaData getEnvironmentEntryByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getEnvironmentEntries());
   }

   public MessageDestinationReferenceMetaData getMessageDestinationReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getMessageDestinationReferences());
   }

   public MessageDestinationReferencesMetaData getMessageDestinationReferences()
   {
      return getMergedEnvironment().getMessageDestinationReferences();
   }

   public PersistenceContextReferenceMetaData getPersistenceContextReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getPersistenceContextRefs());
   }

   public PersistenceContextReferencesMetaData getPersistenceContextRefs()
   {
      return getMergedEnvironment().getPersistenceContextRefs();
   }

   public PersistenceUnitReferenceMetaData getPersistenceUnitReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getPersistenceUnitRefs());
   }

   public PersistenceUnitReferencesMetaData getPersistenceUnitRefs()
   {
      return getMergedEnvironment().getPersistenceUnitRefs();
   }

   public LifecycleCallbacksMetaData getPostConstructs()
   {
      return getMergedEnvironment().getPostConstructs();
   }

   public LifecycleCallbacksMetaData getPreDestroys()
   {
      return getMergedEnvironment().getPreDestroys();
   }

   public ResourceEnvironmentReferenceMetaData getResourceEnvironmentReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getResourceEnvironmentReferences());
   }

   public ResourceEnvironmentReferencesMetaData getResourceEnvironmentReferences()
   {
      return getMergedEnvironment().getResourceEnvironmentReferences();
   }

   public ResourceReferenceMetaData getResourceReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getResourceReferences());
   }

   public ResourceReferencesMetaData getResourceReferences()
   {
      return getMergedEnvironment().getResourceReferences();
   }

   public ServiceReferenceMetaData getServiceReferenceByName(String name)
   {
      return AbstractMappedMetaData.getByName(name, getServiceReferences());
   }

   public ServiceReferencesMetaData getServiceReferences()
   {
      return getMergedEnvironment().getServiceReferences();
   }

   /**
    * Get the security identity
    * 
    * @return the security identity
    */
   public SecurityIdentityMetaData getSecurityIdentity()
   {
      // This is hacky because of the stupid way security identity is included in the environment
      if (getOverridenMetaData() == null)
      {
         if (jndiEnvironmentRefsGroup == null)
            return null;
         else
            return jndiEnvironmentRefsGroup.getSecurityIdentity();
      }
      return getMergedEnvironment().getSecurityIdentity();
   }
   
   /**
    * Get the annotations.
    * 
    * @return the annotations.
    */
   public AnnotationsMetaData getAnnotations()
   {
      return annotations;
   }

   /**
    * Set the annotations.
    * 
    * @param annotations the annotations.
    * @throws IllegalArgumentException for a null annotations
    */
   @XmlElement(name="annotation")
   public void setAnnotations(AnnotationsMetaData annotations)
   {
      if (annotations == null)
         throw new IllegalArgumentException("Null annotations");
      this.annotations = annotations;
   }

   /**
    * Get the aopDomainName.
    * 
    * @return the aopDomainName.
    */
   public String getAopDomainName()
   {
      return aopDomainName;
   }

   /**
    * Set the aopDomainName.
    * 
    * @param aopDomainName the aopDomainName.
    * @throws IllegalArgumentException for a null aopDomainName
    */
   public void setAopDomainName(String aopDomainName)
   {
      if (aopDomainName == null)
         throw new IllegalArgumentException("Null aopDomainName");
      this.aopDomainName = aopDomainName;
   }

   /**
    * Get the jndiRefs.
    * 
    * @return the jndiRefs.
    */
   public JndiRefsMetaData getJndiRefs()
   {
      return jndiRefs;
   }

   /**
    * Set the jndiRefs.
    * 
    * @param jndiRefs the jndiRefs.
    * @throws IllegalArgumentException for a null jndiRefs
    */
   @XmlElement(name="jndi-ref")
   public void setJndiRefs(JndiRefsMetaData jndiRefs)
   {
      if (jndiRefs == null)
         throw new IllegalArgumentException("Null jndiRefs");
      this.jndiRefs = jndiRefs;
   }

   /**
    * Get a security role
    * 
    * @param roleName the role name
    * @return the security role or null if not found
    */
   public SecurityRoleMetaData getSecurityRole(String roleName)
   {
      JBossAssemblyDescriptorMetaData assemblyDescriptor = getJBossMetaDataWithCheck().getAssemblyDescriptor();
      if (assemblyDescriptor == null)
         return null;
      else
         return assemblyDescriptor.getSecurityRole(roleName);
   }

   /**
    * Get a security role's principals
    * 
    * @param roleName the role name
    * @return the principals or null if not found
    */
   public Set<String> getSecurityRolePrincipals(String roleName)
   {
      JBossAssemblyDescriptorMetaData assemblyDescriptor = getJBossMetaDataWithCheck().getAssemblyDescriptor();
      if (assemblyDescriptor == null)
         return null;
      else
         return assemblyDescriptor.getSecurityRolePrincipals(roleName);
   }
   
   /**
    * A somewhat tedious method that builds a Set<Principal> of the roles
    * that have been assigned permission to execute the indicated method. The
    * work performed is tedious because of the wildcard style of declaring
    * method permission allowed in the ejb-jar.xml descriptor. This method is
    * called by the Container.getMethodPermissions() when it fails to find the
    * prebuilt set of method roles in its cache.
    *
    * @param methodName the method name
    * @param params the parameters
    * @param interfaceType the interface type
    * @return The Set<Principal> for the application domain roles that caller principal's are to be validated against.
    */
   public Set<String> getMethodPermissions(String methodName, Class[] params, MethodInterfaceType interfaceType)
   {
      Set<String> result = null;

      EnterpriseBeanMetaData ejb = getOverridenMetaDataWithCheck();
      JBossMetaData jbossMetaData = getJBossMetaDataWithCheck();
      
      // First check the excluded method list as this takes priority over
      // all other assignments
      ExcludeListMetaData excluded = ejb.getExcludeList();
      if (excluded != null && excluded.matches(methodName, params, interfaceType))
      {
         // No one is allowed to execute this method so add a role that
         // fails to equate to any Principal or Principal name and return.
         // We don't return null to differentiate between an explicit
         // assignment of no access and no assignment information.
         if (result == null)
            result = new HashSet<String>();
         result.add("NOBODY_PRINCIPAL");
         return result;
      }

      // Check the permissioned methods list
      MethodPermissionsMetaData permissions = ejb.getMethodPermissions();
      if (permissions != null)
      {
         for (MethodPermissionMetaData permission : permissions)
         {
            if (permission.isNotChecked(methodName, params, interfaceType))
            {
               if (result != null)
                  result.clear();
               result.add("ANYBODY_PRINCIPAL");
               break;
            }
            else if (permission.matches(methodName, params, interfaceType))
            {
               Set<String> roles = permission.getRoles();
               if (roles != null)
               {
                  for (String roleName : roles)
                  {
                     Set<String> principals = getSecurityRolePrincipals(roleName);
                     if (principals != null)
                     {
                        for (String principal : principals)
                        {
                           if (result == null)
                              result = new HashSet<String>();
                           result.add(principal);
                        }
                     }
                  }
               }
            }
         }
      }

      if (jbossMetaData.isExcludeMissingMethods() == false && result == null)
      {
            result = new HashSet<String>();
            result.add("ANYBODY_PRINCIPAL");
      }

      if (result == null)
         result = Collections.emptySet();
      return result;
   }
   
   /**
    * Check to see if there was a method-permission or exclude-list statement
    * for the given method.
    * 
    * @param methodName - the method name
    * @param params - the method parameter signature
    * @param interfaceType - the method interface type
    * @return true if a matching method permission exists, false if no match
    */
   public boolean hasMethodPermissions(String methodName, Class[] params, MethodInterfaceType interfaceType)
   {
      EnterpriseBeanMetaData ejb = getOverridenMetaDataWithCheck();
      
      // First check the excluded method list as this takes priority over
      // all other assignments
      ExcludeListMetaData excluded = ejb.getExcludeList();
      if (excluded != null && excluded.matches(methodName, params, interfaceType))
         return true;

      // Check the permissioned methods list
      MethodPermissionsMetaData permissions = ejb.getMethodPermissions();
      if (permissions != null)
      {
         for (MethodPermissionMetaData permission : permissions)
         {
            if (permission.matches(methodName, params, interfaceType))
               return true;
         }
      }

      // No match
      return false;
   }

   /**
    * Get the iorSecurityConfig.
    * 
    * @return the iorSecurityConfig.
    */
   public IORSecurityConfigMetaData getIorSecurityConfig()
   {
      return iorSecurityConfig;
   }

   /**
    * Set the iorSecurityConfig.
    * 
    * @param iorSecurityConfig the iorSecurityConfig.
    * @throws IllegalArgumentException for a null iorSecurityConfig
    */
   public void setIorSecurityConfig(IORSecurityConfigMetaData iorSecurityConfig)
   {
      if (iorSecurityConfig == null)
         throw new IllegalArgumentException("Null iorSecurityConfig");
      this.iorSecurityConfig = iorSecurityConfig;
   }

   /**
    * Get the ignoreDependency.
    * 
    * @return the ignoreDependency.
    */
   public IgnoreDependencyMetaData getIgnoreDependency()
   {
      return ignoreDependency;
   }

   /**
    * Set the ignoreDependency.
    * 
    * @param ignoreDependency the ignoreDependency.
    * @throws IllegalArgumentException for a null ignoreDependency
    */
   public void setIgnoreDependency(IgnoreDependencyMetaData ignoreDependency)
   {
      if (ignoreDependency == null)
         throw new IllegalArgumentException("Null ignoreDependency");
      this.ignoreDependency = ignoreDependency;
   }

   /**
    * Get the methodAttributes.
    * 
    * @return the methodAttributes.
    */
   public MethodAttributesMetaData getMethodAttributes()
   {
      return methodAttributes;
   }

   /**
    * Set the methodAttributes.
    * 
    * @param methodAttributes the methodAttributes.
    * @throws IllegalArgumentException for a null methodAttributes
    */
   public void setMethodAttributes(MethodAttributesMetaData methodAttributes)
   {
      if (methodAttributes == null)
         throw new IllegalArgumentException("Null methodAttributes");
      this.methodAttributes = methodAttributes;
   }

   /**
    * Is this method a read-only method
    * 
    * @param methodName the method name
    * @return true for read only
    */
   public boolean isMethodReadOnly(String methodName)
   {
      if (methodAttributes == null)
         return false;
      return methodAttributes.isMethodReadOnly(methodName);
   }

   /**
    * Is this method a read-only method
    * 
    * @param method the method
    * @return true for read only
    */
   public boolean isMethodReadOnly(Method method)
   {
      if (method == null)
         return false;
      return isMethodReadOnly(method.getName());
   }

   /**
    * Get the transaction timeout for the method
    * 
    * @param methodName the method name
    * @return the transaction timeout
    */
   public int getMethodTransactionTimeout(String methodName)
   {
      if (methodAttributes == null)
         return 0;
      return methodAttributes.getMethodTransactionTimeout(methodName);
   }

   /**
    * Get the transaction timeout for the method
    * 
    * @param method the method
    * @return the transaction timeout
    */
   public int getMethodTransactionTimeout(Method method)
   {
      if (method == null)
         return 0;
      return getMethodTransactionTimeout(method.getName());
   }
}
