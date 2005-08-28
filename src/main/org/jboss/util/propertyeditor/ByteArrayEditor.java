/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;

/** A property editor for byte[].
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class ByteArrayEditor extends PropertyEditorSupport
{
   /** Map the argument text into and Byte using Byte.decode.
    */
   public void setAsText(final String text)
   {
      if (PropertyEditors.isNull(text, false, false))
      {
         setValue(null);
         return;
      }
      Object newValue = text.getBytes();
      setValue(newValue);
   }
}
