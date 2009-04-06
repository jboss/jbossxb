/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.builder.runtime;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.logging.Logger;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;

public class DOMHandler extends CharactersHandler implements ParticleHandler
{
   /** The logger */
   private static final Logger log = Logger.getLogger(DOMHandler.class);

   /** The instance */
   public static final DOMHandler INSTANCE = new DOMHandler(); 
   
   /** Whether trace is enabled */
   private boolean trace = log.isTraceEnabled();

   public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
   {
      if (trace)
         log.trace("startParticle " + elementName + " parent=" + BuilderUtil.toDebugString(parent));
      Element element = null;
      if (parent == null || parent instanceof Element == false)
         element = createTopElement(elementName.getNamespaceURI(), elementName.getLocalPart());
      else
         element = appendChildElement((Element) parent, elementName.getNamespaceURI(), elementName.getLocalPart());
      addAttributes(element, attrs);
      return element;
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      if (trace)
         log.trace("endParticle " + elementName + " result=" + BuilderUtil.toDebugString(o));
      return o;
   }

   public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      if (trace)
         log.trace("setParent " + elementName + " parent=" + BuilderUtil.toDebugString(parent) + " o=" + BuilderUtil.toDebugString(o));
   }

   @Override
   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
      setText(owner, value, qName);
   }

   @Override
   public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
   {
      return value;
   }

   @Override
   public Object unmarshalEmpty(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData)
   {
      return null;
   }
   
   public Element createTopElement(String namespace, String name)
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         DOMImplementation impl = builder.getDOMImplementation();
         Document document = impl.createDocument(null, null, null);
         
         Element element = document.createElementNS(namespace, name);
         document.appendChild(element);
         if (trace)
            log.trace("createTopElement " + namespace + ":" + name + " result=" + BuilderUtil.toDebugString(element));
         return element;
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error creating dom", e);
      }
   }
   
   public Element appendChildElement(Element parentElement, String namespace, String name)
   {
      Document document = parentElement.getOwnerDocument();
      Element element = document.createElementNS(namespace, name);
      parentElement.appendChild(element);
      if (trace)
         log.trace("appendChild parent=" + BuilderUtil.toDebugString(parentElement) + " child=" + BuilderUtil.toDebugString(element));
      return element;
   }
   
   public void addAttributes(Element element, Attributes attrs)
   {
      for (int i = 0; i < attrs.getLength(); ++i)
      {
         String local = attrs.getLocalName(i);
         String nsURI = attrs.getURI(i);
         String value = attrs.getValue(i);
         if (trace)
            log.trace("setAttribute " + nsURI + " " + local + " element=" + BuilderUtil.toDebugString(element) + " value=" + value);
         element.setAttributeNS(nsURI, local, value);
      }
   }
   
   public void setText(Object owner, Object value, QName qName)
   {
      if (value == null)
         return;
      if (owner == null || owner instanceof Element == false)
         throw new IllegalStateException("Unexpected owner: " + owner + " for " + qName);
      if (value instanceof String == false)
         throw new IllegalStateException("Unexpected value " + value + " for " + qName);
      Element element = (Element) owner;
      Text text = element.getOwnerDocument().createTextNode((String) value);
      if (trace)
         log.trace("setText " + qName + " parent=" + BuilderUtil.toDebugString(owner) + " child=" + BuilderUtil.toDebugString(value));
      element.appendChild(text);
   }
}
