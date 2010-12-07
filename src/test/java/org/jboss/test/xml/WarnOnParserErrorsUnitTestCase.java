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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class WarnOnParserErrorsUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String XSD = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  elementFormDefault='qualified'" +
      "  version='1.0'>" +
      "<xsd:element name='e'>" +
      "  <xsd:complexType>" +
      "    <xsd:sequence>" +
      "      <xsd:element name='value1' type='xsd:string'/>" +
      "      <xsd:element name='value2' type='xsd:string'/>" +
      "    </xsd:sequence>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "</xsd:schema>";

   private static final String XML =
      "<e xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation=''>" +
      "<value2>2</value2>" +
      "<value1>1</value1>" +
      "</e>";

   public WarnOnParserErrorsUnitTestCase(String name)
   {
      super(name);
   }

   private boolean useUnorderedSequence;
   public void setUp() throws Exception
   {
      super.setUp();
      useUnorderedSequence = JBossXBBuilder.isUseUnorderedSequence();
      JBossXBBuilder.setUseUnorderedSequence(true);
   }
   
   public void tearDown() throws Exception
   {
      super.tearDown();
      JBossXBBuilder.setUseUnorderedSequence(useUnorderedSequence);
   }
   
   public void testWarn() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(E.class, true);

      EntityResolver resolver = new EntityResolver(){
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
         {
            return new InputSource(new StringReader(XSD));
         }};

      UnmarshallerFactory factory = UnmarshallerFactory.newInstance();
      
      Unmarshaller unmarshaller = factory.newUnmarshaller();
      unmarshaller.setSchemaValidation(true);
      unmarshaller.setEntityResolver(resolver);
      unmarshaller.setWarnOnParserErrors(true);
      
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof E);
      E e = (E)o;
      assertEquals("1", e.getValue1());
      assertEquals("2", e.getValue2());
   }

   public void testError() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(E.class, true);

      EntityResolver resolver = new EntityResolver(){
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
         {
            return new InputSource(new StringReader(XSD));
         }};

      UnmarshallerFactory factory = UnmarshallerFactory.newInstance();
      
      Unmarshaller unmarshaller = factory.newUnmarshaller();
      unmarshaller.setSchemaValidation(true);
      unmarshaller.setEntityResolver(resolver);
      unmarshaller.setWarnOnParserErrors(false);
      
      try
      {
         unmarshaller.unmarshal(new StringReader(XML), schema);
         fail("validation should have failed");
      }
      catch(JBossXBException e)
      {
      }
   }

   // Inner

   @XmlRootElement(name="e")
   public static final class E
   {
      private String value1;
      private String value2;
      
      public String getValue1()
      {
         return value1;
      }
      
      public void setValue1(String value)
      {
         this.value1 = value;
      }

      public String getValue2()
      {
         return value2;
      }
      
      public void setValue2(String value)
      {
         this.value2 = value;
      }
   }
}
