/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

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
