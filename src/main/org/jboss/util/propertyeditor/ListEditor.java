/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.propertyeditor;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * A property editor for {@link java.util.List}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ListEditor
   extends CollectionEditor
{
   protected Collection createCollection()
   {
      return createList();
   }

   protected List createList()
   {
      return new LinkedList();
   }
}
