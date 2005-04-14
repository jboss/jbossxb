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
public abstract class AttributeHandler
{
   public Object unmarshal(QName elemName, QName attrName, SimpleTypeBinding type, String value)
   {
      return type == null ? value : type.unmarshal(attrName, value);
   }

   public abstract void attribute(QName elemName, QName attrName, Object owner, Object value);
}
