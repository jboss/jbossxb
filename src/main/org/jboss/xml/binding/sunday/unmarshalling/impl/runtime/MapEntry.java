/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

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
      this.key = key;
   }

   public Object getValue()
   {
      return value;
   }

   public void setValue(Object value)
   {
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
}
