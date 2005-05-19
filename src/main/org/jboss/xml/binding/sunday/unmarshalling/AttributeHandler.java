/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xml.binding.metadata.JaxbJavaType;
import org.jboss.xml.binding.metadata.JaxbBaseType;
import org.jboss.xml.binding.metadata.JaxbProperty;

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
      JaxbJavaType jaxbJavaType = null;
      JaxbProperty jaxbProperty = binding.getJaxbProperty();
      if(jaxbProperty != null)
      {
         JaxbBaseType baseType = jaxbProperty.getBaseType();
         jaxbJavaType = baseType == null ? null : baseType.getJavaType();
      }
      else if(type != null)
      {
         jaxbJavaType = type.getJaxbJavaType();
      }
      return type == null ? value : type.getSimpleType().unmarshal(attrName, type, nsCtx, jaxbJavaType, value);
   }

   public abstract void attribute(QName elemName,
                                  QName attrName,
                                  AttributeBinding binding,
                                  Object owner,
                                  Object value);
}
