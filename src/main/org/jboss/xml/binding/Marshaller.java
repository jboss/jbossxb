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
   String MARSHALLER_IMPL = "org.jboss.xml.binding.Marshaller";

   class FACTORY
   {
      public static Marshaller getInstance()
      {
         String impl = System.getProperty(MARSHALLER_IMPL);
         if(impl == null)
         {
            throw new IllegalStateException("Required system property is not set: " + MARSHALLER_IMPL);
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
}
