/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.JBossXBRuntimeException;
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
      else if(Constants.QNAME_BASE64BINARY.equals(typeQName))
      {
         result = new byte[0];
      }
      return result;
   }

   public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
   {
      Object o;
      QName typeQName = typeBinding.getQName();
      TypeBinding itemType = typeBinding.getItemType();
      if(itemType != null)
      {
         QName itemTypeQName = itemType.getQName();
         if(itemTypeQName != null && Constants.NS_XML_SCHEMA.equals(itemTypeQName.getNamespaceURI()))
         {
            o = SimpleTypeBindings.unmarshalList(itemTypeQName.getLocalPart(), value, nsCtx);
         }
         else
         {
            // todo
            throw new JBossXBRuntimeException(
               "Only list types with item type from " + Constants.NS_XML_SCHEMA +
               " namespace are supported currently."
            );
         }
      }
      else if(typeQName != null && Constants.NS_XML_SCHEMA.equals(typeQName.getNamespaceURI()))
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
