/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util;

/**
 * Clone the object without throwing a typed exception.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public interface JBossInterface extends Cloneable
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Clone the object
    * 
    * @return a clone of the object
    */
   Object clone();

   /**
    * Print a short version of the object
    * 
    * @return the short string
    */
   String toShortString();
   
   /**
    * Append the key class properties to the buffer
    * 
    * @param buffer the buffer
    */
   void toShortString(StringBuffer buffer);
 
   // Inner classes -------------------------------------------------
}
