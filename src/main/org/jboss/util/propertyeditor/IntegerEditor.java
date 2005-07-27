/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;

/** A property editor for {@link java.lang.Integer}.
 *
 * @version $Revision$
 * @author Scott.Stark@jboss.org
 */
public class IntegerEditor extends PropertyEditorSupport
{
   /** Map the argument text into and Integer using Integer.decode.
    */
   public void setAsText(final String text)
   {
      Object newValue = Integer.decode(text);
      setValue(newValue);
   }
}
