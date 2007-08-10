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

import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 37406 $
 */
public class AbstractFeatureMetaData
   implements FeatureMetaData
{
   /** The description */
   protected String description;

   /** The generic metadata */
   protected Map values;
   
   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------

   /**
    * Create a new meta data
    */
   public AbstractFeatureMetaData()
   {
   }
   
   // Public --------------------------------------------------------
   
   /**
    * Set the description.
    * 
    * @param description the description.
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * Set the value.
    * 
    * @param key the key to the value
    * @param value The value to set.
    */
   public void setValue(String key, Object value)
   {
      if (values == null)
         values = new ConcurrentHashMap();
      values.put(key, value);
   }

   // MetaData implementation ---------------------------------------
   
   public String getDescription()
   {
      return description;
   }
  
   public Object getValue(String key)
   {
      if (values == null)
         return null;
      return values.get(key);
   }

   // MetaDataVisitorNote overrides ----------------------------------

   public void visit(MetaDataVisitor visitor)
   {
      visitor.visit(this);
   }
   
   public Iterator getChildren()
   {
      return null;
   }
   
   // JBossObject overrides ------------------------------------------
   
   public void toString(StringBuffer buffer)
   {
      if (description != null)
         buffer.append("description=").append(description);
      if (values != null)
         buffer.append(" values=").append(values);
   }
   
   public void toShortString(StringBuffer buffer)
   {
      buffer.append(description);
   }

}
