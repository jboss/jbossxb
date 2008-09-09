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
package org.jboss.xb.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class NoopMap<K,V>
   extends AbstractMap<K,V>
{
   public V put(K key, V value)
   {
      return null;
   }

   public int size()
   {
      return 0;
   }

   public boolean isEmpty()
   {
      return true;
   }

   public boolean containsKey(Object key)
   {
      return false;
   }

   public boolean containsValue(Object value)
   {
      return false;
   }

   public V get(Object key)
   {
      return null;
   }

   public Set<K> keySet()
   {
      return Collections.emptySet();
   }

   public Collection<V> values()
   {
      return Collections.emptySet();
   }

   public Set<Entry<K,V>> entrySet()
   {
      return Collections.emptySet();
   }

   public boolean equals(Object o)
   {
      return (o instanceof Map) && ((Map)o).size() == 0;
   }

   public int hashCode()
   {
      return 0;
   }
}
