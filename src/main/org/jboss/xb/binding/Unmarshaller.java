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
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.metadata.unmarshalling.DocumentBinding;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface Unmarshaller
{
   String VALIDATION = "http://xml.org/sax/features/validation";
   String NAMESPACES = "http://xml.org/sax/features/namespaces";
   String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
   // set some xerces specific features that allow transparent DTD and Schema validation
   String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
   String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
   String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";

   void setValidation(boolean validation) throws JBossXBException;

   void setNamespaceAware(boolean namespaces) throws JBossXBException;

   void setSchemaValidation(boolean validation) throws JBossXBException;
   
   void setFeature(String feature, boolean value) throws JBossXBException;

   void setEntityResolver(EntityResolver entityResolver) throws JBossXBException;

   void setErrorHandler(ErrorHandler errorHandler);

   void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri);

   Object unmarshal(String xmlFile, JBossXBParser.ContentHandler handler) throws JBossXBException;

   Object unmarshal(String xmlFile) throws JBossXBException;

   Object unmarshal(String xml, SchemaBinding schemaBinding) throws JBossXBException;

   Object unmarshal(Reader xmlReader, SchemaBinding schemaBinding) throws JBossXBException;

   Object unmarshal(InputStream xmlStream, SchemaBinding schemaBinding) throws JBossXBException;

   Object unmarshal(String xml, SchemaBindingResolver schemaResolver) throws JBossXBException;

   Object unmarshal(Reader xmlReader, SchemaBindingResolver schemaResolver) throws JBossXBException;

   Object unmarshal(InputStream xmlStream, SchemaBindingResolver schemaResolver) throws JBossXBException;

   Object unmarshal(Reader reader, ObjectModelFactory factory, Object root) throws JBossXBException;

   Object unmarshal(InputStream is, ObjectModelFactory factory, Object root) throws JBossXBException;

   Object unmarshal(String systemId, ObjectModelFactory factory, Object root) throws JBossXBException;
   Object unmarshal(String systemId, ObjectModelFactory factory, DocumentBinding binding) throws JBossXBException;
   Object unmarshal(Reader reader, ObjectModelFactory factory, DocumentBinding binding) throws JBossXBException;
}
