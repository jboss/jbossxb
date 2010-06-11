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

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;

import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.w3c.dom.Element;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46112 $</tt>
 */
public class AnyTypeDomBindingUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(AnyTypeDomBindingUnitTestCase.class);
   }
   
   public AnyTypeDomBindingUnitTestCase(String name)
   {
      super(name);
   }

   public void testComplexContentUnmarshalling() throws Exception
   {
      SchemaBinding schema = bindSchema();

      Object unmarshalled = unmarshal(rootName + "_complexContent.xml", schema);
      
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof MyType);
      
      MyType mt = (MyType) unmarshalled;
      
      assertNotNull(mt.anything);
      assertTrue(mt.anything.toString(), mt.anything instanceof Element);
      
      Element dom = (Element) mt.anything;
      assertEquals("dom", dom.getLocalName());
      assertEquals(null, dom.getNamespaceURI());
      assertEquals("test", dom.getTextContent());
   }

   public void testSimpleContentUnmarshalling() throws Exception
   {
      SchemaBinding schema = bindSchema();

      Object unmarshalled = unmarshal(rootName + "_simpleContent.xml", schema);
      
      assertNotNull(unmarshalled);
      assertTrue(unmarshalled instanceof MyType);
      
      MyType mt = (MyType) unmarshalled;
      
      assertNotNull(mt.anything);
      assertTrue(mt.anything instanceof String);
      assertEquals("test", mt.anything);
   }

   public void testComplexContentMarshalling() throws Exception
   {
      SchemaBinding schema = bindSchema();
      
      DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Element dom = domBuilder.newDocument().createElement("dom");
      dom.setTextContent("test");

      MyType root = new MyType();
      root.anything = dom;
      
      MarshallerImpl marshaller = new MarshallerImpl();
      StringWriter writer = new StringWriter();
      marshaller.marshal(schema, null, root, writer);
      
      assertXmlFileContent(rootName + "_complexContent.xml", writer.getBuffer().toString());
   }

   public void testSimpleContentMarshalling() throws Exception
   {
      SchemaBinding schema = bindSchema();
      
      DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Element dom = domBuilder.newDocument().createElementNS("http://ns1", "dom");
      dom.setTextContent("test");

      MyType root = new MyType();
      root.anything = "test";
      
      MarshallerImpl marshaller = new MarshallerImpl();
      StringWriter writer = new StringWriter();
      marshaller.marshal(schema, null, root, writer);
      
      assertXmlFileContent(rootName + "_simpleContent.xml", writer.getBuffer().toString());
   }

   private SchemaBinding bindSchema() throws Exception
   {
      SchemaBinding schema = bind(rootName + ".xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);
      
      //schema.setUnresolvedContentBoundToDOM(true);
      // is true by default and equivalent to
      /*
      TypeBinding myType = schema.getType(new QName("http://www.jboss.org/xb/test/any", "myType"));
      SequenceBinding seq = (SequenceBinding)myType.getParticle().getTerm();
      ElementBinding anything = (ElementBinding)((ParticleBinding)seq.getParticles().iterator().next()).getTerm();
      TypeBinding anyType = anything.getType();
      WildcardBinding wc = anyType.getWildcard();
      
      wc.setUnresolvedElementHandler(DomParticleHandler.INSTANCE);
      wc.setUnresolvedCharactersHandler(DomCharactersHandler.INSTANCE);
      wc.setUnresolvedMarshaller(DomLocalMarshaller.INSTANCE);
      */

      assertTrue(schema.isUnresolvedContentBoundToDOM());

      return schema;
   }

   public static class MyType
   {
      public Object anything;
   }
}
