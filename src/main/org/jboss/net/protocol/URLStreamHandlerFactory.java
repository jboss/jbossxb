/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.net.protocol;

import java.net.URL;
import java.net.URLStreamHandler;

import org.jboss.logging.Logger;

/**
 * A factory for loading JBoss specific protocols.  This is based
 * on Sun's URL mechanism, in that <tt>Handler</tt> classes will be 
 * looked for in the <tt>org.jboss.net.protocol</tt>.
 *
 * <p>This factory is installed by the default server implementaion
 *    as it appears that our custom class loading disallows the
 *    default URL logic to function when setting the
 *    <tt>java.protocol.handler.pkgs</tt> system property.
 *
 * <p>Use {@link preload} to force the URL handler map to load the
 *    handlers for each protocol listed in {@link #PROTOCOLS}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class URLStreamHandlerFactory
   implements java.net.URLStreamHandlerFactory
{
   /** Class logger. */
   private static final Logger log = Logger.getLogger(URLStreamHandlerFactory.class);

   /** The package prefix where JBoss protocol handlers live. */
   public static final String PACKAGE_PREFIX = "org.jboss.net.protocol";

   /**
    * Returns the Stream Handler.
    *
    * @param protocol    The protocol to use
    * @return            The protocol handler or null if not found
    */
   public URLStreamHandler createURLStreamHandler(final String protocol)
   {
      URLStreamHandler handler = null;

      try {
         String classname = PACKAGE_PREFIX + "." + protocol + ".Handler";
         Class type = null;
         
         try {
            type = Class.forName(classname);
         } 
         catch (ClassNotFoundException e) {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            if (cl != null) {
               type = cl.loadClass(classname);
            }
         }
         
         if (type != null) {
            handler = (URLStreamHandler)type.newInstance();
         }
      } 
      catch (Exception ignore) {}

      return handler;
   }

   /** A list of JBoss specific protocols for preloading. */
   public static final String PROTOCOLS[] = {
      "resource",
      "file",
      "njar",
   };

   /**
    * Preload the JBoss specific protocol handlers, to that URL knows about 
    * them even if the handler factory is changed.
    */
   public static void preload() {
      for (int i=0; i<PROTOCOLS.length; i++) {
         try {
            URL url = new URL(PROTOCOLS[i], "", -1, "");
            log.trace("Loaded protocol: " + PROTOCOLS[i]);
         }
         catch (Exception e) {
            log.warn("Failed to load protocol: " + PROTOCOLS[i], e);
         }
      }
   }
}
