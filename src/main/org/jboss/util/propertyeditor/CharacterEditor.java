/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;

/**
 * A property editor for {@link java.lang.Character}.
 *
 * @todo REVIEW: look at possibly parsing escape sequences?
 * @version $Revision$
 * @author adrian@jboss.org
 */
public class CharacterEditor extends PropertyEditorSupport
{
   public void setAsText(final String text)
   {
      if (PropertyEditors.isNull(text))
      {
         setValue(null);
         return;
      }
      if (text.length() != 1)
         throw new IllegalArgumentException("Too many (" + text.length() + ") characters: '" + text + "'"); 
      Object newValue = Character.valueOf(text.charAt(0));
      setValue(newValue);
   }
}
