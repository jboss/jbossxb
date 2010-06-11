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
package org.jboss.test.xb.builder.object.jbossxmltransient.test;

import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.jbossxmltransient.support.ExtendedBase;
import org.jboss.test.xb.builder.object.jbossxmltransient.support.ExtendedJBossObject;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A JBossXmlTransientTypeAndPropertiesUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossXmlTransientTypeAndPropertiesUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(JBossXmlTransientTypeAndPropertiesUnitTestCase.class);
   }
   
   public JBossXmlTransientTypeAndPropertiesUnitTestCase(String name)
   {
      super(name);
   }

   public void testExtendedJBossObject() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(ExtendedJBossObject.class, true);
      ElementBinding root = schema.getElement(new QName("root"));
      assertNotNull(root);
      ParticleBinding particle = root.getType().getParticle();
      assertNotNull(particle);
      ModelGroupBinding group = (ModelGroupBinding) particle.getTerm();
      assertTrue(group.getParticles().isEmpty());
   }
   
   public void testAllProperties() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(ExtendedBase.class, true);
      ElementBinding root = schema.getElement(new QName("root"));
      assertNotNull(root);
      ParticleBinding particle = root.getType().getParticle();
      assertNotNull(particle);
      ModelGroupBinding group = (ModelGroupBinding) particle.getTerm();
      assertTrue(group.getParticles().isEmpty());
   }
}
