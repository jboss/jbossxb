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
package org.jboss.test.xml;

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;

/**
 * DuplicateInterceptorUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class DuplicateInterceptorUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/duplicateInterceptor";
   
   public static final TestSuite suite()
   {
      return new TestSuite(DuplicateInterceptorUnitTestCase.class);
   }
   
   public DuplicateInterceptorUnitTestCase(String name)
   {
      super(name);
   }

   public void testDuplicateInterceptor() throws Exception
   {
      SchemaBinding schema = bind("DuplicateInterceptor.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Parent1.class.getName());
      TypeBinding parent1Type = schema.getType(new QName(NS, "parent1Type"));
      assertNotNull(parent1Type);
      parent1Type.setClassMetaData(classMetaData);

      parent1Type.pushInterceptor(new QName(NS, "child"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            Parent1 parent1 = (Parent1) parent;
            Child c = (Child) child;
            c.string = c.string + "Parent1";
            parent1.child = c;
         }
      });
      
      classMetaData = new ClassMetaData();
      classMetaData.setImpl(Parent2.class.getName());
      TypeBinding parent2Type = schema.getType(new QName(NS, "parent2Type"));
      assertNotNull(parent2Type);
      parent2Type.setClassMetaData(classMetaData);

      parent2Type.pushInterceptor(new QName(NS, "child"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            fail("Should not invoke interceptor added to parent2Type when processing parent1!"); 
         }
      });

      classMetaData = new ClassMetaData();
      classMetaData.setImpl(Child.class.getName());
      TypeBinding childType = schema.getType(new QName(NS, "childType"));
      assertNotNull(childType);
      childType.setClassMetaData(classMetaData);

      Parent1 parent1 = (Parent1) unmarshal("DuplicateInterceptor.xml", schema, Parent1.class);
      assertNotNull(parent1.child);
      assertEquals("HelloParent1", parent1.child.string);
   }
   
   public static class Parent1
   {
      public Child child;
   }
   
   public static class Parent2
   {
      public Child child;
   }
   
   public static class Child
   {
      public String string;
   }
}
