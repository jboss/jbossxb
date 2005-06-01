/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MapEntryMetaData
{
   private String putMethod;
   private String keyType;
   private String valueType;
   private String keyMethod;

   public String getPutMethod()
   {
      return putMethod;
   }

   public void setPutMethod(String putMethod)
   {
      this.putMethod = putMethod;
   }

   public String getKeyMethod()
   {
      return keyMethod;
   }

   public void setKeyMethod(String keyMethod)
   {
      this.keyMethod = keyMethod;
   }

   public String getKeyType()
   {
      return keyType;
   }

   public void setKeyType(String keyType)
   {
      this.keyType = keyType;
   }

   public String getValueType()
   {
      return valueType;
   }

   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }
}
