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

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A JBossCMPOptimisticLockingMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPOptimisticLockingMetaData
{
   private String groupName;
   private boolean modifiedStrategy;
   private boolean readStrategy;
   private boolean versionColumn;
   private boolean timestampColumn;
   private String keyGeneratorFactory;
   private String fieldType;
   private String fieldName;
   private String columnName;
   private int jdbcType;
   private String sqlType;
   
   /**
    * Get the columnName.
    * 
    * @return the columnName.
    */
   public String getColumnName()
   {
      return columnName;
   }
   
   /**
    * Set the columnName.
    * 
    * @param columnName The columnName to set.
    */
   public void setColumnName(String columnName)
   {
      this.columnName = columnName;
   }

   /**
    * Get the fieldName.
    * 
    * @return the fieldName.
    */
   public String getFieldName()
   {
      return fieldName;
   }

   /**
    * Set the fieldName.
    * 
    * @param fieldName The fieldName to set.
    */
   public void setFieldName(String fieldName)
   {
      this.fieldName = fieldName;
   }

   /**
    * Get the fieldType.
    * 
    * @return the fieldType.
    */
   public String getFieldType()
   {
      return fieldType;
   }

   /**
    * Set the fieldType.
    * 
    * @param fieldType The fieldType to set.
    */
   public void setFieldType(String fieldType)
   {
      this.fieldType = fieldType;
   }

   /**
    * Get the groupName.
    * 
    * @return the groupName.
    */
   public String getGroupName()
   {
      return groupName;
   }

   /**
    * Set the groupName.
    * 
    * @param groupName The groupName to set.
    */
   public void setGroupName(String groupName)
   {
      this.groupName = groupName;
   }

   /**
    * Get the jdbcType.
    * 
    * @return the jdbcType.
    */
   public int getJdbcType()
   {
      return jdbcType;
   }

   /**
    * Set the jdbcType.
    * 
    * @param jdbcType The jdbcType to set.
    */
   @XmlJavaTypeAdapter(value = JDBCTypeAdapter.class)
   public void setJdbcType(int jdbcType)
   {
      this.jdbcType = jdbcType;
   }
   
   /**
    * Get the keyGeneratorFactory.
    * 
    * @return the keyGeneratorFactory.
    */
   public String getKeyGeneratorFactory()
   {
      return keyGeneratorFactory;
   }

   /**
    * Set the keyGeneratorFactory.
    * 
    * @param keyGeneratorFactory The keyGeneratorFactory to set.
    */
   public void setKeyGeneratorFactory(String keyGeneratorFactory)
   {
      this.keyGeneratorFactory = keyGeneratorFactory;
   }

   /**
    * Get the modifiedStrategy.
    * 
    * @return the modifiedStrategy.
    */
   public boolean isModifiedStrategy()
   {
      return modifiedStrategy;
   }

   /**
    * Set the modifiedStrategy.
    * 
    * @param modifiedStrategy The modifiedStrategy to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
   public void setModifiedStrategy(boolean modifiedStrategy)
   {
      this.modifiedStrategy = modifiedStrategy;
   }

   /**
    * Get the readStrategy.
    * 
    * @return the readStrategy.
    */
   public boolean isReadStrategy()
   {
      return readStrategy;
   }

   /**
    * Set the readStrategy.
    * 
    * @param readStrategy The readStrategy to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
   public void setReadStrategy(boolean readStrategy)
   {
      this.readStrategy = readStrategy;
   }

   /**
    * Get the sqlType.
    * 
    * @return the sqlType.
    */   
   public String getSqlType()
   {
      return sqlType;
   }
   
   /**
    * Set the sqlType.
    * 
    * @param sqlType The sqlType to set.
    */
   public void setSqlType(String sqlType)
   {
      this.sqlType = sqlType;
   }

   /**
    * Get the timestampColumn.
    * 
    * @return the timestampColumn.
    */
   public boolean isTimestampColumn()
   {
      return timestampColumn;
   }

   /**
    * Set the timestampColumn.
    * 
    * @param timestampColumn The timestampColumn to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)   
   public void setTimestampColumn(boolean timestampColumn)
   {
      this.timestampColumn = timestampColumn;
   }

   /**
    * Get the versionColumn.
    * 
    * @return the versionColumn.
    */
   public boolean isVersionColumn()
   {
      return versionColumn;
   }

   /**
    * Set the versionColumn.
    * 
    * @param versionColumn The versionColumn to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)   
   public void setVersionColumn(boolean versionColumn)
   {
      this.versionColumn = versionColumn;
   }
}
