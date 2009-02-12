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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlanyelement.support.ElementPropertiesAndWildcard;
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
public class ElementPropertiesAndWildcardUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(ElementPropertiesAndWildcardUnitTestCase.class);
   }
   
   public ElementPropertiesAndWildcardUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalWildcard() throws Exception
   {
      ElementPropertiesAndWildcard result = unmarshalObject(ElementPropertiesAndWildcard.class);
      
      Element element = result.getE1();
      assertNotNull(element);
      assertEquals("e1", element.getNodeName());
      NodeList childNodes = element.getChildNodes();
      assertNotNull(childNodes);
      assertEquals(1, childNodes.getLength());
      element = (Element) childNodes.item(0);
      assertEquals("e1-child", element.getNodeName());

      element = result.getE2();
      assertNotNull(element);
      assertEquals("e2", element.getNodeName());
      childNodes = element.getChildNodes();
      assertNotNull(childNodes);
      assertEquals(1, childNodes.getLength());
      element = (Element) childNodes.item(0);
      assertEquals("e2-child", element.getNodeName());
      
      element = result.getOther();
      assertNotNull(element);
      assertEquals("e3", element.getNodeName());
      childNodes = element.getChildNodes();
      assertNotNull(childNodes);
      assertEquals(1, childNodes.getLength());
      element = (Element) childNodes.item(0);
      assertEquals("e3-child", element.getNodeName());
   }

   public void testWildcardBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(ElementPropertiesAndWildcard.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "element-properties-and-wildcard");
      ElementBinding element = schemaBinding.getElement(qName);
      assertNotNull(element);
      TypeBinding type = element.getType();
      assertNotNull(type);
      ParticleBinding particle = type.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding);
      
      
      Collection<ParticleBinding> particles = ((SequenceBinding)term).getParticles();
      assertEquals(3, particles.size());
      
      Iterator<ParticleBinding> i = particles.iterator();
      particle = i.next();
      term = particle.getTerm();
      assertTrue(particle.getTerm().isElement());
      assertEquals(0, particle.getMinOccurs());
      assertEquals(1, particle.getMaxOccurs());
      assertFalse(particle.getMaxOccursUnbounded());
      element = (ElementBinding) term;
      assertEquals(new QName("e1"), element.getQName());
      particles = ((SequenceBinding)element.getType().getParticle().getTerm()).getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();
      assertWildcardTerm(element.getType(), particle, (short) 2);

      particle = i.next();
      term = particle.getTerm();
      assertTrue(particle.getTerm().isElement());
      assertEquals(0, particle.getMinOccurs());
      assertEquals(1, particle.getMaxOccurs());
      assertFalse(particle.getMaxOccursUnbounded());
      element = (ElementBinding) term;
      assertEquals(new QName("e2"), element.getQName());
      particles = ((SequenceBinding)element.getType().getParticle().getTerm()).getParticles();
      assertEquals(1, particles.size());
      particle = particles.iterator().next();
      assertWildcardTerm(element.getType(), particle, (short) 2);

      particle = i.next();
      assertWildcardTerm(type, particle, (short) 3);
      WildcardBinding wildcardBinding = type.getWildcard();
      ParticleHandler particleHandler = wildcardBinding.getWildcardHandler();
      assertNotNull(particleHandler);
      assertTrue(particleHandler instanceof PropertyWildcardHandler);
   }

   private void assertWildcardTerm(TypeBinding type, ParticleBinding particle, short pc)
   {
      TermBinding term;
      term = particle.getTerm();
      assertTrue(particle.getTerm().isWildcard());
      assertEquals(0, particle.getMinOccurs());
      assertEquals(1, particle.getMaxOccurs());
      assertFalse(particle.getMaxOccursUnbounded());

      WildcardBinding wildcardBinding = type.getWildcard();
      assertNotNull(wildcardBinding);
      assertTrue(term == wildcardBinding);
      assertEquals(pc, wildcardBinding.getProcessContents());
      assertTrue(DOMHandler.INSTANCE == wildcardBinding.getUnresolvedCharactersHandler());
      assertTrue(DOMHandler.INSTANCE == wildcardBinding.getUnresolvedElementHandler());
   }
}
