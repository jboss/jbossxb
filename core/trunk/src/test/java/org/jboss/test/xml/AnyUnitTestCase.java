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

import java.io.FileReader;
import java.net.URL;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xml.collections.Collections;
import org.jboss.test.xml.any.Container;
import org.jboss.test.BaseTestCase;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.w3c.dom.ls.LSInput;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 38057 $</tt>
 */
public class AnyUnitTestCase
   extends BaseTestCase
{
   public static Test suite()
   {
      return suite(AnyUnitTestCase.class);
   }
   
   public AnyUnitTestCase(String localName)
   {
      super(localName);
   }

/*
   public void configureLogging()
   {
      enableTrace("org.jboss.xb");
   }
*/

   public void testMain() throws Exception
   {
      String xsd = getFullPath("xml/any.xsd");
      SchemaBinding schema = XsdBinder.bind(xsd);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      schema.setSchemaResolver(new SchemaBindingResolver()
      {
         public String getBaseURI()
         {
            throw new UnsupportedOperationException("getBaseURI is not implemented.");
         }

         public void setBaseURI(String baseURI)
         {
            throw new UnsupportedOperationException("setBaseURI is not implemented.");
         }

         public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
         {
            SchemaBinding schema = null;
            String ns = "http://www.jboss.org/test/xml/collections";
            if(ns.equals(nsUri))
            {
               String xsd = getFullPath("xml/collections.xsd");
               schema = XsdBinder.bind(xsd);
               QName rootQName = new QName(ns, "collections");
               TypeBinding type = schema.getType(rootQName);
               schema.addElement(rootQName, type);
            }
            return schema;
         }

         public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
         {
            throw new UnsupportedOperationException("resolveResource is not implemented.");
         }
      }
      );

      String xml = getFullPath("xml/any.xml");
      FileReader xmlReader = new FileReader(xml);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xmlReader, schema);

      Container container = new Container();
      container.setId("any test");
      container.setAnyContent(Collections.getInstance());
      assertEquals(container, o);
   }

   // Private

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
