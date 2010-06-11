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

import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.sunday.unmarshalling.LSInputAdaptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.w3c.dom.ls.LSInput;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class XsiTypeUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(XsiTypeUnitTestCase.class);
   }
   
   private static final String PO_NS = "http://www.example.com/IPO";

   private static final String ADDRESS_XSD =
      "<schema targetNamespace='" + PO_NS + "'" +
      "  xmlns='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='" + Constants.NS_JBXB + "'" +
      "  xmlns:ipo='" + PO_NS + "'>" +
      "  <complexType name='Address'>" +
      "    <sequence>" +
      "      <element name='name' type='string'/>" +
      "      <element name='street' type='string'/>" +
      "      <element name='city' type='string'/>" +
      "    </sequence>" +
      "  </complexType>" +
      "  <complexType name='USAddress'>" +
      "    <annotation>" +
      "      <appinfo>" +
      "        <jbxb:class impl='" + USAddress.class.getName() + "'/>" +
      "      </appinfo>" +
      "    </annotation>" +
      "    <complexContent>" +
      "      <extension base='ipo:Address'>" +
      "        <sequence>" +
      "          <element name='state' type='string'/>" +
      "          <element name='zip' type='string'/>" +
      "        </sequence>" +
      "      </extension>" +
      "    </complexContent>" +
      "  </complexType>" +
      "  <complexType name='UKAddress'>" +
      "    <annotation>" +
      "      <appinfo>" +
      "        <jbxb:class impl='" + UKAddress.class.getName() + "'/>" +
      "      </appinfo>" +
      "    </annotation>" +
      "    <complexContent>" +
      "      <extension base='ipo:Address'>" +
      "        <sequence>" +
      "          <element name='postcode' type='string'/>" +
      "        </sequence>" +
      "        <attribute name='exportCode' type='string' fixed='1'/>" +
      "      </extension>" +
      "    </complexContent>" +
      "  </complexType>" +
      "</schema>";

   private static final String ONE_ADDRESS_XSD =
      "<schema targetNamespace='" + PO_NS + "'" +
      "  xmlns='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='" + Constants.NS_JBXB + "'" +
      "  xmlns:ipo='" + PO_NS + "'>" +
      "  <include schemaLocation='http://www.example.com/schemas/address.xsd'/>" +
      "  <element name='address' type='ipo:Address'/>" +
      "</schema>";

   private static final String ONE_ADDRESS_XML =
      "<ipo:address" +
      "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
      "  xmlns:ipo='" + PO_NS + "'" +
      "  exportCode='1' xsi:type='ipo:UKAddress'>" +
      "    <name>Helen Zoe</name>" +
      "    <street>47 Eden Street</street>" +
      "    <city>Cambridge</city>" +
      "    <postcode>CB1 1JR</postcode>" +
      "</ipo:address>";

   private static final String PO_XSD =
      "<schema targetNamespace='" + PO_NS + "'" +
      "  xmlns='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='" + Constants.NS_JBXB + "'" +
      "  xmlns:ipo='" + PO_NS + "'>" +
      "  <include schemaLocation='http://www.example.com/schemas/address.xsd'/>" +
      "  <complexType name='PurchaseOrderType'>" +
      "    <annotation>" +
      "      <appinfo>" +
      "        <jbxb:class impl='" + PurchaseOrder.class.getName() + "'/>" +
      "      </appinfo>" +
      "    </annotation>" +
      "    <sequence>" +
      "      <element name='shipTo' type='ipo:Address'/>" +
      "      <element name='billTo' type='ipo:Address'/>" +
      "    </sequence>" +
      "  </complexType>" +
      "  <element name='purchaseOrder' type='ipo:PurchaseOrderType'/>" +
      "</schema>";
   
   private static final String PO_XML =
      "<ipo:purchaseOrder" +
      "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
      "  xmlns:ipo='" + PO_NS + "'>" +
      "  <shipTo exportCode='1' xsi:type='ipo:UKAddress'>" +
      "    <name>Helen Zoe</name>" +
      "    <street>47 Eden Street</street>" +
      "    <city>Cambridge</city>" +
      "    <postcode>CB1 1JR</postcode>" +
      "  </shipTo>" +
      "  <billTo xsi:type='ipo:USAddress'>" +
      "    <name>Robert Smith</name>" +
      "    <street>8 Oak Avenue</street>" +
      "    <city>Old Town</city>" +
      "    <state>PA</state>" +
      "    <zip>95819</zip>" +
      "  </billTo>" +
      "</ipo:purchaseOrder>";

   private static final String COLLECTION_XSD =
      "<schema targetNamespace='" + PO_NS + "'" +
      "  xmlns='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='" + Constants.NS_JBXB + "'" +
      "  xmlns:ipo='" + PO_NS + "'>" +
      "  <include schemaLocation='http://www.example.com/schemas/address.xsd'/>" +
      "  <complexType name='AddressCollection'>" +
      "    <annotation>" +
      "      <appinfo>" +
      "        <jbxb:class impl='" + PurchaseOrder.class.getName() + "'/>" +
      "      </appinfo>" +
      "    </annotation>" +
      "    <sequence>" +
      "      <element name='address' type='ipo:Address' maxOccurs='unbounded'/>" +
      "    </sequence>" +
      "  </complexType>" +
      "  <element name='addresses' type='ipo:AddressCollection'/>" +
      "</schema>";

   private static final String COLLECTION_XML =
      "<ipo:addresses" +
      "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
      "  xmlns:ipo='" + PO_NS + "'>" +
      "  <address xsi:type='ipo:USAddress'>" +
      "    <name>Robert Smith</name>" +
      "    <street>8 Oak Avenue</street>" +
      "    <city>Old Town</city>" +
      "    <state>PA</state>" +
      "    <zip>95819</zip>" +
      "  </address>" +
      "  <address exportCode='1' xsi:type='ipo:UKAddress'>" +
      "    <name>Helen Zoe</name>" +
      "    <street>47 Eden Street</street>" +
      "    <city>Cambridge</city>" +
      "    <postcode>CB1 1JR</postcode>" +
      "  </address>" +
      "</ipo:addresses>";

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
         if(schemaLocation.endsWith("address.xsd"))
         {
            return new LSInputAdaptor(new StringReader(ADDRESS_XSD), null);
         }
         return null;
      }
   };

   public XsiTypeUnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
   }

   public void testUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(PO_XSD), null, SCHEMA_RESOLVER);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(PO_XML), schema);
      assertEquals(PurchaseOrder.INSTANCE, o);
   }

   public void testMarshallingXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      marshaller.mapClassToXsiType(UKAddress.class, PO_NS, "UKAddress");
      marshaller.mapClassToXsiType(USAddress.class, PO_NS, "USAddress");

      MappingObjectModelProvider provider = new MappingObjectModelProvider();

      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(PO_XSD), provider, PurchaseOrder.INSTANCE, writer);
      assertXmlEqual(PO_XML, writer.getBuffer().toString());
   }

   public void testMarshallingSunday() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(PO_XSD), null, SCHEMA_RESOLVER);

      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      marshaller.mapClassToXsiType(UKAddress.class, PO_NS, "UKAddress");
      marshaller.mapClassToXsiType(USAddress.class, PO_NS, "USAddress");

      StringWriter writer = new StringWriter();

      marshaller.marshal(schema, null, PurchaseOrder.INSTANCE, writer);
      assertXmlEqual(PO_XML, writer.getBuffer().toString());
   }

   public void testUnmarshalCollection() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(COLLECTION_XSD), null, SCHEMA_RESOLVER);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(COLLECTION_XML), schema);
      
      assertNotNull(o);
      assertTrue(o instanceof PurchaseOrder);
      
      PurchaseOrder po = (PurchaseOrder) o;
      assertNotNull(po.address);
      assertEquals(2, po.address.length);
      
      if(po.address[0] instanceof UKAddress)
      {
         assertEquals(PurchaseOrder.INSTANCE.shipTo, po.address[0]);
         assertEquals(PurchaseOrder.INSTANCE.billTo, po.address[1]);
      }
      else
      {
         assertEquals(PurchaseOrder.INSTANCE.shipTo, po.address[1]);
         assertEquals(PurchaseOrder.INSTANCE.billTo, po.address[0]);
      }
   }

   public void testMarshalCollectionSunday() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(COLLECTION_XSD), null, SCHEMA_RESOLVER);

      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      marshaller.mapClassToXsiType(UKAddress.class, PO_NS, "UKAddress");
      marshaller.mapClassToXsiType(USAddress.class, PO_NS, "USAddress");

      StringWriter writer = new StringWriter();

      PurchaseOrder po = new PurchaseOrder();
      po.address = new Address[2];
      po.address[0] = PurchaseOrder.INSTANCE.billTo;
      po.address[1] = PurchaseOrder.INSTANCE.shipTo;
      
      marshaller.marshal(schema, null, po, writer);
      assertXmlEqual(COLLECTION_XML, writer.getBuffer().toString());
   }

   public void testOneAddressUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(ONE_ADDRESS_XSD), null, SCHEMA_RESOLVER);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(ONE_ADDRESS_XML), schema);
      assertEquals(PurchaseOrder.INSTANCE.shipTo, o);
   }

   public void testOneAddressMarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(ONE_ADDRESS_XSD), null, SCHEMA_RESOLVER);

      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setSchemaResolver(SCHEMA_RESOLVER);
      marshaller.mapClassToXsiType(UKAddress.class, PO_NS, "UKAddress");

      StringWriter writer = new StringWriter();

      marshaller.marshal(schema, null, PurchaseOrder.INSTANCE.shipTo, writer);
      assertXmlEqual(ONE_ADDRESS_XML, writer.getBuffer().toString());
   }

   // Inner

   public static final class PurchaseOrder
   {
      public static final PurchaseOrder INSTANCE = new PurchaseOrder();

      static
      {
         UKAddress ukAddr = new UKAddress();
         ukAddr.name = "Helen Zoe";
         ukAddr.street = "47 Eden Street";
         ukAddr.city = "Cambridge";
         ukAddr.postcode = "CB1 1JR";
         ukAddr.exportCode = "1";
         INSTANCE.shipTo = ukAddr;

         USAddress usAddr = new USAddress();
         usAddr.name = "Robert Smith";
         usAddr.street = "8 Oak Avenue";
         usAddr.city = "Old Town";
         usAddr.state = "PA";
         usAddr.zip = "95819";
         INSTANCE.billTo = usAddr;
      }

      public Address shipTo;
      public Address billTo;
      public Address[] address; //for collection test

      public String toString()
      {
         return "[shipTo=" + shipTo + ", billTo=" + billTo + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof PurchaseOrder))
         {
            return false;
         }

         final PurchaseOrder purchaseOrder = (PurchaseOrder)o;

         if(billTo != null ? !billTo.equals(purchaseOrder.billTo) : purchaseOrder.billTo != null)
         {
            return false;
         }
         if(shipTo != null ? !shipTo.equals(purchaseOrder.shipTo) : purchaseOrder.shipTo != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (shipTo != null ? shipTo.hashCode() : 0);
         result = 29 * result + (billTo != null ? billTo.hashCode() : 0);
         return result;
      }
   }

   public static class Address
   {
      public String name;
      public String street;
      public String city;

      public String toString()
      {
         return "name=" + name + ", street=" + street + ", city=" + city;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Address))
         {
            return false;
         }

         final Address address = (Address)o;

         if(city != null ? !city.equals(address.city) : address.city != null)
         {
            return false;
         }
         if(name != null ? !name.equals(address.name) : address.name != null)
         {
            return false;
         }
         if(street != null ? !street.equals(address.street) : address.street != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (name != null ? name.hashCode() : 0);
         result = 29 * result + (street != null ? street.hashCode() : 0);
         result = 29 * result + (city != null ? city.hashCode() : 0);
         return result;
      }
   }

   public static final class USAddress
      extends Address
   {
      public String state;
      public String zip;

      public String toString()
      {
         return super.toString() + ", state=" + state + ", zip=" + zip;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof USAddress))
         {
            return false;
         }
         if(!super.equals(o))
         {
            return false;
         }

         final USAddress usAddress = (USAddress)o;

         if(state != null ? !state.equals(usAddress.state) : usAddress.state != null)
         {
            return false;
         }
         if(zip != null ? !zip.equals(usAddress.zip) : usAddress.zip != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result = super.hashCode();
         result = 29 * result + (state != null ? state.hashCode() : 0);
         result = 29 * result + (zip != null ? zip.hashCode() : 0);
         return result;
      }
   }

   public static final class UKAddress
      extends Address
   {
      public String postcode;
      public String exportCode;

      public String toString()
      {
         return super.toString() + ", postcode=" + postcode + ", exportCode=" + exportCode;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof UKAddress))
         {
            return false;
         }
         if(!super.equals(o))
         {
            return false;
         }

         final UKAddress ukAddress = (UKAddress)o;

         if(exportCode != null ? !exportCode.equals(ukAddress.exportCode) : ukAddress.exportCode != null)
         {
            return false;
         }
         if(postcode != null ? !postcode.equals(ukAddress.postcode) : ukAddress.postcode != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result = super.hashCode();
         result = 29 * result + (postcode != null ? postcode.hashCode() : 0);
         result = 29 * result + (exportCode != null ? exportCode.hashCode() : 0);
         return result;
      }
   }
}
