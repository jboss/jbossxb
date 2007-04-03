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
 * Metadata for an annotation attribute.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 38009 $
 */
public class AbstractAnnotationAttributeMetaData implements AnnotationAttributeMetaData
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   /** The attribute name */
   protected String name;

   /** The attribute value */
   protected ValueMetaData value;
   
   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------

   /**
    * Create a new annotation attribute meta data
    */
   public AbstractAnnotationAttributeMetaData()
   {
   }

   /**
    * Create a new annotation meta data
    * 
    * @param name the name
    * @param value the value
    */
   public AbstractAnnotationAttributeMetaData(String name, Object value)
   {
      this.name = name;
      this.value = new AbstractValueMetaData(value);
   }

   /**
    * Create a new annotation meta data
    * 
    * @param name the name
    * @param value the string value
    */
   public AbstractAnnotationAttributeMetaData(String name, String value)
   {
      this.name = name;
      this.value = new StringValueMetaData(value);
   }

   /**
    * Create a new annotation meta data
    * 
    * @param name the name
    * @param value the value meta data
    */
   public AbstractAnnotationAttributeMetaData(String name, ValueMetaData value)
   {
      this.name = name;
      this.value = value;
   }
   
   // Public --------------------------------------------------------

   /**
    * Set the name
    * 
    * @param name the name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Set the value
    * 
    * @param value the value
    */
   public void setValue(ValueMetaData value)
   {
      this.value = value;
   }
   
   // AnnotationAttributeMetaData implementation --------------------
   
   public String getName()
   {
      return name;
   }
   
   public ValueMetaData getValue()
   {
      return value;
   }
   
   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
