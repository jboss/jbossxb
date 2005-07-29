/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import java.io.InputStream;
import java.io.Reader;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.xb.binding.metadata.unmarshalling.BindingCursor;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBindingFactory;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBindingStack;
import org.jboss.xb.binding.metadata.unmarshalling.impl.RuntimeDocumentBinding;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.xb.binding.parser.sax.SaxJBossXBParser;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

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
      parser = new SaxJBossXBParser();
      //parser = new XniJBossXBParser();

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

   public Object unmarshal(String xmlFile, JBossXBParser.ContentHandler handler) throws JBossXBException
   {
      parser.parse(xmlFile, handler);
      return handler.getRoot();
   }

   public Object unmarshal(String xml, SchemaBinding schemaBinding) throws JBossXBException
   {
      SundayContentHandler cHandler = new SundayContentHandler(schemaBinding);
      parser.parse(xml, cHandler);
      return cHandler.getRoot();
   }

   public Object unmarshal(Reader xmlReader, SchemaBinding schemaBinding) throws JBossXBException
   {
      SundayContentHandler cHandler = new SundayContentHandler(schemaBinding);
      parser.parse(xmlReader, cHandler);
      return cHandler.getRoot();
   }

   public Object unmarshal(InputStream xmlStream, SchemaBinding schemaBinding)
      throws JBossXBException
   {
      SundayContentHandler cHandler = new SundayContentHandler(schemaBinding);
      parser.parse(xmlStream, cHandler);
      return cHandler.getRoot();
   }

   public Object unmarshal(String xml, SchemaBindingResolver schemaResolver) throws JBossXBException
   {
      SundayContentHandler cHandler = new SundayContentHandler(schemaResolver);
      parser.parse(xml, cHandler);
      return cHandler.getRoot();
   }

   public Object unmarshal(Reader xmlReader, SchemaBindingResolver schemaResolver) throws JBossXBException
   {
      SundayContentHandler cHandler = new SundayContentHandler(schemaResolver);
      parser.parse(xmlReader, cHandler);
      return cHandler.getRoot();
   }

   public Object unmarshal(InputStream xmlStream, SchemaBindingResolver schemaResolver) throws JBossXBException
   {
      SundayContentHandler cHandler = new SundayContentHandler(schemaResolver);
      parser.parse(xmlStream, cHandler);
      return cHandler.getRoot();
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
