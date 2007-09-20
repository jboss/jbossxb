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

import org.jboss.ejb.metadata.spec.CMPFieldMetaData;
import org.jboss.ejb.metadata.spec.CMPFieldsMetaData;
import org.jboss.javaee.metadata.support.AbstractMappedMetaDataWithOverride;
import org.jboss.javaee.metadata.support.JavaEEMetaDataUtil;


/**
 * A JBossCMPFieldsMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossCMPFieldsMetaData
   extends AbstractMappedMetaDataWithOverride<CMPFieldMetaData, JBossCMPFieldMetaData, CMPFieldsMetaData>
{
   public JBossCMPFieldsMetaData()
   {
      super("field name");
   }

   public static JBossCMPFieldsMetaData merge(JBossCMPFieldsMetaData jbossCMPFieldsMetaData, CMPFieldsMetaData cmpFieldsMetaData)
   {
      if(cmpFieldsMetaData == null)
         return null;
      
      JBossCMPFieldsMetaData merged = new JBossCMPFieldsMetaData();
      return JavaEEMetaDataUtil.mergeOverrideJBossXml(merged, cmpFieldsMetaData, jbossCMPFieldsMetaData, "cmp field", true);
   }

   public JBossCMPFieldMetaData createOverride(CMPFieldMetaData data)
   {
      if (data == null)
         throw new IllegalArgumentException("Null data");
      if (!(data instanceof CMPFieldMetaData))
         return null;

      JBossCMPFieldMetaData result = new JBossCMPFieldMetaData();
      result.setFieldName(data.getFieldName());
      result.setOverridenMetaData(data);
      return result;
   }
}
