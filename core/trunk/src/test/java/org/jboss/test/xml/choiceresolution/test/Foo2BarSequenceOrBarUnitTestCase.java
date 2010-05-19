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
package org.jboss.test.xml.choiceresolution.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.test.xml.AbstractJBossXBTest;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;

/**
 * A ChoiceResolutionUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class Foo2BarSequenceOrBarUnitTestCase extends AbstractJBossXBTest
{
   public Foo2BarSequenceOrBarUnitTestCase(String name)
   {
      super(name);
   }

   public void testBar() throws Exception
   {
      String xml = findXML(getRootName() + '_' + getName() + ".xml");
      SchemaBinding schema = getSchemaBinding();
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xml, schema);
      assertNotNull(o);
      Root root = (Root) o;
      assertNull(root.foo);
      assertNull(root.barMaxOccurs2);
      assertEquals("bar", root.bar);
   }

   public void testFooBar() throws Exception
   {
      String xml = findXML(getRootName() + '_' + getName() + ".xml");
      SchemaBinding schema = getSchemaBinding();
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xml, schema);
      assertNotNull(o);
      Root root = (Root) o;
      assertEquals("foo", root.foo);
      assertNotNull(root.barMaxOccurs2);
      assertEquals(1, root.barMaxOccurs2.size());
      assertEquals("bar", root.barMaxOccurs2.get(0));
      assertNull(root.bar);
   }

   public void testFoo2Bar() throws Exception
   {
      String xml = findXML(getRootName() + '_' + getName() + ".xml");
      SchemaBinding schema = getSchemaBinding();
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xml, schema);
      assertNotNull(o);
      Root root = (Root) o;
      assertEquals("foo", root.foo);
      assertNotNull(root.barMaxOccurs2);
      assertEquals(2, root.barMaxOccurs2.size());
      assertEquals("bar1", root.barMaxOccurs2.get(0));
      assertEquals("bar2", root.barMaxOccurs2.get(1));
      assertNull(root.bar);
   }

   public void testFoo3Bar() throws Exception
   {
      String xml = findXML(getRootName() + '_' + getName() + ".xml");      
      SchemaBinding schema = getSchemaBinding();
      
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(xml, schema);
      assertNotNull(o);
      Root root = (Root) o;
      assertEquals("foo", root.foo);
      assertNotNull(root.barMaxOccurs2);
      assertEquals(2, root.barMaxOccurs2.size());
      assertEquals("bar1", root.barMaxOccurs2.get(0));
      assertEquals("bar2", root.barMaxOccurs2.get(1));
      assertEquals("bar3", root.bar);
   }

   private SchemaBinding getSchemaBinding()
   {
      String xsd = findXML(getRootName() + ".xsd");
      SchemaBinding schema = XsdBinder.bind(xsd);
      schema.setStrictSchema(true);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      
      ElementBinding e = schema.getElement(new QName("file:///tmp/test.xsd", "message"));
      assertNotNull(e);
      TypeBinding type = e.getType();

      ClassMetaData cmd = new ClassMetaData();
      cmd.setImpl(Root.class.getName());
      type.setClassMetaData(cmd);
      
      TermBinding t = type.getParticle().getTerm();
      assertTrue(t instanceof ChoiceBinding);
      Collection<ParticleBinding> particles = ((ChoiceBinding)t).getParticles();
      assertEquals(2, particles.size());
      Iterator<ParticleBinding> i = particles.iterator();
      ParticleBinding p = i.next();
      assertEquals(1, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      t = p.getTerm();
      assertTrue(t instanceof SequenceBinding);
      
      p = i.next();
      assertEquals(1, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      assertTrue(p.getTerm().isElement());
      assertEquals(new QName("file:///tmp/test.xsd", "bar"), ((ElementBinding)p.getTerm()).getQName());
            
      particles = ((SequenceBinding)t).getParticles();
      assertEquals(2, particles.size());
      i = particles.iterator();
      p = i.next();
      assertEquals(1, p.getMinOccurs());
      assertEquals(1, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      assertTrue(p.getTerm().isElement());
      assertEquals(new QName("file:///tmp/test.xsd", "foo"), ((ElementBinding)p.getTerm()).getQName());

      p = i.next();
      assertEquals(1, p.getMinOccurs());
      assertEquals(2, p.getMaxOccurs());
      assertFalse(p.getMaxOccursUnbounded());
      assertTrue(p.getTerm().isElement());
      e = (ElementBinding) p.getTerm();
      assertEquals(new QName("file:///tmp/test.xsd", "bar"), e.getQName());
      PropertyMetaData pmd = new PropertyMetaData();
      pmd.setName("barMaxOccurs2");
      e.setPropertyMetaData(pmd);
      return schema;
   }
   
   public static class Root
   {
      public String foo;
      public List<String> barMaxOccurs2;
      public String bar;
   }
}
