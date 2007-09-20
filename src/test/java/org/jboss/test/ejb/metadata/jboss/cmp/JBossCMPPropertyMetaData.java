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
 * A JBossCMPPropertyMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPPropertyMetaData
{
   private String propertyName;
   private String columnName;
   private boolean notNull;
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
    * Get the propertyName.
    * 
    * @return the propertyName.
    */
   public String getPropertyName()
   {
      return propertyName;
   }

   /**
    * Set the propertyName.
    * 
    * @param propertyName The propertyName to set.
    */
   public void setPropertyName(String propertyName)
   {
      this.propertyName = propertyName;
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
}