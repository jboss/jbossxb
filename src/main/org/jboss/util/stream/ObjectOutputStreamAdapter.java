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

import org.jboss.util.NullArgumentException;

/**
 * An <code>ObjectOutputStream</code> wrapping adapter.
 *
 * <h3>Concurrency</h3>
 * This class is <b>not</b> synchronized.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ObjectOutputStreamAdapter
   extends ObjectOutputStream
{
   /** Nested object output stream */
   protected ObjectOutputStream out;
   
   /**
    * Construct a new ObjectOutputStreamAdapter.
    *
    * @param out     An ObjectOutputStream stream.
    *
    * @throws IOException              Any exception thrown by the underlying
    *                                  OutputStream.
    * @throws IllegalArgumentException Out is null.
    */
   public ObjectOutputStreamAdapter(ObjectOutputStream out)
      throws IOException 
   {
      super(); // allow calls to writeObjectOverride()

      if (out == null)
         throw new NullArgumentException("out");

      this.out = out;
   }

   protected void writeObjectOverride(Object obj) throws IOException {
      out.writeObject(obj);
   }

   public void useProtocolVersion(int version) throws IOException {
      out.useProtocolVersion(version);
   }

   public void defaultWriteObject() throws IOException {
      out.defaultWriteObject();
   }

   public ObjectOutputStream.PutField putFields() throws IOException {
      return out.putFields();
   }

   public void writeFields() throws IOException {
      out.writeFields();
   }

   public void reset() throws IOException {
      out.reset();
   }

   public void write(int data) throws IOException {
      out.write(data);
   }

   public void write(byte b[]) throws IOException {
      out.write(b);
   }

   public void write(byte b[], int off, int len) throws IOException {
      out.write(b, off, len);
   }

   public void flush() throws IOException {
      out.flush();
   }

   public void close() throws IOException {
      out.close();
   }

   public void writeBoolean(boolean data) throws IOException {
      out.writeBoolean(data);
   }

   public void writeByte(int data) throws IOException {
      out.writeByte(data);
   }

   public void writeShort(int data) throws IOException {
      out.writeShort(data);
   }

   public void writeChar(int data) throws IOException {
      out.writeChar(data);
   }

   public void writeInt(int data) throws IOException {
      out.writeInt(data);
   }

   public void writeLong(long data) throws IOException {
      out.writeLong(data);
   }

   public void writeFloat(float data) throws IOException {
      out.writeFloat(data);
   }

   public void writeDouble(double data) throws IOException {
      out.writeDouble(data);
   }

   public void writeBytes(String data) throws IOException {
      out.writeBytes(data);
   }

   public void writeChars(String data) throws IOException {
      out.writeChars(data);
   }

   public void writeUTF(String s) throws IOException {
      out.writeUTF(s);
   }
}
