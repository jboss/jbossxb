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
package org.jboss.util;

/**
 * This exception is thrown to indicate that a problem has occured while
 * trying to coerce an object.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CoercionException
   extends NestedRuntimeException
{
   /**
    * Construct a <tt>CoercionException</tt> with the specified detail 
    * message.
    *
    * @param msg  Detail message.
    */
   public CoercionException(String msg) {
      super(msg);
   }

   /**
    * Construct a <tt>CoercionException</tt> with the specified detail 
    * message and nested <tt>Throwable</tt>.
    *
    * @param msg     Detail message.
    * @param nested  Nested <tt>Throwable</tt>.
    */
   public CoercionException(String msg, Throwable nested) {
      super(msg, nested);
   }

   /**
    * Construct a <tt>CoercionException</tt> with the specified
    * nested <tt>Throwable</tt>.
    *
    * @param nested  Nested <tt>Throwable</tt>.
    */
   public CoercionException(Throwable nested) {
      super(nested);
   }

   /**
    * Construct a <tt>CoercionException</tt> with no detail.
    */
   public CoercionException() {
      super();
   }
}
