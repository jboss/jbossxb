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
import org.jboss.test.BaseTestCase;
import org.jboss.test.xml.jbxb.characters.Binding;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;


/**
 * Test unmarshalling xml documents conforming to xml/jbxb/characters/schema1.xsd
 * into org.jboss.test.xml.jbxb.characters.Binding.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision: 45337 $
 */
public class JbxbCharactersUnitTestCase
   extends BaseTestCase
{
   private static final String XML_VALUE4 =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<ns_binding:binding xmlns:ns_binding=\"urn:jboss:jbxb-characters-schema1\" name=\"root\">" +
      "<ns_binding:value4>val4</ns_binding:value4>" +
      "</ns_binding:binding>";

   public JbxbCharactersUnitTestCase(String name)
   {
      super(name);
   }

/*
   public void configureLogging()
   {
      enableTrace("org.jboss.xb");
   }
*/

   public void testMainUnmarshalling() throws Exception
   {
      String res = getPath("xml/jbxb/characters/testSchema1.xml").getFile();
      FileReader xmlReader = new FileReader(res);

      Binding binding = unmarshal(xmlReader);

      assertEquals("Binding.name = root ", "root", binding.getName());
      assertEquals("Binding.text = value1 ", "value1", binding.getText());
   }

   public void testValue2Unmarshalling() throws Exception
   {
      StringReader xmlReader = new StringReader("<?xml version='1.0' encoding='UTF-8'?>" +
         "<binding xmlns='urn:jboss:jbxb-characters-schema1'" +
         "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
         "  xsi:schemaLocation='urn:jboss:jbxb-characters-schema1 schema1.xsd'" +
         "   name='root'>" +
         "   <value2>value2</value2>" +
         "</binding>"
      );

      Binding binding = unmarshal(xmlReader);

      assertEquals("Binding.name = root ", "root", binding.getName());
      Binding.Value2 value2 = binding.getValue2();
      assertNotNull(value2);
      assertEquals("Binding.text2 = value2 ", "value2", value2.text);
   }

   public void testValue3Unmarshalling() throws Exception
   {
      StringReader xmlReader = new StringReader("<?xml version='1.0' encoding='UTF-8'?>" +
         "<binding xmlns='urn:jboss:jbxb-characters-schema1'" +
         "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
         "  xsi:schemaLocation='urn:jboss:jbxb-characters-schema1 schema1.xsd'" +
         "   name='root'>" +
         "   <value3 attr='attr'>value3</value3>" +
         "</binding>"
      );

      Binding binding = unmarshal(xmlReader);

      assertEquals("Binding.name = root ", "root", binding.getName());
      Binding.Value3 value3 = binding.getValue3();
      assertNotNull(value3);
      assertEquals("value3", value3.chars);
      assertEquals("attr", value3.attr);
   }

   public void testValue4Unmarshalling() throws Exception
   {
      Binding binding = unmarshal(new StringReader(XML_VALUE4));
      assertEquals("Binding.name = root ", "root", binding.getName());
      Binding.Value4 value4 = binding.value4;
      assertNotNull(value4);
      assertEquals("val4", value4.value);

   }

   public void testValue4MarshallingXerces() throws Exception
   {
      Binding binding = new Binding();
      binding.setName("root");
      binding.value4 = new Binding.Value4("val4");
      StringWriter writer = new StringWriter();
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      String file = getPath("xml/jbxb/characters/schema1.xsd").getFile();
      marshaller.marshal(new FileReader(file), new MappingObjectModelProvider(), binding, writer);
      String xml = writer.getBuffer().toString();
      assertEquals(XML_VALUE4, xml);
   }

   public void testValue4MarshallingSunday() throws Exception
   {
      Binding binding = new Binding();
      binding.setName("root");
      binding.value4 = new Binding.Value4("val4");
      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      String file = getPath("xml/jbxb/characters/schema1.xsd").getFile();
      marshaller.marshal(new FileReader(file), new MappingObjectModelProvider(), binding, writer);
      String xml = writer.getBuffer().toString();
      assertEquals(XML_VALUE4, xml);
   }

   // Private

   private Binding unmarshal(Reader xmlReader)
      throws JBossXBException
   {
      String url = getPath("xml/jbxb/characters/schema1.xsd").toExternalForm();
      SchemaBinding schemaBinding = XsdBinder.bind(url);
      schemaBinding.setIgnoreUnresolvedFieldOrClass(true);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Binding binding = (Binding)unmarshaller.unmarshal(xmlReader, schemaBinding);
      return binding;
   }

   private URL getPath(String path)
   {
      java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if(url == null)
      {
         fail("URL not found: " + path);
      }
      return url;
   }
}
