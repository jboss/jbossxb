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
package org.jboss.test.xml.pojoserver.metadata;

/**
 * Metadata for a parameter.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class AbstractParameterMetaData extends AbstractFeatureMetaData implements ParameterMetaData
{
   /** The parameter type */
   protected String type;

   /** The parameter value */
   protected ValueMetaData value;

   /**
    * Create a new parameter meta data
    */
   public AbstractParameterMetaData()
   {
   }

   /**
    * Create a new parameter meta data
    * 
    * @param value the value
    */
   public AbstractParameterMetaData(Object value)
   {
      this.type = value.getClass().getName();
      this.value = new AbstractValueMetaData(value);
   }

   /**
    * Create a new parameter meta data
    * 
    * @param type the type
    * @param value the value
    */
   public AbstractParameterMetaData(String type, Object value)
   {
      this.type = type;
      this.value = new AbstractValueMetaData(value);
   }

   /**
    * Create a new parameter meta data
    * 
    * @param type the type
    * @param value the string value
    */
   public AbstractParameterMetaData(String type, String value)
   {
      this.type = type;
      this.value = new StringValueMetaData(value);
   }

   /**
    * Create a new parameter meta data
    * 
    * @param type the type
    * @param value the value meta data
    */
   public AbstractParameterMetaData(String type, ValueMetaData value)
   {
      this.type = type;
      this.value = value;
   }
   
   public String getType()
   {
      return type;
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   public ValueMetaData getValue()
   {
      return value;
   }

   public void setValue(ValueMetaData value)
   {
      this.value = value;
   }
}
