/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import java.text.ParseException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface TypeConverter
{
   TypeConverter STRING = new TypeConverter()
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

   TypeConverter INT = new TypeConverter()
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

   TypeConverter LONG = new TypeConverter()
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

   TypeConverter DOUBLE = new TypeConverter()
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

   TypeConverter FLOAT = new TypeConverter()
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

   TypeConverter SHORT = new TypeConverter()
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

   TypeConverter BYTE = new TypeConverter()
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

   TypeConverter CHAR = new TypeConverter()
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

   TypeConverter JAVA_UTIL_DATE = new TypeConverter()
   {
      private static final String FORMAT = "yyyy-MM-dd";

      public Object unmarshal(String value)
      {
         try
         {
            return new java.text.SimpleDateFormat(FORMAT).parse(value);
         }
         catch(ParseException e)
         {
            throw new IllegalStateException("Failed to parse date string value: " + value);
         }
      }

      public String marshal(Object value)
      {
         return new java.text.SimpleDateFormat(FORMAT).format(value);
      }
   };

   Object unmarshal(String value);

   String marshal(Object value);
}
