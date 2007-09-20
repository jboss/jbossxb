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

import java.math.BigDecimal;

import junit.framework.Test;

import org.jboss.ejb.metadata.spec.CMPFieldMetaData;
import org.jboss.ejb.metadata.spec.CMPFieldsMetaData;
import org.jboss.ejb.metadata.spec.EjbJar21MetaData;
import org.jboss.ejb.metadata.spec.EjbJar2xMetaData;
import org.jboss.ejb.metadata.spec.EjbJarMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeansMetaData;
import org.jboss.ejb.metadata.spec.EntityBeanMetaData;
import org.jboss.ejb.metadata.spec.MessageDrivenBeanMetaData;
import org.jboss.ejb.metadata.spec.PersistenceType;
import org.jboss.ejb.metadata.spec.QueriesMetaData;
import org.jboss.ejb.metadata.spec.QueryMetaData;
import org.jboss.ejb.metadata.spec.QueryMethodMetaData;
import org.jboss.ejb.metadata.spec.ResultTypeMapping;
import org.jboss.ejb.metadata.spec.SessionBeanMetaData;
import org.jboss.ejb.metadata.spec.SessionType;
import org.jboss.ejb.metadata.spec.TransactionType;
import org.jboss.javaee.metadata.spec.LifecycleCallbacksMetaData;
import org.jboss.javaee.metadata.spec.PersistenceContextReferencesMetaData;
import org.jboss.javaee.metadata.spec.PersistenceUnitReferencesMetaData;
import org.jboss.javaee.metadata.spec.ResourceInjectionMetaData;
import org.jboss.test.ejb.AbstractEJBEverythingTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;

/**
 * EjbJar2xUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class EjbJar21EverythingUnitTestCase extends AbstractEJBEverythingTest
{
   public static Test suite()
   {
      return suite(EjbJar21EverythingUnitTestCase.class);
   }
   
   public static SchemaBindingResolver initResolver()
   {
      return schemaResolverForClass(EjbJar21MetaData.class);
      //return AbstractJavaEEMetaDataTest.initResolverJ2EE(EjbJar21MetaData.class);
   }
   
   public EjbJar21EverythingUnitTestCase(String name)
   {
      super(name);
   }
   
   protected EjbJar2xMetaData unmarshal() throws Exception
   {
      return unmarshal(EjbJar21MetaData.class);
   }
   
   public void testEverything() throws Exception
   {
      //enableTrace("org.jboss.xb");
      //enableTrace("org.jboss.xb.builder");
      EjbJar2xMetaData ejbJarMetaData = unmarshal();
      assertEverything(ejbJarMetaData);
   }

   public void assertEverything(EjbJar2xMetaData ejbJarMetaData)
   {
      //ApplicationMetaData applicationMetaData = new ApplicationMetaData(ejbJarMetaData); 
      assertVersion(ejbJarMetaData);
      //assertVersion(applicationMetaData);
      assertId("ejb-jar", ejbJarMetaData);
      assertEquals("ejb-jar-id", ejbJarMetaData.getId());
      assertEjbClientJar(ejbJarMetaData);
      assertDescriptionGroup("ejb-jar", ejbJarMetaData.getDescriptionGroup());
      assertEnterpriseBeans(ejbJarMetaData);
      //assertEnterpriseBeans(applicationMetaData);
      assertRelationships(ejbJarMetaData);
      //assertRelationships(applicationMetaData);
      assertAssemblyDescriptor(ejbJarMetaData);
      //assertAssemblyDescriptor(applicationMetaData);
   }
   
   private void assertVersion(EjbJar2xMetaData ejbJar2xMetaData)
   {
      assertEquals(new BigDecimal("2.1"), ejbJar2xMetaData.getVersion());
      assertFalse(ejbJar2xMetaData.isEJB1x());
      assertTrue(ejbJar2xMetaData.isEJB2x());
      assertTrue(ejbJar2xMetaData.isEJB21());
      assertFalse(ejbJar2xMetaData.isEJB3x());
   }
   
/*   private void assertVersion(ApplicationMetaData applicationMetadata)
   {
      assertFalse(applicationMetadata.isEJB1x());
      assertTrue(applicationMetadata.isEJB2x());
      assertTrue(applicationMetadata.isEJB21());
      assertFalse(applicationMetadata.isEJB3x());
   }
*/
   protected SessionBeanMetaData assertFullSession(String ejbName, EnterpriseBeansMetaData enterpriseBeansMetaData)
   {
      SessionBeanMetaData session = assertSession(ejbName + "EjbName", enterpriseBeansMetaData);
      assertFullSessionBean(ejbName, session);      
      return session;
   }

   public void assertFullSessionBean(String ejbName, SessionBeanMetaData session)
   {
      assertId(ejbName, session);
      assertClass(ejbName, "Home", session.getHome());
      assertClass(ejbName, "Remote", session.getRemote());
      assertClass(ejbName, "LocalHome", session.getLocalHome());
      assertClass(ejbName, "Local", session.getLocal());
      assertClass(ejbName, "ServiceEndpoint", session.getServiceEndpoint());
      assertClass(ejbName, "EjbClass", session.getEjbClass());
      assertEquals(SessionType.Stateless, session.getSessionType());
      assertEquals(TransactionType.Container, session.getTransactionType());
      assertEnvironment(ejbName, session.getJndiEnvironmentRefsGroup(), true);
      assertContainerTransactions(ejbName, 6, 6, session.getContainerTransactions());
      assertMethodPermissions(ejbName, ejbName + "MethodPermission", 3, 3, session.getMethodPermissions());
      assertExcludeList(ejbName, 5, 5, session.getExcludeList());
      assertSecurityRoleRefs(ejbName, 2, session.getSecurityRoleRefs());
      assertSecurityIdentity(ejbName, "SecurityIdentity", session.getSecurityIdentity(), true);
   }
   
   protected EntityBeanMetaData assertFullEntity(String ejbName, EnterpriseBeansMetaData enterpriseBeansMetaData)
   {
      EntityBeanMetaData entity = assertEntity(ejbName + "EjbName", enterpriseBeansMetaData);
      assertFullEntity(ejbName, entity);      
      return entity;
   }

   public void assertFullEntity(String ejbName, EntityBeanMetaData entity)
   {
      assertId(ejbName, entity);
      assertClass(ejbName, "Home", entity.getHome());
      assertClass(ejbName, "Remote", entity.getRemote());
      assertClass(ejbName, "LocalHome", entity.getLocalHome());
      assertClass(ejbName, "Local", entity.getLocal());
      assertClass(ejbName, "EjbClass", entity.getEjbClass());
      assertEquals(PersistenceType.Container, entity.getPersistenceType());
      assertEquals(ejbName + "PrimKeyClass", entity.getPrimKeyClass());
      assertTrue(entity.isReentrant());
      assertEquals("2.x", entity.getCmpVersion());
      assertFalse(entity.isCMP1x());
      assertEquals(ejbName + "AbstractSchemaName", entity.getAbstractSchemaName());
      assertCmpFields(ejbName, 2, entity.getCmpFields());
      assertEquals(ejbName + "PrimKeyField", entity.getPrimKeyField());
      assertEnvironment(ejbName, entity.getJndiEnvironmentRefsGroup(), true);
      assertContainerTransactions(ejbName, 6, 6, entity.getContainerTransactions());
      assertMethodPermissions(ejbName, ejbName + "MethodPermission", 3, 3, entity.getMethodPermissions());
      assertExcludeList(ejbName, 5, 5, entity.getExcludeList());
      assertSecurityRoleRefs(ejbName, 2, entity.getSecurityRoleRefs());
      assertSecurityIdentity(ejbName, "SecurityIdentity", entity.getSecurityIdentity(), true);
      assertQueries(ejbName, 2, entity.getQueries());
   }

   private void assertCmpFields(String ejbName, int size, CMPFieldsMetaData cmpFieldsMetaData)
   {
      assertNotNull(cmpFieldsMetaData);
      assertEquals(size, cmpFieldsMetaData.size());
      int count = 1;
      for (CMPFieldMetaData cmpField : cmpFieldsMetaData)
      {
         assertId(ejbName + "CmpField" + count, cmpField);
         assertEquals(ejbName + "CmpField" + count, cmpField.getFieldName());
         ++count;
      }
   }

   private void assertQueries(String ejbName, int size, QueriesMetaData queriesMetaData)
   {
      assertNotNull(queriesMetaData);
      assertEquals(size, queriesMetaData.size());
      int count = 1;
      for (QueryMetaData query : queriesMetaData)
      {
         assertId(ejbName + "Query" + count, query);
         assertQueryMethod(ejbName + "Query" + count, 2, query.getQueryMethod());
         if (count == 1)
            assertEquals(ResultTypeMapping.Local, query.getResultTypeMapping());
         else
            assertEquals(ResultTypeMapping.Remote, query.getResultTypeMapping());
         assertEquals(ejbName + "Query" + count + "EjbQL", query.getEjbQL());
         ++count;
      }
   }

   private void assertQueryMethod(String ejbName, int size, QueryMethodMetaData queryMethodMetaData)
   {
      assertNotNull(queryMethodMetaData);
      assertId(ejbName + "QueryMethod", queryMethodMetaData);
      assertEquals(ejbName + "QueryMethod", queryMethodMetaData.getMethodName());
      if (size > 0)
         assertMethodParams(ejbName + "QueryMethod", size, queryMethodMetaData.getMethodParams());
   }
   
   protected MessageDrivenBeanMetaData assertFullMDB(String ejbName, EnterpriseBeansMetaData enterpriseBeansMetaData)
   {
      MessageDrivenBeanMetaData mdb = assertMDB(ejbName + "EjbName", enterpriseBeansMetaData);
      assertId(ejbName, mdb);
      assertEquals(ejbName + "MessagingType", mdb.getMessagingType());
      assertEquals(TransactionType.Container, mdb.getTransactionType());
      assertEquals(ejbName + "MessageDestinationType", mdb.getMessageDestinationType());
      assertEquals(ejbName + "MessageDestinationLink", mdb.getMessageDestinationLink());
      assertActivationConfig(ejbName, mdb.getActivationConfig());
      assertEnvironment(ejbName, mdb.getJndiEnvironmentRefsGroup(), true);
      assertContainerTransactions(ejbName, 6, 6, mdb.getContainerTransactions());
      assertMethodPermissions(ejbName, ejbName + "MethodPermission", 3, 3, mdb.getMethodPermissions());
      assertExcludeList(ejbName, 5, 5, mdb.getExcludeList());
      assertSecurityIdentity(ejbName, "SecurityIdentity", mdb.getSecurityIdentity(), true);
      
      return mdb;
   }
   
   private void assertEjbClientJar(EjbJarMetaData ejbJarMetaData)
   {
      assertEquals("some/path/client.jar", ejbJarMetaData.getEjbClientJar());
   }

   protected void assertPersistenceContextRefs(String prefix, int size, PersistenceContextReferencesMetaData persistenceContextReferencesMetaData)
   {
      assertNull(persistenceContextReferencesMetaData);
   }

   protected void assertPersistenceUnitRefs(String prefix, int size, PersistenceUnitReferencesMetaData persistenceUnitReferencesMetaData)
   {
      assertNull(persistenceUnitReferencesMetaData);
   }

   protected void assertLifecycleCallbacks(String ejbName, String type, int size, LifecycleCallbacksMetaData lifecycleCallbacksMetaData)
   {
      assertNull(lifecycleCallbacksMetaData);
   }

   protected void assertResourceGroup(String prefix, ResourceInjectionMetaData resourceInjectionMetaData, boolean full, boolean first)
   {
      return;
   }

   protected void assertJndiName(String prefix, boolean full, String jndiName)
   {
      if (full)
         assertNull(jndiName);
      else
         assertEquals(prefix + "JndiName", jndiName);
   }
}
