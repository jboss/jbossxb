/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class CharactersMetaData
{
   private PropertyMetaData property;
   private ValueMetaData value;
   private boolean mapEntryValue;
   private boolean mapEntryKey;

   public PropertyMetaData getProperty()
   {
      return property;
   }

   public void setProperty(PropertyMetaData property)
   {
      this.property = property;
   }

   public ValueMetaData getValue()
   {
      return value;
   }

   public void setValue(ValueMetaData value)
   {
      this.value = value;
   }

   public boolean isMapEntryValue()
   {
      return mapEntryValue;
   }

   public void setMapEntryValue(boolean mapEntryValue)
   {
      this.mapEntryValue = mapEntryValue;
   }

   public boolean isMapEntryKey()
   {
      return mapEntryKey;
   }

   public void setMapEntryKey(boolean mapEntryKey)
   {
      this.mapEntryKey = mapEntryKey;
   }
}
