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

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlMapEntry;
import org.jboss.xb.annotations.JBossXmlMapKeyAttribute;
import org.jboss.xb.annotations.JBossXmlMapKeyElement;
import org.jboss.xb.annotations.JBossXmlMapValueAttribute;
import org.jboss.xb.annotations.JBossXmlMapValueElement;
import org.jboss.xb.annotations.JBossXmlSchema;
import org.jboss.xb.annotations.JBossXmlType;


/**
 * A Root.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement
@JBossXmlSchema(namespace="ns", elementFormDefault= XmlNsForm.QUALIFIED)
@JBossXmlType(modelGroup=JBossXmlConstants.MODEL_GROUP_CHOICE)
public class Root
{
   private Map<String, Integer> stringToInteger;
   private Map<String, EntryTypeKeyAttributeValueEntry> entryTypeMap;
   private AnnotatedMapKeyValueSequence annotatedMapKeyValueSequence;
   private AnnotatedMapWrappedKeyValueSequence annotatedWrappedMapKeyValueSequence;
   private AnnotatedMapKeyValueAttributes<String,Integer> annotatedMapKeyValueAttributes;
   private AnnotatedMapKeyAttributeValueEntryContent annotatedMapKeyAttributeValueEntryContent;
   private AnnotatedMapWithEntryTypeKeyAttributeValueEntryContent annotatedMapWithEntryTypeKeyAttributeValueEntryContent;
   private Map<String, EntryTypeKeyAttributeValueEntry> annotatedMapWithEntryTypeKeyAttributeValueEntry;
   
   @JBossXmlMapKeyElement(name = "key")
   @JBossXmlMapValueElement(name = "value")
   public Map<String, Integer> getKeyValueSequence()
   {
      return this.stringToInteger;
   }
   
   public void setKeyValueSequence(Map<String, Integer> value)
   {
      this.stringToInteger = value;
   }

   public AnnotatedMapKeyValueSequence getAnnotatedMapKeyValueSequence()
   {
      return this.annotatedMapKeyValueSequence;
   }

   public void setAnnotatedMapKeyValueSequence(AnnotatedMapKeyValueSequence value)
   {
      this.annotatedMapKeyValueSequence = value;
   }

   @JBossXmlMapEntry(name = "wrapped-key-value")
   @JBossXmlMapKeyElement(name = "key")
   @JBossXmlMapValueElement(name = "value")
   public Map<String, Integer> getWrappedKeyValueSequence()
   {
      return this.stringToInteger;
   }
   
   public void setWrappedKeyValueSequence(Map<String, Integer> value)
   {
      this.stringToInteger = value;
   }

   public AnnotatedMapWrappedKeyValueSequence getAnnotatedMapWrappedKeyValueSequence()
   {
      return this.annotatedWrappedMapKeyValueSequence;
   }
   
   public void setAnnotatedMapWrappedKeyValueSequence(AnnotatedMapWrappedKeyValueSequence value)
   {
      this.annotatedWrappedMapKeyValueSequence = value;
   }

   @JBossXmlMapEntry(name = "key-value-attr")
   @JBossXmlMapKeyAttribute(name = "key")
   @JBossXmlMapValueAttribute(name = "value")
   public Map<String, Integer> getKeyValueAttributes()
   {
      return this.stringToInteger;
   }
   
   public void setKeyValueAttributes(Map<String, Integer> value)
   {
      this.stringToInteger = value;
   }

   public AnnotatedMapKeyValueAttributes<String, Integer> getAnnotatedMapKeyValueAttributes()
   {
      return this.annotatedMapKeyValueAttributes;
   }
   
   public void setAnnotatedMapKeyValueAttributes(AnnotatedMapKeyValueAttributes<String,Integer> value)
   {
      this.annotatedMapKeyValueAttributes = value;
   }

   @JBossXmlMapEntry(name = "key-attr-value-entry-content")
   @JBossXmlMapKeyAttribute(name = "key")
   public Map<String, Integer> getKeyAttributeValueEntryContent()
   {
      return this.stringToInteger;
   }
   
   public void setKeyAttributeValueEntryContent(Map<String,Integer> value)
   {
      this.stringToInteger = value;
   }

   public AnnotatedMapKeyAttributeValueEntryContent getAnnotatedMapKeyAttributeValueEntryContent()
   {
      return this.annotatedMapKeyAttributeValueEntryContent;
   }
   
   public void setAnnotatedMapKeyAttributeValueEntryContent(AnnotatedMapKeyAttributeValueEntryContent value)
   {
      this.annotatedMapKeyAttributeValueEntryContent = value;
   }

   @JBossXmlMapEntry(name = "entry-type-key-attr-value-entry-content", type=EntryTypeKeyAttributeValueEntryContent.class)
   public Map<String, Integer> getEntryTypeKeyAttributeValueEntryContent()
   {
      return this.stringToInteger;
   }
   
   public void setEntryTypeKeyAttributeValueEntryContent(Map<String, Integer> value)
   {
      this.stringToInteger = value;
   }

   public AnnotatedMapWithEntryTypeKeyAttributeValueEntryContent getAnnotatedMapWithEntryTypeKeyAttributeValueEntryContent()
   {
      return this.annotatedMapWithEntryTypeKeyAttributeValueEntryContent;
   }
   
   public void setAnnotatedMapWithEntryTypeKeyAttributeValueEntryContent(AnnotatedMapWithEntryTypeKeyAttributeValueEntryContent value)
   {
      this.annotatedMapWithEntryTypeKeyAttributeValueEntryContent = value;
   }

   @JBossXmlMapEntry(name = "entry-type-key-attr-value-entry", type=EntryTypeKeyAttributeValueEntry.class)
   public Map<String, EntryTypeKeyAttributeValueEntry> getEntryTypeKeyAttributeValueEntry()
   {
      return this.entryTypeMap;
   }
   
   public void setEntryTypeKeyAttributeValueEntry(Map<String, EntryTypeKeyAttributeValueEntry> value)
   {
      this.entryTypeMap = value;
   }

   @XmlElement(type=AnnotatedMapWithEntryTypeKeyAttributeValueEntry.class)
   public Map<String, EntryTypeKeyAttributeValueEntry> getAnnotatedMapWithEntryTypeKeyAttributeValueEntry()
   {
      return this.annotatedMapWithEntryTypeKeyAttributeValueEntry;
   }
   
   public void setAnnotatedMapWithEntryTypeKeyAttributeValueEntry(Map<String, EntryTypeKeyAttributeValueEntry> value)
   {
      this.annotatedMapWithEntryTypeKeyAttributeValueEntry = value;
   }
   
   @XmlElementWrapper(name="map")
   @JBossXmlMapEntry(name = "entry")
   @JBossXmlMapKeyAttribute(name = "key")
   public Map<String,Integer> getXmlElementWrapperMap()
   {
      return this.stringToInteger;
   }
   
   public void setXmlElementWrapperMap(Map<String, Integer> value)
   {
      this.stringToInteger = value;
   }
}
