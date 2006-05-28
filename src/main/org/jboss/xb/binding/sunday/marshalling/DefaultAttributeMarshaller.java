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
package org.jboss.xb.binding.sunday.marshalling;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DefaultAttributeMarshaller
   extends AbstractAttributeMarshaller
{
   private final Logger log = Logger.getLogger(AttributeMarshaller.class);

   public static final DefaultAttributeMarshaller INSTANCE = new DefaultAttributeMarshaller();
   
   public Object getValue(MarshallingContext ctx)
   {
      Object owner = ctx.peek();
      SchemaBinding schema = ctx.getSchemaBinding();
      AttributeBinding binding = ctx.getAttributeBinding();
      QName qName = binding.getQName();

      String fieldName = null;
      PropertyMetaData propertyMetaData = binding.getPropertyMetaData();
      if(propertyMetaData != null)
      {
         fieldName = propertyMetaData.getName();
      }

      if(fieldName == null)
      {
         fieldName =
            Util.xmlNameToFieldName(qName.getLocalPart(), schema.isIgnoreLowLine());
      }

      Method getter = null;
      Field field = null;
      Class fieldType = null;

      try
      {
         getter = Classes.getAttributeGetter(owner.getClass(), fieldName);
         fieldType = getter.getReturnType();
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            field = owner.getClass().getField(fieldName);
            fieldType = field.getType();
         }
         catch(NoSuchFieldException e3)
         {
            if(schema.isIgnoreUnresolvedFieldOrClass())
            {
               if(log.isTraceEnabled())
               {
                  log.trace("Found neither field " +
                     fieldName +
                     " nor its getter in " +
                     owner.getClass() +
                     " for element/attribute " + qName
                  );
               }
            }
            else
            {
               throw new JBossXBRuntimeException("Found neither field " +
                  fieldName +
                  " nor its getter in " +
                  owner.getClass() +
                  " for element/attribute " + qName
               );
            }
         }
      }

      Object value = null;
      if(fieldType != null)
      {
         if(getter != null)
         {
            try
            {
               value = getter.invoke(owner, null);
            }
            catch(Exception e)
            {
               log.error("Failed to invoke getter '" + getter + "' on object: " + owner);
               throw new JBossXBRuntimeException("Failed to provide value for " + qName + " from " + owner, e);
            }
         }
         else
         {
            try
            {
               value = field.get(owner);
            }
            catch(Exception e)
            {
               log.error("Failed to invoke get on field '" + field + "' on object: " + owner);
               throw new JBossXBRuntimeException("Failed to provide value for " + qName + " from " + owner, e);
            }
         }
      }

      return value;
   }

   public String marshalValue(MarshallingContext ctx, Object value)
   {
      if(value == null)
      {
         return null;
      }

      String marshalled;

      AttributeBinding binding = ctx.getAttributeBinding();
      TypeBinding attrType = binding.getType();

      if(attrType.getItemType() != null)
      {
         TypeBinding itemType = attrType.getItemType();
         if(Constants.NS_XML_SCHEMA.equals(itemType.getQName().getNamespaceURI()))
         {
            List list;
            if(value instanceof List)
            {
               list = (List)value;
            }
            else if(value.getClass().isArray())
            {
               list = Arrays.asList((Object[])value);
            }
            else
            {
               throw new JBossXBRuntimeException("Expected value for list type is an array or " +
                  List.class.getName() +
                  " but got: " +
                  value
               );
            }

            if(Constants.QNAME_QNAME.getLocalPart().equals(itemType.getQName().getLocalPart()))
            {
               String attrLocal = binding.getQName().getLocalPart();
               for(int listInd = 0; listInd < list.size(); ++listInd)
               {
                  QName item = (QName)list.get(listInd);
                  String itemNs = item.getNamespaceURI();
                  if(itemNs != null && itemNs.length() > 0)
                  {
                     String itemPrefix = ctx.getPrefix(itemNs);
                     if(itemPrefix == null)
                     {
                        itemPrefix = attrLocal + listInd;
                        ctx.declareNamespace(itemPrefix, itemNs);
                     }

                     item = new QName(item.getNamespaceURI(), item.getLocalPart(), itemPrefix);
                     list.set(listInd, item);
                  }
               }
            }

            marshalled = SimpleTypeBindings.marshalList(itemType.getQName().getLocalPart(), list, null);
         }
         else
         {
            throw new JBossXBRuntimeException("Marshalling of list types with item types not from " +
               Constants.NS_XML_SCHEMA + " is not supported."
            );
         }
      }
      else if(attrType.getLexicalPattern() != null &&
         attrType.getBaseType() != null &&
         Constants.QNAME_BOOLEAN.equals(attrType.getBaseType().getQName()))
      {
         String item = (String)attrType.getLexicalPattern().get(0);
         if(item.indexOf('0') != -1 && item.indexOf('1') != -1)
         {
            marshalled = ((Boolean)value).booleanValue() ? "1" : "0";
         }
         else
         {
            marshalled = ((Boolean)value).booleanValue() ? "true" : "false";
         }
      }
      else
      {
         marshalled = value.toString();
      }

      return marshalled;
   }
}
