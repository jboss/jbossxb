/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * Interface which is implemented by all the nested throwable flavors.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface NestedThrowable
   extends Serializable
{
   /**
    * Return the nested throwable.
    *
    * @return  Nested throwable.
    */
   Throwable getNested();

   /**
    * Return the nested <tt>Throwable</tt>.
    *
    * <p>For JDK 1.4 compatibility.
    *
    * @return  Nested <tt>Throwable</tt>.
    */
   Throwable getCause();


   /////////////////////////////////////////////////////////////////////////
   //                      Nested Throwable Utilities                     //
   /////////////////////////////////////////////////////////////////////////

   /**
    * Utilitiy methods for the various flavors of
    * <code>NestedThrowable</code>.
    */
   final class Util 
   {
      /**
       * Returns a formated message for the given detail message
       * and nested <code>Throwable</code>.
       *
       * @param msg     Detail message.
       * @param nested  Nested <code>Throwable</code>.
       * @return        Formatted message.
       */
      public static String getMessage(final String msg,
                                      final Throwable nested)
      {
         StringBuffer buff = new StringBuffer(msg == null ? "" : msg);

         if (nested != null) {
            buff.append(msg == null ? "- " : "; - ")
               .append("nested throwable: (")
               .append(nested)
               .append(")");
         }
 
         return buff.toString();
      }

      /**
       * Prints the nested <code>Throwable</code> to the given stream.
       *
       * @param nested  Nested <code>Throwable</code>.
       * @param stream  Stream to print to.
       */
      public static void print(final Throwable nested,
                               final PrintStream stream)
      {
         if (stream == null)
            throw new NullArgumentException("stream");

         if (nested != null) {
            synchronized (stream) {
               stream.print(" + nested throwable: ");
               nested.printStackTrace();
            }
         }
      }

      /**
       * Prints the nested <code>Throwable</code> to the given writer.
       *
       * @param nested  Nested <code>Throwable</code>.
       * @param writer  Writer to print to.
       */
      public static void print(final Throwable nested,
                               final PrintWriter writer)
      {
         if (writer == null)
            throw new NullArgumentException("writer");

         if (nested != null) {
            synchronized (writer) {
               writer.print(" + nested throwable: ");
               nested.printStackTrace();
            }
         }
      }
   }
}
