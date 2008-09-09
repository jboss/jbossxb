/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.ejb.metadata.jboss.cmp;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.jboss.xb.annotations.JBossXmlChild;


/**
 * A EntityCommandMetaData.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="entity-commandType")
@JBossXmlChild(name="attribute", type=JBossCMPEntityCommandMetaData.Attribute.class, unbounded=true)
public class JBossCMPEntityCommandMetaData extends AbstractCollection<JBossCMPEntityCommandMetaData.Attribute>
{
   private String name;
   private String className;
   private Map<String, Attribute> attributes = Collections.emptyMap();
   
   
   /**
    * Get the className.
    * 
    * @return the className.
    */
   public String getClassName()
   {
      return className;
   }

   /**
    * Set the className.
    * 
    * @param className The className to set.
    */
   @XmlAttribute(name="class")
   public void setClassName(String className)
   {
      this.className = className;
   }

   /**
    * Get the name.
    * 
    * @return the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    * 
    * @param name The name to set.
    */
   @XmlAttribute
   public void setName(String name)
   {
      this.name = name;
   }

   @XmlTransient
   public String getAttribute(String name)
   {
      Attribute attr = attributes.get(name);
      return attr == null ? null : attr.getValue();
   }
   
   public boolean hasAttributes()
   {
      return !attributes.isEmpty();
   }
   
   @Override
   public boolean add(JBossCMPEntityCommandMetaData.Attribute attribute)
   {
      if(attributes.isEmpty())
      {
         attributes = Collections.singletonMap(attribute.getName(), attribute);
         return true;
      }
      
      if(attributes.size() == 1)
      {
         attributes = new HashMap<String, Attribute>(attributes);
      }
      return attributes.put(attribute.name, attribute) == null;
   }
   
   @Override
   public Iterator<JBossCMPEntityCommandMetaData.Attribute> iterator()
   {
      return attributes.values().iterator();
   }
   
   @Override
   public int size()
   {
      return attributes.size();
   }
   
   public static class Attribute
   {
      private String name;
      private String value;
      
      /**
       * Get the name.
       * 
       * @return the name.
       */      
      public String getName()
      {
         return name;
      }
      
      /**
       * Set the name.
       * 
       * @param name The name to set.
       */
      @XmlAttribute
      public void setName(String name)
      {
         this.name = name;
      }
      
      /**
       * Get the value.
       * 
       * @return the value.
       */      
      public String getValue()
      {
         return value;
      }
      
      /**
       * Set the value.
       * 
       * @param value The value to set.
       */
      @XmlValue
      public void setValue(String value)
      {
         this.value = value;
      }
   }
}
