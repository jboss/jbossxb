/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;
import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.parser.xni.XniJBossXBParser;
import org.jboss.xml.binding.parser.sax.SaxJBossXBParser;
import org.jboss.util.xml.JBossEntityResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import java.io.Reader;
import java.io.InputStream;

/**
 * Unmarshaller implementation.
 * WARNING: this implementation is not thread-safe.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class UnmarshallerImpl implements Unmarshaller
{
   private static final Logger log = Logger.getLogger(UnmarshallerImpl.class);

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

   public Object unmarshal(Reader reader, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      builder.init(factory, root);
      parser.parse(reader, builder);
      return builder.getRoot();
   }

   public Object unmarshal(InputStream is, ObjectModelFactory factory, Object root) throws JBossXBException
   {
      builder.init(factory, root);
      parser.parse(is, builder);
      return builder.getRoot();
   }

   public Object unmarshal(String systemId, ObjectModelFactory factory, Object root)
      throws JBossXBException
   {
      builder.init(factory, root);
      parser.parse(systemId, builder);
      return builder.getRoot();
   }
}