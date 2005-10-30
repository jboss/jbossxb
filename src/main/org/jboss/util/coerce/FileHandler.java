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

import java.io.File;

/**
 * A <tt>java.io.File</tt> coercion handler.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class FileHandler
   extends BoundCoercionHandler
{
   /**
    * Get the target class type for this <tt>CoercionHandler</tt>.
    *
    * @return  <tt>Class</tt> type.
    */
   public Class getType() {
      return File.class;
   }

   /**
    * Coerces the given value into the given type (which should be
    * <tt>File</tt>).
    *
    * <p>This currently only support coercion from a <tt>String</tt>
    *
    * @param value   Value to coerce.
    * @param type    <tt>File</tt>.
    * @return        Value coerced into a <tt>File</tt>.
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
    * Coerces the given String into a <tt>File</tt> by creating attempting
    * to create a new file for the given filename.
    *
    * @param value   The name of the file to create.
    * @return        <tt>File</tt>
    *
    * @throws NotCoercibleException    Failed to create file.
    */
   public Object coerce(String value) {
      return new File(value);
   }
}

