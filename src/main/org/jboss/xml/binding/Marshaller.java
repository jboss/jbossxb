/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

/**
 * An interface for marshaller implementations, e.g. DTD and XML schema marshallers.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public interface Marshaller
{
   String VERSION = "1.0";
   String ENCODING = "UTF-8";

   void setVersion(String version);
   void setEncoding(String encoding);

   void mapPublicIdToSystemId(String publicId, String systemId);

   void mapProviderToNamespace(ObjectModelProvider provider, String namespaceUri);

   void mapClassToNamespace(Class cls, String root, String namespaceUri, Reader schemaReader, ObjectModelProvider provider);

   void addRootElement(String namespaceUri, String prefix, String name);

   void marshal(Reader schema, ObjectModelProvider provider, Object document, Writer writer)
      throws IOException, SAXException, ParserConfigurationException;
}
