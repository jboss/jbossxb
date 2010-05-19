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
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.marshalling.AttributeMarshaller;
import org.jboss.xb.binding.sunday.marshalling.DefaultAttributeMarshaller;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBinding
{
   private final SchemaBinding schema;
   private final QName qName;
   private final TypeBinding type;
   private AttributeHandler handler;
   private AttributeMarshaller marshaller = DefaultAttributeMarshaller.INSTANCE;
   private PropertyMetaData propertyMetaData;
   private ValueMetaData valueMetaData;
   /** The default="value" constraint for the attribute */
   private String defaultConstraint;
   private boolean mapEntryKey;
   private boolean mapEntryValue;
   private ValueAdapter valueAdapter;
   private boolean required;
   protected Boolean normalizeSpace;

   public AttributeBinding(SchemaBinding schema, QName qName, TypeBinding type, AttributeHandler handler)
   {
      if(qName == null)
      {
         throw new JBossXBRuntimeException("Each attribute should have a non-null QName!");
      }

      if(type == null || !type.isSimple() && type.getValueAdapter() == ValueAdapter.NOOP)
         throw new JBossXBRuntimeException("The type of the attribute " + qName +
            " must be simple or complex with a value adapter: " + type);
      
      this.schema = schema;
      this.qName = qName;
      this.type = type;
      this.handler = handler;
   }

   public QName getQName()
   {
      return qName;
   }

   public TypeBinding getType()
   {
      return type;
   }

   public AttributeHandler getHandler()
   {
      return handler;
   }

   public void setHandler(AttributeHandler handler)
   {
      this.handler = handler;
   }

   public AttributeMarshaller getMarshaller()
   {
      return marshaller;
   }

   public void setMarshaller(AttributeMarshaller marshaller)
   {
      this.marshaller = marshaller;
   }

   public PropertyMetaData getPropertyMetaData()
   {
      return propertyMetaData;
   }

   public void setPropertyMetaData(PropertyMetaData propertyMetaData)
   {
      this.propertyMetaData = propertyMetaData;
   }

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData != null ? valueMetaData : type.getValueMetaData();
   }

   public void setValueMetaData(ValueMetaData valueMetaData)
   {
      this.valueMetaData = valueMetaData;
   }

   public void setMapEntryKey(boolean mapEntryKey)
   {
      this.mapEntryKey = mapEntryKey;
   }

   public boolean isMapEntryKey()
   {
      return mapEntryKey;
   }

   public boolean isMapEntryValue()
   {
      return mapEntryValue;
   }

   public void setMapEntryValue(boolean mapEntryValue)
   {
      this.mapEntryValue = mapEntryValue;
   }

   public String getDefaultConstraint()
   {
      return defaultConstraint;
   }

   public void setDefaultConstraint(String value)
   {
      defaultConstraint = value;
   }

   public SchemaBinding getSchema()
   {
      return schema;
   }

   public ValueAdapter getValueAdapter()
   {
      return valueAdapter == null ? type.getValueAdapter() : valueAdapter;
   }

   public void setValueAdapter(ValueAdapter valueAdapter)
   {
      this.valueAdapter = valueAdapter;
   }

   public String toString()
   {
      return super.toString() + "[" + qName + "]";
   }

   public void setRequired(boolean required)
   {
      this.required = required;
   }

   public boolean getRequired()
   {
      return required;
   }

   public void setNormalizeSpace(Boolean value)
   {
      this.normalizeSpace = value;
   }
   
   public boolean isNormalizeSpace()
   {
      if(normalizeSpace != null)
         return normalizeSpace.booleanValue();      
      return schema == null ? true : schema.isNormalizeSpace();
   }
}
