/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;

/**
 * A property editor for {@link Integer}.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class FloatEditor extends PropertyEditorSupport
{
   /**
    * Map the argument text into and Integer using Integer.valueOf.
    */
   public void setAsText(final String text)
   {
      Object newValue = Float.valueOf(text);
      setValue(newValue);
   }
}
