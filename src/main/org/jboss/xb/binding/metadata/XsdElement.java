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

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdElement
{
   public static final QName QNAME_NAME = new QName("name");

   private final QName qName;
   private Map attributes = Collections.EMPTY_MAP;
   private Map children = Collections.EMPTY_MAP;
   private String data;

   public XsdElement(QName qName)
   {
      this.qName = qName;
   }

   public String getAttribute(QName qName)
   {
      return (String)attributes.get(qName);
   }

   public void addAttribute(QName qName, String value)
   {
      switch(attributes.size())
      {
         case 0:
            attributes = Collections.singletonMap(qName, value);
            break;
         case 1:
            attributes = new HashMap(attributes);
         default:
            attributes.put(qName, value);
      }
   }

   public XsdElement getChild(QName qName)
   {
      return (XsdElement)children.get(qName);
   }

   public void addChild(XsdElement child)
   {
      switch(children.size())
      {
         case 0:
            children = Collections.singletonMap(child.qName, child);
            break;
         case 1:
            children = new HashMap(children);
         default:
            children.put(child.qName, child);
      }
   }

   public String getData()
   {
      return data;
   }

   public void setData(String data)
   {
      this.data = data;
   }

   public QName getQName()
   {
      return qName;
   }

   public String getNameAttribute()
   {
      return getAttribute(QNAME_NAME);
   }

   public void setNameAttribute(String name)
   {
      addAttribute(QNAME_NAME, name);
   }
   
   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof XsdElement))
      {
         return false;
      }

      final XsdElement xsdElement = (XsdElement)o;

      if(!qName.equals(xsdElement.qName))
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      return qName.hashCode();
   }
}
