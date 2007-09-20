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


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.javaee.metadata.support.AbstractMappedMetaData;
import org.jboss.javaee.metadata.support.MappableMetaData;


/**
 * A JBossCMPTypeMappingsMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="type-mappingType")
public class JBossCMPTypeMappingMetaData
{
   private String name;
   private String rowLockingTemplate;
   private String pkConstraintTemplate;
   private String fkConstraintTemplate;
   private String autoIncrementTemplate;
   private String addColumnTemplate;
   private String alterColumnTemplate;
   private String dropColumnTemplate;
   private String aliasHeaderPrefix;
   private String aliasHeaderSuffix;
   private int aliasMaxLength;
   private boolean subquerySupported;
   private String trueMapping;
   private String falseMapping;
   private long maxKeysInDelete;
   private FunctionMappings functionMappings;
   private Mappings mappings;

   public JBossCMPTypeMappingMetaData()
   {
   }
   
   /**
    * Get the addColumnTemplate.
    * 
    * @return the addColumnTemplate.
    */
   
   public String getAddColumnTemplate()
   {
      return addColumnTemplate;
   }

   /**
    * Set the addColumnTemplate.
    * 
    * @param addColumnTemplate The addColumnTemplate to set.
    */
   
   public void setAddColumnTemplate(String addColumnTemplate)
   {
      this.addColumnTemplate = addColumnTemplate;
   }

   /**
    * Get the aliasHeaderPrefix.
    * 
    * @return the aliasHeaderPrefix.
    */
   
   public String getAliasHeaderPrefix()
   {
      return aliasHeaderPrefix;
   }

   /**
    * Set the aliasHeaderPrefix.
    * 
    * @param aliasHeaderPrefix The aliasHeaderPrefix to set.
    */
   
   public void setAliasHeaderPrefix(String aliasHeaderPrefix)
   {
      this.aliasHeaderPrefix = aliasHeaderPrefix;
   }

   /**
    * Get the aliasHeaderSuffix.
    * 
    * @return the aliasHeaderSuffix.
    */
   
   public String getAliasHeaderSuffix()
   {
      return aliasHeaderSuffix;
   }

   /**
    * Set the aliasHeaderSuffix.
    * 
    * @param aliasHeaderSuffix The aliasHeaderSuffix to set.
    */
   
   public void setAliasHeaderSuffix(String aliasHeaderSuffix)
   {
      this.aliasHeaderSuffix = aliasHeaderSuffix;
   }

   /**
    * Get the aliasMaxLength.
    * 
    * @return the aliasMaxLength.
    */
   
   public int getAliasMaxLength()
   {
      return aliasMaxLength;
   }

   /**
    * Set the aliasMaxLength.
    * 
    * @param aliasMaxLength The aliasMaxLength to set.
    */
   
   public void setAliasMaxLength(int aliasMaxLength)
   {
      this.aliasMaxLength = aliasMaxLength;
   }

   /**
    * Get the alterColumnTemplate.
    * 
    * @return the alterColumnTemplate.
    */
   
   public String getAlterColumnTemplate()
   {
      return alterColumnTemplate;
   }

   /**
    * Set the alterColumnTemplate.
    * 
    * @param alterColumnTemplate The alterColumnTemplate to set.
    */
   
   public void setAlterColumnTemplate(String alterColumnTemplate)
   {
      this.alterColumnTemplate = alterColumnTemplate;
   }

   /**
    * Get the autoIncrementTemplate.
    * 
    * @return the autoIncrementTemplate.
    */
   
   public String getAutoIncrementTemplate()
   {
      return autoIncrementTemplate;
   }

   /**
    * Set the autoIncrementTemplate.
    * 
    * @param autoIncrementTemplate The autoIncrementTemplate to set.
    */
   
   public void setAutoIncrementTemplate(String autoIncrementTemplate)
   {
      this.autoIncrementTemplate = autoIncrementTemplate;
   }

   /**
    * Get the dropColumnTemplate.
    * 
    * @return the dropColumnTemplate.
    */
   
   public String getDropColumnTemplate()
   {
      return dropColumnTemplate;
   }

   /**
    * Set the dropColumnTemplate.
    * 
    * @param dropColumnTemplate The dropColumnTemplate to set.
    */
   
   public void setDropColumnTemplate(String dropColumnTemplate)
   {
      this.dropColumnTemplate = dropColumnTemplate;
   }

   /**
    * Get the falseMapping.
    * 
    * @return the falseMapping.
    */
   
   public String getFalseMapping()
   {
      return falseMapping;
   }

   /**
    * Set the falseMapping.
    * 
    * @param falseMapping The falseMapping to set.
    */
   
   public void setFalseMapping(String falseMapping)
   {
      this.falseMapping = falseMapping;
   }

   /**
    * Get the fkConstraintTemplate.
    * 
    * @return the fkConstraintTemplate.
    */
   
   public String getFkConstraintTemplate()
   {
      return fkConstraintTemplate;
   }

   /**
    * Set the fkConstraintTemplate.
    * 
    * @param fkConstraintTemplate The fkConstraintTemplate to set.
    */
   
   public void setFkConstraintTemplate(String fkConstraintTemplate)
   {
      this.fkConstraintTemplate = fkConstraintTemplate;
   }

   /**
    * Get the functionMappings.
    * 
    * @return the functionMappings.
    */
   public FunctionMappings getFunctionMappings()
   {
      return functionMappings;
   }

   /**
    * Set the functionMappings.
    * 
    * @param functionMappings The functionMappings to set.
    */
   @XmlElement(name="function-mapping")
   public void setFunctionMappings(FunctionMappings functionMappings)
   {
      this.functionMappings = functionMappings;
   }

   /**
    * Get the mappings.
    * 
    * @return the mappings.
    */
   public Mappings getMappings()
   {
      return mappings;
   }

   /**
    * Set the mappings.
    * 
    * @param mappings The mappings to set.
    */
   @XmlElement(name="mapping")
   public void setMappings(Mappings mappings)
   {
      this.mappings = mappings;
   }

   /**
    * Get the maxKeysInDelete.
    * 
    * @return the maxKeysInDelete.
    */
   
   public long getMaxKeysInDelete()
   {
      return maxKeysInDelete;
   }

   /**
    * Set the maxKeysInDelete.
    * 
    * @param maxKeysInDelete The maxKeysInDelete to set.
    */
   
   public void setMaxKeysInDelete(long maxKeysInDelete)
   {
      this.maxKeysInDelete = maxKeysInDelete;
   }

   /**
    * Get the name.
    * 
    * @return the name.
    */
   
   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    * 
    * @param name The name to set.
    */
   
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Get the pkConstraintTemplate.
    * 
    * @return the pkConstraintTemplate.
    */
   
   public String getPkConstraintTemplate()
   {
      return pkConstraintTemplate;
   }

   /**
    * Set the pkConstraintTemplate.
    * 
    * @param pkConstraintTemplate The pkConstraintTemplate to set.
    */
   
   public void setPkConstraintTemplate(String pkConstraintTemplate)
   {
      this.pkConstraintTemplate = pkConstraintTemplate;
   }

   /**
    * Get the rowLockingTemplate.
    * 
    * @return the rowLockingTemplate.
    */
   
   public String getRowLockingTemplate()
   {
      return rowLockingTemplate;
   }

   /**
    * Set the rowLockingTemplate.
    * 
    * @param rowLockingTemplate The rowLockingTemplate to set.
    */
   
   public void setRowLockingTemplate(String rowLockingTemplate)
   {
      this.rowLockingTemplate = rowLockingTemplate;
   }

   /**
    * Get the subquerySupported.
    * 
    * @return the subquerySupported.
    */
   
   public boolean isSubquerySupported()
   {
      return subquerySupported;
   }

   /**
    * Set the subquerySupported.
    * 
    * @param subquerySupported The subquerySupported to set.
    */
   
   public void setSubquerySupported(boolean subquerySupported)
   {
      this.subquerySupported = subquerySupported;
   }

   /**
    * Get the trueMapping.
    * 
    * @return the trueMapping.
    */
   
   public String getTrueMapping()
   {
      return trueMapping;
   }

   /**
    * Set the trueMapping.
    * 
    * @param trueMapping The trueMapping to set.
    */
   
   public void setTrueMapping(String trueMapping)
   {
      this.trueMapping = trueMapping;
   }
   
   @XmlTransient
   public String getFunctionSql(String functionName)
   {
      FunctionMapping mapping = functionMappings.get(functionName);
      return mapping == null ? null : mapping.getFunctionSql();
   }
   
   @XmlTransient
   public int getJdbcType(String javaType)
   {
      Mapping mapping = mappings.get(javaType);
      return mapping == null ? null : mapping.getJdbcType();
   }
   
   @XmlTransient
   public String getSqlType(String javaType)
   {
      Mapping mapping = mappings.get(javaType);
      return mapping == null ? null : mapping.getSqlType();
   }
   
   public static class FunctionMappings extends AbstractMappedMetaData<FunctionMapping>
   {
      public FunctionMappings()
      {
         super("function mappings");
      }      
   }
   
   public static class FunctionMapping implements MappableMetaData
   {
      private String functionName;
      private String functionSql;
      
      /**
       * Get the functionName.
       * 
       * @return the functionName.
       */
      public String getFunctionName()
      {
         return functionName;
      }
      
      /**
       * Set the functionName.
       * 
       * @param functionName The functionName to set.
       */
      public void setFunctionName(String functionName)
      {
         this.functionName = functionName;
      }

      /**
       * Get the functionSql.
       * 
       * @return the functionSql.
       */
      public String getFunctionSql()
      {
         return functionSql;
      }

      /**
       * Set the functionSql.
       * 
       * @param functionSql The functionSql to set.
       */
      public void setFunctionSql(String functionSql)
      {
         this.functionSql = functionSql;
      }

      public String getKey()
      {
         return functionName;
      }
   }
   
   public static class Mappings extends AbstractMappedMetaData<Mapping>
   {
      public Mappings()
      {
         super("mapping");
      }      
   }
   
   public static class Mapping implements MappableMetaData
   {
      private String javaType;
      private int jdbcType;
      private String sqlType;
      private String paramSetter;
      private String resultReader;
      
      /**
       * Get the javaType.
       * 
       * @return the javaType.
       */
      
      public String getJavaType()
      {
         return javaType;
      }
      
      /**
       * Set the javaType.
       * 
       * @param javaType The javaType to set.
       */      
      public void setJavaType(String javaType)
      {
         this.javaType = javaType;
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
       * Get the paramSetter.
       * 
       * @return the paramSetter.
       */
      public String getParamSetter()
      {
         return paramSetter;
      }
      
      /**
       * Set the paramSetter.
       * 
       * @param paramSetter The paramSetter to set.
       */
      public void setParamSetter(String paramSetter)
      {
         this.paramSetter = paramSetter;
      }

      /**
       * Get the resultReader.
       * 
       * @return the resultReader.
       */
      public String getResultReader()
      {
         return resultReader;
      }
      
      /**
       * Set the resultReader.
       * 
       * @param resultReader The resultReader to set.
       */
      public void setResultReader(String resultReader)
      {
         this.resultReader = resultReader;
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

      public String getKey()
      {
         return javaType;
      }
   }
}
