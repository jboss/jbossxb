/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.io.InputStream;

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

   void setValidation(boolean validation)
      throws JBossXBException;

   void setNamespaceAware(boolean namespaces)
      throws JBossXBException;

   void setEntityResolver(EntityResolver entityResolver) throws JBossXBException;

   void setErrorHandler(ErrorHandler errorHandler);

   void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri);

   Object unmarshal(Reader reader, ObjectModelFactory factory, Object root) throws JBossXBException;

   Object unmarshal(InputStream is, ObjectModelFactory factory, Object root) throws JBossXBException;

   Object unmarshal(String systemId, ObjectModelFactory factory, Object root)
      throws JBossXBException;

   Object unmarshal(InputSource is, ObjectModelFactory factory, Object root) throws JBossXBException;
}
