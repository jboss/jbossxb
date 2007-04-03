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

/**
 * Map metadata.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 38013 $
 */
public class AbstractMapMetaDataEntry
{
   private AbstractValueMetaData key;

   private AbstractValueMetaData value;

   public Object getKey()
   {
      return key.getUnderlyingValue();
   }

   public void setKey(Object key)
   {
      this.key = (AbstractValueMetaData) key;
   }

   public Object getValue()
   {
      return value.getUnderlyingValue();
   }

   public void setValue(Object value)
   {
      this.value = (AbstractValueMetaData) value;
   }

   public String toString()
   {
      return "[map-entry key=" + key + ", value=" + value + "]";
   }

}