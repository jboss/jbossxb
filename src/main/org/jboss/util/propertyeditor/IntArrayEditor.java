/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.util.StringTokenizer;
import java.beans.PropertyEditorSupport;

/**
 * A property editor for int[].
 *
 * @version <tt>$Revision$</tt>
 */
public class IntArrayEditor
   extends PropertyEditorSupport
{
   /** Build a int[] from comma or eol seperated elements
    *
    */
   public void setAsText(final String text)
   {
      StringTokenizer stok = new StringTokenizer(text, ",\r\n");
      int[] theValue = new int[stok.countTokens()];
      int i = 0;
      while (stok.hasMoreTokens())
      {
         theValue[i++] = Integer.decode(stok.nextToken()).intValue();
      }
      setValue(theValue);
   }

   /**
    * @return a comma seperated string of the array elements
    */
   public String getAsText()
   {
      int[] theValue = (int[]) getValue();
      StringBuffer text = new StringBuffer();
      int length = theValue == null ? 0 : theValue.length;
      for(int n = 0; n < length; n ++)
      {
         if (n > 0)
            text.append(',');
         text.append(theValue[n]);
      }
      return text.toString();
   }
}
