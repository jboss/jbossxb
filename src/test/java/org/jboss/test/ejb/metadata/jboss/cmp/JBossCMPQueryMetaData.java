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
 * A JBossCMPQueryMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPQueryMetaData
{
   private String description;
   private JBossCMPQueryMethodMetaData queryMethod;
   private String jbossQl;
   private boolean dynamicQl;
   private JBossCMPDeclaredSqlMetaData declaredSql;
   private JBossCMPReadAheadMetaData readAhead;
   private String qlCompiler;
   private boolean lazyResultsetLoading;
   
   /**
    * Get the declaredSql.
    * 
    * @return the declaredSql.
    */   
   public JBossCMPDeclaredSqlMetaData getDeclaredSql()
   {
      return declaredSql;
   }
   /**
    * Set the declaredSql.
    * 
    * @param declaredSql The declaredSql to set.
    */
   
   public void setDeclaredSql(JBossCMPDeclaredSqlMetaData declaredSql)
   {
      this.declaredSql = declaredSql;
   }
   
   /**
    * Get the description.
    * 
    * @return the description.
    */   
   public String getDescription()
   {
      return description;
   }
   
   /**
    * Set the description.
    * 
    * @param description The description to set.
    */   
   public void setDescription(String description)
   {
      this.description = description;
   }
   
   /**
    * Get the dynamicQl.
    * 
    * @return the dynamicQl.
    */
   public boolean isDynamicQl()
   {
      return dynamicQl;
   }
   
   /**
    * Set the dynamicQl.
    * 
    * @param dynamicQl The dynamicQl to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
   public void setDynamicQl(boolean dynamicQl)
   {
      this.dynamicQl = dynamicQl;
   }
   
   /**
    * Get the jbossQl.
    * 
    * @return the jbossQl.
    */   
   public String getJBossQl()
   {
      return jbossQl;
   }
   
   /**
    * Set the jbossQl.
    * 
    * @param jbossQl The jbossQl to set.
    */   
   public void setJBossQl(String jbossQl)
   {
      this.jbossQl = jbossQl;
   }
   
   /**
    * Get the lazyResultsetLoading.
    * 
    * @return the lazyResultsetLoading.
    */
   
   public boolean isLazyResultsetLoading()
   {
      return lazyResultsetLoading;
   }
   /**
    * Set the lazyResultsetLoading.
    * 
    * @param lazyResultsetLoading The lazyResultsetLoading to set.
    */
   
   public void setLazyResultsetLoading(boolean lazyResultsetLoading)
   {
      this.lazyResultsetLoading = lazyResultsetLoading;
   }
   /**
    * Get the qlCompiler.
    * 
    * @return the qlCompiler.
    */
   
   public String getQlCompiler()
   {
      return qlCompiler;
   }
   /**
    * Set the qlCompiler.
    * 
    * @param qlCompiler The qlCompiler to set.
    */
   
   public void setQlCompiler(String qlCompiler)
   {
      this.qlCompiler = qlCompiler;
   }
   /**
    * Get the queryMethod.
    * 
    * @return the queryMethod.
    */
   
   public JBossCMPQueryMethodMetaData getQueryMethod()
   {
      return queryMethod;
   }
   /**
    * Set the queryMethod.
    * 
    * @param queryMethod The queryMethod to set.
    */
   
   public void setQueryMethod(JBossCMPQueryMethodMetaData queryMethod)
   {
      this.queryMethod = queryMethod;
   }
   /**
    * Get the readAhead.
    * 
    * @return the readAhead.
    */
   
   public JBossCMPReadAheadMetaData getReadAhead()
   {
      return readAhead;
   }
   /**
    * Set the readAhead.
    * 
    * @param readAhead The readAhead to set.
    */
   
   public void setReadAhead(JBossCMPReadAheadMetaData readAhead)
   {
      this.readAhead = readAhead;
   }
}
