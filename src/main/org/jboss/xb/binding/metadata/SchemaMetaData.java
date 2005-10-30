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

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Metadata for the SchemaBinding instance.
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SchemaMetaData
{
   private PackageMetaData packageMetaData;
   private Map values = Collections.EMPTY_MAP;
   private boolean ignoreUnresolvedFieldOrClass = true;
   private boolean replacePropertyRefs = true;

   public boolean isIgnoreUnresolvedFieldOrClass()
   {
      return ignoreUnresolvedFieldOrClass;
   }
   public void setIgnoreUnresolvedFieldOrClass(boolean flag)
   {
      this.ignoreUnresolvedFieldOrClass = flag;
   }

   public boolean isReplacePropertyRefs()
   {
      return replacePropertyRefs;
   }
   public void setReplacePropertyRefs(boolean flag)
   {
      this.replacePropertyRefs = flag;
   }

   public PackageMetaData getPackage()
   {
      return packageMetaData;
   }

   public void setPackage(PackageMetaData pkg)
   {
      this.packageMetaData = pkg;
   }

   public ValueMetaData getValue(String id)
   {
      return (ValueMetaData)values.get(id);
   }

   public void addValue(ValueMetaData value)
   {
      if(value.getId() == null)
      {
         throw new IllegalArgumentException("ValueMetaData must have a non-null id.");
      }

      switch(values.size())
      {
         case 0:
            values = Collections.singletonMap(value.getId(), value);
            break;
         case 1:
            values = new HashMap(values);
         default:
            values.put(value.getId(), value);
      }
   }
}
