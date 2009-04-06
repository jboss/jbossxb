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
package org.jboss.test.xb.builder.object.jbossxmlmapentry.support;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jboss.xb.annotations.JBossXmlMapKey;


/**
 * A EntryTypeKeyAttributeValueEntry.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class EntryTypeKeyAttributeValueEntry
{
   private String key;
   private String valueAttribute;
   private Integer valueElement;

   
   @JBossXmlMapKey
   @XmlAttribute
   public String getKey()
   {
      return key;
   }
   
   public void setKey(String key)
   {
      this.key = key;
   }

   @XmlAttribute(name="attr")
   public String getValueAttribute()
   {
      return valueAttribute;
   }

   public void setValueAttribute(String valueAttribute)
   {
      this.valueAttribute = valueAttribute;
   }

   @XmlElement(name="value")
   public Integer getValueElement()
   {
      return valueElement;
   }

   public void setValueElement(Integer valueElement)
   {
      this.valueElement = valueElement;
   }
}
