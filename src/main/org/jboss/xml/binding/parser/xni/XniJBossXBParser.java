/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.parser.xni;

import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.JBossXBException;
import org.jboss.xml.binding.Unmarshaller;
import org.jboss.xml.binding.AttributesImpl;
import org.jboss.logging.Logger;
import org.apache.xerces.parsers.IntegratedParserConfiguration;
import org.apache.xerces.parsers.XMLDocumentParser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.impl.xs.JBossXBSchemaValidator;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XniJBossXBParser
   implements JBossXBParser
{
   private static final Logger log = Logger.getLogger(XniJBossXBParser.class);
   private final XMLParserConfiguration config;
   private final DocumentParser parser;
   private JBossXBParser.ContentHandler contentHandler;

   public XniJBossXBParser()
   {
      config = new ParserConfiguration();
      parser = new DocumentParser(config);

      config.setErrorHandler(new XMLErrorHandler()
      {
         public void warning(String domain, String key, XMLParseException exception) throws XNIException
         {
            log.warn("domain=" + domain + ", key=" + key + ": " + exception.getMessage());
         }

         public void error(String domain, String key, XMLParseException exception) throws XNIException
         {
            log.error("domain=" + domain + ", key=" + key + ": " + exception.getMessage());
            throw exception;
         }

         public void fatalError(String domain, String key, XMLParseException exception) throws XNIException
         {
            log.error("domain=" + domain + ", key=" + key + ": " + exception.getMessage());
            throw exception;
         }
      }
      );

      config.setFeature(Unmarshaller.NAMESPACES, true);
      config.setFeature(Unmarshaller.VALIDATION, true);
      config.setFeature(Unmarshaller.SCHEMA_VALIDATION, true);
      config.setFeature(Unmarshaller.SCHEMA_FULL_CHECKING, true);
      config.setFeature(Unmarshaller.DYNAMIC_VALIDATION, true);
   }

   public void setEntityResolver(final EntityResolver entityResolver) throws JBossXBException
   {
      config.setEntityResolver(new XMLEntityResolver()
      {
         private EntityResolver resolver;

         {
            this.resolver = entityResolver;
         }

         public XMLInputSource resolveEntity(XMLResourceIdentifier resId) throws XNIException,
            IOException
         {
            XMLInputSource result;
            try
            {
               InputSource source = resolver.resolveEntity(resId.getPublicId(), resId.getExpandedSystemId());
               if(source != null)
               {
                  if(source.getCharacterStream() != null)
                  {
                     result = new XMLInputSource(resId.getPublicId(),
                        resId.getExpandedSystemId(),
                        resId.getBaseSystemId(),
                        source.getCharacterStream(),
                        source.getEncoding()
                     );
                  }
                  else if(source.getByteStream() != null)
                  {
                     result = new XMLInputSource(resId.getPublicId(),
                        resId.getExpandedSystemId(),
                        resId.getBaseSystemId(),
                        source.getByteStream(),
                        source.getEncoding()
                     );
                  }
                  else if(source.getSystemId() != null)
                  {
                     result = new XMLInputSource(resId.getPublicId(), source.getSystemId(), resId.getBaseSystemId());
                  }
                  else
                  {
                     throw new IllegalStateException(
                        "Resolved source contains no about the source, i.e. systemId, byte stream and character stream are all null."
                     );
                  }
               }
               else
               {
                  result = null;
               }
            }
            catch(SAXException e)
            {
               throw new XNIException("Failed to resolve entity: publicId=" +
                  resId.getPublicId() +
                  ", literal systemId=" +
                  resId.getLiteralSystemId() +
                  ", base systemId=" +
                  resId.getBaseSystemId()
                  + ", expanded systemId=" + resId.getExpandedSystemId()
               );
            }
            return result;
         }
      }
      );
   }

   public void setProperty(String name, Object value) throws JBossXBException
   {
      config.setProperty(name, value);
   }

   public void setFeature(String name, boolean value) throws JBossXBException
   {
      config.setFeature(name, value);
   }

   public void parse(String systemId, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;

      XMLInputSource xmlSource = new XMLInputSource(null, systemId, null);
      try
      {
         parser.parse(xmlSource);
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to parse document " + systemId, e);
      }
   }

   public void parse(InputStream is, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;

      XMLInputSource xmlSource = new XMLInputSource(null, null, null, is, null);//todo encoding?
      try
      {
         parser.parse(xmlSource);
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to parse document", e);
      }
   }

   public void parse(Reader reader, ContentHandler handler) throws JBossXBException
   {
      this.contentHandler = handler;

      XMLInputSource xmlSource = new XMLInputSource(null, null, null, reader, null);//todo encoding?
      try
      {
         parser.parse(xmlSource);
      }
      catch(Exception e)
      {
         throw new JBossXBException("Failed to parse document", e);
      }
   }

   // Inner

   class DocumentParser
      extends XMLDocumentParser
   {
      private boolean namespaces;
      private boolean prefixes;
      private NamespaceContext namespaceContext;
      private QName qName = new QName();
      private SAXAttributes saxAttrs = new SAXAttributes();

      public DocumentParser(XMLParserConfiguration config)
      {
         super(config);
         namespaces = config.getFeature(Unmarshaller.NAMESPACES);
         //prefixes = config.getFeature(Unmarshaller.NAMESPACE_PREFIXES);
         prefixes = false;
      }

      public void startDocument(XMLLocator locator, String encoding,
                                NamespaceContext namespaceContext,
                                Augmentations augs)
         throws XNIException
      {
         this.namespaceContext = namespaceContext;
      }

      public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
         throws XNIException
      {
      }

      public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
         throws XNIException
      {
      }

      public void comment(XMLString text, Augmentations augs) throws XNIException
      {
      }

      public void processingInstruction(String target, XMLString data, Augmentations augs)
         throws XNIException
      {
      }

      public void startElement(QName name, XMLAttributes attributes, Augmentations augs)
         throws XNIException
      {
         XSTypeDefinition type = null;
         if(augs != null)
         {
            JBossXBSchemaValidator validator = (JBossXBSchemaValidator)augs.getItem("jbossxb.validator");
            if(validator != null)
            {
               XSElementDeclaration element = validator.getCurrentElementDelcaration();
               type = element.getTypeDefinition();
            }
         }

         if(type == null)
         {
            log.warn("Type is not available for " + name.rawname);
         }

         if(namespaces)
         {
            int count = startNamespaceMapping();

            // If there were no new namespaces declared then we can skip searching
            // the attribute list for namespace declarations. Otherwise, remove namespace declaring attributes
            if(count > 0)
            {
               int len = attributes.getLength();
               for(int i = len - 1; i >= 0; i--)
               {
                  attributes.getName(i, qName);

                  if((qName.prefix != null && qName.prefix.equals("xmlns")) ||
                     qName.rawname.equals("xmlns"))
                  {
                     if(!prefixes)
                     {
                        attributes.removeAttributeAt(i);
                     }
                     else
                     {
                        // localpart should be empty string as per SAX documentation:
                        // http://www.saxproject.org/?selected=namespaces
                        qName.prefix = "";
                        qName.uri = "";
                        qName.localpart = "";
                        attributes.setName(i, qName);
                     }
                  }
               }
            }
         }

         String uri = name.uri != null ? name.uri : "";
         String localpart = namespaces ? name.localpart : "";
         saxAttrs.setAttrs(attributes);
         contentHandler.startElement(uri, localpart, name.rawname, saxAttrs, type);
      }
/*
      public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
         throws XNIException
      {
         AttributesImpl attrs = toSaxAttributes(attributes);
         contentHandler.startElement(element.uri, element.localpart, element.rawname, attrs);
      }
*/
      public void startGeneralEntity(String name,
                                     XMLResourceIdentifier identifier,
                                     String encoding,
                                     Augmentations augs) throws XNIException
      {
      }

      public void textDecl(String version, String encoding, Augmentations augs) throws XNIException
      {
      }

      public void endGeneralEntity(String name, Augmentations augs) throws XNIException
      {
      }

      public void characters(XMLString text, Augmentations augs) throws XNIException
      {
         // todo look at this later
         // do not notify content handler if these are just whitespaces
         int i = text.offset;
         while(i < text.offset + text.length)
         {
            if(!Character.isWhitespace(text.ch[i++]))
            {
               contentHandler.characters(text.ch, text.offset, text.length);
               break;
            }
         }
      }

      public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException
      {
      }

      public void endElement(QName element, Augmentations augs) throws XNIException
      {
         contentHandler.endElement(element.uri, element.localpart, element.rawname);
      }

      public void startCDATA(Augmentations augs) throws XNIException
      {
      }

      public void endCDATA(Augmentations augs) throws XNIException
      {
      }

      public void endDocument(Augmentations augs) throws XNIException
      {
         super.endDocument(augs);
      }

      // Private

      protected final int startNamespaceMapping()
      {
         int count = namespaceContext.getDeclaredPrefixCount();
         if(count > 0)
         {
            String prefix = null;
            String uri = null;
            for(int i = 0; i < count; i++)
            {
               prefix = namespaceContext.getDeclaredPrefixAt(i);
               uri = namespaceContext.getURI(prefix);
               contentHandler.startPrefixMapping(prefix, (uri == null) ? "" : uri);
            }
         }
         return count;
      }

      private AttributesImpl toSaxAttributes(XMLAttributes attributes)
      {
         AttributesImpl attrs = null;
         if(attributes != null)
         {
            attrs = new AttributesImpl(attributes.getLength());
            for(int i = 0; i < attributes.getLength(); ++i)
            {
               if(!"xmlns".equals(attributes.getPrefix(i)))
               {
                  attrs.add(attributes.getURI(i),
                     attributes.getLocalName(i),
                     attributes.getQName(i),
                     attributes.getType(i),
                     attributes.getValue(i)
                  );
               }
            }
         }
         return attrs;
      }
   }

   class ParserConfiguration
      extends IntegratedParserConfiguration
   {
      public ParserConfiguration()
      {
      }

      public ParserConfiguration(SymbolTable symbolTable)
      {
         super(symbolTable);
      }

      public ParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool)
      {
         super(symbolTable, grammarPool);
      }

      public ParserConfiguration(SymbolTable symbolTable,
                                 XMLGrammarPool grammarPool,
                                 XMLComponentManager parentSettings)
      {
         super(symbolTable, grammarPool, parentSettings);
      }

      protected void configurePipeline()
      {
         // use XML 1.0 datatype library
         setProperty(DATATYPE_VALIDATOR_FACTORY, fDatatypeValidatorFactory);

         // setup DTD pipeline
         configureDTDPipeline();

         // setup document pipeline
         if(fFeatures.get(NAMESPACES) == Boolean.TRUE)
         {
            fProperties.put(NAMESPACE_BINDER, fNamespaceBinder);
            fScanner = fNamespaceScanner;
            fProperties.put(DOCUMENT_SCANNER, fNamespaceScanner);
            if(fDTDValidator != null)
            {
               fProperties.put(DTD_VALIDATOR, fDTDValidator);
               fNamespaceScanner.setDTDValidator(fDTDValidator);
               fNamespaceScanner.setDocumentHandler(fDTDValidator);
               fDTDValidator.setDocumentSource(fNamespaceScanner);
               fDTDValidator.setDocumentHandler(fDocumentHandler);
               if(fDocumentHandler != null)
               {
                  fDocumentHandler.setDocumentSource(fDTDValidator);
               }
               fLastComponent = fDTDValidator;
            }
            else
            {
               fNamespaceScanner.setDocumentHandler(fDocumentHandler);
               fNamespaceScanner.setDTDValidator(null);
               if(fDocumentHandler != null)
               {
                  fDocumentHandler.setDocumentSource(fNamespaceScanner);
               }
               fLastComponent = fNamespaceScanner;
            }
         }
         else
         {
            fScanner = fNonNSScanner;
            fProperties.put(DOCUMENT_SCANNER, fNonNSScanner);
            if(fNonNSDTDValidator != null)
            {
               fProperties.put(DTD_VALIDATOR, fNonNSDTDValidator);
               fNonNSScanner.setDocumentHandler(fNonNSDTDValidator);
               fNonNSDTDValidator.setDocumentSource(fNonNSScanner);
               fNonNSDTDValidator.setDocumentHandler(fDocumentHandler);
               if(fDocumentHandler != null)
               {
                  fDocumentHandler.setDocumentSource(fNonNSDTDValidator);
               }
               fLastComponent = fNonNSDTDValidator;
            }
            else
            {
               fScanner.setDocumentHandler(fDocumentHandler);
               if(fDocumentHandler != null)
               {
                  fDocumentHandler.setDocumentSource(fScanner);
               }
               fLastComponent = fScanner;
            }
         }

         // setup document pipeline
         if(fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE)
         {
            // If schema validator was not in the pipeline insert it.
            if(fSchemaValidator == null)
            {
               fSchemaValidator = new JBossXBSchemaValidator();

               // add schema component
               fProperties.put(SCHEMA_VALIDATOR, fSchemaValidator);
               addComponent(fSchemaValidator);
               // add schema message formatter
               if(fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null)
               {
                  XSMessageFormatter xmft = new XSMessageFormatter();
                  fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, xmft);
               }

            }

            fLastComponent.setDocumentHandler(fSchemaValidator);
            fSchemaValidator.setDocumentSource(fLastComponent);
            fSchemaValidator.setDocumentHandler(fDocumentHandler);
            if(fDocumentHandler != null)
            {
               fDocumentHandler.setDocumentSource(fSchemaValidator);
            }
            fLastComponent = fSchemaValidator;
         }
      }
   }

   private static class SAXAttributes
      implements org.xml.sax.Attributes
   {
      private XMLAttributes attrs;

      public void setAttrs(XMLAttributes attrs)
      {
         this.attrs = attrs;
      }

      public int getLength()
      {
         return attrs.getLength();
      }

      public String getLocalName(int index)
      {
         return attrs.getLocalName(index);
      }

      public String getQName(int index)
      {
         return attrs.getQName(index);
      }

      public String getType(int index)
      {
         return attrs.getType(index);
      }

      public String getURI(int index)
      {
         return attrs.getURI(index);
      }

      public String getValue(int index)
      {
         return attrs.getValue(index);
      }

      public int getIndex(String qName)
      {
         return attrs.getIndex(qName);
      }

      public String getType(String qName)
      {
         return attrs.getType(qName);
      }

      public String getValue(String qName)
      {
         return attrs.getValue(qName);
      }

      public int getIndex(String uri, String localName)
      {
         return attrs.getIndex(uri, localName);
      }

      public String getType(String uri, String localName)
      {
         return attrs.getType(uri, localName);
      }

      public String getValue(String uri, String localName)
      {
         return attrs.getValue(uri, localName);
      }
      
      public String toString()
      {
         StringBuffer buffer = new StringBuffer();
         buffer.append('(');
         for (int i = 0; i < getLength(); ++i)
         {
            buffer.append(getLocalName(i));
            buffer.append('=');
            buffer.append(getValue(i));
            if (i < getLength()-1)
               buffer.append(", ");
         }
         buffer.append(')');
         return buffer.toString();
      }
   }
}
