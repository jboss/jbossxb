/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A static singleton that handles processing throwables that otherwise would
 * be ignored or dumped to System.err.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class ThrowableHandler
{
   /**
    * Do not allow direct public instantiation of this class.
    */
   private ThrowableHandler() {}


   /////////////////////////////////////////////////////////////////////////
   //                            Listener Methods                         //
   /////////////////////////////////////////////////////////////////////////

   /** The list of listeners */
   protected static List listeners = Collections.synchronizedList(new ArrayList());

   /**
    * Add a ThrowableListener to the listener list.  Listener is added only
    * if if it is not already in the list.
    *
    * @param listener   ThrowableListener to add to the list.
    */
   public static void addThrowableListener(ThrowableListener listener) {
      // only add the listener if it isn't already in the list
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   /**
    * Remove a ThrowableListener from the listener list.
    *
    * @param listener   ThrowableListener to remove from the list.
    */
   public static void removeThrowableListener(ThrowableListener listener) {
      listeners.remove(listener);
   }

   /**
    * Fire onThrowable to all registered listeners.
    *
    * @param t    Throwable
    */
   protected static void fireOnThrowable(Throwable t) {
      Object[] list = listeners.toArray();

      for (int i=0; i<list.length; i++) {
         ((ThrowableListener)list[i]).onThrowable(t);
      }
   }


   /////////////////////////////////////////////////////////////////////////
   //                          Throwable Processing                       //
   /////////////////////////////////////////////////////////////////////////

   /**
    * Add a throwable that is to be handled.
    *
    * @param t    Throwable to be handled.
    */
   public static void add(Throwable t) {
      // don't add null throwables
      if (t == null) return;

      fireOnThrowable(t);
   }
}
