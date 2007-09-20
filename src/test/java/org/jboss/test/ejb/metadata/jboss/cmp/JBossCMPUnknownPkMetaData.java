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
 * A UnknownPkMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPUnknownPkMetaData
{
   private String keyGeneratorFactory;
   private String unknownPkClass;
   private String fieldName;
   private boolean readOnly;
   private long readOnlyTimeOut;
   private String columnName;
   private int jdbcType;
   private String sqlType;
   private boolean autoIncrement;
   
   /**
    * Get the autoIncrement.
    * 
    * @return the autoIncrement.
    */
   public boolean isAutoIncrement()
   {
      return autoIncrement;
   }
   
   /**
    * Set the autoIncrement.
    * 
    * @param autoIncrement The autoIncrement to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
   public void setAutoIncrement(boolean autoIncrement)
   {
      this.autoIncrement = autoIncrement;
   }
   
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
   @XmlJavaTypeAdapter(value=JDBCTypeAdapter.class)
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
    * Get the readOnly.
    * 
    * @return the readOnly.
    */
   
   public boolean isReadOnly()
   {
      return readOnly;
   }
   /**
    * Set the readOnly.
    * 
    * @param readOnly The readOnly to set.
    */
   
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }
   /**
    * Get the readOnlyTimeOut.
    * 
    * @return the readOnlyTimeOut.
    */
   
   public long getReadOnlyTimeOut()
   {
      return readOnlyTimeOut;
   }
   /**
    * Set the readOnlyTimeOut.
    * 
    * @param readOnlyTimeOut The readOnlyTimeOut to set.
    */
   
   public void setReadOnlyTimeOut(long readOnlyTimeOut)
   {
      this.readOnlyTimeOut = readOnlyTimeOut;
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
    * Get the unknownPkClass.
    * 
    * @return the unknownPkClass.
    */
   
   public String getUnknownPkClass()
   {
      return unknownPkClass;
   }
   /**
    * Set the unknownPkClass.
    * 
    * @param unknownPkClass The unknownPkClass to set.
    */
   
   public void setUnknownPkClass(String unknownPkClass)
   {
      this.unknownPkClass = unknownPkClass;
   }
}
