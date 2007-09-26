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
package org.jboss.test.ejb.metadata.test;

import java.util.Collection;
import java.util.Set;

import junit.framework.Test;

import org.jboss.ejb.metadata.jboss.CommitOption;
import org.jboss.ejb.metadata.jboss.ContainerConfigurationMetaData;
import org.jboss.ejb.metadata.jboss.ContainerConfigurationsMetaData;
import org.jboss.ejb.metadata.jboss.InvokerBindingMetaData;
import org.jboss.ejb.metadata.jboss.InvokerBindingsMetaData;
import org.jboss.ejb.metadata.jboss.InvokerProxyBindingMetaData;
import org.jboss.ejb.metadata.jboss.InvokerProxyBindingsMetaData;
import org.jboss.ejb.metadata.jboss.JBoss50MetaData;
import org.jboss.ejb.metadata.jboss.JBossAssemblyDescriptorMetaData;
import org.jboss.ejb.metadata.jboss.JBossEnterpriseBeanMetaData;
import org.jboss.ejb.metadata.jboss.JBossEnterpriseBeansMetaData;
import org.jboss.ejb.metadata.jboss.JBossEntityBeanMetaData;
import org.jboss.ejb.metadata.jboss.JBossMessageDrivenBeanMetaData;
import org.jboss.ejb.metadata.jboss.JBossMetaData;
import org.jboss.ejb.metadata.jboss.JBossSessionBeanMetaData;
import org.jboss.ejb.metadata.jboss.LoaderRepositoryConfigMetaData;
import org.jboss.ejb.metadata.jboss.LoaderRepositoryMetaData;
import org.jboss.ejb.metadata.jboss.ResourceManagerMetaData;
import org.jboss.ejb.metadata.jboss.ResourceManagersMetaData;
import org.jboss.ejb.metadata.jboss.WebserviceDescriptionMetaData;
import org.jboss.ejb.metadata.jboss.WebserviceDescriptionsMetaData;
import org.jboss.ejb.metadata.jboss.WebservicesMetaData;
import org.jboss.ejb.metadata.spec.AssemblyDescriptorMetaData;
import org.jboss.ejb.metadata.spec.EjbJar30MetaData;
import org.jboss.ejb.metadata.spec.EjbJarMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeansMetaData;
import org.jboss.ejb.metadata.spec.EntityBeanMetaData;
import org.jboss.ejb.metadata.spec.MessageDrivenBeanMetaData;
import org.jboss.ejb.metadata.spec.SecurityIdentityMetaData;
import org.jboss.ejb.metadata.spec.SessionBeanMetaData;
import org.jboss.javaee.metadata.spec.EJBLocalReferencesMetaData;
import org.jboss.javaee.metadata.spec.EJBReferencesMetaData;
import org.jboss.javaee.metadata.spec.EnvironmentRefsGroupMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationReferencesMetaData;
import org.jboss.javaee.metadata.spec.MessageDestinationsMetaData;
import org.jboss.javaee.metadata.spec.ResourceEnvironmentReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceInjectionMetaData;
import org.jboss.javaee.metadata.spec.ResourceReferencesMetaData;
import org.jboss.javaee.metadata.spec.SecurityRoleMetaData;
//import org.jboss.metadata.ApplicationMetaData;
//import org.jboss.metadata.BeanMetaData;
//import org.jboss.metadata.ConfigurationMetaData;
//import org.jboss.metadata.EntityMetaData;
//import org.jboss.metadata.MessageDrivenMetaData;
//import org.jboss.metadata.SessionMetaData;
import org.jboss.test.ejb.AbstractEJBEverythingTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * JBoss5xEverythingUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class JBoss5xEverythingUnitTestCase extends AbstractEJBEverythingTest
{
   public static Test suite()
   {
      return suite(JBoss5xEverythingUnitTestCase.class);
   }
   
   public static SchemaBindingResolver initResolver()
   {
      return schemaResolverForClass(JBoss50MetaData.class);
      //return AbstractJavaEEMetaDataTest.initResolverJavaEE(JBoss50MetaData.class);
   }
   
   public JBoss5xEverythingUnitTestCase(String name)
   {
      super(name);
   }
   
   protected JBoss50MetaData unmarshal() throws Exception
   {
      return unmarshal(JBoss50MetaData.class);
   }
   
   public void testEverything() throws Exception
   {
      //enableTrace("org.jboss.xb");
      //enableTrace("org.jboss.xb.builder");
      JBoss50MetaData jbossMetaData = unmarshal();      
      assertEverything(jbossMetaData);
   }

   public void assertEverything(JBoss50MetaData jbossMetaData)
   {
      assertVersion(jbossMetaData);
      assertId("jboss", jbossMetaData);
      assertDescriptionGroup("jboss", jbossMetaData.getDescriptionGroup());
      assertLoaderRepository(jbossMetaData.getLoaderRepository());
      assertEquals("jboss-jmx-name", jbossMetaData.getJmxName());
      assertEquals("jboss-security-domain", jbossMetaData.getSecurityDomain());
      assertFalse(jbossMetaData.isExcludeMissingMethods());
      assertEquals("jboss-unauthenticated-principal", jbossMetaData.getUnauthenticatedPrincipal());
      assertTrue(jbossMetaData.isExceptionOnRollback());
      assertWebservices(jbossMetaData.getWebservices());
      assertJBossEnterpriseBeans(jbossMetaData);
      assertAssemblyDescriptor(jbossMetaData);
      assertResourceManagers(jbossMetaData.getResourceManagers());
      assertResourceManager("resourceManager1", true, jbossMetaData.getResourceManager("resourceManager1Name"));
      assertResourceManager("resourceManager2", false, jbossMetaData.getResourceManager("resourceManager2Name"));
      assertInvokerProxyBindings(jbossMetaData.getInvokerProxyBindings());
      assertInvokerProxyBinding("invokerProxyBinding1", 1, jbossMetaData.getInvokerProxyBinding("invokerProxyBinding1Name"));
      InvokerProxyBindingMetaData ipbmd2 = jbossMetaData.getInvokerProxyBinding("invokerProxyBinding2Name");
      assertInvokerProxyBinding("invokerProxyBinding2", 2, ipbmd2);
      fixUpContainerConfigurations(jbossMetaData);
      assertContainerConfigurations(jbossMetaData);
      
      fixUpEjbJar(jbossMetaData);
      //ApplicationMetaData application = new ApplicationMetaData(jbossMetaData);
      //assertEquals("jboss-jmx-name", application.getJmxName());
      //assertEquals("jboss-security-domain", application.getSecurityDomain());
      //assertFalse(application.isExcludeMissingMethods());
      //assertEquals("jboss-unauthenticated-principal", application.getUnauthenticatedPrincipal());
      //assertTrue(application.getExceptionRollback());
      //assertWebservices(application);
      fixUpEnterpriseBeans(jbossMetaData);
      //assertJBossEnterpriseBeans(application);
      fixUpAssemblyDescriptor(jbossMetaData);
      //assertAssemblyDescriptor(application);
      //assertEquals("resourceManager1JndiName", application.getResourceByName("resourceManager1Name"));
      //assertEquals("resourceManager2URL", application.getResourceByName("resourceManager2Name"));
      //assertInvokerProxyBinding("invokerProxyBinding1", application.getInvokerProxyBindingMetaDataByName("invokerProxyBinding1Name"));
      //assertInvokerProxyBinding("invokerProxyBinding2", application.getInvokerProxyBindingMetaDataByName("invokerProxyBinding2Name"));
      //assertContainerConfigurations(application);
   }
   
   private void fixUpEjbJar(JBossMetaData jbossMetaData)
   {
      EjbJarMetaData ejbJarMetaData = jbossMetaData.getOverridenMetaData();
      if (ejbJarMetaData == null)
      {
         ejbJarMetaData = new EjbJar30MetaData();
         jbossMetaData.setOverridenMetaData(ejbJarMetaData);
      }
   }
   
   private void assertVersion(JBoss50MetaData jbossMetaData)
   {
      assertEquals("5.0", jbossMetaData.getVersion());
   }
   
   private void assertLoaderRepository(LoaderRepositoryMetaData loaderRepositoryMetaData)
   {
      assertNotNull(loaderRepositoryMetaData);
      assertId("loaderRepository", loaderRepositoryMetaData);
      assertEquals("loaderRepositoryClass", loaderRepositoryMetaData.getLoaderRepositoryClass());
      assertEquals("loaderRepositoryName", trim(loaderRepositoryMetaData.getName()));
      assertLoaderRepositoryConfig(2, loaderRepositoryMetaData);
   }
   
   private void assertLoaderRepositoryConfig(int size, LoaderRepositoryMetaData loaderRepositoryMetaData)
   {
      Set<LoaderRepositoryConfigMetaData> configs = loaderRepositoryMetaData.getLoaderRepositoryConfig();
      assertNotNull(configs);
      assertEquals(size, configs.size());
      for (int count = 1; count < configs.size(); ++count)
      {
         LoaderRepositoryConfigMetaData config = new LoaderRepositoryConfigMetaData();
         config.setId("loaderRepositoryConfig" + count + "-id");
         config.setConfig("loaderRepositoryConfig" + count);
         config.setConfigParserClass("loaderRepositoryConfigParserClass" + count);
         assertTrue(configs + " contains " + config, configs.contains(config));
//         assertId("loaderRepositoryConfig" + count, config);
//         assertEquals("loaderRepositoryConfigParserClass" + count, config.getConfigParserClass());
//         assertEquals("loaderRepositoryConfig" + count, trim(config.getConfig()));
      }
   }
   
   private void assertWebservices(WebservicesMetaData webservices)
   {
      assertNotNull(webservices);
      assertId("webservices", webservices);
      assertEquals("webservicesContextRoot", webservices.getContextRoot());
      WebserviceDescriptionsMetaData webserviceDescriptionsMetaData = webservices.getWebserviceDescriptions();
      assertNotNull(webserviceDescriptionsMetaData);
      assertEquals(2, webserviceDescriptionsMetaData.size());
      int count = 1;
      for (WebserviceDescriptionMetaData description : webserviceDescriptionsMetaData)
      {
         assertId("webserviceDescription" + count, description);
         assertEquals("webserviceDescription" + count + "ConfigName", description.getConfigName());
         assertEquals("webserviceDescription" + count + "ConfigFile", description.getConfigFile());
         assertEquals("webserviceDescription" + count + "WsdlPublishLocation", description.getWsdlPublishLocation());
         ++count;
      }
   }
   
/*   private void assertWebservices(ApplicationMetaData application)
   {
      assertEquals("webservicesContextRoot", application.getWebServiceContextRoot());
      assertEquals("webserviceDescription2ConfigName", application.getConfigName());
      assertEquals("webserviceDescription2ConfigFile", application.getConfigFile());
      Map<String, String> result = application.getWsdlPublishLocations();
      Map<String, String> expected = new HashMap<String, String>();
      expected.put("webserviceDescription1Name", "webserviceDescription1WsdlPublishLocation");
      expected.put("webserviceDescription2Name", "webserviceDescription2WsdlPublishLocation");
      assertEquals(expected, result);
      assertEquals("webserviceDescription1WsdlPublishLocation", application.getWsdlPublishLocationByName("webserviceDescription1Name"));
      assertEquals("webserviceDescription2WsdlPublishLocation", application.getWsdlPublishLocationByName("webserviceDescription2Name"));
   }
*/   
   private String trim(String string)
   {
      assertNotNull(string);
      return string.trim();
   }
   
   private void assertJBossEnterpriseBeans(JBossMetaData jbossMetaData)
   {
      JBossEnterpriseBeansMetaData enterpriseBeansMetaData = jbossMetaData.getEnterpriseBeans();
      assertNotNull(enterpriseBeansMetaData);
      assertEquals(9, enterpriseBeansMetaData.size());

      assertNullSessionBean("session0", jbossMetaData);
      assertFullSessionBean("session1", jbossMetaData, true);
      assertFullSessionBean("session2", jbossMetaData, false);

      assertNullEntityBean("entity0", jbossMetaData);
      assertFullEntityBean("entity1", jbossMetaData, true);
      assertFullEntityBean("entity2", jbossMetaData, false);

      assertNullMessageDrivenBean("mdb0", jbossMetaData);
      assertFullMessageDrivenBean("mdb1", jbossMetaData, true);
      assertFullMessageDrivenBean("mdb2", jbossMetaData, false);
   }
   
   private void fixUpEnterpriseBeans(JBossMetaData jbossMetaData)
   {
      EjbJarMetaData ejbJarMetaData = jbossMetaData.getOverridenMetaData();
      assertNotNull(ejbJarMetaData);

      EnterpriseBeansMetaData enterpriseBeansMetaData = ejbJarMetaData.getEnterpriseBeans();
      if (enterpriseBeansMetaData == null)
      {
         enterpriseBeansMetaData = new EnterpriseBeansMetaData();
         ejbJarMetaData.setEnterpriseBeans(enterpriseBeansMetaData);
      }

      JBossEnterpriseBeansMetaData jbossEnterpriseBeansMetaData = jbossMetaData.getEnterpriseBeans();
      for (JBossEnterpriseBeanMetaData jbossEnterpriseBeanMetaData : jbossEnterpriseBeansMetaData)
      {
         String name = jbossEnterpriseBeanMetaData.getEjbName();
         if (enterpriseBeansMetaData.get(name) == null)
         {
            EnterpriseBeanMetaData enterpriseBeanMetaData = null;
            if (jbossEnterpriseBeanMetaData instanceof JBossSessionBeanMetaData)
               enterpriseBeanMetaData = new SessionBeanMetaData();
            else if (jbossEnterpriseBeanMetaData instanceof JBossEntityBeanMetaData)
               enterpriseBeanMetaData = new EntityBeanMetaData();
            else if (jbossEnterpriseBeanMetaData instanceof JBossMessageDrivenBeanMetaData)
               enterpriseBeanMetaData = new MessageDrivenBeanMetaData();
            enterpriseBeanMetaData.setEjbName(name);
            jbossEnterpriseBeanMetaData.setOverridenMetaData(enterpriseBeanMetaData);
            enterpriseBeansMetaData.add(enterpriseBeanMetaData);
         }
      }
      
      for (JBossEnterpriseBeanMetaData enterpriseBean : jbossMetaData.getEnterpriseBeans())
         fixUpEnterpriseBean(enterpriseBean);
   }
   
/*   private void assertJBossEnterpriseBeans(ApplicationMetaData application)
   {
      Iterator<BeanMetaData> beans = application.getEnterpriseBeans();
      assertNotNull(beans);
      int count = 0;
      while (beans.hasNext())
      {
         beans.next();
         ++count;
      }
      assertEquals(9, count);
      
      assertNullSessionBean("session0", application);
      assertFullSessionBean("session1", application, true);
      assertFullSessionBean("session2", application, false);

      assertNullEntityBean("entity0", application);
      assertFullEntityBean("entity1", application, true);
      assertFullEntityBean("entity2", application, false);

      assertNullMessageDrivenBean("mdb0", application);
      assertFullMessageDrivenBean("mdb1", application, true);
      assertFullMessageDrivenBean("mdb2", application, false);
   }
*/   
   private void fixUpEnterpriseBean(JBossEnterpriseBeanMetaData jbossEjb)
   {
      EnterpriseBeanMetaData ejb = jbossEjb.getOverridenMetaData();
      fixUpEnvironment(ejb, jbossEjb);
   }
   
   private <T extends JBossEnterpriseBeanMetaData> T assertJBossEnterpriseBean(String prefix, JBossMetaData jBossMetaData, Class<T> expected)
   {
      JBossEnterpriseBeanMetaData ejb = jBossMetaData.getEnterpriseBean(prefix + "EjbName");
      assertNotNull(ejb);
      assertEquals(prefix + "EjbName", ejb.getEjbName());
      assertTrue(expected.isInstance(ejb));
      return expected.cast(ejb);
   }
   
/*   private <T extends BeanMetaData> T assertBeanMetaData(String prefix, ApplicationMetaData application, Class<T> expected)
   {
      BeanMetaData ejb = application.getBeanByEjbName(prefix + "EjbName");
      assertNotNull(ejb);
      assertEquals(prefix + "EjbName", ejb.getEjbName());
      assertTrue(expected.isInstance(ejb));
      return expected.cast(ejb);
   }
*/   
   private JBossSessionBeanMetaData assertJBossSessionBean(String prefix, JBossMetaData jbossMetaData)
   {
      JBossSessionBeanMetaData ejb = assertJBossEnterpriseBean(prefix, jbossMetaData, JBossSessionBeanMetaData.class);
      assertTrue(ejb.isSession());
      assertFalse(ejb.isEntity());
      assertFalse(ejb.isMessageDriven());
      return ejb;
   }
   
/*   private SessionMetaData assertJBossSessionBean(String prefix, ApplicationMetaData application)
   {
      SessionMetaData ejb = assertBeanMetaData(prefix, application, SessionMetaData.class);
      assertTrue(ejb.isSession());
      assertFalse(ejb.isEntity());
      assertFalse(ejb.isMessageDriven());
      return ejb;
   }
*/   
   private void assertFullSessionBean(String prefix, JBossMetaData jbossMetaData, boolean first)
   {
      JBossSessionBeanMetaData session = assertJBossSessionBean(prefix, jbossMetaData);
      assertId(prefix, session);
      assertDescriptionGroup(prefix, session.getDescriptionGroup());

      assertRemoteBinding(prefix, session.getRemoteBinding());
      
      assertEquals(prefix + "JndiName", session.getJndiName());
      assertEquals(prefix + "LocalJndiName", session.getLocalJndiName());
      assertEquals(prefix + "ConfigurationName", session.getConfigurationName());
      assertEquals(prefix + "SecurityProxy", session.getSecurityProxy());
      assertEquals(prefix + "SecurityDomain", session.getSecurityDomain());

      if (first)
      {
         assertTrue(session.isCallByValue());
         assertTrue(session.isExceptionOnRollback());
         assertTrue(session.isTimerPersistence());
         assertTrue(session.isClustered());
      }
      else
      {
         assertFalse(session.isCallByValue());
         assertFalse(session.isExceptionOnRollback());
         assertFalse(session.isTimerPersistence());
         assertFalse(session.isClustered());
      }
      
      assertInvokerBindings(prefix, session.getInvokerBindings());

      assertEnvironment(prefix, session.getJbossJndiEnvironmentRefsGroup(), false);

      assertSecurityIdentity(prefix, "SecurityIdentity", session.getSecurityIdentity(), false);

      assertClusterConfig(prefix, session.determineClusterConfig(), true);
      
      assertMethodAttributes(prefix, session.getMethodAttributes());

      assertDepends(prefix, 2, session.getDepends());
      
      assertIORSecurityConfig(prefix, session.getIorSecurityConfig());
      
      // TODO webservice port-component
      
      assertSecurityIdentity(prefix, "EjbTimeoutIdentity", session.getEjbTimeoutIdentity(), false);
      
      assertAnnotations(prefix, 2, session.getAnnotations());

      assertIgnoreDependency(prefix, session.getIgnoreDependency());

      assertEquals(prefix + "AOPDomain", session.getAopDomainName());
      
      // TODO DOM cache-config
      
      // TODO DOM pool-config
      
      if (first)
         assertTrue(session.isConcurrent());
      else
         assertFalse(session.isConcurrent());
      
      assertJndiRefs(prefix, 2, session.getJndiRefs());
   }
   
   private void assertNullSessionBean(String prefix, JBossMetaData jbossMetaData)
   {
      JBossSessionBeanMetaData session = assertJBossSessionBean(prefix, jbossMetaData);
      assertNull(session.getId());
      assertNull(session.getDescriptionGroup());

      assertNull(session.getRemoteBinding());
      
      assertNull(session.getJndiName());
      assertNull(session.getLocalJndiName());
      assertNull(session.getConfigurationName());
      assertNull(session.getSecurityProxy());
      assertNull(session.getSecurityDomain());

      assertFalse(session.isCallByValue());
      assertFalse(session.isExceptionOnRollback());
      assertTrue(session.isTimerPersistence());
      assertFalse(session.isClustered());

      assertNull(session.getInvokerBindings());

      assertNullEnvironment(session.getJbossJndiEnvironmentRefsGroup());

      assertNull(session.getMethodAttributes());
      
      assertNull(session.getSecurityIdentity());
      assertNull(session.getEjbTimeoutIdentity());
      
      assertNull(session.getClusterConfig());
      
      assertNull(session.getDepends());

      assertNull(session.getIorSecurityConfig());

      assertNull(session.getAnnotations());
      assertNull(session.getIgnoreDependency());
      assertNull(session.getAopDomainName());
      assertFalse(session.isConcurrent());
      assertNull(session.getJndiRefs());
   }
   
/*   private void assertFullSessionBean(String prefix, ApplicationMetaData application, boolean first)
   {
      SessionMetaData session = assertJBossSessionBean(prefix, application);

      assertEquals(prefix + "JndiName", session.getJndiName());
      assertEquals(prefix + "LocalJndiName", session.getLocalJndiName());
      assertEquals(prefix + "ConfigurationName", session.getConfigurationName());
      assertEquals(prefix + "SecurityProxy", session.getSecurityProxy());

      if (first)
      {
         assertTrue(session.isCallByValue());
         assertTrue(session.getExceptionRollback());
         assertTrue(session.getTimerPersistence());
         assertTrue(session.isClustered());
      }
      else
      {
         assertFalse(session.isCallByValue());
         assertFalse(session.getExceptionRollback());
         assertFalse(session.getTimerPersistence());
         assertFalse(session.isClustered());
      }

      assertInvokerBindings(prefix, session, session.getInvokerBindings());

      assertEnvironment(prefix, session, false);

      assertSecurityIdentity(prefix, "SecurityIdentity", session.getSecurityIdentityMetaData(), false);
      
      assertClusterConfig(prefix, session.getClusterConfigMetaData(), true);
      
      assertMethodAttributes(prefix, session);

      assertDepends(prefix, 2, session.getDepends());
      
      assertIORSecurityConfig(prefix, session.getIorSecurityConfigMetaData());
      
      // TODO webservice port-component
      
      assertSecurityIdentity(prefix, "EjbTimeoutIdentity", session.getEjbTimeoutIdentity(), false);
   }
*/   
/*   private void assertNullSessionBean(String prefix, ApplicationMetaData application)
   {
      SessionMetaData session = assertJBossSessionBean(prefix, application);
      
      String ejbName = session.getEjbName();
      assertEquals(session.getEjbName(), session.getJndiName());
      String localName = "local/" + ejbName + '@' + System.identityHashCode(ejbName);
      assertEquals(localName, session.getLocalJndiName());
      assertEquals(ContainerConfigurationMetaData.STATELESS, session.getConfigurationName());
      assertNull(session.getSecurityProxy());

      assertFalse(session.isCallByValue());
      assertFalse(session.getExceptionRollback());
      assertTrue(session.getTimerPersistence());
      assertFalse(session.isClustered());
      
      assertDefaultInvoker(InvokerBindingMetaData.STATELESS, session);
      
      assertNullEnvironment(session);

      assertNull(session.getSecurityIdentityMetaData());
      
      assertDefaultMethodAttributes(ejbName, session);

      assertEmpty(session.getDepends());

      assertNull(session.getIorSecurityConfigMetaData());
      
      // TODO webservice port-component
      
      assertNull(session.getEjbTimeoutIdentity());
   }
*/   
   private JBossEntityBeanMetaData assertJBossEntityBean(String prefix, JBossMetaData jbossMetaData)
   {
      JBossEntityBeanMetaData ejb = assertJBossEnterpriseBean(prefix, jbossMetaData, JBossEntityBeanMetaData.class);
      assertFalse(ejb.isSession());
      assertTrue(ejb.isEntity());
      assertFalse(ejb.isMessageDriven());
      return ejb;
   }
   
/*   private EntityMetaData assertJBossEntityBean(String prefix, ApplicationMetaData application)
   {
      EntityMetaData ejb = assertBeanMetaData(prefix, application, EntityMetaData.class);
      assertFalse(ejb.isSession());
      assertTrue(ejb.isEntity());
      assertFalse(ejb.isMessageDriven());
      return ejb;
   }
*/
   private void assertFullEntityBean(String prefix, JBossMetaData jbossMetaData, boolean first)
   {
      JBossEntityBeanMetaData entity = assertJBossEntityBean(prefix, jbossMetaData);
      assertId(prefix, entity);
      assertDescriptionGroup(prefix, entity.getDescriptionGroup());
      
      assertEquals(prefix + "JndiName", entity.getJndiName());
      assertEquals(prefix + "LocalJndiName", entity.getLocalJndiName());
      assertEquals(prefix + "ConfigurationName", entity.getConfigurationName());
      assertEquals(prefix + "SecurityProxy", entity.getSecurityProxy());
      assertEquals(prefix + "SecurityDomain", entity.getSecurityDomain());

      if (first)
      {
         assertTrue(entity.isCallByValue());
         assertTrue(entity.isReadOnly());
         assertTrue(entity.isExceptionOnRollback());
         assertTrue(entity.isTimerPersistence());
         assertTrue(entity.isClustered());
         assertTrue(entity.isCacheInvalidation());
      }
      else
      {
         assertFalse(entity.isCallByValue());
         assertFalse(entity.isReadOnly());
         assertFalse(entity.isExceptionOnRollback());
         assertFalse(entity.isTimerPersistence());
         assertFalse(entity.isClustered());
         assertFalse(entity.isCacheInvalidation());
      }
      
      assertInvokerBindings(prefix, entity.getInvokerBindings());

      assertEnvironment(prefix, entity.getJbossJndiEnvironmentRefsGroup(), false);

      assertSecurityIdentity(prefix, "SecurityIdentity", entity.getSecurityIdentity(), false);

      assertClusterConfig(prefix, entity.getClusterConfig(), false);
      
      assertMethodAttributes(prefix, entity.getMethodAttributes());

      assertDepends(prefix, 2, entity.getDepends());
      
      assertIORSecurityConfig(prefix, entity.getIorSecurityConfig());
      
      assertAnnotations(prefix, 2, entity.getAnnotations());

      assertIgnoreDependency(prefix, entity.getIgnoreDependency());

      assertEquals(prefix + "AOPDomain", entity.getAopDomainName());

      assertCacheInvalidationConfig(prefix, entity.getCacheInvalidationConfig());

      // TODO DOM cache-config
      
      // TODO DOM pool-config
      
      assertJndiRefs(prefix, 2, entity.getJndiRefs());
   }
   
   private void assertNullEntityBean(String prefix, JBossMetaData jbossMetaData)
   {
      JBossEntityBeanMetaData entity = assertJBossEntityBean(prefix, jbossMetaData);
      assertNull(entity.getId());
      assertNull(entity.getDescriptionGroup());
      
      assertNull(entity.getJndiName());
      assertNull(entity.getLocalJndiName());
      assertNull(entity.getConfigurationName());
      assertNull(entity.getSecurityProxy());
      assertNull(entity.getSecurityDomain());

      assertFalse(entity.isCallByValue());
      assertFalse(entity.isReadOnly());
      assertFalse(entity.isExceptionOnRollback());
      assertTrue(entity.isTimerPersistence());
      assertFalse(entity.isClustered());
      assertFalse(entity.isCacheInvalidation());
      
      assertNull(entity.getInvokerBindings());

      assertNullEnvironment(entity.getJbossJndiEnvironmentRefsGroup());

      assertNull(entity.getMethodAttributes());
      
      assertNull(entity.getSecurityIdentity());
      
      assertNull(entity.getClusterConfig());

      assertNull(entity.getCacheInvalidationConfig());

      assertNull(entity.getDepends());

      assertNull(entity.getIorSecurityConfig());

      assertNull(entity.getAnnotations());
      assertNull(entity.getIgnoreDependency());
      assertNull(entity.getAopDomainName());
      assertNull(entity.getJndiRefs());
   }
   
/*   private void assertFullEntityBean(String prefix, ApplicationMetaData application, boolean first)
   {
      EntityMetaData entity = assertJBossEntityBean(prefix, application);

      assertEquals(prefix + "JndiName", entity.getJndiName());
      assertEquals(prefix + "LocalJndiName", entity.getLocalJndiName());
      assertEquals(prefix + "ConfigurationName", entity.getConfigurationName());
      assertEquals(prefix + "SecurityProxy", entity.getSecurityProxy());

      if (first)
      {
         assertTrue(entity.isCallByValue());
         assertTrue(entity.isReadOnly());
         assertTrue(entity.getExceptionRollback());
         assertTrue(entity.getTimerPersistence());
         assertTrue(entity.isClustered());
      }
      else
      {
         assertFalse(entity.isCallByValue());
         assertFalse(entity.isReadOnly());
         assertFalse(entity.getExceptionRollback());
         assertFalse(entity.getTimerPersistence());
         assertFalse(entity.isClustered());
      }

      assertInvokerBindings(prefix, entity, entity.getInvokerBindings());

      assertEnvironment(prefix, entity, false);

      assertSecurityIdentity(prefix, "SecurityIdentity", entity.getSecurityIdentityMetaData(), false);
      
      assertClusterConfig(prefix, entity.getClusterConfigMetaData(), false);
      
      assertMethodAttributes(prefix, entity);

      assertCacheInvalidationConfig(prefix, entity.getDistributedCacheInvalidationConfig());

      assertDepends(prefix, 2, entity.getDepends());
      
      assertIORSecurityConfig(prefix, entity.getIorSecurityConfigMetaData());
   }
*/   
/*   private void assertNullEntityBean(String prefix, ApplicationMetaData application)
   {
      EntityMetaData entity = assertJBossEntityBean(prefix, application);
      
      String ejbName = entity.getEjbName();
      assertEquals(entity.getEjbName(), entity.getJndiName());
      String localName = "local/" + ejbName + '@' + System.identityHashCode(ejbName);
      assertEquals(localName, entity.getLocalJndiName());
      assertEquals(ContainerConfigurationMetaData.CMP_2x, entity.getConfigurationName());
      assertNull(entity.getSecurityProxy());

      assertFalse(entity.isCallByValue());
      assertFalse(entity.isReadOnly());
      assertFalse(entity.getExceptionRollback());
      assertTrue(entity.getTimerPersistence());
      assertFalse(entity.isClustered());
      assertFalse(entity.doDistributedCacheInvalidations());
      
      assertDefaultInvoker(InvokerBindingMetaData.CMP_2x, entity);
      
      assertNullEnvironment(entity);

      assertNull(entity.getSecurityIdentityMetaData());

      assertNull(entity.getDistributedCacheInvalidationConfig());
     
      assertEmpty(entity.getDepends());

      assertNull(entity.getIorSecurityConfigMetaData());
   }
*/   
   private JBossMessageDrivenBeanMetaData assertJBossMessageDrivenBean(String prefix, JBossMetaData jbossMetaData)
   {
      JBossMessageDrivenBeanMetaData ejb = assertJBossEnterpriseBean(prefix, jbossMetaData, JBossMessageDrivenBeanMetaData.class);
      assertFalse(ejb.isSession());
      assertFalse(ejb.isEntity());
      assertTrue(ejb.isMessageDriven());
      return ejb;
   }
   
/*   private MessageDrivenMetaData assertJBossMessageDrivenBean(String prefix, ApplicationMetaData application)
   {
      MessageDrivenMetaData ejb = assertBeanMetaData(prefix, application, MessageDrivenMetaData.class);
      assertFalse(ejb.isSession());
      assertFalse(ejb.isEntity());
      assertTrue(ejb.isMessageDriven());
      return ejb;
   }
*/
   private void assertFullMessageDrivenBean(String prefix, JBossMetaData jbossMetaData, boolean first)
   {
      JBossMessageDrivenBeanMetaData mdb = assertJBossMessageDrivenBean(prefix, jbossMetaData);
      assertId(prefix, mdb);
      assertDescriptionGroup(prefix, mdb.getDescriptionGroup());
      
      assertEquals(prefix + "DestinationJndiName", mdb.getDestinationJndiName());
      assertEquals(prefix + "LocalJndiName", mdb.getLocalJndiName());
      assertEquals(prefix + "User", mdb.getMdbUser());
      assertEquals(prefix + "Password", mdb.getMdbPassword());
      assertEquals(prefix + "ClientId", mdb.getMdbClientId());
      assertEquals(prefix + "SubscriptionId", mdb.getMdbSubscriptionId());
      assertEquals(prefix + "RAR", mdb.getResourceAdapterName());
      assertEquals(prefix + "ConfigurationName", mdb.getConfigurationName());
      assertEquals(prefix + "SecurityProxy", mdb.getSecurityProxy());

      if (first)
      {
         assertTrue(mdb.isExceptionOnRollback());
         assertTrue(mdb.isTimerPersistence());
      }
      else
      {
         assertFalse(mdb.isExceptionOnRollback());
         assertFalse(mdb.isTimerPersistence());
      }
      
      assertInvokerBindings(prefix, mdb.getInvokerBindings());

      assertEnvironment(prefix, mdb.getJbossJndiEnvironmentRefsGroup(), false);

      assertMethodAttributes(prefix, mdb.getMethodAttributes());

      assertSecurityIdentity(prefix, "SecurityIdentity", mdb.getSecurityIdentity(), false);

      assertDepends(prefix, 2, mdb.getDepends());
      
      assertIORSecurityConfig(prefix, mdb.getIorSecurityConfig());
      
      assertSecurityIdentity(prefix, "EjbTimeoutIdentity", mdb.getEjbTimeoutIdentity(), false);
      
      assertAnnotations(prefix, 2, mdb.getAnnotations());

      assertIgnoreDependency(prefix, mdb.getIgnoreDependency());

      assertEquals(prefix + "AOPDomain", mdb.getAopDomainName());
     
      // TODO DOM pool-config
      
      assertJndiRefs(prefix, 2, mdb.getJndiRefs());

      assertActivationConfig(prefix + "Default", mdb.getDefaultActivationConfig());
   }
   
   private void assertNullMessageDrivenBean(String prefix, JBossMetaData jbossMetaData)
   {
      JBossMessageDrivenBeanMetaData mdb = assertJBossMessageDrivenBean(prefix, jbossMetaData);
      assertNull(mdb.getId());
      assertNull(mdb.getDescriptionGroup());
      
      assertNull(mdb.getDestinationJndiName());
      assertNull(mdb.getLocalJndiName());
      assertNull(mdb.getMdbUser());
      assertNull(mdb.getMdbPassword());
      assertNull(mdb.getMdbClientId());
      assertNull(mdb.getMdbSubscriptionId());
      assertNull(mdb.getResourceAdapterName());
      assertNull(mdb.getConfigurationName());
      assertNull(mdb.getSecurityProxy());

      assertFalse(mdb.isExceptionOnRollback());
      assertTrue(mdb.isTimerPersistence());

      assertNull(mdb.getInvokerBindings());

      assertNullEnvironment(mdb.getJbossJndiEnvironmentRefsGroup());

      assertNull(mdb.getMethodAttributes());

      assertNull(mdb.getSecurityIdentity());
      assertNull(mdb.getEjbTimeoutIdentity());

      assertNull(mdb.getDepends());

      assertNull(mdb.getIorSecurityConfig());

      assertNull(mdb.getAnnotations());
      assertNull(mdb.getIgnoreDependency());
      assertNull(mdb.getAopDomainName());
      assertNull(mdb.getJndiRefs());
   }
   
/*   private void assertFullMessageDrivenBean(String prefix, ApplicationMetaData application, boolean first)
   {
      MessageDrivenMetaData mdb = assertJBossMessageDrivenBean(prefix, application);

      assertEquals(prefix + "DestinationJndiName", mdb.getDestinationJndiName());
      assertEquals(prefix + "LocalJndiName", mdb.getLocalJndiName());
      assertEquals(prefix + "User", mdb.getUser());
      assertEquals(prefix + "Password", mdb.getPasswd());
      assertEquals(prefix + "ClientId", mdb.getClientId());
      assertEquals(prefix + "SubscriptionId", mdb.getSubscriptionId());
      assertEquals(prefix + "RAR", mdb.getResourceAdapterName());
      assertEquals(prefix + "ConfigurationName", mdb.getConfigurationName());
      assertEquals(prefix + "SecurityProxy", mdb.getSecurityProxy());

      if (first)
      {
         assertTrue(mdb.getExceptionRollback());
         assertTrue(mdb.getTimerPersistence());
      }
      else
      {
         assertFalse(mdb.getExceptionRollback());
         assertFalse(mdb.getTimerPersistence());
      }

      assertInvokerBindings(prefix, mdb, mdb.getInvokerBindings());

      assertEnvironment(prefix, mdb, false);

      assertSecurityIdentity(prefix, "SecurityIdentity", mdb.getSecurityIdentityMetaData(), false);

      assertSecurityIdentity(prefix, "EjbTimeoutIdentity", mdb.getEjbTimeoutIdentity(), false);
      
      assertMethodAttributes(prefix, mdb);

      assertDepends(prefix, 2, mdb.getDepends());
      
      assertIORSecurityConfig(prefix, mdb.getIorSecurityConfigMetaData());
   }
*/   
/*   private void assertNullMessageDrivenBean(String prefix, ApplicationMetaData application)
   {
      MessageDrivenMetaData mdb = assertJBossMessageDrivenBean(prefix, application);
      
      assertNull(mdb.getDestinationJndiName());
      String ejbName = mdb.getEjbName();
      String localName = "local/" + ejbName + '@' + System.identityHashCode(ejbName);
      assertEquals(localName, mdb.getLocalJndiName());
      assertNull(mdb.getUser());
      assertNull(mdb.getPasswd());
      assertNull(mdb.getClientId());
      assertNull(mdb.getSubscriptionId());
      assertNull(mdb.getResourceAdapterName());
      assertEquals(ContainerConfigurationMetaData.MESSAGE_INFLOW_DRIVEN, mdb.getConfigurationName());
      assertNull(mdb.getSecurityProxy());

      assertFalse(mdb.getExceptionRollback());
      assertTrue(mdb.getTimerPersistence());
      
      assertNullEnvironment(mdb);

      assertDefaultMethodAttributes(prefix, mdb);
      
      assertNull(mdb.getSecurityIdentityMetaData());
      
      assertNull(mdb.getEjbTimeoutIdentity());
      
      assertEmpty(mdb.getDepends());

      assertNull(mdb.getIorSecurityConfigMetaData());
   }
*/   
   private void fixUpContainerConfigurations(JBossMetaData jbossMetaData)
   {
      ContainerConfigurationsMetaData configurations = jbossMetaData.getContainerConfigurations();
      assertNotNull(configurations);
      fixUpContainerConfiguration(ContainerConfigurationMetaData.STATELESS, configurations);
      fixUpContainerConfiguration(ContainerConfigurationMetaData.CMP_2x, configurations);
      fixUpContainerConfiguration(ContainerConfigurationMetaData.MESSAGE_INFLOW_DRIVEN, configurations);
      fixUpContainerConfiguration("session1ConfigurationName", configurations);
      fixUpContainerConfiguration("session2ConfigurationName", configurations);
      fixUpContainerConfiguration("entity1ConfigurationName", configurations);
      fixUpContainerConfiguration("entity2ConfigurationName", configurations);
      fixUpContainerConfiguration("mdb1ConfigurationName", configurations);
      fixUpContainerConfiguration("mdb2ConfigurationName", configurations);
   }
   
   private void fixUpContainerConfiguration(String name, ContainerConfigurationsMetaData configurations)
   {
      if (configurations.get(name) == null)
      {
         ContainerConfigurationMetaData configuration = new ContainerConfigurationMetaData();
         configuration.setName(name);
         configurations.add(configuration);
      }
   }
   
   private void assertContainerConfigurations(JBossMetaData jbossMetaData)
   {
      ContainerConfigurationsMetaData configurations = jbossMetaData.getContainerConfigurations();
      assertNotNull(configurations);
      assertEquals(14, configurations.size());
      assertNullContainerConfiguration("containerConfiguration0", jbossMetaData);
      assertFullContainerConfiguration("containerConfiguration1", jbossMetaData, true);
      assertFullContainerConfiguration("containerConfiguration2", jbossMetaData, false);
      ContainerConfigurationMetaData configuration = assertContainerConfiguration("containerConfiguration3", jbossMetaData);
      assertEquals(CommitOption.B, configuration.getCommitOption());
      configuration = assertContainerConfiguration("containerConfiguration4", jbossMetaData);
      assertEquals(CommitOption.C, configuration.getCommitOption());
   }
   
/*   private void assertContainerConfigurations(ApplicationMetaData application)
   {
      Iterator<ConfigurationMetaData> configurations = application.getConfigurations();
      assertNotNull(configurations);
      int count = 0;
      while (configurations.hasNext())
      {
         configurations.next();
         ++count;
      }
      assertEquals(14, count);
      assertNullContainerConfiguration("containerConfiguration0", application);
      assertFullContainerConfiguration("containerConfiguration1", application, true);
      assertFullContainerConfiguration("containerConfiguration2", application, false);
      ConfigurationMetaData configuration = assertContainerConfiguration("containerConfiguration3", application);
      assertEquals(ConfigurationMetaData.B_COMMIT_OPTION, configuration.getCommitOption());
      configuration = assertContainerConfiguration("containerConfiguration4", application);
      assertEquals(ConfigurationMetaData.C_COMMIT_OPTION, configuration.getCommitOption());
   }
*/   
   private ContainerConfigurationMetaData assertContainerConfiguration(String prefix, JBossMetaData jBossMetaData)
   {
      ContainerConfigurationMetaData configuration = jBossMetaData.getContainerConfiguration(prefix + "Name");
      assertNotNull(configuration);
      assertEquals(prefix + "Name", configuration.getContainerName());
      return configuration;
   }
   
   private void assertFullContainerConfiguration(String prefix, JBossMetaData jBossMetaData, boolean first)
   {
      ContainerConfigurationMetaData configuration = assertContainerConfiguration(prefix, jBossMetaData);
      assertId(prefix, configuration);
      assertDescriptions(prefix, configuration.getDescriptions());
      assertEquals(prefix + "Extends", configuration.getExtendsName());
      assertEquals(prefix + "InstancePool", configuration.getInstancePool());
      assertEquals(prefix + "InstanceCache", configuration.getInstanceCache());
      assertEquals(prefix + "PersistenceManager", configuration.getPersistenceManager());
      assertEquals(prefix + "WebClassLoader", configuration.getWebClassLoader());
      assertEquals(prefix + "LockingPolicy", configuration.getLockingPolicy());
      assertEquals(prefix + "SecurityDomain", configuration.getSecurityDomain());
      if (first)
      {
         assertTrue(configuration.isCallLogging());
         assertTrue(configuration.isSyncOnCommitOnly());
         assertTrue(configuration.isInsertAfterEjbPostCreate());
         assertTrue(configuration.isEjbStoreOnClean());
         assertTrue(configuration.isStoreNotFlushed());
      }
      else
      {
         assertFalse(configuration.isCallLogging());
         assertFalse(configuration.isSyncOnCommitOnly());
         assertFalse(configuration.isInsertAfterEjbPostCreate());
         assertFalse(configuration.isEjbStoreOnClean());
         assertFalse(configuration.isStoreNotFlushed());
      }
      assertInvokerProxyBindingNames(prefix, 2, configuration.getInvokerProxyBindingNames());
      // TODO DOM container interceptors
      // TODO DOM container cache conf
      // TODO DOM container pool conf
      assertEquals(CommitOption.D, configuration.getCommitOption());
      assertEquals(10000, configuration.getOptiondRefreshRateMillis());
      assertClusterConfig(prefix, configuration.getClusterConfig(), true);
      assertDepends(prefix, 2, configuration.getDepends());
   }
   
   private void assertNullContainerConfiguration(String prefix, JBossMetaData jBossMetaData)
   {
      ContainerConfigurationMetaData configuration = assertContainerConfiguration(prefix, jBossMetaData);
      assertNull(configuration.getId());
      assertNull(configuration.getDescriptions());
      assertNull(configuration.getExtendsName());
      assertNull(configuration.getInstancePool());
      assertNull(configuration.getInstanceCache());
      assertNull(configuration.getPersistenceManager());
      assertNull(configuration.getWebClassLoader());
      assertNull(configuration.getLockingPolicy());
      assertNull(configuration.getSecurityDomain());
      assertFalse(configuration.isCallLogging());
      assertFalse(configuration.isSyncOnCommitOnly());
      assertFalse(configuration.isInsertAfterEjbPostCreate());
      assertFalse(configuration.isEjbStoreOnClean());
      assertTrue(configuration.isStoreNotFlushed());
      assertNull(configuration.getInvokerProxyBindingNames());
      // TODO DOM container interceptors
      // TODO DOM container cache conf
      // TODO DOM container pool conf
      assertEquals(CommitOption.A, configuration.getCommitOption());
      assertEquals(30000, configuration.getOptiondRefreshRateMillis());
      assertNull(configuration.getClusterConfig());
      assertNull(configuration.getDepends());
   }
   
/*   private ConfigurationMetaData assertContainerConfiguration(String prefix, ApplicationMetaData application)
   {
      ConfigurationMetaData configuration = application.getConfigurationMetaDataByName(prefix + "Name");
      assertNotNull(configuration);
      assertEquals(prefix + "Name", configuration.getName());
      return configuration;
   }
*/   
/*   private void assertFullContainerConfiguration(String prefix, ApplicationMetaData application, boolean first)
   {
      ConfigurationMetaData configuration = assertContainerConfiguration(prefix, application);
      assertEquals(prefix + "InstancePool", configuration.getInstancePool());
      assertEquals(prefix + "InstanceCache", configuration.getInstanceCache());
      assertEquals(prefix + "PersistenceManager", configuration.getPersistenceManager());
      assertEquals(prefix + "WebClassLoader", configuration.getWebClassLoader());
      assertEquals(prefix + "LockingPolicy", configuration.getLockClass());
      assertEquals(prefix + "SecurityDomain", configuration.getSecurityDomain());
      if (first)
      {
         assertTrue(configuration.getCallLogging());
         assertTrue(configuration.getSyncOnCommitOnly());
         assertTrue(configuration.isInsertAfterEjbPostCreate());
         assertTrue(configuration.isEjbStoreForClean());
         assertTrue(configuration.isStoreNotFlushed());
      }
      else
      {
         assertFalse(configuration.getCallLogging());
         assertFalse(configuration.getSyncOnCommitOnly());
         assertFalse(configuration.isInsertAfterEjbPostCreate());
         assertFalse(configuration.isEjbStoreForClean());
         assertFalse(configuration.isStoreNotFlushed());
      }
      assertInvokerProxyBindingNames(prefix, 2, configuration.getInvokers());
      // TODO DOM container interceptors
      // TODO DOM container cache conf
      // TODO DOM container pool conf
      assertEquals(ConfigurationMetaData.D_COMMIT_OPTION, configuration.getCommitOption());
      assertEquals(10000, configuration.getOptionDRefreshRate());
      assertClusterConfig(prefix, configuration.getClusterConfigMetaData(), true);
      assertDepends(prefix, 2, configuration.getDepends());
   }
*/   
/*   private void assertNullContainerConfiguration(String prefix, ApplicationMetaData application)
   {
      ConfigurationMetaData configuration = assertContainerConfiguration(prefix, application);
      assertNull(configuration.getInstancePool());
      assertNull(configuration.getInstanceCache());
      assertNull(configuration.getPersistenceManager());
      assertNull(configuration.getWebClassLoader());
      assertNull(configuration.getLockClass());
      assertNull(configuration.getSecurityDomain());
      assertFalse(configuration.getCallLogging());
      assertFalse(configuration.getSyncOnCommitOnly());
      assertFalse(configuration.isInsertAfterEjbPostCreate());
      assertFalse(configuration.isEjbStoreForClean());
      assertTrue(configuration.isStoreNotFlushed());
      assertInvokerProxyBindingNames(null, 0, configuration.getInvokers());
      // TODO DOM container interceptors
      // TODO DOM container cache conf
      // TODO DOM container pool conf
      assertEquals(ConfigurationMetaData.A_COMMIT_OPTION, configuration.getCommitOption());
      assertEquals(30000, configuration.getOptionDRefreshRate());
      assertNull(configuration.getClusterConfigMetaData());
      assertEmpty(configuration.getDepends());
   }
*/   
   private void assertInvokerProxyBindingNames(String prefix, int size, Collection<String> names)
   {
      assertNotNull(names);
      assertEquals(2, names.size());
      for(int count = 1; count <= names.size(); ++count)
      {
         assertTrue(names.contains(prefix + "InvokerProxyBindingName" + count));
      }
   }
   
   private void assertInvokerProxyBindingNames(String prefix, int size, String[] names)
   {
      assertNotNull(names);
      assertEquals(size, names.length);
      int count = 1;
      for (String name : names)
      {
         assertEquals(prefix + "InvokerProxyBindingName" + count, name);
         ++count;
      }
   }
   
   private void assertDepends(String prefix, int size, Collection<String> depends)
   {
      assertNotNull(depends);
      assertEquals(size, depends.size());
      for(int count = 1; count <= depends.size(); ++count)
      {
         assertTrue(depends.contains(prefix + "Depends" + count));
      }
   }
   
   private void assertInvokerProxyBindings(InvokerProxyBindingsMetaData bindings)
   {
      assertNotNull(bindings);
      assertId("invoker-proxy-bindings", bindings);
      assertDescriptions("invoker-proxy-bindings", bindings.getDescriptions());
      assertEquals(2, bindings.size());
      int count = 1;
      for (InvokerProxyBindingMetaData binding : bindings)
      {
         assertInvokerProxyBinding("invokerProxyBinding" + count, count, binding);
         ++count;
      }
   }
   
   private void assertInvokerProxyBinding(String prefix, int count, InvokerProxyBindingMetaData binding)
   {
      assertNotNull(binding);
      assertId(prefix, binding);
      assertDescriptions(prefix, binding.getDescriptions());
      assertEquals(prefix + "Name", binding.getInvokerProxyBindingName());
      assertEquals(prefix + "InvokerMBean", binding.getInvokerMBean());
      assertEquals(prefix + "ProxyFactory", binding.getProxyFactory());
      // The DOM invoker-proxy-config
      Element config = binding.getProxyFactoryConfig();
      if (config == null)
         return;
      if (config.getElementsByTagName("client-interceptors").getLength() > 0)
         assertInvokerProxyBindingPFCClientInterceptor(prefix, count, config);
   }
   private void assertInvokerProxyBindingPFCClientInterceptor(String prefix, int count, Element config)
   {
      NodeList ci = config.getElementsByTagName("client-interceptors");
      assertEquals("client-interceptors count is 1", 1, ci.getLength());
      Element cis = (Element) ci.item(0);
      NodeList home = cis.getElementsByTagName("home");
      Element homeE = (Element) home.item(0);
      NodeList homeInterceptors = homeE.getElementsByTagName("interceptor");
      assertEquals("home count is 4", 4, homeInterceptors.getLength());
      for(int n = 0; n < homeInterceptors.getLength(); n ++)
      {
         Element interceptor = (Element) homeInterceptors.item(n);
         String callByValue = interceptor.getAttribute("call-by-value");
         String text = interceptor.getTextContent();
         String expected;
         if (callByValue.length() == 0)
            expected = "org.jboss.proxy.ejb.HomeInterceptor"+(n+1)+"."+count;
         else
            expected = "org.jboss.proxy.ejb.HomeInterceptor"+(Boolean.valueOf(callByValue)?"cbvt" : "cbvf")+(n+1)+"."+count;
         assertEquals(expected, text);
      }
      NodeList bean = cis.getElementsByTagName("bean");
      Element beanE = (Element) bean.item(0);
      NodeList beanInterceptors = beanE.getElementsByTagName("interceptor");
      assertEquals("bean count is 4", 4, beanInterceptors.getLength());
      for(int n = 0; n < beanInterceptors.getLength(); n ++)
      {
         Element interceptor = (Element) beanInterceptors.item(n);
         String callByValue = interceptor.getAttribute("call-by-value");
         String text = interceptor.getTextContent();
         String expected;
         if (callByValue.length() == 0)
            expected = "org.jboss.proxy.ejb.BeanInterceptor"+(n+1)+"."+count;
         else
            expected = "org.jboss.proxy.ejb.BeanInterceptor"+(Boolean.valueOf(callByValue)?"cbvt" : "cbvf")+(n+1)+"."+count;
         assertEquals(expected, text);
      }
   }

/*   private void assertInvokerProxyBindings(Iterator<org.jboss.metadata.InvokerProxyBindingMetaData> bindings)
   {
      assertNotNull(bindings);
      int count = 1;
      while (bindings.hasNext())
      {
         assertInvokerProxyBinding("invokerProxyBinding" + count, bindings.next());
         ++count;
      }
      assertEquals(3, count);
   }
*/   
/*   private void assertInvokerProxyBinding(String prefix, org.jboss.metadata.InvokerProxyBindingMetaData binding)
   {
      assertNotNull(binding);
      assertEquals(prefix + "Name", binding.getName());
      assertEquals(prefix + "InvokerMBean", binding.getInvokerMBean());
      assertEquals(prefix + "ProxyFactory", binding.getProxyFactory());
      // TODO DOM invoker-proxy-config
   }
*/   
   private void assertInvokerBindings(String prefix, InvokerBindingsMetaData bindings)
   {
      assertId(prefix + "InvokerBindings", bindings);
      assertDescriptions(prefix + "InvokerBindings", bindings.getDescriptions());
      assertNotNull(bindings);
      assertEquals(2, bindings.size());
      int count = 1;
      for (InvokerBindingMetaData binding : bindings)
      {
         assertInvokerBinding(prefix + "Invoker" + count, count, binding);
         ++count;
      }
   }
   
   private void assertInvokerBinding(String prefix, int count, InvokerBindingMetaData binding)
   {
      assertNotNull(binding);
      assertId(prefix, binding);
      assertDescriptions(prefix, binding.getDescriptions());
      assertEquals(prefix + "Name", binding.getInvokerProxyBindingName());
      assertEquals(prefix + "JndiName", binding.getJndiName());
      // TODO LAST ejb-ref - needs a seperate test
   }
   
/*   private void assertInvokerBindings(String prefix, BeanMetaData bean, Iterator<String> names)
   {
      assertNotNull(names);
      int count = 1;
      while (names.hasNext())
      {
         String name = names.next();
         assertInvokerBinding(prefix + "Invoker" + count, count, bean, name);
         ++count;
      }
      assertEquals(3, count);
   }
*/   
/*   private void assertInvokerBinding(String prefix, int count, BeanMetaData bean, String name)
   {
      assertEquals(prefix + "Name", name);
      assertEquals(prefix + "JndiName", bean.getInvokerBinding(name));
   }
*/   
/*   private void assertDefaultInvoker(String name, BeanMetaData bean)
   {
      assertEquals(bean.getJndiName(), bean.getInvokerBinding(name));
   }
*/   
   private void assertResourceManagers(ResourceManagersMetaData resources)
   {
      assertNotNull(resources);
      assertId("resource-managers", resources);
      assertDescriptions("resource-managers", resources.getDescriptions());
      assertEquals(2, resources.size());
      int count = 1;
      for (ResourceManagerMetaData resource : resources)
      {
         assertResourceManager("resourceManager" + count, count == 1, resource);
         ++count;
      }
   }
   
   private void assertResourceManager(String prefix, boolean jndi, ResourceManagerMetaData resource)
   {
      assertNotNull(resource);
      assertId(prefix, resource);
      assertDescriptions(prefix, resource.getDescriptions());
      assertEquals(prefix + "Name", resource.getResName());
      if (jndi)
      {
         assertEquals(prefix + "JndiName", resource.getResJndiName());
         assertNull(resource.getResUrl());
         assertEquals(prefix + "JndiName", resource.getResource());
      }
      else
      {
         assertNull(resource.getResJndiName());
         assertEquals(prefix + "URL", resource.getResUrl());
         assertEquals(prefix + "URL", resource.getResource());
      }
   }
   
   protected void assertAssemblyDescriptor(JBossMetaData jbossMetaData)
   {
      JBossAssemblyDescriptorMetaData assemblyDescriptorMetaData = jbossMetaData.getAssemblyDescriptor();
      assertNotNull(assemblyDescriptorMetaData);
      assertId("assembly-descriptor", assemblyDescriptorMetaData);
      assertSecurityRoles(2, assemblyDescriptorMetaData.getSecurityRoles());
      assertMessageDestinations(2, assemblyDescriptorMetaData.getMessageDestinations());
   }
   
   protected void fixUpAssemblyDescriptor(JBossMetaData jbossMetaData)
   {
      EjbJarMetaData ejbJarMetaData = jbossMetaData.getOverridenMetaData();
      assertNotNull(ejbJarMetaData);
      AssemblyDescriptorMetaData assemblyDescriptorMetaData = ejbJarMetaData.getAssemblyDescriptor();
      if (assemblyDescriptorMetaData == null)
      {
         assemblyDescriptorMetaData = new AssemblyDescriptorMetaData();
         ejbJarMetaData.setAssemblyDescriptor(assemblyDescriptorMetaData);
      }
      fixUpMessageDestinations(2, assemblyDescriptorMetaData);
   }

   protected void fixUpMessageDestinations(int size, AssemblyDescriptorMetaData assemblyDescriptorMetaData)
   {
      MessageDestinationsMetaData messageDestinationsMetaData = assemblyDescriptorMetaData.getMessageDestinations();
      if (messageDestinationsMetaData == null)
      {
         messageDestinationsMetaData = new MessageDestinationsMetaData();
         assemblyDescriptorMetaData.setMessageDestinations(messageDestinationsMetaData);
      }
      fixUpMessageDestinations(size, messageDestinationsMetaData);
   }

/*   protected void assertAssemblyDescriptor(ApplicationMetaData applicationMetaData)
   {
      org.jboss.metadata.AssemblyDescriptorMetaData assemblyDescriptorMetaData = applicationMetaData.getAssemblyDescriptor();
      assertNotNull(assemblyDescriptorMetaData);
      assertSecurityRoles(2, assemblyDescriptorMetaData.getSecurityRoles());
      assertMessageDestination("messageDestination1", assemblyDescriptorMetaData.getMessageDestinationMetaData("messageDestination1Name"));
      assertMessageDestination("messageDestination2", assemblyDescriptorMetaData.getMessageDestinationMetaData("messageDestination2Name"));
   }
*/
   @Override
   protected void assertSecurityRole(String prefix, SecurityRoleMetaData securityRoleMetaData)
   {
      super.assertSecurityRole(prefix, securityRoleMetaData);
      assertPrincipals(prefix, 2, securityRoleMetaData.getPrincipals());
   }

/*   @Override
   protected void assertSecurityRole(String prefix, org.jboss.security.SecurityRoleMetaData securityRoleMetaData)
   {
      super.assertSecurityRole(prefix, securityRoleMetaData);
      assertPrincipals(prefix, 2, securityRoleMetaData.getPrincipals());
   }
*/
   protected void assertMessageDestination(String prefix, MessageDestinationMetaData messageDestinationMetaData)
   {
      assertMessageDestination14(prefix, messageDestinationMetaData);
      assertEquals(prefix + "JndiName", messageDestinationMetaData.getMappedName());
   }

   protected void fixUpEnvironment(EnterpriseBeanMetaData ejb, JBossEnterpriseBeanMetaData jbossEjb)
   {
      EnvironmentRefsGroupMetaData jbossEjbEnvironment = jbossEjb.getJbossJndiEnvironmentRefsGroup();
      if (jbossEjbEnvironment == null)
         return;

      EnvironmentRefsGroupMetaData ejbEnvironment = ejb.getJndiEnvironmentRefsGroup();
      if (ejbEnvironment == null)
      {
         ejbEnvironment = new EnvironmentRefsGroupMetaData();
         ejb.setJndiEnvironmentRefsGroup(ejbEnvironment);
      }

      EJBReferencesMetaData ejbReferences = fixUpEjbRefs(ejbEnvironment.getEjbReferences(), jbossEjbEnvironment.getEjbReferences());
      if (ejbReferences != null)
         ejbEnvironment.setEjbReferences(ejbReferences);
      EJBLocalReferencesMetaData ejbLocalReferences = fixUpEjbLocalRefs(ejbEnvironment.getEjbLocalReferences(), jbossEjbEnvironment.getEjbLocalReferences());
      if (ejbLocalReferences != null)
         ejbEnvironment.setEjbLocalReferences(ejbLocalReferences);
      // TODO webservice service ref
      ResourceReferencesMetaData resourceReferences = fixUpResourceRefs(ejbEnvironment.getResourceReferences(), jbossEjbEnvironment.getResourceReferences());
      if (resourceReferences != null)
         ejbEnvironment.setResourceReferences(resourceReferences);
      ResourceEnvironmentReferencesMetaData resourceEnvironmentReferences = fixUpResourceEnvRefs(ejbEnvironment.getResourceEnvironmentReferences(), jbossEjbEnvironment.getResourceEnvironmentReferences());
      if (resourceEnvironmentReferences != null)
         ejbEnvironment.setResourceEnvironmentReferences(resourceEnvironmentReferences);
      MessageDestinationReferencesMetaData messageDestinationReferences = fixUpMessageDestinationRefs(ejbEnvironment.getMessageDestinationReferences(), jbossEjbEnvironment.getMessageDestinationReferences());
      if (messageDestinationReferences != null)
         ejbEnvironment.setMessageDestinationReferences(messageDestinationReferences);
   }
   
   @Override
   protected void assertResourceGroup(String prefix, ResourceInjectionMetaData resourceInjectionMetaData, boolean full, boolean first)
   {
      super.assertResourceGroupNoJndiName(prefix, resourceInjectionMetaData, true, first);
      assertEquals(prefix + "JndiName", resourceInjectionMetaData.getMappedName());
      if (first)
         assertTrue(resourceInjectionMetaData.isDependencyIgnored());
      else
         assertFalse(resourceInjectionMetaData.isDependencyIgnored());
   }
      
   @Override
   protected void assertSecurityIdentity(String ejbName, String type, SecurityIdentityMetaData securityIdentity, boolean full)
   {
      super.assertSecurityIdentity(ejbName, type, securityIdentity, full);
      assertEquals(ejbName + type + "RunAsPrincipal", securityIdentity.getRunAsPrincipal());
   }
   
/*   @Override
   protected void assertSecurityIdentity(String ejbName, String type, org.jboss.metadata.SecurityIdentityMetaData securityIdentity, boolean full)
   {
      super.assertSecurityIdentity(ejbName, type, securityIdentity, full);
      assertEquals(ejbName + type + "RunAsPrincipal", securityIdentity.getRunAsPrincipalName());
   }
*/
/*   @Override
   protected void assertMethodAttributes(String ejbName, BeanMetaData bean)
   {
      assertTrue(bean.isMethodReadOnly("getSomething"));
      assertEquals(5000, bean.getTransactionTimeout("getSomething"));
      assertFalse(bean.isMethodReadOnly("setSomething"));
      assertEquals(0, bean.getTransactionTimeout("setSomething"));
   }
*/}
