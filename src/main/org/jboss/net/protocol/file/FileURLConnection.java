/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.net.protocol.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;

import java.security.Permission;
import java.io.FilePermission;

/**
 * Provides local file access via URL semantics, correctly returning
 * lastModified.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class FileURLConnection
   extends URLConnection
{
   protected File file;

   public FileURLConnection(final URL url)
      throws MalformedURLException, IOException
   {
      super(url);

      file = new File(url.getFile());
      doOutput = false;
   }

   public void connect() throws IOException {
      if (connected) return;

      if (!file.exists()) {
         throw new FileNotFoundException(file.getPath());
      }

      connected = true;
   }

   public InputStream getInputStream() throws IOException {
      if (!connected) connect();

      return new FileInputStream(file);
   }

   public OutputStream getOutputStream() throws IOException {
      if (!connected) connect();

      return new FileOutputStream(file);
   }

   public String getHeaderField(final String name) {
      if (name.equalsIgnoreCase("last-modified")) {
         return String.valueOf(getLastModified());
      }

      return super.getHeaderField(name);
   }

   /* FIXME (or remove me) 
   public Permission getPermission() throws IOException {
      // should probably return a FilePermission here... 
      // but I don't understand that crap, so just return the default
      return super.getPermission();
   }
   */

   public long getLastModified() {
      return file.lastModified();
   }
}
