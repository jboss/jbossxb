/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

import org.jboss.xb.binding.metadata.ValueMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class AttributeHandler
{
   public static final AttributeHandler NOOP = new AttributeHandler()
   {
      public void attribute(QName elemName, QName attrName, AttributeBinding binding, Object owner, Object value)
      {
      }
   };

   public Object unmarshal(QName elemName,
                           QName attrName,
                           AttributeBinding binding,
                           NamespaceContext nsCtx,
                           String value)
   {
      TypeBinding type = binding.getType();
      ValueMetaData valueMetaData = binding.getValueMetaData();
      if(valueMetaData == null)
      {
         valueMetaData = type.getValueMetaData();
      }

      return type == null ? value : type.getSimpleType().unmarshal(attrName, type, nsCtx, valueMetaData, value);
   }

   public abstract void attribute(QName elemName,
                                  QName attrName,
                                  AttributeBinding binding,
                                  Object owner,
                                  Object value);
}
