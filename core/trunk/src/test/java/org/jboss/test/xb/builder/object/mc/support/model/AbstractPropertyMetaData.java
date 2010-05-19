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
package org.jboss.test.xb.builder.object.mc.support.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.jboss.util.JBossStringBuilder;

/**
 * Metadata for a property.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 60019 $
 */
@XmlType(propOrder={"annotations", "value"})
public class AbstractPropertyMetaData extends AbstractFeatureMetaData
   implements PropertyMetaData, ValueMetaDataAware, Serializable
{
   private static final long serialVersionUID = 1L;

   /** The property name */
   protected String name;

   /** The property value */
   protected ValueMetaData value;

   /**
    * Create a new property meta data
    */
   public AbstractPropertyMetaData()
   {
   }

   /**
    * Create a new property meta data
    * 
    * @param name the name
    * @param value the value
    */
   public AbstractPropertyMetaData(String name, Object value)
   {
      this.name = name;
      this.value = new AbstractValueMetaData(value);
   }

   /**
    * Create a new property meta data
    * 
    * @param name the name
    * @param value the string value
    */
   public AbstractPropertyMetaData(String name, String value)
   {
      this.name = name;
      this.value = new StringValueMetaData(value);
   }

   /**
    * Create a new attribute meta data
    * 
    * @param name the name
    * @param value the value meta data
    */
   public AbstractPropertyMetaData(String name, ValueMetaData value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Create a new property meta data
    * 
    * @param name the name
    * @param value the string value
    * @param type the type
    */
   public AbstractPropertyMetaData(String name, String value, String type)
   {
      this.name = name;
      StringValueMetaData svmd = new StringValueMetaData(value);
      svmd.setType(type);
      this.value = svmd;
   }

   public String getName()
   {
      return name;
   }

   /**
    * Set the name
    * 
    * @param name the name
    */
   @XmlAttribute
   public void setName(String name)
   {
      this.name = name;
      flushJBossObjectCache();
   }

   public String getType()
   {
      if (value instanceof AbstractTypeMetaData)
      {
         return ((AbstractTypeMetaData)value).getType();
      }
      return null;
   }

   public ValueMetaData getValue()
   {
      return value;
   }

   /**
    * Set the value
    * 
    * @param value the value
    */
   @XmlElements
   ({
      @XmlElement(name="array", type=AbstractArrayMetaData.class),
      @XmlElement(name="collection", type=AbstractCollectionMetaData.class),
      @XmlElement(name="inject", type=AbstractDependencyValueMetaData.class),
      @XmlElement(name="list", type=AbstractListMetaData.class),
      @XmlElement(name="map", type=AbstractMapMetaData.class),
      @XmlElement(name="set", type=AbstractSetMetaData.class),
      @XmlElement(name="this", type=ThisValueMetaData.class),
      @XmlElement(name="value", type=StringValueMetaData.class)
   })
   public void setValue(ValueMetaData value)
   {
      this.value = value;
      flushJBossObjectCache();
   }

   @XmlAnyElement
   public void setValueObject(Object value)
   {
      if (value == null)
         setValue(null);
      else if (value instanceof ValueMetaData)
         setValue((ValueMetaData) value);
      else
         setValue(new AbstractValueMetaData(value));
   }

   @XmlValue
   public void setValueString(String value)
   {
      if (value == null)
         setValue(null);
      else
      {
         ValueMetaData valueMetaData = getValue();
         if (valueMetaData instanceof StringValueMetaData)
         {
            ((StringValueMetaData) valueMetaData).setValue(value);
            return;
         }
         StringValueMetaData stringValue = new StringValueMetaData(value);
         stringValue.setType(getType());
         setValue(stringValue);
      }
   }

   @XmlAttribute(name="class")
   public void setPropertyType(String type)
   {
      ValueMetaData valueMetaData = getValue();
      if (valueMetaData != null && valueMetaData instanceof StringValueMetaData == false)
         throw new IllegalArgumentException("Property is not a string");
      if (valueMetaData == null)
      {
         valueMetaData = new StringValueMetaData();
         setValue(valueMetaData);
      }
      ((StringValueMetaData) valueMetaData).setType(type);
   }
   
   public void toString(JBossStringBuilder buffer)
   {
      buffer.append("name=").append(name);
      if (value != null)
         buffer.append(" value=").append(value);
      super.toString(buffer);
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      buffer.append(name);
   }
}
