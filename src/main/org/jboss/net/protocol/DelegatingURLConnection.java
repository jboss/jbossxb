/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.net.protocol;

import java.io.IOException;
import java.io.InputStream;

import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;

import java.security.Permission;

/**
 * An delegating URLConnection support class.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DelegatingURLConnection
   extends URLConnection
{
   protected URL delegateUrl;
   protected URLConnection delegateConnection;

   public DelegatingURLConnection(final URL url)
      throws MalformedURLException, IOException
   {
      super(url);
      
      delegateUrl = makeDelegateUrl(url);
      delegateConnection = makeDelegateUrlConnection(delegateUrl);
   }

   protected URL makeDelegateUrl(final URL url)
      throws MalformedURLException, IOException
   {
      return url;
   }

   protected URLConnection makeDelegateUrlConnection(final URL url)
      throws IOException
   {
      return url.openConnection();
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
