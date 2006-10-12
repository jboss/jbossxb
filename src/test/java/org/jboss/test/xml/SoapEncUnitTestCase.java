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
import java.io.InputStream;
import java.io.StringWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Arrays;
import java.util.TimeZone;
import java.lang.reflect.Array;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.AbstractMarshaller;
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
public class SoapEncUnitTestCase
   extends AbstractJBossXBTest
{
   private static final SchemaBindingResolver RESOLVER = new SchemaBindingResolver()
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
         if("http://schemas.xmlsoap.org/soap/encoding/".equals(nsUri))
         {
            InputStream is = getStream("xml/soapenc/soapenc.xsd");
            return new LSInputAdaptor(is, null);
         }

         throw new IllegalStateException("Unexpected nsUri: " + nsUri);
      }
   };

   private static final String XML =
      "<ns_allStruct:allStruct xmlns:ns_allStruct=\"http://ParameterModeTest.org/xsd\">" +
      "<varString>String1</varString>" +
      "<varInteger>3512359</varInteger>" +
      "<varInt>-2147483648</varInt>" +
      "<varLong>-9223372036854775808</varLong>" +
      "<varShort>-32768</varShort>" +
      "<varDecimal>3512359.1456</varDecimal>" +
      "<varFloat>1.4E-45</varFloat>" +
      "<varDouble>4.9E-324</varDouble>" +
      "<varBoolean>false</varBoolean>" +
      "<varByte>-128</varByte>" +
      "<varQName xmlns=\"\">String2</varQName>" +
      "<varDateTime>0096-05-31T23:00:00.000Z</varDateTime>" +
      "<varSoapString>String3</varSoapString>" +
      "<varSoapBoolean>false</varSoapBoolean>" +
      "<varSoapFloat>1.4E-45</varSoapFloat>" +
      "<varSoapDouble>4.9E-324</varSoapDouble>" +
      "<varSoapDecimal>3512359.1111</varSoapDecimal>" +
      "<varSoapInt>-2147483648</varSoapInt>" +
      "<varSoapShort>-32768</varSoapShort>" +
      "<varSoapByte>-128</varSoapByte>" +
      "<varBase64Binary>gAB/</varBase64Binary>" +
      "<varHexBinary>80007f</varHexBinary>" +
      "<varSoapBase64>gAB/</varSoapBase64>" +
      "</ns_allStruct:allStruct>";

   public SoapEncUnitTestCase(String name)
   {
      super(name);
   }

   public void testMarshallingXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setSchemaResolver(RESOLVER);
      assertMarshalling(marshaller);
   }

   public void testMarshallingSunday() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setSchemaResolver(RESOLVER);
      assertMarshalling(marshaller);
   }

   public void testUnmarshalling() throws Exception
   {
      String xsdPath = getUrl("xml/soapenc/test.xsd").toExternalForm();
      SchemaBinding schema = XsdBinder.bind(xsdPath, RESOLVER);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof AllStruct);
      assertEquals(AllStruct.INSTANCE, o);
   }

   // private

   private void assertMarshalling(AbstractMarshaller marshaller)
      throws Exception
   {
      StringWriter writer = new StringWriter();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_XML_VERSION, "false");
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      marshaller.marshal(new FileReader(getUrl("xml/soapenc/test.xsd").getFile()),
         new MappingObjectModelProvider(),
         AllStruct.INSTANCE,
         writer
      );

      String xml = writer.getBuffer().toString();
      assertXmlEqual(XML, xml);
   }

   private static URL getUrl(String path)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if(url == null)
      {
         fail("resource not found: " + path);
      }
      return url;
   }

   private static InputStream getStream(String path)
   {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
      if(is == null)
      {
         fail("resource not found: " + path);
      }
      return is;
   }

   // inner

   public static class AllStruct
   {
      public static final AllStruct INSTANCE;

      static
      {
         byte[] bytes = new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE};

         AllStruct struct = new AllStruct();
         struct = new AllStruct();
         struct.setVarString(new String("String1"));
         struct.setVarInteger(new BigInteger("3512359"));
         struct.setVarInt((int)Integer.MIN_VALUE);
         struct.setVarLong((long)Long.MIN_VALUE);
         struct.setVarShort((short)Short.MIN_VALUE);
         struct.setVarDecimal(new BigDecimal("3512359.1456"));
         struct.setVarFloat((float)Float.MIN_VALUE);
         struct.setVarDouble((double)Double.MIN_VALUE);
         struct.setVarBoolean(false);
         struct.setVarByte((byte)Byte.MIN_VALUE);
         struct.setVarQName(new QName("String2"));

         Calendar varDateTime = (Calendar)new GregorianCalendar(96, 5, 1);
         int offset = varDateTime.get(Calendar.ZONE_OFFSET) + varDateTime.get(Calendar.DST_OFFSET);
         StringBuffer buf = new StringBuffer();
         buf.append("GMT");
         if(offset > 0)
         {
            buf.append('+');
         }
         buf.append(offset);
         varDateTime.setTimeZone(TimeZone.getTimeZone(buf.toString()));
         struct.setVarDateTime(varDateTime);

         struct.setVarSoapString("String3");
         struct.setVarSoapBoolean(new Boolean(false));
         struct.setVarSoapFloat(new Float(Float.MIN_VALUE));
         struct.setVarSoapDouble(new Double(Double.MIN_VALUE));
         struct.setVarSoapDecimal(new BigDecimal("3512359.1111"));
         struct.setVarSoapInt(new Integer(Integer.MIN_VALUE));
         struct.setVarSoapShort(new Short(Short.MIN_VALUE));
         struct.setVarSoapByte(new Byte(Byte.MIN_VALUE));
         struct.setVarBase64Binary(bytes);
         struct.setVarHexBinary(bytes);
         struct.setVarSoapBase64(bytes);

         INSTANCE = struct;
      }

      private java.lang.String varString;
      private java.math.BigInteger varInteger;
      private int varInt;
      private long varLong;
      private short varShort;
      private java.math.BigDecimal varDecimal;
      private float varFloat;
      private double varDouble;
      private boolean varBoolean;
      private byte varByte;
      private javax.xml.namespace.QName varQName;
      private java.util.Calendar varDateTime;
      private java.lang.String varSoapString;
      private java.lang.Boolean varSoapBoolean;
      private java.lang.Float varSoapFloat;
      private java.lang.Double varSoapDouble;
      private java.math.BigDecimal varSoapDecimal;
      private java.lang.Integer varSoapInt;
      private java.lang.Short varSoapShort;
      private java.lang.Byte varSoapByte;
      private byte[] varBase64Binary;
      private byte[] varHexBinary;
      private byte[] varSoapBase64;

      public AllStruct()
      {
      }

      public AllStruct(java.lang.String varString,
                       java.math.BigInteger varInteger,
                       int varInt,
                       long varLong,
                       short varShort,
                       java.math.BigDecimal varDecimal,
                       float varFloat,
                       double varDouble,
                       boolean varBoolean,
                       byte varByte,
                       javax.xml.namespace.QName varQName,
                       java.util.Calendar varDateTime,
                       java.lang.String varSoapString,
                       java.lang.Boolean varSoapBoolean,
                       java.lang.Float varSoapFloat,
                       java.lang.Double varSoapDouble,
                       java.math.BigDecimal varSoapDecimal,
                       java.lang.Integer varSoapInt,
                       java.lang.Short varSoapShort,
                       java.lang.Byte varSoapByte,
                       byte[] varBase64Binary,
                       byte[] varHexBinary,
                       byte[] varSoapBase64)
      {
         this.varString = varString;
         this.varInteger = varInteger;
         this.varInt = varInt;
         this.varLong = varLong;
         this.varShort = varShort;
         this.varDecimal = varDecimal;
         this.varFloat = varFloat;
         this.varDouble = varDouble;
         this.varBoolean = varBoolean;
         this.varByte = varByte;
         this.varQName = varQName;
         this.varDateTime = varDateTime;
         this.varSoapString = varSoapString;
         this.varSoapBoolean = varSoapBoolean;
         this.varSoapFloat = varSoapFloat;
         this.varSoapDouble = varSoapDouble;
         this.varSoapDecimal = varSoapDecimal;
         this.varSoapInt = varSoapInt;
         this.varSoapShort = varSoapShort;
         this.varSoapByte = varSoapByte;
         this.varBase64Binary = varBase64Binary;
         this.varHexBinary = varHexBinary;
         this.varSoapBase64 = varSoapBase64;
      }

      public java.lang.String getVarString()
      {
         return varString;
      }

      public void setVarString(java.lang.String varString)
      {
         this.varString = varString;
      }

      public java.math.BigInteger getVarInteger()
      {
         return varInteger;
      }

      public void setVarInteger(java.math.BigInteger varInteger)
      {
         this.varInteger = varInteger;
      }

      public int getVarInt()
      {
         return varInt;
      }

      public void setVarInt(int varInt)
      {
         this.varInt = varInt;
      }

      public long getVarLong()
      {
         return varLong;
      }

      public void setVarLong(long varLong)
      {
         this.varLong = varLong;
      }

      public short getVarShort()
      {
         return varShort;
      }

      public void setVarShort(short varShort)
      {
         this.varShort = varShort;
      }

      public java.math.BigDecimal getVarDecimal()
      {
         return varDecimal;
      }

      public void setVarDecimal(java.math.BigDecimal varDecimal)
      {
         this.varDecimal = varDecimal;
      }

      public float getVarFloat()
      {
         return varFloat;
      }

      public void setVarFloat(float varFloat)
      {
         this.varFloat = varFloat;
      }

      public double getVarDouble()
      {
         return varDouble;
      }

      public void setVarDouble(double varDouble)
      {
         this.varDouble = varDouble;
      }

      public boolean isVarBoolean()
      {
         return varBoolean;
      }

      public void setVarBoolean(boolean varBoolean)
      {
         this.varBoolean = varBoolean;
      }

      public byte getVarByte()
      {
         return varByte;
      }

      public void setVarByte(byte varByte)
      {
         this.varByte = varByte;
      }

      public javax.xml.namespace.QName getVarQName()
      {
         return varQName;
      }

      public void setVarQName(javax.xml.namespace.QName varQName)
      {
         this.varQName = varQName;
      }

      public java.util.Calendar getVarDateTime()
      {
         return varDateTime;
      }

      public void setVarDateTime(java.util.Calendar varDateTime)
      {
         this.varDateTime = varDateTime;
      }

      public java.lang.String getVarSoapString()
      {
         return varSoapString;
      }

      public void setVarSoapString(java.lang.String varSoapString)
      {
         this.varSoapString = varSoapString;
      }

      public java.lang.Boolean getVarSoapBoolean()
      {
         return varSoapBoolean;
      }

      public void setVarSoapBoolean(java.lang.Boolean varSoapBoolean)
      {
         this.varSoapBoolean = varSoapBoolean;
      }

      public java.lang.Float getVarSoapFloat()
      {
         return varSoapFloat;
      }

      public void setVarSoapFloat(java.lang.Float varSoapFloat)
      {
         this.varSoapFloat = varSoapFloat;
      }

      public java.lang.Double getVarSoapDouble()
      {
         return varSoapDouble;
      }

      public void setVarSoapDouble(java.lang.Double varSoapDouble)
      {
         this.varSoapDouble = varSoapDouble;
      }

      public java.math.BigDecimal getVarSoapDecimal()
      {
         return varSoapDecimal;
      }

      public void setVarSoapDecimal(java.math.BigDecimal varSoapDecimal)
      {
         this.varSoapDecimal = varSoapDecimal;
      }

      public java.lang.Integer getVarSoapInt()
      {
         return varSoapInt;
      }

      public void setVarSoapInt(java.lang.Integer varSoapInt)
      {
         this.varSoapInt = varSoapInt;
      }

      public java.lang.Short getVarSoapShort()
      {
         return varSoapShort;
      }

      public void setVarSoapShort(java.lang.Short varSoapShort)
      {
         this.varSoapShort = varSoapShort;
      }

      public java.lang.Byte getVarSoapByte()
      {
         return varSoapByte;
      }

      public void setVarSoapByte(java.lang.Byte varSoapByte)
      {
         this.varSoapByte = varSoapByte;
      }

      public byte[] getVarBase64Binary()
      {
         return varBase64Binary;
      }

      public void setVarBase64Binary(byte[] varBase64Binary)
      {
         this.varBase64Binary = varBase64Binary;
      }

      public byte[] getVarHexBinary()
      {
         return varHexBinary;
      }

      public void setVarHexBinary(byte[] varHexBinary)
      {
         this.varHexBinary = varHexBinary;
      }

      public byte[] getVarSoapBase64()
      {
         return varSoapBase64;
      }

      public void setVarSoapBase64(byte[] varSoapBase64)
      {
         this.varSoapBase64 = varSoapBase64;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof AllStruct))
         {
            return false;
         }

         final AllStruct struct = (AllStruct)o;

         if(varBoolean != struct.varBoolean)
         {
            return false;
         }
         if(varByte != struct.varByte)
         {
            return false;
         }
         if(varDouble != struct.varDouble)
         {
            return false;
         }
         if(varFloat != struct.varFloat)
         {
            return false;
         }
         if(varInt != struct.varInt)
         {
            return false;
         }
         if(varLong != struct.varLong)
         {
            return false;
         }
         if(varShort != struct.varShort)
         {
            return false;
         }
         if(!Arrays.equals(varBase64Binary, struct.varBase64Binary))
         {
            return false;
         }
         if(varDateTime != null ? !varDateTime.equals(struct.varDateTime) : struct.varDateTime != null)
         {
            return false;
         }
         if(varDecimal != null ? !varDecimal.equals(struct.varDecimal) : struct.varDecimal != null)
         {
            return false;
         }
         if(!Arrays.equals(varHexBinary, struct.varHexBinary))
         {
            return false;
         }
         if(varInteger != null ? !varInteger.equals(struct.varInteger) : struct.varInteger != null)
         {
            return false;
         }
         if(varQName != null ? !varQName.equals(struct.varQName) : struct.varQName != null)
         {
            return false;
         }
         if(!Arrays.equals(varSoapBase64, struct.varSoapBase64))
         {
            return false;
         }
         if(varSoapBoolean != null ? !varSoapBoolean.equals(struct.varSoapBoolean) : struct.varSoapBoolean != null)
         {
            return false;
         }
         if(varSoapByte != null ? !varSoapByte.equals(struct.varSoapByte) : struct.varSoapByte != null)
         {
            return false;
         }
         if(varSoapDecimal != null ? !varSoapDecimal.equals(struct.varSoapDecimal) : struct.varSoapDecimal != null)
         {
            return false;
         }
         if(varSoapDouble != null ? !varSoapDouble.equals(struct.varSoapDouble) : struct.varSoapDouble != null)
         {
            return false;
         }
         if(varSoapFloat != null ? !varSoapFloat.equals(struct.varSoapFloat) : struct.varSoapFloat != null)
         {
            return false;
         }
         if(varSoapInt != null ? !varSoapInt.equals(struct.varSoapInt) : struct.varSoapInt != null)
         {
            return false;
         }
         if(varSoapShort != null ? !varSoapShort.equals(struct.varSoapShort) : struct.varSoapShort != null)
         {
            return false;
         }
         if(varSoapString != null ? !varSoapString.equals(struct.varSoapString) : struct.varSoapString != null)
         {
            return false;
         }
         if(varString != null ? !varString.equals(struct.varString) : struct.varString != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         long temp;
         result = (varString != null ? varString.hashCode() : 0);
         result = 29 * result + (varInteger != null ? varInteger.hashCode() : 0);
         result = 29 * result + varInt;
         result = 29 * result + (int)(varLong ^ (varLong >>> 32));
         result = 29 * result + (int)varShort;
         result = 29 * result + (varDecimal != null ? varDecimal.hashCode() : 0);
         result = 29 * result + varFloat != +0.0f ? Float.floatToIntBits(varFloat) : 0;
         temp = varDouble != +0.0d ? Double.doubleToLongBits(varDouble) : 0l;
         result = 29 * result + (int)(temp ^ (temp >>> 32));
         result = 29 * result + (varBoolean ? 1 : 0);
         result = 29 * result + (int)varByte;
         result = 29 * result + (varQName != null ? varQName.hashCode() : 0);
         result = 29 * result + (varDateTime != null ? varDateTime.hashCode() : 0);
         result = 29 * result + (varSoapString != null ? varSoapString.hashCode() : 0);
         result = 29 * result + (varSoapBoolean != null ? varSoapBoolean.hashCode() : 0);
         result = 29 * result + (varSoapFloat != null ? varSoapFloat.hashCode() : 0);
         result = 29 * result + (varSoapDouble != null ? varSoapDouble.hashCode() : 0);
         result = 29 * result + (varSoapDecimal != null ? varSoapDecimal.hashCode() : 0);
         result = 29 * result + (varSoapInt != null ? varSoapInt.hashCode() : 0);
         result = 29 * result + (varSoapShort != null ? varSoapShort.hashCode() : 0);
         result = 29 * result + (varSoapByte != null ? varSoapByte.hashCode() : 0);
         return result;
      }

      public String toString()
      {
         return
         "[varString=" + varString +
         "\nvarInteger=" + varInteger +
         "\nvarInt=" + varInt +
         "\nvarLong=" + varLong +
         "\nvarShort=" + varShort +
         "\nvarDecimal=" + varDecimal +
         "\nvarFloat=" + varFloat +
         "\nvarDouble=" + varDouble +
         "\nvarBoolean=" + varBoolean +
         "\nvarByte=" + varByte +
         "\nvarQName=" + varQName +
         "\nvarDateTime=" + varDateTime +
         "\nvarSoapString=" + varSoapString +
         "\nvarSoapBoolean=" + varSoapBoolean +
         "\nvarSoapFloat=" + varSoapFloat +
         "\nvarSoapDouble=" + varSoapDouble +
         "\nvarSoapDecimal=" + varSoapDecimal +
         "\nvarSoapInt=" + varSoapInt +
         "\nvarSoapShort=" + varSoapShort +
         "\nvarSoapByte=" + varSoapByte +
         "\nvarBase64Binary=" + arrayToString(varBase64Binary) +
         "\nvarHexBinary=" + arrayToString(varHexBinary) +
         "\nvarSoapBase64=" + arrayToString(varSoapBase64) + "]";
      }
   }

   private static String arrayToString(Object arr)
   {
      String str;
      if(arr == null)
      {
         str = "null";
      }
      else
      {
         StringBuffer buf = new StringBuffer();
         buf.append('[');
         for(int i = 0; i < Array.getLength(arr); ++i)
         {
            if(i > 0)
            {
               buf.append(", ");
            }
            buf.append(Array.get(arr, i));
         }
         buf.append(']');
         str = buf.toString();
      }
      return str;
   }
}
