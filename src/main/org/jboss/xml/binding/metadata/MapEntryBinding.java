/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import java.util.Map;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MapEntryBinding
   implements JavaValueBinding
{
   public Object newInstance()
   {
      return new KeyValuePair();
   }

   public Object get(Object owner, String name)
   {
      return ((Map)owner).get(name);
   }

   public void set(Object owner, Object value, String name)
   {
      KeyValuePair entry = (KeyValuePair)value;
      ((Map)owner).put(entry.key, entry.value);
   }

   // Inner

   static class KeyValuePair
   {
      Object key;
      Object value;

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof KeyValuePair))
         {
            return false;
         }

         final KeyValuePair keyValuePair = (KeyValuePair)o;

         if(key != null ? !key.equals(keyValuePair.key) : keyValuePair.key != null)
         {
            return false;
         }
         if(value != null ? !value.equals(keyValuePair.value) : keyValuePair.value != null)
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
}
