/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.binding.sunday.unmarshalling.impl.runtime;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.GenericValueContainer;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.group.ValueList;
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
      else if(owner instanceof ValueList)
      {
         ValueList valueList = (ValueList)owner;
         valueList.getInitializer().addAttributeValue(attrName, binding, valueList, value);
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
