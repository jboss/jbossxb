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
 * An <tt>ObjectOutputStream</tt> that can conditionally be put into
 * <i>appending</i> mode.
 *
 * <dl>
 * <dt><b>Concurrency: </b></dt>
 * <dd>This class is <b>not</b> synchronized.</dd>
 * </dl>
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AppendingObjectOutputStream
   extends ObjectOutputStreamAdapter
{
   /**
    * Construct an <tt>AppendingObjectOutputStream</tt>.
    *
    * @param out     An <tt>OutputStream</tt> stream.
    * @param append  <tt>True</tt> to append written objects; <tt>false</tt>
    *                to use default action (writes stream header).
    *
    * @throws IOException                 Any exception thrown by
    *                                     the underlying <tt>OutputStream</tt>.
    */
   public AppendingObjectOutputStream(OutputStream out, boolean append)
      throws IOException 
   {
      super(createStream(out, append));
   }

   /**
    * Helper to return a <tt>ObjectOutputStream</tt>.
    */
   private static ObjectOutputStream createStream(OutputStream out, 
                                                  boolean append)
      throws IOException
   {
      ObjectOutputStream stream;

      // if we are appending then return an append only stream
      if (append) {
         stream = new AppendObjectOutputStream(out);
      }
      // else if it already an oos then return it
      else if (out instanceof ObjectOutputStream) {
         stream = (ObjectOutputStream)out;
      }
      // else wrap the stream in an oos
      else {
         stream = new ObjectOutputStream(out);
      }

      return stream;
   }
}
