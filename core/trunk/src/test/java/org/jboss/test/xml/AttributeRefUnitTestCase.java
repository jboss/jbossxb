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

import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 45337 $</tt>
 */
public class AttributeRefUnitTestCase extends AbstractJBossXBTest
{
   private static final String XSD =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  targetNamespace='http://www.jboss.org/test/xml/attrRef'" +
      "  xmlns='http://www.jboss.org/test/xml/attrRef'" +
      "  xmlns:jbxb='http://www.jboss.org/xml/ns/jbxb'" +
      "  elementFormDefault='qualified'" +
      "  attributeFormDefault='unqualified'" +
      "  version='1.0'>" +
      "  <xsd:attribute name='attrRef' type='xsd:string'/>" +
      "  <xsd:element name='top' type='someType'>" +
      "    <xsd:annotation>" +
      "      <xsd:appinfo>" +
      "        <jbxb:class impl='" + Top.class.getName() + "'/>" +
      "      </xsd:appinfo>" +
      "    </xsd:annotation>" +
      "  </xsd:element>" +
      "  <xsd:complexType name='someType'>" +
      "    <xsd:attribute name='attr' type='xsd:string' use='required'/>" +
      "    <xsd:attribute ref='attrRef' use='required'/>" +
      "  </xsd:complexType>" +
      "</xsd:schema>";
                    
   private static final String XML =
      "<ns:top xmlns:ns='http://www.jboss.org/test/xml/attrRef' attr='attr' ns:attrRef='attrRef'/>";

   public AttributeRefUnitTestCase(String name)
   {
      super(name);
   }

   public void testMain() throws Exception
   {
      //enableTrace("org.jboss.xb.binding.sunday.unmarshalling.XsdBinder");
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);      
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);
      assertNotNull(o);
      assertTrue(o instanceof Top);
      Top top = (Top) o;
      assertEquals("attr", top.attr);
      assertEquals("attrRef", top.attrRef);
   }
   
   public static class Top
   {
      public String attr;
      public String attrRef;
   }
}
