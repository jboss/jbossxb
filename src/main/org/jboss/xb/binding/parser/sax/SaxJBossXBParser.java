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
package org.jboss.xb.binding.parser.sax;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jboss.logging.Logger;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SaxJBossXBParser
   implements JBossXBParser
{
   private static final Logger log = Logger.getLogger(SaxJBossXBParser.class);

   private final SAXParser parser;
   private JBossXBParser.ContentHandler contentHandler;

   public SaxJBossXBParser()
      throws JBossXBException
   {
      SAXParserFactory saxFactory = SAXParserFactory.newInstance();
      saxFactory.setValidating(true);
      saxFactory.setNamespaceAware(true);

      try
      {
         parser = saxFactory.newSAXParser();
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to create a new SAX parser", e);
      }

      XMLReader reader = null;
      try
      {
         reader = parser.getXMLReader();
      }
      catch(SAXException e)
      {
         throw new JBossXBException("Failed to get parser's XMLReader", e);
      }

      reader.setContentHandler(new DelegatingContentHandler());
      reader.setErrorHandler(new MetaDataErrorHandler());
      reader.setEntityResolver(new JBossEntityResolver());

      setFeature(Unmarshaller.VALIDATION, true);
      setFeature(Unmarshaller.SCHEMA_VALIDATION, true);
      setFeature(Unmarshaller.SCHEMA_FULL_CHECKING, true);
      setFeature(Unmarshaller.DYNAMIC_VALIDATION, true);
      setFeature(Unmarshaller.NAMESPACES, true);
   }

   // JBossXBParser implementation

   public void setEntityResolver(EntityResolver entityResolver)
      throws JBossXBException
   {
      try
      {
         parser.getXMLReader().setEntityResolver(entityResolver);
      }
      catch(SAXException e)
      {
         throw new JBossXBException("Failed to set EntityResolver", e);
      }
   }

   public void setProperty(String name, Object value) throws JBossXBException
   {
      try
      {
         parser.getXMLReader().setProperty(name, value);
      }
      catch(SAXException e)
      {
         throw new JBossXBException("Failed to get parser's XMLReader", e);
      }
   }

   public void setFeature(String name, boolean value) throws JBossXBException
   {
      try
      {
         parser.getXMLReader().setFeature(name, value);
      }
      catch(SAXException e)
      {
         throw new JBossXBException("Failed to get parser's XMLReader", e);
      }
   }

   public void parse(String systemId, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;
      try
      {
         parser.getXMLReader().parse(systemId);
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to parse source: " + e.getMessage(), e);
      }
   }

   public void parse(InputStream is, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;
      try
      {
         parser.getXMLReader().parse(new InputSource(is));
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to parse source: " + e.getMessage(), e);
      }
   }

   public void parse(Reader reader, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;
      try
      {
         parser.getXMLReader().parse(new InputSource(reader));
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to parse source: " + e.getMessage(), e);
      }
   }

   // Inner

   private final class DelegatingContentHandler
      implements org.xml.sax.ContentHandler
   {
      boolean trace = log.isTraceEnabled();
      
      public void endDocument()
      {
      }

      public void startDocument()
      {
      }

      public void characters(char ch[], int start, int length)
      {
         // todo look at this later
         // do not notify content handler if these are just whitespaces
         int i = start;
         while(i < start + length)
         {
            if(!Character.isWhitespace(ch[i++]))
            {
               contentHandler.characters(ch, start, length);
               break;
            }
         }
      }

      public void ignorableWhitespace(char ch[], int start, int length)
      {
      }

      public void endPrefixMapping(String prefix)
      {
         contentHandler.endPrefixMapping(prefix);
      }

      public void skippedEntity(String name)
      {
      }

      public void setDocumentLocator(Locator locator)
      {
      }

      public void processingInstruction(String target, String data)
      {
         contentHandler.processingInstruction(target, data);         
      }

      public void startPrefixMapping(String prefix, String uri)
      {
         contentHandler.startPrefixMapping(prefix, uri);
      }

      public void endElement(String namespaceURI, String localName, String qName)
      {
         String name = null;
         if (trace)
         {
            if (localName.length() == 0)
               name = qName;
            else
               name = namespaceURI + ':' + localName; 
            log.trace("endElement enter " + name);
         }
         try
         {
            contentHandler.endElement(namespaceURI, localName, qName);
         }
         finally
         {
            if (trace)
               log.trace("endElement exit  " + name);
         }
      }

      public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      {
         String name = null;
         if (trace)
         {
            if (localName.length() == 0)
               name = qName;
            else
               name = namespaceURI + ':' + localName; 
            log.trace("startElement enter " + name);
         }
         try
         {
            contentHandler.startElement(namespaceURI, localName, qName, atts, null);
         }
         finally
         {
            if (trace)
               log.trace("startElement exit  " + name);
         }
      }
   }

   private static final class MetaDataErrorHandler
      implements ErrorHandler
   {
      public void warning(SAXParseException exception)
      {
         log.warn(formatMessage(exception));
      }

      public void error(SAXParseException exception)
         throws SAXException
      {
         throw new SAXException(formatMessage(exception));
      }

      public void fatalError(SAXParseException exception)
         throws SAXException
      {
         throw new SAXException(formatMessage(exception));
      }

      public String formatMessage(SAXParseException exception)
      {
         StringBuffer buffer = new StringBuffer(50);
         buffer.append(exception.getMessage()).append(" @ ");
         String location = exception.getPublicId();
         if(location != null)
         {
            buffer.append(location);
         }
         else
         {
            location = exception.getSystemId();
            if(location != null)
            {
               buffer.append(location);
            }
            else
            {
               buffer.append("*unknown*");
            }
         }
         buffer.append('[');
         buffer.append(exception.getLineNumber()).append(',');
         buffer.append(exception.getColumnNumber()).append(']');
         return buffer.toString();
      }
   }
}
