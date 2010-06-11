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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46059 $</tt>
 */
public class QNameAttributesUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(QNameAttributesUnitTestCase.class);
   }
   
   private static final String XSD =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "   targetNamespace='http://www.jboss.org/test/xml/qnameattr'" +
      "   xmlns='http://www.jboss.org/test/xml/qnameattr'" +
      "   xmlns:jbxb='" +
      Constants.NS_JBXB +
      "'" +
      "   elementFormDefault='qualified' version='1.0'>" +
      "   <xsd:complexType name='UserType'>" +
      "     <xsd:annotation>" +
      "      <xsd:appinfo>" +
      "       <jbxb:class impl='" +
      UserType.class.getName() +
      "'/>" +
      "      </xsd:appinfo>" +
      "     </xsd:annotation>" +
      "     <xsd:sequence>" +
      "        <xsd:element name='arr' type='xsd:QName' minOccurs='0' maxOccurs='unbounded'/>" +
      "     </xsd:sequence>" +
      "     <xsd:attribute name='qname' type='xsd:QName'/>" +
      "     <xsd:attribute name='qname2' type='xsd:QName'/>" +
      "     <xsd:attribute name='qname3' type='xsd:QName'/>" +
      "   </xsd:complexType>" +
      "   <xsd:element name='ut' type='UserType'/>" +
      "</xsd:schema>";

   private static final String XML =
      "<ns_ut:ut xmlns:ns_ut='http://www.jboss.org/test/xml/qnameattr'" +
      " xmlns:ns_local1='http://ns1' qname='ns_local1:local1' qname2='local2'" +
      " xmlns:x='http://x' qname3='x:local3'>" +
      "</ns_ut:ut>";

   private static final String ARR_XML =
      "<ut xmlns='http://www.jboss.org/test/xml/qnameattr'>" +
      "  <arr xmlns:local2_ns='http://ns2'>local2_ns:local2</arr>" +
      "  <arr xmlns:ns3='http://ns3'>ns3:local3</arr>" +
      "  <arr xmlns:ns4='http://ns4'>ns4:local4</arr>" +
      "</ut>";

   private static final MappingObjectModelProvider OM_PROVIDER = new MappingObjectModelProvider();

   public QNameAttributesUnitTestCase(String name)
   {
      super(name);
   }

   // Tests

   public void testMarshallingXerces() throws Exception
   {
      assertMarshalling(new XercesXsMarshaller());
   }

   public void testMarshallingSunday() throws Exception
   {
      assertMarshalling(new MarshallerImpl());
   }

   public void testUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof UserType);
      UserType ut = (UserType)o;
      assertNotNull(ut.qname);
      assertEquals("local1", ut.qname.getLocalPart());
      assertEquals("http://ns1", ut.qname.getNamespaceURI());
      assertNotNull(ut.qname2);
      assertEquals("local2", ut.qname2.getLocalPart());
      assertEquals("", ut.qname2.getNamespaceURI());
      assertNotNull(ut.qname3);
      assertEquals("local3", ut.qname3.getLocalPart());
      assertEquals("http://x", ut.qname3.getNamespaceURI());
   }

   public void testUnmarshallingArray() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(ARR_XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof UserType);
      UserType ut = (UserType)o;
      assertNull(ut.qname);
      assertNull(ut.qname2);
      assertNull(ut.qname3);
      assertNotNull(ut.arr);
      assertEquals(3, ut.arr.length);
      assertEquals(new QName("http://ns2", "local2"), ut.arr[0]);
      assertEquals(new QName("http://ns3", "local3"), ut.arr[1]);
      assertEquals(new QName("http://ns4", "local4"), ut.arr[2]);
   }

   public void testMarshallingArrayXerces() throws Exception
   {
      assertMarshallingArray(new XercesXsMarshaller());
   }

   public void testMarshallingArraySunday() throws Exception
   {
      assertMarshallingArray(new MarshallerImpl());
   }

   // Private

   private void assertMarshallingArray(AbstractMarshaller marshaller)
      throws IOException, SAXException, ParserConfigurationException
   {
      UserType ut = new UserType();
      ut.arr = new QName[]{
         new QName("http://ns2", "local2"),
         new QName("http://ns3", "local3", "ns3"),
         new QName("http://ns4", "local4", "ns4")
      };
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), OM_PROVIDER, ut, writer);
      assertXmlEqual(ARR_XML, writer.getBuffer().toString());
   }

   private void assertMarshalling(AbstractMarshaller marshaller)
      throws IOException, SAXException, ParserConfigurationException
   {
      UserType ut = new UserType();
      ut.qname = new QName("http://ns1", "local1");
      ut.qname2 = new QName("local2");
      ut.qname3 = new QName("http://x", "local3");

      StringWriter writer = new StringWriter();
      marshaller.declareNamespace("x", "http://x");
      marshaller.marshal(new StringReader(XSD), OM_PROVIDER, ut, writer);

      assertXmlEqual(XML, writer.getBuffer().toString());
   }

   public static final class UserType
   {
      public QName qname;
      public QName qname2;
      public QName qname3;
      public QName[] arr;
   }
}
