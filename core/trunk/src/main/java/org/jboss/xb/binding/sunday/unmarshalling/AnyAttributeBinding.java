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

import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.sunday.marshalling.AttributeMarshaller;
import org.jboss.xb.binding.sunday.marshalling.DefaultAttributeMarshaller;

/**
 * A AnyAttributeBinding.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class AnyAttributeBinding
{
   private final SchemaBinding schema;
   private AnyAttributeHandler handler;
   private AttributeMarshaller marshaller = DefaultAttributeMarshaller.INSTANCE;
   private PropertyMetaData propertyMetaData;
   private ValueAdapter valueAdapter;
   protected Boolean normalizeSpace;
   
   public AnyAttributeBinding(SchemaBinding schema, AnyAttributeHandler handler)
   {
      super();
      this.schema = schema;
      this.handler = handler;
   }

   public AnyAttributeHandler getHandler()
   {
      return handler;
   }

   public void setHandler(AnyAttributeHandler handler)
   {
      this.handler = handler;
   }

   public AttributeMarshaller getMarshaller()
   {
      return marshaller;
   }

   public void setMarshaller(AttributeMarshaller marshaller)
   {
      this.marshaller = marshaller;
   }

   public PropertyMetaData getPropertyMetaData()
   {
      return propertyMetaData;
   }

   public void setPropertyMetaData(PropertyMetaData propertyMetaData)
   {
      this.propertyMetaData = propertyMetaData;
   }

   public ValueAdapter getValueAdapter()
   {
      return valueAdapter;
   }

   public void setValueAdapter(ValueAdapter valueAdapter)
   {
      this.valueAdapter = valueAdapter;
   }

   public boolean isNormalizeSpace()
   {
      if(normalizeSpace != null)
         return normalizeSpace.booleanValue();      
      return schema == null ? true : schema.isNormalizeSpace();
   }

   public void setNormalizeSpace(Boolean normalizeSpace)
   {
      this.normalizeSpace = normalizeSpace;
   }

   public SchemaBinding getSchema()
   {
      return schema;
   }
}
