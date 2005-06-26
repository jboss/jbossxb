/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class PutMethodMetaData
{
   private String methodName;
   private String keyType;
   private String valueType;

   public String getName()
   {
      return methodName;
   }

   public void setName(String methodName)
   {
      this.methodName = methodName;
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

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof PutMethodMetaData))
      {
         return false;
      }

      final PutMethodMetaData putMethodMetaData = (PutMethodMetaData)o;

      if(keyType != null ? !keyType.equals(putMethodMetaData.keyType) : putMethodMetaData.keyType != null)
      {
         return false;
      }
      if(methodName != null ? !methodName.equals(putMethodMetaData.methodName) : putMethodMetaData.methodName != null)
      {
         return false;
      }
      if(valueType != null ? !valueType.equals(putMethodMetaData.valueType) : putMethodMetaData.valueType != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (methodName != null ? methodName.hashCode() : 0);
      result = 29 * result + (keyType != null ? keyType.hashCode() : 0);
      result = 29 * result + (valueType != null ? valueType.hashCode() : 0);
      return result;
   }
}
