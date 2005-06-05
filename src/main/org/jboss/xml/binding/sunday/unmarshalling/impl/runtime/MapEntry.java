/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import org.jboss.xml.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MapEntry
   extends AbstractMap
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

   public Object put(Object key, Object value)
   {
      Map map;
      if(this.value == null)
      {
         map = new HashMap();
         this.value = map;
      }
      else if(this.value instanceof Map)
      {
         map = (Map)this.value;
      }
      else
      {
         throw new JBossXBRuntimeException("Map entry value is not an instance of java.util.Map as expected: entry=" +
            toString() /*(value == null ? "null" : "class=" + value.getClass().getName() + ", value=" + value)*/
         );
      }

      return map.put(key, value);
   }

   public Set entrySet()
   {
      Map map;
      if(value == null)
      {
         map = new HashMap();
         value = map;
      }
      else if(value instanceof Map)
      {
         map = (Map)value;
      }
      else
      {
         throw new JBossXBRuntimeException("Map entry value is not an instance of java.util.Map as expected: " +
            toString() /*(value == null ? "null" : "class=" + value.getClass().getName() + ", value=" + value)*/
         );
      }

      return map.entrySet();
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
