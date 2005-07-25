/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.metadata.ValueMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class CharactersHandler
{
   public static final CharactersHandler NOOP = new CharactersHandler()
   {
      public Object unmarshal(QName qName,
                              TypeBinding typeBinding,
                              NamespaceContext nsCtx,
                              ValueMetaData valueMetaData,
                              String value)
      {
         return value;
      }
   };

   public static final CharactersHandler DEFAULT = new CharactersHandler()
   {
   };

   public Object unmarshalEmpty(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData)
   {
      Object result = null;
      QName typeQName = typeBinding.getQName();
      if(Constants.QNAME_STRING.equals(typeQName))
      {
         result = "";
      }
      return result;
   }

   public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
   {
      Object o;
      QName typeQName = typeBinding.getQName();
      if(typeQName != null && Constants.NS_XML_SCHEMA.equals(typeQName.getNamespaceURI()))
      {
         o = SimpleTypeBindings.unmarshal(typeQName.getLocalPart(), value, nsCtx);
      }
      else
      {
         TypeBinding baseType = typeBinding.getBaseType();
         o = (baseType == null ? value : unmarshal(qName, baseType, nsCtx, valueMetaData, value));
      }
      return o;
   }

   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
   }
}
