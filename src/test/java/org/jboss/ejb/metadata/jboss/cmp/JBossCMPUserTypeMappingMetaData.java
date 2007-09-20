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


/**
 * A JBossCMPUserTypeMappingsMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPUserTypeMappingMetaData
{
   private String javaType;
   private String mappedType;
   private String mapper;
   private boolean checkDirtyAfterGet;
   private String stateFactory;
   
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
    * Get the mappedType.
    * 
    * @return the mappedType.
    */
   public String getMappedType()
   {
      return mappedType;
   }

   /**
    * Set the mappedType.
    * 
    * @param mappedType The mappedType to set.
    */
   public void setMappedType(String mappedType)
   {
      this.mappedType = mappedType;
   }

   /**
    * Get the mapper.
    * 
    * @return the mapper.
    */
   public String getMapper()
   {
      return mapper;
   }

   /**
    * Set the mapper.
    * 
    * @param mapper The mapper to set.
    */
   public void setMapper(String mapper)
   {
      this.mapper = mapper;
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
