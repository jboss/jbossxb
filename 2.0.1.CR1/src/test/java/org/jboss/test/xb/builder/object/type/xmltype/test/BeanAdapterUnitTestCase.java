/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xb.builder.object.type.xmltype.test;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmltype.adapter.Adapted;
import org.jboss.test.xb.builder.object.type.xmltype.adapter.AdaptedSubclass;
import org.jboss.test.xb.builder.object.type.xmltype.adapter.TestBeanAdapterFactory;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;
import org.jboss.xb.builder.runtime.BeanHandler;
import org.jboss.xb.spi.BeanAdapterFactory;

/**
 * AbstractMCTest.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class BeanAdapterUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(BeanAdapterUnitTestCase.class);
   }
   
   public BeanAdapterUnitTestCase(String name)
   {
      super(name);
   }

   public void testAdaptedUnmarshal() throws Exception
   {
      Adapted result = unmarshalObject(Adapted.class);
      // Class should have been changed by the adapter 
      assertTrue(result instanceof AdaptedSubclass);
      // Properties should have been swapped by the adapter 
      assertEquals("property1", result.property2);
      assertEquals("property2", result.property1);
   }

   public void testAdaptedTypeBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(Adapted.class);
      assertNotNull(schemaBinding);
      
      // Check the type
      QName qName = new QName("testNamespace", "adapted");
      TypeBinding type = schemaBinding.getType(qName);
      assertNotNull(type);
      assertEquals(qName, type.getQName());
      
      QName elementName = new QName(XMLConstants.NULL_NS_URI, "adapted");
      ElementBinding elementBinding = schemaBinding.getElement(elementName);
      assertNotNull(elementBinding);
      TypeBinding typeBinding = elementBinding.getType();
      assertTrue(type == typeBinding);
      
      ParticleHandler particleHandler = typeBinding.getHandler();
      assertNotNull(particleHandler);
      assertTrue(particleHandler instanceof BeanHandler);
      BeanHandler beanInfoElementHandler = (BeanHandler) particleHandler;
      BeanAdapterFactory beanAdapterFactory = beanInfoElementHandler.getBeanAdapterFactory();
      assertTrue(beanAdapterFactory instanceof TestBeanAdapterFactory);
   }
}
