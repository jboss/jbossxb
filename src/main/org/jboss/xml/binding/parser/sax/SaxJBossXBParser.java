/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.parser.sax;

import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.Unmarshaller;
import org.jboss.xml.binding.JBossXBException;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.logging.Logger;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.Reader;
import java.io.InputStream;

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
         throw new JBossXBException("Failed to parse source.", e);
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
         throw new JBossXBException("Failed to parse source.", e);
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
         throw new JBossXBException("Failed to parse source.", e);
      }
   }

   // Inner

   private final class DelegatingContentHandler
      implements org.xml.sax.ContentHandler
   {
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
      }

      public void startPrefixMapping(String prefix, String uri)
      {
         contentHandler.startPrefixMapping(prefix, uri);
      }

      public void endElement(String namespaceURI, String localName, String qName)
      {
         contentHandler.endElement(namespaceURI, localName, qName);
      }

      public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      {
         contentHandler.startElement(namespaceURI, localName, qName, atts, null);
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
