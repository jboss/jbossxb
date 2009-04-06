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
package org.jboss.test.xb.builder.multiplerootclasses.test;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.multiplerootclasses.support.AnotherRootAnotherNs;
import org.jboss.test.xb.builder.multiplerootclasses.support.AnotherRootDefaultNs;
import org.jboss.test.xb.builder.multiplerootclasses.support.AnotherRootMainNs;
import org.jboss.test.xb.builder.multiplerootclasses.support.MainRootDefaultNs;
import org.jboss.test.xb.builder.multiplerootclasses.support.MainRootMainNs;
import org.jboss.test.xb.builder.multiplerootclasses.support.YetAnotherRootMainNs;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A MultipleRootClassesUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class MultipleRootClassesUnitTestCase extends AbstractBuilderTest
{
   public MultipleRootClassesUnitTestCase(String name)
   {
      super(name);
   }

   public void testMainNsAnotherNs() throws Exception
   {
      SchemaBinding mainSchema = JBossXBBuilder.build(MainRootMainNs.class, true);
      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      Iterator<ElementBinding> elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      ElementBinding e = elements.next();
      assertEquals(new QName("main.namespace", "main-root"), e.getQName());
      assertFalse(elements.hasNext());

      SchemaBinding anotherSchema = JBossXBBuilder.build(AnotherRootAnotherNs.class);
      assertTrue(anotherSchema.isStrictSchema());
      assertEquals(1, anotherSchema.getNamespaces().size());
      elements = anotherSchema.getElements();
      assertTrue(elements.hasNext());
      e = elements.next();
      assertEquals(new QName("another.namespace", "another-root"), e.getQName());
      assertFalse(elements.hasNext());

      try
      {
         JBossXBBuilder.build(mainSchema, AnotherRootAnotherNs.class);
         fail("Different namespaces can not be merged.");
      }
      catch (IllegalStateException ise)
      {
         assertEquals("SchemaBinding namespace 'main.namespace' does not match class namespace 'another.namespace'",
               ise.getMessage());
      }
   }

   public void testDefaultNsAnotherNs() throws Exception
   {
      SchemaBinding mainSchema = JBossXBBuilder.build(MainRootDefaultNs.class, true);
      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      Iterator<ElementBinding> elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      ElementBinding e = elements.next();
      assertEquals(new QName("main-root"), e.getQName());
      assertFalse(elements.hasNext());

      SchemaBinding anotherSchema = JBossXBBuilder.build(AnotherRootAnotherNs.class);
      assertTrue(anotherSchema.isStrictSchema());
      assertEquals(1, anotherSchema.getNamespaces().size());
      elements = anotherSchema.getElements();
      assertTrue(elements.hasNext());
      e = elements.next();
      assertEquals(new QName("another.namespace", "another-root"), e.getQName());
      assertFalse(elements.hasNext());

      try
      {
         JBossXBBuilder.build(mainSchema, AnotherRootAnotherNs.class);
         fail("Different namespaces can not be merged.");
      }
      catch (IllegalStateException ise)
      {
         assertEquals("SchemaBinding namespace '' does not match class namespace 'another.namespace'",
               ise.getMessage());
      }
   }

   public void testMainNsDefaultNs() throws Exception
   {
      SchemaBinding mainSchema = JBossXBBuilder.build(MainRootMainNs.class, true);
      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      Iterator<ElementBinding> elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      ElementBinding e = elements.next();
      assertEquals(new QName("main.namespace", "main-root"), e.getQName());
      assertFalse(elements.hasNext());

      SchemaBinding anotherSchema = JBossXBBuilder.build(AnotherRootDefaultNs.class);
      assertTrue(anotherSchema.isStrictSchema());
      assertEquals(1, anotherSchema.getNamespaces().size());
      elements = anotherSchema.getElements();
      assertTrue(elements.hasNext());
      e = elements.next();
      assertEquals(new QName("another-root"), e.getQName());
      assertFalse(elements.hasNext());

      try
      {
         JBossXBBuilder.build(mainSchema, AnotherRootDefaultNs.class);
         fail("Different namespaces can not be merged.");
      }
      catch (IllegalStateException ise)
      {
         assertEquals("SchemaBinding namespace 'main.namespace' does not match class namespace ''",
               ise.getMessage());
      }
   }
   
   public void testDefaultNsDefaultNs() throws Exception
   {
      SchemaBinding mainSchema = JBossXBBuilder.build(MainRootDefaultNs.class, true);
      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      Iterator<ElementBinding> elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      ElementBinding e = elements.next();
      assertEquals(new QName("main-root"), e.getQName());
      assertFalse(elements.hasNext());

      SchemaBinding anotherSchema = JBossXBBuilder.build(AnotherRootDefaultNs.class);
      assertTrue(anotherSchema.isStrictSchema());
      assertEquals(1, anotherSchema.getNamespaces().size());
      elements = anotherSchema.getElements();
      assertTrue(elements.hasNext());
      e = elements.next();
      assertEquals(new QName("another-root"), e.getQName());
      assertFalse(elements.hasNext());

      JBossXBBuilder.build(mainSchema, AnotherRootDefaultNs.class);

      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      elements.next();
      assertTrue(elements.hasNext());
      elements.next();
      assertFalse(elements.hasNext());
      e = mainSchema.getElement(new QName("main-root"));
      assertNotNull(e);
      e = mainSchema.getElement(new QName("another-root"));
      assertNotNull("another-root is added to the main schema", e);
   }

   public void testMainNsMainNs() throws Exception
   {
      SchemaBinding mainSchema = JBossXBBuilder.build(MainRootMainNs.class, true);
      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      Iterator<ElementBinding> elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      ElementBinding e = elements.next();
      assertEquals(new QName("main.namespace", "main-root"), e.getQName());
      assertFalse(elements.hasNext());

      SchemaBinding anotherSchema = JBossXBBuilder.build(AnotherRootMainNs.class);
      assertTrue(anotherSchema.isStrictSchema());
      assertEquals(1, anotherSchema.getNamespaces().size());
      elements = anotherSchema.getElements();
      assertTrue(elements.hasNext());
      e = elements.next();
      assertEquals(new QName("main.namespace", "another-root"), e.getQName());
      assertFalse(elements.hasNext());

      JBossXBBuilder.build(mainSchema, AnotherRootMainNs.class);

      assertFalse(mainSchema.isStrictSchema());
      assertEquals(1, mainSchema.getNamespaces().size());
      elements = mainSchema.getElements();
      assertTrue(elements.hasNext());
      elements.next();
      assertTrue(elements.hasNext());
      elements.next();
      assertFalse(elements.hasNext());
      e = mainSchema.getElement(new QName("main.namespace", "main-root"));
      assertNotNull(e);
      e = mainSchema.getElement(new QName("main.namespace", "another-root"));
      assertNotNull("another-root is added to the main schema", e);
   }
   
   public void testBuildArrayMainNsDefaultNs() throws Exception
   {
      try
      {
         JBossXBBuilder.build(MainRootMainNs.class, YetAnotherRootMainNs.class, AnotherRootDefaultNs.class);
         fail("Different namespaces can not be merged.");
      }
      catch (IllegalStateException ise)
      {
         assertEquals("SchemaBinding namespace 'main.namespace' does not match class namespace ''",
               ise.getMessage());
      }      
   }

   public void testBuildArrayMainNs() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(MainRootMainNs.class, YetAnotherRootMainNs.class, AnotherRootMainNs.class);
      
      assertFalse(schema.isStrictSchema());
      assertEquals(1, schema.getNamespaces().size());
      Iterator<ElementBinding> elements = schema.getElements();
      assertTrue(elements.hasNext());
      elements.next();
      assertTrue(elements.hasNext());
      elements.next();
      assertTrue(elements.hasNext());
      elements.next();
      assertFalse(elements.hasNext());
      ElementBinding e = schema.getElement(new QName("main.namespace", "main-root"));
      assertNotNull(e);
      e = schema.getElement(new QName("main.namespace", "another-root"));
      assertNotNull("another-root is added to the main schema", e);
      e = schema.getElement(new QName("main.namespace", "yet-another-root"));
      assertNotNull("yet-another-root is added to the main schema", e);
   }
}
