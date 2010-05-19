/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.xb.builder.runtime;


/**
 * A DefaultMapEntry.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class DefaultMapEntry
{
   private Object key;
   private Object value;
      
   /**
    * Get the key.
    * 
    * @return the key.
    */
   public Object getKey()
   {
      return key;
   }
   
   /**
    * Set the key.
    * 
    * @param key The key to set.
    */
   public void setKey(Object key)
   {
      this.key = key;
   }

   /**
    * Get the value.
    * 
    * @return the value.
    */
   public Object getValue()
   {
      return value;
   }

   /**
    * Set the value.
    * 
    * @param value The value to set.
    */
   public void setValue(Object value)
   {
      this.value = value;
   }
}
