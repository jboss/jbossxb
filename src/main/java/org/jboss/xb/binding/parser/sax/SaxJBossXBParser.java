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
import org.jboss.util.JBossStringBuilder;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.JBossXBRuntimeException;
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

   private static final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
   static
   {
      saxFactory.setValidating(true);
      saxFactory.setNamespaceAware(true);

      try
      {
         saxFactory.setXIncludeAware(true);
      }
      catch (UnsupportedOperationException e)
      {
         log.trace("setXIncludeAware is not supported by the SAXParserFactory", e);
      }
   }

   private final SAXParser parser;
   private final XMLReader reader;
   private JBossXBParser.ContentHandler contentHandler;
   private DelegatingContentHandler delegateHandler;
   private boolean trace;

   public SaxJBossXBParser()
      throws JBossXBException
   {
      try
      {
         parser = saxFactory.newSAXParser();
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to create a new SAX parser", e);
      }

      try
      {
         reader = parser.getXMLReader();
      }
      catch(SAXException e1)
      {
         throw new JBossXBRuntimeException("Failed to get parser's XMLReader", e1);
      }

      delegateHandler = new DelegatingContentHandler();
      reader.setContentHandler(delegateHandler);
      reader.setErrorHandler(MetaDataErrorHandler.INSTANCE);
      reader.setEntityResolver(new JBossEntityResolver());
      // LexicalHandler
      try
      {
         reader.setProperty("http://xml.org/sax/properties/lexical-handler", delegateHandler);
      }
      catch(Exception e)
      {
         log.trace("LexicalHandler", e);
      }

/*
      setFeature(Unmarshaller.VALIDATION, true);
      setFeature(Unmarshaller.SCHEMA_VALIDATION, true);
      setFeature(Unmarshaller.SCHEMA_FULL_CHECKING, true);
      setFeature(Unmarshaller.DYNAMIC_VALIDATION, true);
      setFeature(Unmarshaller.NAMESPACES, true);
*/
   }

   // JBossXBParser implementation

   public void setEntityResolver(EntityResolver entityResolver)
      throws JBossXBException
   {
      reader.setEntityResolver(entityResolver);
   }

   public void setProperty(String name, Object value)
   {
      try
      {
         reader.setProperty(name, value);
      }
      catch(SAXException e)
      {
         throw new JBossXBRuntimeException("Failed to set property on the XML reader", e);
      }
   }

   public void setFeature(String name, boolean value)
   {
      try
      {
         reader.setFeature(name, value);
         if(trace)
            log.trace(name+" set to: "+reader.getFeature(name));
      }
      catch(SAXException e)
      {
         throw new JBossXBRuntimeException("Failed to set feature on the XMLReader", e);
      }
   }

   public void parse(String systemId, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;
      trace = log.isTraceEnabled();
      if(trace)
         logParserInfo();
      
      try
      {
         reader.parse(systemId);
      }
      catch(Throwable e)
      {
         throw new JBossXBException("Failed to parse source: " + getLocationAsString(systemId), e);
      }
   }

   public void parse(InputStream is, ContentHandler handler) throws JBossXBException
   {
      parse(new InputSource(is), handler);
   }

   public void parse(Reader reader, ContentHandler handler) throws JBossXBException
   {
      parse(new InputSource(reader), handler);
   }

   public void parse(InputSource source, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;
      trace = log.isTraceEnabled();
      if(trace)
         logParserInfo();

      try
      {
         reader.parse(source);
      }
      catch(Throwable e)
      {
         throw new JBossXBException("Failed to parse source: " + e.getMessage(), e);
      }
   }

   public String getLocationAsString(String fileName)
   {
      Locator locator = delegateHandler.getDocumentLocator();
      if (locator == null)
         return fileName;
      else
      {
         JBossStringBuilder buffer = new JBossStringBuilder();
         String id = locator.getSystemId();
         if (id == null)
            id = locator.getPublicId();
         buffer.append(id).append('@');
         buffer.append(locator.getLineNumber());
         buffer.append(',');
         buffer.append(locator.getColumnNumber());
         return buffer.toString();
      }
   }

   private void logParserInfo()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("Created parser: ").append(parser)
      .append(", isNamespaceAware: ").append(parser.isNamespaceAware())
      .append(", isValidating: ").append(parser.isValidating())
      .append(", isXIncludeAware: ");
      try
      {
         sb.append(parser.isXIncludeAware());
      }
      catch(UnsupportedOperationException e)
      {
         sb.append("unsupported operation '").append(e.getMessage()).append('\'');
      }
      log.trace(sb.toString());
   }

   // Inner

   private final class DelegatingContentHandler
      implements org.xml.sax.ContentHandler,
      org.xml.sax.ext.LexicalHandler
   {
      Locator locator;
      // Begin LexicalHandler

      public void comment(char[] ch, int start, int length) throws SAXException
      {
         // TODO Auto-generated method stub
         
      }

      public void startDTD(String name, String publicId, String systemId) throws SAXException
      {
         if( contentHandler instanceof DtdAwareContentHandler )
         {
            DtdAwareContentHandler dach = (DtdAwareContentHandler) contentHandler;
            dach.startDTD(name, publicId, systemId);
         }
      }
      public void endDTD() throws SAXException
      {
         if( contentHandler instanceof DtdAwareContentHandler )
         {
            DtdAwareContentHandler dach = (DtdAwareContentHandler) contentHandler;
            dach.endDTD();
         }
      }

      public void startEntity(String name) throws SAXException
      {
         // TODO Auto-generated method stub
         
      }
      public void endEntity(String name) throws SAXException
      {
         // TODO Auto-generated method stub
         
      }

      public void startCDATA() throws SAXException
      {
         // TODO Auto-generated method stub
         
      }
      public void endCDATA() throws SAXException
      {
         // TODO Auto-generated method stub
         
      }

      // End LexicalHandler

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
         //int i = start;
         //while(i < start + length)
         //{
         //   if(!Character.isWhitespace(ch[i++]))
         //   {
               contentHandler.characters(ch, start, length);
         //      break;
         //   }
         //}
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

      public Locator getDocumentLocator()
      {
         return locator;
      }
      
      public void setDocumentLocator(Locator locator)
      {
         this.locator = locator;
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
         if(trace)
         {
            String name = localName.length() == 0 ? qName : namespaceURI + ':' + localName;
            log.trace("Enter endElement " + name);
            try
            {
               contentHandler.endElement(namespaceURI, localName, qName);
            }
            finally
            {
               log.trace("Exit endElement  " + name);
            }
         }
         else
            contentHandler.endElement(namespaceURI, localName, qName);
      }

      public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      {
         if(trace)
         {
            String name = localName.length() == 0 ? qName : namespaceURI + ':' + localName;
            log.trace("Enter startElement " + name);
            
            try
            {
               contentHandler.startElement(namespaceURI, localName, qName, atts);
            }
            finally
            {
               log.trace("Exit startElement  " + name);
            }
         }
         else
            contentHandler.startElement(namespaceURI, localName, qName, atts);
      }
   }

   private static final class MetaDataErrorHandler
      implements ErrorHandler
   {
      public static final ErrorHandler INSTANCE = new MetaDataErrorHandler();

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
