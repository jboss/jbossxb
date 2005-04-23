/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class AttributeHandler
{
   public static final AttributeHandler NOOP = new AttributeHandler()
   {
      public void attribute(QName elemName, QName attrName, Object owner, Object value)
      {
      }
   };

   public Object unmarshal(QName elemName, QName attrName, TypeBinding type, String value, NamespaceContext nsCtx)
   {
      return type == null ? value : type.getSimpleType().unmarshal(attrName, type.getQName(), nsCtx, value);
   }

   public abstract void attribute(QName elemName, QName attrName, Object owner, Object value);
}
