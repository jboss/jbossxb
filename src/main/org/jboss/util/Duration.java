/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util;

/**
 * An abstraction of the time during which something exists or lasts.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Duration
   extends MuLong
{
   /**
    * Default constructor.
    */
   public Duration()
   {
      super(0);
   }

   /**
    * Construct a duration.
    *
    * @param time   The time value for the duration.
    */
   public Duration(final long time)
   {
      super(time);
   }

   /**
    * Construct a duration.
    *
    * @param time   The time value for the duration.
    */
   public Duration(final Number time)
   {
      super(time.longValue());
   }

   /** jason: eventually fix these
   
   public long getYears()
   {
      return 0;
   }

   public long getMonths()
   {
      return 0;
   }
   
   public long getWeeks()
   {
      return 0;
   }
   
   public long getDays()
   {
      return 0;
   }
   
   public long getHours()
   {
      return 0;
   }
   
   public long getMinutes()
   {
      return 0;
   }
   
   public long getSeconds()
   {
      return 0;
   }
   
   public long getMilliSeconds()
   {
      return 0;
   }
   */

   public static final long ONE_YEAR = 2903040000L;
   public static final long ONE_MONTH = 241920000;
   public static final long ONE_WEEK = 60480000;
   public static final long ONE_DAY = 8640000;
   public static final long ONE_HOUR = 3600000;
   public static final long ONE_MINUTE = 600000;
   public static final long ONE_SECOND = 1000;
   public static final long ONE_MILLISECOND = 1;
   
   public String toString()
   {
      StringBuffer buff = new StringBuffer();

      //
      // jason: Bah... must be a better way
      //
      
      long y = value / ONE_YEAR;
      long mo = (value - (y * ONE_YEAR)) / ONE_MONTH;
      long w = (value - (y * ONE_YEAR) - (mo * ONE_MONTH)) / ONE_WEEK;
      long d = (value - (y * ONE_YEAR) - (mo * ONE_MONTH) - (w * ONE_WEEK)) / ONE_DAY;
      long h = (value - (y * ONE_YEAR) - (mo * ONE_MONTH) - (w * ONE_WEEK) - (d * ONE_DAY)) / ONE_HOUR;
      long m = (value - (y * ONE_YEAR) - (mo * ONE_MONTH) - (w * ONE_WEEK) - (d * ONE_DAY) - (h * ONE_HOUR)) / ONE_MINUTE;
      long s = (value - (y * ONE_YEAR) - (mo * ONE_MONTH) - (w * ONE_WEEK) - (d * ONE_DAY) - (h * ONE_HOUR) - (m * ONE_MINUTE)) / ONE_SECOND;
      long ms =  (value - (y * ONE_YEAR) - (mo * ONE_MONTH) - (w * ONE_WEEK) - (d * ONE_DAY) - (h * ONE_HOUR) - (m * ONE_MINUTE) - (s * ONE_SECOND));
      
      char spacer = ':';
      
      if (y != 0) {
         buff.append(y).append("y");
      }
      if (mo != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(mo).append("mo");
      }
      if (w != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(w).append("w");
      }
      if (d != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(d).append("d");
      }
      if (h != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(h).append("h");
      }
      if (m != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(m).append("m");
      }
      if (s != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(s).append("s");
      }
      if (ms != 0) {
         if (buff.length() != 0) buff.append(spacer);
         buff.append(ms).append("ms");
      }

      return buff.toString();
   }

   public static Duration parseDuration(final String text)
   {
      // for now...
      return new Duration(Long.parseLong(text));      
   }
   
   public static class PropertyEditor
      extends java.beans.PropertyEditorSupport
   {
      public void setAsText(final String text)
      {
         setValue(parseDuration(text));
      }
   }
}
