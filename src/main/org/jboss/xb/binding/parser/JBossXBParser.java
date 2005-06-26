/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.parser;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.jboss.xb.binding.JBossXBException;
import org.apache.xerces.xs.XSTypeDefinition;

import java.io.Reader;
import java.io.InputStream;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface JBossXBParser
{
   interface ContentHandler
   {
      void characters(char[] ch, int start, int length);

      void endElement(String namespaceURI, String localName, String qName);

      void startElement(String namespaceURI, String localName, String qName, Attributes atts, XSTypeDefinition type);

      void startPrefixMapping(String prefix, String uri);

      void endPrefixMapping(String prefix);

      void processingInstruction(String target, String data);

      Object getRoot();
   }

   void setEntityResolver(EntityResolver entityResolver) throws JBossXBException;

   void setProperty(String name, Object value) throws JBossXBException;

   void setFeature(String name, boolean value) throws JBossXBException;

   void parse(String source, ContentHandler handler) throws JBossXBException;

   void parse(InputStream is, ContentHandler handler) throws JBossXBException;

   void parse(Reader reader, ContentHandler handler) throws JBossXBException;
}
