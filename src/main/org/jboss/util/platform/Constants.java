/*
 * JBoss, Home of Professional Open Source
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.platform;

import org.jboss.util.property.Property;

/**
 * Platform constants.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author  <a href="mailto:adrian@jboss.com">Adrian Brock</a>
 */
public interface Constants
{
   /** Platform dependent line separator. */
   String LINE_SEPARATOR = Property.LINE_SEPARATOR;

   /** Platform dependant file separator. */
   String FILE_SEPARATOR = Property.FILE_SEPARATOR;

   /** Platform dependant path separator. */
   String PATH_SEPARATOR = Property.PATH_SEPARATOR;
}
