/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.net.protocol.resource;

import java.io.IOException;
import java.io.InputStream;

import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;

import java.security.Permission;

/**
 * Provides access to system resources as a URLConnection.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ResourceURLConnection
   extends URLConnection
{
   private URL delegateUrl;
   private URLConnection delegateConnection;
   
   public ResourceURLConnection(final URL url)
      throws MalformedURLException, IOException
   {
      super(url);
      
      delegateUrl = makeDelegateUrl(url);
      delegateConnection = delegateUrl.openConnection();
   }

   private URL makeDelegateUrl(final URL url)
      throws MalformedURLException, IOException
   {
      String name = url.getHost();
      String file = url.getFile();
      if (file != null && !file.equals("")) {
         name += file;
      }
      
      URL _url = ClassLoader.getSystemResource(name);
      if (_url == null)
         throw new IOException("could not locate resource: " + name);

      return _url;
   }

   public void connect() throws IOException {
      delegateConnection.connect();
   }

   public Object getContent() throws IOException {
      return delegateConnection.getContent();
   }

   public String getContentType() {
      return delegateConnection.getContentType();
   }

   public InputStream getInputStream() throws IOException {
      return delegateConnection.getInputStream();
   }

   public String getHeaderField(final String name) {
      return delegateConnection.getHeaderField(name);
   }

   public Permission getPermission() throws IOException {
      return delegateConnection.getPermission();
   }
}
