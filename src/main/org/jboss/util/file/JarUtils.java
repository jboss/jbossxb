package org.jboss.util.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/** A utility class for dealing with Jar files.

@version $Revision$
*/
public final class JarUtils
{
   /**
    * Hide the constructor
    */
   private JarUtils()
   {
   }
   
   /**
    * <P>This function will create a Jar archive containing the src
    * file/directory.  The archive will be written to the specified
    * OutputStream.</P>
    *
    * <P>This is a shortcut for<br>
    * <code>jar(out, new File[] { src }, null, null, null);</code></P>
    *
    * @param out The output stream to which the generated Jar archive is
    *        written.
    * @param src The file or directory to jar up.  Directories will be
    *        processed recursively.
    */
   public static void jar(OutputStream out, File src) throws IOException
   {
      jar(out, new File[] { src }, null, null, null);
   }
 
   /**
    * <P>This function will create a Jar archive containing the src
    * file/directory.  The archive will be written to the specified
    * OutputStream.</P>
    *
    * <P>This is a shortcut for<br>
    * <code>jar(out, src, null, null, null);</code></P>
    *
    * @param out The output stream to which the generated Jar archive is
    *        written.
    * @param src The file or directory to jar up.  Directories will be
    *        processed recursively.
    */
   public static void jar(OutputStream out, File[] src) throws IOException
   {
      jar(out, src, null, null, null);
   }
   
   /**
    * <P>This function will create a Jar archive containing the src
    * file/directory.  The archive will be written to the specified
    * OutputStream.  Directories are processed recursively, applying the
    * specified filter if it exists.
    *
    * <P>This is a shortcut for<br>
    * <code>jar(out, src, filter, null, null);</code></P>
    *
    * @param out The output stream to which the generated Jar archive is
    *        written.
    * @param src The file or directory to jar up.  Directories will be
    *        processed recursively.
    * @param filter The filter to use while processing directories.  Only
    *        those files matching will be included in the jar archive.  If
    *        null, then all files are included.
    */
   public static void jar(OutputStream out, File[] src, FileFilter filter)
      throws IOException
   {
      jar(out, src, filter, null, null);
   }
   
   /**
    * <P>This function will create a Jar archive containing the src
    * file/directory.  The archive will be written to the specified
    * OutputStream.  Directories are processed recursively, applying the
    * specified filter if it exists.
    *
    * @param out The output stream to which the generated Jar archive is
    *        written.
    * @param src The file or directory to jar up.  Directories will be
    *        processed recursively.
    * @param filter The filter to use while processing directories.  Only
    *        those files matching will be included in the jar archive.  If
    *        null, then all files are included.
    * @param prefix The name of an arbitrary directory that will precede all
    *        entries in the jar archive.  If null, then no prefix will be
    *        used.
    * @param man The manifest to use for the Jar archive.  If null, then no
    *        manifest will be included.
    */
   public static void jar(OutputStream out, File[] src, FileFilter filter,
      String prefix, Manifest man) throws IOException
   {
      
      for (int i = 0; i < src.length; i++)
      {
         if (!src[i].exists())
         {
            throw new FileNotFoundException(src.toString());
         }
      }
      
      JarOutputStream jout;
      if (man == null)
      {
         jout = new JarOutputStream(out);
      }
      else
      {
         jout = new JarOutputStream(out, man);
      }
      if (prefix != null && prefix.length() > 0 && !prefix.equals("/"))
      {
         // strip leading '/'
         if (prefix.charAt(0) == '/')
         {
            prefix = prefix.substring(1);
         }
         // ensure trailing '/'
         if (prefix.charAt(prefix.length() - 1) != '/')
         {
            prefix = prefix + "/";
         }
      } 
      else
      {
         prefix = "";
      }
      JarInfo info = new JarInfo(jout, filter);
      for (int i = 0; i < src.length; i++)
      {
         jar(src[i], prefix, info);
      }
      jout.close();
   }
   
   /**
    * This simple convenience class is used by the jar method to reduce the
    * number of arguments needed.  It holds all non-changing attributes
    * needed for the recursive jar method.
    */
   private static class JarInfo
   {
      public JarOutputStream out;
      public FileFilter filter;
      public byte[] buffer;
      
      public JarInfo(JarOutputStream out, FileFilter filter)
      {
         this.out = out;
         this.filter = filter;
         buffer = new byte[1024];
      }
   }
   
   /**
    * This recursive method writes all matching files and directories to
    * the jar output stream.
    */
   private static void jar(File src, String prefix, JarInfo info)
      throws IOException
   {
      
      JarOutputStream jout = info.out;
      if (src.isDirectory())
      {
         // create / init the zip entry
         prefix = prefix + src.getName() + "/";
         ZipEntry entry = new ZipEntry(prefix);
         entry.setTime(src.lastModified());
         entry.setMethod(JarOutputStream.STORED);
         entry.setSize(0L);
         entry.setCrc(0L);
         jout.putNextEntry(entry);
         jout.closeEntry();
         
         // process the sub-directories
         File[] files = src.listFiles(info.filter);
         for (int i = 0; i < files.length; i++)
         {
            jar(files[i], prefix, info);
         }
      } 
      else if (src.isFile())
      {
         // get the required info objects
         byte[] buffer = info.buffer;
         
         // create / init the zip entry
         ZipEntry entry = new ZipEntry(prefix + src.getName());
         entry.setTime(src.lastModified());
         jout.putNextEntry(entry);
         
         // dump the file
         FileInputStream in = new FileInputStream(src);
         int len;
         while ((len = in.read(buffer, 0, buffer.length)) != -1)
         {
            jout.write(buffer, 0, len);
         }
         in.close();
         jout.closeEntry();
      }
   }
   
   /**
    * Dump the contents of a JarArchive to the dpecified destination.
    */
   public static void unjar(InputStream in, File dest) throws IOException
   {
      if (!dest.exists())
      {
         dest.mkdirs();
      }
      if (!dest.isDirectory())
      {
         throw new IOException("Destination must be a directory.");
      }
      JarInputStream jin = new JarInputStream(in);
      byte[] buffer = new byte[1024];
      
      ZipEntry entry = jin.getNextEntry();
      while (entry != null)
      {
         String fileName = entry.getName();
         if (fileName.charAt(fileName.length() - 1) == '/')
         {
            fileName = fileName.substring(0, fileName.length() - 1);
         }
         if (fileName.charAt(0) == '/')
         {
            fileName = fileName.substring(1);
         }
         if (File.separatorChar != '/')
         {
            fileName = fileName.replace('/', File.separatorChar);
         }
         File file = new File(dest, fileName);
         if (entry.isDirectory())
         {
            // make sure the directory exists
            file.mkdirs();
            jin.closeEntry();
         } 
         else
         {
            // make sure the directory exists
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
            {
               parent.mkdirs();
            }
            
            // dump the file
            OutputStream out = new FileOutputStream(file);
            int len = 0;
            while ((len = jin.read(buffer, 0, buffer.length)) != -1)
            {
               out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            jin.closeEntry();
         }
         entry = jin.getNextEntry();
      }
      jin.close();
   }
   
   /**
    * A simple jar-like tool used for testing.  It's actually faster than 
    * jar, though doesn't sipport as many options.
    */
   public static void main(String[] args) throws Exception
   {
      if (args.length == 0)
      {
         System.out.println("usage: <x or c> <jar-archive> <files...>");
         System.exit(0);
      }
      if (args[0].equals("x"))
      {
         BufferedInputStream in = new BufferedInputStream(new FileInputStream(args[1]));
         File dest = new File(args[2]);
         unjar(in, dest);
      }
      else if (args[0].equals("c"))
      {
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(args[1]));
         File[] src = new File[args.length - 2];
         for (int i = 0; i < src.length; i++)
         {
            src[i] = new File(args[2 + i]);
         }
         jar(out, src);
      }
      else
      {
         System.out.println("Need x or c as first argument");
      }
   }
}
