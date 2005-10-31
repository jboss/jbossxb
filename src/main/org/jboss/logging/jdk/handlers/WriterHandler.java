package org.jboss.logging.jdk.handlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A base handler that outputs log messages to a Writer
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class WriterHandler extends HandlerSkeleton
{
   /**
    * Immediate flush means that the underlying writer or output stream
    * will be flushed at the end of each append operation. Immediate
    * flush is slower but ensures that each append request is actually
    * written. If <code>immediateFlush</code> is set to
    * <code>false</code>, then there is a good chance that the last few
    * logs events are not actually written to persistent media if and
    * when the application crashes.
    * <p/>
    * <p>The <code>immediateFlush</code> variable is set to
    * <code>true</code> by default.
    */
   protected boolean immediateFlush = true;
   /**
    * Do we do bufferedIO?
    */
   protected boolean bufferedIO = false;
   /**
    * Determines the size of IO buffer be. Default is 8K.
    */
   protected int bufferSize = 8 * 1024;

   private OutputStream msgOutput;
   private Writer msgWriter;
   /**
    * Has the
    */
   private boolean wroteHeader;

   public WriterHandler()
   {
      super();
   }

   public WriterHandler(OutputStream output, Formatter formatter)
   {
      setFormatter(formatter);
      setOutputStream(output);
   }

   /**
    * If the <b>ImmediateFlush</b> option is set to
    * <code>true</code>, the appender will flush at the end of each
    * write. This is the default behavior. If the option is set to
    * <code>false</code>, then the underlying stream can defer writing
    * to physical medium to a later time.
    * <p/>
    * <p>Avoiding the flush operation at the end of each append results in
    * a performance gain of 10 to 20 percent. However, there is safety
    * tradeoff involved in skipping flushing. Indeed, when flushing is
    * skipped, then it is likely that the last few log events will not
    * be recorded on disk when the application exits. This is a high
    * price to pay even for a 20% performance gain.
    */
   public void setImmediateFlush(boolean value)
   {
      immediateFlush = value;
   }

   /**
    * Returns value of the <b>ImmediateFlush</b> option.
    */
   public boolean getImmediateFlush()
   {
      return immediateFlush;
   }

   public boolean isBufferedIO()
   {
      return bufferedIO;
   }

   /**
    * The <b>BufferedIO</b> option takes a boolean value. It is set to
    * <code>false</code> by default. If true, then <code>File</code>
    * will be opened and the resulting {@link java.io.Writer} wrapped
    * around a {@link java.io.BufferedWriter}.
    * <p/>
    * BufferedIO will significatnly increase performance on heavily
    * loaded systems.
    */
   public void setBufferedIO(boolean bufferedIO)
   {
      this.bufferedIO = bufferedIO;
      if (bufferedIO)
      {
         immediateFlush = false;
      }
   }

   public int getBufferSize()
   {
      return bufferSize;
   }

   /**
    * Set the size of the IO buffer.
    */
   public void setBufferSize(int bufferSize)
   {
      this.bufferSize = bufferSize;
   }

   public void setEncoding(String encoding)
      throws SecurityException, UnsupportedEncodingException
   {
      super.setEncoding(encoding);
      if (msgOutput == null)
      {
         return;
      }
      // Replace the current writer with a writer for the new encoding.
      flush();
      if (encoding == null)
      {
         msgWriter = new OutputStreamWriter(msgOutput);
      }
      else
      {
         msgWriter = new OutputStreamWriter(msgOutput, encoding);
      }
   }

   public synchronized void flush()
   {
      if (msgWriter != null)
      {
         try
         {
            msgWriter.flush();
         }
         catch (IOException e)
         {
            reportError("Failed to flush writer", e, ErrorManager.FLUSH_FAILURE);
         }
      }
   }

   public synchronized void close()
   {
      if (msgWriter != null)
      {
         try
         {
            if (!wroteHeader)
            {
               msgWriter.write(getFormatter().getHead(this));
               wroteHeader = true;
            }
            msgWriter.write(getFormatter().getTail(this));
            msgWriter.flush();
            msgWriter.close();
         }
         catch (Exception ex)
         {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.CLOSE_FAILURE);
         }
         msgWriter = null;
         msgOutput = null;
      }

   }

   public void publish(LogRecord record)
   {
      if(checkEntryConditions(record) == false)
      {
         return;
      }
      subPublish(record);
   }

   protected boolean checkEntryConditions(LogRecord record)
   {
      boolean canWrite = super.isLoggable(record);
      if( canWrite )
      {
         canWrite = msgWriter != null;
      }
      return canWrite;
   }

   /**
    * Actual writing occurs here.
    * Most subclasses of WriterHandler will need to
    * override this method.
    */
   protected void subPublish(LogRecord record)
   {
      Formatter fmt = getFormatter();
      String msg = fmt.format(record);
      synchronized (this)
      {
         try
         {
            msgWriter.write(msg);
         }
         catch (IOException e)
         {
            reportError("Failed to publish recored", e, ErrorManager.WRITE_FAILURE);
         }
         if (this.immediateFlush)
         {
            flush();
         }
      }
   }

   /**
    * Change the output stream.
    * <p/>
    * If there is a current output stream then the <tt>Formatter</tt>'s
    * tail string is written and the stream is flushed and closed.
    * Then the output stream is replaced with the new output stream.
    *
    * @param out New output stream.  May not be null.
    * @throws SecurityException if a security manager exists and if
    *                           the caller does not have <tt>LoggingPermission("control")</tt>.
    */
   protected synchronized void setOutputStream(OutputStream out)
   {
      if (out == null)
      {
         throw new NullPointerException("The out argument cannot be null");
      }
      close();
      msgOutput = out;
      wroteHeader = false;
      String encoding = getEncoding();
      if (encoding == null)
      {
         msgWriter = new OutputStreamWriter(msgOutput);
      }
      else
      {
         try
         {
            msgWriter = new OutputStreamWriter(msgOutput, encoding);
         }
         catch (UnsupportedEncodingException ex)
         {
            // This shouldn't happen.  The setEncoding method
            // should have validated that the encoding is OK.
            throw new Error("Unexpected exception " + ex);
         }
      }
      if (bufferedIO)
      {
         msgWriter = new BufferedWriter(msgWriter, bufferSize);
      }
   }


}
