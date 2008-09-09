/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors as indicated
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.xml.sax.Attributes;


/**
 * IgnorableWhitespaceUnitTestCase.
 * 
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision$
 */
public class IgnorableWhitespaceUnitTestCase extends AbstractJBossXBTest
{
   private static final String NS = "http://www.jboss.org/test/xml/simpleContent";
 
   private static final String XSD =
      "<?xml version='1.0' encoding='UTF-8'?>" +
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      " targetNamespace='http://www.jboss.org/test/xml/simpleContent'" +
      " xmlns='http://www.jboss.org/test/xml/simpleContent'" +
      " elementFormDefault='qualified'" +
      " attributeFormDefault='unqualified'" +
      " version='1.0'>" +
      " <xsd:element name='top'>" +
      "  <xsd:complexType>" +
      "   <xsd:sequence>" +
      "    <xsd:element name='string' type='myString' minOccurs='0' maxOccurs='unbounded'/>" +
      "   </xsd:sequence>" +
      "  </xsd:complexType>" +
      " </xsd:element>" +
      " <xsd:complexType name='myString'>" +
      "  <xsd:simpleContent>" +
      "   <xsd:extension base='xsd:string'>" +
      "    <xsd:attribute name='id' type='xsd:ID'/>" +
      "   </xsd:extension>" +
      "  </xsd:simpleContent>" +
      " </xsd:complexType>" +
      "</xsd:schema>";

   public static final TestSuite suite()
   {
      return new TestSuite(IgnorableWhitespaceUnitTestCase.class);
   }
   
   public IgnorableWhitespaceUnitTestCase(String name)
   {
      super(name);
   }

   public void testWhitespaceUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);

      schema.setIgnoreUnresolvedFieldOrClass(false);
      schema.setIgnoreWhitespacesInMixedContent(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);
      
      Top top = (Top) unmarshal("IgnorableWhitespaceContent.xml", schema, Top.class);
      assertNotNull(top.string);
      assertEquals(2, top.string.size());
      assertEquals(" ", top.string.get(0));
      assertEquals("\n      newline, 6 spaces, newline, 3 spaces\n   ", top.string.get(1));
   }

   public void testWhitespaceMarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);

      schema.setIgnoreUnresolvedFieldOrClass(false);
      schema.setIgnoreWhitespacesInMixedContent(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);
      
      Top top = new Top();
      top.string = new ArrayList();
      top.string.add(" ");
      top.string.add("\n      newline, 6 spaces, newline, 3 spaces\n   ");
      MarshallerImpl marshaller = new MarshallerImpl();
      StringWriter writer = new StringWriter();
      marshaller.marshal(schema, null, top, writer);
      
      // TODO: the xml diff trims whitespaces...
      //assertXmlFileContent("IgnorableWhitespaceContent.xml", writer.getBuffer().toString());
      //System.out.println(writer.getBuffer().toString());
      
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(writer.getBuffer().toString()), schema);
      
      assertEquals(top, o);
   }

   public void testObjectModelFactory() throws Exception
   {
      String url = findXML("IgnorableWhitespaceContent.xml");
      
      ObjectModelFactory omf = new OMF();
      
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(url, omf, null);
      
      assertNotNull(o);
      assertTrue(o instanceof Top);
      Top top = (Top) o;
      assertEquals(2, top.string.size());
      assertEquals(" ", top.string.get(0));
      assertEquals("\n      newline, 6 spaces, newline, 3 spaces\n   ", top.string.get(1));

   }
   
   public static final class OMF implements ObjectModelFactory
   {
      public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
      {
         return root;
      }

      public Object newRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName, Attributes attrs)
      {
         ctx.setTrimTextContent(false);
         return new Top();
      }

      public void setValue(Top top, UnmarshallingContext ctx, String ns, String name, String value)
      {
         if(name.equals("string"))
         {
            if(top.string == null)
            {
               top.string = new ArrayList<Object>();
            }
            top.string.add(value);
         }
      }
   }

   public static class Top
   {
      public List string;

      public int hashCode()
      {
         final int PRIME = 31;
         int result = 1;
         result = PRIME * result + ((string == null) ? 0 : string.hashCode());
         return result;
      }

      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         final Top other = (Top) obj;
         if (string == null)
         {
            if (other.string != null)
               return false;
         }
         else if (!string.equals(other.string))
            return false;
         return true;
      }
      
      public String toString()
      {
         return "[top: string=" + string + "]";
      }
   }
}
