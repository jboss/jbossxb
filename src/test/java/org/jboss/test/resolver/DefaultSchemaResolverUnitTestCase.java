/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.resolver;

import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.ejb.metadata.spec.EjbJar20MetaData;
import org.jboss.ejb.metadata.spec.EjbJar21MetaData;
import org.jboss.ejb.metadata.spec.EjbJar30MetaData;
import org.jboss.test.xml.AbstractJBossXBTest;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DefaultSchemaResolverUnitTestCase extends AbstractJBossXBTest
{
   public DefaultSchemaResolverUnitTestCase(String name)
   {
      super(name);
   }

   public void testSchemaByLocation()
      throws Exception
   {
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      String[] locations = {"http://java.sun.com/dtd/ejb-jar_2_0.dtd",
            "http://java.sun.com/xml/ns/j2ee/ejb-jar_2_1.xsd",
            "http://java.sun.com/xml/ns/javaee/ejb-jar_3_0.xsd"};
      String[] shortLocations = {"ejb-jar_2_0.dtd",
            "ejb-jar_2_1.xsd",
            "ejb-jar_3_0.xsd"};
      Class[] classes = {EjbJar20MetaData.class,
            EjbJar21MetaData.class,
            EjbJar30MetaData.class};
      for(int n = 0; n < locations.length; n ++)
         resolver.addClassBindingForLocation(locations[n], classes[n]);

      for(int n = 0; n < locations.length; n ++)
      {
         SchemaBinding schema = resolver.resolve("", null, locations[n]);
         assertNotNull(schema);
      }

      for(int n = 0; n < locations.length; n ++)
      {
         Class c = resolver.removeClassBindingForLocation(locations[n]);
         assertNotNull(c);
      }
      for(int n = 0; n < locations.length; n ++)
         resolver.addClassBindingForLocation(shortLocations[n], classes[n]);
      // Test that the full name resolves to the short name
      for(int n = 0; n < locations.length; n ++)
      {
         SchemaBinding schema = resolver.resolve("", null, locations[n]);
         assertNotNull(schema);
      }
      
      // Test the schema binding root elements
      SchemaBinding schema = resolver.resolve("", null, "ejb-jar_2_0.dtd");
      assertNotNull(schema);
      QName ejbJar20Name = new QName("", "ejb-jar");
      ElementBinding ejbJar20 = schema.getElement(ejbJar20Name);
      assertNotNull(ejbJar20);

      schema = resolver.resolve("", null, "ejb-jar_2_1.xsd");
      assertNotNull(schema);
      QName ejbJar21Name = new QName("http://java.sun.com/xml/ns/j2ee", "ejb-jar");
      ElementBinding ejbJar21 = schema.getElement(ejbJar21Name);
      assertNotNull(ejbJar21);

      schema = resolver.resolve("", null, "ejb-jar_3_0.xsd");
      assertNotNull(schema);
      QName ejbJar3xName = new QName("http://java.sun.com/xml/ns/javaee", "ejb-jar");
      ElementBinding ejbJar3x = schema.getElement(ejbJar3xName);
      assertNotNull(ejbJar3x);
   }
   
   public void testRedefine() throws Exception
   {
      //enableTrace("org.jboss.util.xml");

      /**
       * it *has to* be in the classpath, not found with findXML()
       */
      String redefiningName = getRootName() + "_" + getName() + "_redefining.xsd";
      URL redefiningURL = Thread.currentThread().getContextClassLoader().getResource(redefiningName);
      assertNotNull("Expected to find " + redefiningName + " in the classpath", redefiningURL);
      
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.addSchemaLocation("urn:jboss:xb:test", redefiningName);
      Util.loadSchema(redefiningURL.toExternalForm(), resolver);
   }
}
