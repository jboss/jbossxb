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
package org.jboss.xb.binding.metadata;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.Constants;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdAppInfo
   extends XsdElement
{
   static final QName QNAME = new QName(Constants.NS_XML_SCHEMA, "appinfo");

   private SchemaMetaData schemaMetaData;
   private ClassMetaData classMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private PutMethodMetaData putMethodMetaData;
   private AddMethodMetaData addMethodMetaData;
   private ValueMetaData valueMetaData;
   private CharactersMetaData charactersMetaData;
   private boolean mapEntryKey;
   private boolean mapEntryValue;
   private boolean skip;

   public XsdAppInfo()
   {
      super(QNAME);
   }

   public SchemaMetaData getSchemaMetaData()
   {
      return schemaMetaData;
   }

   public void setSchemaMetaData(SchemaMetaData schema)
   {
      this.schemaMetaData = schema;
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

   public PutMethodMetaData getPutMethodMetaData()
   {
      return putMethodMetaData;
   }

   public void setPutMethodMetaData(PutMethodMetaData putMethodMetaData)
   {
      this.putMethodMetaData = putMethodMetaData;
   }

   public void setAddMethodMetaData(AddMethodMetaData addMethod)
   {
      this.addMethodMetaData = addMethod;
   }

   public AddMethodMetaData getAddMethodMetaData()
   {
      return addMethodMetaData;
   }

   public boolean isSkip()
   {
      return skip;
   }

   public void setSkip(boolean skip)
   {
      this.skip = skip;
   }

   public CharactersMetaData getCharactersMetaData()
   {
      return charactersMetaData;
   }

   public void setCharactersMetaData(CharactersMetaData charactersMetaData)
   {
      this.charactersMetaData = charactersMetaData;
   }
}
