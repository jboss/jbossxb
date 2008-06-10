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
package org.jboss.xb.binding.parser;

import java.io.InputStream;
import java.io.Reader;

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.xb.binding.JBossXBException;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;

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
   /**
    * Extended to support key SAX2 LexicalHandler events
    */
   interface DtdAwareContentHandler extends ContentHandler
   {
      public void startDTD(String name, String publicId, String systemId);
      public void endDTD();
   }

   void setEntityResolver(EntityResolver entityResolver) throws JBossXBException;

   void setProperty(String name, Object value);

   void setFeature(String name, boolean value);

   void parse(String source, ContentHandler handler) throws JBossXBException;

   void parse(InputStream is, ContentHandler handler) throws JBossXBException;

   void parse(InputStream is, String systemId, ContentHandler handler) throws JBossXBException;

   void parse(Reader reader, ContentHandler handler) throws JBossXBException;

   void parse(Reader reader, String systemId, ContentHandler handler) throws JBossXBException;
}
