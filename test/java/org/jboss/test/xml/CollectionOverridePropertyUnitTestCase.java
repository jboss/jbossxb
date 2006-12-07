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
package org.jboss.test.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;


/**
 * CollectionOverridePropertyUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class CollectionOverridePropertyUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/collectionOverrideProperty";
   
   public static final TestSuite suite()
   {
      return new TestSuite(CollectionOverridePropertyUnitTestCase.class);
   }
   
   public CollectionOverridePropertyUnitTestCase(String name)
   {
      super(name);
   }

   public void testCollectionOverrideProperty() throws Exception
   {
      SchemaBinding schema = bind("CollectionOverrideProperty.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Parent.class.getName());
      TypeBinding type = schema.getType(new QName(NS, "parent-type"));
      assertNotNull(type);
      type.setClassMetaData(classMetaData);
      
      type.pushInterceptor(new QName(NS, "child"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            getLog().debug("Add " + parent + " " + child);
            Parent p = (Parent) parent;
            if (p.list == null)
               p.list = new LinkedList();
            p.list.add(child);
         }
      }); 

      classMetaData = new ClassMetaData();
      classMetaData.setImpl(Child.class.getName());
      type = schema.getType(new QName(NS, "child-type"));
      assertNotNull(type);
      type.setClassMetaData(classMetaData);

      Parent parent = (Parent) unmarshal("CollectionOverrideProperty.xml", schema, Parent.class);
      List list = parent.list;
      assertNotNull(list);
      assertEquals("one", ((Child) list.get(0)).getValue());
      assertEquals("two", ((Child) list.get(1)).getValue());
   }

   public void testWithPropertyMetaData() throws Exception
   {
      SchemaBinding schema = bind("CollectionOverrideProperty.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Parent.class.getName());
      TypeBinding type = schema.getType(new QName(NS, "parent-type"));
      assertNotNull(type);
      type.setClassMetaData(classMetaData);

      PropertyMetaData prop = new PropertyMetaData();
      prop.setName("list");
      type.getElement(new QName(NS, "child")).setPropertyMetaData(prop);
      
      classMetaData = new ClassMetaData();
      classMetaData.setImpl(Child.class.getName());
      type = schema.getType(new QName(NS, "child-type"));
      assertNotNull(type);
      type.setClassMetaData(classMetaData);

      Parent parent = (Parent) unmarshal("CollectionOverrideProperty.xml", schema, Parent.class);
      List list = parent.list;
      assertNotNull(list);
      assertEquals("one", ((Child) list.get(0)).getValue());
      assertEquals("two", ((Child) list.get(1)).getValue());
   }

   public static class Parent
   {
      private List list;
      
      public List getList()
      {
         return list;
      }
      
      public void setList(List list)
      {
         this.list = list;
      }
   }
   
   public static class Child
   {
      private String value;
      
      public String getValue()
      {
         return value;
      }
      
      public void setValue(String value)
      {
         this.value = value;
      }
   }
}
