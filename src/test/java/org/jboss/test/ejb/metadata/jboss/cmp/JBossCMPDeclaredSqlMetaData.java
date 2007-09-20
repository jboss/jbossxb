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
 * A JBossCMPDeclaredSqlMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPDeclaredSqlMetaData
{
   private Select select;
   private String from;
   private String where;
   private String order;
   private String other;

   /**
    * Get the from.
    * 
    * @return the from.
    */
   public String getFrom()
   {
      return from;
   }

   /**
    * Set the from.
    * 
    * @param from The from to set.
    */
   public void setFrom(String from)
   {
      this.from = from;
   }

   /**
    * Get the order.
    * 
    * @return the order.
    */
   public String getOrder()
   {
      return order;
   }

   /**
    * Set the order.
    * 
    * @param order The order to set.
    */
   public void setOrder(String order)
   {
      this.order = order;
   }

   /**
    * Get the other.
    * 
    * @return the other.
    */
   public String getOther()
   {
      return other;
   }

   /**
    * Set the other.
    * 
    * @param other The other to set.
    */
   public void setOther(String other)
   {
      this.other = other;
   }

   /**
    * Get the select.
    * 
    * @return the select.
    */   
   public Select getSelect()
   {
      return select;
   }

   /**
    * Set the select.
    * 
    * @param select The select to set.
    */
   public void setSelect(Select select)
   {
      this.select = select;
   }

   /**
    * Get the where.
    * 
    * @return the where.
    */
   public String getWhere()
   {
      return where;
   }

   /**
    * Set the where.
    * 
    * @param where The where to set.
    */   
   public void setWhere(String where)
   {
      this.where = where;
   }

   public static class Select
   {
      private boolean distinct;
      private String ejbName;
      private String fieldName;
      private String alias;
      private String additionalColumns;
      
      /**
       * Get the additionalColumns.
       * 
       * @return the additionalColumns.
       */      
      public String getAdditionalColumns()
      {
         return additionalColumns;
      }
      
      /**
       * Set the additionalColumns.
       * 
       * @param additionalColumns The additionalColumns to set.
       */
      public void setAdditionalColumns(String additionalColumns)
      {
         this.additionalColumns = additionalColumns;
      }

      /**
       * Get the alias.
       * 
       * @return the alias.
       */
      public String getAlias()
      {
         return alias;
      }
      
      /**
       * Set the alias.
       * 
       * @param alias The alias to set.
       */
      public void setAlias(String alias)
      {
         this.alias = alias;
      }

      /**
       * Get the distinct.
       * 
       * @return the distinct.
       */
      public boolean isDistinct()
      {
         return distinct;
      }

      /**
       * Set the distinct.
       * 
       * @param distinct The distinct to set.
       */
      @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
      public void setDistinct(boolean distinct)
      {
         this.distinct = distinct;
      }
      
      /**
       * Get the ejbName.
       * 
       * @return the ejbName.
       */
      public String getEjbName()
      {
         return ejbName;
      }

      /**
       * Set the ejbName.
       * 
       * @param ejbName The ejbName to set.
       */
      public void setEjbName(String ejbName)
      {
         this.ejbName = ejbName;
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
   }
}
