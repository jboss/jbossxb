/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * org.xml.sax.ContentHandler implementation that poplulates an instance of org.jboss.xml.binding.Content.
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