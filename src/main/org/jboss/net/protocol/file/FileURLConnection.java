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

   public Permission getPermission() throws IOException {
      // this sucks... must be a better way, but screw it for now
      // if you know how this should be done, please fix it... please
      java.util.List list = new java.util.ArrayList(4);
      if (file.canRead()) list.add("read");
      if (file.canWrite()) list.add("write");

      SecurityManager security = System.getSecurityManager();
      if (security != null) {
         try {
            security.checkExec(file.getPath());
            list.add("execute");
         }
         catch (SecurityException ignore) {}

         try {
            security.checkDelete(file.getPath());
            list.add("delete");
         }
         catch (SecurityException ignore) {}
      }
      else {
         // ?? sure, whatever
         list.add("execute");
         list.add("delete");
      }

      StringBuffer actions = new StringBuffer();
      java.util.Iterator iter = list.iterator();
      while (iter.hasNext()) {
         actions.append(iter.next());
         if (iter.hasNext()) actions.append(",");
      }

      return new FilePermission(file.getPath(), actions.toString());
   }

   public long getLastModified() {
      return file.lastModified();
   }
}
