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
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.ObjectModelProvider;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46059 $</tt>
 */
public class BasicArraysUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(BasicArraysUnitTestCase.class);
   }
   
   private static final String NS = "http://www.jboss.org/test/xml/longarr";

   private static final long[] PRIMITIVES = new long[]{-1, 0, 1};
   private static final String PRIMITIVES_XML =
      "<longArray xmlns='" +
      NS +
      "'>" +
      "  <item>-1</item>" +
      "  <item>0</item>" +
      "  <item>1</item>" +
      "</longArray>";

   private static final Long[] WRAPPERS = new Long[]{new Long(-1), new Long(0), new Long(1), null};
   private static final String WRAPPERS_XML =
      "<longArray xmlns='" +
      NS +
      "'>" +
      "  <item>-1</item>" +
      "  <item>0</item>" +
      "  <item>1</item>" +
      "  <item xmlns:xsi='" +
      Constants.NS_XML_SCHEMA_INSTANCE +
      "' xsi:nil='1'/>" +
      "</longArray>";

   private static final String WRAPPERS_NULL_ITEM_XML =
      "<longArray xmlns='" +
      NS +
      "'>" +
      "  <item xmlns:xsi='" +
      Constants.NS_XML_SCHEMA_INSTANCE +
      "' xsi:nil='1'/>" +
      "</longArray>";

   private static final String EMPTY_ARRAY_XML = "<longArray xmlns='" + NS + "'/>";

   public BasicArraysUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalEmptyPrimitiveArray() throws Exception
   {
      String xsd = getArrayOfLongXsd(false, false);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(EMPTY_ARRAY_XML), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof long[]);
      long[] arr = (long[])unmarshalled;
      assertEquals(0, arr.length);
   }

   public void testMarshalEmptyPrimitiveArrayXerces() throws Exception
   {
      marshallingTest(new XercesXsMarshaller(),
         getProvider(),
         getArrayOfLongXsd(false, false),
         new long[0],
         EMPTY_ARRAY_XML
      );
   }

   public void testMarshalEmptyPrimitiveArraySunday() throws Exception
   {
      marshallingTest(new MarshallerImpl(), null, getArrayOfLongXsd(false, false), new long[0], EMPTY_ARRAY_XML);
   }

   public void testUnmarshalEmptyArrayOfWrappers() throws Exception
   {
      String xsd = getArrayOfLongXsd(true, false);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(EMPTY_ARRAY_XML), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof Long[]);
      Long[] arr = (Long[])unmarshalled;
      assertEquals(0, arr.length);
   }

   public void testMarshalEmptyArrayOfWrappersXerces() throws Exception
   {
      marshallingTest(new XercesXsMarshaller(),
         getProvider(),
         getArrayOfLongXsd(true, false),
         new Long[0],
         EMPTY_ARRAY_XML
      );
   }

   public void testMarshalEmptyArrayOfWrappersSunday() throws Exception
   {
      marshallingTest(new MarshallerImpl(), null, getArrayOfLongXsd(true, false), new Long[0], EMPTY_ARRAY_XML);
   }

   public void testUnmarshalEmptyAnnotatedPrimitiveArray() throws Exception
   {
      String xsd = getArrayOfLongXsd(false, true);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(EMPTY_ARRAY_XML), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof ArrayWrapper);
      long[] arr = ((ArrayWrapper)unmarshalled).primitives;
      assertNotNull(arr);
      assertEquals(0, arr.length);
   }

   public void testMarshalEmptyAnnotatedPrimitiveArrayXerces() throws Exception
   {
      MappingObjectModelProvider provider = getProvider();
      provider.mapClassToElement(ArrayWrapper.class, NS, "longArray", null);
      provider.mapFieldToElement(ArrayWrapper.class, "primitives", NS, "item", null);
      marshallingTest(new XercesXsMarshaller(),
         provider,
         getArrayOfLongXsd(false, true),
         new ArrayWrapper(new long[0]),
         EMPTY_ARRAY_XML
      );
   }

   public void testMarshalEmptyAnnotatedPrimitiveArraySunday() throws Exception
   {
      marshallingTest(new MarshallerImpl(), null, getArrayOfLongXsd(false, true), new ArrayWrapper(new long[0]), EMPTY_ARRAY_XML);
   }

   public void testUnmarshalEmptyAnnotatedArrayOfWrappers() throws Exception
   {
      String xsd = getArrayOfLongXsd(true, true);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(EMPTY_ARRAY_XML), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof ArrayWrapper);
      Long[] arr = ((ArrayWrapper)unmarshalled).wrappers;
      assertNotNull(arr);
      assertEquals(0, arr.length);
   }

   public void testMarshalEmptyAnnotatedArrayOfWrappersXerces() throws Exception
   {
      MappingObjectModelProvider provider = getProvider();
      provider.mapClassToElement(ArrayWrapper.class, NS, "longArray", null);
      provider.mapFieldToElement(ArrayWrapper.class, "wrappers", NS, "item", null);
      marshallingTest(new XercesXsMarshaller(),
         provider,
         getArrayOfLongXsd(true, true),
         new ArrayWrapper(new Long[0]),
         EMPTY_ARRAY_XML
      );
   }

   public void testMarshalEmptyAnnotatedArrayOfWrappersSunday() throws Exception
   {
      marshallingTest(new MarshallerImpl(), null, getArrayOfLongXsd(true, true), new ArrayWrapper(new Long[0]), EMPTY_ARRAY_XML);
   }
   
   public void testUnmarshalPrimitiveArray() throws Exception
   {
      String xsd = getArrayOfLongXsd(false, false);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(PRIMITIVES_XML), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof long[]);
      long[] arr = (long[])unmarshalled;
      assertPrimitiveArray(arr);
   }

   public void testMarshalPrimitiveArrayXerces() throws Exception
   {
      marshallingTest(new XercesXsMarshaller(),
         getProvider(),
         getArrayOfLongXsd(false, false),
         PRIMITIVES,
         PRIMITIVES_XML
      );
   }

   public void testMarshalPrimitiveArraySunday() throws Exception
   {
      marshallingTest(new MarshallerImpl(), null, getArrayOfLongXsd(false, false), PRIMITIVES, PRIMITIVES_XML);
   }

   public void testUnmarshalAnnotatedPrimtiveArray() throws Exception
   {
      String xsd = getArrayOfLongXsd(false, true);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(PRIMITIVES_XML), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof ArrayWrapper);
      long[] arr = ((ArrayWrapper)unmarshalled).primitives;
      assertPrimitiveArray(arr);
   }

   public void testMarshalAnnotatedPrimtiveArrayXerces() throws Exception
   {
      MappingObjectModelProvider provider = getProvider();
      provider.mapClassToElement(ArrayWrapper.class, NS, "longArray", null);
      provider.mapFieldToElement(ArrayWrapper.class, "primitives", NS, "item", null);
      marshallingTest(new XercesXsMarshaller(),
         provider,
         getArrayOfLongXsd(false, true),
         new ArrayWrapper(PRIMITIVES),
         PRIMITIVES_XML
      );
   }

   public void testMarshalAnnotatedPrimtiveArraySunday() throws Exception
   {
      marshallingTest(new MarshallerImpl(),
         null,
         getArrayOfLongXsd(false, true),
         new ArrayWrapper(PRIMITIVES),
         PRIMITIVES_XML
      );
   }

   public void testArrayOfWrappersXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      arrayOfWrappersTest(marshaller);
   }

   public void testArrayOfWrappersSunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      arrayOfWrappersTest(marshaller);
   }

   public void testQNameArrayWithPrefixesXerces() throws Exception
   {
      qNameArrayWithPrefixesTest(new XercesXsMarshaller());
   }

   public void testQNameArrayWithPrefixesSunday() throws Exception
   {
      qNameArrayWithPrefixesTest(new MarshallerImpl());
   }

   public void testQNameArrayWithoutPrefixesXerces() throws Exception
   {
      qNameArrayWithoutPrefixesTest(new XercesXsMarshaller());
   }

   public void testQNameArrayWithoutPrefixesSunday() throws Exception
   {
      qNameArrayWithoutPrefixesTest(new MarshallerImpl());
   }
   
   // Private

   private MappingObjectModelProvider getProvider()
   {
      MappingObjectModelProvider provider = new MappingObjectModelProvider();
      provider.setIgnoreNotFoundField(false);
      return provider;
   }

   private void marshallingTest(AbstractMarshaller marshaller,
                                ObjectModelProvider provider,
                                String xsd,
                                Object o,
                                String expectedXml)
      throws Exception
   {
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(xsd), provider, o, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(expectedXml, marshalled);
   }

   private void assertPrimitiveArray(long[] arr)
   {
      assertEquals(3, arr.length);
      assertEquals(-1, arr[0]);
      assertEquals(0, arr[1]);
      assertEquals(1, arr[2]);
   }

   private void arrayOfWrappersTest(AbstractMarshaller marshaller) throws Exception
   {
      String xsd = getArrayOfLongXsd(true, false);
      arrayOfWrappersTest(xsd, WRAPPERS_XML, WRAPPERS, marshaller);
      arrayOfWrappersTest(xsd, WRAPPERS_NULL_ITEM_XML, new Long[]{null}, marshaller);
   }

   private void arrayOfWrappersTest(String xsd, String xml, Object[] expected, AbstractMarshaller marshaller)
      throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof Long[]);
      Long[] arr = (Long[])unmarshalled;
      if(expected != null)
      {
         assertNotNull(arr);
         assertEquals(expected.length, arr.length);
         for(int i = 0; i < expected.length; ++i)
         {
            if(expected[i] != null)
            {
               assertEquals(expected[i], arr[i]);
            }
            else
            {
               assertNull(arr[i]);
            }
         }
      }
      else
      {
         assertNull(arr);
      }

      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(xsd), getProvider(), unmarshalled, writer);

      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(xml, marshalled);
   }

   private String getArrayOfLongXsd(boolean nillableItems, boolean annotations)
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'")
         .append("  targetNamespace='").append(NS).append("'")
         .append("  xmlns='").append(NS).append("'")
         .append("  xmlns:jbxb='" + Constants.NS_JBXB + "'")
         .append("  elementFormDefault='qualified'")
         .append("  attributeFormDefault='unqualified'")
         .append("  version='1.0'>")
         .append("<xsd:complexType name='LongArray'>");

      if(annotations)
      {
         buf.append("<xsd:annotation>")
            .append("  <xsd:appinfo>")
            .append("    <jbxb:class impl='").append(ArrayWrapper.class.getName()).append("'/>")
            .append("  </xsd:appinfo>")
            .append("</xsd:annotation>");
      }

      buf.append("  <xsd:sequence>")
         .append("    <xsd:element name='item' type='xsd:long' minOccurs='0' maxOccurs='unbounded' nillable='")
         .append(nillableItems)
         .append("    '>");

      if(annotations)
      {
         buf.append("<xsd:annotation>")
            .append("  <xsd:appinfo>")
            .append("    <jbxb:property name='")
            .append(nillableItems ? "wrappers" : "primitives")
            .append("'/>")
            .append("  </xsd:appinfo>")
            .append("</xsd:annotation>");
      }

      buf.append("    </xsd:element>")
         .append("  </xsd:sequence>")
         .append("</xsd:complexType>")
         .append("<xsd:element name='longArray' type='LongArray'/>")
         .append("</xsd:schema>");
      return buf.toString();
   }

   private void qNameArrayWithoutPrefixesTest(AbstractMarshaller marshaller) throws Exception
   {
      QName[] arr = new QName[]{
         new QName("http://some-ns1", "lp1"),
         new QName("http://some-ns2", "lp2"),
         new QName("http://some-ns3", "lp3")
      };

      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/qname'" +
         "   xmlns='http://www.jboss.org/test/xml/qname'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "<xsd:element name='qnameArray'>" +
         "<xsd:complexType>" +
         "<xsd:sequence>" +
         "<xsd:element name='item' type='xsd:QName' minOccurs='0' maxOccurs='unbounded'/>" +
         "</xsd:sequence>" +
         "</xsd:complexType>" +
         "</xsd:element>" +
         "</xsd:schema>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<qnameArray xmlns='http://www.jboss.org/test/xml/qname'>";
      for(int i = 0; i < arr.length; ++i)
      {
         String prefix = arr[i].getLocalPart() + "_ns";
         xml += "<item xmlns:" + prefix + "='" +
            arr[i].getNamespaceURI() +
            "'>" + prefix + ":" +
            arr[i].getLocalPart() +
            "</item>";
      }
      xml += "</qnameArray>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      QName[] unmarshalled = (QName[])unmarshaller.unmarshal(new StringReader(xml), schema);

      assertNotNull(unmarshalled);
      assertEquals(arr.length, unmarshalled.length);
      for(int i = 0; i < arr.length; ++i)
      {
         assertEquals(arr[i], unmarshalled[i]);
      }

      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.declareNamespace("", "http://www.jboss.org/test/xml/qname");
      MappingObjectModelProvider provider = getProvider();
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(xsd), provider, arr, writer);

      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(xml, marshalled);
   }

   private void qNameArrayWithPrefixesTest(AbstractMarshaller marshaller) throws Exception
   {
      QName[] arr = new QName[]{
         new QName("http://some-ns1", "lp1", "ns1"),
         new QName("http://some-ns2", "lp2", "ns2"),
         new QName("http://some-ns3", "lp3", "ns3")
      };

      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/qname'" +
         "   xmlns='http://www.jboss.org/test/xml/qname'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "<xsd:element name='qnameArray'>" +
         "<xsd:complexType>" +
         "<xsd:sequence>" +
         "<xsd:element name='item' type='xsd:QName' minOccurs='0' maxOccurs='unbounded'/>" +
         "</xsd:sequence>" +
         "</xsd:complexType>" +
         "</xsd:element>" +
         "</xsd:schema>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<qnameArray xmlns='http://www.jboss.org/test/xml/qname'>";
      for(int i = 0; i < arr.length; ++i)
      {
         xml += "<item xmlns:" +
            arr[i].getPrefix() +
            "='" +
            arr[i].getNamespaceURI() +
            "'>" +
            arr[i].getPrefix() +
            ":" +
            arr[i].getLocalPart() +
            "</item>";
      }
      xml += "</qnameArray>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      QName[] unmarshalled = (QName[])unmarshaller.unmarshal(new StringReader(xml), schema);

      assertNotNull(unmarshalled);
      assertEquals(arr.length, unmarshalled.length);
      for(int i = 0; i < arr.length; ++i)
      {
         assertEquals(arr[i], unmarshalled[i]);
      }

      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.declareNamespace(null, "http://www.jboss.org/test/xml/qname");
      MappingObjectModelProvider provider = getProvider();
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(xsd), provider, unmarshalled, writer);

      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(xml, marshalled);
   }

   // Inner

   public static class ArrayWrapper
   {
      public long[] primitives;
      public Long[] wrappers;

      public ArrayWrapper()
      {
      }

      public ArrayWrapper(long[] primitives)
      {
         this.primitives = primitives;
      }

      public ArrayWrapper(Long[] wrappers)
      {
         this.wrappers = wrappers;
      }
   }
}
