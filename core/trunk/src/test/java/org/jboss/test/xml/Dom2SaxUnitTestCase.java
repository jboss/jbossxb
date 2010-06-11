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
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jboss.xb.util.Dom2Sax;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 41882 $</tt>
 */
public class Dom2SaxUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(Dom2SaxUnitTestCase.class);
   }
   
   private static final String XML =
      "<e1 xmlns='http://ns1'" +
      "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
      "e text content" +
      "  <ns2:e2 xmlns:ns2='http://ns2'/>" +
      "  <e3 xmlns='http://ns3' e3_attr1='e3a1 value'>" +
      "    <ns4:e4 xmlns:ns4='http://ns4'>e4 text content</ns4:e4>" +
      "  </e3>" +
      "</e1>";

   public Dom2SaxUnitTestCase(String name)
   {
      super(name);
   }

   public void testMain() throws Exception
   {
      Element e1 = parse();

      StringWriter writer = new StringWriter();
      XMLSerializer ser = new XMLSerializer(writer, null);
      ser.setNamespaces(true);

      OutputFormat of = new OutputFormat();
      of.setOmitXMLDeclaration(true);
      ser.setOutputFormat(of);

      final ContentHandler serCh = ser.asContentHandler();
      Dom2Sax.dom2sax(e1, serCh);
      
      assertXmlEqual(XML, writer.getBuffer().toString());
   }

   private Element parse()
      throws ParserConfigurationException, SAXException, IOException
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document doc = builder.parse(new ByteArrayInputStream(XML.getBytes()));
      return (Element)doc.getFirstChild();
   }
}
