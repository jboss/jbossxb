/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;

/** A property editor for {@link java.lang.Byte}.
 *
 * @version $Revision$
 */
public class ByteEditor extends PropertyEditorSupport
{
   /** Map the argument text into and Byte using Byte.decode.
    */
   public void setAsText(final String text)
   {
      Object newValue = Byte.decode(text);
      setValue(newValue);
   }

}

