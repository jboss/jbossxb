/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.file;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public abstract class ArchiveBrowser
{
   public interface Filter
   {
      boolean accept(String filename);
   }

   public static Iterator getBrowser(URL url, Filter filter)
   {
      if (url.getProtocol().equals("file"))
      {
         File f = new File(url.getFile());
         if (f.isDirectory())
         {
            return new DirectoryArchiveBrowser(f, filter);
         }
         else
         {
            return new JarArchiveBrowser(f, filter);
         }
      }
      else
      {
         throw new RuntimeException("NOT IMPLEMENTED");
      }
   }
}