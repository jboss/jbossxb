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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class RequiredAttributesUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String XSD =
      "<xsd:schema" +
      " xmlns:jbxb='http://www.jboss.org/xml/ns/jbxb'" +
      " xmlns:xsd='http://www.w3.org/2001/XMLSchema'>" +
      " <xsd:element name='root' type='root-type'/>" +
      " <xsd:complexType name='root-type'>" +
      "   <xsd:annotation>" +
      "     <xsd:appinfo>" +
      "       <jbxb:class impl='" + E.class.getName() + "'/>" +
      "     </xsd:appinfo>" +
      "   </xsd:annotation>" +
      "   <xsd:attribute name='unqualified' type='xsd:string' use='required'/>" +
      " </xsd:complexType>" +
      "</xsd:schema>";

   private static SchemaBinding SCHEMA;

   public RequiredAttributesUnitTestCase(String name)
   {
      super(name);
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null);
         SCHEMA.setIgnoreUnresolvedFieldOrClass(false);
      }
   }
   
   public void testBinding() throws Exception
   {
      assertNotNull(SCHEMA);
      ElementBinding element = SCHEMA.getElement(new QName("root"));
      assertNotNull(element);
      AttributeBinding attribute = element.getType().getAttribute(new QName("unqualified"));
      assertNotNull(attribute);
      assertTrue(attribute.getRequired());
   }
   
   public void testUnmarshallingInvalidXml() throws Exception
   {
      String xml = "<root xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='testns'/>";

      org.xml.sax.EntityResolver resolver = new org.xml.sax.EntityResolver()
      {
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
         {
            if(systemId != null && systemId.endsWith("testns"))
               return new org.xml.sax.InputSource(new StringReader(XSD));
            return null;
         }
      };

/*
      Validator.assertValidXml(xml, new org.xml.sax.EntityResolver()
      {
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
         {
            if(systemId != null && systemId.endsWith("testns"))
               return new org.xml.sax.InputSource(new StringReader(xsd));
            return null;
         }
      });
*/      

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      unmarshaller.setEntityResolver(resolver);
      unmarshaller.setSchemaValidation(true);

      try
      {
         unmarshaller.unmarshal(new StringReader(xml), SCHEMA);
         fail("required attribute is missing");
      }
      catch(JBossXBException e)
      {
         // expected
      }
   }

   public void testMarshallingInvalidObject() throws Exception
   {      
      E root = new E();
      StringWriter writer = new StringWriter();
      
      MarshallerImpl marshaller = new MarshallerImpl();
      
      try
      {
         marshaller.marshal(SCHEMA, null, root, writer);
         fail("required attribute is missing");
      }
      catch(JBossXBRuntimeException e)
      {
         // expected
      }
   }

   public void testUnmarshallingValidXml() throws Exception
   {
      String xml = "<root xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='testns' unqualified='1'/>";

      org.xml.sax.EntityResolver resolver = new org.xml.sax.EntityResolver()
      {
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
         {
            if(systemId != null && systemId.endsWith("testns"))
               return new org.xml.sax.InputSource(new StringReader(XSD));
            return null;
         }
      };

/*
      Validator.assertValidXml(xml, new org.xml.sax.EntityResolver()
      {
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
         {
            if(systemId != null && systemId.endsWith("testns"))
               return new org.xml.sax.InputSource(new StringReader(xsd));
            return null;
         }
      });
*/      

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      unmarshaller.setEntityResolver(resolver);
      unmarshaller.setSchemaValidation(true);

      E e = (E) unmarshaller.unmarshal(new StringReader(xml), SCHEMA);
      assertNotNull(e);
      assertEquals("1", e.unqualified);
   }

   public void testMarshallingValidObject() throws Exception
   {      
      E root = new E();
      root.unqualified = "1";
      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.marshal(SCHEMA, null, root, writer);
      
      String xml = "<root xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='testns' unqualified='1'/>";
      assertXmlEqual(xml, writer.getBuffer().toString());
   }

   // Inner

   public static final class E
   {
      public String unqualified;
   }
}
