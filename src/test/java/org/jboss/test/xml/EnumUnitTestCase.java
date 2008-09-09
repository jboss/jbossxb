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
import java.util.List;
import java.util.Arrays;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.Marshaller;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class EnumUnitTestCase
   extends AbstractJBossXBTest
{
   private final String XSD = "<xsd:schema xmlns='http://jboss.org/ns/enum' targetNamespace='http://jboss.org/ns/enum'" +
      " xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      " xmlns:jbxb='http://www.jboss.org/xml/ns/jbxb'" +
      " elementFormDefault='qualified'>" +
      "<xsd:simpleType name='color'>" +
      "  <xsd:annotation>" +
      "    <xsd:appinfo>" +
      "      <jbxb:class impl='" + Color.class.getName() + "'/>" +
      "    </xsd:appinfo>" +
      "  </xsd:annotation>" +
      "  <xsd:restriction base='xsd:string'>" +
      "    <xsd:enumeration value='red'/>" +
      "    <xsd:enumeration value='gree'/>" +
      "    <xsd:enumeration value='blue'/>" +
      "  </xsd:restriction>" +
      "</xsd:simpleType>" +
      "<xsd:simpleType name='number'>" +
      "  <xsd:annotation>" +
      "    <xsd:appinfo>" +
      "      <jbxb:class impl='" + Nmbr.class.getName() + "'/>" +
      "    </xsd:appinfo>" +
      "  </xsd:annotation>" +
      "  <xsd:restriction base='xsd:int'>" +
      "    <xsd:enumeration value='1'/>" +
      "    <xsd:enumeration value='2'/>" +
      "    <xsd:enumeration value='3'/>" +
      "  </xsd:restriction>" +
      "</xsd:simpleType>" +
      "<xsd:element name='global'>" +
      "  <xsd:annotation>" +
      "    <xsd:appinfo>" +
      "      <jbxb:class impl='" + Global.class.getName() + "'/>" +
      "    </xsd:appinfo>" +
      "  </xsd:annotation>" +
      "  <xsd:complexType>" +
      "    <xsd:sequence>" +
      "      <xsd:element name='color' type='color' minOccurs='0'/>" +
      "      <xsd:element name='number' type='number' minOccurs='0'/>" +
      "      <xsd:element name='all' minOccurs='0' maxOccurs='unbounded'>" +
      "        <xsd:annotation>" +
      "          <xsd:appinfo>" +
      "            <jbxb:skip/>" +
      "          </xsd:appinfo>" +
      "        </xsd:annotation>" +
      "        <xsd:complexType>" +
      "          <xsd:all>" +
      "            <xsd:element name='color' type='color' minOccurs='0'>" +
      "              <xsd:annotation>" +
      "                <xsd:appinfo>" +
      "                  <jbxb:property name='all'/>" +
      "                </xsd:appinfo>" +
      "              </xsd:annotation>" +
      "            </xsd:element>" +
      "            <xsd:element name='number' type='number' minOccurs='0'>" +
      "              <xsd:annotation>" +
      "                <xsd:appinfo>" +
      "                  <jbxb:property name='all'/>" +
      "                </xsd:appinfo>" +
      "              </xsd:annotation>" +
      "            </xsd:element>" +
      "          </xsd:all>" +
      "        </xsd:complexType>" +
      "      </xsd:element>" +
      "    </xsd:sequence>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "</xsd:schema>";

   private final String XML_SIMPLE =
      "<global xmlns='http://jboss.org/ns/enum'>" +
      "<color>red</color>" +
      "<number>1</number>" +
      "</global>";

   private static final Global GLOBAL_SIMPLE;
   static
   {
      Global simple = new Global();
      simple.color = Color.RED;
      simple.number = Nmbr.ONE;
      GLOBAL_SIMPLE = simple;
   }

   private final String XML_ALL =
      "<global xmlns='http://jboss.org/ns/enum'>" +
      "  <all>" +
      "    <number>3</number>" +
      "    <color>green</color>" +
      "  </all>" +
      "  <all>" +
      "    <color>blue</color>" +
      "    <number>1</number>" +
      "  </all>" +
      "  <all>" +
      "    <color>red</color>" +
      "    <number>2</number>" +
      "  </all>" +
      "</global>";

   private static final Global GLOBAL_ALL;

   static
   {
      Global all = new Global();
      all.all = Arrays.asList(new Object[]{Nmbr.THREE, Color.GREEN, Color.BLUE, Nmbr.ONE, Color.RED, Nmbr.TWO});
      GLOBAL_ALL = all;
   }

   public EnumUnitTestCase(String name)
   {
      super(name);
   }

   public void testSimpleUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML_SIMPLE), schema);

      assertEquals(GLOBAL_SIMPLE, o);
   }

   public void testSimpleMarshallingSunday() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(schema, null, GLOBAL_SIMPLE, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML_SIMPLE, marshalled);
   }

   public void testSimpleMarshallingXerces() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), GLOBAL_SIMPLE, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML_SIMPLE, marshalled);
   }

   public void testAllUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML_ALL), schema);

      assertEquals(GLOBAL_ALL, o);
   }

   // Private

   public static final class Global
   {
      public Color color;
      public Nmbr number;
      public List<Object> all;

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Global))
         {
            return false;
         }

         final Global global = (Global)o;

         if(all != null ? !all.equals(global.all) : global.all != null)
         {
            return false;
         }
         if(color != null ? !color.equals(global.color) : global.color != null)
         {
            return false;
         }
         if(number != null ? !number.equals(global.number) : global.number != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (color != null ? color.hashCode() : 0);
         result = 29 * result + (number != null ? number.hashCode() : 0);
         result = 29 * result + (all != null ? all.hashCode() : 0);
         return result;
      }

      public String toString()
      {
         return color + " " + number + ", all=" + all;
      }
   }
   
   public static final class Color
   {
      private static final String RED_VALUE = "red";
      private static final String GREEN_VALUE = "green";
      private static final String BLUE_VALUE = "blue";

      public static final Color RED = new Color(RED_VALUE);
      public static final Color GREEN = new Color(GREEN_VALUE);
      public static final Color BLUE = new Color(BLUE_VALUE);

      public static Color fromValue(String value)
      {
         Color color;
         if(RED_VALUE.equals(value))
         {
            color = RED;
         }
         else if(GREEN_VALUE.equals(value))
         {
            color = GREEN;
         }
         else if(BLUE_VALUE.equals(value))
         {
            color = BLUE;
         }
         else
         {
            throw new IllegalArgumentException("Not supported value: " + value);
         }
         return color;
      }

      private final String value;

      private Color(String value)
      {
         this.value = value;
      }

      public String value()
      {
         return value;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Color))
         {
            return false;
         }

         final Color color = (Color)o;

         if(!value.equals(color.value))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return value.hashCode();
      }

      public String toString()
      {
         return "COLOR " + value;
      }
   }

   public static final class Nmbr
   {
      public static final Nmbr ONE = new Nmbr(1);
      public static final Nmbr TWO = new Nmbr(2);
      public static final Nmbr THREE = new Nmbr(3);

      public static Nmbr fromValue(int value)
      {
         Nmbr nmbr;
         switch(value)
         {
            case 1:
               nmbr = ONE;
               break;
            case 2:
               nmbr = TWO;
               break;
            case 3:
               nmbr = THREE;
               break;
            default:
               throw new IllegalArgumentException("Not supported value: " + value);
         }
         return nmbr;
      }

      private final int value;

      private Nmbr(int value)
      {
         this.value = value;
      }

      public int value()
      {
         return value;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Nmbr))
         {
            return false;
         }

         final Nmbr nmbr = (Nmbr)o;

         if(value != nmbr.value)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return value;
      }

      public String toString()
      {
         return "NUMBER " + value;
      }
   }
}
