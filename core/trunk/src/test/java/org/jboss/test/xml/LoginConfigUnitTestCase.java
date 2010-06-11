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
import java.io.InputStream;
import java.net.URL;

import junit.framework.Test;

import org.jboss.test.xml.loginconfig.Users;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * A LoginConfigUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class LoginConfigUnitTestCase extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(LoginConfigUnitTestCase.class);
   }
   
   public LoginConfigUnitTestCase(String name)
   {
      super(name);
   }

   public void testLoginConfig() throws Exception
   {
      InputStream xsdIs = openStream("xml/loginconfig/user-roles_1_0.xsd");
      InputStream xmlIs = openStream("xml/loginconfig/user-roles.xml");
      SchemaBinding schema = XsdBinder.bind(xsdIs, null);
      schema.setStrictSchema(true);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Users users = (Users) unmarshaller.unmarshal(xmlIs, schema);
      assertNotNull(users);
      assertEquals(6, users.size());
   }
   
   private InputStream openStream(String path) throws IOException
   {
      java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if (url == null)
         fail("URL not found: " + path);
      return url.openStream();
   }
}
