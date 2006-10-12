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
package org.jboss.test.xml;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45119 $</tt>
 */
public class Validator
{
   public static void assertValidXml(final String xsd, String xml)
   {
      assertValidXml(xsd, xml, null);
   }

   public static void assertValidXml(String xml, final EntityResolver resolver)
   {
      assertValidXml(null, xml, resolver);
   }

   private static void assertValidXml(final String xsd, String xml, final EntityResolver resolver)
   {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
      SAXParser parser = null;
      try
      {
         parser = factory.newSAXParser();
      }
      catch(Exception e)
      {
         throw new IllegalStateException("Failed to instantiate a SAX parser: " + e.getMessage());
      }

      try
      {
         parser.getXMLReader().setFeature("http://apache.org/xml/features/validation/schema", true);
      }
      catch(SAXException e)
      {
         throw new IllegalStateException("Schema validation feature is not supported by the parser: " + e.getMessage());
      }

      try
      {
         parser.parse(new ByteArrayInputStream(xml.getBytes()),
            new DefaultHandler()
            {
               public void warning(SAXParseException e)
               {
               }

               public void error(SAXParseException e)
               {
                  throw new JBossXBRuntimeException("Error", e);
               }

               public void fatalError(SAXParseException e)
               {
                  throw new JBossXBRuntimeException("Fatal error", e);
               }

               public InputSource resolveEntity(String publicId, String systemId)
               {
                  if(resolver != null)
                  {
                     try
                     {
                        return resolver.resolveEntity(publicId, systemId);
                     }
                     catch(Exception e)
                     {
                        throw new IllegalStateException("Failed to resolveEntity " + systemId + ": " + systemId);
                     }
                  }
                  else
                  {
                     return new InputSource(new StringReader(xsd));
                  }
               }
            }
         );
      }
      catch(Exception e)
      {
         throw new JBossXBRuntimeException("Parsing failed.", e);
      }
   }
}
