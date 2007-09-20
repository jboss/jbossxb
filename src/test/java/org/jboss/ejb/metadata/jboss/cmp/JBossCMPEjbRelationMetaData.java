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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.ejb.metadata.spec.RelationMetaData;
import org.jboss.javaee.metadata.support.NamedMetaDataWithDescriptionGroupWithOverride;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;


/**
 * A JBossCMPEjbRelationMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(
      kind=JBossXmlConstants.MODEL_GROUP_CHOICE,
      particles={@JBossXmlModelGroup.Particle(element=@XmlElement(name="ejb-relation"), type=JBossCMPEjbRelationMetaData.class)})
public class JBossCMPEjbRelationMetaData
   extends NamedMetaDataWithDescriptionGroupWithOverride<RelationMetaData>
{
   private boolean readOnly;
   private long readTimeOut;
   private boolean foreignKeyMapping;
   private JBossCMPRelationTableMappingMetaData relationTableMapping;
   private JBossCMPEjbRelationshipRoleMetaData leftRole;
   private JBossCMPEjbRelationshipRoleMetaData rightRole;
   
   public String getEjbRelationName()
   {
      return getName();
   }
   
   public void setEjbRelationName(String ejbRelationName)
   {
      setName(ejbRelationName);
   }
   
   public boolean isForeignKeyMapping()
   {
      return foreignKeyMapping;
   }
   
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
   public void setForeignKeyMapping(boolean foreignKeyMapping)
   {
      this.foreignKeyMapping = foreignKeyMapping;
   }
   
   public boolean isReadOnly()
   {
      return readOnly;
   }
   
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }
   
   public long getReadTimeOut()
   {
      return readTimeOut;
   }
   
   public void setReadTimeOut(long readTimeOut)
   {
      this.readTimeOut = readTimeOut;
   }
   
   public JBossCMPRelationTableMappingMetaData getRelationTableMapping()
   {
      return relationTableMapping;
   }
   
   public void setRelationTableMapping(JBossCMPRelationTableMappingMetaData relationTableMapping)
   {
      this.relationTableMapping = relationTableMapping;
   }

   /**
    * Get the leftRole.
    * 
    * @return the leftRole.
    */   
   public JBossCMPEjbRelationshipRoleMetaData getLeftRole()
   {
      return leftRole;
   }

   /**
    * Set the leftRole.
    * 
    * @param leftRole The leftRole to set.
    */
   @XmlTransient
   public void setLeftRole(JBossCMPEjbRelationshipRoleMetaData leftRole)
   {
      this.leftRole = leftRole;
   }

   /**
    * Get the rightRole.
    * 
    * @return the rightRole.
    */
   public JBossCMPEjbRelationshipRoleMetaData getRightRole()
   {
      return rightRole;
   }

   /**
    * Set the rightRole.
    * 
    * @param rightRole The rightRole to set.
    */
   @XmlTransient
   public void setRightRole(JBossCMPEjbRelationshipRoleMetaData rightRole)
   {
      this.rightRole = rightRole;
   }

   public List<JBossCMPEjbRelationshipRoleMetaData> getEjbRelationshipRoles()
   {
      return new java.util.AbstractList<JBossCMPEjbRelationshipRoleMetaData>()
      {
         @Override
         public JBossCMPEjbRelationshipRoleMetaData get(int index)
         {
            if(index > size())
            {
               throw new IllegalArgumentException("Index must be less then " + size() + ": " + index);
            }
            return index == 0 ? leftRole : rightRole;
         }

         @Override
         public int size()
         {
            return leftRole == null ? (rightRole == null ? 0 : 1) : 2;
         }
         
         @Override
         public boolean add(JBossCMPEjbRelationshipRoleMetaData o)
         {
            if(leftRole == null)
            {
               setLeftRole(o);
            }
            else if(rightRole == null)
            {
               setRightRole(o);
            }
            else
            {
               throw new IllegalStateException("Too many roles: " + o);
            }
            return true;
         }
      };
   }
   
   /**
    * Set the relation role metadata<p>
    * 
    * On first invocation it sets the left role,
    * on second invocation it sets the right role,
    * after that it throws an IllegalStateException
    * 
    * @param roleMetaData
    * @throws IllegalArgumentException for a null role metadata
    * @throws IllegalStateException for too many roles
    */
   @XmlElement(name="ejb-relationship-role")
   public void setEjbRelationshipRoles(List<JBossCMPEjbRelationshipRoleMetaData> roleMetaData)
   {
      throw new UnsupportedOperationException("this list shouldn't be set");
   }
}
