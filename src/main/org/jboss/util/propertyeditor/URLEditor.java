/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.net.MalformedURLException;

import org.jboss.util.NestedRuntimeException;
import org.jboss.util.Strings;

/**
 * A property editor for {@link java.net.URL}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class URLEditor extends TextPropertyEditorSupport
{
   /**
    * Returns a URL for the input object converted to a string.
    *
    * @return a URL object
    *
    * @throws NestedRuntimeException   An MalformedURLException occured.
    */
   public Object getValue()
   {
      try
      {
         return Strings.toURL(getAsText());
      }
      catch (MalformedURLException e)
      {
         throw new NestedRuntimeException(e);
      }
   }
}
