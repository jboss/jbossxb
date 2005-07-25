/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

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
   /**
    * Allowed values are true and false. If not set, true is assumed.
    * If true, xml version and encoding will be included into the marshalled XML content.
    */
   String PROP_OUTPUT_XML_VERSION = "org.jboss.xml.binding.marshalling.version";

   /**
    * The value should be a fully qualified class name of the Marshaller implementation.
    * Used by the FACTORY.getInstance().
    */
   String PROP_MARSHALLER = "org.jboss.xml.binding.Marshaller";

   /**
    * Allowed values are true and false. If not set, true is assumed.
    * If true, XML content will be written with indentations, otherwise in one string.
    */
   String PROP_OUTPUT_INDENTATION = "org.jboss.xml.binding.marshalling.indent";

   class FACTORY
   {
      public static Marshaller getInstance()
      {
         String impl = System.getProperty(PROP_MARSHALLER);
         if(impl == null)
         {
            throw new IllegalStateException("Required system property is not set: " + PROP_MARSHALLER);
         }

         Class implCls;
         try
         {
            implCls = Thread.currentThread().getContextClassLoader().loadClass(impl);
         }
         catch(ClassNotFoundException e)
         {
            throw new IllegalStateException("Failed to load marshaller implementation class: " + impl);
         }

         try
         {
            return (Marshaller)implCls.newInstance();
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to instantiate a marshaller: " + implCls);
         }
      }
   }

   String VERSION = "1.0";
   String ENCODING = "UTF-8";

   void setVersion(String version);
   void setEncoding(String encoding);

   void mapPublicIdToSystemId(String publicId, String systemId);

   void mapClassToNamespace(Class cls, String root, String namespaceUri, Reader schemaReader, ObjectModelProvider provider);

   void mapClassToNamespace(Class cls, String root, String namespaceUri, String schemaUrl, ObjectModelProvider provider);

   void addRootElement(String namespaceUri, String prefix, String name);

   void marshal(String schemaUri, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      ParserConfigurationException,
      SAXException;

   void marshal(Reader schema, ObjectModelProvider provider, Object document, Writer writer)
      throws IOException, SAXException, ParserConfigurationException;

   void setProperty(String name, String value);

   String getProperty(String name);
}
