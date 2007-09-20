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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A JBossCMPEjbRelationshipRoleMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPEjbRelationshipRoleMetaData
{
   private String ejbRelationshipRoleName;
   private boolean fkConstraint;
   private List<JBossCMPKeyFieldMetaData> keyFields;
   private JBossCMPReadAheadMetaData readAhead;
   private boolean batchCascadeDelete;
   
   /**
    * Get the batchCascadeDelete.
    * 
    * @return the batchCascadeDelete.
    */
   public boolean isBatchCascadeDelete()
   {
      return batchCascadeDelete;
   }

   /**
    * Set the batchCascadeDelete.
    * 
    * @param batchCascadeDelete The batchCascadeDelete to set.
    */
   @XmlJavaTypeAdapter(value = EmptyElementToBooleanAdapter.class)
   public void setBatchCascadeDelete(boolean batchCascadeDelete)
   {
      this.batchCascadeDelete = batchCascadeDelete;
   }
   /**
    * Get the ejbRelationshipRoleName.
    * 
    * @return the ejbRelationshipRoleName.
    */
   public String getEjbRelationshipRoleName()
   {
      return ejbRelationshipRoleName;
   }
   
   /**
    * Set the ejbRelationshipRoleName.
    * 
    * @param ejbRelationshipRoleName The ejbRelationshipRoleName to set.
    */
   public void setEjbRelationshipRoleName(String ejbRelationshipRoleName)
   {
      this.ejbRelationshipRoleName = ejbRelationshipRoleName;
   }

   /**
    * Get the fkConstraint.
    * 
    * @return the fkConstraint.
    */
   public boolean isFkConstraint()
   {
      return fkConstraint;
   }

   /**
    * Set the fkConstraint.
    * 
    * @param fkConstraint The fkConstraint to set.
    */
   public void setFkConstraint(boolean fkConstraint)
   {
      this.fkConstraint = fkConstraint;
   }

   /**
    * Get the keyFields.
    * 
    * @return the keyFields.
    */
   public List<JBossCMPKeyFieldMetaData> getKeyFields()
   {
      return keyFields;
   }
   
   /**
    * Set the keyFields.
    * 
    * @param keyFields The keyFields to set.
    */
   @XmlElementWrapper
   @XmlElement(name="key-field")
   public void setKeyFields(List<JBossCMPKeyFieldMetaData> keyFields)
   {
      this.keyFields = keyFields;
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
