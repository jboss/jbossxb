/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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

import javax.xml.namespace.QName;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * A XsiNilUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class XsiNilUnitTestCase extends AbstractJBossXBTest
{
   private static String XSD =
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns='http://www.w3.org/2001/XMLSchema'" +
      "  xmlns:jbxb='" + Constants.NS_JBXB + "'>" +
      "<xsd:element name='root' nillable='true'>" +
      "  <xsd:complexType>" +
      "    <annotation>" +
      "      <appinfo>" +
      "        <jbxb:class impl='" + Root.class.getName() + "'/>" +
      "      </appinfo>" +
      "    </annotation>" +
      "  </xsd:complexType>" +
      "</xsd:element>" +
      "</xsd:schema>";
   
   private static String XML_NIL_1 = "<root xsi:nil='1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'/>";
   private static String XML_NIL_0 = "<root xsi:nil='0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'/>";
   private static String XML = "<root/>";
   
   public XsiNilUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);
      ElementBinding rootBinding = schema.getElement(new QName("root"));
      assertNotNull(rootBinding);
      assertTrue(rootBinding.isNillable());
      
      Root root = (Root) UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(XML), schema);
      assertNotNull(root);
      root = (Root) UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(XML_NIL_1), schema);
      assertNull(root);
      root = (Root) UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(XML_NIL_0), schema);
      assertNotNull(root);
   }
   
   public static class Root
   {
   }
}
