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
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.jboss.ejb.metadata.jboss.JBossMetaData;
import org.jboss.ejb.metadata.spec.EjbJarMetaData;
import org.jboss.javaee.metadata.spec.JavaEEMetaDataConstants;
import org.jboss.javaee.metadata.support.IdMetaDataImplWithDescriptionGroupWithOverride;
import org.jboss.logging.Logger;
import org.jboss.xb.annotations.JBossXmlSchema;


/**
 * A JBossCMPJDBCMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="jbosscmp-jdbc", namespace=JavaEEMetaDataConstants.JBOSS_CMP2X_NS)
@JBossXmlSchema(
      xmlns={@XmlNs(namespaceURI = JavaEEMetaDataConstants.JAVAEE_NS, prefix = "jee")},
      ignoreUnresolvedFieldOrClass=false,
      namespace=JavaEEMetaDataConstants.JBOSS_CMP2X_NS,
      elementFormDefault=XmlNsForm.QUALIFIED)
@XmlType(name="jbosscmp-jdbcType", namespace=JavaEEMetaDataConstants.JBOSS_CMP2X_NS)
public class JBossCMPMetaData extends IdMetaDataImplWithDescriptionGroupWithOverride<EjbJarMetaData>
{
   /** The log */
   private static final Logger log = Logger.getLogger(JBossMetaData.class);

   private JBossCMPDefaultsMetaData defaults;
   private JBossCMPEnterpriseBeansMetaData enterpriseBeans;
   private JBossCMPEnterpriseBeansMetaData mergedEnterpriseBeans;
   private JBossCMPRelationshipsMetaData relationships;
   private List<JBossCMPDependentValueClassMetaData> dependentValueClasses;
   private List<JBossCMPTypeMappingMetaData> typeMappings;
   private List<JBossCMPEntityCommandMetaData> entityCommands;
   private List<JBossCMPUserTypeMappingMetaData> userTypeMappings;
   private List<String> reservedWords;

   public JBossCMPMetaData()
   {
      // for serialization
   }

   /**
    * Get the defaults.
    * 
    * @return the defaults.
    */
   
   public JBossCMPDefaultsMetaData getDefaults()
   {
      return defaults;
   }

   /**
    * Set the defaults.
    * 
    * @param defaults The defaults to set.
    */
   
   public void setDefaults(JBossCMPDefaultsMetaData defaults)
   {
      this.defaults = defaults;
   }

   /**
    * Get the dependentValueClasses.
    * 
    * @return the dependentValueClasses.
    */   
   public List<JBossCMPDependentValueClassMetaData> getDependentValueClasses()
   {
      return dependentValueClasses;
   }

   /**
    * Set the dependentValueClasses.
    * 
    * @param dependentValueClasses The dependentValueClasses to set.
    */
   @XmlElementWrapper
   @XmlElement(name="dependent-value-class")
   public void setDependentValueClasses(List<JBossCMPDependentValueClassMetaData> dependentValueClasses)
   {
      this.dependentValueClasses = dependentValueClasses;
   }

   /**
    * Get the enterpriseBeans.
    * 
    * @return the enterpriseBeans.
    */
   public JBossCMPEnterpriseBeansMetaData getEnterpriseBeans()
   {
      return enterpriseBeans;
   }

   /**
    * Set the enterpriseBeans.
    * 
    * @param enterpriseBeans The enterpriseBeans to set.
    */
   public void setEnterpriseBeans(JBossCMPEnterpriseBeansMetaData enterpriseBeans)
   {
      this.enterpriseBeans = enterpriseBeans;
   }

   @XmlTransient
   public JBossCMPEnterpriseBeansMetaData getMergedEnterpriseBeans()
   {
      if(mergedEnterpriseBeans != null)
         return mergedEnterpriseBeans;
      mergedEnterpriseBeans = JBossCMPEnterpriseBeansMetaData.merge(enterpriseBeans, getOverridenMetaDataWithCheck().getEnterpriseBeans());
      return mergedEnterpriseBeans;
   }

   /**
    * Get a merged enterprise bean
    * 
    * @param name the name
    * @return the container configuration
    */
   @XmlTransient
   public JBossCMPEntityBeanMetaData getMergedEnterpriseBean(String name)
   {
      return getMergedEnterpriseBeans().get(name);
   }

   /**
    * Get the entityCommands.
    * 
    * @return the entityCommands.
    */   
   public List<JBossCMPEntityCommandMetaData> getEntityCommands()
   {
      return entityCommands;
   }

   /**
    * Set the entityCommands.
    * 
    * @param entityCommands The entityCommands to set.
    */
   @XmlElementWrapper
   @XmlElement(name="entity-command")
   public void setEntityCommands(List<JBossCMPEntityCommandMetaData> entityCommands)
   {
      this.entityCommands = entityCommands;
   }

   /**
    * Get the relationships.
    * 
    * @return the relationships.
    */
   public JBossCMPRelationshipsMetaData getRelationships()
   {
      return relationships;
   }

   /**
    * Set the relationships.
    * 
    * @param relationships The relationships to set.
    */
   public void setRelationships(JBossCMPRelationshipsMetaData relationships)
   {
      this.relationships = relationships;
   }

   /**
    * Get the reservedWords.
    * 
    * @return the reservedWords.
    */
   public List<String> getReservedWords()
   {
      return reservedWords;
   }

   /**
    * Set the reservedWords.
    * 
    * @param reservedWords The reservedWords to set.
    */
   @XmlElementWrapper
   @XmlElement(name="word")
   public void setReservedWords(List<String> reservedWords)
   {
      this.reservedWords = reservedWords;
   }

   /**
    * Get the typeMappings.
    * 
    * @return the typeMappings.
    */
   
   public List<JBossCMPTypeMappingMetaData> getTypeMappings()
   {
      return typeMappings;
   }

   /**
    * Set the typeMappings.
    * 
    * @param typeMappings The typeMappings to set.
    */
   @XmlElementWrapper
   @XmlElement(name="type-mapping")
   public void setTypeMappings(List<JBossCMPTypeMappingMetaData> typeMappings)
   {
      this.typeMappings = typeMappings;
   }

   /**
    * Get the userTypeMappings.
    * 
    * @return the userTypeMappings.
    */
   public List<JBossCMPUserTypeMappingMetaData> getUserTypeMappings()
   {
      return userTypeMappings;
   }

   /**
    * Set the userTypeMappings.
    * 
    * @param userTypeMappings The userTypeMappings to set.
    */
   @XmlElementWrapper
   @XmlElement(name="user-type-mapping")
   public void setUserTypeMappings(List<JBossCMPUserTypeMappingMetaData> userTypeMappings)
   {
      this.userTypeMappings = userTypeMappings;
   }
}
