/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.logging.Logger;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.Reader;

/**
 * Unmarshaller implementation.
 * WARNING: this implementation is not thread-safe.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class Unmarshaller
{
   private static final Logger log = Logger.getLogger(Unmarshaller.class);

   private static final String VALIDATION = "http://xml.org/sax/features/validation";
   private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";

   // set some xerces specific features that allow transparent DTD and Schema validation
   private static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
   private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
   private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";

   private ObjectModelBuilder builder = new ObjectModelBuilder();
   private XMLReader reader;
   private SAXParser parser;
   private EntityResolver entityResolver;

   // Constructor

   /**
    * The constructor for DTD and XSD client awareness.
    */
   public Unmarshaller()
      throws SAXException, ParserConfigurationException
   {
      SAXParserFactory saxFactory = SAXParserFactory.newInstance();
      saxFactory.setValidating(true);
      saxFactory.setNamespaceAware(true);

      parser = saxFactory.newSAXParser();

      reader = parser.getXMLReader();
      reader.setContentHandler(new ContentPopulator());
      reader.setDTDHandler(new MetaDataDTDHandler());
      reader.setErrorHandler(new MetaDataErrorHandler());

      setXmlReaderFeature(VALIDATION, true);
      setXmlReaderFeature(SCHEMA_VALIDATION, true);
      setXmlReaderFeature(SCHEMA_FULL_CHECKING, true);
      setXmlReaderFeature(DYNAMIC_VALIDATION, true);
      setXmlReaderFeature(NAMESPACES, true);

      entityResolver = new JBossEntityResolver();
      reader.setEntityResolver(entityResolver);
   }

   public void setValidation(boolean validation)
      throws SAXNotSupportedException, SAXNotRecognizedException
   {
      reader.setFeature(VALIDATION, validation);
   }

   public void setNamespaceAware(boolean namespaces)
      throws SAXNotSupportedException, SAXNotRecognizedException
   {
      reader.setFeature(NAMESPACES, namespaces);
   }

   public void setXmlReaderFeature(String name, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException
   {
      try
      {
         reader.setFeature(name, value);
      }
      catch (SAXNotRecognizedException e)
      {
         log.warn("SAX feature not recognized: " + name);
      }
      catch (SAXNotSupportedException e)
      {
         log.warn("SAX feature not supported: " + name);
      }
   }

   public void setXmlReaderProperty(String name, Object value)
      throws SAXNotRecognizedException, SAXNotSupportedException
   {
      reader.setProperty(name, value);
   }

   public void setDTDHandler(DTDHandler dtdHandler)
   {
      reader.setDTDHandler(dtdHandler);
   }

   public void setEntityResolver(EntityResolver entityResolver)
   {
      reader.setEntityResolver(entityResolver);
      this.entityResolver = entityResolver;
   }

   public void setErrorHandler(ErrorHandler errorHandler)
   {
      reader.setErrorHandler(errorHandler);
   }

   public void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri)
   {
      builder.mapFactoryToNamespace(factory, namespaceUri);
   }

   public Object unmarshal(InputStream is, ObjectModelFactory factory, Object root)
      throws Exception
   {
      InputSource source = new InputSource(is);
      return unmarshal(source, factory, root);
   }

   public Object unmarshal(Reader reader, ObjectModelFactory factory, Object root)
      throws Exception
   {
      InputSource source = new InputSource(reader);
      return unmarshal(source, factory, root);
   }

   public Object unmarshal(InputSource source, ObjectModelFactory factory, Object root)
      throws Exception
   {
      reader.parse(source);
      ContentPopulator populator = (ContentPopulator)reader.getContentHandler();
      Content content = populator.getContent();
      return builder.build(factory, root, content);
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
         if (location != null)
            buffer.append(location);
         else
         {
            location = exception.getSystemId();
            if (location != null)
               buffer.append(location);
            else
               buffer.append("*unknown*");
         }
         buffer.append('[');
         buffer.append(exception.getLineNumber()).append(',');
         buffer.append(exception.getColumnNumber()).append(']');
         return buffer.toString();
      }
   }

   private static final class MetaDataDTDHandler
      implements DTDHandler
   {
      public void notationDecl(String name, String publicId, String systemId)
      {
         log.debug("notationDecl: name=" + name + ", publicId=" + publicId + ", systemId=" + systemId);
      }

      public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
      {
         log.debug("unparsedEntityDecl: name=" + name + ", publicId=" + publicId + ", systemId=" + systemId
            + ", notationName=" + notationName);
      }
   }
}
