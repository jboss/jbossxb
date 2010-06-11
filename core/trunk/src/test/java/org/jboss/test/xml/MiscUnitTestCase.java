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
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import junit.framework.Test;

import org.jboss.test.xml.sandbox.Choice1;
import org.jboss.test.xml.sandbox.Choice2;
import org.jboss.test.xml.sandbox.Choice3;
import org.jboss.test.xml.sandbox.Root;
import org.jboss.test.xml.immutable.Child1;
import org.jboss.test.xml.immutable.Child2;
import org.jboss.test.xml.immutable.Child3;
import org.jboss.test.xml.immutable.ImmutableChoice;
import org.jboss.test.xml.immutable.Parent;
import org.jboss.test.xml.person.Person;
import org.jboss.xb.binding.MappingObjectModelFactory;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;


/**
 * Various tests that should later be categorized.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45498 $</tt>
 */
public class MiscUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(MiscUnitTestCase.class);
   }
   
   //private static final Logger log = Logger.getLogger(MiscUnitTestCase.class);

   public MiscUnitTestCase(String localName)
   {
      super(localName);
   }

   public void testStringNormalization() throws Exception
   {
      String xsd = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/snorm'" +
         "   xmlns='http://www.jboss.org/test/xml/snorm'" +
         "   xmlns:jbxb='" + Constants.NS_JBXB + "'" +
         "   version='1.0'>" +
         "<xsd:complexType name='ctype'>" +
         "   <xsd:annotation>" +
         "      <xsd:appinfo>" +
         "         <jbxb:class impl='" + Person.class.getName() + "'/>" +
         "         <jbxb:characters>" +
         "            <jbxb:property name='lastName'/>" +
         "         </jbxb:characters>" +
         "      </xsd:appinfo>" +
         "   </xsd:annotation>" +
         "   <xsd:simpleContent>" +
         "      <xsd:extension base='xsd:string'>" +
         "         <xsd:attribute name='attr' type='xsd:string'>" +
         "            <xsd:annotation>" +
         "               <xsd:appinfo>" +
         "                  <jbxb:property name='firstName'/>" +
         "               </xsd:appinfo>" +
         "            </xsd:annotation>" +
         "         </xsd:attribute>" +
         "      </xsd:extension>" +
         "   </xsd:simpleContent>" +
         "</xsd:complexType>" +
         "<xsd:element name='person' type='ctype'/>" +
         "</xsd:schema>";

      String xml = "<person" +
         " xmlns='http://www.jboss.org/test/xml/snorm'" +
         " attr='&apos;&quot;&amp;&gt;&lt;'>&lt;&gt;&amp;&quot;&apos;</person>";

      String lastName = "<>&\"'";
      String firstName = "'\"&><";
      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof Person);
      Person person = (Person)unmarshalled;
      assertEquals(firstName, person.getFirstName());
      assertEquals(lastName, person.getLastName());

      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.marshal(schema, null, person, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(xml, marshalled);
   }

   public void testImmutableXerces() throws Exception
   {
      immutableTest(new XercesXsMarshaller());
   }

   public void testImmutableSunday() throws Exception
   {
      immutableTest(new MarshallerImpl());
   }

   public void testEmptyElements() throws Exception
   {
      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/empty'" +
         "   xmlns='http://www.jboss.org/test/xml/empty'" +
         "   xmlns:jbxb='" + Constants.NS_JBXB + "'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "<xsd:complexType name='emptyType'>" +
         "<xsd:annotation>" +
         "<xsd:appinfo>" +
         "<jbxb:class impl='" + java.util.Date.class.getName() + "'/>" +
         "</xsd:appinfo>" +
         "</xsd:annotation>" +
         "<xsd:sequence/>" +
         "</xsd:complexType>" +
         "<xsd:element name='empty' type='emptyType'/>" +
         "</xsd:schema>";

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<empty xmlns='http://www.jboss.org/test/xml/empty'/>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object unmarshalled = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof java.util.Date);
   }

   public void testBase64BinaryUnmarshalling() throws Exception
   {
      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/base64'" +
         "   xmlns='http://www.jboss.org/test/xml/collections'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "   <xsd:element name='base64' type='xsd:base64Binary' nillable='1'/>" +
         "</xsd:schema>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<base64 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
         "   xmlns='http://www.jboss.org/test/xml/base64'" +
         "   xsi:nil='1'/>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object result = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNull(result);

      xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<base64 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
         "   xmlns='http://www.jboss.org/test/xml/base64'/>";
      byte[] bytes = (byte[])unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNotNull(bytes);
      assertEquals(0, bytes.length);
   }

   public void testEmptyStringUnmarshalling() throws Exception
   {
      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/emptystring'" +
         "   xmlns='http://www.jboss.org/test/xml/emptystring'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "   <xsd:element name='str' type='xsd:string' nillable='1'/>" +
         "</xsd:schema>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<str xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
         "   xmlns='http://www.jboss.org/test/xml/emptystring'/>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      String result = (String)unmarshaller.unmarshal(new StringReader(xml), schema);
      assertEquals("", result);
   }

   public void testNullStringUnmarshalling() throws Exception
   {
      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/emptystring'" +
         "   xmlns='http://www.jboss.org/test/xml/emptystring'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "   <xsd:element name='str' type='xsd:string' nillable='1'/>" +
         "</xsd:schema>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<str xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
         "   xmlns='http://www.jboss.org/test/xml/emptystring'" +
         "   xsi:nil='1'/>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object result = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNull(result);
   }

   public void testDateTimeXerces() throws Exception
   {
      dateTimeTest(new XercesXsMarshaller());
   }

   public void testDateTimeSunday() throws Exception
   {
      dateTimeTest(new MarshallerImpl());
   }

   public void testJavaUtilDateXerces() throws Exception
   {
      javaUtilDateTest(new XercesXsMarshaller());
   }

   public void testJavaUtilDateSunday() throws Exception
   {
      javaUtilDateTest(new MarshallerImpl());
   }

   // Private

   private void javaUtilDateTest(AbstractMarshaller marshaller) throws Exception
   {
      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/datetime'" +
         "   xmlns='http://www.jboss.org/test/xml/datetime'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "   <xsd:element name='date' type='xsd:dateTime' nillable='1'/>" +
         "</xsd:schema>";

      java.util.Date date = java.util.Calendar.getInstance().getTime();

      marshaller.declareNamespace(null, "http://www.jboss.org/test/xml/datetime");
      MappingObjectModelProvider provider = new MappingObjectModelProvider();
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(xsd), provider, date, writer);

      String marshalled = writer.getBuffer().toString();

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Calendar cal = (Calendar)unmarshaller.unmarshal(new StringReader(marshalled), schema);

      assertEquals(date, cal.getTime());
   }

   private void dateTimeTest(AbstractMarshaller marshaller) throws Exception
   {
      String xsd = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
         "   targetNamespace='http://www.jboss.org/test/xml/datetime'" +
         "   xmlns='http://www.jboss.org/test/xml/datetime'" +
         "   elementFormDefault='qualified'" +
         "   attributeFormDefault='unqualified'" +
         "   version='1.0'>" +
         "   <xsd:element name='date' type='xsd:dateTime' nillable='1'/>" +
         "</xsd:schema>";

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null);

      String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
         "<date xmlns='http://www.jboss.org/test/xml/datetime'>" +
         "2005-06-24T12:24:43.555+01:00</date>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object result = unmarshaller.unmarshal(new StringReader(xml), schema);

      marshaller.declareNamespace(null, "http://www.jboss.org/test/xml/datetime");
      MappingObjectModelProvider provider = new MappingObjectModelProvider();
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(xsd), provider, result, writer);

      assertXmlEqual(xml, writer.getBuffer().toString());
   }

   private void immutableTest(AbstractMarshaller marshaller) throws Exception
   {
      final URL xsdUrl = getUrl("xml/immutable.xsd");

      Child1 child1 = new Child1("child1");
      List<Object> child2 = Arrays.asList(new Object[]{new Child2("child2_1"), new Child2("child2_2")});
      List<Object> others = Arrays.asList(new Object[]{new Child3("child3_1"), new Child3("child3_2"), new Child3("child3_3")});
      List<Object> choice = Arrays.asList(
         new Object[]{new ImmutableChoice("choice1"), new ImmutableChoice(new Child1("child1"))}
      );
      Parent parent = new Parent(child1, child2, others, choice);

      StringWriter writer = new StringWriter();
      marshaller.declareNamespace("imm", "http://www.jboss.org/test/xml/immutable/");
      //marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.marshal(xsdUrl.toExternalForm(),
         new MappingObjectModelProvider(),
         parent,
         writer
      );
      String xml = writer.getBuffer().toString();

      SchemaBinding schema = XsdBinder.bind(xsdUrl.openStream(), null);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertEquals(parent, o);
   }

   private void assertUnmarshalled(Root unmarshalled)
   {
      Root root = new Root();
      root.setChoiceCollection1(
         Arrays.asList(new Object[]{new Choice1("choice1_a", null), new Choice1(null, "choice1_b")})
      );
      root.setChoice2(Arrays.asList(new Object[]{new Choice2("choice2_c", "choice2_d", null),
         new Choice2(null, "choice2_d", "choice2_e")
      }
      )
      );
      root.setChoice3(Arrays.asList(new Object[]{
         new Choice3(Arrays.asList(new Integer[]{new Integer(1), new Integer(2), new Integer(3)}), null),
         new Choice3(null, Arrays.asList(new String[]{"1", "2", "3"}))
      }
      )
      );

      assertEquals(root, unmarshalled);
   }

   private static String getXmlUrl(String name)
   {
      return getUrl(name).getFile();
   }

   private static URL getUrl(String name)
   {
      URL xmlUrl = Thread.currentThread().getContextClassLoader().getResource(name);
      if(xmlUrl == null)
      {
         throw new IllegalStateException(name + " not found");
      }
      return xmlUrl;
   }
}
