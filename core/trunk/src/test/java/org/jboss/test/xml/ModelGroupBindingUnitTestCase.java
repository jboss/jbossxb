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
import java.util.Collection;
import java.util.List;

import junit.framework.Test;

import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.Marshaller;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class ModelGroupBindingUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(ModelGroupBindingUnitTestCase.class);
   }
   
   private static final String XSD =
      "<xsd:schema targetNamespace='http://jboss.org/ns/mg'" +
      " xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      " xmlns:jbxb='http://www.jboss.org/xml/ns/jbxb'" +
      " elementFormDefault='qualified'>" +
      "<xsd:element name='global'>" +
      "<xsd:annotation>" +
      "<xsd:appinfo>" +
      "<jbxb:class impl='" +
      GlobalElement.class.getName() +
      "'/>" +
      "</xsd:appinfo>" +
      "</xsd:annotation>" +
      "  <xsd:complexType>" +
      "    <xsd:sequence>" +
      "       <xsd:sequence minOccurs='0' maxOccurs='unbounded'>" +
      "         <xsd:annotation>" +
      "           <xsd:appinfo>" +
      "             <jbxb:class impl='" +
      GlobalElement.Sequence.class.getName() +
      "' collectionType='java.util.ArrayList'/>" +
      "             <jbxb:property name='sequences'/>" +
      "           </xsd:appinfo>" +
      "          </xsd:annotation>" +
      "          <xsd:element name='item1' type='xsd:string'/>" +
      "          <xsd:element name='item2' type='xsd:string'/>" +
      "          <xsd:element name='item3' type='xsd:string'/>" +
      "       </xsd:sequence>" +
      "       <xsd:choice minOccurs='0' maxOccurs='unbounded'>" +
      "         <xsd:annotation>" +
      "           <xsd:appinfo>" +
      "             <jbxb:class impl='" +
      GlobalElement.Choice.class.getName() +
      "' collectionType='java.util.ArrayList'/>" +
      "             <jbxb:property name='choices'/>" +
      "           </xsd:appinfo>" +
      "          </xsd:annotation>" +
      "          <xsd:element name='choice1' type='xsd:string'/>" +
      "          <xsd:element name='choice2' type='xsd:string'/>" +
      "          <xsd:element name='choice3' type='xsd:string'/>" +
      "       </xsd:choice>" +
      "       <xsd:sequence minOccurs='0' maxOccurs='unbounded'>" +
      "         <xsd:annotation>" +
      "           <xsd:appinfo>" +
      "             <jbxb:class impl='" +
      java.util.ArrayList.class.getName() +
      "' collectionType='java.util.ArrayList'/>" +
      "             <jbxb:property name='listOfLists'/>" +
      "           </xsd:appinfo>" +
      "          </xsd:annotation>" +
      "          <xsd:element name='item_1' type='xsd:string'/>" +
      "          <xsd:element name='item_2' type='xsd:string'/>" +
      "       </xsd:sequence>" +
      "    </xsd:sequence>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "</xsd:schema>";

   private static SchemaBinding SCHEMA;

   private static final String XML =
      "<global xmlns='http://jboss.org/ns/mg'>" +
      "<item1>item11</item1>" +
      "<item2>item12</item2>" +
      "<item3>item13</item3>" +
      "<item1>item21</item1>" +
      "<item2>item22</item2>" +
      "<item3>item23</item3>" +
      "<choice1>choice1</choice1>" +
      "<choice3>choice3</choice3>" +
      "</global>";

   private static final String LIST_OF_LISTS_XML = "<global xmlns='http://jboss.org/ns/mg'>" +
      "<item_1>item11</item_1>" +
      "<item_2>item12</item_2>" +
      "<item_1>item21</item_1>" +
      "<item_2>item22</item_2>" +
      "<item_1>item31</item_1>" +
      "<item_2>item32</item_2>" +
      "</global>";

   private static final List<Object> LIST_OF_LISTS = Arrays.asList(new Object[]{
      Arrays.asList(new String[]{"item11", "item12"}),
      Arrays.asList(new String[]{"item21", "item22"}),
      Arrays.asList(new String[]{"item31", "item32"})
   }
   );

   public ModelGroupBindingUnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null);
         SCHEMA.setIgnoreUnresolvedFieldOrClass(false);
      }
   }

   public void testUnmarshalling() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      GlobalElement global = (GlobalElement)unmarshaller.unmarshal(new StringReader(XML), SCHEMA);
      assertEquals(GlobalElement.INSTANCE, global);
   }

   /**
    * XercesXsMarshaller does not support marshalling of model groups since
    * it is not based on annotations
    */
   public void testMarshallingSunday() throws Exception
   {
      marshallingTest(new MarshallerImpl());
   }

   public void testListOfListsUnmarshalling() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      GlobalElement global = (GlobalElement)unmarshaller.unmarshal(new StringReader(LIST_OF_LISTS_XML), SCHEMA);
      assertNull(global.choices);
      assertNull(global.sequences);
      assertEquals(LIST_OF_LISTS, global.listOfLists);
   }

   public void testListOfListsMarshalling() throws Exception
   {
      GlobalElement global = new GlobalElement();
      global.listOfLists = LIST_OF_LISTS;

      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(SCHEMA, null, global, writer);

      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(LIST_OF_LISTS_XML, marshalled);
   }

   // Private

   private void marshallingTest(AbstractMarshaller marshaller) throws Exception
   {
      marshaller.setProperty(Marshaller.PROP_OUTPUT_INDENTATION, "false");
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), new MappingObjectModelProvider(), GlobalElement.INSTANCE, writer);

      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML, marshalled);
   }

   // Inner

   public static class GlobalElement
   {
      public static final GlobalElement INSTANCE;

      static
      {
         GlobalElement global = new GlobalElement();
         global.sequences = Arrays.asList(new Object[]{
            new Sequence("item11", "item12", "item13"),
            new Sequence("item21", "item22", "item23")
         }
         );
         global.choices = Arrays.asList(new Object[]{
            new Choice("choice1", null, null),
            new Choice(null, null, "choice3")
         }
         );
         INSTANCE = global;
      }

      public Collection<Object> sequences;
      public Collection<Object> choices;
      public Collection<Object> listOfLists;

      public String toString()
      {
         return "[global sequences=" + sequences + ", choices=" + choices + ", listOfLists=" + listOfLists + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof GlobalElement))
         {
            return false;
         }

         final GlobalElement globalElement = (GlobalElement)o;

         if(choices != null ? !choices.equals(globalElement.choices) : globalElement.choices != null)
         {
            return false;
         }
         if(sequences != null ? !sequences.equals(globalElement.sequences) : globalElement.sequences != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (sequences != null ? sequences.hashCode() : 0);
         result = 29 * result + (choices != null ? choices.hashCode() : 0);
         return result;
      }
      // Inner

      public static class Sequence
      {
         public String item1;
         public String item2;
         public String item3;

         public Sequence()
         {
         }

         public Sequence(String item1, String item2, String item3)
         {
            this.item1 = item1;
            this.item2 = item2;
            this.item3 = item3;
         }

         public String toString()
         {
            return "[sequence item1=" +
               item1 +
               ", item2=" +
               item2 +
               ", item3=" + item3 + "]";
         }

         public boolean equals(Object o)
         {
            if(this == o)
            {
               return true;
            }
            if(!(o instanceof Sequence))
            {
               return false;
            }

            final Sequence sequence = (Sequence)o;

            if(item1 != null ? !item1.equals(sequence.item1) : sequence.item1 != null)
            {
               return false;
            }
            if(item2 != null ? !item2.equals(sequence.item2) : sequence.item2 != null)
            {
               return false;
            }
            if(item3 != null ? !item3.equals(sequence.item3) : sequence.item3 != null)
            {
               return false;
            }

            return true;
         }

         public int hashCode()
         {
            int result;
            result = (item1 != null ? item1.hashCode() : 0);
            result = 29 * result + (item2 != null ? item2.hashCode() : 0);
            result = 29 * result + (item3 != null ? item3.hashCode() : 0);
            return result;
         }
      }

      public static class Choice
      {
         public String choice1;
         public String choice2;
         public String choice3;

         public Choice()
         {
         }

         public Choice(String choice1, String choice2, String choice3)
         {
            this.choice1 = choice1;
            this.choice2 = choice2;
            this.choice3 = choice3;
         }

         public String toString()
         {
            return "[choice choice1=" +
               choice1 +
               ", choice2=" +
               choice2 +
               ", choice3=" + choice3 + "]";
         }

         public boolean equals(Object o)
         {
            if(this == o)
            {
               return true;
            }
            if(!(o instanceof Choice))
            {
               return false;
            }

            final Choice choice = (Choice)o;

            if(choice1 != null ? !choice1.equals(choice.choice1) : choice.choice1 != null)
            {
               return false;
            }
            if(choice2 != null ? !choice2.equals(choice.choice2) : choice.choice2 != null)
            {
               return false;
            }
            if(choice3 != null ? !choice3.equals(choice.choice3) : choice.choice3 != null)
            {
               return false;
            }

            return true;
         }

         public int hashCode()
         {
            int result;
            result = (choice1 != null ? choice1.hashCode() : 0);
            result = 29 * result + (choice2 != null ? choice2.hashCode() : 0);
            result = 29 * result + (choice3 != null ? choice3.hashCode() : 0);
            return result;
         }
      }
   }
}
