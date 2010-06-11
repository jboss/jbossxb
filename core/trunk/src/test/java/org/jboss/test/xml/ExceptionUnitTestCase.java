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

import junit.framework.Test;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 41107 $</tt>
 */
public class ExceptionUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(ExceptionUnitTestCase.class);
   }
   
   private static final String XSD =
      "<schema targetNamespace='http://jboss.org/xml/test/exc'" +
      " elementFormDefault='qualified'" +
      " xmlns:tns='http://jboss.org/xml/test/exc'" +
      " xmlns:jbxb='" + Constants.NS_JBXB + "'" +
      " xmlns='http://www.w3.org/2001/XMLSchema'>" +
      " <complexType name='ExceptionType'>" +
      "   <annotation>" +
      "     <appinfo>" +
      "       <jbxb:class impl='" + Exception.class.getName() + "'/>" +
      "     </appinfo>" +
      "   </annotation>" +
      "   <sequence>" +
      "     <element name='message' type='string' minOccurs='0' maxOccurs='1'/>" +
      "     <element name='cause' type='tns:ExceptionType' minOccurs='0' maxOccurs='1'/>" +
      "   </sequence>" +
      " </complexType>" +
      " <element name='exc' type='tns:ExceptionType'/>" +
      "</schema>";

   private static final String EXC_XML =
      "<exc xmlns='http://jboss.org/xml/test/exc'/>";

   private static final String EXC_MSG_XML =
      "<exc xmlns='http://jboss.org/xml/test/exc'>" +
      "  <message>didn't work</message>" +
      "</exc>";

   private static final String EXC_CAUSE_XML =
      "<exc xmlns='http://jboss.org/xml/test/exc'>" +
      "  <cause>" +
      "    <message>didn't work</message>" +
      "  </cause>" +
      "</exc>";

   private static final String EXC_MSG_CAUSE_XML =
      "<exc xmlns='http://jboss.org/xml/test/exc'>" +
      "  <message>see cause</message>" +
      "  <cause>" +
      "    <message>didn't work</message>" +
      "  </cause>" +
      "</exc>";

   private static SchemaBinding SCHEMA;

   static
   {
/*
      Validator.assertValidXml(XSD, EXC_XML);
      Validator.assertValidXml(XSD, EXC_MSG_XML);
      Validator.assertValidXml(XSD, EXC_CAUSE_XML);
      Validator.assertValidXml(XSD, EXC_MSG_CAUSE_XML);
*/
   }

   public ExceptionUnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
      //enableTrace(XsdBinder.class.getName());
      if(SCHEMA == null)
      {
         SCHEMA = XsdBinder.bind(new StringReader(XSD), null);
      }
   }

   public void testUnmarshallingExcXml() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(EXC_XML), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof Exception);
      Exception e = (Exception)o;
      assertNull(e.getMessage());
      assertNull(e.getCause());
   }

   public void testUnmarshallingExcMsgXml() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(EXC_MSG_XML), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof Exception);
      Exception e = (Exception)o;
      assertEquals("didn't work", e.getMessage());
      assertNull(e.getCause());
   }

   public void testUnmarshallingExcCauseXml() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(EXC_CAUSE_XML), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof Exception);
      Exception e = (Exception)o;
      assertEquals("java.lang.Exception: didn't work", e.getMessage());
      assertNotNull(e.getCause());
      e = (Exception)e.getCause();
      assertEquals("didn't work", e.getMessage());
      assertNull(e.getCause());
   }

   public void testUnmarshallingExcMsgCauseXml() throws Exception
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(EXC_MSG_CAUSE_XML), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof Exception);
      Exception e = (Exception)o;
      assertEquals("see cause", e.getMessage());
      assertNotNull(e.getCause());
      e = (Exception)e.getCause();
      assertEquals("didn't work", e.getMessage());
      assertNull(e.getCause());
   }
}
