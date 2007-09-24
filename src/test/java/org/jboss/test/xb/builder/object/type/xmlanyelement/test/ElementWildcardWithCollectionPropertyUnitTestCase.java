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
package org.jboss.test.xb.builder.object.type.xmlanyelement.test;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlanyelement.support.ElementWildcard;
import org.jboss.test.xb.builder.object.type.xmlanyelement.support.ElementWildcardWithCollectionProperty;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.jboss.xb.builder.JBossXBBuilder;
import org.jboss.xb.builder.runtime.DOMHandler;
import org.jboss.xb.builder.runtime.PropertyWildcardHandler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ElementWildcardUnitTestCase.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ElementWildcardWithCollectionPropertyUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(ElementWildcardWithCollectionPropertyUnitTestCase.class);
   }
   
   public ElementWildcardWithCollectionPropertyUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalWildcardWithProperties() throws Exception
   {
      ElementWildcardWithCollectionProperty result = unmarshalObject(ElementWildcardWithCollectionProperty.class);
      Collection<String> properties = result.getProperties();
      ArrayList<String> expected = new ArrayList<String>();
      expected.add("1");
      expected.add("2");
      assertEquals(expected, properties);
      Element element = result.getWildcard();
      assertNotNull(element);
      assertEquals("test-element", element.getNodeName());
      NodeList childNodes = element.getChildNodes();
      assertNotNull(childNodes);
      assertEquals(1, childNodes.getLength());
      element = (Element) childNodes.item(0);
      assertEquals("test-child-element", element.getNodeName());
   }

   // TODO Fix this
   public void testWildcardWithPropertiesBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(ElementWildcardWithCollectionProperty.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "element-wildcard-with-collection-property");
      ElementBinding element = schemaBinding.getElement(qName);
      assertNotNull(element);
      TypeBinding type = element.getType();
      assertNotNull(type);
      ParticleBinding particle = type.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding);
      // i don't think the following assertion is wrong
      term = assertSingleSequence(term);
      assertTrue(term instanceof WildcardBinding);
      WildcardBinding wildcardBinding = type.getWildcard();
      assertNotNull(wildcardBinding);
      assertTrue(term == wildcardBinding);
      assertTrue(wildcardBinding.isProcessContentsLax());
      assertTrue(DOMHandler.INSTANCE == wildcardBinding.getUnresolvedCharactersHandler());
      assertTrue(DOMHandler.INSTANCE == wildcardBinding.getUnresolvedElementHandler());
      ParticleHandler particleHandler = wildcardBinding.getWildcardHandler();
      assertNotNull(particleHandler);
      assertTrue(particleHandler instanceof PropertyWildcardHandler);
   }
}
