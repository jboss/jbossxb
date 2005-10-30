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

import java.io.OutputStream;

/**
 * A <tt>null</tt> <code>OutputStream</code>.  All values passed to 
 * {@link #write(int)} are discarded.  Calls to {@link #flush()} and 
 * {@link #close()} are ignored. 
 *
 * <p>All methods are declared <b>NOT</b> to throw <code>IOException</code>s.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class NullOutputStream
   extends OutputStream
{
   /** A default null output stream. */
   public static final NullOutputStream STREAM = new NullOutputStream();

   /**
    * Non-operation.
    */
   public void write(final int b) {}

   /**
    * Non-operation.
    */
   public void flush() {}

   /**
    * Non-operation.
    */
   public void close() {}

   /**
    * Non-operation.
    */
   public void write(final byte[] bytes) {}

   /**
    * Non-operation.
    */
   public void write(final byte[] bytes, final int offset, final int length) {}
}
