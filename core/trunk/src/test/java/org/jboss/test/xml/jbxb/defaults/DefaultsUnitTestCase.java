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
package org.jboss.test.xml.jbxb.defaults;

import java.io.InputStream;
import java.io.IOException;

import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 37406 $
 */
public class DefaultsUnitTestCase
   extends TestCase
{
   public static Test suite()
   {
      return new TestSuite(DefaultsUnitTestCase.class);
   }
   
   public void testSchema1() throws Exception
   {
      InputStream xsd = getResource("xml/jbxb/defaults/schema1.xsd");
      SchemaBinding schemaBinding = XsdBinder.bind(xsd, "UTF-8");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      InputStream is = getResource("xml/jbxb/defaults/testSchema1.xml");
      JBean bean = (JBean) unmarshaller.unmarshal(is, schemaBinding);
      is.close();
      int attr2 = bean.getAttr2();
      assertEquals("JBean.Attr2("+attr2+") == 123", 123, attr2);
      String attr1 = bean.getAttr1();
      assertEquals("JBean.Attr1("+attr1+") == DefaultVaue", "DefaultValue", attr1);

      boolean attr3 = bean.getAttr3();
      assertEquals("JBean.Attr3("+attr3+") == true", true, attr3);
   }

   public void testSchema2() throws Exception
   {
      InputStream xsd = getResource("xml/jbxb/defaults/schema2.xsd");
      SchemaBinding schemaBinding = XsdBinder.bind(xsd, "UTF-8");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      InputStream is = getResource("xml/jbxb/defaults/testSchema2.xml");
      JBean bean = (JBean) unmarshaller.unmarshal(is, schemaBinding);
      is.close();
      int attr2 = bean.getAttr2();
      assertEquals("JBean.Attr2("+attr2+") == 123", 123, attr2);
      String attr1 = bean.getAttr1();
      assertEquals("JBean.Attr1("+attr1+") == DefaultVaue", "DefaultValue", attr1);

      boolean attr3 = bean.getAttr3();
      assertEquals("JBean.Attr3("+attr3+") == true", true, attr3);
   }

   private InputStream getResource(String path)
      throws IOException
   {
      java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if(url == null)
      {
         fail("URL not found: " + path);
      }
      return url.openStream();
   }
}
