/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.deployment.vdf.spi;

/**
 * Generic Virtual Deployment Framework Exception 
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public class VDFException extends Exception
{
   /**
    * Constructs a <code>VDFException</code> with <code>null</code>
    * as its error detail message.
    */
   public VDFException()
   {
      super();
   }

   /**
    * Constructs a <code>VDFException</code> with the specified detail
    * message. The error message string <code>s</code> can later be
    * retrieved by the <code>{@link java.lang.Throwable#getMessage}</code>
    * method of class <code>java.lang.Throwable</code>.
    */
   public VDFException(String s)
   {
      super(s);
   }
   
   /**
    * Constructs a <code>VDFException</code> with the specified detail message and
    * cause.  <p>Note that the detail message associated with
    * <code>cause</code> is <i>not</i> automatically incorporated in
    * this exception's detail message.
    */   
   public VDFException(String s, Throwable cause)
   {
      super(s, cause);
   }
   
   /**
     * Constructs a <code>VDFException</code> with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
    */
   public VDFException(Throwable cause)
   {
      super(cause);
   }
}
