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
import java.io.FileFilter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.jboss.net.protocol.URLListerBase;

public class FileURLLister extends URLListerBase {
   public Collection listMembers(final URL baseUrl, final URLFilter filter) throws IOException {
      File directory = new File(baseUrl.getPath());
      if (directory.exists() == false) {
         throw new FileNotFoundException(directory.toString());
      }
      File[] files = directory.listFiles(new FileFilter() {
         public boolean accept(File file) {
            return filter.accept(baseUrl, file.getName());
         }
      });
      return filesToURLs(baseUrl, files);
   }

   private Collection filesToURLs(URL baseUrl, File[] files) {
      URL[] urls = new URL[files.length];
      for (int i = 0; i < files.length; i++) {
         File file = files[i];
         try {
            String name = file.getName();
            URL url = new URL(baseUrl, file.isDirectory() ? name+"/" : name);
            urls[i] = url;
         } catch (MalformedURLException e) {
            // shouldn't happen
            throw new IllegalStateException();
         }
      }
      return Arrays.asList(urls);
   }
}
