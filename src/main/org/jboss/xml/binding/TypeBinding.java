/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;

import java.io.Serializable;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public final class TypeBinding
   implements Serializable
{
   public static final String XS_INT_NAME = "int";
   public static final String XS_LONG_NAME = "long";
   public static final String XS_SHORT_NAME = "short";
   public static final String XS_FLOAT_NAME = "float";
   public static final String XS_DOUBLE_NAME = "double";
   public static final String XS_BOOLEAN_NAME = "boolean";
   public static final String XS_BYTE_NAME = "byte";
   public static final String XS_STRING_NAME = "string";
   public static final String XS_INTEGER_NAME = "integer";
   public static final String XS_DECIMAL_NAME = "decimal";
   public static final String XS_DATETIME_NAME = "dateTime";
   public static final String XS_QNAME_NAME = "QName";
   public static final String XS_ANYURI_NAME = "anyURI";
   public static final String XS_UNSIGNEDINT_NAME = "unsignedInt";
   public static final String XS_UNSIGNEDSHORT_NAME = "unsignedShort";
   public static final String XS_UNSIGNEDBYTE_NAME = "unsignedByte";
   public static final String XS_DATE_NAME = "date";
   public static final String XS_TIME_NAME = "time";
   public static final String XS_BASE64BINARY_NAME = "base64Binary";
   public static final String XS_HEXBINARY_NAME = "hexBinary";
   public static final String XS_ANYSIMPLETYPE_NAME = "anySimpleType";
   public static final String XS_DURATION_NAME = "duration";
   public static final String XS_GYEARMONTH_NAME = "gYearMonth";
   public static final String XS_GYEAR_NAME = "gYear";
   public static final String XS_GMONTHDAY_NAME = "gMonthDay";
   public static final String XS_GMONTH_NAME = "gMonth";
   public static final String XS_GDAY_NAME = "gDay";
   public static final String XS_NORMALIZEDSTRING_NAME = "normalizedString";
   public static final String XS_TOKEN_NAME = "token";
   public static final String XS_LANGUAGE_NAME = "language";
   public static final String XS_NAME_NAME = "Name";
   public static final String XS_NCNAME_NAME = "NCName";
   public static final String XS_ID_NAME = "ID";
   public static final String XS_NMTOKEN_NAME = "NMToken";
   public static final String XS_NMTOKENS_NAME = "NMTokens";
   public static final String XS_NONPOSITIVEINTEGER_NAME = "nonPositiveInteger";
   public static final String XS_NEGATIVEINTEGER_NAME = "negativeInteger";
   public static final String XS_NONNEGATIVEINTEGER_NAME = "nonNegativeInteger";
   public static final String XS_POSITIVEINTEGER_NAME = "positiveInteger";
   public static final String XS_UNSIGNEDLONG_NAME = "unsignedLong";

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
   public static final int XS_UNSIGNEDLONG = XS_UNSIGNEDLONG_NAME.hashCode();

   static
   {
      // check for uniqueness of hashCode's
      int[] codes = new int[40];
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

      Logger log = Logger.getLogger(TypeBinding.class);
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

   public static Object unmarshal(String xsdType, String value)
   {
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
         result = Float.valueOf(value);
      }
      else if(typeCode == XS_DOUBLE)
      {
         result = Double.valueOf(value);
      }
      else if(typeCode == XS_BOOLEAN)
      {
         result = Boolean.valueOf(value);
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
         // todo XS_QNAME
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_QNAME_NAME);
      }
      else if(typeCode == XS_ANYURI)
      {
         // todo XS_ANYURI
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_ANYURI_NAME);
      }
      else if(typeCode == XS_UNSIGNEDINT)
      {
         result = Integer.valueOf(value);
      }
      else if(typeCode == XS_UNSIGNEDSHORT)
      {
         result = Short.valueOf(value);
      }
      else if(typeCode == XS_UNSIGNEDBYTE)
      {
         result = Byte.valueOf(value);
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
         // todo XS_BASE64BINARY
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_BASE64BINARY_NAME);
      }
      else if(typeCode == XS_HEXBINARY)
      {
         // todo XS_HEXBINARY
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_HEXBINARY_NAME);
      }
      else if(typeCode == XS_ANYSIMPLETYPE)
      {
         // todo XS_ANYSIMPLETYPE
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_ANYSIMPLETYPE_NAME);
      }
      else if(typeCode == XS_DURATION)
      {
         // todo XS_DURATION
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_DURATION_NAME);
      }
      else if(typeCode == XS_GYEARMONTH)
      {
         // todo XS_GYEARMONTH
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_GYEARMONTH_NAME);
      }
      else if(typeCode == XS_GYEAR)
      {
         // todo XS_GYEAR
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_GYEAR_NAME);
      }
      else if(typeCode == XS_GMONTHDAY)
      {
         // todo XS_GMONTHDAY
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_GMONTHDAY_NAME);
      }
      else if(typeCode == XS_GMONTH)
      {
         // todo XS_GMONTH
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_GMONTH_NAME);
      }
      else if(typeCode == XS_GDAY)
      {
         return unmarshalGDay(value);
      }
      else if(typeCode == XS_NORMALIZEDSTRING)
      {
         result = value;
      }
      else if(typeCode == XS_TOKEN)
      {
         // todo XS_TOKEN
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_TOKEN_NAME);
      }
      else if(typeCode == XS_LANGUAGE)
      {
         // todo XS_LANGUAGE
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_LANGUAGE_NAME);
      }
      else if(typeCode == XS_NAME)
      {
         // todo XS_NAME
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_NAME_NAME);
      }
      else if(typeCode == XS_NCNAME)
      {
         // todo XS_NCNAME
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_NCNAME_NAME);
      }
      else if(typeCode == XS_ID)
      {
         // todo XS_ID
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_ID_NAME);
      }
      else if(typeCode == XS_NMTOKEN)
      {
         // todo XS_NMTOKEN
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_NMTOKEN_NAME);
      }
      else if(typeCode == XS_NMTOKENS)
      {
         // todo XS_NMTOKENS
         throw new IllegalStateException("Recognized but not supported xsdType: " + XS_NMTOKENS_NAME);
      }
      else if(typeCode == XS_NONPOSITIVEINTEGER)
      {
         result = Integer.valueOf(value);
      }
      else if(typeCode == XS_NEGATIVEINTEGER)
      {
         result = Integer.valueOf(value);
      }
      else if(typeCode == XS_NONNEGATIVEINTEGER)
      {
         result = Integer.valueOf(value);
      }
      else if(typeCode == XS_UNSIGNEDLONG)
      {
         result = Long.valueOf(value);
      }
      else if(typeCode == XS_POSITIVEINTEGER)
      {
         result = Integer.valueOf(value);
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
            throw new IllegalStateException(
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
         //todo do something
         throw new IllegalStateException("Unexpected field type " + javaType);
      }

      return result;
   }

   public static Calendar unmarshalGDay(String value)
   {
      // the format is ---DD[timezonePart]
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
      String timezonePart = value.substring(5);
      TimeZone tz = parseTimeZone(timezonePart);

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

      int ind = parseDate(value, cal);

      TimeZone tz = null;
      if(ind < value.length())
      {
         String timezonePart = value.substring(ind);
         tz = parseTimeZone(timezonePart);
      }

      if(tz != null)
      {
         cal.setTimeZone(tz);
      }

      return cal;
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

      parseTime(value, cal);

      TimeZone tz = null;
      if(value.length() > 12)
      {
         String timezonePart = value.substring(12);
         tz = parseTimeZone(timezonePart);
      }

      if(tz != null)
      {
         cal.setTimeZone(tz);
      }
      return cal;
   }

   /**
    * Parses string value of datetime following the format [-]yyyy-mm-ddThh:mm:ss[.s+][timezone].
    * @param value
    * @return
    */
   public static Calendar unmarshalDateTime(String value)
   {
      Calendar cal = Calendar.getInstance();
      cal.clear();

      int timeInd = value.indexOf('T');
      if(timeInd == -1)
      {
         throw new JBossXBValueFormatException(
            "DateTime value does not follow the format '[-]yyyy-mm-ddThh:mm:ss[.s+][timezone]'"
         );
      }

      parseDate(value.substring(0, timeInd), cal);
      parseTime(value.substring(timeInd + 1), cal);

      TimeZone tz = null;
      if(value.length() > timeInd + 13)
      {
         String timezonePart = value.substring(timeInd + 13);
         tz = parseTimeZone(timezonePart);
      }

      if(tz != null)
      {
         cal.setTimeZone(tz);
      }

      return cal;
   }

   private static int parseDate(String value, Calendar cal)
   {
      int i = 0;
      if(value.charAt(i) == '-')
      {
         ++i;
      }

      if(!Character.isDigit(value.charAt(i)))
      {
         throw new JBossXBValueFormatException(
            "Date value does not follow the format '-'? yyyy '-' mm '-' dd: " + value
         );
      }

      int nextToken = value.indexOf('-', i);
      if(nextToken == -1 || nextToken - i < 4)
      {
         throw new JBossXBValueFormatException(
            "Date value does not follow the format '-'? yyyy '-' mm '-' dd: " + value
         );
      }

      int year = Integer.parseInt(value.substring(i, nextToken));

      i = nextToken + 1;
      nextToken = value.indexOf('-', i);
      if(nextToken == -1 || nextToken - i < 2)
      {
         throw new JBossXBValueFormatException(
            "Date value does not follow the format '-'? yyyy '-' mm '-' dd: " + value
         );
      }

      int month = Integer.parseInt(value.substring(i, nextToken));

      i = nextToken + 1;
      nextToken += 3;
      int day = Integer.parseInt(value.substring(i, nextToken));

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
   private static void parseTime(String value, Calendar cal)
   {
      if(value.charAt(2) != ':' || value.charAt(5) != ':' || value.charAt(8) != '.')
      {
         throw new JBossXBValueFormatException("Time value does not follow the format 'hh:mm:ss:sss': " + value);
      }

      int hh = Integer.parseInt(value.substring(0, 2));
      int mm = Integer.parseInt(value.substring(3, 5));
      int ss = Integer.parseInt(value.substring(6, 8));
      int sss = Integer.parseInt(value.substring(9, 12));

      cal.set(Calendar.HOUR_OF_DAY, hh);
      cal.set(Calendar.MINUTE, mm);
      cal.set(Calendar.SECOND, ss);
      cal.set(Calendar.MILLISECOND, sss);
   }

   /**
    * Parses timzone.
    * Format: [+/-]HH:MM
    *
    * @param timezonePart
    * @return
    */
   private static TimeZone parseTimeZone(String timezonePart)
   {
      TimeZone tz = null;
      if(timezonePart.charAt(0) == '+' || (timezonePart.charAt(0) == '-'))
      {
         if(timezonePart.length() == 6 &&
            Character.isDigit(timezonePart.charAt(1)) &&
            Character.isDigit(timezonePart.charAt(2)) &&
            timezonePart.charAt(3) == ':' &&
            Character.isDigit(timezonePart.charAt(4)) &&
            Character.isDigit(timezonePart.charAt(5)))
         {
            tz = TimeZone.getTimeZone("GMT" + timezonePart);
         }
         else
         {
            throw new NumberFormatException("Timezone value does not follow the format ([+/-]HH:MM): " + timezonePart);
         }
      }
      else if(timezonePart.equals("Z"))
      {
         tz = TimeZone.getTimeZone("GMT");
      }
      else
      {
         throw new NumberFormatException("Timezone value does not follow the format ([+/-]HH:MM): " + timezonePart);
      }
      return tz;
   }
}
