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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.ejb.metadata.spec.CMPFieldMetaData;
import org.jboss.javaee.metadata.support.NamedMetaDataWithDescriptionGroupWithOverride;


/**
 * A JBossCMPFieldMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jbosscmp-cmp-fieldType")
public class JBossCMPFieldMetaData
   extends NamedMetaDataWithDescriptionGroupWithOverride<CMPFieldMetaData>
{
   private boolean readOnly;
   private long readTimeOut;
   private String columnName;
   private boolean notNull;
   private int jdbcType;
   private String sqlType;
   private List<JBossCMPPropertyMetaData> properties;
   private boolean autoIncrement;
   private boolean dbindex;
   private boolean checkDirtyAfterGet;
   private String stateFactory;
   
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
   @XmlJavaTypeAdapter(value=EmptyElementToBooleanAdapter.class)
   public void setAutoIncrement(boolean autoIncrement)
   {
      this.autoIncrement = autoIncrement;
   }
   
   /**
    * Get the checkDirtyAfterGet.
    * 
    * @return the checkDirtyAfterGet.
    */
   public boolean isCheckDirtyAfterGet()
   {
      return checkDirtyAfterGet;
   }

   /**
    * Set the checkDirtyAfterGet.
    * 
    * @param checkDirtyAfterGet The checkDirtyAfterGet to set.
    */
   public void setCheckDirtyAfterGet(boolean checkDirtyAfterGet)
   {
      this.checkDirtyAfterGet = checkDirtyAfterGet;
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
    * Get the dbIndex.
    * 
    * @return the dbIndex.
    */
   public boolean isDbindex()
   {
      return dbindex;
   }

   /**
    * Set the dbIndex.
    * 
    * @param dbIndex The dbIndex to set.
    */
   @XmlJavaTypeAdapter(value=EmptyElementToBooleanAdapter.class)
   public void setDbindex(boolean dbIndex)
   {
      this.dbindex = dbIndex;
   }

   /**
    * Get the fieldName.
    * 
    * @return the fieldName.
    */
   public String getFieldName()
   {
      return getName();
   }

   /**
    * Set the fieldName.
    * 
    * @param fieldName The fieldName to set.
    */
   public void setFieldName(String fieldName)
   {
      setName(fieldName);
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
    * Get the notNull.
    * 
    * @return the notNull.
    */
   public boolean isNotNull()
   {
      return notNull;
   }

   /**
    * Set the notNull.
    * 
    * @param notNull The notNull to set.
    */
   public void setNotNull(boolean notNull)
   {
      this.notNull = notNull;
   }

   /**
    * Get the properties.
    * 
    * @return the properties.
    */
   public List<JBossCMPPropertyMetaData> getProperties()
   {
      return properties;
   }

   /**
    * Set the properties.
    * 
    * @param properties The properties to set.
    */
   @XmlElement(name="property")
   public void setProperties(List<JBossCMPPropertyMetaData> properties)
   {
      this.properties = properties;
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
    * Get the readTimeOut.
    * 
    * @return the readTimeOut.
    */
   public long getReadTimeOut()
   {
      return readTimeOut;
   }

   /**
    * Set the readTimeOut.
    * 
    * @param readTimeOut The readTimeOut to set.
    */
   public void setReadTimeOut(long readTimeOut)
   {
      this.readTimeOut = readTimeOut;
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
    * Get the stateFactory.
    * 
    * @return the stateFactory.
    */
   public String getStateFactory()
   {
      return stateFactory;
   }

   /**
    * Set the stateFactory.
    * 
    * @param stateFactory The stateFactory to set.
    */
   public void setStateFactory(String stateFactory)
   {
      this.stateFactory = stateFactory;
   }
}
