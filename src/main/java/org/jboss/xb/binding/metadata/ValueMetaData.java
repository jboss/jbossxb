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


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ValueMetaData
{
   private String id;
   private String ref;
   private String unmarshalMethod;
   private String marshalMethod;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getRef()
   {
      return ref;
   }

   public void setRef(String ref)
   {
      this.ref = ref;
   }

   public String getUnmarshalMethod()
   {
      return unmarshalMethod;
   }

   public void setUnmarshalMethod(String unmarshalMethod)
   {
      this.unmarshalMethod = unmarshalMethod;
   }

   public String getMarshalMethod()
   {
      return marshalMethod;
   }

   public void setMarshalMethod(String marshalMethod)
   {
      this.marshalMethod = marshalMethod;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof ValueMetaData))
      {
         return false;
      }

      final ValueMetaData valueMetaData = (ValueMetaData)o;

      if(id != null ? !id.equals(valueMetaData.id) : valueMetaData.id != null)
      {
         return false;
      }
      if(marshalMethod != null ? !marshalMethod.equals(valueMetaData.marshalMethod) : valueMetaData.marshalMethod != null)
      {
         return false;
      }
      if(ref != null ? !ref.equals(valueMetaData.ref) : valueMetaData.ref != null)
      {
         return false;
      }
      if(unmarshalMethod != null ?
         !unmarshalMethod.equals(valueMetaData.unmarshalMethod) :
         valueMetaData.unmarshalMethod != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (id != null ? id.hashCode() : 0);
      result = 29 * result + (ref != null ? ref.hashCode() : 0);
      result = 29 * result + (unmarshalMethod != null ? unmarshalMethod.hashCode() : 0);
      result = 29 * result + (marshalMethod != null ? marshalMethod.hashCode() : 0);
      return result;
   }
}
