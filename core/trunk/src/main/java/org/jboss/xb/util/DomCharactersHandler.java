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
package org.jboss.xb.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * CharactersHandler that unmarshals into org.w3c.dom.Element.
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46112 $</tt>
 */
public class DomCharactersHandler
   extends CharactersHandler
{
   public static final DomCharactersHandler INSTANCE = new DomCharactersHandler();
   
   public Object unmarshalEmpty(QName qName,
                                TypeBinding typeBinding,
                                NamespaceContext nsCtx,
                                ValueMetaData valueMetaData)
   {
      return "";
   }

   public Object unmarshal(QName qName,
                           TypeBinding typeBinding,
                           NamespaceContext nsCtx,
                           ValueMetaData valueMetaData,
                           String value)
   {
      return value;
   }

   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
      if(!(owner instanceof Element))
      {
         throw new JBossXBRuntimeException("The parent must be an instance of "
               + Element.class + ": parent=" + owner);
      }

      Element e = (Element)owner;
      Text textNode = e.getOwnerDocument().createTextNode((String)value);
      e.appendChild(textNode);
   }
}
