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

import org.jboss.test.xml.CollectionOverridePropertyUnitTestCase.Parent;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;

/**
 * SimpleContentUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class SimpleContentUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/simpleContent";
   
   public static final TestSuite suite()
   {
      return new TestSuite(SimpleContentUnitTestCase.class);
   }
   
   public SimpleContentUnitTestCase(String name)
   {
      super(name);
   }

   public void testCollectionOverrideProperty() throws Exception
   {
      enableTrace("org.jboss.xb");

      SchemaBinding schema = bind("SimpleContent.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);

      //TypeBinding type = schema.getType(new QName(NS, "myString"));
      //type.setStartElementCreatesObject(false);
      
      element.getType().pushInterceptor(new QName(NS, "child"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            getLog().debug("Add " + parent + " " + child);
            Top p = (Top) parent;
            if (p.string == null)
               p.string = new LinkedList();
            p.string.add(child);
         }
      }); 

      Top top = (Top) unmarshal("SimpleContent.xml", schema, Top.class);
      assertNotNull(top.getString());
      assertEquals("Hello", top.getString().get(0));
   }
   
   public static class Top
   {
      public List string;

      public List getString()
      {
         return string;
      }

      public void setString(List string)
      {
         this.string = string;
      }
   }
}
