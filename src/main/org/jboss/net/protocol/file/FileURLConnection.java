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
import java.io.BufferedInputStream;

/**
 * Provides local file access via URL semantics, correctly returning
 * the last modified time of the underlying file.
 *
 * @version $Revision$
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author  Scott.Stark@jboss.org
 */
public class FileURLConnection
   extends URLConnection
{
   protected File file;

   public FileURLConnection(final URL url)
      throws MalformedURLException, IOException
   {
      super(url);
      
      file = new File(url.getPath().replace('/', File.separatorChar).replace('|', ':'));

      doOutput = false;
   }

   /**
    * Returns the underlying file for this connection.
    */
   public File getFile()
   {
      return file;
   }

   /**
    * Checks if the underlying file for this connection exists.
    *
    * @throws FileNotFoundException
    */
   public void connect() throws IOException
   {
      if (connected)
         return;

      if (!file.exists())
      {
         throw new FileNotFoundException(file.getPath());
      }
      
      connected = true;
   }

   public InputStream getInputStream() throws IOException
   {
      if (!connected)
         connect();

      return new FileInputStream(file);
   }

   public OutputStream getOutputStream() throws IOException
   {
      if (!connected)
         connect();
      SecurityManager sm = System.getSecurityManager();
      if( sm != null )
      {
         // Check for write access
         FilePermission p = new FilePermission(file.getPath(), "write");
         sm.checkPermission(p);
      }
      return new FileOutputStream(file);
   }

   /**
    * Provides support for returning the value for the
    * <tt>last-modified</tt> header.
    */
   public String getHeaderField(final String name)
   {
      String headerField = null;
      if (name.equalsIgnoreCase("last-modified"))
         headerField = String.valueOf(getLastModified());
      else if (name.equalsIgnoreCase("content-length"))
         headerField = String.valueOf(file.length());
      else if (name.equalsIgnoreCase("content-type"))
      {
         headerField = getFileNameMap().getContentTypeFor(file.getName());
         if( headerField == null )
         {
            try
            {
               InputStream is = getInputStream();
               BufferedInputStream bis = new BufferedInputStream(is);
               headerField = URLConnection.guessContentTypeFromStream(bis);
               bis.close();
            }
            catch(IOException e)
            {
            }
         }
      }
      else if (name.equalsIgnoreCase("date"))
         headerField = String.valueOf(file.lastModified());
      else
      {
         // This always returns null currently
         headerField = super.getHeaderField(name);
      }
      return headerField;
   }

   /** Return a permission for reading of the file
    */
   public Permission getPermission() throws IOException
   {
      return new FilePermission(file.getPath(), "read");
   }

   /**
    * Returns the last modified time of the underlying file.
    */
   public long getLastModified()
   {
      return file.lastModified();
   }
}
