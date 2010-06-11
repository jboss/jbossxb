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
import java.util.Iterator;

import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xml.BeforeMarshalAfterUnmarshalHandlerTestCase.GlobalElement.StringType;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.marshalling.MarshallingContext;
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingCallback;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnmarshallingContext;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 2096 $</tt>
 */
public class BeforeMarshalAfterUnmarshalHandlerTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(BeforeMarshalAfterUnmarshalHandlerTestCase.class);
   }
   
   private static final String XSD =
      "<xsd:schema targetNamespace='http://jboss.org/ns/test'" +
      "  xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='http://www.jboss.org/xml/ns/jbxb'" +
      "  elementFormDefault='qualified'>" +
      "  <xsd:element name='global'>" +
      "    <xsd:annotation>" +
      "      <xsd:appinfo>" +
      "        <jbxb:class impl='" + GlobalElement.class.getName() + "'/>" +
      "      </xsd:appinfo>" +
      "    </xsd:annotation>" +
      "    <xsd:complexType>" +
      "      <xsd:sequence>" +
      "        <xsd:element name='stringType' type='xsd:string' minOccurs='0'/>" +
      "        <xsd:element name='stringElement' type='xsd:string' minOccurs='0'/>" +
      "        <xsd:sequence>" +
      "          <xsd:annotation>" +
      "            <xsd:appinfo>" +
      "              <jbxb:class impl='" + GlobalElement.Sequence.class.getName() + "'/>" +
      "              <jbxb:property name='sequenceItem'/>" +
      "            </xsd:appinfo>" +
      "          </xsd:annotation>" +
      "          <xsd:element name='item' type='xsd:string' minOccurs='0'/>" +
      "        </xsd:sequence>" +
      "      </xsd:sequence>" +
      "    </xsd:complexType>" +
      "  </xsd:element>" +
      "</xsd:schema>";

   private static final String XML_STRING_TYPE =
      "<global xmlns='http://jboss.org/ns/test'>" +
      "  <stringType>traumeel</stringType>" +
      "</global>";

   private static final String XML_STRING_ELEMENT =
      "<global xmlns='http://jboss.org/ns/test'>" +
      "  <stringElement>traumeel</stringElement>" +
      "</global>";

   private static final String XML_SEQUENCE_ITEM =
      "<global xmlns='http://jboss.org/ns/test'>" +
      "  <item>traumeel</item>" +
      "</global>";

   private static SchemaBinding SCHEMA;

   public BeforeMarshalAfterUnmarshalHandlerTestCase(String name)
   {
      super(name);
   }
   
   public void testTermBeforeMarshallingHandler_stringType() throws Exception
   {
      SchemaBinding schema = getSchema();
      MarshallerImpl marshaller = getMarshaller();
      StringWriter writer = new StringWriter();
      GlobalElement global = new GlobalElement();
      global.stringType = GlobalElement.STRING_TYPE;
      marshaller.marshal(schema, null, global, writer);
      assertXmlEqual(XML_STRING_TYPE, writer.getBuffer().toString());
   }

   public void testTermAfterUnmarshallingHandler_stringType() throws Exception
   {
      SchemaBinding schema = getSchema();
      StringReader xmlReader = new StringReader(XML_STRING_TYPE);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xmlReader, schema);
      assertNotNull(o);
      assertTrue(o instanceof GlobalElement);
      GlobalElement global = (GlobalElement) o;
      assertNotNull(global.stringType);
      assertEquals(GlobalElement.STRING_TYPE, global.stringType);
   }

   public void testTermBeforeMarshallingHandler_stringElement() throws Exception
   {
      SchemaBinding schema = getSchema();
      MarshallerImpl marshaller = getMarshaller();
      StringWriter writer = new StringWriter();
      GlobalElement global = new GlobalElement();
      global.stringElement = GlobalElement.TEXT;
      marshaller.marshal(schema, null, global, writer);
      assertXmlEqual(XML_STRING_ELEMENT, writer.getBuffer().toString());
   }

   public void testTermAfterUnmarshallingHandler_stringElement() throws Exception
   {
      SchemaBinding schema = getSchema();
      StringReader xmlReader = new StringReader(XML_STRING_ELEMENT);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xmlReader, schema);
      assertNotNull(o);
      assertTrue(o instanceof GlobalElement);
      GlobalElement global = (GlobalElement) o;
      assertNotNull(global.stringElement);
      assertEquals(GlobalElement.TEXT, global.stringElement);
   }

   public void testTermBeforeMarshallingHandler_sequenceItem() throws Exception
   {
      SchemaBinding schema = getSchema();
      MarshallerImpl marshaller = getMarshaller();
      StringWriter writer = new StringWriter();
      GlobalElement global = new GlobalElement();
      global.sequenceItem = GlobalElement.STRING_TYPE;
      marshaller.marshal(schema, null, global, writer);
      assertXmlEqual(XML_SEQUENCE_ITEM, writer.getBuffer().toString());
   }

   public void testTermAfterUnmarshallingHandler_sequenceItem() throws Exception
   {
      SchemaBinding schema = getSchema();
      StringReader xmlReader = new StringReader(XML_SEQUENCE_ITEM);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xmlReader, schema);
      assertNotNull(o);
      assertTrue(o instanceof GlobalElement);
      GlobalElement global = (GlobalElement) o;
      assertNotNull(global.sequenceItem);
      assertEquals(GlobalElement.STRING_TYPE, global.sequenceItem);
   }

   // private
   
   private MarshallerImpl getMarshaller()
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      //marshaller.addRootElement(new QName("http://jboss.org/ns/test", "global"));
      //marshaller.mapFieldToWildcard(GlobalElement.class, "anyObject", null);
      //marshaller.mapClassToGlobalElement(GlobalElement.Any.class, "intElement", "http://jboss.org/ns/test", null, null);
      return marshaller;
   }

   private static SchemaBinding getSchema()
   {
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null);
         SCHEMA.setIgnoreUnresolvedFieldOrClass(false);
         
         TypeBinding stringType = SCHEMA.getType(Constants.QNAME_STRING);

         // stringType
         stringType.setBeforeMarshallingCallback(new TermBeforeMarshallingCallback()
         {
            public Object beforeMarshalling(Object o, MarshallingContext ctx)
            {
               if(o != null)
               {
                  ParticleBinding particle = ctx.getParticleBinding();
                  assertNotNull(particle);
                  TermBinding term = particle.getTerm();
                  assertTrue(term.isElement());
                  ElementBinding element = (ElementBinding) term;
                  String localPart = element.getQName().getLocalPart();
                  assertTrue("stringType".endsWith(localPart) || "item".equals(localPart));
                  TypeBinding type = element.getType();
                  assertEquals(Constants.QNAME_STRING, type.getQName());
                  
                  o = ((GlobalElement.StringType)o).data;
               }
               return o;
            }
         });         
         stringType.setBeforeSetParentCallback(new TermBeforeSetParentCallback()
         {
            public Object beforeSetParent(Object o, UnmarshallingContext ctx)
            {
               ParticleBinding particle = ctx.getParticle();
               assertNotNull(particle);
               assertTrue(particle.getTerm().isElement());
               ElementBinding element = (ElementBinding) particle.getTerm();
               
               String eName = element.getQName().getLocalPart();
               if(eName.equals("stringType"))
               {
                  assertTrue(ctx.getParentValue() instanceof GlobalElement);
                  TermBinding parentTerm = ctx.getParentParticle().getTerm();
                  assertTrue(parentTerm.isElement());
                  assertEquals("global", ((ElementBinding)parentTerm).getQName().getLocalPart());

                  assertEquals("stringType", ctx.resolvePropertyName());
                  assertEquals(StringType.class, ctx.resolvePropertyType());
               }
               else if(eName.equals("item"))
               {
                  assertTrue(ctx.getParentValue() instanceof GlobalElement.Sequence);
                  TermBinding parentTerm = ctx.getParentParticle().getTerm();
                  assertTrue(parentTerm instanceof SequenceBinding);
                  
                  assertEquals("item", ctx.resolvePropertyName());
                  assertEquals(StringType.class, ctx.resolvePropertyType());
               }
               else
               {
                  fail("Expected stringType or item but got " + eName);
               }
               return o == null ? null : new GlobalElement.StringType((String)o);
            }
         });
         
         // have to override string type's handlers
         ElementBinding global = SCHEMA.getElement(new QName("http://jboss.org/ns/test", "global"));
         SequenceBinding sequence = (SequenceBinding) global.getType().getParticle().getTerm();
         Iterator<ParticleBinding> iter = sequence.getParticles().iterator();
         iter.next(); // stringType element
         ElementBinding stringElement = (ElementBinding) iter.next().getTerm();
         stringElement.setBeforeMarshallingCallback(new TermBeforeMarshallingCallback()
         {
            public Object beforeMarshalling(Object o, MarshallingContext ctx)
            {
               ParticleBinding particle = ctx.getParticleBinding();
               assertNotNull(particle);
               assertTrue(particle.getTerm().isElement());
               ElementBinding element = (ElementBinding) particle.getTerm();
               assertEquals(new QName("http://jboss.org/ns/test", "stringElement"), element.getQName());
               
               TypeBinding type = element.getType();
               assertEquals(Constants.QNAME_STRING, type.getQName());
               
               return o == null ? null : (String)o;
            }
         });         
         stringElement.setBeforeSetParentCallback(new TermBeforeSetParentCallback()
         {
            public Object beforeSetParent(Object o, UnmarshallingContext ctx)
            {
               ParticleBinding particle = ctx.getParticle();
               assertNotNull(particle);
               assertTrue(particle.getTerm().isElement());
               ElementBinding element = (ElementBinding) particle.getTerm();
               assertEquals(new QName("http://jboss.org/ns/test", "stringElement"), element.getQName());
               
               assertTrue(ctx.getParentValue() instanceof GlobalElement);
               TermBinding parentTerm = ctx.getParentParticle().getTerm();
               assertTrue(parentTerm.isElement());
               assertEquals("global", ((ElementBinding)parentTerm).getQName().getLocalPart());

               assertEquals(String.class, ctx.resolvePropertyType());

               return o == null ? null : (String)o;
            }
         });
         
         // sequence
         sequence = (SequenceBinding) ((ParticleBinding)iter.next()).getTerm();
         sequence.setBeforeMarshallingCallback(new TermBeforeMarshallingCallback()
         {
            public Object beforeMarshalling(Object o, MarshallingContext ctx)
            {
               ParticleBinding particle = ctx.getParticleBinding();
               assertNotNull(particle);
               assertTrue(particle.getTerm() instanceof SequenceBinding);

               if(o != null)
               {
                  GlobalElement.Sequence seq = new GlobalElement.Sequence();
                  seq.item = (StringType) o;
                  o = seq;
               }
               return o;
            }
         });
         sequence.setBeforeSetParentCallback(new TermBeforeSetParentCallback()
         {
            public Object beforeSetParent(Object o, UnmarshallingContext ctx)
            {
               ParticleBinding particle = ctx.getParticle();
               assertTrue(particle.getTerm() instanceof SequenceBinding);

               assertTrue(ctx.getParentValue() instanceof GlobalElement);
               TermBinding parentTerm = ctx.getParentParticle().getTerm();
               assertTrue(parentTerm.isElement());
               assertEquals("global", ((ElementBinding)parentTerm).getQName().getLocalPart());

               String prop = ctx.resolvePropertyName();
               assertEquals("sequenceItem", prop);

               assertEquals(StringType.class, ctx.resolvePropertyType());

               return o == null ? null : ((GlobalElement.Sequence)o).item;
            }
         });
      }
      return SCHEMA;
   }

   // inner
   
   public static class GlobalElement
   {
      public static final String TEXT = "traumeel";
      public static final StringType STRING_TYPE = new StringType(TEXT);
      
      public StringType stringType;
      public String stringElement;
      public StringType sequenceItem;
      public Object anyObject;

      public static class StringType
      {
         public String data;

         public StringType(String data)
         {
            this.data = data;
         }

         public int hashCode()
         {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((data == null) ? 0 : data.hashCode());
            return result;
         }

         public boolean equals(Object obj)
         {
            if (this == obj)
               return true;
            if (obj == null)
               return false;
            if (getClass() != obj.getClass())
               return false;
            final StringType other = (StringType) obj;
            if (data == null)
            {
               if (other.data != null)
                  return false;
            }
            else if (!data.equals(other.data))
               return false;
            return true;
         }      
      }
      
      public static class Sequence
      {
         public StringType item;
      }
   }
}
