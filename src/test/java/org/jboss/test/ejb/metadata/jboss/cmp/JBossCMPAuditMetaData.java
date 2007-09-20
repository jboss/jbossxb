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
 * A JBossCMPAuditMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPAuditMetaData
{
   private Field createdBy;
   private Field createdTime;
   private Field updatedBy;
   private Field updatedTime;

   /**
    * Get the createdBy.
    * 
    * @return the createdBy.
    */
   public Field getCreatedBy()
   {
      return createdBy;
   }

   /**
    * Set the createdBy.
    * 
    * @param createdBy The createdBy to set.
    */
   public void setCreatedBy(Field createdBy)
   {
      this.createdBy = createdBy;
   }

   /**
    * Get the createdTime.
    * 
    * @return the createdTime.
    */   
   public Field getCreatedTime()
   {
      return createdTime;
   }

   /**
    * Set the createdTime.
    * 
    * @param createdTime The createdTime to set.
    */
   public void setCreatedTime(Field createdTime)
   {
      this.createdTime = createdTime;
   }

   /**
    * Get the updatedBy.
    * 
    * @return the updatedBy.
    */
   public Field getUpdatedBy()
   {
      return updatedBy;
   }

   /**
    * Set the updatedBy.
    * 
    * @param updatedBy The updatedBy to set.
    */
   public void setUpdatedBy(Field updatedBy)
   {
      this.updatedBy = updatedBy;
   }

   /**
    * Get the updatedTime.
    * 
    * @return the updatedTime.
    */
   public Field getUpdatedTime()
   {
      return updatedTime;
   }

   /**
    * Set the updatedTime.
    * 
    * @param updatedTime The updatedTime to set.
    */
   public void setUpdatedTime(Field updatedTimed)
   {
      this.updatedTime = updatedTimed;
   }

   
   public static class Field
   {
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
}
