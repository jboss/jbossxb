/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import org.jboss.logging.Logger;
import org.jboss.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @author Thomas.Diesler@jboss.org
 * @version <tt>$Revision$</tt>
 */
public final class SimpleTypeBindings
   implements Serializable
{
   static final long serialVersionUID = 4372272109355825813L;

   public static final String XS_ANYSIMPLETYPE_NAME = "anySimpleType";

   //
   // primitive datatypes
   //
   public static final String XS_STRING_NAME = "string";
   public static final String XS_BOOLEAN_NAME = "boolean";
   public static final String XS_DECIMAL_NAME = "decimal";
   public static final String XS_FLOAT_NAME = "float";
   public static final String XS_DOUBLE_NAME = "double";
   public static final String XS_DURATION_NAME = "duration";
   public static final String XS_DATETIME_NAME = "dateTime";
   public static final String XS_TIME_NAME = "time";
   public static final String XS_DATE_NAME = "date";
   public static final String XS_GYEARMONTH_NAME = "gYearMonth";
   public static final String XS_GYEAR_NAME = "gYear";
   public static final String XS_GMONTHDAY_NAME = "gMonthDay";
   public static final String XS_GDAY_NAME = "gDay";
   public static final String XS_GMONTH_NAME = "gMonth";
   public static final String XS_HEXBINARY_NAME = "hexBinary";
   public static final String XS_BASE64BINARY_NAME = "base64Binary";
   public static final String XS_ANYURI_NAME = "anyURI";
   public static final String XS_QNAME_NAME = "QName";
   public static final String XS_NOTATION_NAME = "NOTATION";

   //
   // derived datatypes
   //

   public static final String XS_NORMALIZEDSTRING_NAME = "normalizedString";
   public static final String XS_TOKEN_NAME = "token";
   public static final String XS_LANGUAGE_NAME = "language";
   public static final String XS_NMTOKEN_NAME = "NMTOKEN";
   public static final String XS_NMTOKENS_NAME = "NMTOKENS";
   public static final String XS_NAME_NAME = "Name";
   public static final String XS_NCNAME_NAME = "NCName";
   public static final String XS_ID_NAME = "ID";
   public static final String XS_IDREF_NAME = "IDREF";
   public static final String XS_IDREFS_NAME = "IDREFS";
   public static final String XS_ENTITY_NAME = "ENTITY";
   public static final String XS_ENTITIES_NAME = "ENTITIES";
   public static final String XS_INTEGER_NAME = "integer";
   public static final String XS_NONPOSITIVEINTEGER_NAME = "nonPositiveInteger";
   public static final String XS_NEGATIVEINTEGER_NAME = "negativeInteger";
   public static final String XS_LONG_NAME = "long";
   public static final String XS_INT_NAME = "int";
   public static final String XS_SHORT_NAME = "short";
   public static final String XS_BYTE_NAME = "byte";
   public static final String XS_NONNEGATIVEINTEGER_NAME = "nonNegativeInteger";
   public static final String XS_UNSIGNEDLONG_NAME = "unsignedLong";
   public static final String XS_UNSIGNEDINT_NAME = "unsignedInt";
   public static final String XS_UNSIGNEDSHORT_NAME = "unsignedShort";
   public static final String XS_UNSIGNEDBYTE_NAME = "unsignedByte";
   public static final String XS_POSITIVEINTEGER_NAME = "positiveInteger";

   public static final int XS_INT = XS_INT_NAME.hashCode();
   public static final int XS_LONG = XS_LONG_NAME.hashCode();
   public static final int XS_SHORT = XS_SHORT_NAME.hashCode();
   public static final int XS_FLOAT = XS_FLOAT_NAME.hashCode();
   public static final int XS_DOUBLE = XS_DOUBLE_NAME.hashCode();
   public static final int XS_BOOLEAN = XS_BOOLEAN_NAME.hashCode();
   public static final int XS_BYTE = XS_BYTE_NAME.hashCode();
   public static final int XS_STRING = XS_STRING_NAME.hashCode();
   public static final int XS_INTEGER = XS_INTEGER_NAME.hashCode();
   public static final int XS_DECIMAL = XS_DECIMAL_NAME.hashCode();
   public static final int XS_DATETIME = XS_DATETIME_NAME.hashCode();
   public static final int XS_QNAME = XS_QNAME_NAME.hashCode();
   public static final int XS_ANYURI = XS_ANYURI_NAME.hashCode();
   public static final int XS_UNSIGNEDLONG = XS_UNSIGNEDLONG_NAME.hashCode();
   public static final int XS_UNSIGNEDINT = XS_UNSIGNEDINT_NAME.hashCode();
   public static final int XS_UNSIGNEDSHORT = XS_UNSIGNEDSHORT_NAME.hashCode();
   public static final int XS_UNSIGNEDBYTE = XS_UNSIGNEDBYTE_NAME.hashCode();
   public static final int XS_DATE = XS_DATE_NAME.hashCode();
   public static final int XS_TIME = XS_TIME_NAME.hashCode();
   public static final int XS_BASE64BINARY = XS_BASE64BINARY_NAME.hashCode();
   public static final int XS_HEXBINARY = XS_HEXBINARY_NAME.hashCode();
   public static final int XS_ANYSIMPLETYPE = XS_ANYSIMPLETYPE_NAME.hashCode();
   public static final int XS_DURATION = XS_DURATION_NAME.hashCode();
   public static final int XS_GYEARMONTH = XS_GYEARMONTH_NAME.hashCode();
   public static final int XS_GYEAR = XS_GYEAR_NAME.hashCode();
   public static final int XS_GMONTHDAY = XS_GMONTHDAY_NAME.hashCode();
   public static final int XS_GMONTH = XS_GMONTH_NAME.hashCode();
   public static final int XS_GDAY = XS_GDAY_NAME.hashCode();
   public static final int XS_NORMALIZEDSTRING = XS_NORMALIZEDSTRING_NAME.hashCode();
   public static final int XS_TOKEN = XS_TOKEN_NAME.hashCode();
   public static final int XS_LANGUAGE = XS_LANGUAGE_NAME.hashCode();
   public static final int XS_NAME = XS_NAME_NAME.hashCode();
   public static final int XS_NCNAME = XS_NCNAME_NAME.hashCode();
   public static final int XS_ID = XS_ID_NAME.hashCode();
   public static final int XS_NMTOKEN = XS_NMTOKEN_NAME.hashCode();
   public static final int XS_NMTOKENS = XS_NMTOKENS_NAME.hashCode();
   public static final int XS_NONPOSITIVEINTEGER = XS_NONPOSITIVEINTEGER_NAME.hashCode();
   public static final int XS_NEGATIVEINTEGER = XS_NEGATIVEINTEGER_NAME.hashCode();
   public static final int XS_NONNEGATIVEINTEGER = XS_NONNEGATIVEINTEGER_NAME.hashCode();
   public static final int XS_POSITIVEINTEGER = XS_POSITIVEINTEGER_NAME.hashCode();
   public static final int XS_NOTATION = XS_NOTATION_NAME.hashCode();
   public static final int XS_IDREF = XS_IDREF_NAME.hashCode();
   public static final int XS_IDREFS = XS_IDREFS_NAME.hashCode();
   public static final int XS_ENTITY = XS_ENTITY_NAME.hashCode();
   public static final int XS_ENTITIES = XS_ENTITIES_NAME.hashCode();

   public static final TypeBinding STRING = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return value;
      }

      public String marshal(Object value)
      {
         return (String)value;
      }
   };

   public static final TypeBinding INT = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return Integer.valueOf(value);
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding LONG = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return Long.valueOf(value);
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding DOUBLE = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return Double.valueOf(value);
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding FLOAT = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return Float.valueOf(value);
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding SHORT = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return Short.valueOf(value);
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding BYTE = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return Byte.valueOf(value);
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding CHAR = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return value == null ? null : new Character(value.charAt(0));
      }

      public String marshal(Object value)
      {
         return String.valueOf(value);
      }
   };

   public static final TypeBinding JAVA_UTIL_DATE = new TypeBinding()
   {
      public Object unmarshal(String value)
      {
         return unmarshalDate(value).getTime();
      }

      public String marshal(Object value)
      {
         Calendar c = Calendar.getInstance();
         c.setTime((java.util.Date)value);
         return marshalDate(c);
      }
   };

   // check for uniqueness of hashCode's
   static
   {
      int[] codes = new int[45];
      String[] names = new String[codes.length];
      int i = 0;

      names[i] = XS_INT_NAME;
      codes[i++] = XS_INT;

      names[i] = XS_LONG_NAME;
      codes[i++] = XS_LONG;

      names[i] = XS_SHORT_NAME;
      codes[i++] = XS_SHORT;

      names[i] = XS_FLOAT_NAME;
      codes[i++] = XS_FLOAT;

      names[i] = XS_DOUBLE_NAME;
      codes[i++] = XS_DOUBLE;

      names[i] = XS_BOOLEAN_NAME;
      codes[i++] = XS_BOOLEAN;

      names[i] = XS_BYTE_NAME;
      codes[i++] = XS_BYTE;

      names[i] = XS_STRING_NAME;
      codes[i++] = XS_STRING;

      names[i] = XS_INTEGER_NAME;
      codes[i++] = XS_INTEGER;

      names[i] = XS_DECIMAL_NAME;
      codes[i++] = XS_DECIMAL;

      names[i] = XS_DATETIME_NAME;
      codes[i++] = XS_DATETIME;

      names[i] = XS_QNAME_NAME;
      codes[i++] = XS_QNAME;

      names[i] = XS_ANYURI_NAME;
      codes[i++] = XS_ANYURI;

      names[i] = XS_UNSIGNEDINT_NAME;
      codes[i++] = XS_UNSIGNEDINT;

      names[i] = XS_UNSIGNEDSHORT_NAME;
      codes[i++] = XS_UNSIGNEDSHORT;

      names[i] = XS_UNSIGNEDBYTE_NAME;
      codes[i++] = XS_UNSIGNEDBYTE;

      names[i] = XS_DATE_NAME;
      codes[i++] = XS_DATE;

      names[i] = XS_TIME_NAME;
      codes[i++] = XS_TIME;

      names[i] = XS_BASE64BINARY_NAME;
      codes[i++] = XS_BASE64BINARY;

      names[i] = XS_HEXBINARY_NAME;
      codes[i++] = XS_HEXBINARY;

      names[i] = XS_ANYSIMPLETYPE_NAME;
      codes[i++] = XS_ANYSIMPLETYPE;

      names[i] = XS_DURATION_NAME;
      codes[i++] = XS_DURATION;

      names[i] = XS_GYEARMONTH_NAME;
      codes[i++] = XS_GYEARMONTH;

      names[i] = XS_GYEAR_NAME;
      codes[i++] = XS_GYEAR;

      names[i] = XS_GMONTHDAY_NAME;
      codes[i++] = XS_GMONTHDAY;

      names[i] = XS_GMONTH_NAME;
      codes[i++] = XS_GMONTH;

      names[i] = XS_GDAY_NAME;
      codes[i++] = XS_GDAY;

      names[i] = XS_NORMALIZEDSTRING_NAME;
      codes[i++] = XS_NORMALIZEDSTRING;

      names[i] = XS_TOKEN_NAME;
      codes[i++] = XS_TOKEN;

      names[i] = XS_LANGUAGE_NAME;
      codes[i++] = XS_LANGUAGE;

      names[i] = XS_NAME_NAME;
      codes[i++] = XS_NAME;

      names[i] = XS_NCNAME_NAME;
      codes[i++] = XS_NCNAME;

      names[i] = XS_ID_NAME;
      codes[i++] = XS_ID;

      names[i] = XS_NMTOKEN_NAME;
      codes[i++] = XS_NMTOKEN;

      names[i] = XS_NMTOKENS_NAME;
      codes[i++] = XS_NMTOKENS;

      names[i] = XS_NONPOSITIVEINTEGER_NAME;
      codes[i++] = XS_NONPOSITIVEINTEGER;

      names[i] = XS_NONNEGATIVEINTEGER_NAME;
      codes[i++] = XS_NONNEGATIVEINTEGER;

      names[i] = XS_POSITIVEINTEGER_NAME;
      codes[i++] = XS_POSITIVEINTEGER;

      names[i] = XS_NEGATIVEINTEGER_NAME;
      codes[i++] = XS_NEGATIVEINTEGER;

      names[i] = XS_UNSIGNEDLONG_NAME;
      codes[i++] = XS_UNSIGNEDLONG;

      names[i] = XS_NOTATION_NAME;
      codes[i++] = XS_NOTATION;

      names[i] = XS_IDREF_NAME;
      codes[i++] = XS_IDREF;

      names[i] = XS_IDREFS_NAME;
      codes[i++] = XS_IDREFS;

      names[i] = XS_ENTITY_NAME;
      codes[i++] = XS_ENTITY;

      names[i] = XS_ENTITIES_NAME;
      codes[i++] = XS_ENTITIES;

      Logger log = Logger.getLogger(SimpleTypeBindings.class);
      boolean allAreUnique = true;
      for(int outer = 0; outer < names.length; ++outer)
      {
         int outerCode = codes[outer];
         String outerName = names[outer];

         for(int inner = outer + 1; inner < names.length; ++inner)
         {
            int innerCode = codes[inner];
            String innerName = names[inner];

            if(outerCode == innerCode)
            {
               log.error("Types have the same hash code " + outerCode + ": " + outerName + " and " + innerName);
               allAreUnique = false;
            }
         }
      }

      if(!allAreUnique)
      {
         throw new IllegalStateException("Not all the schema types have unique hash codes! See log for more details.");
      }
   }

   public static Class classForType(String xsdType, boolean nillable)
   {
      Class result;
      int typeCode = xsdType.hashCode();
      if(typeCode == XS_INT)
      {
         result = nillable ? Integer.class : int.class;
      }
      else if(typeCode == XS_LONG)
      {
         result = nillable ? Long.class : long.class;
      }
      else if(typeCode == XS_SHORT)
      {
         result = nillable ? Short.class : short.class;
      }
      else if(typeCode == XS_BYTE)
      {
         result = nillable ? Byte.class : byte.class;
      }
      else if(typeCode == XS_FLOAT)
      {
         result = nillable ? Float.class : float.class;
      }
      else if(typeCode == XS_DOUBLE)
      {
         result = nillable ? Double.class : double.class;
      }
      else if(typeCode == XS_BOOLEAN)
      {
         result = nillable ? Boolean.class : boolean.class;
      }
      else if(typeCode == XS_STRING)
      {
         result = String.class;
      }
      else if(typeCode == XS_INTEGER)
      {
         result = BigInteger.class;
      }
      else if(typeCode == XS_DECIMAL)
      {
         result = BigDecimal.class;
      }
      else if(typeCode == XS_DATETIME)
      {
         result = java.util.Calendar.class;
      }
      else if(typeCode == XS_QNAME)
      {
         result = QName.class;
      }
      else if(typeCode == XS_ANYURI)
      {
         // anyUri is by default bound to java.net.URI for now. The following is the warning from JAXB2.0:
         //
         // Design Note � xs:anyURI is not bound to java.net.URI by default since not all
         // possible values of xs:anyURI can be passed to the java.net.URI constructor. Using
         // a global JAXB customization described in Section 7.9, �<javaType>
         // Declaration", a JAXB user can override the default mapping to map xs:anyURI to
         // java.net.URI.
         //
         result = java.net.URI.class;
      }
      else if(typeCode == XS_UNSIGNEDLONG)
      {
         result = BigInteger.class;
      }
      else if(typeCode == XS_UNSIGNEDINT)
      {
         result = nillable ? Long.class : long.class;
      }
      else if(typeCode == XS_UNSIGNEDSHORT)
      {
         result = nillable ? Integer.class : int.class;
      }
      else if(typeCode == XS_UNSIGNEDBYTE)
      {
         result = nillable ? Short.class : short.class;
      }
      else if(typeCode == XS_DATE)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_TIME)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_BASE64BINARY)
      {
         result = byte[].class;
      }
      else if(typeCode == XS_HEXBINARY)
      {
         result = byte[].class;
      }
      else if(typeCode == XS_ANYSIMPLETYPE)
      {
         result = String.class;
      }
      else if(typeCode == XS_DURATION)
      {
         // todo XS_DURATION
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_DURATION_NAME);
      }
      else if(typeCode == XS_GYEARMONTH)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_GYEAR)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_GMONTHDAY)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_GMONTH)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_GDAY)
      {
         result = Calendar.class;
      }
      else if(typeCode == XS_NORMALIZEDSTRING)
      {
         result = String.class;
      }
      else if(typeCode == XS_TOKEN)
      {
         result = String.class;
      }
      else if(typeCode == XS_LANGUAGE)
      {
         result = String.class;
      }
      else if(typeCode == XS_NAME)
      {
         result = String.class;
      }
      else if(typeCode == XS_NCNAME)
      {
         result = String.class;
      }
      else if(typeCode == XS_ID)
      {
         result = String.class;
      }
      else if(typeCode == XS_NMTOKEN)
      {
         result = String.class;
      }
      else if(typeCode == XS_NMTOKENS)
      {
         result = String[].class;
      }
      else if(typeCode == XS_NONPOSITIVEINTEGER)
      {
         result = BigInteger.class;
      }
      else if(typeCode == XS_NEGATIVEINTEGER)
      {
         result = BigInteger.class;
      }
      else if(typeCode == XS_NONNEGATIVEINTEGER)
      {
         result = BigInteger.class;
      }
      else if(typeCode == XS_POSITIVEINTEGER)
      {
         result = BigInteger.class;
      }
      else if(typeCode == XS_NOTATION)
      {
         result = String.class;
      }
      else if(typeCode == XS_IDREF)
      {
         result = String.class;
      }
      else if(typeCode == XS_IDREFS)
      {
         result = String[].class;
      }
      else if(typeCode == XS_ENTITY)
      {
         result = String.class;
      }
      else if(typeCode == XS_ENTITIES)
      {
         result = String[].class;
      }
      else
      {
         throw new IllegalStateException("Not supported xsdType: " + xsdType + ", hashCode=" + xsdType.hashCode());
      }
      return result;
   }

   public static Object unmarshal(String xsdType, String value, NamespaceContext nsCtx)
   {
      if (xsdType == null)
         throw new IllegalArgumentException("Schema type cannot be null");
      if (value == null)
         throw new IllegalArgumentException("Value string cannot be null");

      int typeCode = xsdType.hashCode();
      Object result;
      if(typeCode == XS_INT)
      {
         result = Integer.valueOf(value);
      }
      else if(typeCode == XS_LONG)
      {
         result = Long.valueOf(value);
      }
      else if(typeCode == XS_SHORT)
      {
         result = Short.valueOf(value);
      }
      else if(typeCode == XS_BYTE)
      {
         result = Byte.valueOf(value);
      }
      else if(typeCode == XS_FLOAT)
      {
         if("INF".equals(value))
         {
            result = new Float(Float.POSITIVE_INFINITY);
         }
         else if("-INF".equals(value))
         {
            result = new Float(Float.NEGATIVE_INFINITY);
         }
         else
         {
            result = Float.valueOf(value);
         }
      }
      else if(typeCode == XS_DOUBLE)
      {
         if("INF".equals(value))
         {
            result = new Double(Double.POSITIVE_INFINITY);
         }
         else if("-INF".equals(value))
         {
            result = new Double(Double.NEGATIVE_INFINITY);
         }
         else
         {
            result = Double.valueOf(value);
         }
      }
      else if(typeCode == XS_BOOLEAN)
      {
         if(value.length() == 1)
         {
            switch(value.charAt(0))
            {
               case '1':
                  result = Boolean.TRUE;
                  break;
               case '0':
                  result = Boolean.FALSE;
                  break;
               default:
                  throw new JBossXBValueFormatException("An instance of a datatype that is defined as ?boolean? can have the following legal literals" +
                     " {true, false, 1, 0}. But got: " + value
                  );
            }
         }
         else
         {
            result = Boolean.valueOf(value);
         }
      }
      else if(typeCode == XS_STRING)
      {
         result = value;
      }
      else if(typeCode == XS_INTEGER)
      {
         result = new BigInteger(value);
      }
      else if(typeCode == XS_DECIMAL)
      {
         result = new BigDecimal(value);
      }
      else if(typeCode == XS_DATETIME)
      {
         result = unmarshalDateTime(value);
      }
      else if(typeCode == XS_QNAME)
      {
         result = unmarshalQName(value, nsCtx);
      }
      else if(typeCode == XS_ANYURI)
      {
         // anyUri is by default bound to java.net.URI for now. The following is the warning from JAXB2.0:
         //
         // Design Note � xs:anyURI is not bound to java.net.URI by default since not all
         // possible values of xs:anyURI can be passed to the java.net.URI constructor. Using
         // a global JAXB customization described in Section 7.9, �<javaType>
         // Declaration", a JAXB user can override the default mapping to map xs:anyURI to
         // java.net.URI.
         //
         try
         {
            result = new java.net.URI(value);
         }
         catch(URISyntaxException e)
         {
            throw new JBossXBValueFormatException("Failed to unmarshal anyURI value " + value, e);
         }
      }
      else if(typeCode == XS_UNSIGNEDLONG)
      {
         BigInteger d = new BigInteger(value);
         if(d.doubleValue() < 0 || d.doubleValue() > 18446744073709551615D)
         {
            throw new JBossXBValueFormatException("Invalid unsignedLong value: " + value);
         }
         result = d;
      }
      else if(typeCode == XS_UNSIGNEDINT)
      {
         long l = Long.parseLong(value);
         if(l < 0 || l > 4294967295L)
         {
            throw new JBossXBValueFormatException("Invalid unsignedInt value: " + value);
         }
         result = new Long(l);
      }
      else if(typeCode == XS_UNSIGNEDSHORT)
      {
         int i = Integer.parseInt(value);
         if(i < 0 || i > 65535)
         {
            throw new JBossXBValueFormatException("Invalid unsignedShort value: " + value);
         }
         result = new Integer(i);
      }
      else if(typeCode == XS_UNSIGNEDBYTE)
      {
         short s = Short.parseShort(value);
         if(s < 0 || s > 255)
         {
            throw new JBossXBValueFormatException("Invalid unsignedByte value: " + value);
         }
         result = new Short(s);
      }
      else if(typeCode == XS_DATE)
      {
         result = unmarshalDate(value);
      }
      else if(typeCode == XS_TIME)
      {
         result = unmarshalTime(value);
      }
      else if(typeCode == XS_BASE64BINARY)
      {
         result = unmarshalBase64(value);
      }
      else if(typeCode == XS_HEXBINARY)
      {
         result = unmarshalHexBinary(value);
      }
      else if(typeCode == XS_ANYSIMPLETYPE)
      {
         result = value;
      }
      else if(typeCode == XS_DURATION)
      {
         // todo XS_DURATION
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_DURATION_NAME);
      }
      else if(typeCode == XS_GYEARMONTH)
      {
         result = unmarshalGYearMonth(value);
      }
      else if(typeCode == XS_GYEAR)
      {
         result = unmarshalGYear(value);
      }
      else if(typeCode == XS_GMONTHDAY)
      {
         result = unmarshalGMonthDay(value);
      }
      else if(typeCode == XS_GMONTH)
      {
         return unmarshalGMonth(value);
      }
      else if(typeCode == XS_GDAY)
      {
         return unmarshalGDay(value);
      }
      else if(typeCode == XS_NORMALIZEDSTRING)
      {
         if(isNormalizedString(value))
         {
            result = value;
         }
         else
         {
            throw new JBossXBValueFormatException("Invalid normalizedString value: " + value);
         }
      }
      else if(typeCode == XS_TOKEN)
      {
         if(isValidToken(value))
         {
            result = value;
         }
         else
         {
            throw new JBossXBValueFormatException("Invalid token value: " + value);
         }
      }
      else if(typeCode == XS_LANGUAGE)
      {
         result = value;
      }
      else if(typeCode == XS_NAME)
      {
         result = value;
      }
      else if(typeCode == XS_NCNAME)
      {
         result = value;
      }
      else if(typeCode == XS_ID)
      {
         result = value;
      }
      else if(typeCode == XS_NMTOKEN)
      {
         result = value;
      }
      else if(typeCode == XS_NMTOKENS)
      {
         result = unmarshalNMTokens(value);
      }
      else if(typeCode == XS_NONPOSITIVEINTEGER)
      {
         result = new BigInteger(value);
         if(BigInteger.ZERO.compareTo((BigInteger)result) < 0)
         {
            throw new JBossXBValueFormatException("Invalid nonPositiveInteger value: " + value);
         }
      }
      else if(typeCode == XS_NEGATIVEINTEGER)
      {
         result = new BigInteger(value);
         if(BigInteger.ZERO.compareTo((BigInteger)result) <= 0)
         {
            throw new JBossXBValueFormatException("Invalid negativeInteger value: " + value);
         }
      }
      else if(typeCode == XS_NONNEGATIVEINTEGER)
      {
         result = new BigInteger(value);
         if(BigInteger.ZERO.compareTo((BigInteger)result) > 0)
         {
            throw new JBossXBValueFormatException("Invalid nonNegativeInteger value: " + value);
         }
      }
      else if(typeCode == XS_POSITIVEINTEGER)
      {
         result = new BigInteger(value);
         if(BigInteger.ZERO.compareTo((BigInteger)result) >= 0)
         {
            throw new JBossXBValueFormatException("Invalid positiveInteger value: " + value);
         }
      }
      else if(typeCode == XS_NOTATION)
      {
         // todo NOTATION
         result = value;
      }
      else if(typeCode == XS_IDREF)
      {
         result = value;
      }
      else if(typeCode == XS_IDREFS)
      {
         result = unmarshalIdRefs(value);
      }
      else if(typeCode == XS_ENTITY)
      {
         result = value;
      }
      else if(typeCode == XS_ENTITIES)
      {
         result = unmarshalIdRefs(value);
      }
      else
      {
         throw new IllegalStateException("Not supported xsdType: " + xsdType + ", hashCode=" + xsdType.hashCode());
      }
      return result;
   }

   public static Object unmarshal(String value, Class javaType)
   {
      Object result;
      if(String.class == javaType)
      {
         result = value;
      }
      else if(int.class == javaType || Integer.class == javaType)
      {
         result = Integer.valueOf(value);
      }
      else if(long.class == javaType || Long.class == javaType)
      {
         result = Long.valueOf(value);
      }
      else if(double.class == javaType || Double.class == javaType)
      {
         result = Double.valueOf(value);
      }
      else if(float.class == javaType || Float.class == javaType)
      {
         result = Float.valueOf(value);
      }
      else if(short.class == javaType || Short.class == javaType)
      {
         result = Short.valueOf(value);
      }
      else if(byte.class == javaType || Byte.class == javaType)
      {
         result = Byte.valueOf(value);
      }
      else if(char.class == javaType || Character.class == javaType)
      {
         result = new Character(value.charAt(0));
      }
      else if(java.util.Date.class == javaType)
      {
         final String FORMAT = "yyyy-MM-dd";
         try
         {
            result = new java.text.SimpleDateFormat(FORMAT).parse(value);
         }
         catch(ParseException e)
         {
            throw new JBossXBRuntimeException(
               "Failed to parse date accroding to " + FORMAT + " format: " + value + ": " + e.getMessage()
            );
         }
      }
      else if(Object.class == javaType)
      {
         result = value;
      }
      else
      {
         throw new JBossXBRuntimeException("Unexpected field type " + javaType);
      }

      return result;
   }

   public static String marshal(String xsdType, Object value, NamespaceContext nsCtx)
   {
      if(value == null)
      {
         throw new IllegalArgumentException("Can't marshal null value!");
      }

      int typeCode = xsdType.hashCode();
      String result;
      if(typeCode == XS_INT)
      {
         Integer i = (Integer)value;
         result = i.toString();
      }
      else if(typeCode == XS_LONG)
      {
         Long l = (Long)value;
         result = l.toString();
      }
      else if(typeCode == XS_SHORT)
      {
         Short s = (Short)value;
         result = s.toString();
      }
      else if(typeCode == XS_BYTE)
      {
         Byte b = (Byte)value;
         result = b.toString();
      }
      else if(typeCode == XS_FLOAT)
      {
         Float f = (Float)value;
         if(f.floatValue() == Float.POSITIVE_INFINITY)
         {
            result = "INF";
         }
         else if(f.floatValue() == Float.NEGATIVE_INFINITY)
         {
            result = "-INF";
         }
         else
         {
            result = f.toString();
         }
      }
      else if(typeCode == XS_DOUBLE)
      {
         Double d = (Double)value;
         if(d.doubleValue() == Double.POSITIVE_INFINITY)
         {
            result = "INF";
         }
         else if(d.doubleValue() == Double.NEGATIVE_INFINITY)
         {
            result = "-INF";
         }
         else
         {
            result = d.toString();
         }
      }
      else if(typeCode == XS_BOOLEAN)
      {
         if(value instanceof Boolean)
         {
            result = ((Boolean)value).booleanValue() ? "true" : "false";
         }
         else if(value instanceof Number)
         {
            Number n = (Number)value;
            switch(n.byteValue())
            {
               case 1:
                  result = "1";
                  break;
               case 0:
                  result = "0";
                  break;
               default:
                  throw new JBossXBValueFormatException("An instance of a datatype that is defined as ?boolean? can have the following legal literals" +
                     " {true, false, 1, 0}. But got: " + value
                  );
            }
         }
         else
         {
            throw new JBossXBValueFormatException("Java value for XSD boolean type expected to be an instance of java.lang.Boolean or java.lang.Number. But the value is of type " +
               value.getClass().getName()
            );
         }
      }
      else if(typeCode == XS_STRING)
      {
         result = (String)value;
      }
      else if(typeCode == XS_INTEGER)
      {
         BigInteger bi = (BigInteger)value;
         result = bi.toString();
      }
      else if(typeCode == XS_DECIMAL)
      {
         BigDecimal bd = (BigDecimal)value;
         result = bd.toString();
      }
      else if(typeCode == XS_DATETIME)
      {
         Calendar c;
         if(value.getClass() == java.util.Date.class)
         {
            c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c.clear();
            c.setTime((java.util.Date)value);
         }
         else
         {
            c = (Calendar)value;
         }
         result = marshalDateTime(c);
      }
      else if(typeCode == XS_QNAME)
      {
         QName qName = (QName)value;
         result = marshalQName(qName, nsCtx);
      }
      else if(typeCode == XS_ANYURI)
      {
         java.net.URI u = (java.net.URI)value;
         result = u.toString();
      }
      else if(typeCode == XS_UNSIGNEDLONG)
      {
         BigInteger d = (BigInteger)value;
         if (d.doubleValue() < 0 || d.doubleValue() > 18446744073709551615D)
         {
            throw new JBossXBValueFormatException("Invalid unsignedLong value: " + value);
         }
         result = d.toString();
      }
      else if(typeCode == XS_UNSIGNEDINT)
      {
         Long l = (Long)value;
         if(l.longValue() < 0 || l.longValue() > 4294967295L)
         {
            throw new JBossXBValueFormatException("Invalid unsignedInt value: " + value);
         }
         result = l.toString();
      }
      else if(typeCode == XS_UNSIGNEDSHORT)
      {
         Integer i = (Integer)value;
         if(i.intValue() < 0 || i.intValue() > 65535)
         {
            throw new JBossXBValueFormatException("Invalid unsignedShort value: " + value);
         }
         result = i.toString();
      }
      else if(typeCode == XS_UNSIGNEDBYTE)
      {
         Short s = (Short)value;
         if(s.shortValue() < 0 || s.shortValue() > 255)
         {
            throw new JBossXBValueFormatException("Invalid unsignedByte value: " + value);
         }
         result = s.toString();
      }
      else if(typeCode == XS_DATE)
      {
         Calendar c = (Calendar)value;
         result = marshalDate(c);
      }
      else if(typeCode == XS_TIME)
      {
         Calendar c = (Calendar)value;
         result = marshalTime(c);
      }
      else if(typeCode == XS_BASE64BINARY)
      {
         byte[] b = (byte[])value;
         result = marshalBase64(b);
      }
      else if(typeCode == XS_HEXBINARY)
      {
         byte[] b = (byte[])value;
         result = marshalHexBinary(b);
      }
      else if(typeCode == XS_ANYSIMPLETYPE)
      {
         return (String)value;
      }
      else if(typeCode == XS_DURATION)
      {
         // todo XS_DURATION
         throw new IllegalStateException("Recognized but not supported xsdType: " + xsdType);
      }
      else if(typeCode == XS_GYEARMONTH)
      {
         Calendar c = (Calendar)value;
         result = marshalGYearMonth(c);
      }
      else if(typeCode == XS_GYEAR)
      {
         Calendar c = (Calendar)value;
         result = marshalGYear(c);
      }
      else if(typeCode == XS_GMONTHDAY)
      {
         Calendar c = (Calendar)value;
         result = marshalGMonthDay(c);
      }
      else if(typeCode == XS_GMONTH)
      {
         Calendar c = (Calendar)value;
         result = marshalGMonth(c);
      }
      else if(typeCode == XS_GDAY)
      {
         Calendar c = (Calendar)value;
         result = marshalGDay(c);
      }
      else if(typeCode == XS_NORMALIZEDSTRING)
      {
         String s = (String)value;
         if(isNormalizedString(s))
         {
            result = s;
         }
         else
         {
            throw new JBossXBValueFormatException("Invalid normalizedString value: " + value);
         }
      }
      else if(typeCode == XS_TOKEN)
      {
         String s = (String)value;
         if(isValidToken(s))
         {
            result = s;
         }
         else
         {
            throw new JBossXBValueFormatException("Invalid token value: " + value);
         }
      }
      else if(typeCode == XS_LANGUAGE)
      {
         result = (String)value;
      }
      else if(typeCode == XS_NAME)
      {
         result = (String)value;
      }
      else if(typeCode == XS_NCNAME)
      {
         result = (String)value;
      }
      else if(typeCode == XS_ID)
      {
         result = (String)value;
      }
      else if(typeCode == XS_NMTOKEN)
      {
         result = (String)value;
      }
      else if(typeCode == XS_NMTOKENS)
      {
         String[] tokens = (String[])value;
         if(tokens.length > 0)
         {
            result = tokens[0];
            for(int i = 1; i < tokens.length; ++i)
            {
               result += ' ' + tokens[i];
            }
         }
         else
         {
            result = "";
         }
      }
      else if(typeCode == XS_NONPOSITIVEINTEGER)
      {
         BigInteger bi = (BigInteger)value;
         if(BigInteger.ZERO.compareTo(bi) < 0)
         {
            throw new JBossXBValueFormatException("Invalid nonPositiveInteger value: " + value);
         }
         result = bi.toString();
      }
      else if(typeCode == XS_NEGATIVEINTEGER)
      {
         BigInteger bi = (BigInteger)value;
         if(BigInteger.ZERO.compareTo(bi) <= 0)
         {
            throw new JBossXBValueFormatException("Invalid negativeInteger value: " + value);
         }
         result = bi.toString();
      }
      else if(typeCode == XS_NONNEGATIVEINTEGER)
      {
         BigInteger bi = (BigInteger)value;
         if(BigInteger.ZERO.compareTo(bi) > 0)
         {
            throw new JBossXBValueFormatException("Invalid nonNegativeInteger value: " + value);
         }
         result = bi.toString();
      }
      else if(typeCode == XS_POSITIVEINTEGER)
      {
         BigInteger bi = (BigInteger)value;
         if(BigInteger.ZERO.compareTo(bi) >= 0)
         {
            throw new JBossXBValueFormatException("Invalid positiveInteger value: " + value);
         }
         result = bi.toString();
      }
      else if(typeCode == XS_NOTATION)
      {
         // todo NOTATION
         result = (String)value;
      }
      else if(typeCode == XS_IDREF)
      {
         result = (String)value;
      }
      else if(typeCode == XS_IDREFS)
      {
         String[] refs = (String[])value;
         if(refs.length > 0)
         {
            result = refs[0];
            for(int i = 1; i < refs.length; ++i)
            {
               result += ' ' + refs[i];
            }
         }
         else
         {
            result = "";
         }
      }
      else if(typeCode == XS_ENTITY)
      {
         result = (String)value;
      }
      else if(typeCode == XS_ENTITIES)
      {
         String[] refs = (String[])value;
         if(refs.length > 0)
         {
            result = refs[0];
            for(int i = 1; i < refs.length; ++i)
            {
               result += ' ' + refs[i];
            }
         }
         else
         {
            result = "";
         }
      }
      else
      {
         throw new IllegalStateException("Not supported xsdType: " + xsdType + ", hashCode=" + xsdType.hashCode());
      }
      return result;
   }

   public static String[] unmarshalNMTokens(String value)
   {
      StringTokenizer tokenizer = new StringTokenizer(value);
      String[] tokens = new String[tokenizer.countTokens()];
      for(int i = 0; i < tokens.length; ++i)
      {
         tokens[i] = tokenizer.nextToken();
      }
      return tokens;
   }

   public static String[] unmarshalIdRefs(String value)
   {
      StringTokenizer tokenizer = new StringTokenizer(value);
      String[] tokens = new String[tokenizer.countTokens()];
      for(int i = 0; i < tokens.length; ++i)
      {
         tokens[i] = tokenizer.nextToken();
      }
      return tokens;
   }

   /**
    * --MM-DD[timezone]
    *
    * @param value
    * @return unmarshalled Calendar
    */
   public static Calendar unmarshalGMonthDay(String value)
   {
      if(value.length() < 6 ||
         value.charAt(0) != '-' ||
         value.charAt(1) != '-' ||
         value.charAt(4) != '-')
      {
         throw new JBossXBValueFormatException(
            "gMonthDay value does not follow the format '--MM-DD[timezone]: " + value
         );
      }

      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(Calendar.MONTH, Integer.parseInt(value.substring(2, 4)) - 1);
      cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(value.substring(5, 7)));
      if(value.length() > 7)
      {
         cal.setTimeZone(parseTimeZone(value, 7));
      }
      return cal;
   }

   /**
    * --MM-DD[timezone]
    *
    * @param value
    * @return
    */
   public static String marshalGMonthDay(Calendar value)
   {
      String result = "--";
      result += marshalInt(value.get(Calendar.MONTH) + 1, 2);
      result += '-';
      result += marshalInt(value.get(Calendar.DAY_OF_MONTH), 2);
      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * --MM[timezone]
    *
    * @param value
    * @return
    */
   public static Calendar unmarshalGMonth(String value)
   {
      if(value.length() < 4 || value.charAt(0) != '-' || value.charAt(1) != '-')
      {
         throw new JBossXBValueFormatException("gMonth value does not follow the format '--MM': " + value);
      }

      Calendar cal = Calendar.getInstance();
      cal.clear();

      cal.set(Calendar.MONTH, Integer.parseInt(value.substring(2, 4)) - 1);
      if(value.length() > 4)
      {
         cal.setTimeZone(parseTimeZone(value, 4));
      }
      return cal;
   }

   /**
    * --MM[timezone]
    *
    * @param value
    * @return
    */
   public static String marshalGMonth(Calendar value)
   {
      String result = "--";
      result += marshalInt(value.get(Calendar.MONTH) + 1, 2);
      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   public static Calendar unmarshalGYear(String value)
   {
      Calendar cal = Calendar.getInstance();
      cal.clear();
      int timeZone = parseGYear(value, 0, cal);
      if(value.length() > timeZone)
      {
         TimeZone tz = parseTimeZone(value, timeZone);
         cal.setTimeZone(tz);
      }
      return cal;
   }

   public static String marshalGYear(Calendar value)
   {
      String result = String.valueOf(value.get(Calendar.YEAR));
      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * Unmarshals gYearDate string following the format [-]CCYY-MM[timezone]
    *
    * @param value
    * @return
    */
   public static Calendar unmarshalGYearMonth(String value)
   {
      Calendar cal = Calendar.getInstance();
      cal.clear();

      int month = parseGYear(value, 0, cal);
      if(value.charAt(month) != '-')
      {
         throw new JBossXBValueFormatException(
            "gYearMonth value does not follow the format '[-]CCYY-MM[timezone]': " + value
         );
      }

      cal.set(Calendar.MONTH, Integer.parseInt(value.substring(month + 1, month + 3)) - 1);

      if(value.length() > month + 3)
      {
         TimeZone tz = parseTimeZone(value, month + 3);
         cal.setTimeZone(tz);
      }

      return cal;
   }

   /**
    * [-]CCYY-MM[timezone]
    *
    * @param value
    * @return
    */
   public static String marshalGYearMonth(Calendar value)
   {
      String result = String.valueOf(value.get(Calendar.YEAR));
      result += '-';
      result += marshalInt(value.get(Calendar.MONTH) + 1, 2);
      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * ---DD[timezonePart]
    *
    * @param value
    * @return
    */
   public static Calendar unmarshalGDay(String value)
   {
      if(value.length() < 5 || value.charAt(0) != '-' || value.charAt(1) != '-' || value.charAt(2) != '-')
      {
         throw new NumberFormatException("gDay value does not follow the format (---DD[timezonePart]): " + value);
      }

      // validate day
      int day = Integer.parseInt(value.substring(3, 5));
      if(day < 1 || day > 31)
      {
         throw new NumberFormatException("gDay value is not in the interval [1..31]: " + day);
      }

      // validate timezonePart
      TimeZone tz = parseTimeZone(value, 5);

      Calendar cal = Calendar.getInstance();
      cal.clear();
      if(tz != null)
      {
         cal.setTimeZone(tz);
      }
      cal.set(Calendar.DAY_OF_MONTH, day);

      return cal;
   }

   /**
    * ---DD[timezonePart]
    *
    * @param value
    * @return
    */
   public static String marshalGDay(Calendar value)
   {
      String result = "---";
      result += marshalInt(value.get(Calendar.DAY_OF_MONTH), 2);
      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * Parses a string value that represents date following the format defined in
    * http://www.w3.org/TR/xmlschema-2/#dateTime, i.e. '-'? yyyy '-' mm '-' dd.
    * Creates an instance of java.util.Calendar and initializes it to the parsed values of the year, month and day.
    *
    * @param value string date value
    * @return equivalent date as an instance of java.util.Calendar.
    */
   public static Calendar unmarshalDate(String value)
   {
      Calendar cal = Calendar.getInstance();
      cal.clear();

      int ind = parseDate(value, 0, cal);

      TimeZone tz = null;
      if(ind < value.length())
      {
         tz = parseTimeZone(value, ind);
      }

      if(tz != null)
      {
         cal.setTimeZone(tz);
      }

      return cal;
   }

   /**
    * [-]yyyy-mm-dd
    *
    * @param value string date value
    * @return equivalent date as an instance of java.util.Calendar.
    */
   public static String marshalDate(Calendar value)
   {
      String result = String.valueOf(value.get(Calendar.YEAR));
      result += '-';
      result += marshalInt(value.get(Calendar.MONTH) + 1, 2);
      result += '-';
      result += marshalInt(value.get(Calendar.DAY_OF_MONTH), 2);
      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * Parses string representation of time following the format hh:mm:ss:sss with optional timezone indicator.
    *
    * @param value
    * @return
    */
   public static Calendar unmarshalTime(String value)
   {
      Calendar cal = Calendar.getInstance();
      cal.clear();

      int tzLoc = parseTime(value, 0, cal);

      TimeZone tz = null;
      if(value.length() > tzLoc)
      {
         tz = parseTimeZone(value, tzLoc);
      }

      if(tz != null)
      {
         cal.setTimeZone(tz);
      }
      return cal;
   }

   /**
    * hh:mm:ss:sss[timezone]
    *
    * @param value
    * @return
    */
   public static String marshalTime(Calendar value)
   {
      String result = marshalInt(value.get(Calendar.HOUR_OF_DAY), 2);
      result += ':';
      result += marshalInt(value.get(Calendar.MINUTE), 2);
      result += ':';
      result += marshalInt(value.get(Calendar.SECOND), 2);
      result += '.';

      int millis = value.get(Calendar.MILLISECOND);
      if(millis > 99)
      {
         result += String.valueOf(millis);
      }
      else if(millis > 9)
      {
         result += "0" + String.valueOf(millis);
      }
      else
      {
         result += "00" + String.valueOf(millis);
      }

      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * Parses string value of datetime following the format [-]yyyy-mm-ddThh:mm:ss[.s+][timezone].
    *
    * @param value
    * @return
    */
   public static Calendar unmarshalDateTime(String value)
   {
      Calendar cal = Calendar.getInstance();
      cal.clear();

      int timeInd = parseDate(value, 0, cal);
      if(value.charAt(timeInd) != 'T')
      {
         throw new JBossXBValueFormatException("DateTime value does not follow the format '[-]yyyy-mm-ddThh:mm:ss[.s+][timezone]': expected 'T' but got " +
            value.charAt(timeInd)
         );
      }

      int tzStart = parseTime(value, timeInd + 1, cal);

      TimeZone tz = null;
      if(value.length() > tzStart)
      {
         tz = parseTimeZone(value, tzStart);
      }

      if(tz != null)
      {
         cal.setTimeZone(tz);
      }

      return cal;
   }

   /**
    * [-]yyyy-mm-ddThh:mm:ss[.s+][timezone]
    *
    * @param value
    * @return
    */
   public static String marshalDateTime(Calendar value)
   {
      String result = String.valueOf(value.get(Calendar.YEAR));
      result += '-';
      result += marshalInt(value.get(Calendar.MONTH) + 1, 2);
      result += '-';
      result += marshalInt(value.get(Calendar.DAY_OF_MONTH), 2);
      result += 'T';
      result += marshalInt(value.get(Calendar.HOUR_OF_DAY), 2);
      result += ':';
      result += marshalInt(value.get(Calendar.MINUTE), 2);
      result += ':';
      result += marshalInt(value.get(Calendar.SECOND), 2);
      result += '.';

      int millis = value.get(Calendar.MILLISECOND);
      if(millis > 99)
      {
         result += String.valueOf(millis);
      }
      else if(millis > 9)
      {
         result += "0" + String.valueOf(millis);
      }
      else
      {
         result += "00" + String.valueOf(millis);
      }

      result += marshalTimeZone(value.getTimeZone());
      return result;
   }

   /**
    * Converts hexBinary value into byte array by encoding two subsequent hexadecimal digits into one byte.
    *
    * @param value
    * @return
    */
   public static byte[] unmarshalHexBinary(String value)
   {
      if(value.length() % 2 != 0)
      {
         throw new IllegalArgumentException("hexBinary value must have even length.");
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      for(int i = 0; i < value.length(); i += 2)
      {
         char c1 = value.charAt(i);
         char c2 = value.charAt(i + 1);
         byte b = 0;
         if((c1 >= '0') && (c1 <= '9'))
         {
            b += ((c1 - '0') * 16);
         }
         else if((c1 >= 'a') && (c1 <= 'f'))
         {
            b += ((c1 - 'a' + 10) * 16);
         }
         else if((c1 >= 'A') && (c1 <= 'F'))
         {
            b += ((c1 - 'A' + 10) * 16);
         }
         else
         {
            throw new IllegalArgumentException("hexBinary value contains illegal character: " + value);
         }

         if((c2 >= '0') && (c2 <= '9'))
         {
            b += (c2 - '0');
         }
         else if((c2 >= 'a') && (c2 <= 'f'))
         {
            b += (c2 - 'a' + 10);
         }
         else if((c2 >= 'A') && (c2 <= 'F'))
         {
            b += (c2 - 'A' + 10);
         }
         else
         {
            throw new IllegalArgumentException("hexBinary value contains illegal character: " + value);
         }
         baos.write(b);
      }
      return (baos.toByteArray());
   }

   /**
    * @param value
    * @return
    */
   public static String marshalHexBinary(byte[] value)
   {
      StringBuffer result = new StringBuffer(2 * value.length);
      for(int i = 0; i < value.length; ++i)
      {
         result.append(convertDigit((value[i] >> 4)));
         result.append(convertDigit((value[i] & 0x0f)));
      }
      return result.toString();
   }

   public static boolean isNormalizedString(String value)
   {
      for(int i = 0; i < value.length(); ++i)
      {
         char c = value.charAt(i);
         if(c == 0x09 || c == 0x0A || c == 0x0D)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Converts base64Binary value into byte array.
    */
   public static byte[] unmarshalBase64(String value)
   {
      return Base64.decode(value);
   }

   /**
    * Converts byte array into a base64Binary value.
    */
   public static String marshalBase64(byte[] value)
   {
      return Base64.encodeBytes(value);
   }

   /**
    * Converts a value of form prefix:localPart into a QName
    * The prefix must be registered previously
    */
   public static QName unmarshalQName(String value, NamespaceContext nsRegistry)
   {
      int colonIndex = value.lastIndexOf(":");
      if(colonIndex > 0)
      {
         String prefix = value.substring(0, colonIndex);
         String nsURI = nsRegistry.getNamespaceURI(prefix);
         if(nsURI == null)
         {
            throw new IllegalStateException("No namespace URI registered for prefix: " + prefix);
         }

         String localPart = value.substring(colonIndex + 1);
         return new QName(nsURI, localPart, prefix);
      }
      else
      {
         return new QName(value);
      }
   }

   /**
    * Converts a QName value to form prefix:localPart
    * The prefix must be registered previously
    */
   public static String marshalQName(QName value, NamespaceContext nsRegistry)
   {
      String nsURI = value.getNamespaceURI();
      if(value.getPrefix().length() > 0)
      {
         return value.getPrefix() + ":" + value.getLocalPart();
      }
      else if(nsURI.length() > 0 && nsRegistry != null)
      {
         String prefix = nsRegistry.getPrefix(nsURI);
         if(prefix == null)
         {
            throw new IllegalStateException("Namespace URI not registered: " + nsURI);
         }

         return prefix.length() > 0 ? prefix + ":" + value.getLocalPart() : value.getLocalPart();
      }
      else
      {
         return value.getLocalPart();
      }
   }

   public static boolean isValidToken(String value)
   {
      if(value != null && value.length() > 0)
      {
         if(value.charAt(0) == 0x20 || value.charAt(value.length() - 1) == 0x20)
         {
            return false;
         }

         for(int i = 0; i < value.length(); ++i)
         {
            char c = value.charAt(i);
            if(c == 0x09 || c == 0x0A || c == 0x0D)
            {
               return false;
            }
            else if(c == 0x20)
            {
               if(i + 1 < value.length() && value.charAt(i + 1) == 0x20)
               {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static int parseGYear(String value, int start, Calendar cal)
   {
      int negative = (value.charAt(start) == '-' ? 1 : 0);
      cal.set(Calendar.YEAR, Integer.parseInt(value.substring(start, start + 4 + negative)));
      return start + 4 + negative;
   }

   private static int parseDate(String value, int start, Calendar cal)
   {
      if(value.charAt(start) == '-')
      {
         ++start;
      }

      if(!Character.isDigit(value.charAt(start)))
      {
         throw new JBossXBValueFormatException(
            "Date value does not follow the format '-'? yyyy '-' mm '-' dd: " + value
         );
      }

      int nextToken = value.indexOf('-', start);
      if(nextToken == -1 || nextToken - start < 4)
      {
         throw new JBossXBValueFormatException(
            "Date value does not follow the format '-'? yyyy '-' mm '-' dd: " + value
         );
      }

      int year = Integer.parseInt(value.substring(start, nextToken));

      start = nextToken + 1;
      nextToken = value.indexOf('-', start);
      if(nextToken == -1 || nextToken - start < 2)
      {
         throw new JBossXBValueFormatException(
            "Date value does not follow the format '-'? yyyy '-' mm '-' dd: " + value
         );
      }

      int month = Integer.parseInt(value.substring(start, nextToken));

      start = nextToken + 1;
      nextToken += 3;
      int day = Integer.parseInt(value.substring(start, nextToken));

      cal.set(Calendar.YEAR, year);
      cal.set(Calendar.MONTH, month - 1);
      cal.set(Calendar.DAY_OF_MONTH, day);

      return nextToken;
   }

   /**
    * Parses string value of time following the format 'hh:mm:ss:sss' and sets time value on the passed in
    * java.util.Calendar instace.
    *
    * @param value
    * @param cal
    */
   private static int parseTime(String value, int start, Calendar cal)
   {
      if(value.charAt(start + 2) != ':' || value.charAt(start + 5) != ':')
      {
         throw new JBossXBValueFormatException("Time value does not follow the format 'hh:mm:ss.[s+]': " + value);
      }

      int hh = Integer.parseInt(value.substring(start, start + 2));
      int mm = Integer.parseInt(value.substring(start + 3, start + 5));
      int ss = Integer.parseInt(value.substring(start + 6, start + 8));

      int millis = 0;

      int x = start + 8;

      if(value.length() > x && value.charAt(x) == '.')
      {
         int mul = 100;
         for(x += 1; x < value.length(); x++)
         {
            char c = value.charAt(x);

            if(Character.isDigit(c))
            {
               if(mul != 0)
               {
                  millis += Character.digit(c, 10) * mul;
                  mul = (mul == 1) ? 0 : mul / 10;
               }
            }
            else
            {
               break;
            }
         }
      }

      cal.set(Calendar.HOUR_OF_DAY, hh);
      cal.set(Calendar.MINUTE, mm);
      cal.set(Calendar.SECOND, ss);
      cal.set(Calendar.MILLISECOND, millis);

      return x;
   }

   /**
    * Parses timzone.
    * Format: [+/-]HH:MM
    *
    * @return
    */
   private static TimeZone parseTimeZone(String value, int start)
   {
      TimeZone tz;
      if(value.charAt(start) == '+' || (value.charAt(start) == '-'))
      {
         if(value.length() - start == 6 &&
            Character.isDigit(value.charAt(start + 1)) &&
            Character.isDigit(value.charAt(start + 2)) &&
            value.charAt(start + 3) == ':' &&
            Character.isDigit(value.charAt(start + 4)) &&
            Character.isDigit(value.charAt(start + 5)))
         {
            tz = TimeZone.getTimeZone("GMT" + value.substring(start));
         }
         else
         {
            throw new NumberFormatException(
               "Timezone value does not follow the format ([+/-]HH:MM): " + value.substring(start)
            );
         }
      }
      else if(value.charAt(start) == 'Z')
      {
         tz = TimeZone.getTimeZone("GMT");
      }
      else
      {
         throw new NumberFormatException(
            "Timezone value does not follow the format ([+/-]HH:MM): " + value.substring(start)
         );
      }
      return tz;
   }

   /**
    * Parses timezone.
    * Format: [+/-]HH:MM
    *
    * @return
    */
   private static String marshalTimeZone(TimeZone value)
   {
      int offset = value.getRawOffset();
      if(offset == 0)
      {
         return "Z";
      }

      DecimalFormat hourFormat = new DecimalFormat("'+'00;-00");
      DecimalFormat minuteFormat = new DecimalFormat("00");

      int minutes = offset / (1000 * 60);
      int hours = minutes / 60;

      minutes -= (hours * 60);

      return hourFormat.format(hours) + ":" + minuteFormat.format(minutes);
   }

   private static String marshalInt(int value, int length)
   {
      String result = String.valueOf(value);
      if(result.length() < length)
      {
         while(result.length() < length)
         {
            result = '0' + result;
         }
      }
      else if(result.length() > length)
      {
         throw new JBossXBValueFormatException(
            "Can't marshal int value " + value + " to a string with length of " + length
         );
      }
      return result;
   }

   private static char convertDigit(int value)
   {
      value &= 0x0f;
      if(value >= 10)
      {
         return ((char)(value - 10 + 'a'));
      }
      else
      {
         return ((char)(value + '0'));
      }
   }
}
