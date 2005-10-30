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
package org.jboss.util.stream;

import java.io.InputStream;

/**
 * A <tt>null</tt> <code>InputStream</code>.  Methods that return values, 
 * return values that indicate that there is no more data to be read, other 
 * methods are non-operations.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class NullInputStream
   extends InputStream
{
   /** A default null input stream. */
   public static final NullInputStream INSTANCE = new NullInputStream();

   /**
    * Always returns zero.
    *
    * @return  Zero.
    */
   public int available() {
      return 0;
   }

   /**
    * Non-operation.
    */
   public void mark(final int readLimit) {
   }

   /**
    * Always returns false.
    *
    * @return  False.
    */
   public boolean markSupported() {
      return false;
   }

   /**
    * Non-operation.
    */
   public void reset() {
   }

   /**
    * Non-operation.
    */
   public void close() {
   }

   /**
    * Always returns -1.
    *
    * @return  -1.
    */
   public int read() {
      return -1;
   }

   /**
    * Always returns -1.
    *
    * @return  -1.
    */
   public int read(final byte bytes[], final int offset, final int length) {
      return -1;
   }

   /**
    * Always returns -1.
    *
    * @return  -1.
    */
   public int read(final byte bytes[]) {
      return -1;
   }

   /**
    * Always returns zero.
    *
    * @return  Zero.
    */
   public long skip(final long n) {
      return 0;
   }
}
