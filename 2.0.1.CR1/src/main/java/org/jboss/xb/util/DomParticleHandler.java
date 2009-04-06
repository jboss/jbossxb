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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


/**
 * ParticleHandler that unmarshals into org.w3c.dom.Element.
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46112 $</tt>
 */
public class DomParticleHandler extends RtElementHandler implements ParticleHandler
{
   public static final DomParticleHandler INSTANCE = new DomParticleHandler();
   
   private Document doc;

   public Object startParticle(Object parent,
         QName elementName,
         ParticleBinding particle,
         Attributes attrs,
         NamespaceContext nsCtx)
   {
      Document doc = getDocument();
      Element element = doc.createElementNS(elementName.getNamespaceURI(), elementName.getLocalPart());

      if (attrs != null)
      {
         for (int i = 0; i < attrs.getLength(); ++i)
         {
            element.setAttribute(attrs.getLocalName(i), attrs.getValue(i));
         }
      }

      return element;
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      return o;
   }

   public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle)
   {
      if (parent instanceof Element)
      {
         ((Element) parent).appendChild((Element) o);
      }
      else
      {
         super.setParent(parent, o, elementName, particle, parentParticle);
      }
   }

   private Document getDocument()
   {
      if (doc == null)
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder domBuilder = null;
         try
         {
            domBuilder = factory.newDocumentBuilder();
         }
         catch (ParserConfigurationException e)
         {
            throw new JBossXBRuntimeException("Failed to create document builder instance", e);
         }
         doc = domBuilder.newDocument();
      }
      return doc;
   }
}
