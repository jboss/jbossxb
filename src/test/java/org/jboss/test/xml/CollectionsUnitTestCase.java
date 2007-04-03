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

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import org.jboss.test.xml.collections.Collections;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 56874 $</tt>
 */
public class CollectionsUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String LIST_TYPE_XSD = "<xsd:schema xmlns:xsd='" +
      Constants.NS_XML_SCHEMA +
      "'" +
      "  targetNamespace='http://jboss.org/test/xml/list'" +
      "  xmlns='http://jboss.org/test/xml/list'" +
      "  xmlns:jbxb='" +
      Constants.NS_JBXB +
      "'>" +
      "  <xsd:element name='e'>" +
      "    <xsd:annotation>" +
      "      <xsd:appinfo>" +
      "        <jbxb:class impl='" +
      ListWrapper.class.getName() +
      "'/>" +
      "      </xsd:appinfo>" +
      "    </xsd:annotation>" +
      "    <xsd:complexType>" +
      "      <xsd:annotation>" +
      "        <xsd:appinfo>" +
      "          <jbxb:characters>" +
      "            <jbxb:property name='field2'/>" +
      "          </jbxb:characters>" +
      "        </xsd:appinfo>" +
      "      </xsd:annotation>" +
      "      <xsd:simpleContent>" +
      "        <xsd:extension base='booleanList'>" +
      "          <xsd:attribute name='field1'>" +
      "            <xsd:simpleType>" +
      "              <xsd:list itemType='xsd:string'/>" +
      "            </xsd:simpleType>" +
      "          </xsd:attribute>" +
      "          <xsd:attribute name='field3'>" +
      "            <xsd:simpleType>" +
      "              <xsd:list itemType='xsd:QName'/>" +
      "            </xsd:simpleType>" +
      "          </xsd:attribute>" +
      "        </xsd:extension>" +
      "      </xsd:simpleContent>" +
      "    </xsd:complexType>" +
      "  </xsd:element>" +
      "  <xsd:simpleType name='booleanList'>" +
      "    <xsd:list itemType='xsd:boolean'/>" +
      "  </xsd:simpleType>" +
      "</xsd:schema>";

   private static final String LIST_TYPE_XML = "<e xmlns='http://jboss.org/test/xml/list'" +
      "  field1='str1 str2 str3'>true false true</e>";

   private static final String LIST_TYPE_QNAME_ARR_XML = "<e xmlns='http://jboss.org/test/xml/list'" +
      " xmlns:ns1='http://ns1' xmlns:ns2='http://ns2' xmlns:ns3='http://ns3'" +
      " field3='ns1:name1 ns2:name2 ns3:name3'/>";

   private static final String MULTIDIM_ARR_XML = "<arr xmlns='http://www.jboss.org/test/xml/arr' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" +
      "  <item>" +
      "    <item>s1</item>" +
      "  </item>" +
      "  <item>" +
      "    <item>s2</item>" +
      "    <item>s3</item>" +
      "  </item>" +
      "  <item>" +
      "    <item xsi:nil='1'/>" +
      "  </item>" +
      "  <item xsi:nil='1'/>" +
      "</arr>";

   private static final String[][] MULTIDIM_ARR = new String[][]
   {
      new String[]{"s1"},
      new String[]{"s2", "s3"},
      new String[]{null},
      null
   };

   public CollectionsUnitTestCase(String name)
   {
      super(name);
   }

/*
   protected void configureLogging()
   {
      enableTrace("org.jboss.xb.binding");
   }
*/

   public void testUnmarshalling() throws Exception
   {
      String xml = getFullPath("xml/collections.xml");
      Reader xmlReader = new FileReader(xml);
      unmarshalCollections(xmlReader);
   }

   public void testMarshalling() throws Exception
   {
      String ns = "http://www.jboss.org/test/xml/collections";

      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.addRootElement(new QName(ns, "collections"));
      marshaller.setRootTypeQName(new QName(ns, "collections"));
      marshaller.declareNamespace(null, ns);

      String xsd = getFullPath("xml/collections.xsd");
      StringWriter xml = new StringWriter();
      marshaller.marshal(xsd, new MappingObjectModelProvider(), Collections.getInstance(), xml);

      String marshalled = xml.getBuffer().toString();
      try
      {
         unmarshalCollections(new StringReader(marshalled));
      }
      catch(Throwable t)
      {
         fail("Failed to unmarshal:\n" + marshalled + "\n" + t.getMessage());
      }
   }

   public void testMultidimArrMarshallingXerces() throws Exception
   {
      StringWriter writer = new StringWriter();
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.marshal(new StringReader(getMultiDimArrayXsd(false)),
         new MappingObjectModelProvider(),
         MULTIDIM_ARR,
         writer
      );
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(MULTIDIM_ARR_XML, marshalled);
   }

   public void testMultidimArrMarshallingSunday() throws Exception
   {
      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.marshal(new StringReader(getMultiDimArrayXsd(false)), null, MULTIDIM_ARR, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(MULTIDIM_ARR_XML, marshalled);
   }

   public void testMultidimArrayUnmarshalling() throws Exception
   {
      Object o = unmarshalMDArray(false);
      assertTrue(o instanceof String[][]);
      String[][] arr = (String[][])o;
      assertMultidimArray(arr);
   }

   public void testAnnotatedMultiDimArray() throws Exception
   {
      Object o = unmarshalMDArray(true);
      assertTrue(o instanceof ArrayWrapper);
      String[][] arr = ((ArrayWrapper)o).arr;
      assertMultidimArray(arr);
   }

   public void testUnmarshalListType() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(LIST_TYPE_XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(LIST_TYPE_XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof ListWrapper);
      ListWrapper lists = (ListWrapper)o;
      assertEquals(Arrays.asList(new String[]{"str1", "str2", "str3"}), lists.field1);
      assertEquals(Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.TRUE}), lists.field2);
   }

   public void testMarshallingListTypeXerces() throws Exception
   {
      ListWrapper lists = new ListWrapper();
      lists.field1 = Arrays.asList(new String[]{"str1", "str2", "str3"});
      lists.field2 = Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.TRUE});

      StringWriter writer = new StringWriter();
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      MappingObjectModelProvider provider = new MappingObjectModelProvider();
      provider.mapFieldToElement(ListWrapper.class, "field2", "http://jboss.org/test/xml/list", "e", null);
      marshaller.marshal(new StringReader(LIST_TYPE_XSD), provider, lists, writer);

      String xml = writer.getBuffer().toString();
      assertXmlEqual(LIST_TYPE_XML, xml);
   }

   public void testMarshallingListTypeSunday() throws Exception
   {
      ListWrapper lists = new ListWrapper();
      lists.field1 = Arrays.asList(new String[]{"str1", "str2", "str3"});
      lists.field2 = Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.TRUE});

      SchemaBinding schema = XsdBinder.bind(new StringReader(LIST_TYPE_XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);

      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.marshal(schema, null, lists, writer);

      String xml = writer.getBuffer().toString();
      assertXmlEqual(LIST_TYPE_XML, xml);
   }

   public void testUnmarshalListTypeToArrayField() throws Exception
   {
      String xml = LIST_TYPE_QNAME_ARR_XML;
      unmarshalQNameArray(xml);

   }

   public void testMarshallingListTypeArrayToListXerces() throws Exception
   {
      ListWrapper lists = new ListWrapper();
      lists.field3 =
         new QName[]{new QName("http://ns1", "name1"),
                     new QName("http://ns2", "name2"),
                     new QName("http://ns3", "name3")
         };

      StringWriter writer = new StringWriter();
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      MappingObjectModelProvider provider = new MappingObjectModelProvider();
      marshaller.marshal(new StringReader(LIST_TYPE_XSD), provider, lists, writer);

      String xml = writer.getBuffer().toString();
      unmarshalQNameArray(xml);
   }

   public void testMarshallingListTypeArrayToListSunday() throws Exception
   {
      ListWrapper lists = new ListWrapper();
      lists.field3 =
         new QName[]{new QName("http://ns1", "name1"),
                     new QName("http://ns2", "name2"),
                     new QName("http://ns3", "name3")
         };

      SchemaBinding schema = XsdBinder.bind(new StringReader(LIST_TYPE_XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);

      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.marshal(schema, null, lists, writer);

      String xml = writer.getBuffer().toString();
      unmarshalQNameArray(xml);
   }

   // Private

   private void unmarshalQNameArray(String xml)
      throws JBossXBException
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(LIST_TYPE_XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNotNull(o);
      assertTrue(o instanceof ListWrapper);
      ListWrapper lists = (ListWrapper)o;
      assertNotNull(lists.field3);
      assertEquals(3, lists.field3.length);
      assertEquals(new QName("http://ns1", "name1"), lists.field3[0]);
      assertEquals(new QName("http://ns2", "name2"), lists.field3[1]);
      assertEquals(new QName("http://ns3", "name3"), lists.field3[2]);
   }

   private Object unmarshalMDArray(boolean annotations)
      throws JBossXBException
   {
      String xsd = getMultiDimArrayXsd(annotations);
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      return unmarshaller.unmarshal(new StringReader(MULTIDIM_ARR_XML), schema);
   }

   private String getMultiDimArrayXsd(boolean annotations)
   {
      StringBuffer xsdBuf = new StringBuffer()
         .append("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n")
         .append("  targetNamespace='http://www.jboss.org/test/xml/arr'\n")
         .append("  xmlns='http://www.jboss.org/test/xml/arr'\n")
         .append("  xmlns:jbxb='http://www.jboss.org/xml/ns/jbxb'\n")
         .append("  elementFormDefault='qualified'\n")
         .append("  version='1.0'>\n")
         .append("  <xsd:complexType name='strArr'>\n")
         .append("    <xsd:sequence>\n")
         .append(
            "      <xsd:element name='item' type='xsd:string' minOccurs='0' maxOccurs='unbounded' nillable='1'/>\n"
         )
         .append("    </xsd:sequence>\n")
         .append("  </xsd:complexType>\n")
         .append("  <xsd:complexType name='mdStrArr'>\n");

      if(annotations)
      {
         xsdBuf
            .append("    <xsd:annotation>\n")
            .append("      <xsd:appinfo>\n")
            .append("        <jbxb:class impl='" + ArrayWrapper.class.getName() + "'/>\n")
            .append("      </xsd:appinfo>\n")
            .append("    </xsd:annotation>\n");
      }

      xsdBuf
         .append("    <xsd:sequence>\n")
         .append("      <xsd:element name='item' type='strArr' minOccurs='0' maxOccurs='unbounded' nillable='1'>\n");

      if(annotations)
      {
         xsdBuf
            .append("        <xsd:annotation>\n")
            .append("          <xsd:appinfo>\n")
            .append("            <jbxb:property name='arr'/>\n")
            .append("          </xsd:appinfo>\n")
            .append("        </xsd:annotation>\n");
      }

      xsdBuf
         .append("      </xsd:element>\n")
         .append("    </xsd:sequence>\n")
         .append("  </xsd:complexType>\n")
         .append("  <xsd:element name='arr' type='mdStrArr'/>\n")
         .append("</xsd:schema>");

      String xsd = xsdBuf.toString();
      return xsd;
   }

   private void assertMultidimArray(String[][] arr)
   {
      assertNotNull(arr);
      assertEquals(4, arr.length);
      assertTrue(arr[0] instanceof String[]);
      assertEquals(1, arr[0].length);
      assertEquals("s1", arr[0][0]);
      assertEquals(2, arr[1].length);
      assertEquals("s2", arr[1][0]);
      assertEquals("s3", arr[1][1]);
      assertTrue(arr[2] instanceof String[]);
      assertEquals(1, arr[2].length);
      assertNull(arr[2][0]);
      assertNull(arr[3]);
   }

   private void unmarshalCollections(Reader xmlReader) throws JBossXBException
   {
      String xsd = getFullPath("xml/collections.xsd");
      SchemaBinding schema = XsdBinder.bind(xsd);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      String ns = "http://www.jboss.org/test/xml/collections";
      QName rootQName = new QName(ns, "collections");
      TypeBinding type = schema.getType(rootQName);
      assertNotNull(type);
      schema.addElement(rootQName, type);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Collections collections;
      collections = (Collections)unmarshaller.unmarshal(xmlReader, schema);
      assertEquals(Collections.getInstance(), collections);
   }

   private String getFullPath(String name)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(name);
      if(url == null)
      {
         fail("Resource not found: " + name);
      }
      return url.getFile();
   }

   // Inner

   public static final class ArrayWrapper
   {
      public String[][] arr;
   }

   public static final class ListWrapper
   {
      public List field1;
      public List field2;
      public QName[] field3;
   }
}
