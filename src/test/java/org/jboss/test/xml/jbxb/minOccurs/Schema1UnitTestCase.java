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

package org.jboss.test.xml.jbxb.minOccurs;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.test.xml.jbxb.defaults.JBean;
import org.jboss.test.xml.AbstractJBossXBTest;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 41759 $
 */
public class Schema1UnitTestCase
   extends AbstractJBossXBTest
{
   public Schema1UnitTestCase(String name)
   {
      super(name);
   }

   public void testSchema1() throws Exception
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.addSchemaLocation("urn:jboss:jbxb-minOccurs-schema1", "xml/jbxb/minOccurs/schema1.xsd");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      URL testSchema1 = loader.getResource("xml/jbxb/minOccurs/testSchema1.xml");
      Object root = unmarshaller.unmarshal(testSchema1.toString(), resolver);
   }

   public void testSchema1v2() throws Exception
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.addSchemaLocation("urn:jboss:jbxb-minOccurs-schema1", "xml/jbxb/minOccurs/schema1v2.xsd");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      URL testSchema1 = loader.getResource("xml/jbxb/minOccurs/testSchema1.xml");
      Object root = unmarshaller.unmarshal(testSchema1.toString(), resolver);
   }
}
