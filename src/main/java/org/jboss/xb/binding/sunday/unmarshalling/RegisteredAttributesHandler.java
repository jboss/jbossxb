/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

/**
 * A RegisteredAttributesHandler.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class RegisteredAttributesHandler extends AttributesHandler
{
   private Map<QName, AttributeBinding> registered;
   private AnyAttributeBinding any;
   private Map<QName, AttributeBinding> defaultAttrs;
   
   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
   {
      if(registered == null)
         return;
      
      Map<QName, AttributeBinding> notSetDefaultAttrs = null;
      // note: this is never used in the builder impl
      // but there is a test for this in SchemaDefaultAttributeValueUnitTestCase
      if(defaultAttrs != null)
         notSetDefaultAttrs = new HashMap<QName, AttributeBinding>(defaultAttrs);
      
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         QName qName = new QName(attrs.getURI(i), attrs.getLocalName(i));
         AttributeBinding binding = registered.get(qName);
         if(binding != null)
         {
            AttributeHandler handler = binding.getHandler();
            Object value = handler.unmarshal(elementName, qName, binding, nsCtx, attrs.getValue(i));
            handler.attribute(elementName, qName, binding, o, value);
            
            if(notSetDefaultAttrs != null && binding.getDefaultConstraint() != null)
               notSetDefaultAttrs.remove(qName);
         }
         else if (any != null)
         {
            AnyAttributeHandler handler = any.getHandler();
            Object value = handler.unmarshal(elementName, qName, any, nsCtx, attrs.getValue(i));
            handler.attribute(elementName, qName, any, o, value);
         }
      }
      
      if(notSetDefaultAttrs != null && !notSetDefaultAttrs.isEmpty())
      {
         for(AttributeBinding binding : notSetDefaultAttrs.values())
         {
            AttributeHandler handler = binding.getHandler();
            Object value = handler.unmarshal(elementName, binding.getQName(), binding, nsCtx, binding.getDefaultConstraint());
            handler.attribute(elementName, binding.getQName(), binding, o, value);
         }
      }
   }
   
   public void addAttribute(AttributeBinding attr)
   {
      if(registered == null)
         registered = Collections.singletonMap(attr.getQName(), attr);
      else
      {
         if(registered.size() == 1)
            registered = new HashMap<QName, AttributeBinding>(registered);
         registered.put(attr.getQName(), attr);
      }
      
      if(attr.getDefaultConstraint() != null)
      {
         if(defaultAttrs == null)
            defaultAttrs = Collections.singletonMap(attr.getQName(), attr);
         else
         {
            if(defaultAttrs.size() == 1)
               defaultAttrs = new HashMap<QName, AttributeBinding>(defaultAttrs);
            defaultAttrs.put(attr.getQName(), attr);
         }
      }
   }
   
   public void setAnyAttribute(AnyAttributeBinding any)
   {
      this.any = any;
   }
}
