/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.util.Set;
import java.util.TreeSet;

/**
 * A property editor for {@link java.util.TreeSet}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TreeSetEditor
   extends SetEditor
{
   protected Set createSet()
   {
      return new TreeSet();
   }
}
