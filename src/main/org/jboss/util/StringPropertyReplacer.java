/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util;

import java.util.Properties;

/**
 * A utility class for replacing properties in strings. 
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="Scott.Stark@jboss.org">Scott Stark</a>
 * @author <a href="claudio.vesco@previnet.it">Claudio Vesco</a>
 * @author  <a href="mailto:adrian@jboss.com">Adrian Brock</a>
 */
public final class StringPropertyReplacer
{
   /** New line string constant */
   public static final String NEWLINE = org.jboss.util.platform.Constants.LINE_SEPARATOR;

   /** File separator value */
   private static final String FILE_SEPARATOR = org.jboss.util.platform.Constants.FILE_SEPARATOR;

   /** Path separator value */
   private static final String PATH_SEPARATOR = org.jboss.util.platform.Constants.PATH_SEPARATOR;

   /** File separator alias */
   private static final String FILE_SEPARATOR_ALIAS = "/";

   /** Path separator alias */
   private static final String PATH_SEPARATOR_ALIAS = ":";

   // States used in property parsing
   private static final int NORMAL = 0;
   private static final int SEEN_DOLLAR = 1;
   private static final int IN_BRACKET = 2;

   /**
    * Go through the input string and replace any occurance of ${p} with
    * the System.getProperty(p) value. If there is no such property p defined, then
    * the ${p} reference will remain unchanged. The property ${/} is replaced with
    * System.getProperty("file.separator") value and the property ${:} is replaced with
    * system.getProperty("path.separator").
    *
    * @param string - the string with possible ${} references
    * @return the input string with all property references replaced if any.
    *    If there are no valid references the input string will be returned.
    */
   public static String replaceProperties(final String string)
   {
      return replaceProperties(string, System.getProperties());
   }

   /**
    * Go through the input string and replace any occurance of ${p} with
    * the System.getProperty(p) value. If there is no such property p defined, then
    * the ${p} reference will remain unchanged. The property ${/} is replaced with
    * System.getProperty("file.separator") value and the property ${:} is replaced with
    * system.getProperty("path.separator").
    *
    * @param string - the string with possible ${} references
    * @param props - the source for ${x} property ref values
    * @return the input string with all property references replaced if any.
    *    If there are no valid references the input string will be returned.
    */
   public static String replaceProperties(final String string, final Properties props)
   {
      final char[] chars = string.toCharArray();
      StringBuffer buffer = new StringBuffer();
      boolean properties = false;
      int state = NORMAL;
      int start = 0;
      for (int i = 0; i < chars.length; ++i)
      {
         char c = chars[i];

         // Dollar sign outside brackets
         if (c == '$' && state != IN_BRACKET)
            state = SEEN_DOLLAR;

         // Open bracket immediatley after dollar
         else if (c == '{' && state == SEEN_DOLLAR)
         {
            buffer.append(string.substring(start, i - 1));
            state = IN_BRACKET;
            start = i - 1;
         }

         // No open bracket after dollar
         else if (state == SEEN_DOLLAR)
            state = NORMAL;

         // Closed bracket after open bracket
         else if (c == '}' && state == IN_BRACKET)
         {
            // No content
            if (start + 2 == i)
            {
               buffer.append("${}"); // REVIEW: Correct?
            }
            else // Collect the system property
            {
               String value = null;

               String key = string.substring(start + 2, i);
               
               // check for alias
               if (FILE_SEPARATOR_ALIAS.equals(key))
               {
                  value = FILE_SEPARATOR;
               }
               else if (PATH_SEPARATOR_ALIAS.equals(key))
               {
                  value = PATH_SEPARATOR;
               }
               else
               {
                  // check from System properties
                  value = props.getProperty(key);
               }

               if (value != null)
               {
                  properties = true;
                  buffer.append(value);
               }
            }
            start = i + 1;
            state = NORMAL;
         }
      }

      // No properties
      if (properties == false)
         return string;

      // Collect the trailing characters
      if (start != chars.length)
         buffer.append(string.substring(start, chars.length));

      // Done
      return buffer.toString();
   }
}