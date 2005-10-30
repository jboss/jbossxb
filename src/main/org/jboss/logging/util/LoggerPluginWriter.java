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

import org.jboss.logging.LoggerPlugin;

/** A subclass of PrintWriter that redirects its output to a LoggerPlugin at
 * INFO level. The only usecase for this is legacy java apis which require
 * integration with the logging layer through a Writer.
 *
 * @author David Jencks
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class LoggerPluginWriter
   extends PrintWriter
{
   /**
    *
    * @param  logger the logging plugin used to write messages
    */
   public LoggerPluginWriter(final LoggerPlugin logger)
   {
      super(new PluginWriter(logger), true);
   }

   static class PluginWriter extends Writer
   {
      private LoggerPlugin logger;
      private boolean closed;

      public PluginWriter(final LoggerPlugin logger)
      {
         lock = logger;
         this.logger = logger;
      }

      public void write(char[] cbuf, int off, int len)
         throws IOException
      {
         if (closed)
         {
            throw new IOException("Called write on closed Writer");
         }
         // Remove the end of line chars
         while (len > 0 && (cbuf[len - 1] == '\n' || cbuf[len - 1] == '\r'))
         {
            len--;
         }
         if (len > 0)
         {
            logger.info(String.copyValueOf(cbuf, off, len));
         }
      }

      public void flush()
         throws IOException
      {
         if (closed)
         {
            throw new IOException("Called flush on closed Writer");
         }
      }

      public void close()
      {
         closed = true;
      }
   }

}
