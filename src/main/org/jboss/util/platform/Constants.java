/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.platform;

/**
 * Platform constants.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface Constants
{
   /** Platform dependent line separator. */
   String LINE_SEPARATOR = System.getProperty("line.separator");

   /** Platform dependant file separator. */
   String FILE_SEPARATOR = System.getProperty("file.separator");

   /** Platform dependant path separator. */
   String PATH_SEPARATOR = System.getProperty("path.separator");
}
