/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.util.List;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * A property editor for String[].
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * total code replacement...
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 */
public class StringArrayEditor
   extends TextPropertyEditorSupport
{
   /**
    * Returns a String[] by spliting up the input string where 
    * elements are separated by commas.
    *
    * @return a URL object
    *
    * @throws NestedRuntimeException   An MalformedURLException occured.
    */
   public void setAsText(String text)
   {
      if (text == null || text.length() == 0) 
      {
	 setValue(null);
      } // end of if ()
      else 
      {
	 StringTokenizer stok = new StringTokenizer(text, ",");
	 List list = new LinkedList();
      
	 while (stok.hasMoreTokens()) 
	 {
	    list.add(stok.nextToken());
	 }

	 setValue((String[])list.toArray(new String[list.size()]));
      } // end of else
   }

   public String getAsText()
   {
      String[] strings = (String[])getValue();
      if (strings == null || strings.length == 0)
      {
	 return null; 
      } // end of if ()
      StringBuffer result = new StringBuffer(strings[0]);
      for (int i = 1; i < strings.length; i++)
      {
	 result.append(",").append(strings[i]); 
      } // end of for ()
      return result.toString();
   }

}
