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

import java.util.List;
import java.lang.reflect.Array;
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
   public static CharactersHandler NOOP = new CharactersHandler()
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

   public static CharactersHandler DEFAULT = new CharactersHandler()
   {
   };

   protected UnmarshalCharactersHandler unmarshalHandler = DEFAULT_UNMARSHAL_HANDLER;
   
   public CharactersHandler()
   {
   }
   
   public CharactersHandler(UnmarshalCharactersHandler unmarshalHandler)
   {
      this.unmarshalHandler = unmarshalHandler;
   }
   
   public Object unmarshalEmpty(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData)
   {
      if(typeBinding.isIgnoreEmptyString())
         return null;
      
      Object result = "";
      QName typeQName = typeBinding.getQName();
      if(Constants.QNAME_BASE64BINARY.equals(typeQName))
      {
         result = new byte[0];
      }
      else if(Constants.QNAME_BOOLEAN.equals(typeQName))
      {// this should be an error but this hack is still here
       // for backwards compatibility in handling empty elements bound to boolean types
         result = null;
      }
      return result;
   }

   public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
   {
      return unmarshalHandler.unmarshal(qName, typeBinding, nsCtx, valueMetaData, value);
   }

   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
   }
   
   public static interface UnmarshalCharactersHandler
   {
      Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value);
   }

   public static class DefaultUnmarshalCharactersHandler implements UnmarshalCharactersHandler
   {
      public Object unmarshal(QName name, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
      {
         Object o;
         QName typeQName = typeBinding.getQName();
         TypeBinding itemType = typeBinding.getItemType();
         if(itemType != null)
         {
            QName itemTypeQName = itemType.getQName();
            ValueAdapter adapter = itemType.getValueAdapter();
            
            if(itemTypeQName == null || !Constants.NS_XML_SCHEMA.equals(itemTypeQName.getNamespaceURI()))
            {
               if(adapter == null)
                  throw new JBossXBRuntimeException(
                        "Only list types with item type from " + Constants.NS_XML_SCHEMA +
                        " namespace are supported currently."
                     );
               else
                  itemTypeQName = Constants.QNAME_STRING;
            }
            
            if(adapter == null)
               adapter = ValueAdapter.NOOP;

            List<?> list = SimpleTypeBindings.unmarshalList(itemTypeQName.getLocalPart(), value, nsCtx, adapter);
            if (typeBinding.getSchemaBinding().isUnmarshalListsToArrays())
            {
               if (list.isEmpty())
               {
                  Class<?> compType = SimpleTypeBindings.classForType(itemTypeQName.getLocalPart(), true);
                  o = Array.newInstance(compType, 0);
               }
               else
               {
                  Class<?> compType = list.get(0).getClass();
                  o = list.toArray((Object[]) Array.newInstance(compType, list.size()));
               }
            }
            else
            {
               o = list;
            }
         }
         else if(typeQName != null && Constants.NS_XML_SCHEMA.equals(typeQName.getNamespaceURI()))
         {
            try
            {
               o = SimpleTypeBindings.unmarshal(typeQName.getLocalPart(), value, nsCtx);
            }
            catch (IllegalStateException e)
            {
               throw new JBossXBRuntimeException("Characters are not allowed here", e);
            }
         }
         else
         {
            TypeBinding baseType = typeBinding.getBaseType();
            o = (baseType == null ? value : unmarshal(name, baseType, nsCtx, valueMetaData, value));
         }
         return o;
      }
   }
   
   public static final UnmarshalCharactersHandler NOOP_UNMARSHAL_HANDLER = new UnmarshalCharactersHandler()
   {
      public Object unmarshal(QName name, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
      {
         return value;
      }      
   };
   
   public static final UnmarshalCharactersHandler DEFAULT_UNMARSHAL_HANDLER = new DefaultUnmarshalCharactersHandler();
}
