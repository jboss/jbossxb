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
