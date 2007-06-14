/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors as indicated
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
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;


/**
 * IgnorableWhitespaceUnitTestCase.
 * 
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision$
 */
public class IgnorableWhitespaceUnitTestCase extends AbstractJBossXBTest
{
   private static final String NS = "http://www.jboss.org/test/xml/simpleContent";
 
   private static final String XSD =
      "<?xml version='1.0' encoding='UTF-8'?>" +
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      " targetNamespace='http://www.jboss.org/test/xml/simpleContent'" +
      " xmlns='http://www.jboss.org/test/xml/simpleContent'" +
      " elementFormDefault='qualified'" +
      " attributeFormDefault='unqualified'" +
      " version='1.0'>" +
      " <xsd:element name='top'>" +
      "  <xsd:complexType>" +
      "   <xsd:sequence>" +
      "    <xsd:element name='string' type='myString' minOccurs='0' maxOccurs='unbounded'/>" +
      "   </xsd:sequence>" +
      "  </xsd:complexType>" +
      " </xsd:element>" +
      " <xsd:complexType name='myString'>" +
      "  <xsd:simpleContent>" +
      "   <xsd:extension base='xsd:string'>" +
      "    <xsd:attribute name='id' type='xsd:ID'/>" +
      "   </xsd:extension>" +
      "  </xsd:simpleContent>" +
      " </xsd:complexType>" +
      "</xsd:schema>";

   public static final TestSuite suite()
   {
      return new TestSuite(IgnorableWhitespaceUnitTestCase.class);
   }
   
   public IgnorableWhitespaceUnitTestCase(String name)
   {
      super(name);
   }

   public void testCollectionOverrideProperty() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);

      schema.setIgnoreUnresolvedFieldOrClass(false);
      schema.setIgnoreWhitespacesInMixedContent(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);
      
      Top top = (Top) unmarshal("IgnorableWhitespaceContent.xml", schema, Top.class);
      assertNotNull(top.string);
      assertEquals(1, top.string.size());
      assertEquals(" ", top.string.get(0));
   }
   
   public static class Top
   {
      public List string;
   }
}
