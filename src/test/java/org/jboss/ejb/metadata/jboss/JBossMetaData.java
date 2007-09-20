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

import org.jboss.ejb.metadata.spec.EjbJarMetaData;
import org.jboss.javaee.metadata.support.IdMetaDataImplWithDescriptionGroupWithOverride;
import org.jboss.logging.Logger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * JBossMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class JBossMetaData extends IdMetaDataImplWithDescriptionGroupWithOverride<EjbJarMetaData>
   // TODO LAST extends EjbJar30MetaData 
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 598759931857080298L;
   
   /** The log */
   private static final Logger log = Logger.getLogger(JBossMetaData.class);

   /** The version */
   private String version;

   /** The loader repository */
   private LoaderRepositoryMetaData loaderRepository;

   /** The jmx name */
   private String jmxName;
   
   /** The security domain */
   private String securityDomain;

   /** Whether to exclude missing methods */
   private boolean excludeMissingMethods = true;
   
   /** The unauthenticated principal */
   private String unauthenticatedPrincipal;
   
   /** Whether to throw an exception when marked rollback */
   private boolean exceptionOnRollback;
   
   /** The webservices */
   private WebservicesMetaData webservices;
   
   /** The enterprise beans */
   private JBossEnterpriseBeansMetaData enterpriseBeans;
   
   /** The merged enterprise beans */
   private JBossEnterpriseBeansMetaData mergedEnterpriseBeans;
   
   /** The assembly descriptor */
   private JBossAssemblyDescriptorMetaData assemblyDescriptor;
   
   /** The merged assembly descriptor */
   private JBossAssemblyDescriptorMetaData mergedAssemblyDescriptor;
   
   /** The resource manager */
   private ResourceManagersMetaData resourceManagers;
   
   /** The invoker-proxy-bindings */
   private InvokerProxyBindingsMetaData invokerProxyBindings;

   /** The container configurations */
   private ContainerConfigurationsMetaData containerConfigurations;
   
   /**
    * Create a new JBossMetaData.
    */
   public JBossMetaData()
   {
      // For serialization
   }

   /**
    * Get the version.
    * 
    * @return the version.
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Set the version.
    * 
    * @param version the version.
    * @throws IllegalArgumentException for a null version
    */
   @XmlAttribute
   public void setVersion(String version)
   {
      if (version == null)
         throw new IllegalArgumentException("Null version");
      this.version = version;
   }

   /**
    * Get the loaderRepository.
    * 
    * @return the loaderRepository.
    */
   public LoaderRepositoryMetaData getLoaderRepository()
   {
      return loaderRepository;
   }

   /**
    * Set the loaderRepository.
    * 
    * @param loaderRepository the loaderRepository.
    * @throws IllegalArgumentException for a null loaderRepository
    */
   public void setLoaderRepository(LoaderRepositoryMetaData loaderRepository)
   {
      if (loaderRepository == null)
         throw new IllegalArgumentException("Null loaderRepository");
      this.loaderRepository = loaderRepository;
   }

   /**
    * Get the jmxName.
    * 
    * @return the jmxName.
    */
   public String getJmxName()
   {
      return jmxName;
   }

   /**
    * Set the jmxName.
    * 
    * @param jmxName the jmxName.
    * @throws IllegalArgumentException for a null jmxName
    */
   public void setJmxName(String jmxName)
   {
      if (jmxName == null)
         throw new IllegalArgumentException("Null jmxName");
      this.jmxName = jmxName;
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
   public void setSecurityDomain(String securityDomain)
   {
      if (securityDomain == null)
         throw new IllegalArgumentException("Null securityDomain");
      this.securityDomain = securityDomain;
   }

   /**
    * Get the excludeMissingMethods.
    * 
    * @return the excludeMissingMethods.
    */
   public boolean isExcludeMissingMethods()
   {
      return excludeMissingMethods;
   }

   /**
    * Set the excludeMissingMethods.
    * 
    * @param excludeMissingMethods the excludeMissingMethods.
    */
   @XmlElement(name="missing-method-permissions-excluded-mode")
   public void setExcludeMissingMethods(boolean excludeMissingMethods)
   {
      this.excludeMissingMethods = excludeMissingMethods;
   }

   /**
    * Get the unauthenticatedPrincipal.
    * 
    * @return the unauthenticatedPrincipal.
    */
   public String getUnauthenticatedPrincipal()
   {
      return unauthenticatedPrincipal;
   }

   /**
    * Set the unauthenticatedPrincipal.
    * 
    * @param unauthenticatedPrincipal the unauthenticatedPrincipal.
    * @throws IllegalArgumentException for a null unauthenticatedPrincipal
    */
   public void setUnauthenticatedPrincipal(String unauthenticatedPrincipal)
   {
      if (unauthenticatedPrincipal == null)
         throw new IllegalArgumentException("Null unauthenticatedPrincipal");
      this.unauthenticatedPrincipal = unauthenticatedPrincipal;
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
    * Get the merged enterpriseBeans.
    * 
    * @return the merged enterpriseBeans.
    */
   @XmlTransient
   public JBossEnterpriseBeansMetaData getMergedEnterpriseBeans()
   {
      if (mergedEnterpriseBeans != null)
         return mergedEnterpriseBeans;
      mergedEnterpriseBeans = JBossEnterpriseBeansMetaData.merge(enterpriseBeans, getOverridenMetaDataWithCheck().getEnterpriseBeans());
      mergedEnterpriseBeans.setJBossMetaData(this);
      return mergedEnterpriseBeans;
   }

   /**
    * Get a merged enterprise bean
    * 
    * @param name the name
    * @return the container configuration
    */
   @XmlTransient
   public JBossEnterpriseBeanMetaData getMergedEnterpriseBean(String name)
   {
      return getMergedEnterpriseBeans().get(name);
   }

   /**
    * Get the enterpriseBeans.
    * 
    * @return the enterpriseBeans.
    */
   public JBossEnterpriseBeansMetaData getEnterpriseBeans()
   {
      return enterpriseBeans;
   }

   /**
    * Set the enterpriseBeans.
    * 
    * @param enterpriseBeans the enterpriseBeans.
    * @throws IllegalArgumentException for a null enterpriseBeans
    */
   public void setEnterpriseBeans(JBossEnterpriseBeansMetaData enterpriseBeans)
   {
      if (enterpriseBeans == null)
         throw new IllegalArgumentException("Null enterpriseBeans");
      this.enterpriseBeans = enterpriseBeans;
      enterpriseBeans.setJBossMetaData(this);
   }

   /**
    * Get an enterprise bean
    * 
    * @param name the name
    * @return the container configuration
    */
   public JBossEnterpriseBeanMetaData getEnterpriseBean(String name)
   {
      if (enterpriseBeans == null)
         return null;
      return enterpriseBeans.get(name);
   }

   /**
    * Set the enforceEjbRestrictions.
    * 
    * @param enforceEjbRestrictions the enforceEjbRestrictions.
    * @throws IllegalArgumentException for a null enforceEjbRestrictions
    */
   public void setEnforceEjbRestrictions(String enforceEjbRestrictions)
   {
      log.warn("<enforce-ejb-restrictions/> in jboss.xml is no longer used and will be removed in a future version.");
   }

   /**
    * Get the webservices.
    * 
    * @return the webservices.
    */
   public WebservicesMetaData getWebservices()
   {
      return webservices;
   }

   /**
    * Set the webservices.
    * 
    * @param webservices the webservices.
    * @throws IllegalArgumentException for a null webservices
    */
   public void setWebservices(WebservicesMetaData webservices)
   {
      if (webservices == null)
         throw new IllegalArgumentException("Null webservices");
      this.webservices = webservices;
   }

   /**
    * Get the containerConfigurations.
    * 
    * @return the containerConfigurations.
    */
   public ContainerConfigurationsMetaData getContainerConfigurations()
   {
      return containerConfigurations;
   }

   /**
    * Set the containerConfigurations.
    * 
    * @param containerConfigurations the containerConfigurations.
    * @throws IllegalArgumentException for a null containerConfigurations
    */
   public void setContainerConfigurations(ContainerConfigurationsMetaData containerConfigurations)
   {
      if (containerConfigurations == null)
         throw new IllegalArgumentException("Null containerConfigurations");
      this.containerConfigurations = containerConfigurations;
   }

   /**
    * Get a container configuration
    * 
    * @param name the name
    * @return the container configuration
    */
   public ContainerConfigurationMetaData getContainerConfiguration(String name)
   {
      if (containerConfigurations == null)
         return null;
      return containerConfigurations.get(name);
   }

   /**
    * Get the invokerProxyBindings.
    * 
    * @return the invokerProxyBindings.
    */
   public InvokerProxyBindingsMetaData getInvokerProxyBindings()
   {
      return invokerProxyBindings;
   }

   /**
    * Set the invokerProxyBindings.
    * 
    * @param invokerProxyBindings the invokerProxyBindings.
    * @throws IllegalArgumentException for a null invokerProxyBindings
    */
   public void setInvokerProxyBindings(InvokerProxyBindingsMetaData invokerProxyBindings)
   {
      if (invokerProxyBindings == null)
         throw new IllegalArgumentException("Null invokerProxyBindings");
      this.invokerProxyBindings = invokerProxyBindings;
   }

   /**
    * Get an invoker proxy binding
    * 
    * @param name the name
    * @return the invoker proxy binding
    */
   public InvokerProxyBindingMetaData getInvokerProxyBinding(String name)
   {
      if (invokerProxyBindings == null)
         return null;
      return invokerProxyBindings.get(name);
   }

   /**
    * Get the resourceManagers.
    * 
    * @return the resourceManagers.
    */
   public ResourceManagersMetaData getResourceManagers()
   {
      return resourceManagers;
   }

   /**
    * Set the resourceManagers.
    * 
    * @param resourceManagers the resourceManagers.
    * @throws IllegalArgumentException for a null resourceManagers
    */
   public void setResourceManagers(ResourceManagersMetaData resourceManagers)
   {
      if (resourceManagers == null)
         throw new IllegalArgumentException("Null resourceManagers");
      this.resourceManagers = resourceManagers;
   }

   /**
    * Get a resource manager
    * 
    * @param name the name
    * @return the resource manager
    */
   public ResourceManagerMetaData getResourceManager(String name)
   {
      if (resourceManagers == null)
         return null;
      return resourceManagers.get(name);
   }

   /**
    * Get the merged assembly descriptor
    * 
    * @return the merged assembly descriptor.
    */
   @XmlTransient
   public JBossAssemblyDescriptorMetaData getMergedAssemblyDescriptor()
   {
      if (mergedAssemblyDescriptor != null)
         return mergedAssemblyDescriptor;
      mergedAssemblyDescriptor = JBossAssemblyDescriptorMetaData.merge(assemblyDescriptor, getOverridenMetaDataWithCheck().getAssemblyDescriptor());
      return mergedAssemblyDescriptor;
   }

   /**
    * Get the assemblyDescriptor.
    * 
    * @return the assemblyDescriptor.
    */
   public JBossAssemblyDescriptorMetaData getAssemblyDescriptor()
   {
      return assemblyDescriptor;
   }

   /**
    * Set the assemblyDescriptor.
    * 
    * @param assemblyDescriptor the assemblyDescriptor.
    * @throws IllegalArgumentException for a null assemblyDescriptor
    */
   public void setAssemblyDescriptor(JBossAssemblyDescriptorMetaData assemblyDescriptor)
   {
      if (assemblyDescriptor == null)
         throw new IllegalArgumentException("Null assemblyDescriptor");
      this.assemblyDescriptor = assemblyDescriptor;
   }

   // TODO
   public void mergeMetaDataDefaults(JBossMetaData defaults)
   {
      
   }
}
