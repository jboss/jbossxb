/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jboss.util.NestedRuntimeException;

/**
 * A property editor for {@link java.util.Date}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author  <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @author Scott.Stark@jboss.org
 */
public class DateEditor
   extends TextPropertyEditorSupport
{
   static DateFormat[] formats;

   static
   {
      String defaultFormat = System.getProperty("org.jboss.util.propertyeditor.DateEditor.format",
         "MMM d, yyyy");
      formats = new DateFormat[] {
         new SimpleDateFormat(defaultFormat),
         // Tue Jan 04 00:00:00 PST 2005
         new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy"),
         // Wed, 4 Jul 2001 12:08:56 -0700
         new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")
      };
   }

   /**
    * Returns a Date for the input object converted to a string. It tries
    * the default date format as specified by the following SimpleDateFormat
    * formats in order:
    * 
    * org.jboss.util.propertyeditor.DateEditor.format system property
    * (or MMM d, yyyy, if not specified),
    * EEE MMM d HH:mm:ss z yyyy
    * EEE, d MMM yyyy HH:mm:ss Z
    * @return a Date object
    *
    */
   public Object getValue()
   {
      int n = 0;
      ParseException ex = null;
      do
      {
         try
         {
            DateFormat df = formats[n];
            return df.parse(getAsText());
         }
         catch (ParseException e)
         {
            ex = e;
         }
         n ++;
      } while( n < formats.length );
      throw new NestedRuntimeException(ex);
   }
}
