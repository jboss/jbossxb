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


/**
 * A ReadAheadMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPReadAheadMetaData
{
   private String strategy;
   private long pageSize;
   private String eagerLoadGroup;
   private List<JBossCMPLeftJoinMetaData> leftJoins;
   
   /**
    * Get the eagerLoadGroup.
    * 
    * @return the eagerLoadGroup.
    */
   
   public String getEagerLoadGroup()
   {
      return eagerLoadGroup;
   }
   /**
    * Set the eagerLoadGroup.
    * 
    * @param eagerLoadGroup The eagerLoadGroup to set.
    */
   
   public void setEagerLoadGroup(String eagerLoadGroup)
   {
      this.eagerLoadGroup = eagerLoadGroup;
   }
   /**
    * Get the leftJoins.
    * 
    * @return the leftJoins.
    */
   
   public List<JBossCMPLeftJoinMetaData> getLeftJoins()
   {
      return leftJoins;
   }
   /**
    * Set the leftJoins.
    * 
    * @param leftJoins The leftJoins to set.
    */
   
   public void setLeftJoins(List<JBossCMPLeftJoinMetaData> leftJoins)
   {
      this.leftJoins = leftJoins;
   }
   /**
    * Get the pageSize.
    * 
    * @return the pageSize.
    */
   
   public long getPageSize()
   {
      return pageSize;
   }
   /**
    * Set the pageSize.
    * 
    * @param pageSize The pageSize to set.
    */
   
   public void setPageSize(long pageSize)
   {
      this.pageSize = pageSize;
   }
   /**
    * Get the strategy.
    * 
    * @return the strategy.
    */
   
   public String getStrategy()
   {
      return strategy;
   }
   /**
    * Set the strategy.
    * 
    * @param strategy The strategy to set.
    */
   
   public void setStrategy(String strategy)
   {
      this.strategy = strategy;
   }
}
