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

import javax.xml.bind.annotation.XmlType;


/**
 * A DefaultsMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="defaultsType")
public class JBossCMPDefaultsMetaData
{
   private String datasource;
   private String datasourceMapping;
   private boolean createTable;
   private boolean alterTable;
   private boolean removeTable;
   private List<String> postTableCreate;
   private boolean readOnly;
   private long readTimeOut;
   private boolean rowLocking;
   private boolean pkConstraint;
   private boolean fkConstraint;
   private JBossCMPPreferredRelationMapping preferredRelationMapping;
   private JBossCMPReadAheadMetaData readAhead;
   private long listCacheMax;
   private boolean cleanReadAheadOnLoad;
   private long fetchSize;
   private JBossCMPUnknownPkMetaData unknownPk;
   private JBossCMPEntityCommandMetaData entityCommand;
   private String qlCompiler;
   private boolean throwRuntimeExceptions;
   
   public boolean isAlterTable()
   {
      return alterTable;
   }
   
   public void setAlterTable(boolean alterTable)
   {
      this.alterTable = alterTable;
   }
   
   public boolean isCleanReadAheadOnLoad()
   {
      return cleanReadAheadOnLoad;
   }
   
   public void setCleanReadAheadOnLoad(boolean cleanReadAheadOnLoad)
   {
      this.cleanReadAheadOnLoad = cleanReadAheadOnLoad;
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
   
   public boolean isFkConstraint()
   {
      return fkConstraint;
   }
   
   public void setFkConstraint(boolean fkConstraint)
   {
      this.fkConstraint = fkConstraint;
   }
   
   public long getListCacheMax()
   {
      return listCacheMax;
   }
   
   public void setListCacheMax(long listCacheMax)
   {
      this.listCacheMax = listCacheMax;
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
   
   public void setPostTableCreate(List<String> postTableCreate)
   {
      this.postTableCreate = postTableCreate;
   }
   
   public JBossCMPPreferredRelationMapping getPreferredRelationMapping()
   {
      return preferredRelationMapping;
   }
   
   public void setPreferredRelationMapping(JBossCMPPreferredRelationMapping preferredRelationMapping)
   {
      this.preferredRelationMapping = preferredRelationMapping;
   }
   
   public String getQlCompiler()
   {
      return qlCompiler;
   }
   
   public void setQlCompiler(String qlCompiler)
   {
      this.qlCompiler = qlCompiler;
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
   
   public boolean isThrowRuntimeExceptions()
   {
      return throwRuntimeExceptions;
   }
   
   public void setThrowRuntimeExceptions(boolean throwRuntimeExceptions)
   {
      this.throwRuntimeExceptions = throwRuntimeExceptions;
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
