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

import java.util.List;
import java.util.Collections;
import java.io.StringReader;
import java.io.StringWriter;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.LSInputAdaptor;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.test.xml.book.Book;
import org.w3c.dom.ls.LSInput;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45336 $</tt>
 */
public class SchemaImportUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String NS = "http://www.jboss.org/test/xml/schema-import";
   private static final String XSD = "<xsd:schema" +
      "   targetNamespace='" + NS + "'" +
      "   xmlns='" + NS + "'" +
      "   xmlns:bk='http://example.org/ns/books/'" +
      "   xmlns:jbxb='" + Constants.NS_JBXB + "'" +
      "   xmlns:xsd='http://www.w3.org/2001/XMLSchema'>" +
      "   <xsd:import namespace='http://example.org/ns/books/'/>" +
      "   <xsd:element name='root'>" +
      "      <xsd:complexType>" +
      "         <xsd:annotation>" +
      "            <xsd:appinfo>" +
      "               <jbxb:class impl='java.util.ArrayList'/>" +
      "            </xsd:appinfo>" +
      "         </xsd:annotation>" +
      "         <xsd:sequence>" +
      "            <xsd:element ref='bk:book' maxOccurs='unbounded'/>" +
      "         </xsd:sequence>" +
      "      </xsd:complexType>" +
      "   </xsd:element>" +
      "</xsd:schema>";

   private static final String XML = "<root xmlns='" + NS + "'" +
      " xmlns:bk='http://example.org/ns/books/'>" +
      "<bk:book isbn='123'>" +
      "<bk:title>Ti t le</bk:title>" +
      "<bk:author>A.U.Thor</bk:author>" +
      "</bk:book>" +
      "</root>";

   private static final SchemaBindingResolver SCHEMA_RESOLVER = new SchemaBindingResolver()
   {
      public String getBaseURI()
      {
         throw new UnsupportedOperationException("getBaseURI is not implemented.");
      }

      public void setBaseURI(String baseURI)
      {
         throw new UnsupportedOperationException("setBaseURI is not implemented.");
      }

      public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
      {
         throw new UnsupportedOperationException("resolve is not implemented.");
      }

      public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
      {
         if("http://example.org/ns/books/".equals(nsUri))
         {
            return new LSInputAdaptor(
               Thread.currentThread().getContextClassLoader().
                  getResourceAsStream("xml/book/annotated_books.xsd"), null);
         }
         return null;
      }
   };

   public SchemaImportUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null, SCHEMA_RESOLVER);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof List);
      List list = (List)o;
      assertEquals(1, list.size());
      o = list.get(0);
      assertTrue(o instanceof Book);
      Book book = (Book)o;
      assertEquals("123", book.getIsbn());
      assertEquals("Ti t le", book.getTitle());
      assertEquals("A.U.Thor", book.getAuthor());
   }

   public void testMarshallingXerces() throws Exception
   {
      Book book = new Book();
      book.setIsbn("123");
      book.setTitle("Ti t le");
      book.setAuthor("A.U.Thor");

      String marshalled = marshalXerces(book);

      assertXmlEqual(XML, marshalled);
   }

   public void testMarshallingSunday() throws Exception
   {
      Book book = new Book();
      book.setIsbn("123");
      book.setTitle("Ti t le");
      book.setAuthor("A.U.Thor");

      String marshalled = marshalSunday(book);

      assertXmlEqual(XML, marshalled);
   }

   private String marshalXerces(Book book)
      throws Exception
   {
      StringWriter writer = new StringWriter();
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      initMarshaller(marshaller);
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), Collections.singleton(book), writer);
      return writer.getBuffer().toString();
   }

   private String marshalSunday(Book book)
      throws Exception
   {
      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      initMarshaller(marshaller);
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), Collections.singleton(book), writer);
      return writer.getBuffer().toString();
   }

   private void initMarshaller(AbstractMarshaller marshaller)
   {
      marshaller.setProperty(Marshaller.PROP_OUTPUT_XML_VERSION, "false");
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.declareNamespace("", NS);
      marshaller.declareNamespace("bk", "http://example.org/ns/books/");
      marshaller.addRootElement(NS, "", "root");
   }
}
