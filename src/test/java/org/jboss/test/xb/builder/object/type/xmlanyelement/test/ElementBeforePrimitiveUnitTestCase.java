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
package org.jboss.test.xb.builder.object.type.xmlanyelement.test;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlanyelement.support.ElementBeforePrimitive;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A ElementBeforePrimitiveUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ElementBeforePrimitiveUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(ElementBeforePrimitiveUnitTestCase.class);
   }

   public ElementBeforePrimitiveUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      ElementBeforePrimitive o = unmarshalObject(ElementBeforePrimitive.class);
      Element element = o.getDom();
      assertNotNull(element);
      assertEquals("dom", element.getNodeName());
      NodeList childNodes = element.getChildNodes();
      assertNotNull(childNodes);
      assertEquals(2, childNodes.getLength());
      element = (Element) childNodes.item(0);
      assertEquals("sweet", element.getNodeName());
      
      assertEquals("frustration is not professional", o.getText());
   }
   
   public void testBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(ElementBeforePrimitive.class);
      assertNotNull(schemaBinding);
      
      QName qName = new QName(XMLConstants.NULL_NS_URI, "element-before-primitive");
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
      assertEquals(2, particles.size());

      Iterator<ParticleBinding> i = particles.iterator();
      particle = i.next();
      term = particle.getTerm();
      assertTrue(term.isElement());
      assertEquals(0, particle.getMinOccurs());
      assertEquals(1, particle.getMaxOccurs());
      assertFalse(particle.getMaxOccursUnbounded());
      element = (ElementBinding) term;
      assertEquals(new QName("dom"), element.getQName());

      particle = i.next();
      term = particle.getTerm();
      assertTrue(term.isElement());
      assertEquals(0, particle.getMinOccurs());
      assertEquals(1, particle.getMaxOccurs());
      assertFalse(particle.getMaxOccursUnbounded());
      element = (ElementBinding) term;
      assertEquals(new QName("text"), element.getQName());
   }
}
