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

import java.net.URL;
import java.net.MalformedURLException;

import org.jboss.net.protocol.DelegatingURLConnection;

/**
 * Provides access to system resources as a URLConnection.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ResourceURLConnection
   extends DelegatingURLConnection
{
   public ResourceURLConnection(final URL url)
      throws MalformedURLException, IOException
   {
      super(url);
   }

   protected URL makeDelegateUrl(final URL url)
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
}
