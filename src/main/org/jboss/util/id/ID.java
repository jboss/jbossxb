/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.id;

import java.io.Serializable;

/**
 * A tagging interface for an identifier.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface ID
   extends Serializable, Cloneable
{
   /**
    * Expose clone as a public method.
    */
   Object clone();
}
