/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.net.protocol.njar;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A simple factory for creating njar stream handlers.

 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:cojonudo14@hotmail.com">Hiram Chirino</a>
 */
public class NestedJarURLHandlerFactory 
   implements URLStreamHandlerFactory
{
   Handler handler = new Handler();

   public URLStreamHandler createURLStreamHandler(String protocol)
   {
      if (protocol.equals(Handler.PROTOCOL))
         return handler;
      return null;
   }

   public static void start()
   {
      URL.setURLStreamHandlerFactory(new NestedJarURLHandlerFactory());
   }
}
