/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeErrorException;

/**
 * A simple helper to rethrow and/or decode those pesky 
 * JMX exceptions.
 *      
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Revision$
 */
public class JMXExceptionDecoder
{
   /**
    * Attempt to decode the given Throwable.  If it
    * is a container JMX exception, then the target
    * is returned.  Otherwise the argument is returned.
    */
   public static Throwable decode(final Exception e)
   {
      if (e instanceof MBeanException) {
         return ((MBeanException)e).getTargetException();
      }
      if (e instanceof ReflectionException) {
         return ((ReflectionException)e).getTargetException();
      }
      if (e instanceof RuntimeOperationsException) {
         return ((RuntimeOperationsException)e).getTargetException();
      }
      if (e instanceof RuntimeMBeanException) {
         return ((RuntimeMBeanException)e).getTargetException();
      }
      if (e instanceof RuntimeErrorException) {
         return ((RuntimeErrorException)e).getTargetError();
      }

      // can't decode
      return e;
   }

   /**
    * Decode and rethrow the given Throwable.  If it
    * is a container JMX exception, then the target
    * is thrown.  Otherwise the argument is thrown.
    */
   public static void rethrow(final Exception e)
      throws Exception
   {
      if (e instanceof MBeanException) {
         throw ((MBeanException)e).getTargetException();
      }
      if (e instanceof ReflectionException) {
         throw ((ReflectionException)e).getTargetException();
      }
      if (e instanceof RuntimeOperationsException) {
         throw ((RuntimeOperationsException)e).getTargetException();
      }
      if (e instanceof RuntimeMBeanException) {
         throw ((RuntimeMBeanException)e).getTargetException();
      }
      if (e instanceof RuntimeErrorException) {
         throw ((RuntimeErrorException)e).getTargetError();
      }

      // can't decode
      throw e;
   }
}
