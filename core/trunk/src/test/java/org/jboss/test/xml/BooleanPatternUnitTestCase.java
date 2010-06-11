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

import junit.framework.Test;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class BooleanPatternUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(BooleanPatternUnitTestCase.class);
   }
   
   private static final String XSD = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  targetNamespace='http://www.jboss.org/test/xml/patterns'" +
      "  xmlns='http://www.jboss.org/test/xml/patterns'" +
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
      "      <xsd:element name='eTrue10' type='bool10'/>" +
      "      <xsd:element name='eFalse10' type='bool10'/>" +
      "      <xsd:element name='eTrueTF' type='boolTrueFalse'/>" +
      "      <xsd:element name='eFalseTF' type='boolTrueFalse'/>" +
      "    </xsd:sequence>" +
      "    <xsd:attribute name='aTrueTF' type='boolTrueFalse'/>" +
      "    <xsd:attribute name='aFalseTF' type='boolTrueFalse'/>" +
      "    <xsd:attribute name='aTrue10' type='bool10'/>" +
      "    <xsd:attribute name='aFalse10' type='bool10'/>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "<xsd:simpleType name='bool10'>" +
      "  <xsd:restriction base='xsd:boolean'>" +
      "    <xsd:pattern value='0|1'/>" +
      "  </xsd:restriction>" +
      "</xsd:simpleType>" +
      "<xsd:simpleType name='boolTrueFalse'>" +
      "  <xsd:restriction base='xsd:boolean'>" +
      "    <xsd:pattern value='true|false'/>" +
      "  </xsd:restriction>" +
      "</xsd:simpleType>" +
      "</xsd:schema>";

   private static SchemaBinding SCHEMA;

   private static final String XML =
      "<e xmlns='http://www.jboss.org/test/xml/patterns' aTrueTF='true' aFalseTF='false' aTrue10='1' aFalse10='0'>" +
      "<eTrue10>1</eTrue10>" +
      "<eFalse10>0</eFalse10>" +
      "<eTrueTF>true</eTrueTF>" +
      "<eFalseTF>false</eFalseTF>" +
      "</e>";

   public BooleanPatternUnitTestCase(String name)
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
   
   public void testUnmarshalling() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), SCHEMA);
      assertNotNull(o);
      assertTrue(o instanceof E);
      E e = (E)o;
      assertTrue(e.aTrue10);
      assertFalse(e.aFalse10);
      assertTrue(e.aTrueTF);
      assertFalse(e.aFalseTF);
      assertTrue(e.eTrue10);
      assertFalse(e.eFalse10);
      assertTrue(e.eTrueTF);
      assertFalse(e.eFalseTF);
   }

   public void testMarshallingXerces() throws Exception
   {
      StringWriter writer = new StringWriter();
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), E.INSTANCE, writer);
      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML, xml);
   }

   public void testMarshallingSunday() throws Exception
   {
      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.marshal(SCHEMA, null, E.INSTANCE, writer);
      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML, xml);
   }

   // Inner

   public static final class E
   {
      public static final E INSTANCE;

      static
      {
         E e = new E();
         e.aTrue10 = true;
         e.aTrueTF = true;
         e.eTrue10 = true;
         e.eTrueTF = true;
         INSTANCE = e;
      }

      public boolean aTrue10;
      public boolean aFalse10;
      public boolean aTrueTF;
      public boolean aFalseTF;
      public boolean eTrue10;
      public boolean eFalse10;
      public boolean eTrueTF;
      public boolean eFalseTF;
   }
}
