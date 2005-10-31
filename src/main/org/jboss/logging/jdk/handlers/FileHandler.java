/*
 * Copyright 1999-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.logging.jdk.handlers;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.util.logging.Formatter;
import java.util.logging.ErrorManager;

/**
 *  FileAppender appends log events to a file.
 *
 * @author Ceki G&uuml;lc&uuml; 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class FileHandler extends WriterHandler
{
   /**
    * Controls file truncatation. The default value for this variable
    * is <code>true</code>, meaning that by default a
    * <code>FileAppender</code> will append to an existing file and not
    * truncate it.
    * <p/>
    * <p>This option is meaningful only if the FileAppender opens the
    * file.
    */
   protected boolean fileAppend = true;

   /**
    * The name of the log file.
    */
   protected String fileName = null;

   /**
    * The default constructor does not do anything.
    */
   public FileHandler()
   {
   }

   /**
    * Instantiate a <code>FileHandler</code> and open the file
    * designated by <code>filename</code>. The opened filename will
    * become the output destination for this appender.
    * <p/>
    * <p>If the <code>append</code> parameter is true, the file will be
    * appended to. Otherwise, the file designated by
    * <code>filename</code> will be truncated before being opened.
    * <p/>
    * <p>If the <code>bufferedIO</code> parameter is <code>true</code>,
    * then buffered IO will be used to write to the output file.
    */
   public FileHandler(Formatter layout, String filename, boolean append, boolean bufferedIO,
      int bufferSize)
      throws IOException
   {
      super.setFormatter(layout);
      this.setFile(filename, append, bufferedIO, bufferSize);
   }

   /**
    * Instantiate a FileHandler and open the file designated by
    * <code>filename</code>. The opened filename will become the output
    * destination for this appender.
    * <p/>
    * <p>If the <code>append</code> parameter is true, the file will be
    * appended to. Otherwise, the file designated by
    * <code>filename</code> will be truncated before being opened.
    */
   public FileHandler(Formatter layout, String filename, boolean append)
      throws IOException
   {
      this(layout, filename, append, true, 2048);
   }

   /**
    * Instantiate a FileHandler and open the file designated by
    * <code>filename</code>. The opened filename will become the output
    * destination for this appender.
    * <p/>
    * <p>The file will be appended to.
    */
   public FileHandler(Formatter layout, String filename) throws IOException
   {
      this(layout, filename, true);
   }

   /**
    * The <b>File</b> property takes a string value which should be the
    * name of the file to append to.
    * <p/>
    * <p>Note: Actual opening of the file is made when {@link
    * #activateOptions} is called, not when the options are set.
    */
   public void setFile(String file)
   {
      // Trim spaces from both ends. The users probably does not want
      // trailing spaces in file names.
      String val = file.trim();
      fileName = val;
   }

   /**
    * Returns the value of the <b>Append</b> option.
    */
   public boolean getAppend()
   {
      return fileAppend;
   }


   /**
    * Returns the value of the <b>File</b> option.
    */
   public String getFile()
   {
      return fileName;
   }

   /**
    * If the value of <b>File</b> is not <code>null</code>, then {@link
    * #setFile} is called with the values of <b>File</b>  and
    * <b>Append</b> properties.
    *
    */
   public void activateOptions()
   {
      if (fileName != null)
      {
         try
         {
            setFile(fileName, fileAppend, bufferedIO, bufferSize);
         }
         catch (java.io.IOException e)
         {
            reportError("setFile(" + fileName + "," + fileAppend + ") call failed.",
               e, ErrorManager.OPEN_FAILURE);
         }
      }
      else
      {
         reportError("File option not set for appender [" + name + "]."
            +" Are you using FileHandler instead of ConsoleAppender?",
            null, ErrorManager.OPEN_FAILURE);
      }
   }

   /**
    * The <b>Append</b> option takes a boolean value. It is set to
    * <code>true</code> by default. If true, then <code>File</code>
    * will be opened in append mode by {@link #setFile setFile} (see
    * above). Otherwise, {@link #setFile setFile} will open
    * <code>File</code> in truncate mode.
    * <p/>
    * <p>Note: Actual opening of the file is made when {@link
    * #activateOptions} is called, not when the options are set.
    */
   public void setAppend(boolean flag)
   {
      fileAppend = flag;
   }

   /**
    * <p>Sets and <i>opens</i> the file where the log output will
    * go. The specified file must be writable.
    * <p/>
    * <p>If there was already an opened file, then the previous file
    * is closed first.
    * <p/>
    * <p><b>Do not use this method directly. To configure a FileHandler
    * or one of its subclasses, set its properties one by one and then
    * call activateOptions.</b>
    *
    * @param fileName The path to the log file.
    * @param append   If true will append to fileName. Otherwise will
    *                 truncate fileName.
    */
   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
      throws IOException
   {
//      reportError("setFile called: " + fileName + ", " + append, null, ErrorManager.GENERIC_FAILURE);
      super.setBufferedIO(bufferedIO);
      super.setBufferSize(bufferSize);
      FileOutputStream ostream = null;
      try
      {
         //   attempt to create file
         ostream = new FileOutputStream(fileName, append);
      }
      catch (FileNotFoundException ex)
      {
         //   if parent directory does not exist then
         //      attempt to create it and try to create file
         String parentName = new File(fileName).getParent();
         if (parentName != null)
         {
            File parentDir = new File(parentName);
            if (!parentDir.exists() && parentDir.mkdirs())
            {
               ostream = new FileOutputStream(fileName, append);
            }
            else
            {
               throw ex;
            }
         }
         else
         {
            throw ex;
         }
      }
      super.setOutputStream(ostream);
      this.fileName = fileName;
      this.fileAppend = append;
      // LogLog.debug("setFile ended");
   }

}

