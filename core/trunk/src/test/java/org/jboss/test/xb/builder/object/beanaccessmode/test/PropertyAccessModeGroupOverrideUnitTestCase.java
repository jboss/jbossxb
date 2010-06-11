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
package org.jboss.test.xb.builder.object.beanaccessmode.test;

import java.util.Collection;

import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.beanaccessmode.support.PropertyAccessModeGroupOverride;
import org.jboss.test.xb.builder.object.beanaccessmode.support.PropertyAccessModeGroupOverride.PropertyAccessModeGroup;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A PropertyAccessModeTypeOverrideUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class PropertyAccessModeGroupOverrideUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(PropertyAccessModeGroupOverrideUnitTestCase.class);
   }

   public PropertyAccessModeGroupOverrideUnitTestCase(String name)
   {
      super(name);
   }

   public void testBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(PropertyAccessModeGroupOverride.class, true);
      ElementBinding root = schema.getElement(new QName("root"));
      assertNotNull(root);
      TypeBinding type = root.getType();
      assertEquals(3, type.getAttributes().size());
      ModelGroupBinding group = (ModelGroupBinding)type.getParticle().getTerm();
      Collection<ParticleBinding> particles = group.getParticles();
      assertEquals(1, particles.size());
      AllBinding all = (AllBinding) particles.iterator().next().getTerm();
      particles = all.getParticles();
      assertEquals(1, particles.size());
      ElementBinding e = (ElementBinding) particles.iterator().next().getTerm();
      assertEquals(new QName("property"), e.getQName());
   }
   
   public void testUnmarshalling() throws Exception
   {
      PropertyAccessModeGroupOverride root = unmarshalObject(PropertyAccessModeGroupOverride.class);
      assertEquals("property", root.getProperty());
      assertEquals("fields", root.publicField);
      assertEquals("all", root.privateField());
      PropertyAccessModeGroup e = root.e;
      assertNotNull(e);
      assertEquals("property", e.getProperty());
      assertNull(e.publicField);
      assertNull(e.privateField());
   }
}
