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
package org.jboss.test.xml.pojoserver.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map metadata.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class AbstractMapMetaData extends AbstractTypeMetaData implements Map
{
   /** The map */
   private HashMap map = new HashMap();

   /** The key type */
   protected String keyType;

   /** The value type */
   protected String valueType;

   /**
    * Create a new map value
    */
   public AbstractMapMetaData()
   {
   }

   /**
    * Get the key type
    * 
    * @return the key type
    */
   public String getKeyType()
   {
      return keyType;
   }

   /**
    * Set the key type
    * 
    * @param keyType the key type
    */
   public void setKeyType(String keyType)
   {
      this.keyType = keyType;
   }

   /**
    * Get the value type
    * 
    * @return the value type
    */
   public String getValueType()
   {
      return valueType;
   }

   /**
    * Set the value type
    * 
    * @param valueType the value type
    */
   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }

   public void clear()
   {
      map.clear();
   }

   public boolean containsKey(Object key)
   {
      return map.containsKey(key);
   }

   public boolean containsValue(Object value)
   {
      return map.containsValue(value);
   }

   public Set entrySet()
   {
      return map.entrySet();
   }

   public Object get(Object key)
   {
      return map.get(key);
   }

   public boolean isEmpty()
   {
      return map.isEmpty();
   }

   public Set keySet()
   {
      return map.keySet();
   }

   public Object put(Object key, Object value)
   {
      return map.put(key, value);
   }

   public void putAll(Map t)
   {
      map.putAll(t);
   }

   public Object remove(Object key)
   {
      return map.remove(key);
   }

   public int size()
   {
      return map.size();
   }

   public Collection values()
   {
      return map.values();
   }
}