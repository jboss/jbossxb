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
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class MinOccurs0UnitTestCase
   extends AbstractJBossXBTest
{
   private static final String XSD =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  targetNamespace='http://www.jboss.org/test/xml/minoccurs'" +
      "  xmlns='http://www.jboss.org/test/xml/minoccurs'" +
      "  elementFormDefault='qualified'" +
      "  xmlns:jbxb='" +
      Constants.NS_JBXB +
      "'" +
      "  version='1.0'>" +
      "<xsd:element name='e'>" +
      "  <xsd:annotation>" +
      "    <xsd:appinfo>" +
      "      <jbxb:class impl='" +
      E.class.getName() +
      "'/>" +
      "    </xsd:appinfo>" +
      "  </xsd:annotation>" +
      "  <xsd:complexType>" +
      "    <xsd:sequence>" +
      "      <xsd:element name='value' type='xsd:string' minOccurs='0'/>" +
      "    </xsd:sequence>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "</xsd:schema>";

   private static final String MBEAN_XSD =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='" + Constants.NS_JBXB + "' version='1.0'>" +
      "<xsd:element name='mbean'>" +
      "  <xsd:annotation>" +
      "    <xsd:appinfo>" +
      "      <jbxb:class impl='" + ClassForMBean.class.getName() + "'/>" +
      "    </xsd:appinfo>" +
      "  </xsd:annotation>" +
      "  <xsd:complexType>" +
      "    <xsd:sequence>" +
      "      <xsd:element minOccurs='0' ref='description'/>" +
      "      <xsd:element minOccurs='0' ref='descriptors'/>" +
      "    </xsd:sequence>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "<xsd:element name='description' type='xsd:string'/>" +
      "<xsd:element name='descriptors' type='xsd:string'/>" +
      "</xsd:schema>";

   private static SchemaBinding SCHEMA;

   private static final String XML_NO_VALUE =
      "<e xmlns='http://www.jboss.org/test/xml/minoccurs'>" +
      "</e>";

   private static final String XML_WITH_VALUE =
      "<e xmlns='http://www.jboss.org/test/xml/minoccurs'>" +
      "<value>val</value>" +
      "</e>";

   public MinOccurs0UnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null);
         SCHEMA.setIgnoreUnresolvedFieldOrClass(false);
      }
   }

   public void testUnmarshallingNoValue() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML_NO_VALUE), SCHEMA);
      assertNotNull(o);
      assertTrue(o instanceof E);
      E e = (E)o;
      assertNull(e.value);
   }

   public void testUnmarshallingWithValue() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML_WITH_VALUE), SCHEMA);
      assertNotNull(o);
      assertTrue(o instanceof E);
      E e = (E)o;
      assertEquals("val", e.value);
   }

   public void testMarshallingNoValueXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), new E(), writer);
      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML_NO_VALUE, xml);
   }

   public void testMarshallingWithValueXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), new E("val"), writer);
      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML_WITH_VALUE, xml);
   }

   public void testMarshallingNoValueSunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(SCHEMA, null, new E(), writer);
      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML_NO_VALUE, xml);
   }

   public void testMarshallingWithValueSunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(SCHEMA, null, new E("val"), writer);
      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML_WITH_VALUE, xml);
   }

   public void testMBeanUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(MBEAN_XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);

      String xml = "<mbean><descriptors>descriptors</descriptors></mbean>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), schema);

      assertNotNull(o);
      assertTrue(o instanceof ClassForMBean);
      ClassForMBean mb = (ClassForMBean)o;
      assertNull(mb.description);
      assertEquals("descriptors", mb.descriptors);
   }

   // Inner

   public static final class E
   {
      public String value;

      public E()
      {
      }

      public E(String value)
      {
         this.value = value;
      }
   }

   public static final class ClassForMBean
   {
      public String description;
      public String descriptors;
   }
}
