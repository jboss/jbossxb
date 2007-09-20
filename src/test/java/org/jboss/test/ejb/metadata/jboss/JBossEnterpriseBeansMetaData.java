/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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
package org.jboss.ejb.metadata.jboss;

import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeansMetaData;
import org.jboss.ejb.metadata.spec.EntityBeanMetaData;
import org.jboss.ejb.metadata.spec.MessageDrivenBeanMetaData;
import org.jboss.ejb.metadata.spec.SessionBeanMetaData;
import org.jboss.javaee.metadata.support.AbstractMappedMetaDataWithOverride;
import org.jboss.javaee.metadata.support.JavaEEMetaDataUtil;
import javax.xml.bind.annotation.XmlType;

/**
 * JBossEnterpriseBeansMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jboss-enterprise-beansType")
public class JBossEnterpriseBeansMetaData extends AbstractMappedMetaDataWithOverride<EnterpriseBeanMetaData, JBossEnterpriseBeanMetaData, EnterpriseBeansMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -5123700601271986251L;
   
   /** The top level metadata */
   private JBossMetaData jbossMetaData;
   
   /**
    * Create a new EnterpriseBeansMetaData.
    */
   public JBossEnterpriseBeansMetaData()
   {
      super("ejb name for enterprise bean");
   }
   
   /**
    * Merge
    * 
    * @param jbossEnterpriseBeansMetaData the jboss enterprise beans
    * @param enterpriseBeansMetaData the enterprise beans
    * @return the merged jboss enterprise beans
    */
   public static JBossEnterpriseBeansMetaData merge(JBossEnterpriseBeansMetaData jbossEnterpriseBeansMetaData, EnterpriseBeansMetaData enterpriseBeansMetaData)
   {
      JBossEnterpriseBeansMetaData merged = new JBossEnterpriseBeansMetaData();
      return JavaEEMetaDataUtil.mergeOverrideJBossXml(merged, enterpriseBeansMetaData, jbossEnterpriseBeansMetaData, "enterprise bean", true);
   }

   public JBossEnterpriseBeanMetaData createOverride(EnterpriseBeanMetaData data)
   {
      if (data == null)
         throw new IllegalArgumentException("Null data");
      JBossEnterpriseBeanMetaData result = null;
      if (data instanceof SessionBeanMetaData)
         result = new JBossSessionBeanMetaData();
      else if (data instanceof EntityBeanMetaData)
         result = new JBossEntityBeanMetaData();
      else if (data instanceof MessageDrivenBeanMetaData)
         result = new JBossMessageDrivenBeanMetaData();
      else
         throw new IllegalArgumentException("Unregonised: " + data);
      result.setEjbName(data.getEjbName());
      result.setOverridenMetaData(data);
      return result;
   }

   /**
    * Get the jbossMetaData.
    * 
    * @return the jbossMetaData.
    */
   JBossMetaData getJBossMetaData()
   {
      return jbossMetaData;
   }

   /**
    * Set the jbossMetaData.
    * 
    * @param jbossMetaData the jbossMetaData.
    * @throws IllegalArgumentException for a null jbossMetaData
    */
   void setJBossMetaData(JBossMetaData jbossMetaData)
   {
      if (jbossMetaData == null)
         throw new IllegalArgumentException("Null jbossMetaData");
      this.jbossMetaData = jbossMetaData;
   }

   @Override
   public void addNotification(JBossEnterpriseBeanMetaData added)
   {
      added.setEnterpriseBeansMetaData(this);
   }
   
   @Override
   public void removeNotification(JBossEnterpriseBeanMetaData removed)
   {
      removed.setEnterpriseBeansMetaData(null);
   }
}
