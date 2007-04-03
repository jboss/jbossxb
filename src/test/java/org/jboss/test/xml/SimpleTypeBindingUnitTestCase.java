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

import junit.framework.TestCase;

import org.jboss.xb.binding.SimpleTypeBindings;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.TimeZone;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.URI;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 40286 $</tt>
 */
public class SimpleTypeBindingUnitTestCase
   extends TestCase
{
   public SimpleTypeBindingUnitTestCase()
   {
   }

   public SimpleTypeBindingUnitTestCase(String localName)
   {
      super(localName);
   }

   public void testIntUnmarshalling() throws Exception
   {
      assertEquals(Integer.MAX_VALUE,
         ((Integer)SimpleTypeBindings.unmarshal("int", Integer.toString(Integer.MAX_VALUE), null)).intValue()
      );
      assertEquals(Integer.MIN_VALUE,
         ((Integer)SimpleTypeBindings.unmarshal("int", Integer.toString(Integer.MIN_VALUE), null)).intValue()
      );
   }

   public void testLongUnmarshalling() throws Exception
   {
      assertEquals(Long.MAX_VALUE,
         ((Long)SimpleTypeBindings.unmarshal("long", Long.toString(Long.MAX_VALUE), null)).longValue()
      );
      assertEquals(Long.MIN_VALUE,
         ((Long)SimpleTypeBindings.unmarshal("long", Long.toString(Long.MIN_VALUE), null)).longValue()
      );
   }

   public void testShortUnmarshalling() throws Exception
   {
      assertEquals(Short.MAX_VALUE,
         ((Short)SimpleTypeBindings.unmarshal("short", Short.toString(Short.MAX_VALUE), null)).shortValue()
      );
      assertEquals(Short.MIN_VALUE,
         ((Short)SimpleTypeBindings.unmarshal("short", Short.toString(Short.MIN_VALUE), null)).shortValue()
      );
   }

   public void testByteUnmarshalling() throws Exception
   {
      assertEquals(Byte.MAX_VALUE,
         ((Byte)SimpleTypeBindings.unmarshal("byte", Byte.toString(Byte.MAX_VALUE), null)).byteValue()
      );
      assertEquals(Byte.MIN_VALUE,
         ((Byte)SimpleTypeBindings.unmarshal("byte", Byte.toString(Byte.MIN_VALUE), null)).byteValue()
      );
   }

   public void testFloatUnmarshalling() throws Exception
   {
      assertEquals(Float.MAX_VALUE,
         ((Float)SimpleTypeBindings.unmarshal("float", Float.toString(Float.MAX_VALUE), null)).floatValue(),
         0
      );
      assertEquals(Float.MIN_VALUE,
         ((Float)SimpleTypeBindings.unmarshal("float", Float.toString(Float.MIN_VALUE), null)).floatValue(),
         0
      );
      assertEquals(-1E4, ((Float)SimpleTypeBindings.unmarshal("float", "-1E4", null)).floatValue(), 0);
      assertEquals(1267.43233E12,
         ((Float)SimpleTypeBindings.unmarshal("float", "1267.43233E12", null)).floatValue(),
         1.267432366800896E15 - 1.26743233E15
      );
      assertEquals(12.78e-2,
         ((Float)SimpleTypeBindings.unmarshal("float", "12.78e-2", null)).floatValue(),
         0.12780000269412994 - 0.1278
      );
      assertEquals(-0, ((Float)SimpleTypeBindings.unmarshal("float", "-0", null)).floatValue(), 0);
      assertEquals(Float.POSITIVE_INFINITY,
         ((Float)SimpleTypeBindings.unmarshal("float", "INF", null)).floatValue(),
         0
      );
   }

   public void testDoubleUnmarshalling() throws Exception
   {
      assertEquals(Double.MAX_VALUE,
         ((Double)SimpleTypeBindings.unmarshal("double", Double.toString(Double.MAX_VALUE), null)).doubleValue(),
         0
      );
      assertEquals(Double.MIN_VALUE,
         ((Double)SimpleTypeBindings.unmarshal("double", Double.toString(Double.MIN_VALUE), null)).doubleValue(),
         0
      );
      assertEquals(-1E4, ((Double)SimpleTypeBindings.unmarshal("double", "-1E4", null)).doubleValue(), 0);
      assertEquals(1267.43233E12,
         ((Double)SimpleTypeBindings.unmarshal("double", "1267.43233E12", null)).doubleValue(),
         1.267432366800896E15 - 1.26743233E15
      );
      assertEquals(12.78e-2,
         ((Double)SimpleTypeBindings.unmarshal("double", "12.78e-2", null)).doubleValue(),
         0.12780000269412994 - 0.1278
      );
      assertEquals(-0, ((Double)SimpleTypeBindings.unmarshal("double", "-0", null)).doubleValue(), 0);
      assertEquals(Double.POSITIVE_INFINITY,
         ((Double)SimpleTypeBindings.unmarshal("double", "INF", null)).doubleValue(),
         0
      );
   }

   public void testBooleanUnmarshalling() throws Exception
   {
      assertEquals(Boolean.TRUE, SimpleTypeBindings.unmarshal("boolean", "true", null));
      assertEquals(Boolean.TRUE, SimpleTypeBindings.unmarshal("boolean", "1", null));
      assertEquals(Boolean.FALSE, SimpleTypeBindings.unmarshal("boolean", "false", null));
      assertEquals(Boolean.FALSE, SimpleTypeBindings.unmarshal("boolean", "0", null));
   }

   public void testIntegerUnmarshalling() throws Exception
   {
      assertEquals(new BigInteger("-1"), SimpleTypeBindings.unmarshal("integer", "-1", null));
      assertEquals(new BigInteger("0"), SimpleTypeBindings.unmarshal("integer", "0", null));
      assertEquals(new BigInteger("12678967543233"), SimpleTypeBindings.unmarshal("integer", "12678967543233", null));
   }

   public void testDecimalUnmarshalling() throws Exception
   {
      assertEquals(new BigDecimal("-1.23"), SimpleTypeBindings.unmarshal("decimal", "-1.23", null));
      assertEquals(new BigDecimal("12678967.543233"),
         SimpleTypeBindings.unmarshal("decimal", "12678967.543233", null)
      );
   }

   public void testAnyUriUnmarshalling() throws Exception
   {
      assertEquals(new URI("http://jboss.org"), SimpleTypeBindings.unmarshal("anyURI", "http://jboss.org", null));
   }

   public void testUnsignedLongUnmarshalling() throws Exception
   {
      assertEquals(new BigInteger("18446744073709551615"),
         SimpleTypeBindings.unmarshal("unsignedLong", "18446744073709551615", null)
      );
   }

   public void testUnsignedIntUnmarshalling() throws Exception
   {
      assertEquals(new Long(4294967295L), SimpleTypeBindings.unmarshal("unsignedInt", "4294967295", null));
   }

   public void testUnsignedShortUnmarshalling() throws Exception
   {
      assertEquals(new Integer(65535), SimpleTypeBindings.unmarshal("unsignedShort", "65535", null));
   }

   public void testUnsignedByteUnmarshalling() throws Exception
   {
      assertEquals(Short.valueOf("255"), SimpleTypeBindings.unmarshal("unsignedByte", "255", null));
   }

   public void testNonPositiveIntegerUnmarshalling() throws Exception
   {
      assertEquals(new BigInteger("0"), SimpleTypeBindings.unmarshal("nonPositiveInteger", "0", null));
      assertEquals(new BigInteger("-12678967543233"),
         SimpleTypeBindings.unmarshal("nonPositiveInteger", "-12678967543233", null)
      );
   }

   public void testPositiveIntegerUnmarshalling() throws Exception
   {
      assertEquals(new BigInteger("12678967543233"),
         SimpleTypeBindings.unmarshal("positiveInteger", "12678967543233", null)
      );
   }

   public void testNonNegativeIntegerUnmarshalling() throws Exception
   {
      assertEquals(new BigInteger("0"), SimpleTypeBindings.unmarshal("nonNegativeInteger", "0", null));
      assertEquals(new BigInteger("12678967543233"),
         SimpleTypeBindings.unmarshal("nonNegativeInteger", "12678967543233", null)
      );
   }

   public void testNegativeIntegerUnmarshalling() throws Exception
   {
      assertEquals(new BigInteger("-12678967543233"),
         SimpleTypeBindings.unmarshal("negativeInteger", "-12678967543233", null)
      );
   }

   public void testNMTokensUnmarshalling() throws Exception
   {
      String[] tokens = SimpleTypeBindings.unmarshalNMTokens("1\n2\n3\n");
      assertEquals(3, tokens.length);
      assertEquals("1", tokens[0]);
      assertEquals("2", tokens[1]);
      assertEquals("3", tokens[2]);
   }

   public void testGMonthDayUnmarshalling() throws Exception
   {
      Calendar cal = SimpleTypeBindings.unmarshalGMonthDay("--12-31-02:00");
      assertEquals(1970, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
      assertEquals("GMT-02:00", cal.getTimeZone().getDisplayName());

      cal = SimpleTypeBindings.unmarshalGMonthDay("--12-31");
      assertEquals(1970, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
   }

   public void testGMonthUnmarshalling() throws Exception
   {
      Calendar cal = SimpleTypeBindings.unmarshalGMonth("--12+03:00");
      assertEquals(1970, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
      assertEquals("GMT+03:00", cal.getTimeZone().getDisplayName());

      cal = SimpleTypeBindings.unmarshalGMonth("--12");
      assertEquals(1970, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
   }

   public void testGYearUnmarshalling() throws Exception
   {
      Calendar cal = SimpleTypeBindings.unmarshalGYear("1997-03:00");
      assertEquals(1997, cal.get(Calendar.YEAR));
      assertEquals(0, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
      assertEquals("GMT-03:00", cal.getTimeZone().getDisplayName());

      cal = SimpleTypeBindings.unmarshalGYear("1997");
      assertEquals(1997, cal.get(Calendar.YEAR));
      assertEquals(0, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
   }

   public void testGYearMonthUnmarshalling() throws Exception
   {
      Calendar cal = SimpleTypeBindings.unmarshalGYearMonth("1995-04+01:00");
      assertEquals(1995, cal.get(Calendar.YEAR));
      assertEquals(3, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
      assertEquals("GMT+01:00", cal.getTimeZone().getDisplayName());

      cal = SimpleTypeBindings.unmarshalGYearMonth("1995-04");
      assertEquals(1995, cal.get(Calendar.YEAR));
      assertEquals(3, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
   }

   public void testDatetimeUnmarshalling() throws Exception
   {
      Calendar cal = SimpleTypeBindings.unmarshalDateTime("1980-02-19T03:22:17.333+02:00");
      assertEquals(1980, cal.get(Calendar.YEAR));
      assertEquals(1, cal.get(Calendar.MONTH));
      assertEquals(19, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(3, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(22, cal.get(Calendar.MINUTE));
      assertEquals(17, cal.get(Calendar.SECOND));
      assertEquals(333, cal.get(Calendar.MILLISECOND));
      assertEquals("GMT+02:00", cal.getTimeZone().getDisplayName());
   }

   public void testTimeUnmarshalling() throws Exception
   {
      String value = "23:32:28.123+01:00";
      Calendar cal = SimpleTypeBindings.unmarshalTime(value);
      assertEquals(1970, cal.get(Calendar.YEAR));
      assertEquals(0, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals("GMT+01:00", cal.getTimeZone().getDisplayName());
      assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(32, cal.get(Calendar.MINUTE));
      assertEquals(28, cal.get(Calendar.SECOND));
      assertEquals(123, cal.get(Calendar.MILLISECOND));

      value = "23:32:28.123+01:00";
      cal = SimpleTypeBindings.unmarshalTime(value);
      assertEquals(1970, cal.get(Calendar.YEAR));
      assertEquals(0, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(32, cal.get(Calendar.MINUTE));
      assertEquals(28, cal.get(Calendar.SECOND));
      assertEquals(123, cal.get(Calendar.MILLISECOND));
   }

   public void testDateUnmarshalling() throws Exception
   {
      String value = "2004-12-01+01:00";
      Calendar cal = SimpleTypeBindings.unmarshalDate(value);
      assertEquals(2004, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals("GMT+01:00", cal.getTimeZone().getDisplayName());
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));

      value = "2004-12-01";
      cal = SimpleTypeBindings.unmarshalDate(value);
      assertEquals(2004, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));

      value = "-2004-12-01+01:00";
      cal = SimpleTypeBindings.unmarshalDate(value);
      assertEquals(2004, cal.get(Calendar.YEAR));
      assertEquals(11, cal.get(Calendar.MONTH));
      assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      assertEquals("GMT+01:00", cal.getTimeZone().getDisplayName());
      assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
      assertEquals(0, cal.get(Calendar.MINUTE));
      assertEquals(0, cal.get(Calendar.SECOND));
      assertEquals(0, cal.get(Calendar.MILLISECOND));
   }

   public void testDateMarshalling() throws Exception
   {
      Calendar c = new GregorianCalendar(6,5,1,10,0,0);
      c.setTimeZone(TimeZone.getTimeZone("GMT"));
      String marshalled = SimpleTypeBindings.marshalDate(c);
      assertEquals("0006-06-01Z", marshalled);
   }
   
   public void testHexBinary() throws Exception
   {
      String s = "kloop";
      String marshalled = SimpleTypeBindings.marshal("hexBinary", s.getBytes(), null);
      byte[] bytes = (byte[])SimpleTypeBindings.unmarshal("hexBinary", marshalled, null);
      String unmarshalled = new String(bytes);
      assertEquals(s, unmarshalled);
   }

   public void testQName() throws Exception
   {
      final String ns = "http://jboss.org/test/simple/bindings";
      String local = "test1";
      final String prefix = "p1";
      QName qName = new QName(ns, local, prefix);

      NamespaceContext nsCtx = new NamespaceContext()
      {
         public String getNamespaceURI(String p)
         {
            if(!prefix.equals(p))
            {
               throw new IllegalStateException("The only supported prefix is " + prefix + " but got " + p);
            }
            return ns;
         }

         public String getPrefix(String namespaceURI)
         {
            if(!ns.equals(namespaceURI))
            {
               throw new IllegalStateException("The only supported uri is " + ns + " but got " + namespaceURI);
            }
            return prefix;
         }

         public Iterator getPrefixes(String namespaceURI)
         {
            if(!ns.equals(namespaceURI))
            {
               throw new IllegalStateException("The only supported uri is " + ns + " but got " + namespaceURI);
            }
            return Collections.singletonList(prefix).iterator();
         }
      };

      String marshalled = SimpleTypeBindings.marshal(SimpleTypeBindings.XS_QNAME_NAME, qName, nsCtx);
      assertEquals(prefix + ":" + local, marshalled);

      QName unmarshalled = (QName)SimpleTypeBindings.unmarshal(SimpleTypeBindings.XS_QNAME_NAME, marshalled, nsCtx);

      assertEquals(qName.getPrefix(), unmarshalled.getPrefix());
      assertEquals(qName.getNamespaceURI(), unmarshalled.getNamespaceURI());
      assertEquals(qName.getLocalPart(), unmarshalled.getLocalPart());
   }

   public void testBooleanListUnmarshalling() throws Exception
   {
      List booleans = SimpleTypeBindings.unmarshalList(
         SimpleTypeBindings.XS_BOOLEAN_NAME, " 1 1 0  true true false  ", null
      );
      assertNotNull(booleans);
      assertEquals(6, booleans.size());

      int i = 0;
      Boolean item = (Boolean)booleans.get(i++);
      assertNotNull(item);
      assertTrue(item.booleanValue());
      item = (Boolean)booleans.get(i++);
      assertNotNull(item);
      assertTrue(item.booleanValue());
      item = (Boolean)booleans.get(i++);
      assertNotNull(item);
      assertFalse(item.booleanValue());
      item = (Boolean)booleans.get(i++);
      assertNotNull(item);
      assertTrue(item.booleanValue());
      item = (Boolean)booleans.get(i++);
      assertNotNull(item);
      assertTrue(item.booleanValue());
      item = (Boolean)booleans.get(i++);
      assertNotNull(item);
      assertFalse(item.booleanValue());
   }

   public void testBooleanListMarshalling() throws Exception
   {
      String marshalled = SimpleTypeBindings.marshalList(
         SimpleTypeBindings.XS_BOOLEAN_NAME,
         Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE}),
         null
      );
      assertNotNull(marshalled);
      assertEquals("true true false true", marshalled);
   }
   
   public void testBase64BinaryUnmarshalling() throws Exception
   {
      byte[] unmarshalled = (byte[]) SimpleTypeBindings.unmarshal("base64Binary", "VGVzdCBNZXNzYWdl", null);

      assertEquals("Test Message", new String(unmarshalled));
   }

   public void testBase64BinaryGZippedUnmarshalling() throws Exception
   {
      // GZipped content should NOT be automatically unzipped.
      byte[] unmarshalled = (byte[]) SimpleTypeBindings.unmarshal("base64Binary",
            "H4sIAAAAAAAAAAtJLS5R8E0tLk5MTwUA74UAyAwAAAA=", null);

      assertEquals(32, unmarshalled.length);
      assertFalse("Test Message".equals(new String(unmarshalled)));
   }

   public void testBase64BinaryMarshalling() throws Exception
   {
      String marshalled = SimpleTypeBindings.marshal("base64Binary", "Test Message".getBytes(), null);

      assertEquals("VGVzdCBNZXNzYWdl", marshalled);
   }   
}
