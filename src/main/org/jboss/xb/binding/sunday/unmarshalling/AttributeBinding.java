/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBinding
{
   private final SchemaBinding schema;
   private final TypeBinding type;
   private AttributeHandler handler;
   private PropertyMetaData propertyMetaData;
   private ValueMetaData valueMetaData;
   /** The default="value" constraint for the attribute */
   private String defaultConstraint;
   private boolean mapEntryKey;
   private boolean mapEntryValue;

   public AttributeBinding(SchemaBinding schema, TypeBinding type, AttributeHandler handler)
   {
      this.schema = schema;
      this.type = type;
      this.handler = handler;
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
      return valueMetaData;
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
}
