/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling.impl.runtime;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.GenericValueContainer;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtAttributeHandler
   extends AttributeHandler
{
   public static final RtAttributeHandler INSTANCE = new RtAttributeHandler();

   public void attribute(QName elemName, QName attrName, AttributeBinding binding, Object owner, Object value)
   {
      if(owner instanceof MapEntry)
      {
         if(binding.isMapEntryKey())
         {
            ((MapEntry)owner).setKey(value);
         }
         else if(binding.isMapEntryValue())
         {
            ((MapEntry)owner).setValue(value);
         }
         else
         {
            throw new JBossXBRuntimeException(
               "Parent object is a map entry but attribute " +
               attrName +
               " in element " +
               elemName +
               " bound to neither key nor value in a map entry."
            );
         }
      }
      else if(owner instanceof GenericValueContainer)
      {
         ((GenericValueContainer)owner).addChild(attrName, value);         
      }
      else
      {
         String property = null;
         PropertyMetaData propertyMetaData = binding.getPropertyMetaData();
         if(propertyMetaData != null)
         {
            property = propertyMetaData.getName();
         }

         if(property == null)
         {
            property = Util.xmlNameToFieldName(attrName.getLocalPart(), binding.getSchema().isIgnoreLowLine());
         }

         RtUtil.set(owner, value, property, null,
            binding.getSchema().isIgnoreUnresolvedFieldOrClass(),
            binding.getValueAdapter());
      }
   }
}
