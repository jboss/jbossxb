/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Unmarshaller implementation.
 * WARNING: this implementation is not thread-safe.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class Unmarshaller
{
   private static final Category log = Category.getInstance(Unmarshaller.class);

   private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   private static final String VALIDATION = "http://xml.org/sax/features/validation";
   private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";

   private ObjectModelBuilder builder = new ObjectModelBuilder();
   private XMLReader reader;
   private SAXParser parser;
   private EntityResolver entityResolver;

   public static void mapPublicIdToSystemId(String publicId, String dtdLocation)
   {
      MetaDataEntityResolver.mapPublicIdToSystemId(publicId, dtdLocation);
   }

   // Constructor

   public Unmarshaller(boolean hasDTD)
      throws SAXException, ParserConfigurationException
   {
      SAXParserFactory saxFactory = SAXParserFactory.newInstance();
      saxFactory.setValidating(true);
      saxFactory.setNamespaceAware(true);

      parser = saxFactory.newSAXParser();

      if (hasDTD == false)
         parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

      reader = parser.getXMLReader();
      reader.setContentHandler(new ContentPopulator());
      reader.setDTDHandler(new MetaDataDTDHandler());
      reader.setErrorHandler(new MetaDataErrorHandler());

      entityResolver = new CatalogResolver(); //MetaDataEntityResolver(null);
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
      reader.setFeature(name, value);
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

   public void setSchemaSource(String schemaPath)
      throws SAXNotSupportedException, SAXNotRecognizedException
   {
      parser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaPath));
   }

   public void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri)
   {
      builder.mapFactoryToNamespace(factory, namespaceUri);
   }

   public Object unmarshal(InputStream is, ObjectModelFactory factory, Object root)
      throws SAXException, IOException
   {
      InputSource source = new InputSource(is);
      return unmarshal(source, factory, root);
   }

   public Object unmarshal(Reader reader, ObjectModelFactory factory, Object root)
      throws SAXException, IOException
   {
      InputSource source = new InputSource(reader);
      return unmarshal(source, factory, root);
   }

   public Object unmarshal(InputSource source, ObjectModelFactory factory, Object root)
      throws IOException, SAXException
   {
      reader.parse(source);
      ContentPopulator populator = (ContentPopulator)reader.getContentHandler();
      Content content = populator.getContent();
      return builder.build(factory, root, content);
   }

   // Private

   private static String getResource(String name)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(name);
      if(url == null)
         throw new IllegalStateException("Resource not found: " + name);
      return url.toString();
   }

   // Inner

   private static final class MetaDataEntityResolver
      extends ChainedEntityResolver
   {
      private static final String EJB_JAR_20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
      private static final String JBOSS_32 = "-//JBoss//DTD JBOSS 3.2//EN";
      private static final String JBOSSCMP_JDBC_32 = "-//JBoss//DTD JBOSSCMP-JDBC 3.2//EN";

      private static final Map DTD_FOR_ID = new HashMap();

      static
      {
         DTD_FOR_ID.put(EJB_JAR_20, "ejb-jar_2_0.dtd");
         DTD_FOR_ID.put(JBOSS_32, "jboss_3_2.dtd");
         DTD_FOR_ID.put(JBOSSCMP_JDBC_32, "jbosscmp-jdbc_3_2.dtd");
      }

      public static final void mapPublicIdToSystemId(String publicId, String systemId)
      {
         DTD_FOR_ID.put(publicId, systemId);
      }

      public MetaDataEntityResolver(ChainedEntityResolver next)
      {
         super(next);
      }

      protected InputSource tryToResolveEntity(String publicId, String systemId)
         throws IOException
      {
         if(publicId == null && systemId == null)
            throw new IllegalStateException("Validation error: neither publicId nor systemId is specified.");

         InputSource source = null;
         if(publicId != null)
         {
            String dtd = (String)DTD_FOR_ID.get(publicId);
            if(dtd != null)
            {
               String dtdPath = getResource(dtd);
               source = new InputSource(dtdPath);
            }
         }
         else
         {
            URL url = new URL(systemId);
            source = new InputSource(url.openStream());
         }

         return source;
      }
   }

   private static abstract class ChainedEntityResolver
      implements EntityResolver
   {
      private final ChainedEntityResolver next;

      public ChainedEntityResolver(ChainedEntityResolver next)
      {
         this.next = next;
      }

      // EntityResolver implementation

      public InputSource resolveEntity(String publicId, String systemId)
         throws SAXException, IOException
      {
         InputSource source = tryToResolveEntity(publicId, systemId);
         if(source == null)
         {
            if(next != null)
            {
               source = next.resolveEntity(publicId, systemId);
            }
         }

         if(source == null)
         {
            if(systemId != null)
            {
               source = new InputSource(systemId);
            }
            else
            {

               throw new IllegalStateException(
                  "Failed to resolve entity: publicId=" + publicId + ", systemId=" + systemId
               );
            }
         }

         return source;
      }

      // Protected

      protected abstract InputSource tryToResolveEntity(String publicId, String systemId)
         throws SAXException, IOException;
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
