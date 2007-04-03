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

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.beans.IntrospectionException;

import org.jboss.xb.binding.introspection.ClassInfo;
import org.jboss.xb.binding.introspection.ClassInfos;
import org.jboss.xb.binding.introspection.FieldInfo;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 56570 $</tt>
 */
public class IntrospectionUnitTestCase
   extends TestCase
{
   public IntrospectionUnitTestCase(String localName)
   {
      super(localName);
   }

   public void testClassInfoNANonRequired() throws Exception
   {
      assertNull(ClassInfos.getClassInfo("na.package.A", false));
   }

   public void testClassInfoNARequired() throws Exception
   {
      try
      {
         ClassInfos.getClassInfo("na.package.B", true);
         fail("Exception must be thrown for not available ClassInfo.");
      }
      catch(RuntimeException e)
      {
      }
   }

   public void testFieldInfoNANonRequired() throws Exception
   {
      ClassInfo classInfo = ClassInfos.getClassInfo(A.class);
      assertNull(classInfo.getFieldInfo("field99", false));
   }

   public void testFieldInfoNARequired() throws Exception
   {
      ClassInfo classInfo = ClassInfos.getClassInfo(A.class);
      try
      {
         classInfo.getFieldInfo("field99", true);
         fail("Exception must be thrown for not availble FieldInfo.");
      }
      catch(RuntimeException e)
      {
      }
   }

   public void testGetterSetterAccess() throws Exception
   {
      FieldInfo fieldInfo = ClassInfos.getClassInfo(A.class).getFieldInfo("field1", true);
      A a = new A();
      assertNull(fieldInfo.getValue(a));
      a.setField1("val1");
      assertEquals("val1", fieldInfo.getValue(a));
      fieldInfo.setValue(a, "val2");
      assertEquals("val2", fieldInfo.getValue(a));
      assertEquals("val2", a.getField1());
   }

   public void testFieldAccess() throws Exception
   {
      FieldInfo fieldInfo = ClassInfos.getClassInfo(A.class).getFieldInfo("field2", true);
      A a = new A();
      assertNull(fieldInfo.getValue(a));
      a.field2 = "val1";
      assertEquals("val1", fieldInfo.getValue(a));
      fieldInfo.setValue(a, "val2");
      assertEquals("val2", fieldInfo.getValue(a));
      assertEquals("val2", a.field2);
   }
   public void testReadWriteMethodAccess() throws Exception
   {
      FieldInfo fieldInfo = ClassInfos.getClassInfo(A.class).getFieldInfo("field3", true);
      A a = new A();
      assertNull(fieldInfo.getValue(a));
      a.writeField3("val1");
      assertEquals("val1", fieldInfo.getValue(a));
      fieldInfo.setValue(a, "val2");
      assertEquals("val2", fieldInfo.getValue(a));
      assertEquals("val2", a.readField3());
   }

   public static class A
   {
      private String field1;
      public String field2;
      private String field3;

      public String getField1()
      {
         return field1;
      }

      public void setField1(String field1)
      {
         this.field1 = field1;
      }

      public String readField3()
      {
         return field3;
      }

      public void writeField3(String field3)
      {
         this.field3 = field3;
      }
   }

   public static class ABeanInfo
      extends SimpleBeanInfo
   {
      public PropertyDescriptor[] getPropertyDescriptors()
      {
         try
         {
            return new PropertyDescriptor[]
            {
               new PropertyDescriptor("field3", A.class, "readField3", "writeField3")
            };
         }
         catch(IntrospectionException e)
         {
            throw new IllegalStateException("Failed to create property descriptors.");
         }
      }
   }
}
