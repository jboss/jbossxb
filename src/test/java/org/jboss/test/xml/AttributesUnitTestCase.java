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
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.LSInputAdaptor;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.w3c.dom.ls.LSInput;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class AttributesUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String NS_1 = "http://www.jboss.org/test/xml/attrs";
   private static final String NS_2 = "http://www.jboss.org/test/xml/attrs2";
   private static final String UNQUALIFIED_NAME = "unqualified";
   private static final String MATE_NAME = "mate";
   private static final String FOREIGNER_NAME = "foreigner";
   private static final QName UNQUALIFIED_QNAME = new QName(UNQUALIFIED_NAME);
   private static final QName MATE_QNAME = new QName(NS_1, MATE_NAME);
   private static final QName FOREIGNER_QNAME = new QName(NS_2, FOREIGNER_NAME);

   private static final String XSD =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  targetNamespace='" +
      NS_1 +
      "'" +
      "  xmlns='" +
      NS_1 +
      "'" +
      "  xmlns:attrs2='" +
      NS_2 +
      "'" +
      "  xmlns:jbxb='" +
      Constants.NS_JBXB +
      "'" +
      "  attributeFormDefault='unqualified'" +
      "  version='1.0'>" +
      "<xsd:import namespace='" +
      NS_2 +
      "'/>" +
      "<xsd:element name='e'>" +
      "  <xsd:annotation>" +
      "    <xsd:appinfo>" +
      "      <jbxb:class impl='" +
      E.class.getName() +
      "'/>" +
      "    </xsd:appinfo>" +
      "  </xsd:annotation>" +
      "  <xsd:complexType>" +
      "        <xsd:attribute name='" +
      UNQUALIFIED_NAME +
      "' type='xsd:string'/>" +
      "        <xsd:attribute form='qualified' name='" +
      MATE_NAME +
      "' type='xsd:string'/>" +
      "        <xsd:attribute ref='attrs2:" +
      FOREIGNER_NAME +
      "'/>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "</xsd:schema>";

   private static final String XSD_2 =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  targetNamespace='" +
      NS_2 +
      "'" +
      "  xmlns='" +
      NS_2 +
      "'" +
      "  attributeFormDefault='qualified'" +
      "  version='1.0'>" +
      "  <xsd:attribute name='" +
      FOREIGNER_NAME +
      "' type='xsd:string'/>" +
      "</xsd:schema>";

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
         return new LSInputAdaptor(new StringReader(XSD_2), null);
      }
   };

   private static SchemaBinding SCHEMA;

   private static final String XML =
      "<attrs:e xmlns:attrs='" +
      NS_1 +
      "' xmlns:attrs2='" +
      NS_2 +
      "' " +
      "attrs:" +
      MATE_NAME +
      "='m' attrs2:" +
      FOREIGNER_NAME +
      "='f' " +
      UNQUALIFIED_NAME + "='u'/>";

   public AttributesUnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null, SCHEMA_RESOLVER);
         SCHEMA.setIgnoreUnresolvedFieldOrClass(false);
      }
   }
   
   public void testAttributesInSchema()
   {
      ElementBinding eBinding = SCHEMA.getElement(new QName(NS_1, "e"));
      TypeBinding eType = eBinding.getType();
      Collection attributes = eType.getAttributes();
      assertNotNull(attributes);
      assertEquals(3, attributes.size());
      assertNotNull(eType.getAttribute(UNQUALIFIED_QNAME));
      assertNotNull(eType.getAttribute(MATE_QNAME));
      assertNotNull(eType.getAttribute(FOREIGNER_QNAME));
   }

   public void testUnmarshalling() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), SCHEMA);
      assertTrue(o instanceof E);
      assertEquals(E.INSTANCE, o);
   }

   public void testMarshallingXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller(); //new MarshallerImpl();
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), E.INSTANCE, writer);
      assertXmlEqual(XML, writer.getBuffer().toString());
   }

   public void testMarshallingSunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      StringWriter writer = new StringWriter();
      marshaller.marshal(SCHEMA, null, E.INSTANCE, writer);
      assertXmlEqual(XML, writer.getBuffer().toString());
   }

   // Inner

   public static final class E
   {
      public static final E INSTANCE;

      static
      {
         E e = new E();
         e.unqualified = "u";
         e.mate = "m";
         e.foreigner = "f";
         INSTANCE = e;
      }

      public String unqualified;
      public String mate;
      public String foreigner;

      public String toString()
      {
         return "[unqualified=" + unqualified + ", mate=" + mate + ", foreigner=" + foreigner + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof E))
         {
            return false;
         }

         final E e = (E)o;

         if(foreigner != null ? !foreigner.equals(e.foreigner) : e.foreigner != null)
         {
            return false;
         }
         if(mate != null ? !mate.equals(e.mate) : e.mate != null)
         {
            return false;
         }
         if(unqualified != null ? !unqualified.equals(e.unqualified) : e.unqualified != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (unqualified != null ? unqualified.hashCode() : 0);
         result = 29 * result + (mate != null ? mate.hashCode() : 0);
         result = 29 * result + (foreigner != null ? foreigner.hashCode() : 0);
         return result;
      }
   }
}
