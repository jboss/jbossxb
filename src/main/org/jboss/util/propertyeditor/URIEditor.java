/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.util.NestedRuntimeException;

/**
 * A property editor for {@link java.net.URI}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 */
public class URIEditor
   extends TextPropertyEditorSupport
{
   /**
    * Returns a URI for the input object converted to a string.
    *
    * @return a URI object
    *
    * @throws NestedRuntimeException   An MalformedURLException occured.
    */
   public Object getValue()
   {
      try {
         // TODO - more strict checking, like URLEditor
         return new URI(getAsText());
      }
      catch (URISyntaxException e) {
         throw new NestedRuntimeException(e);
      }
   }
}
