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

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * A simple interface to allow an object to print itself to a 
 * <code>PrintWriter</code> or <code>PrintStream</code>.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface Printable
{
   /**
    * Print to a PrintWriter.
    *
    * @param writer  PrintWriter to print to.
    */
   void print(PrintWriter writer);

   /**
    * Print to a PrintWriter.
    *
    * @param writer  PrintWriter to print to.
    * @param prefix  Prefix to append to each line in the stream.
    */
   void print(PrintWriter writer, String prefix);

   /**
    * Print to a PrintStream.
    *
    * @param stream  PrintStream to print to.
    */
   void print(PrintStream stream);

   /**
    * Print to a PrintStream.
    *
    * @param stream  PrintStream to print to.
    * @param prefix  Prefix to append to each line in the stream.
    */
   void print(PrintStream stream, String prefix);
}
