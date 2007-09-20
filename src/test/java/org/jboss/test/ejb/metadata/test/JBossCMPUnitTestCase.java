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

import org.jboss.ejb.metadata.jboss.cmp.JBossCMPAuditMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPFieldMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPDeclaredSqlMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPDefaultsMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPDependentValueClassMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPEjbRelationMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPEjbRelationshipRoleMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPEnterpriseBeansMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPEntityBeanMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPEntityCommandMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPFieldsMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPKeyFieldMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPLoadGroupMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPOptimisticLockingMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPPreferredRelationMapping;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPPropertyMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPQueryMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPQueryMethodMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPReadAheadMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPRelationTableMappingMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPRelationshipsMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPTypeMappingMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPUnknownPkMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPUserTypeMappingMetaData;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPAuditMetaData.Field;
import org.jboss.ejb.metadata.jboss.cmp.JBossCMPDeclaredSqlMetaData.Select;
import org.jboss.test.ejb.AbstractEJBEverythingTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;


/**
 * A JBossCMPDefaultsUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPUnitTestCase extends AbstractEJBEverythingTest
{
   public static Test suite()
   {
      return suite(JBossCMPUnitTestCase.class);
   }
   
   public static SchemaBindingResolver initResolver()
   {
      return schemaResolverForClass(JBossCMPMetaData.class);
   }

   public JBossCMPUnitTestCase(String name)
   {
      super(name);
   }

   protected JBossCMPMetaData unmarshal() throws Exception
   {
      return unmarshal(JBossCMPMetaData.class);
   }
   
   public void testDefaults() throws Exception
   {
      //enableTrace("org.jboss.xb");
      //enableTrace("org.jboss.xb.builder");
      
      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      
      JBossCMPDefaultsMetaData defaults = jbossMetaData.getDefaults();
      assertNotNull(defaults);
      assertEquals("java:/DefaultDS", defaults.getDatasource());
      assertEquals("Hypersonic SQL",  defaults.getDatasourceMapping());
      assertTrue(defaults.isCreateTable());
      assertTrue(defaults.isRemoveTable());
      assertTrue(defaults.isReadOnly());
      assertEquals(300000, defaults.getReadTimeOut());
      assertTrue(defaults.isRowLocking());
      assertTrue(defaults.isPkConstraint());
      assertTrue(defaults.isFkConstraint());
      assertEquals(JBossCMPPreferredRelationMapping.ForeignKey, defaults.getPreferredRelationMapping());
      
      JBossCMPReadAheadMetaData readAhead = defaults.getReadAhead();
      assertNotNull(readAhead);
      assertEquals("on-load", readAhead.getStrategy());
      assertEquals(1000, readAhead.getPageSize());
      assertEquals("*", readAhead.getEagerLoadGroup());
      
      assertEquals(1000, defaults.getListCacheMax());
      assertTrue(defaults.isCleanReadAheadOnLoad());
      
      JBossCMPUnknownPkMetaData unknownPk = defaults.getUnknownPk();
      assertNotNull(unknownPk);
      assertEquals("UUIDKeyGeneratorFactory", unknownPk.getKeyGeneratorFactory());
      assertEquals("java.lang.String", unknownPk.getUnknownPkClass());
      assertEquals(Types.VARCHAR, unknownPk.getJdbcType());
      assertEquals("VARCHAR(32)", unknownPk.getSqlType());
      assertNull(unknownPk.getColumnName());
      assertNull(unknownPk.getFieldName());
      assertEquals(0, unknownPk.getReadOnlyTimeOut());
      
      JBossCMPEntityCommandMetaData entityCommand = defaults.getEntityCommand();
      assertNotNull(entityCommand);
      assertEquals("default", entityCommand.getName());
      assertNull(entityCommand.getClassName());
      assertTrue(entityCommand.isEmpty());
      
      assertEquals("org.jboss.ejb.plugins.cmp.jdbc.JDBCEJBQLCompiler", defaults.getQlCompiler());
      assertTrue(defaults.isThrowRuntimeExceptions());
   }
   
   public void testTypeMappings() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      assertNull(jbossMetaData.getDefaults());
      
      List<JBossCMPTypeMappingMetaData> typeMappings = jbossMetaData.getTypeMappings();
      assertNotNull(typeMappings);
      assertEquals(2, typeMappings.size());
      
      JBossCMPTypeMappingMetaData typeMapping = typeMappings.get(0);
      assertNotNull(typeMapping);
      assertEquals("Hypersonic SQL", typeMapping.getName());
      assertEquals("", typeMapping.getRowLockingTemplate());
      assertEquals("CONSTRAINT ?1 PRIMARY KEY (?2)", typeMapping.getPkConstraintTemplate());
      assertEquals("ALTER TABLE ?1 ADD CONSTRAINT ?2 FOREIGN KEY (?3) REFERENCES ?4 (?5)", typeMapping.getFkConstraintTemplate());
      assertEquals("?1 IDENTITY", typeMapping.getAutoIncrementTemplate());
      assertEquals("ALTER TABLE ?1 ADD COLUMN ?2 ?3", typeMapping.getAddColumnTemplate());
      assertEquals("ALTER TABLE ?1 DROP COLUMN ?2", typeMapping.getDropColumnTemplate());
      assertEquals("t", typeMapping.getAliasHeaderPrefix());
      assertEquals("_", typeMapping.getAliasHeaderSuffix());
      assertEquals(32, typeMapping.getAliasMaxLength());
      assertTrue(typeMapping.isSubquerySupported());
      assertEquals("(1=1)", typeMapping.getTrueMapping());
      assertEquals("(1=0)", typeMapping.getFalseMapping());
      
      assertNotNull(typeMapping.getFunctionMappings());
      assertEquals(9, typeMapping.getFunctionMappings().size());
      assertEquals("(?1 || ?2)", typeMapping.getFunctionSql("concat"));
      assertEquals("SUBSTRING(?1, ?2, ?3)", typeMapping.getFunctionSql("substring"));
      assertEquals("lcase(?1)", typeMapping.getFunctionSql("lcase"));
      assertEquals("LENGTH(?1)", typeMapping.getFunctionSql("length"));
      assertEquals("LOCATE(?1, ?2, ?3)", typeMapping.getFunctionSql("locate"));
      assertEquals("ABS(?1)", typeMapping.getFunctionSql("abs"));
      assertEquals("SQRT(?1)", typeMapping.getFunctionSql("sqrt"));
      assertEquals("ucase(?1)", typeMapping.getFunctionSql("ucase"));
      assertEquals("count(?1)", typeMapping.getFunctionSql("count"));
      
      assertNotNull(typeMapping.getMappings());
      assertEquals(15, typeMapping.getMappings().size());
      assertEquals(Types.SMALLINT, typeMapping.getJdbcType(Byte.class.getName()));
      assertEquals("SMALLINT", typeMapping.getSqlType(Byte.class.getName()));
      assertEquals(Types.TIMESTAMP, typeMapping.getJdbcType(java.util.Date.class.getName()));
      assertEquals("TIMESTAMP", typeMapping.getSqlType(java.util.Date.class.getName()));
      assertEquals(Types.BIT, typeMapping.getJdbcType(Boolean.class.getName()));
      assertEquals("BIT", typeMapping.getSqlType(Boolean.class.getName()));
      assertEquals(Types.INTEGER, typeMapping.getJdbcType(Integer.class.getName()));
      assertEquals("INTEGER", typeMapping.getSqlType(Integer.class.getName()));
      assertEquals(Types.VARBINARY, typeMapping.getJdbcType(Object.class.getName()));
      assertEquals("VARBINARY", typeMapping.getSqlType(Object.class.getName()));
      assertEquals(Types.SMALLINT, typeMapping.getJdbcType(Short.class.getName()));
      assertEquals("SMALLINT", typeMapping.getSqlType(Short.class.getName()));
      assertEquals(Types.CHAR, typeMapping.getJdbcType(Character.class.getName()));
      assertEquals("CHAR", typeMapping.getSqlType(Character.class.getName()));
      assertEquals(Types.VARCHAR, typeMapping.getJdbcType(String.class.getName()));
      assertEquals("VARCHAR(256)", typeMapping.getSqlType(String.class.getName()));
      assertEquals(Types.DATE, typeMapping.getJdbcType(java.sql.Date.class.getName()));
      assertEquals("DATE", typeMapping.getSqlType(java.sql.Date.class.getName()));
      assertEquals(Types.TIME, typeMapping.getJdbcType(java.sql.Time.class.getName()));
      assertEquals("TIME", typeMapping.getSqlType(java.sql.Time.class.getName()));
      assertEquals(Types.TIMESTAMP, typeMapping.getJdbcType(java.sql.Timestamp.class.getName()));
      assertEquals("TIMESTAMP", typeMapping.getSqlType(java.sql.Timestamp.class.getName()));
      assertEquals(Types.REAL, typeMapping.getJdbcType(Float.class.getName()));
      assertEquals("REAL", typeMapping.getSqlType(Float.class.getName()));
      assertEquals(Types.BIGINT, typeMapping.getJdbcType(Long.class.getName()));
      assertEquals("BIGINT", typeMapping.getSqlType(Long.class.getName()));
      assertEquals(Types.DOUBLE, typeMapping.getJdbcType(Double.class.getName()));
      assertEquals("DOUBLE", typeMapping.getSqlType(Double.class.getName()));
      assertEquals(Types.DECIMAL, typeMapping.getJdbcType(java.math.BigDecimal.class.getName()));
      assertEquals("DECIMAL", typeMapping.getSqlType(java.math.BigDecimal.class.getName()));

      typeMapping = typeMappings.get(1);
      assertNotNull(typeMapping);
      assertEquals("Oracle9i", typeMapping.getName());
      assertEquals("SELECT ?1 FROM ?2 WHERE ?3 ORDER BY ?4 FOR UPDATE", typeMapping.getRowLockingTemplate());
      assertEquals("CONSTRAINT ?1 PRIMARY KEY (?2)", typeMapping.getPkConstraintTemplate());
      assertEquals("ALTER TABLE ?1 ADD CONSTRAINT ?2 FOREIGN KEY (?3) REFERENCES ?4 (?5)", typeMapping.getFkConstraintTemplate());
      assertNull(typeMapping.getAutoIncrementTemplate());
      assertNull(typeMapping.getAddColumnTemplate());
      assertNull(typeMapping.getDropColumnTemplate());
      assertEquals("t", typeMapping.getAliasHeaderPrefix());
      assertEquals("_", typeMapping.getAliasHeaderSuffix());
      assertEquals(30, typeMapping.getAliasMaxLength());
      assertTrue(typeMapping.isSubquerySupported());
      assertEquals("1", typeMapping.getTrueMapping());
      assertEquals("0", typeMapping.getFalseMapping());
      
      assertNotNull(typeMapping.getFunctionMappings());
      assertEquals(9, typeMapping.getFunctionMappings().size());
      assertEquals("(?1 || ?2)", typeMapping.getFunctionSql("concat"));
      assertEquals("substr(?1, ?2, ?3)", typeMapping.getFunctionSql("substring"));
      assertEquals("lower(?1)", typeMapping.getFunctionSql("lcase"));
      assertEquals("length(?1)", typeMapping.getFunctionSql("length"));
      assertEquals("instr(?2, ?1, ?3)", typeMapping.getFunctionSql("locate"));
      assertEquals("abs(?1)", typeMapping.getFunctionSql("abs"));
      assertEquals("sqrt(?1)", typeMapping.getFunctionSql("sqrt"));
      assertEquals("upper(?1)", typeMapping.getFunctionSql("ucase"));
      assertEquals("count(?1)", typeMapping.getFunctionSql("count"));
      
      assertNotNull(typeMapping.getMappings());
      assertEquals(15, typeMapping.getMappings().size());
      assertEquals(Types.BIT, typeMapping.getJdbcType(Boolean.class.getName()));
      assertEquals("NUMBER(1)", typeMapping.getSqlType(Boolean.class.getName()));
      assertEquals(Types.SMALLINT, typeMapping.getJdbcType(Byte.class.getName()));
      assertEquals("NUMBER(3)", typeMapping.getSqlType(Byte.class.getName()));
      assertEquals(Types.NUMERIC, typeMapping.getJdbcType(Short.class.getName()));
      assertEquals("NUMBER(5)", typeMapping.getSqlType(Short.class.getName()));
      assertEquals(Types.INTEGER, typeMapping.getJdbcType(Integer.class.getName()));
      assertEquals("NUMBER(10)", typeMapping.getSqlType(Integer.class.getName()));
      assertEquals(Types.BIGINT, typeMapping.getJdbcType(Long.class.getName()));
      assertEquals("NUMBER(19)", typeMapping.getSqlType(Long.class.getName()));
      assertEquals(Types.REAL, typeMapping.getJdbcType(Float.class.getName()));
      assertEquals("NUMBER(38,7)", typeMapping.getSqlType(Float.class.getName()));
      assertEquals(Types.DECIMAL, typeMapping.getJdbcType(java.math.BigDecimal.class.getName()));
      assertEquals("NUMBER(38,15)", typeMapping.getSqlType(java.math.BigDecimal.class.getName()));
      assertEquals(Types.DOUBLE, typeMapping.getJdbcType(Double.class.getName()));
      assertEquals("NUMBER(38,15)", typeMapping.getSqlType(Double.class.getName()));
      assertEquals(Types.VARCHAR, typeMapping.getJdbcType(Character.class.getName()));
      assertEquals("CHAR", typeMapping.getSqlType(Character.class.getName()));
      assertEquals(Types.VARCHAR, typeMapping.getJdbcType(String.class.getName()));
      assertEquals("VARCHAR2(255)", typeMapping.getSqlType(String.class.getName()));
      assertEquals(Types.TIMESTAMP, typeMapping.getJdbcType(java.util.Date.class.getName()));
      assertEquals("TIMESTAMP(3)", typeMapping.getSqlType(java.util.Date.class.getName()));
      assertEquals(Types.DATE, typeMapping.getJdbcType(java.sql.Date.class.getName()));
      assertEquals("DATE", typeMapping.getSqlType(java.sql.Date.class.getName()));
      assertEquals(Types.TIME, typeMapping.getJdbcType(java.sql.Time.class.getName()));
      assertEquals("DATE", typeMapping.getSqlType(java.sql.Time.class.getName()));
      assertEquals(Types.TIMESTAMP, typeMapping.getJdbcType(java.sql.Timestamp.class.getName()));
      assertEquals("TIMESTAMP(9)", typeMapping.getSqlType(java.sql.Timestamp.class.getName()));
      assertEquals(Types.BLOB, typeMapping.getJdbcType(Object.class.getName()));
      assertEquals("BLOB", typeMapping.getSqlType(Object.class.getName()));
   }

   public void testEntityCommands() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      assertNull(jbossMetaData.getDefaults());
      assertNull(jbossMetaData.getTypeMappings());
      
      List<JBossCMPEntityCommandMetaData> entityCommands = jbossMetaData.getEntityCommands();
      assertNotNull(entityCommands);
      assertEquals(3, entityCommands.size());
      
      JBossCMPEntityCommandMetaData cmd = entityCommands.get(0);
      assertNotNull(cmd);
      assertEquals("default", cmd.getName());
      assertEquals("org.jboss.ejb.plugins.cmp.jdbc.JDBCCreateEntityCommand", cmd.getClassName());
      assertFalse(cmd.hasAttributes());

      cmd = entityCommands.get(1);
      assertNotNull(cmd);
      assertEquals("no-select-before-insert", cmd.getName());
      assertEquals("org.jboss.ejb.plugins.cmp.jdbc.JDBCCreateEntityCommand", cmd.getClassName());
      assertTrue(cmd.hasAttributes());
      assertEquals("jboss.jdbc:service=SQLExceptionProcessor", cmd.getAttribute("SQLExceptionProcessor"));

      cmd = entityCommands.get(2);
      assertNotNull(cmd);
      assertEquals("another", cmd.getName());
      assertEquals("JDBCAnotherCreateCommand", cmd.getClassName());
      assertTrue(cmd.hasAttributes());
      assertEquals("val1", cmd.getAttribute("attr1"));
      assertEquals("val2", cmd.getAttribute("attr2"));
      assertNull(cmd.getAttribute("attr3"));
   }

   public void testReservedWords() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      assertNull(jbossMetaData.getDefaults());
      assertNull(jbossMetaData.getTypeMappings());
      assertNull(jbossMetaData.getEntityCommands());
      
      List<String> reservedWords = jbossMetaData.getReservedWords();
      assertNotNull(reservedWords);
      assertEquals(3, reservedWords.size());
      for(int i = 0; i < reservedWords.size(); ++i)
      {
         assertEquals("word" + (i + 1),  reservedWords.get(i));
      }
   }

   public void testEnterpriseBeans() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      assertNull(jbossMetaData.getDefaults());
      assertNull(jbossMetaData.getTypeMappings());
      assertNull(jbossMetaData.getEntityCommands());
      assertNull(jbossMetaData.getReservedWords());
      
      JBossCMPEnterpriseBeansMetaData enterpriseBeans = jbossMetaData.getEnterpriseBeans();
      assertNotNull(enterpriseBeans);
      assertEquals(2, enterpriseBeans.size());
      
      JBossCMPEntityBeanMetaData entity = enterpriseBeans.get("entity1EjbName");
      assertNotNull(entity);
      assertEquals("entity1EjbName", entity.getEjbName());
      assertNull(entity.getAudit());
      assertNull(entity.getCmpFields());
      assertNull(entity.getDatasource());
      assertNull(entity.getDatasourceMapping());
      assertNull(entity.getEagerLoadGroup());
      assertNull(entity.getEntityCommand());
      assertEquals(0, entity.getFetchSize());
      assertNull(entity.getLazyLoadGroups());
      assertEquals(0, entity.getListCacheMax());
      assertNull(entity.getLoadGroups());
      assertNull(entity.getOptimisticLocking());
      assertNull(entity.getPostTableCreate());
      assertNull(entity.getQueries());
      assertNull(entity.getReadAhead());
      assertEquals(0, entity.getReadTimeOut());
      assertNull(entity.getTableName());
      assertNull(entity.getUnknownPk());
      assertFalse(entity.isCleanReadAheadOnLoad());
      assertFalse(entity.isCreateTable());
      assertFalse(entity.isPkConstraint());
      assertFalse(entity.isReadOnly());
      assertFalse(entity.isRemoveTable());
      assertFalse(entity.isRowLocking());

      entity = enterpriseBeans.get("entity2EjbName");
      assertNotNull(entity);
      assertEquals("entity2EjbName", entity.getEjbName());
      assertEquals("java:/DefaultDS", entity.getDatasource());
      assertEquals("Hypersonic SQL", entity.getDatasourceMapping());
      assertTrue(entity.isCreateTable());
      assertTrue(entity.isRemoveTable());
      assertTrue(entity.isReadOnly());
      assertEquals(1000, entity.getReadTimeOut());
      assertTrue(entity.isRowLocking());
      assertTrue(entity.isPkConstraint());
      
      JBossCMPReadAheadMetaData readAhead = entity.getReadAhead();
      assertNotNull(readAhead);
      assertEquals("none", readAhead.getStrategy());
      assertEquals(11, readAhead.getPageSize());
      assertEquals("eager-group", readAhead.getEagerLoadGroup());
      
      assertEquals(17, entity.getListCacheMax());
      assertTrue(entity.isCleanReadAheadOnLoad());
      assertEquals(21, entity.getFetchSize());
      assertEquals("entity2Table", entity.getTableName());
      
      //
      // cmp-fields
      //
      JBossCMPFieldsMetaData cmpFields = entity.getCmpFields();
      assertNotNull(cmpFields);
      assertEquals(3, cmpFields.size());
      
      JBossCMPFieldMetaData field = cmpFields.get("id");
      assertNotNull(field);
      assertEquals("id", field.getFieldName());
      assertFalse(field.isReadOnly());
      assertEquals(0, field.getReadTimeOut());
      assertNull(field.getColumnName());
      assertFalse(field.isNotNull());
      assertEquals(0, field.getJdbcType());
      assertNull(field.getSqlType());
      assertNull(field.getProperties());
      assertFalse(field.isAutoIncrement());
      assertFalse(field.isDbindex());
      assertFalse(field.isCheckDirtyAfterGet());
      assertNull(field.getStateFactory());

      field = cmpFields.get("name");
      assertNotNull(field);
      assertEquals("name", field.getFieldName());
      assertTrue(field.isReadOnly());
      assertEquals(1111, field.getReadTimeOut());
      assertEquals("name_column", field.getColumnName());
      assertTrue(field.isNotNull());
      assertEquals(Types.VARCHAR, field.getJdbcType());
      assertEquals("VARCHAR(111)", field.getSqlType());
      assertNull(field.getProperties());
      assertTrue(field.isAutoIncrement());
      assertTrue(field.isDbindex());
      assertTrue(field.isCheckDirtyAfterGet());
      assertEquals("entity2.StateFactory", field.getStateFactory());

      field = cmpFields.get("address");
      assertNotNull(field);
      assertEquals("address", field.getFieldName());
      assertFalse(field.isReadOnly());
      assertEquals(0, field.getReadTimeOut());
      assertNull(field.getColumnName());
      assertFalse(field.isNotNull());
      assertEquals(0, field.getJdbcType());
      assertNull(field.getSqlType());
      assertFalse(field.isAutoIncrement());
      assertFalse(field.isDbindex());
      assertFalse(field.isCheckDirtyAfterGet());
      assertNull(field.getStateFactory());
      
      List<JBossCMPPropertyMetaData> props = field.getProperties();
      assertNotNull(props);
      assertEquals(2, props.size());
      JBossCMPPropertyMetaData fieldProp = props.get(0);
      assertNotNull(fieldProp);
      assertEquals("street", fieldProp.getPropertyName());
      assertNull(fieldProp.getColumnName());
      assertFalse(fieldProp.isNotNull());
      assertEquals(0, fieldProp.getJdbcType());
      assertNull(fieldProp.getSqlType());

      fieldProp = props.get(1);
      assertNotNull(fieldProp);
      assertEquals("city", fieldProp.getPropertyName());
      assertEquals("city_column", fieldProp.getColumnName());
      assertTrue(fieldProp.isNotNull());
      assertEquals(Types.INTEGER, fieldProp.getJdbcType());
      assertEquals("NUMERIC(222)", fieldProp.getSqlType());
      
      //
      // load-groups
      //
      List<JBossCMPLoadGroupMetaData> loadGroups = entity.getLoadGroups();
      assertNotNull(loadGroups);
      assertEquals(2, loadGroups.size());
      JBossCMPLoadGroupMetaData loadGroup = loadGroups.get(0);
      assertNotNull(loadGroup);
      assertNull(loadGroup.getDescription());
      assertEquals("basic", loadGroup.getLoadGroupName());
      List<String> fieldNames = loadGroup.getFieldNames();
      assertNotNull(fieldNames);
      assertEquals(1, fieldNames.size());
      assertEquals("name", fieldNames.get(0));
      
      loadGroup = loadGroups.get(1);
      assertNotNull(loadGroup);
      assertEquals("jboss group", loadGroup.getDescription());
      assertEquals("eager-group", loadGroup.getLoadGroupName());
      fieldNames = loadGroup.getFieldNames();
      assertNotNull(fieldNames);
      assertEquals(2, fieldNames.size());
      assertEquals("name", fieldNames.get(0));
      assertEquals("address", fieldNames.get(1));
      
      assertEquals("basic", entity.getEagerLoadGroup());
      
      List<String> lazyLoadGroups = entity.getLazyLoadGroups();
      assertNotNull(lazyLoadGroups);
      assertEquals(2, lazyLoadGroups.size());
      assertEquals("group1", lazyLoadGroups.get(0));
      assertEquals("group2", lazyLoadGroups.get(1));
      
      //
      // query
      //
      List<JBossCMPQueryMetaData> queries = entity.getQueries();
      assertNotNull(queries);
      assertEquals(4, queries.size());
      JBossCMPQueryMetaData query = queries.get(0);
      assertNotNull(query);
      assertEquals("finder", query.getDescription());
      JBossCMPQueryMethodMetaData queryMethod = query.getQueryMethod();
      assertNotNull(queryMethod);
      assertEquals("findByWhatever", queryMethod.getMethodName());
      List<String> methodParams = queryMethod.getMethodParams();
      assertNotNull(methodParams);
      assertEquals(2, methodParams.size());
      assertEquals("java.lang.String", methodParams.get(0));
      assertEquals("java.lang.Integer", methodParams.get(1));
      assertEquals("select object(o) from entity2", query.getJBossQl());
      assertNull(query.getDeclaredSql());
      assertFalse(query.isDynamicQl());
      readAhead = query.getReadAhead();
      assertNotNull(readAhead);
      assertEquals("on-load", readAhead.getStrategy());
      assertEquals(22, readAhead.getPageSize());
      assertEquals("eager-group", readAhead.getEagerLoadGroup());
      assertEquals("entity.QLCompiler", query.getQlCompiler());
      assertTrue(query.isLazyResultsetLoading());

      query = queries.get(1);
      assertNotNull(query);
      assertNull(query.getDescription());
      queryMethod = query.getQueryMethod();
      assertNotNull(queryMethod);
      assertEquals("findByAllMeans", queryMethod.getMethodName());
      assertNull(queryMethod.getMethodParams());
      assertTrue(query.isDynamicQl());
      assertNull(query.getJBossQl());
      assertNull(query.getDeclaredSql());
      assertNull(query.getReadAhead());
      assertNull(query.getQlCompiler());
      assertFalse(query.isLazyResultsetLoading());

      query = queries.get(2);
      assertNotNull(query);
      assertNull(query.getDescription());
      queryMethod = query.getQueryMethod();
      assertNotNull(queryMethod);
      assertEquals("findByDeclaredSql1", queryMethod.getMethodName());
      assertNull(queryMethod.getMethodParams());
      assertNull(query.getJBossQl());
      assertFalse(query.isDynamicQl());
      assertNull(query.getReadAhead());
      assertNull(query.getQlCompiler());
      assertFalse(query.isLazyResultsetLoading());
      JBossCMPDeclaredSqlMetaData declaredSql = query.getDeclaredSql();
      assertNotNull(declaredSql);
      Select select = declaredSql.getSelect();
      assertNotNull(select);
      assertTrue(select.isDistinct());
      assertEquals("entity2EjbName", select.getEjbName());
      assertEquals("name", select.getFieldName());
      assertEquals("entity2Alias", select.getAlias());
      assertEquals(", address", select.getAdditionalColumns());
      assertEquals(",  entity1 entity1Alias", declaredSql.getFrom());
      assertEquals("entity2Alias.city={0} OR entity2Alias.street={1}", declaredSql.getWhere());
      assertEquals("entity2Alias.name", declaredSql.getOrder());
      assertEquals("limit 11 offset 111", declaredSql.getOther());

      query = queries.get(3);
      assertNotNull(query);
      assertNull(query.getDescription());
      queryMethod = query.getQueryMethod();
      assertNotNull(queryMethod);
      assertEquals("findByDeclaredSql2", queryMethod.getMethodName());
      assertNull(queryMethod.getMethodParams());
      assertNull(query.getJBossQl());
      assertFalse(query.isDynamicQl());
      assertNull(query.getReadAhead());
      assertNull(query.getQlCompiler());
      assertFalse(query.isLazyResultsetLoading());
      declaredSql = query.getDeclaredSql();
      assertNotNull(declaredSql);
      select = declaredSql.getSelect();
      assertNotNull(select);
      assertFalse(select.isDistinct());
      assertEquals("entity2EjbName", select.getEjbName());
      assertNull(select.getFieldName());
      assertNull(select.getAlias());
      assertNull(select.getAdditionalColumns());
      assertNull(declaredSql.getFrom());
      assertEquals("entity2Alias.name='kloop'", declaredSql.getWhere());
      assertNull(declaredSql.getOrder());
      assertNull(declaredSql.getOther());
      
      //
      // unknown-pk
      //
      JBossCMPUnknownPkMetaData unknownPk = entity.getUnknownPk();
      assertNotNull(unknownPk);
      assertEquals("entity2.KeyFactory", unknownPk.getKeyGeneratorFactory());
      assertEquals("java.lang.Long", unknownPk.getUnknownPkClass());
      assertEquals("hiddenId", unknownPk.getFieldName());
      assertEquals("hidden_id", unknownPk.getColumnName());
      assertEquals(Types.INTEGER, unknownPk.getJdbcType());
      assertEquals("NUMERIC(1111)", unknownPk.getSqlType());
      assertTrue(unknownPk.isAutoIncrement());
      
      //
      // entity-command
      //
      JBossCMPEntityCommandMetaData entityCommand = entity.getEntityCommand();
      assertNotNull(entityCommand);
      assertEquals("entity2Command", entityCommand.getName());
      assertNull(entityCommand.getClassName());
      assertFalse(entityCommand.hasAttributes());
      
      //
      // optimistic-locking
      //
      JBossCMPOptimisticLockingMetaData optimisticLocking = entity.getOptimisticLocking();
      assertNotNull(optimisticLocking);
      assertNull(optimisticLocking.getGroupName());
      assertFalse(optimisticLocking.isModifiedStrategy());
      assertFalse(optimisticLocking.isReadStrategy());
      assertTrue(optimisticLocking.isVersionColumn());
      assertFalse(optimisticLocking.isTimestampColumn());
      assertNull(optimisticLocking.getKeyGeneratorFactory());
      assertNull(optimisticLocking.getFieldType());
      assertEquals("version", optimisticLocking.getFieldName());
      assertEquals("vrsn", optimisticLocking.getColumnName());
      assertEquals(Types.INTEGER, optimisticLocking.getJdbcType());
      assertEquals("NUMERIC", optimisticLocking.getSqlType());
      
      //
      // audit
      //
      JBossCMPAuditMetaData audit = entity.getAudit();
      assertNotNull(audit);
      Field auditField = audit.getCreatedBy();
      assertNotNull(auditField);
      assertEquals("createdBy", auditField.getFieldName());
      assertNull(auditField.getColumnName());
      assertEquals(0, auditField.getJdbcType());
      assertNull(auditField.getSqlType());
      auditField = audit.getCreatedTime();
      assertNotNull(auditField);
      assertNull(auditField.getFieldName());
      assertEquals("created_time", auditField.getColumnName());
      assertEquals(0, auditField.getJdbcType());
      assertNull(auditField.getSqlType());
      auditField = audit.getUpdatedBy();
      assertNotNull(auditField);
      assertEquals("updatedBy", auditField.getFieldName());
      assertEquals("updated_by", auditField.getColumnName());
      assertEquals(0, auditField.getJdbcType());
      assertNull(auditField.getSqlType());
      auditField = audit.getUpdatedTime();
      assertNotNull(auditField);
      assertEquals("updatedTime", auditField.getFieldName());
      assertEquals("updated_time", auditField.getColumnName());
      assertEquals(Types.TIMESTAMP, auditField.getJdbcType());
      assertEquals("TIMESTAMP(111)", auditField.getSqlType());
   }
   
   public void testOptimisticLocking() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      JBossCMPEnterpriseBeansMetaData enterpriseBeans = jbossMetaData.getEnterpriseBeans();
      assertNotNull(enterpriseBeans);
      assertEquals(6, enterpriseBeans.size());
      
      JBossCMPEntityBeanMetaData entity = enterpriseBeans.get("entity1EjbName");
      assertNotNull(entity);
      JBossCMPOptimisticLockingMetaData ol = entity.getOptimisticLocking();
      assertNotNull(ol);
      assertEquals("optimistic_group", ol.getGroupName());
      assertFalse(ol.isModifiedStrategy());
      assertFalse(ol.isReadStrategy());
      assertFalse(ol.isVersionColumn());
      assertFalse(ol.isTimestampColumn());
      assertNull(ol.getKeyGeneratorFactory());
      assertNull(ol.getFieldType());
      assertNull(ol.getFieldName());
      assertNull(ol.getColumnName());
      assertEquals(0, ol.getJdbcType());
      assertNull(ol.getSqlType());

      entity = enterpriseBeans.get("entity2EjbName");
      assertNotNull(entity);
      ol = entity.getOptimisticLocking();
      assertNotNull(ol);
      assertNull(ol.getGroupName());
      assertTrue(ol.isModifiedStrategy());
      assertFalse(ol.isReadStrategy());
      assertFalse(ol.isVersionColumn());
      assertFalse(ol.isTimestampColumn());
      assertNull(ol.getKeyGeneratorFactory());
      assertNull(ol.getFieldType());
      assertNull(ol.getFieldName());
      assertNull(ol.getColumnName());
      assertEquals(0, ol.getJdbcType());
      assertNull(ol.getSqlType());

      entity = enterpriseBeans.get("entity3EjbName");
      assertNotNull(entity);
      ol = entity.getOptimisticLocking();
      assertNotNull(ol);
      assertNull(ol.getGroupName());
      assertFalse(ol.isModifiedStrategy());
      assertTrue(ol.isReadStrategy());
      assertFalse(ol.isVersionColumn());
      assertFalse(ol.isTimestampColumn());
      assertNull(ol.getKeyGeneratorFactory());
      assertNull(ol.getFieldType());
      assertNull(ol.getFieldName());
      assertNull(ol.getColumnName());
      assertEquals(0, ol.getJdbcType());
      assertNull(ol.getSqlType());

      entity = enterpriseBeans.get("entity4EjbName");
      assertNotNull(entity);
      ol = entity.getOptimisticLocking();
      assertNotNull(ol);
      assertNull(ol.getGroupName());
      assertFalse(ol.isModifiedStrategy());
      assertFalse(ol.isReadStrategy());
      assertTrue(ol.isVersionColumn());
      assertFalse(ol.isTimestampColumn());
      assertNull(ol.getKeyGeneratorFactory());
      assertNull(ol.getFieldType());
      assertNull(ol.getFieldName());
      assertEquals("last_version", ol.getColumnName());
      assertEquals(Types.INTEGER, ol.getJdbcType());
      assertEquals("INTEGER", ol.getSqlType());

      entity = enterpriseBeans.get("entity5EjbName");
      assertNotNull(entity);
      ol = entity.getOptimisticLocking();
      assertNotNull(ol);
      assertNull(ol.getGroupName());
      assertFalse(ol.isModifiedStrategy());
      assertFalse(ol.isReadStrategy());
      assertFalse(ol.isVersionColumn());
      assertTrue(ol.isTimestampColumn());
      assertNull(ol.getKeyGeneratorFactory());
      assertNull(ol.getFieldType());
      assertNull(ol.getFieldName());
      assertEquals("last_updated", ol.getColumnName());
      assertEquals(0, ol.getJdbcType());
      assertNull(ol.getSqlType());

      entity = enterpriseBeans.get("entity6EjbName");
      assertNotNull(entity);
      ol = entity.getOptimisticLocking();
      assertNotNull(ol);
      assertNull(ol.getGroupName());
      assertFalse(ol.isModifiedStrategy());
      assertFalse(ol.isReadStrategy());
      assertFalse(ol.isVersionColumn());
      assertFalse(ol.isTimestampColumn());
      assertEquals("key.generator.Factory", ol.getKeyGeneratorFactory());
      assertEquals("java.lang.Long", ol.getFieldType());
      assertNull(ol.getFieldName());
      assertNull(ol.getColumnName());
      assertEquals(0, ol.getJdbcType());
      assertNull(ol.getSqlType());
   }

   public void testRelationships() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      JBossCMPRelationshipsMetaData relationships = jbossMetaData.getRelationships();
      assertNotNull(relationships);
      assertEquals(7, relationships.size());
      
      JBossCMPEjbRelationMetaData relation = relationships.get("empty");
      assertNotNull(relation);
      assertEquals("empty", relation.getEjbRelationName());
      assertFalse(relation.isReadOnly());
      assertEquals(0, relation.getReadTimeOut());
      assertFalse(relation.isForeignKeyMapping());
      assertNull(relation.getRelationTableMapping());
      assertNull(relation.getLeftRole());
      assertNull(relation.getRightRole());
      
      relation = relationships.get("read-only");
      assertNotNull(relation);
      assertEquals("read-only", relation.getEjbRelationName());
      assertTrue(relation.isReadOnly());
      assertEquals(111, relation.getReadTimeOut());
      assertFalse(relation.isForeignKeyMapping());
      assertNull(relation.getRelationTableMapping());
      assertNull(relation.getLeftRole());
      assertNull(relation.getRightRole());

      relation = relationships.get("foreign-key-mapping");
      assertNotNull(relation);
      assertEquals("foreign-key-mapping", relation.getEjbRelationName());
      assertFalse(relation.isReadOnly());
      assertEquals(0, relation.getReadTimeOut());
      assertTrue(relation.isForeignKeyMapping());
      assertNull(relation.getRelationTableMapping());
      assertNull(relation.getLeftRole());
      assertNull(relation.getRightRole());
      
      relation = relationships.get("relation-table-mapping");
      assertNotNull(relation);
      assertEquals("relation-table-mapping", relation.getEjbRelationName());
      assertFalse(relation.isReadOnly());
      assertEquals(0, relation.getReadTimeOut());
      assertFalse(relation.isForeignKeyMapping());
      assertNull(relation.getLeftRole());
      assertNull(relation.getRightRole());
      JBossCMPRelationTableMappingMetaData rtm = relation.getRelationTableMapping();
      assertNotNull(rtm);
      assertEquals("relation_table", rtm.getTableName());
      assertEquals("java:/DefaultDS", rtm.getDatasource());
      assertEquals("Hypersonic SQL", rtm.getDatasourceMapping());
      assertTrue(rtm.isCreateTable());
      assertTrue(rtm.isRemoveTable());
      List<String> postTableCreate = rtm.getPostTableCreate();
      assertNotNull(postTableCreate);
      assertEquals(3, postTableCreate.size());
      assertEquals("sql1", postTableCreate.get(0));
      assertEquals("sql2", postTableCreate.get(1));
      assertEquals("sql3", postTableCreate.get(2));
      assertTrue(rtm.isRowLocking());
      assertTrue(rtm.isPkConstraint());
      
      relation = relationships.get("A-B");
      assertNotNull(relation);
      assertEquals("A-B", relation.getEjbRelationName());
      assertFalse(relation.isReadOnly());
      assertEquals(0, relation.getReadTimeOut());
      assertFalse(relation.isForeignKeyMapping());
      assertNull(relation.getRelationTableMapping());
      // left
      JBossCMPEjbRelationshipRoleMetaData role = relation.getLeftRole();
      assertNotNull(role);
      assertEquals("A-has-B", role.getEjbRelationshipRoleName());
      assertFalse(role.isFkConstraint());
      List<JBossCMPKeyFieldMetaData> keyFields = role.getKeyFields();
      assertNotNull(keyFields);
      assertEquals(1, keyFields.size());
      JBossCMPKeyFieldMetaData keyField = keyFields.get(0);
      assertNotNull(keyField);
      assertEquals("id", keyField.getFieldName());
      assertEquals("a_id", keyField.getColumnName());
      assertEquals(0, keyField.getJdbcType());
      assertNull(keyField.getSqlType());
      assertNull(keyField.getProperties());
      assertFalse(keyField.isDbindex());
      assertNull(role.getReadAhead());
      assertFalse(role.isBatchCascadeDelete());
      // right
      role = relation.getRightRole();
      assertNotNull(role);
      assertEquals("B-belongsto-A", role.getEjbRelationshipRoleName());
      assertFalse(role.isFkConstraint());
      assertNull(role.getKeyFields());
      assertNull(role.getReadAhead());
      assertFalse(role.isBatchCascadeDelete());

      relation = relationships.get("A-C");
      assertNotNull(relation);
      assertEquals("A-C", relation.getEjbRelationName());
      assertFalse(relation.isReadOnly());
      assertEquals(0, relation.getReadTimeOut());
      assertFalse(relation.isForeignKeyMapping());
      assertNull(relation.getRelationTableMapping());
      // left
      role = relation.getLeftRole();
      assertNotNull(role);
      assertEquals("A-has-C", role.getEjbRelationshipRoleName());
      assertFalse(role.isFkConstraint());
      keyFields = role.getKeyFields();
      assertNotNull(keyFields);
      assertEquals(2, keyFields.size());
      keyField = keyFields.get(0);
      assertNotNull(keyField);
      assertEquals("id1", keyField.getFieldName());
      assertEquals("a_id1", keyField.getColumnName());
      assertEquals(Types.INTEGER, keyField.getJdbcType());
      assertEquals("NUMERIC", keyField.getSqlType());
      assertNull(keyField.getProperties());
      assertFalse(keyField.isDbindex());
      keyField = keyFields.get(1);
      assertNotNull(keyField);
      assertEquals("id2", keyField.getFieldName());
      assertEquals("a_id2", keyField.getColumnName());
      assertEquals(0, keyField.getJdbcType());
      assertNull(keyField.getSqlType());
      assertNull(keyField.getProperties());
      assertTrue(keyField.isDbindex());
      assertNull(role.getReadAhead());
      assertFalse(role.isBatchCascadeDelete());
      // right
      role = relation.getRightRole();
      assertNotNull(role);
      assertEquals("C-belongsto-A", role.getEjbRelationshipRoleName());
      assertTrue(role.isFkConstraint());
      assertNull(role.getKeyFields());
      assertTrue(role.isBatchCascadeDelete());
      JBossCMPReadAheadMetaData readAhead = role.getReadAhead();
      assertNotNull(readAhead);
      assertEquals("on-find", readAhead.getStrategy());
      assertEquals(4, readAhead.getPageSize());
      assertEquals("quick info", readAhead.getEagerLoadGroup());
      assertNull(readAhead.getLeftJoins());

      relation = relationships.get("A-D");
      assertNotNull(relation);
      assertEquals("A-D", relation.getEjbRelationName());
      assertFalse(relation.isReadOnly());
      assertEquals(0, relation.getReadTimeOut());
      assertFalse(relation.isForeignKeyMapping());
      assertNull(relation.getRelationTableMapping());
      // left
      role = relation.getLeftRole();
      assertNotNull(role);
      assertEquals("A-has-D", role.getEjbRelationshipRoleName());
      assertFalse(role.isFkConstraint());
      keyFields = role.getKeyFields();
      assertNotNull(keyFields);
      assertEquals(1, keyFields.size());
      keyField = keyFields.get(0);
      assertNotNull(keyField);
      assertEquals("id", keyField.getFieldName());
      assertNull(keyField.getColumnName());
      assertEquals(0, keyField.getJdbcType());
      assertNull(keyField.getSqlType());
      assertFalse(keyField.isDbindex());
      List<JBossCMPPropertyMetaData> properties = keyField.getProperties();
      assertNotNull(properties);
      assertEquals(2, properties.size());
      JBossCMPPropertyMetaData prop = properties.get(0);
      assertNotNull(prop);
      assertEquals("id1", prop.getPropertyName());
      assertNull(prop.getColumnName());
      assertEquals(0, prop.getJdbcType());
      prop = properties.get(1);
      assertNotNull(prop);
      assertEquals("id2", prop.getPropertyName());
      assertEquals("ID2", prop.getColumnName());
      assertEquals(Types.VARCHAR, prop.getJdbcType());
      assertEquals("VARCHAR(11)", prop.getSqlType());
      assertNull(role.getReadAhead());
      assertFalse(role.isBatchCascadeDelete());
      // right
      role = relation.getRightRole();
      assertNotNull(role);
      assertEquals("D-belongsto-A", role.getEjbRelationshipRoleName());
      assertFalse(role.isFkConstraint());
      assertNull(role.getKeyFields());
      assertFalse(role.isBatchCascadeDelete());
      assertNull(role.getReadAhead());
   }
   
   public void testDependentValueClasses() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      List<JBossCMPDependentValueClassMetaData> dependentValueClasses = jbossMetaData.getDependentValueClasses();
      assertNotNull(dependentValueClasses);
      assertEquals(2, dependentValueClasses.size());
      JBossCMPDependentValueClassMetaData dvc = dependentValueClasses.get(0);
      assertNotNull(dvc);
      assertEquals("DVC description", dvc.getDescription());
      assertEquals("dependent.ValueClass", dvc.getClassName());
      List<JBossCMPPropertyMetaData> properties = dvc.getProperties();
      assertNotNull(properties);
      assertEquals(2, properties.size());
      JBossCMPPropertyMetaData prop = properties.get(0);
      assertEquals("prop1", prop.getPropertyName());
      assertEquals("PROP1", prop.getColumnName());
      assertFalse(prop.isNotNull());
      assertEquals(0, prop.getJdbcType());
      assertNull(prop.getSqlType());
      prop = properties.get(1);
      assertEquals("prop2", prop.getPropertyName());
      assertNull(prop.getColumnName());
      assertTrue(prop.isNotNull());
      assertEquals(Types.VARCHAR, prop.getJdbcType());
      assertEquals("VARCHAR(111)", prop.getSqlType());
      
      dvc = dependentValueClasses.get(1);
      assertNotNull(dvc);
      assertNull(dvc.getDescription());
      assertEquals("DependentValueClass", dvc.getClassName());
      properties = dvc.getProperties();
      assertNotNull(properties);
      assertEquals(1, properties.size());
      prop = properties.get(0);
      assertEquals("prop1", prop.getPropertyName());
      assertEquals("PROP1", prop.getColumnName());
      assertTrue(prop.isNotNull());
      assertEquals(Types.INTEGER, prop.getJdbcType());
      assertEquals("INTEGER", prop.getSqlType());
   }
   
   public void testUserTypeMappings() throws Exception
   {
      //enableTrace("org.jboss.xb.builder");

      JBossCMPMetaData jbossMetaData = unmarshal();    
      assertNotNull(jbossMetaData);
      List<JBossCMPUserTypeMappingMetaData> userTypeMappings = jbossMetaData.getUserTypeMappings();
      assertNotNull(userTypeMappings);
      assertEquals(2, userTypeMappings.size());
      JBossCMPUserTypeMappingMetaData utm = userTypeMappings.get(0);
      assertEquals("JavaType", utm.getJavaType());
      assertEquals("MappedType", utm.getMappedType());
      assertEquals("Mapper", utm.getMapper());
      assertFalse(utm.isCheckDirtyAfterGet());
      assertNull(utm.getStateFactory());
      
      utm = userTypeMappings.get(1);
      assertEquals("JavaType2", utm.getJavaType());
      assertEquals("MappedType2", utm.getMappedType());
      assertEquals("Mapper2", utm.getMapper());
      assertTrue(utm.isCheckDirtyAfterGet());
      assertEquals("StateFactory", utm.getStateFactory());
   }
}
