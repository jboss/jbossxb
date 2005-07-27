/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;

/** A property editor for {@link java.lang.Short}.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class ShortEditor extends PropertyEditorSupport
{
   /** Map the argument text into and Short using Short.decode.
    */
   public void setAsText(final String text)
   {
      Object newValue = Short.decode(text);
      setValue(newValue);
   }

}
