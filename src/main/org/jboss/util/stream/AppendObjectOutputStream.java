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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * An <code>ObjectOutputStream</code> that is meant for appending onto an
 * existing stream written to by a non <code>AppendObjectOutputStream</code>
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AppendObjectOutputStream
   extends ObjectOutputStream
{
   /**
    * Construct a new AppendObjectOutputStream.
    *
    * @param out     An output stream.
    *
    * @throws IOException  Any exception thrown by the underlying OutputStream.
    */
   public AppendObjectOutputStream(OutputStream out) throws IOException {
      super(out);
   }

   /**
    * Reset the stream, does not write headers.
    *
    * @throws IOException  Any exception thrown by the underlying OutputStream.
    */
   protected void writeStreamHeader() throws IOException {
      this.reset();
   }
}
