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

import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MapEntry
{
   private Object key;
   private Object value;

   public Object getKey()
   {
      return key;
   }

   public void setKey(Object key)
   {
      if(this.key != null)
      {
         throw new JBossXBRuntimeException(
            "The key is already set: current value=" + this.key + ", overriding value=" + key
         );
      }
      this.key = key;
   }

   public Object getValue()
   {
      return value;
   }

   public void setValue(Object value)
   {
      if(this.value != null)
      {
         throw new JBossXBRuntimeException(
            "The value is already set: current value=" + this.value + ", overriding value=" + value
         );
      }
      this.value = value;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof MapEntry))
      {
         return false;
      }

      final MapEntry mapEntry = (MapEntry)o;

      if(key != null ? !key.equals(mapEntry.key) : mapEntry.key != null)
      {
         return false;
      }
      if(value != null ? !value.equals(mapEntry.value) : mapEntry.value != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (key != null ? key.hashCode() : 0);
      result = 29 * result + (value != null ? value.hashCode() : 0);
      return result;
   }

   public String toString()
   {
      return "[map-entry key=" + key + ", value=" + value + "]";
   }
}
