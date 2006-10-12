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
import java.util.Arrays;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 57202 $</tt>
 */
public class ArrayWrapperUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String XSD =
      "<xsd:schema" +
      " targetNamespace='http://www.jboss.org/xml/test/arraywrapper'" +
      " xmlns='http://www.jboss.org/xml/test/arraywrapper'" +
      " xmlns:jbxb='" +
      Constants.NS_JBXB +
      "'" +
      " elementFormDefault='qualified'" +
      " xmlns:xsd='http://www.w3.org/2001/XMLSchema'>" +
      " <xsd:complexType name='ArrayWrapper'>" +
      "   <xsd:annotation>" +
      "      <xsd:appinfo>" +
      "         <jbxb:class impl='" +
      ArrayWrapper.class.getName() +
      "'/>" +
      "      </xsd:appinfo>" +
      "   </xsd:annotation>" +
      "   <xsd:sequence>" +
      "     <xsd:element name='stringArray' type='StringArray' minOccurs='0'/>" +
      "     <xsd:element name='stringArrayArray' type='StringArrayArray' minOccurs='0'/>" +
      "     <xsd:element name='jbxb79' type='xsd:string' minOccurs='0' maxOccurs='unbounded'/>" +
      "   </xsd:sequence>" +
      " </xsd:complexType>" +
      " <xsd:complexType name='StringArray'>" +
      "   <xsd:annotation>" +
      "     <xsd:appinfo>" +
      "       <jbxb:class impl='" + StringArray.class.getName() + "'/>" +
      "     </xsd:appinfo>" +
      "   </xsd:annotation>" +
      "   <xsd:sequence>" +
      "     <xsd:element maxOccurs='unbounded' minOccurs='0' name='value' nillable='true' type='xsd:string'/>" +
      "   </xsd:sequence>" +
      "  </xsd:complexType>" +
      "  <xsd:complexType name='StringArrayArray'>" +
      "   <xsd:annotation>" +
      "     <xsd:appinfo>" +
      "       <jbxb:class impl='" + StringArrayArray.class.getName() + "'/>" +
      "     </xsd:appinfo>" +
      "   </xsd:annotation>" +
      "   <xsd:sequence>" +
      "     <xsd:element maxOccurs='unbounded' minOccurs='0' name='value' nillable='true' type='StringArray'/>" +
      "   </xsd:sequence>" +
      "  </xsd:complexType>" +
      " <xsd:element name='arr' type='ArrayWrapper'/>" +
      "</xsd:schema>";

   private static SchemaBinding SCHEMA;// = XsdBinder.bind(new StringReader(XSD), null);

   private static final String XML =
      "<ns_arr:arr xmlns:ns_arr='http://www.jboss.org/xml/test/arraywrapper'>" +
      "  <ns_arr:stringArray>" +
      "   <ns_arr:value>item1</ns_arr:value>" +
      "   <ns_arr:value>item2</ns_arr:value>" +
      "   <ns_arr:value></ns_arr:value>" +
      "   <ns_arr:value xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='1'></ns_arr:value>" +
      "  </ns_arr:stringArray>" +
      "</ns_arr:arr>";

   private static final String XML_STRING_ARR_ARR =
      "<arr xmlns='http://www.jboss.org/xml/test/arraywrapper'>" +
      "  <stringArrayArray>" +
      "    <value>" +
      "      <value>item1</value>" +
      "    </value>" +
      "    <value>" +
      "      <value/>" +
      "    </value>" +
      "    <value>" +
      "      <value xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='1'/>" +
      "    </value>" +
      "    <value xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='1'/>" +
      "    <value>" +
      "      <value>item1</value>" +
      "      <value>item2</value>" +
      "      <value/>" +
      "      <value xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='1'/>" +
      "    </value>" +
      "  </stringArrayArray>" +
      "</arr>";

   private static final String JBXB79_XML =
      "<ns_arr:arr xmlns:ns_arr='http://www.jboss.org/xml/test/arraywrapper'>" +
      "   <ns_arr:jbxb79>item1</ns_arr:jbxb79>" +
      "   <ns_arr:jbxb79>item2</ns_arr:jbxb79>" +
      "   <ns_arr:jbxb79></ns_arr:jbxb79>" +
      "   <ns_arr:jbxb79 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='1'/>" +
      "</ns_arr:arr>";

   public ArrayWrapperUnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
//      enableTrace("org.jboss.xb.binding");
      
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null);
      }
   }

   public void testMarshalStringArraySunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      StringWriter writer = new StringWriter();
      marshaller.marshal(SCHEMA, null, ArrayWrapper.STRING_ARR_INSTANCE, writer);
      assertXmlEqual(XML, writer.getBuffer().toString());
   }

   public void testMarshalStringArrayXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.mapClassToGlobalElement(ArrayWrapper.class, "arr", "http://www.jboss.org/xml/test/arraywrapper", null, null);

      MappingObjectModelProvider provider = new MappingObjectModelProvider();
      provider.mapFieldToElement(ArrayWrapper.class, "stringArray", "http://www.jboss.org/xml/test/arraywrapper", "stringArray", null);

      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), provider, ArrayWrapper.STRING_ARR_INSTANCE, writer);
      assertXmlEqual(XML, writer.getBuffer().toString());
   }

   public void testUnmarshalStringArray() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), SCHEMA);
      assertNotNull(o);
      assertTrue(o instanceof ArrayWrapper);
      ArrayWrapper aw = (ArrayWrapper)o;
      assertNotNull(aw.stringArray);
      assertTrue(Arrays.equals(ArrayWrapper.STRING_ARR_INSTANCE.stringArray.getValue(), aw.stringArray.getValue()));
   }

   public void testMarshalStringArrayArraySunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      StringWriter writer = new StringWriter();
      marshaller.marshal(SCHEMA, null, ArrayWrapper.STRING_ARR_ARR_INSTANCE, writer);
      assertXmlEqual(XML_STRING_ARR_ARR, writer.getBuffer().toString());
   }

   public void testMarshalStringArrayArrayXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.mapClassToGlobalElement(ArrayWrapper.class, "arr", "http://www.jboss.org/xml/test/arraywrapper", null, null);

      MappingObjectModelProvider provider = new MappingObjectModelProvider();

      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), provider, ArrayWrapper.STRING_ARR_ARR_INSTANCE, writer);
      assertXmlEqual(XML_STRING_ARR_ARR, writer.getBuffer().toString());
   }

   public void testUnmarshalStringArrayArray() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML_STRING_ARR_ARR), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof ArrayWrapper);
      ArrayWrapper aw = (ArrayWrapper)o;
      assertNotNull(aw.stringArrayArray);
      StringArray[] wasArr = aw.stringArrayArray.getValue();
      assertNotNull(wasArr);

      StringArray[] expArr = ArrayWrapper.STRING_ARR_ARR_INSTANCE.stringArrayArray.getValue();
      assertEquals(expArr.length, wasArr.length);

      for(int i = 0; i < expArr.length; ++i)
      {
         StringArray expSA = expArr[i];
         StringArray wasSA = wasArr[i];

         if(expSA == null)
         {
            assertNull(wasSA);
         }
         else
         {
            assertNotNull(wasSA);
            String[] exp = expSA.getValue();
            String[] was = wasSA.getValue();
            if(exp == null)
            {
               assertNull(was);
            }
            else
            {
               assertEquals(exp, was);
            }
         }
      }
   }

   public void testUnmarshalJBXB79StringArray() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(JBXB79_XML), SCHEMA);
      assertNotNull(o);
      assertTrue(o instanceof ArrayWrapper);
      ArrayWrapper aw = (ArrayWrapper)o;
      assertNotNull(aw.jbxb79);
      assertEquals(4, aw.jbxb79.length);
      assertTrue(Arrays.equals(ArrayWrapper.JBXB79_STRING_ARR_INSTANCE.jbxb79, aw.jbxb79));
   }

   // Inner

   public static class ArrayWrapper
   {
      public static final ArrayWrapper STRING_ARR_INSTANCE;
      public static final ArrayWrapper STRING_ARR_ARR_INSTANCE;
      public static final ArrayWrapper JBXB79_STRING_ARR_INSTANCE;

      static
      {
         STRING_ARR_INSTANCE = new ArrayWrapper();
         STRING_ARR_INSTANCE.stringArray = new StringArray(new String[]{"item1", "item2", "", null});

         STRING_ARR_ARR_INSTANCE = new ArrayWrapper();
         STRING_ARR_ARR_INSTANCE.stringArrayArray = new StringArrayArray(
            new StringArray[]
            {
               new StringArray(new String[]{"item1"}),
               new StringArray(new String[]{""}),
               new StringArray(new String[]{null}),
               null,
               STRING_ARR_INSTANCE.stringArray
            }
         );

         JBXB79_STRING_ARR_INSTANCE = new ArrayWrapper();
         JBXB79_STRING_ARR_INSTANCE.jbxb79 = new String[]{"item1", "item2", "", null};
}

      public StringArray stringArray;
      public StringArrayArray stringArrayArray;
      public String[] jbxb79 = new String[10];

      public String toString()
      {
         return "stringArray=" + stringArray + "; stringArrayArray=" + stringArrayArray;
      }
   }

   public static class StringArray
   {
      private String[] value;

      public StringArray()
      {
      }

      public StringArray(final String[] value)
      {
         this.value = value;
      }

      public String[] getValue()
      {
         return this.value;
      }

      public void setValue(final String[] value)
      {
         this.value = value;
      }

      public String toString()
      {
         return (value != null ? Arrays.asList(value).toString() : null);
      }
   }

   public static class StringArrayArray
   {
      private StringArray[] value;

      public StringArrayArray()
      {
      }

      public StringArrayArray(final StringArray[] value)
      {
         this.value = value;
      }

      public StringArray[] getValue()
      {
         return this.value;
      }

      public void setValue(final StringArray[] value)
      {
         this.value = value;
      }

      public String toString()
      {
         return (value != null ? Arrays.asList(value).toString() : null);
      }
   }
}
