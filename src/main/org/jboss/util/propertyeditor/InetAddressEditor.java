/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.util.Strings;

import org.jboss.util.NestedRuntimeException;

/**
 * A property editor for {@link java.net.InetAddress}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:Adrian.Brock@HappeningTimes.com">Adrian Brock</a>
 */
public class InetAddressEditor
   extends TextPropertyEditorSupport
{
   /**
    * Returns a InetAddress for the input object converted to a string.
    *
    * @return an InetAddress
    *
    * @throws NestedRuntimeException   An UnknownHostException occured.
    */
   public Object getValue()
   {
      try
      {
         return InetAddress.getByName(Strings.replaceProperties(getAsText()));
      }
      catch (UnknownHostException e)
     {
         throw new NestedRuntimeException(e);
      }
   }
}
