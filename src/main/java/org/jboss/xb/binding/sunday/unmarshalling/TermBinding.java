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

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingCallback;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class TermBinding
{
   protected SchemaBinding schema;
   protected QName qName;
   
   protected ClassMetaData classMetaData;
   protected PropertyMetaData propertyMetaData;
   protected MapEntryMetaData mapEntryMetaData;
   protected PutMethodMetaData putMethodMetaData;
   protected AddMethodMetaData addMethodMetaData;
   protected ValueMetaData valueMetaData;
   protected boolean mapEntryKey;
   protected boolean mapEntryValue;
   protected int skip;
   protected ValueAdapter valueAdapter;
   protected TermBeforeMarshallingCallback beforeMarshallingCallback;
   protected TermBeforeSetParentCallback beforeSetParentCallback;
   protected RepeatableParticleHandler repeatableHandler = DefaultHandlers.REPEATABLE_HANDLER;

   protected ParticleHandler handler;
   
   protected TermBinding(SchemaBinding schema)
   {
      this.schema = schema;
   }

   protected TermBinding()
   {
   }
   
   public QName getQName()
   {
      return this.qName;
   }

   public void setQName(QName name)
   {
      qName = name;
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

   public void setSkip(boolean skip)
   {
      this.skip = skip ? Constants.TRUE : Constants.FALSE;
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

   public abstract boolean isElement();

   public void setBeforeMarshallingCallback(TermBeforeMarshallingCallback marshallingHandler)
   {
      this.beforeMarshallingCallback = marshallingHandler;
   }

   public TermBeforeMarshallingCallback getBeforeMarshallingCallback()
   {
      return beforeMarshallingCallback;
   }

   public void setBeforeSetParentCallback(TermBeforeSetParentCallback beforeSetParent)
   {
      this.beforeSetParentCallback = beforeSetParent;
   }

   public TermBeforeSetParentCallback getBeforeSetParentCallback()
   {
      return beforeSetParentCallback;
   }
   
   public ParticleHandler getHandler()
   {
      return handler;
   }
   
   public void setHandler(ParticleHandler handler)
   {
      this.handler = handler;
   }
   
   public RepeatableParticleHandler getRepeatableHandler()
   {
      return repeatableHandler;
   }
   
   public void setRepeatableHandler(RepeatableParticleHandler repeatableHandler)
   {
      this.repeatableHandler = repeatableHandler;
   }
   
   public abstract AbstractPosition newPosition(QName qName, Attributes attrs, ParticleBinding particle);
}
