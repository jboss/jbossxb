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

import javax.xml.bind.annotation.XmlType;

import org.jboss.ejb.metadata.spec.EnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.EnterpriseBeansMetaData;
import org.jboss.ejb.metadata.spec.EntityBeanMetaData;
import org.jboss.javaee.metadata.support.AbstractMappedMetaDataWithOverride;
import org.jboss.javaee.metadata.support.JavaEEMetaDataUtil;


/**
 * A JBossCMPEnterpriseBeansMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="jbosscmp-enterprise-beansType")
public class JBossCMPEnterpriseBeansMetaData extends AbstractMappedMetaDataWithOverride<EnterpriseBeanMetaData, JBossCMPEntityBeanMetaData, EnterpriseBeansMetaData>
{
   public JBossCMPEnterpriseBeansMetaData()
   {
      super("ejb name for enterprise bean");
   }

   public static JBossCMPEnterpriseBeansMetaData merge(JBossCMPEnterpriseBeansMetaData jbossCMPEnterpriseBeansMetaData, EnterpriseBeansMetaData enterpriseBeansMetaData)
   {
      JBossCMPEnterpriseBeansMetaData merged = new JBossCMPEnterpriseBeansMetaData();
      return JavaEEMetaDataUtil.mergeOverrideJBossXml(merged, enterpriseBeansMetaData, jbossCMPEnterpriseBeansMetaData, "enterprise bean", true);
   }

   public JBossCMPEntityBeanMetaData createOverride(EnterpriseBeanMetaData data)
   {
      if (data == null)
         throw new IllegalArgumentException("Null data");
      if (!(data instanceof EntityBeanMetaData))
         return null;

      JBossCMPEntityBeanMetaData result = new JBossCMPEntityBeanMetaData();
      result.setEjbName(data.getEjbName());
      result.setOverridenMetaData(data);
      return result;
   }
}
