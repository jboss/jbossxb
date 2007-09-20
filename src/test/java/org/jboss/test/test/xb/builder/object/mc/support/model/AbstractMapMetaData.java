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
package org.jboss.test.xb.builder.object.mc.support.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.annotations.JBossXmlChild;
import org.jboss.xb.annotations.JBossXmlChildren;
import org.jboss.xb.annotations.JBossXmlNoElements;

/**
 * Map metadata.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 59429 $
 */
@XmlType()
@JBossXmlNoElements
@JBossXmlChildren
({
   @JBossXmlChild(name="entry", type=MapEntry.class)
})
public class AbstractMapMetaData extends AbstractTypeMetaData
   implements Set<MapEntry>, Serializable
{
   private static final long serialVersionUID = 1L;

   /** The map */
   private HashMap<MetaDataVisitorNode, MetaDataVisitorNode> map = new HashMap<MetaDataVisitorNode, MetaDataVisitorNode>();

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
   @XmlAttribute(name="keyClass")
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
   @XmlAttribute(name="valueClass")
   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }

   protected Class<? extends Map> expectedMapClass()
   {
      return Map.class;
   }

   public Object getValue(TypeInfo info, ClassLoader cl) throws Throwable
   {
      return null;
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

   public Set<Map.Entry<MetaDataVisitorNode, MetaDataVisitorNode>> entrySet()
   {
      return map.entrySet();
   }

   public MetaDataVisitorNode get(Object key)
   {
      return map.get(key);
   }

   public boolean isEmpty()
   {
      return map.isEmpty();
   }

   public Set<MetaDataVisitorNode> keySet()
   {
      return map.keySet();
   }

   public MetaDataVisitorNode put(MetaDataVisitorNode key, MetaDataVisitorNode value)
   {
      return map.put(key, value);
   }

   public void putAll(Map<? extends MetaDataVisitorNode, ? extends MetaDataVisitorNode> t)
   {
      map.putAll(t);
   }

   public boolean remove(Object key)
   {
      throw new UnsupportedOperationException();
   }

   public int size()
   {
      return map.size();
   }

   public Collection<MetaDataVisitorNode> values()
   {
      return map.values();
   }

   public boolean add(MapEntry o)
   {
      map.put(o.getKey(), o.getValue());
      return true;
   }

   public boolean addAll(Collection<? extends MapEntry> c)
   {
      for (MapEntry mapEntry : c)
         add(mapEntry);
      return true;
   }

   public boolean contains(Object o)
   {
      throw new UnsupportedOperationException();
   }

   public boolean containsAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   public Iterator<MapEntry> iterator()
   {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   public Object[] toArray()
   {
      throw new UnsupportedOperationException();
   }

   public <T> T[] toArray(T[] a)
   {
      throw new UnsupportedOperationException();
   }
}