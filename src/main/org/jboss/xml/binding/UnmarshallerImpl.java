/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.parser.xni.XniJBossXBParser;
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBindingFactory;
import org.jboss.xml.binding.metadata.unmarshalling.BindingCursor;
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBindingStack;
import org.jboss.xml.binding.metadata.unmarshalling.impl.RuntimeDocumentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.DocumentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.impl.DocumentHandlerImpl;
import org.jboss.util.xml.JBossEntityResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.Reader;
import java.io.InputStream;

/**
 * Unmarshaller implementation.
 * WARNING: this implementation is not thread-safe.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class UnmarshallerImpl
   implements Unmarshaller
{
   private ObjectModelBuilder builder = new ObjectModelBuilder();
   private final JBossXBParser parser;

   // Constructor

   /**
    * The constructor for DTD and XSD client awareness.
    */
   public UnmarshallerImpl()
      throws JBossXBException
   {
      //parser = new SaxJBossXBParser();
      parser = new XniJBossXBParser();

      parser.setFeature(VALIDATION, true);
      parser.setFeature(SCHEMA_VALIDATION, true);
      parser.setFeature(SCHEMA_FULL_CHECKING, true);
      parser.setFeature(DYNAMIC_VALIDATION, true);
      parser.setFeature(NAMESPACES, true);

      parser.setEntityResolver(new JBossEntityResolver());
   }

   public void setValidation(boolean validation)
      throws JBossXBException
   {
      parser.setFeature(VALIDATION, validation);
   }

   public void setNamespaceAware(boolean namespaces)
      throws JBossXBException
   {
      parser.setFeature(NAMESPACES, namespaces);
   }

   public void setEntityResolver(EntityResolver entityResolver) throws JBossXBException
   {
      parser.setEntityResolver(entityResolver);
   }

   public void setErrorHandler(ErrorHandler errorHandler)
   {
      // todo reader.setErrorHandler(errorHandler);
   }

   public void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri)
   {
      builder.mapFactoryToNamespace(factory, namespaceUri);
   }

   public Object unmarshal(String xmlFile) throws JBossXBException
   {
      DocumentBindingStack docBinding = DocumentBindingFactory.newInstance()
         .newDocumentBindingStack()
         .push(RuntimeDocumentBinding.class);
      BindingCursor cursor = BindingCursor.Factory.newCursor(docBinding);
      builder.init(new MetadataDrivenObjectModelFactory(), null, cursor);
      parser.parse(xmlFile, builder);
      return builder.getRoot();
   }

   public Object unmarshal(String xmlFile, DocumentHandler handler) throws JBossXBException
   {
      DocumentHandlerImpl handlerImpl = (DocumentHandlerImpl)handler;

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = null;
      try
      {
         parser = factory.newSAXParser();
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Failed to create new SAX parser: " + e.getMessage(), e);
      }

      try
      {
         parser.getXMLReader().setContentHandler((ContentHandler)handler);
         parser.getXMLReader().parse(xmlFile);
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Failed to parse XML content: " + e.getMessage(), e);
      }

      return handlerImpl.root;
   }

   public Object unmarshal(String xmlFile, ObjectModelFactory factory, DocumentBinding metadata)
      throws JBossXBException
   {
      BindingCursor cursor = BindingCursor.Factory.newCursor(metadata);
      builder.init(factory, null, cursor);
      parser.parse(xmlFile, builder);
      return builder.getRoot();
   }

   public Object unmarshal(Reader xmlFile, ObjectModelFactory factory, DocumentBinding metadata)
      throws JBossXBException
   {
      BindingCursor cursor = BindingCursor.Factory.newCursor(metadata);
      builder.init(factory, null, cursor);
      parser.parse(xmlFile, builder);
      return builder.getRoot();
   }

   public Object unmarshal(Reader reader, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      builder.init(factory, root, BindingCursor.Factory.newCursor(null));
      parser.parse(reader, builder);
      return builder.getRoot();
   }

   public Object unmarshal(InputStream is, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      builder.init(factory, root, BindingCursor.Factory.newCursor(null));
      parser.parse(is, builder);
      return builder.getRoot();
   }

   public Object unmarshal(String systemId, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      builder.init(factory, root, BindingCursor.Factory.newCursor(null));
      parser.parse(systemId, builder);
      return builder.getRoot();
   }

   public Object unmarshal(String systemId, BindingCursor cursor, ObjectModelFactory factory) throws JBossXBException
   {
      builder.init(factory, null, cursor);
      parser.parse(systemId, builder);
      return builder.getRoot();
   }
}
