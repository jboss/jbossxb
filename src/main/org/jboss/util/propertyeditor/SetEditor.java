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
import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

import java.beans.PropertyEditorSupport;

/**
 * A property editor for {@link java.util.Set}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SetEditor
   extends PropertyEditorSupport
{
   protected Collection createCollection()
   {
      return createSet();
   }

   protected Set createSet()
   {
      return new HashSet();
   }

   protected void setValue(Set list)
   {
      super.setValue(list);
   }
   
   public void setValue(Collection bag)
   {
      setValue((Set)bag);
   }
}
