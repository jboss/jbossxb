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
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;

/**
 * Metadata for construction.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 60019 $
 */
@XmlType(propOrder={"annotations", "factory", "parameters", "value"})
public class AbstractConstructorMetaData extends AbstractFeatureMetaData
   implements ConstructorMetaData, MutableParameterizedMetaData, ValueMetaDataAware, Serializable
{
   private static final long serialVersionUID = 1L;

   /**
    * The paramaters List<ParameterMetaData>
    */
   protected List<ParameterMetaData> parameters;

   /**
    * The value
    */
   protected ValueMetaData value;

   /**
    * The factory
    */
   protected ValueMetaData factory;

   /**
    * The factory class name
    */
   protected String factoryClassName;

   /**
    * The factory method
    */
   protected String factoryMethod;

   /**
    * Create a new constructor meta data
    */
   public AbstractConstructorMetaData()
   {
   }

   /**
    * Set the parameters
    *
    * @param parameters List<ParameterMetaData>
    */
   @XmlElement(name="parameter", type=AbstractParameterMetaData.class)
   public void setParameters(List<ParameterMetaData> parameters)
   {
      this.parameters = parameters;
      flushJBossObjectCache();
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
      @XmlElement(name="list", type=AbstractListMetaData.class),
      @XmlElement(name="map", type=AbstractMapMetaData.class),
      @XmlElement(name="set", type=AbstractSetMetaData.class),
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

   /**
    * Set the factory
    *
    * @param factory the factory
    */
   @XmlElement(name="factory", type=AbstractDependencyValueMetaData.class)
   public void setFactory(ValueMetaData factory)
   {
      // HACK to have wildcard factories
      if (factory != null && factory instanceof AbstractDependencyValueMetaData)
      {
         Object underlying = factory.getUnderlyingValue();
         if (underlying != null && underlying instanceof ValueMetaData)
            factory = (ValueMetaData) underlying;
      }
         
      this.factory = factory;
      flushJBossObjectCache();
   }

   /**
    * Set the factory class name
    *
    * @param name the factory class name
    */
   @XmlAttribute(name="factoryClass")
   public void setFactoryClass(String name)
   {
      this.factoryClassName = name;
      flushJBossObjectCache();
   }

   /**
    * Set the factory method
    *
    * @param name the factory method
    */
   @XmlAttribute(name="factoryMethod")
   public void setFactoryMethod(String name)
   {
      this.factoryMethod = name;
      flushJBossObjectCache();
   }

   public List<ParameterMetaData> getParameters()
   {
      return parameters;
   }

   public ValueMetaData getValue()
   {
      return value;
   }

   public ValueMetaData getFactory()
   {
      return factory;
   }

   public String getFactoryClass()
   {
      return factoryClassName;
   }

   public String getFactoryMethod()
   {
      return factoryMethod;
   }

   public void toString(JBossStringBuilder buffer)
   {
      buffer.append("parameters=");
      JBossObject.list(buffer, parameters);
      if (value != null)
         buffer.append(" value=").append(value);
      if (factory != null)
         buffer.append(" factory=").append(factory);
      if (factoryClassName != null)
         buffer.append(" factoryClass=").append(factoryClassName);
      if (factoryMethod != null)
         buffer.append(" factoryMethod=").append(factoryMethod);
      super.toString(buffer);
   }
}
