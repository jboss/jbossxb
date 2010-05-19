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

import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.jboss.test.xml.naming.JNDIBinding;
import org.jboss.test.xml.naming.JNDIBindings;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.resolver.MultiClassSchemaResolver;

/**
 * A JNDIBindingUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JNDIBindingUnitTestCase extends AbstractJBossXBTest
{
   public JNDIBindingUnitTestCase(String name)
   {
      super(name);
   }

   public void testJNDIBinding() throws Exception
   {
      MultiClassSchemaResolver resolver = new MultiClassSchemaResolver();
      resolver.mapSchemaLocation("urn:jboss:jndi-binding-service:1.0", "xml/naming/jndi-binding-service_1_0.xsd");
      resolver.mapSchemaLocation("urn:jboss:custom-object-binding", "xml/naming/custom-object-binding.xsd");

      String xml = getFullPath("xml/naming/testBindings.xml");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      JNDIBindings jndiBindings = (JNDIBindings) unmarshaller.unmarshal(xml, resolver);
      assertNotNull(jndiBindings);
      JNDIBinding[] bindings = jndiBindings.getBindings();
      assertNotNull(bindings);
      assertEquals(5, bindings.length);
      
      JNDIBinding binding = bindings[0];
      assertNotNull(binding);
      assertEquals("ctx1/key1", binding.getName());
      assertEquals("value1", binding.getValue());
      assertNull(binding.getEditor());
      assertNull(binding.getType());
      assertTrue(binding.isTrim());
      
      binding = bindings[1];
      assertNotNull(binding);
      assertEquals("ctx1/user.home", binding.getName());
      assertEquals(System.getProperty("user.home"), binding.getValue());
      assertNull(binding.getEditor());
      assertNull(binding.getType());
      assertTrue(binding.isTrim());
      
      binding = bindings[2];
      assertNotNull(binding);
      assertEquals("ctx1/key2", binding.getName());
      assertEquals(new URL("http://www.jboss.org"), binding.getValue());
      assertNull(binding.getEditor());
      assertEquals("java.net.URL", binding.getType());
      assertTrue(binding.isTrim());

      binding = bindings[3];
      assertNotNull(binding);
      assertEquals("ctx2/key1", binding.getName());
      Properties props = (Properties) binding.getValue();
      assertNotNull(props);
      assertEquals(2, props.size());
      assertEquals("value1", props.getProperty("key1"));
      assertEquals("value2", props.getProperty("key2"));
      assertNull(binding.getEditor());
      assertNull(binding.getType());
      assertFalse(binding.isTrim());

      binding = bindings[4];
      assertNotNull(binding);
      assertEquals("hosts/localhost", binding.getName());
      assertEquals(InetAddress.getByName("127.0.0.1"), binding.getValue());
      assertEquals("org.jboss.util.propertyeditor.InetAddressEditor", binding.getEditor());
      assertNull(binding.getType());
      assertTrue(binding.isTrim());
   }
   
   private String getFullPath(String name)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(name);
      if(url == null)
      {
         fail("Resource not found: " + name);
      }
      return url.getFile();
   }
}
