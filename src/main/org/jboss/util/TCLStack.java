/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util;

import java.util.Stack;
import java.util.EmptyStackException;
import java.util.Iterator;

import org.jboss.logging.Logger;

import org.jboss.util.collection.Iterators;

/**
 * A thread context class loader (TCL) stack.
 *
 * <p>
 * Attempts to push or pop a CL that is the same at the current TCL will
 * be ignored.  In the case of an ingored push, the state is preserved such
 * that a pop will function as expected and not corrupt the stack.
 *
 * <p>
 * Also provides TRACE level logging for a better view of TCL usage and
 * provides an immutable view of the stack for inspection.
 * 
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TCLStack
{
   /** Class logger. */
   private static final Logger log = Logger.getLogger(TCLStack.class);
   
   /** The thread local stack of class loaders. */
   private static final ThreadLocal stackTL = new ThreadLocal()
      {
         protected Object initialValue()
         {
            return new Stack();
         }
      };

   /** Get the stack from the thread lcoal. */
   private static Stack getStack()
   {
      return (Stack)stackTL.get();
   }

   /**
    * Push the current TCL and set the given CL to the TCL.
    *
    * <p>If the given cl is the same as the current cl, then the TCL is not
    * changed, though the value will be pushed to allow poping to function.
    *
    * @param cl   The class loader to set as the TCL.
    */
   public static void push(final ClassLoader cl)
   {
      // push the old cl and set the new cl
      ClassLoader oldCL = Thread.currentThread().getContextClassLoader();

      if (cl != oldCL) {
         getStack().push(oldCL);
         Thread.currentThread().setContextClassLoader(cl);

         if (log.isTraceEnabled()) {
            log.trace("Setting TCL to " + cl + "; pushing " + oldCL);
         }
      }
      else {
         if (log.isTraceEnabled()) {
            log.trace("TCL to push is the same as current TCL; TCL will not be changed; pushing: " + oldCL);
         }
      }
   }

   /**
    * Pop the last CL from the stack and make it the TCL.
    *
    * <p>If the stack is empty, then no change is made to the TCL.
    *
    * <p>If the poped cl is the same as the current cl, then the TCL is not changed.
    *
    * @return   The previous CL or null if there was none.
    */
   public static ClassLoader pop()
   {
      // get the last cl in the stack & make it the current
      try {
         ClassLoader cl = (ClassLoader)getStack().pop();
         ClassLoader oldCL = Thread.currentThread().getContextClassLoader();

         if (cl != oldCL) {
            Thread.currentThread().setContextClassLoader(cl);

            if (log.isTraceEnabled()) {
               log.trace("Setting TCL to " + cl + "; popped: " + oldCL);
            }
         }
         else {
            if (log.isTraceEnabled()) {
               log.trace("TCL popped is the same as current TCL; TCL will not be changed; popped: " + oldCL);
            }
         }
         
         return oldCL;
      }
      catch (EmptyStackException ignore) {
         log.warn("Attempt to pop empty stack ingored", ignore);
         return null;
      }
   }

   /**
    * Return the size of the TCL stack.
    */
   public static int size()
   {
      return getStack().size();
   }

   /**
    * Return an immutable iterator over the TCL stack elements.
    */
   public static Iterator iterator()
   {
      return Iterators.makeImmutable(getStack().iterator());
   }

   /**
    * Return the CL in the stack at the given index.
    */
   public static ClassLoader get(final int index)
      throws ArrayIndexOutOfBoundsException
   {
      return (ClassLoader)getStack().get(index);
   }
}
