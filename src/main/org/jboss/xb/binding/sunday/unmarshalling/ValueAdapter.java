/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ValueAdapter
{
   ValueAdapter NOOP = new ValueAdapter()
   {
      public Object cast(Object o, Class c)
      {
         return o;
      }
   };

   /**
    * An implementation should make sure that an object past in as the first parameter
    * can be set as a value of a field that is of a type passed in as the second parameter
    *
    * @param o
    * @param c
    * @return
    */
   Object cast(Object o, Class c);
}
