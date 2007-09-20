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


/**
 * A JBossCMPRelationTableMappingMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPRelationTableMappingMetaData
{
   private String tableName;
   private String datasource;
   private String datasourceMapping;
   private boolean createTable;
   private boolean removeTable;
   private List<String> postTableCreate;
   private boolean rowLocking;
   private boolean pkConstraint;
   
   /**
    * Get the createTable.
    * 
    * @return the createTable.
    */
   public boolean isCreateTable()
   {
      return createTable;
   }
   
   /**
    * Set the createTable.
    * 
    * @param createTable The createTable to set.
    */
   public void setCreateTable(boolean createTable)
   {
      this.createTable = createTable;
   }
   
   /**
    * Get the datasource.
    * 
    * @return the datasource.
    */
   public String getDatasource()
   {
      return datasource;
   }
   
   /**
    * Set the datasource.
    * 
    * @param datasource The datasource to set.
    */
   public void setDatasource(String datasource)
   {
      this.datasource = datasource;
   }
   
   /**
    * Get the datasourceMapping.
    * 
    * @return the datasourceMapping.
    */
   public String getDatasourceMapping()
   {
      return datasourceMapping;
   }
   
   /**
    * Set the datasourceMapping.
    * 
    * @param datasourceMapping The datasourceMapping to set.
    */
   public void setDatasourceMapping(String datasourceMapping)
   {
      this.datasourceMapping = datasourceMapping;
   }
   
   /**
    * Get the pkConstraint.
    * 
    * @return the pkConstraint.
    */
   public boolean isPkConstraint()
   {
      return pkConstraint;
   }
   
   /**
    * Set the pkConstraint.
    * 
    * @param pkConstraint The pkConstraint to set.
    */
   public void setPkConstraint(boolean pkConstraint)
   {
      this.pkConstraint = pkConstraint;
   }
   
   /**
    * Get the postTableCreate.
    * 
    * @return the postTableCreate.
    */
   public List<String> getPostTableCreate()
   {
      return postTableCreate;
   }
   
   /**
    * Set the postTableCreate.
    * 
    * @param postTableCreate The postTableCreate to set.
    */
   @XmlElementWrapper
   @XmlElement(name = "sql-statement")
   public void setPostTableCreate(List<String> postTableCreate)
   {
      this.postTableCreate = postTableCreate;
   }

   /**
    * Get the removeTable.
    * 
    * @return the removeTable.
    */
   public boolean isRemoveTable()
   {
      return removeTable;
   }
   
   /**
    * Set the removeTable.
    * 
    * @param removeTable The removeTable to set.
    */
   public void setRemoveTable(boolean removeTable)
   {
      this.removeTable = removeTable;
   }
   
   /**
    * Get the rowLocking.
    * 
    * @return the rowLocking.
    */
   public boolean isRowLocking()
   {
      return rowLocking;
   }
   
   /**
    * Set the rowLocking.
    * 
    * @param rowLocking The rowLocking to set.
    */
   public void setRowLocking(boolean rowLocking)
   {
      this.rowLocking = rowLocking;
   }
   
   /**
    * Get the tableName.
    * 
    * @return the tableName.
    */
   public String getTableName()
   {
      return tableName;
   }
   
   /**
    * Set the tableName.
    * 
    * @param tableName The tableName to set.
    */
   public void setTableName(String tableName)
   {
      this.tableName = tableName;
   }   
}
