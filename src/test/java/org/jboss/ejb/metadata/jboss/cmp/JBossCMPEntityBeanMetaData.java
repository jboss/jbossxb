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
package org.jboss.ejb.metadata.jboss.cmp;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.EntityBeanMetaData;
import org.jboss.javaee.metadata.support.NamedMetaDataWithDescriptionGroupWithOverride;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;


/**
 * A JBossCMPEntityBeanMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(
      kind=JBossXmlConstants.MODEL_GROUP_CHOICE,
      particles={
            @JBossXmlModelGroup.Particle(element=@XmlElement(name="entity"), type=JBossCMPEntityBeanMetaData.class)})
@XmlType(name="jbosscmp-entityType")
public class JBossCMPEntityBeanMetaData extends NamedMetaDataWithDescriptionGroupWithOverride<EnterpriseBeanMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -4847975458333924853L;
   
   private String datasource;
   private String datasourceMapping;
   private boolean createTable;
   private boolean removeTable;
   private List<String> postTableCreate;
   private boolean readOnly;
   private long readTimeOut;
   private boolean rowLocking;
   private boolean pkConstraint;
   private JBossCMPReadAheadMetaData readAhead;
   private long listCacheMax;
   private boolean cleanReadAheadOnLoad;
   private long fetchSize;
   private String tableName;
   private JBossCMPFieldsMetaData cmpFields;
   private JBossCMPFieldsMetaData mergedCmpFields;
   private List<JBossCMPLoadGroupMetaData> loadGroups;
   private String eagerLoadGroup;
   private List<String> lazyLoadGroups;
   private List<JBossCMPQueryMetaData> queries;
   private JBossCMPUnknownPkMetaData unknownPk;
   private JBossCMPEntityCommandMetaData entityCommand;
   private JBossCMPOptimisticLockingMetaData optimisticLocking;
   private JBossCMPAuditMetaData audit;
   
   @Override
   public EntityBeanMetaData getOverridenMetaData()
   {
      return (EntityBeanMetaData) super.getOverridenMetaData();
   }
   
   @XmlTransient
   public void setOverridenMetaData(EntityBeanMetaData data)
   {
      super.setOverridenMetaData(data);
   }

   public JBossCMPAuditMetaData getAudit()
   {
      return audit;
   }
   public void setAudit(JBossCMPAuditMetaData audit)
   {
      this.audit = audit;
   }
   public boolean isCleanReadAheadOnLoad()
   {
      return cleanReadAheadOnLoad;
   }
   public void setCleanReadAheadOnLoad(boolean cleanReadAheadOnLoad)
   {
      this.cleanReadAheadOnLoad = cleanReadAheadOnLoad;
   }
   
   public JBossCMPFieldsMetaData getCmpFields()
   {
      if(mergedCmpFields != null)
      {
         return mergedCmpFields;
      }
    
      if(getOverridenMetaData() == null)
      {
         return cmpFields;
      }
      
      mergedCmpFields = JBossCMPFieldsMetaData.merge(cmpFields, ((EntityBeanMetaData)getOverridenMetaDataWithCheck()).getCmpFields());
      return mergedCmpFields;
   }
   
   @XmlElement(name="cmp-field")
   public void setCmpFields(JBossCMPFieldsMetaData cmpFields)
   {
      this.cmpFields = cmpFields;
   }

   public boolean isCreateTable()
   {
      return createTable;
   }
   public void setCreateTable(boolean createTable)
   {
      this.createTable = createTable;
   }
   public String getDatasource()
   {
      return datasource;
   }
   public void setDatasource(String datasource)
   {
      this.datasource = datasource;
   }
   public String getDatasourceMapping()
   {
      return datasourceMapping;
   }
   public void setDatasourceMapping(String datasourceMapping)
   {
      this.datasourceMapping = datasourceMapping;
   }
   public String getEagerLoadGroup()
   {
      return eagerLoadGroup;
   }
   public void setEagerLoadGroup(String eagerLoadGroup)
   {
      this.eagerLoadGroup = eagerLoadGroup;
   }
   
   public String getEjbName()
   {
      return getName();
   }
   
   public void setEjbName(String ejbName)
   {
      setName(ejbName);
   }
   
   public JBossCMPEntityCommandMetaData getEntityCommand()
   {
      return entityCommand;
   }
   
   public void setEntityCommand(JBossCMPEntityCommandMetaData entityCommand)
   {
      this.entityCommand = entityCommand;
   }
   
   public long getFetchSize()
   {
      return fetchSize;
   }
   public void setFetchSize(long fetchSize)
   {
      this.fetchSize = fetchSize;
   }
   
   public List<String> getLazyLoadGroups()
   {
      return lazyLoadGroups;
   }
   
   @XmlElementWrapper
   @XmlElement(name="load-group-name")
   public void setLazyLoadGroups(List<String> lazyLoadGroups)
   {
      this.lazyLoadGroups = lazyLoadGroups;
   }
   
   public long getListCacheMax()
   {
      return listCacheMax;
   }
   public void setListCacheMax(long listCacheMax)
   {
      this.listCacheMax = listCacheMax;
   }
   
   public List<JBossCMPLoadGroupMetaData> getLoadGroups()
   {
      return loadGroups;
   }
   
   @XmlElementWrapper
   @XmlElement(name="load-group")
   public void setLoadGroups(List<JBossCMPLoadGroupMetaData> loadGroups)
   {
      this.loadGroups = loadGroups;
   }
   
   public JBossCMPOptimisticLockingMetaData getOptimisticLocking()
   {
      return optimisticLocking;
   }
   
   public void setOptimisticLocking(JBossCMPOptimisticLockingMetaData optimisticLocking)
   {
      this.optimisticLocking = optimisticLocking;
   }
   
   public boolean isPkConstraint()
   {
      return pkConstraint;
   }
   public void setPkConstraint(boolean pkConstraint)
   {
      this.pkConstraint = pkConstraint;
   }
   
   public List<String> getPostTableCreate()
   {
      return postTableCreate;
   }
   
   @XmlElementWrapper
   @XmlElement(name="sql-statement")
   public void setPostTableCreate(List<String> postTableCreate)
   {
      this.postTableCreate = postTableCreate;
   }
   
   public List<JBossCMPQueryMetaData> getQueries()
   {
      return queries;
   }
   
   @XmlElement(name="query")
   public void setQueries(List<JBossCMPQueryMetaData> queries)
   {
      this.queries = queries;
   }
   
   public JBossCMPReadAheadMetaData getReadAhead()
   {
      return readAhead;
   }
   
   public void setReadAhead(JBossCMPReadAheadMetaData readAhead)
   {
      this.readAhead = readAhead;
   }
   
   public boolean isReadOnly()
   {
      return readOnly;
   }
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }
   public long getReadTimeOut()
   {
      return readTimeOut;
   }
   public void setReadTimeOut(long readTimeOut)
   {
      this.readTimeOut = readTimeOut;
   }
   public boolean isRemoveTable()
   {
      return removeTable;
   }
   public void setRemoveTable(boolean removeTable)
   {
      this.removeTable = removeTable;
   }
   public boolean isRowLocking()
   {
      return rowLocking;
   }
   public void setRowLocking(boolean rowLocking)
   {
      this.rowLocking = rowLocking;
   }
   public String getTableName()
   {
      return tableName;
   }
   public void setTableName(String tableName)
   {
      this.tableName = tableName;
   }
   public JBossCMPUnknownPkMetaData getUnknownPk()
   {
      return unknownPk;
   }
   public void setUnknownPk(JBossCMPUnknownPkMetaData unknownPk)
   {
      this.unknownPk = unknownPk;
   }
   
   
}
