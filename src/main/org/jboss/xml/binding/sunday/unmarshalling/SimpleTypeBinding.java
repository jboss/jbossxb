/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface SimpleTypeBinding
{
   SimpleTypeBinding NOOP = new SimpleTypeBinding()
   {
      public Object unmarshal(QName qName, String text)
      {
         return text;
      }
   };

   Object unmarshal(QName qName, String text);
}
