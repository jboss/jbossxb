/*
* JBoss, the OpenSource J2EE webOS
*
* Distributable under LGPL license.
* See terms of license at gnu.org.
*/
package org.jboss.net.protocol.nestedjar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

/**
*
* This is class allows you to use the njar: URL protocol. It is very
* similar to it's jar: cusin.  The difference being that jars can be
* nested.
*
* An example of how to use this class is:
* <code>
*
* 	NestedJarURLHandlerFactory.start();
*    URL u = new URL("njar:njar:file:c:/test1.zip^/test2.zip^/hello.txt");
* 	u.openStream();
*
* </code>
*
* Please be aware that the njar protocol caches it's jar in temporary 
storage
* when connections are opened into them.  So for the above example, 2 files 
would
* cached a temp files names similar to nested-xxxx.jar
*
* TODO: Add accessors so that the cache can be flushed.
*
*
* @author <a href="mailto:cojonudo14@hotmail.com">Hiram Chirino</a>
*
*/
public class NestedJarURLHandlerFactory implements URLStreamHandlerFactory
{

   public class NestedJarURLHandler extends URLStreamHandler
   {

      Map savedJars = new HashMap();

      // URL protocol designations
      public static final String PROTOCOL = "njar";
      public static final String NJAR_SEPARATOR = "^/";
      public static final String JAR_SEPARATOR = "!/";

      /*
       * @see URLStreamHandler#openConnection(URL)
       */
      protected URLConnection openConnection(URL u) throws IOException
      {
         System.out.println("Using old factory style n(ested)jar protocol...");

         String file = u.getFile();
         String embeddedURL = file;
         String jarPath = "";

         int pos = file.lastIndexOf(NJAR_SEPARATOR);
         if (pos >= 0)
         {
            embeddedURL = file.substring(0, pos);
            if (file.length() > pos + NJAR_SEPARATOR.length())
               jarPath = file.substring(pos + NJAR_SEPARATOR.length());
         }

         if (embeddedURL.startsWith(PROTOCOL))
         {

            //System.out.println("Opening next  nested jar: " + embeddedURL);
            File tempJar = (File) savedJars.get(embeddedURL);
            if (tempJar == null)
            {
               InputStream embededData = new URL(embeddedURL).openStream();
               tempJar = File.createTempFile("nested-", ".jar");
               tempJar.deleteOnExit();
               //System.out.println("temp file location : " + tempJar);
               storeJar(embededData, new FileOutputStream(tempJar));
               savedJars.put(embeddedURL, tempJar);
            }

            String t = tempJar.getCanonicalFile().toURL().toExternalForm();
            //System.out.println("file URL : " + t);
            t = "njar:" + t + NJAR_SEPARATOR + jarPath;
            //System.out.println("Opening saved jar: " + t);

            return new URL(t).openConnection();

         }
         else
         {
            //System.out.println("Opening final nested jar: " + embeddedURL);
            return new URL("jar:" + embeddedURL + JAR_SEPARATOR + jarPath).openConnection();
         }
      }

   }

   private NestedJarURLHandler handler = new NestedJarURLHandler();

   public URLStreamHandler createURLStreamHandler(String protocol)
   {
      if (protocol.equals(handler.PROTOCOL))
         return handler;
      return null;
   }

   public static void start()
   {
      URL.setURLStreamHandlerFactory(new NestedJarURLHandlerFactory());
   }


   protected void storeJar(InputStream in, OutputStream out) 
      throws IOException
   {

      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      try
      {

         bis = new BufferedInputStream(in);
         bos = new BufferedOutputStream(out);

         byte data[] = new byte[512];
         int c;
         while ((c = bis.read(data)) >= 0)
         {
            bos.write(data, 0, c);
         }

      }
      finally
      {
         try
         {
            bis.close();
         }
         catch (IOException ignore)
         {
         }
         try
         {
            bos.close();
         }
         catch (IOException ignore)
         {
         }
      }
   }

}
