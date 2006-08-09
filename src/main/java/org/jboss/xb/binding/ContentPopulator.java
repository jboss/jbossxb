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
package org.jboss.xb.binding;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * org.xml.sax.ContentHandler implementation that poplulates an instance of org.jboss.xb.binding.Content.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ContentPopulator
   implements ContentHandler
{
   private Content content = new Content();

   // Public

   public Content getContent()
   {
      return content;
   }

   // ContentHandler implementation

   public void setDocumentLocator(Locator locator)
   {
   }

   public void startDocument()
      throws SAXException
   {
      content.startDocument();
   }

   public void endDocument()
      throws SAXException
   {
      content.endDocument();
   }

   public void startPrefixMapping(String prefix, String uri)
      throws SAXException
   {
      content.startPrefixMapping(prefix, uri);
   }

   public void endPrefixMapping(String prefix)
      throws SAXException
   {
      content.endPrefixMapping(prefix);
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      throws SAXException
   {
      content.startElement(namespaceURI, localName, qName, atts);
   }

   public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException
   {
      content.endElement(namespaceURI, localName, qName);
   }

   public void characters(char ch[], int start, int length)
      throws SAXException
   {
      content.characters(ch, start, length);
   }

   public void ignorableWhitespace(char ch[], int start, int length)
      throws SAXException
   {
   }

   public void processingInstruction(String target, String data)
      throws SAXException
   {
   }

   public void skippedEntity(String name)
      throws SAXException
   {
   }
}