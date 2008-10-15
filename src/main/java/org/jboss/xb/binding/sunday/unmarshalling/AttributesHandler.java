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
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributesHandler
{
   private static final Logger log = Logger.getLogger(AttributesHandler.class);

   public static final AttributesHandler INSTANCE = new AttributesHandler();

   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         QName qName = new QName(attrs.getURI(i), attrs.getLocalName(i));
         AttributeBinding binding = type.getAttribute(qName);
         if(binding != null)
         {
            AttributeHandler handler = binding.getHandler();
            Object value = handler.unmarshal(elementName, qName, binding, nsCtx, attrs.getValue(i));
            handler.attribute(elementName, qName, binding, o, value);
         }
         else
         {
            AnyAttributeBinding anyAttribute = type.getAnyAttribute();
            if(anyAttribute != null)
            {
               AnyAttributeHandler handler = anyAttribute.getHandler();
               Object value = handler.unmarshal(elementName, qName, anyAttribute, nsCtx, attrs.getValue(i));
               handler.attribute(elementName, qName, anyAttribute, o, value);
            }
            else if(!Constants.NS_XML_SCHEMA_INSTANCE.equals(qName.getNamespaceURI()))
            {
               SchemaBinding schemaBinding = type.getSchemaBinding();
               if(schemaBinding != null && schemaBinding.isStrictSchema())
               {
                  throw new JBossXBRuntimeException(
                     "Attribute is not bound: element owner " + elementName + ", attribute " + qName
                  );
               }
               else if(log.isTraceEnabled())
               {
                  log.trace("Attribute is not bound: element owner " + elementName + ", attribute " + qName);
               }
            }
         }
      }
   }
}
