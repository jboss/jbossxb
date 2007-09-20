/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.builder.runtime;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.namespace.QName;

import org.jboss.reflect.spi.EnumConstantInfo;
import org.jboss.reflect.spi.EnumInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;

/**
 * EnumValueAdapter.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class EnumValueAdapter implements ValueAdapter
{
   /** The qName */
   private QName qName;
   
   /** The valid values */
   private Map<Object, Object> valid;
   
   /**
    * Create a new EnumValueAdapter.
    * 
    * @param qName the qName
    * @param enumInfo the enumeration
    * @param enumType the enumType
    * @throws IllegalArgumentException for a null enumInfo or enumType
    */
   public EnumValueAdapter(QName qName, EnumInfo enumInfo, TypeInfo enumType)
   {
      if (enumInfo == null)
         throw new IllegalArgumentException("Null enumInfo");
      if (enumType == null)
         throw new IllegalArgumentException("Null enumType");
      
      this.qName = qName;
      
      // Setup the mapping
      EnumConstantInfo[] constants = enumInfo.getEnumConstants();
      valid = new HashMap<Object, Object>(constants.length);
      for (EnumConstantInfo constant : constants)
      {
         String enumValue = constant.getName();
         XmlEnumValue xmlEnumValue = constant.getUnderlyingAnnotation(XmlEnumValue.class);
         if (xmlEnumValue != null)
            enumValue = xmlEnumValue.value();
         
         Object key = enumValue;
         try
         {
            key = enumType.convertValue(enumValue, false);
         }
         catch (Throwable t)
         {
            throw new RuntimeException("Error for enum " + enumInfo.getName() + " unable to convert " + enumValue + " to " + enumType.getName());
         }
         Object value = constant.getValue();
         valid.put(key, value);
      }
   }
   
   /**
    * Get the mapping
    * 
    * @return the mapping
    */
   public Map<Object, Object> getMapping()
   {
      return valid;
   }
   
   @SuppressWarnings("unchecked")
   public Object cast(Object o, Class c)
   {
      if (o == null)
         return null;
      
      Object result = valid.get(o);
      if (result == null)
      {
         if (qName == null)
            throw new RuntimeException("Invalid value " + BuilderUtil.toDebugString(o) + " valid are " + valid.keySet());
         throw new RuntimeException("Invalid value " + BuilderUtil.toDebugString(o) + " for " + qName + " valid are " + valid.keySet());
      }
      return result;
   }
}
