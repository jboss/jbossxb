/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class DirectoryArchiveBrowser implements Iterator
{
   private Iterator files;

   public DirectoryArchiveBrowser(File file, ArchiveBrowser.Filter filter)
   {
      ArrayList list = new ArrayList();
      try
      {
         create(list, file, filter);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      files = list.iterator();
   }

   public static void create(List list, File dir, ArchiveBrowser.Filter filter) throws Exception
   {
      File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         if (files[i].isDirectory())
         {
            create(list, files[i], filter);
         }
         else
         {
            if (filter.accept(files[i].getAbsolutePath()))
            {
               list.add(files[i]);
            }
         }
      }
   }

   public boolean hasNext()
   {
      return files.hasNext();
   }

   public Object next()
   {
      File fp = (File) files.next();
      try
      {
         return new FileInputStream(fp);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void remove()
   {
      throw new RuntimeException("Illegal operation call");
   }


}
