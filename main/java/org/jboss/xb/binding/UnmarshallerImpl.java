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

import java.io.InputStream;
import java.io.Reader;

import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.xb.binding.parser.sax.SaxJBossXBParser;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinding;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

/**
 * Unmarshaller implementation.
 * WARNING: this implementation is not thread-safe.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class UnmarshallerImpl implements Unmarshaller
{
   private ObjectModelBuilder builder = new ObjectModelBuilder();
   private final JBossXBParser parser;

   // Constructor

   /**
    * The constructor for DTD and XSD client awareness.
    */
   public UnmarshallerImpl() throws JBossXBException
   {
      parser = new SaxJBossXBParser();
   }

   public void setValidation(boolean validation) throws JBossXBException
   {
      parser.setFeature(VALIDATION, validation);
   }

   public void setSchemaValidation(boolean validation) throws JBossXBException
   {
      parser.setFeature(SCHEMA_VALIDATION, validation);
   }

   public void setFeature(String feature, boolean value) throws JBossXBException
   {
      parser.setFeature(feature, value);
   }

   public void setNamespaceAware(boolean namespaces) throws JBossXBException
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
      if(builder == null)
      {
         builder = new ObjectModelBuilder();
      }
      builder.mapFactoryToNamespace(factory, namespaceUri);
   }

   public Object unmarshal(String xmlFile) throws JBossXBException
   {
      // todo
      throw new UnsupportedOperationException();
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

   public Object unmarshal(InputStream xmlStream, SchemaBinding schemaBinding) throws JBossXBException
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

   public Object unmarshal(Reader reader, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      if(builder == null)
      {
         builder = new ObjectModelBuilder();
      }
      builder.init(factory, root);
      parser.parse(reader, builder);
      return builder.getRoot();
   }

   public Object unmarshal(InputStream is, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      if(builder == null)
      {
         builder = new ObjectModelBuilder();
      }
      builder.init(factory, root);
      parser.parse(is, builder);
      return builder.getRoot();
   }

   public Object unmarshal(String systemId, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      if(builder == null)
      {
         builder = new ObjectModelBuilder();
      }
      builder.init(factory, root);
      parser.parse(systemId, builder);
      return builder.getRoot();
   }

   public Object unmarshal(String systemId, ObjectModelFactory factory, DocumentBinding binding)
      throws JBossXBException
   {
      if(binding != null)
      {
         throw new IllegalStateException("DocumentBinding API is not supported anymore!");
      }
      return unmarshal(systemId, factory, (Object)null);
   }

   public Object unmarshal(Reader reader, ObjectModelFactory factory, DocumentBinding binding) throws JBossXBException
   {
      if(binding != null)
      {
         throw new IllegalStateException("DocumentBinding API is not supported anymore!");
      }
      return unmarshal(reader, factory, (Object)null);
   }

   JBossXBParser getParser()
   {
      return parser;
   }
}
