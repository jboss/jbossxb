/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

import java.sql.Types;
import java.util.List;

import junit.framework.Test;

import org.jboss.ejb.metadata.jboss.cmp.JBossCMPFieldMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPEntityBeanMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPFieldsMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPQueryMetaData;
import org.jboss.ejb.metadata.spec.EjbJar21MetaData;
import org.jboss.ejb.metadata.spec.EjbJar30MetaData;
import org.jboss.javaee.metadata.spec.JavaEEMetaDataConstants;
import org.jboss.test.ejb.AbstractEJBEverythingTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.builder.JBossXBBuilder;
import org.w3c.dom.ls.LSInput;


/**
 * A JBossCMPMergeUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPMergeUnitTestCase
   extends AbstractEJBEverythingTest
{
   public static Test suite()
   {
      return suite(JBossCMPMergeUnitTestCase.class);
   }

   public static SchemaBindingResolver initResolver()
   {
      return new SchemaBindingResolver()
      {
         public String getBaseURI()
         {
            return null;
         }

         public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
         {
            SchemaBinding schema;
            if(JavaEEMetaDataConstants.JAVAEE_NS.equals(nsUri))
            {
               schema = JBossXBBuilder.build(EjbJar30MetaData.class);
            }
            else if(JavaEEMetaDataConstants.J2EE_NS.equals(nsUri))
            {
               schema = JBossXBBuilder.build(EjbJar21MetaData.class);
            }
            else if(JavaEEMetaDataConstants.JBOSS_CMP2X_NS.equals(nsUri))
            {
               schema = JBossXBBuilder.build(JBossCMPMetaData.class);
            }
            else
            {
               throw new IllegalStateException("Unexpected namespace: " + nsUri);
            }
            return schema;
         }

         public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
         {
            return null;
         }

         public void setBaseURI(String baseURI)
         {
         }
      };
   }

   public JBossCMPMergeUnitTestCase(String name)
   {
      super(name);
   }

   /**
    * Very basic merge test
    */
   public void testMerge() throws Exception
   {
      EjbJar21MetaData ejbJarMetaData = unmarshal("EjbJar21Everything_testEverything.xml", EjbJar21MetaData.class, null);
      EjbJar21EverythingUnitTestCase ejbJar = new EjbJar21EverythingUnitTestCase("ejb-jar");
      ejbJar.assertEverything(ejbJarMetaData);

      JBossCMPMetaData jbossCMPMetaData = unmarshal("JBossCMPMerge_testMerge.xml", JBossCMPMetaData.class, null);

      jbossCMPMetaData.setOverridenMetaData(ejbJarMetaData);

      assertEquals(6, jbossCMPMetaData.getMergedEnterpriseBeans().size());

      // both null
      JBossCMPEntityBeanMetaData ejb = jbossCMPMetaData.getMergedEnterpriseBean("entity0EjbName");      
      assertNullEntity("entity0", ejb);
      assertNotNull(ejb.getOverridenMetaData());
      ejbJar.assertNullEntity(ejb.getOverridenMetaData());

      // full in ejb-jar and null in jbosscmp
      ejb = jbossCMPMetaData.getMergedEnterpriseBean("entity1EjbName");      
      assertNotNull(ejb);
      assertEquals("entity1EjbName", ejb.getEjbName());
      assertNull(ejb.getAudit());
      assertNull(ejb.getDatasource());
      assertNull(ejb.getDatasourceMapping());
      assertNull(ejb.getEagerLoadGroup());
      assertNull(ejb.getEntityCommand());
      assertNull(ejb.getLazyLoadGroups());
      assertNull(ejb.getLoadGroups());
      assertNull(ejb.getOptimisticLocking());
      assertNull(ejb.getPostTableCreate());
      assertNull(ejb.getReadAhead());
      assertNull(ejb.getTableName());
      assertNull(ejb.getUnknownPk());
      assertEquals(0, ejb.getFetchSize());
      assertEquals(0, ejb.getListCacheMax());
      assertEquals(0, ejb.getReadTimeOut());
      assertFalse(ejb.isCleanReadAheadOnLoad());
      assertFalse(ejb.isCreateTable());
      assertFalse(ejb.isPkConstraint());
      assertFalse(ejb.isReadOnly());
      assertFalse(ejb.isRemoveTable());
      assertFalse(ejb.isRowLocking());
      JBossCMPFieldsMetaData cmpFields = ejb.getCmpFields();
      assertNotNull(cmpFields);
      assertEquals(2, cmpFields.size());
      JBossCMPFieldMetaData cmpField = cmpFields.get("entity1CmpField1");
      assertNullField("entity1CmpField1", cmpField);
      cmpField = cmpFields.get("entity1CmpField2");
      assertNullField("entity1CmpField2", cmpField);
      
      //    TODO queries should not be null!!! 
      assertNull(ejb.getQueries());

      assertNotNull(ejb.getOverridenMetaData());
      ejbJar.assertFullEntity("entity1", ejb.getOverridenMetaData());
      
      // both not null
      ejb = jbossCMPMetaData.getMergedEnterpriseBean("entity2EjbName");      
      assertNotNull(ejb.getOverridenMetaData());
      ejbJar.assertFullEntity("entity2", ejb.getOverridenMetaData());
      assertEquals("entity2EjbName", ejb.getEjbName());
      assertEquals("java:/DefaultDS", ejb.getDatasource());
      assertEquals("Hypersonic SQL", ejb.getDatasourceMapping());
      assertTrue(ejb.isCreateTable());
      assertTrue(ejb.isRemoveTable());
      assertEquals("entity2Table", ejb.getTableName());
      assertTrue(ejb.isCreateTable());
      assertTrue(ejb.isRemoveTable());

      cmpFields = ejb.getCmpFields();
      assertNotNull(cmpFields);
      assertEquals(2, cmpFields.size());

      cmpField = cmpFields.get("entity2CmpField1");
      assertNullField("entity2CmpField1", cmpField);
      cmpField = cmpFields.get("entity2CmpField2");
      assertNotNull(cmpField);
      assertEquals("entity2CmpField2", cmpField.getFieldName());
      assertEquals("name_column", cmpField.getColumnName());
      assertEquals(Types.VARCHAR, cmpField.getJdbcType());
      assertNull(cmpField.getProperties());
      assertEquals(1111, cmpField.getReadTimeOut());
      assertEquals("VARCHAR(111)", cmpField.getSqlType());
      assertEquals("entity2.StateFactory", cmpField.getStateFactory());
      assertTrue(cmpField.isAutoIncrement());
      assertTrue(cmpField.isCheckDirtyAfterGet());
      assertTrue(cmpField.isDbindex());
      assertTrue(cmpField.isNotNull());
      assertTrue(cmpField.isReadOnly());
      assertNotNull(cmpField.getOverridenMetaData());
      assertEquals("entity2CmpField2", cmpField.getOverridenMetaData().getFieldName());

      // TODO there should be 2 queries!!!
      List<JBossCMPQueryMetaData> queries = ejb.getQueries();
      assertNotNull(queries);
      assertEquals(1, queries.size());

      assertNull(ejb.getAudit());
      assertNull(ejb.getEagerLoadGroup());
      assertNull(ejb.getEntityCommand());
      assertNull(ejb.getLazyLoadGroups());
      assertNull(ejb.getLoadGroups());
      assertNull(ejb.getOptimisticLocking());
      assertNull(ejb.getPostTableCreate());
      assertNull(ejb.getReadAhead());
      assertNull(ejb.getUnknownPk());
      assertEquals(0, ejb.getFetchSize());
      assertEquals(0, ejb.getListCacheMax());
      assertEquals(0, ejb.getReadTimeOut());
      assertFalse(ejb.isCleanReadAheadOnLoad());
      assertFalse(ejb.isPkConstraint());
      assertFalse(ejb.isReadOnly());
      assertFalse(ejb.isRowLocking());
      
      // TODO relationships
   }

   private void assertNullField(String fieldName, JBossCMPFieldMetaData cmpField)
   {
      assertNotNull(cmpField);
      assertEquals(fieldName, cmpField.getFieldName());
      assertNull(cmpField.getColumnName());
      assertEquals(0, cmpField.getJdbcType());
      assertNull(cmpField.getProperties());
      assertEquals(0, cmpField.getReadTimeOut());
      assertNull(cmpField.getSqlType());
      assertNull(cmpField.getStateFactory());
      assertFalse(cmpField.isAutoIncrement());
      assertFalse(cmpField.isCheckDirtyAfterGet());
      assertFalse(cmpField.isDbindex());
      assertFalse(cmpField.isNotNull());
      assertFalse(cmpField.isReadOnly());
      assertNotNull(cmpField.getOverridenMetaData());
      assertEquals(fieldName, cmpField.getOverridenMetaData().getFieldName());
   }

   private void assertNullEntity(String entity, JBossCMPEntityBeanMetaData ejb)
   {
      assertNotNull(ejb);
      assertEquals(entity + "EjbName", ejb.getEjbName());
      assertNull(ejb.getAudit());
      assertNull(ejb.getCmpFields());
      assertNull(ejb.getDatasource());
      assertNull(ejb.getDatasourceMapping());
      assertNull(ejb.getEagerLoadGroup());
      assertNull(ejb.getEntityCommand());
      assertNull(ejb.getLazyLoadGroups());
      assertNull(ejb.getLoadGroups());
      assertNull(ejb.getOptimisticLocking());
      assertNull(ejb.getPostTableCreate());
      assertNull(ejb.getQueries());
      assertNull(ejb.getReadAhead());
      assertNull(ejb.getTableName());
      assertNull(ejb.getUnknownPk());
      assertEquals(0, ejb.getFetchSize());
      assertEquals(0, ejb.getListCacheMax());
      assertEquals(0, ejb.getReadTimeOut());
      assertFalse(ejb.isCleanReadAheadOnLoad());
      assertFalse(ejb.isCreateTable());
      assertFalse(ejb.isPkConstraint());
      assertFalse(ejb.isReadOnly());
      assertFalse(ejb.isRemoveTable());
      assertFalse(ejb.isRowLocking());
   }
}
