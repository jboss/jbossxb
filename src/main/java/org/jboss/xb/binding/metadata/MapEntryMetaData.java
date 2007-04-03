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
public class MapEntryMetaData
{
   private String impl;
   private String getKeyMethod;
   private String setKeyMethod;
   private String getValueMethod;
   private String setValueMethod;
   private String valueType;
   private boolean nonNullValue;

   public String getImpl()
   {
      return impl;
   }

   public void setImpl(String impl)
   {
      this.impl = impl;
   }

   public String getGetKeyMethod()
   {
      return getKeyMethod;
   }

   public void setGetKeyMethod(String getKeyMethod)
   {
      this.getKeyMethod = getKeyMethod;
   }

   public String getSetKeyMethod()
   {
      return setKeyMethod;
   }

   public void setSetKeyMethod(String setKeyMethod)
   {
      this.setKeyMethod = setKeyMethod;
   }

   public String getGetValueMethod()
   {
      return getValueMethod;
   }

   public void setGetValueMethod(String getValueMethod)
   {
      this.getValueMethod = getValueMethod;
   }

   public String getSetValueMethod()
   {
      return setValueMethod;
   }

   public void setSetValueMethod(String setValueMethod)
   {
      this.setValueMethod = setValueMethod;
   }

   public String getValueType()
   {
      return valueType;
   }

   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }

   public boolean isNonNullValue()
   {
      return nonNullValue;
   }

   public void setNonNullValue(boolean nonNullValue)
   {
      this.nonNullValue = nonNullValue;
   }
}
