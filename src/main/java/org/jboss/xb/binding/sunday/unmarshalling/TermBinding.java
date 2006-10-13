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

import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class TermBinding
{
   protected SchemaBinding schema;

   protected ClassMetaData classMetaData;
   protected PropertyMetaData propertyMetaData;
   protected MapEntryMetaData mapEntryMetaData;
   protected PutMethodMetaData putMethodMetaData;
   protected AddMethodMetaData addMethodMetaData;
   protected ValueMetaData valueMetaData;
   protected boolean mapEntryKey;
   protected boolean mapEntryValue;
   protected Boolean skip;
   protected ValueAdapter valueAdapter;
   protected TermBeforeMarshallingHandler beforeMarshallingHandler;
   protected TermAfterUnmarshallingHandler afterUnmarshallingHandler;

   protected TermBinding(SchemaBinding schema)
   {
      this.schema = schema;
   }

   protected TermBinding()
   {
   }
   
   public ClassMetaData getClassMetaData()
   {
      return classMetaData;
   }

   public void setClassMetaData(ClassMetaData classMetaData)
   {
      this.classMetaData = classMetaData;
   }

   public PropertyMetaData getPropertyMetaData()
   {
      return propertyMetaData;
   }

   public void setPropertyMetaData(PropertyMetaData propertyMetaData)
   {
      this.propertyMetaData = propertyMetaData;
   }

   public MapEntryMetaData getMapEntryMetaData()
   {
      return mapEntryMetaData;
   }

   public void setMapEntryMetaData(MapEntryMetaData mapEntryMetaData)
   {
      this.mapEntryMetaData = mapEntryMetaData;
   }

   public PutMethodMetaData getPutMethodMetaData()
   {
      return putMethodMetaData;
   }

   public void setPutMethodMetaData(PutMethodMetaData putMethodMetaData)
   {
      this.putMethodMetaData = putMethodMetaData;
   }

   public AddMethodMetaData getAddMethodMetaData()
   {
      return addMethodMetaData;
   }

   public void setAddMethodMetaData(AddMethodMetaData addMethodMetaData)
   {
      this.addMethodMetaData = addMethodMetaData;
   }

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData;
   }

   public void setValueMetaData(ValueMetaData valueMetaData)
   {
      this.valueMetaData = valueMetaData;
   }

   public boolean isMapEntryKey()
   {
      return mapEntryKey;
   }

   public void setMapEntryKey(boolean mapEntryKey)
   {
      this.mapEntryKey = mapEntryKey;
   }

   public boolean isMapEntryValue()
   {
      return mapEntryValue;
   }

   public void setMapEntryValue(boolean mapEntryValue)
   {
      this.mapEntryValue = mapEntryValue;
   }

   public abstract boolean isSkip();

   public void setSkip(Boolean skip)
   {
      this.skip = skip;
   }

   public ValueAdapter getValueAdapter()
   {
      return valueAdapter;
   }

   public void setValueAdapter(ValueAdapter valueAdapter)
   {
      this.valueAdapter = valueAdapter;
   }

   public SchemaBinding getSchema()
   {
      return schema;
   }

   public abstract boolean isModelGroup();

   public abstract boolean isWildcard();

   public void setBeforeMarshallingHandler(TermBeforeMarshallingHandler marshallingHandler)
   {
      this.beforeMarshallingHandler = marshallingHandler;
   }

   public TermBeforeMarshallingHandler getBeforeMarshallingHandler()
   {
      return beforeMarshallingHandler;
   }

   public void setAfterUnmarshallingHandler(TermAfterUnmarshallingHandler unmarshallingHandler)
   {
      this.afterUnmarshallingHandler = unmarshallingHandler;
   }

   public TermAfterUnmarshallingHandler getAfterUnmarshallingHandler()
   {
      return afterUnmarshallingHandler;
   }
}
