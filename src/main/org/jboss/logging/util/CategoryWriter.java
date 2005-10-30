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
package org.jboss.logging.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 *  A subclass of PrintWriter that redirects its output to a log4j Category. <p>
 *
 *  This class is used to have something to give api methods that require a
 *  PrintWriter for logging. JBoss-owned classes of this nature generally ignore
 *  the PrintWriter and do their own log4j logging.
 *
 * @deprecated Use {@link LoggerWriter} instead.
 *
 * @author     <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 *      .
 * @created    August 19, 2001
 * @version    $$
 */
public class CategoryWriter
       extends PrintWriter {
   private Category category;
   private Priority priority;
   private boolean  inWrite;
   private boolean  issuedWarning;

   /**
    *  Redirect logging to the indicated category using Priority.INFO
    *
    * @param  category  Description of Parameter
    */
   public CategoryWriter( final Category category ) {
      this( category, Priority.INFO );
   }

   /**
    *  Redirect logging to the indicated category using the given priority. The
    *  ps is simply passed to super but is not used.
    *
    * @param  category  Description of Parameter
    * @param  priority  Description of Parameter
    */
   public CategoryWriter( final Category category,
         final Priority priority ) {
      super( new InternalCategoryWriter( category, priority ), true );
   }

   /**
    * @created    August 19, 2001
    */
   static class InternalCategoryWriter extends Writer {
      private Category category;
      private Priority priority;
      private boolean closed;

      public InternalCategoryWriter( final Category category, final Priority priority ) {
         lock = category;
         //synchronize on this category
         this.category = category;
         this.priority = priority;
      }

      public void write( char[] cbuf, int off, int len )
         throws IOException {
         if ( closed ) {
            throw new IOException( "Called write on closed Writer" );
         }
         // Remove the end of line chars
         while ( len > 0 && ( cbuf[len - 1] == '\n' || cbuf[len - 1] == '\r' ) ) {
            len--;
         }
         if ( len > 0 ) {
            category.log( priority, String.copyValueOf( cbuf, off, len ) );
         }
      }


      public void flush()
         throws IOException {
         if ( closed ) {
            throw new IOException( "Called flush on closed Writer" );
         }
      }

      public void close() {
         closed = true;
      }
   }

}
