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

import java.util.Iterator;
import java.util.Collections;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 37406 $
 */
public class AbstractPropertyMetaData
   extends AbstractFeatureMetaData implements PropertyMetaData
  {
     // Constants -----------------------------------------------------

     // Attributes ----------------------------------------------------

     /** The property name */
     protected String name;

     /** The property value */
     protected ValueMetaData value;
   
     // Static --------------------------------------------------------
   
     // Constructors --------------------------------------------------

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
   
     // Public --------------------------------------------------------
   
     // PropertyMetaData implementation -------------------------------
   
     public String getName()
     {
        return name;
     }
   
     public void setName(String name)
     {
        this.name = name;
     }
   
     public ValueMetaData getValue()
     {
        return value;
     }

     public void setValue(ValueMetaData value)
     {
        this.value = value;
     }
   
     // MetaDataVisitorNode overrides ----------------------------------
   
     public Iterator getChildren()
     {
        if (value != null)
           return Collections.singletonList(value).iterator();
        return null;
     }

     // JBossObject overrides ------------------------------------------
   
     public void toString(StringBuffer buffer)
     {
        buffer.append("name=").append(name);
        if (value != null)
           buffer.append(" value=").append(value);
        super.toString(buffer);
     }
   
     public void toShortString(StringBuffer buffer)
     {
        buffer.append(name);
     }
   
}
