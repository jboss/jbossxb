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

import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.xb.annotations.JBossXmlMapEntry;
import org.jboss.xb.annotations.JBossXmlMapKeyAttribute;
import org.jboss.xb.annotations.JBossXmlMapKeyElement;
import org.jboss.xb.annotations.JBossXmlMapValueAttribute;
import org.jboss.xb.annotations.JBossXmlMapValueElement;


/**
 * A Root.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement
public class Root
{
   private Map<String, Integer> stringToInteger;
   
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

   @JBossXmlMapEntry(name = "key-attr-value-entry-content")
   @JBossXmlMapKeyAttribute(name = "key")
   public Map<String, Integer> getKeyAttributeValueEntryContent()
   {
      return this.stringToInteger;
   }
   
   public void setKeyAttributeValueEntryContent(Map<String, Integer> value)
   {
      this.stringToInteger = value;
   }
}
