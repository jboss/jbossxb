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
package org.jboss.xb.binding.introspection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import org.jboss.xb.binding.JBossXBRuntimeException;
import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class ClassInfo
{
   private static final Object FIELD_INFO_NA = new Object();
   private final Class type;
   private Map fields = new ConcurrentHashMap();
   private boolean introspected;

   public ClassInfo(Class cls)
   {
      this.type = cls;
   }

   public Class getType()
   {
      return type;
   }

   /**
    * @param name     the name of the field
    * @param required if true never returns null (if the FieldInfo is not available, an exception will be thrown).
    *                 If false and FieldInfo is not available then null will be returned.
    * @return an instance of FieldInfo or null
    */
   public FieldInfo getFieldInfo(String name, boolean required)
   {
      Object o = fields.get(name);
      if(o == null)
      {
         FieldInfo fieldInfo = FieldInfo.getFieldInfo(this, name);
         if(fieldInfo == null)
         {
            fields.put(name, FIELD_INFO_NA);
         }
         else
         {
            return fieldInfo;
         }
      }
      else if(o != FIELD_INFO_NA)
      {
         return (FieldInfo)o;
      }

      if(required)
      {
         throw new JBossXBRuntimeException(
            "Failed to find read method or field for property '" + name + "' in " + type
         );
      }

      return null;
   }

   void addFieldInfo(FieldInfo fieldInfo)
   {
      fields.put(fieldInfo.getName(), fieldInfo);
   }

   FieldInfo introspect(String name)
   {
      if(introspected)
      {
         return null;
      }

      try
      {
         BeanInfo info = java.beans.Introspector.getBeanInfo(type);
         PropertyDescriptor[] props = info.getPropertyDescriptors();
         if(props != null)
         {
            for(int i = 0; i < props.length; ++i)
            {
               PropertyDescriptor prop = props[i];
               Method readMethod = prop.getReadMethod();
               // todo: there are issues with null readMethod, e.g. scale in BigDecimal...
               if(readMethod != null)
               {
                  Method writeMethod = prop.getWriteMethod();
                  FieldInfo fieldInfo = new FieldInfo(type, prop.getName(), readMethod, writeMethod);
                  addFieldInfo(fieldInfo);
               }
            }
         }
      }
      catch(IntrospectionException e)
      {
      }

      introspected = true;
      return getFieldInfo(name, false);
   }
}
