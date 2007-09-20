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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlanyelement.support.ElementWildcard;
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

/**
 * ElementWildcardUnitTestCase.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ElementWildcardUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(ElementWildcardUnitTestCase.class);
   }
   
   public ElementWildcardUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalWildcard() throws Exception
   {
      ElementWildcard result = unmarshalObject(ElementWildcard.class);
      Element element = result.getWildcard();
      assertNotNull(element);
      assertEquals("test-element", element.getNodeName());
   }

   public void testWildcardBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(ElementWildcard.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "element-wildcard");
      ElementBinding element = schemaBinding.getElement(qName);
      assertNotNull(element);
      TypeBinding type = element.getType();
      assertNotNull(type);
      ParticleBinding particle = type.getParticle();
      assertNotNull(particle);
      TermBinding term = particle.getTerm();
      assertNotNull(term);
      assertTrue(term instanceof SequenceBinding);
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
