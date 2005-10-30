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
 * A <tt>java.lang.Class</tt> coercion handler.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ClassHandler
   extends BoundCoercionHandler
{
   /**
    * Get the target class type for this <tt>CoercionHandler</tt>.
    *
    * @return     Class type.
    */
   public Class getType() {
      return Class.class;
   }

   /**
    * Coerces the given value into the given type (which should be
    * <tt>Class</tt>).
    *
    * <p>This currently only support coercion from a <tt>String</tt>.
    *
    * @param value   Value to coerce.
    * @param type    <tt>java.lang.Class</tt>.
    * @return        Value coerced into a <tt>Class</tt>.
    *
    * @throws CoercionException  Failed to coerce.
    */
   public Object coerce(Object value, Class type) throws CoercionException {
      if (value.getClass().equals(String.class)) {
         return coerce((String)value);
      }
      
      throw new NotCoercibleException(value);
   }

   /**
    * Coerces the given String into a <tt>Class</tt> by doing a
    * <code>Class.forName()</code>.
    *
    * @param value   String value to convert to a <tt>Class</tt>.
    * @return        <tt>Class</tt> value.
    *
    * @throws NotCoercibleException    Class not found.
    */
   public Object coerce(String value) {
      try {
         return Class.forName(value);
      }
      catch (ClassNotFoundException e) {
         throw new NotCoercibleException(value, e);
      }
   }
}

