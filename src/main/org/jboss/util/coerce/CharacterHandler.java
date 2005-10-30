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
package org.jboss.util.coerce;

import org.jboss.util.CoercionException;
import org.jboss.util.NotCoercibleException;

/**
 * A Character coercion handler.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CharacterHandler
   extends BoundCoercionHandler
{
   /**
    * Get the target class type for this CoercionHandler.
    *
    * @return     Class type
    */
   public Class getType() {
      return Character.class;
   }

   /**
    * Coerces the given value into the given type (which should be
    * Character.class).
    *
    * <p>This currently only support coercion from a String.
    *
    * @param value   Value to coerce
    * @param type    Character.class
    * @return        Value coerced into a Character
    *
    * @throws CoercionException  Failed to coerce
    */
   public Object coerce(Object value, Class type) throws CoercionException {
      if (value.getClass().equals(String.class)) {
         return coerce((String)value);
      }
      
      throw new NotCoercibleException(value);
   }

   /**
    * Coerces the given string into a Character, by taking off the first
    * index of the string and wrapping it.
    *
    * @param value   String value to convert to a Character
    * @return        Character value or null if the string is empty.
    */
   public Object coerce(String value) {
      char[] temp = value.toCharArray();
      if (temp.length == 0) {
         return null;
      }
      return new Character(temp[0]);
   }
}

