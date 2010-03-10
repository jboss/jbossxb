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

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.beanaccessmode.support.SomeType;
import org.jboss.test.xb.builder.object.beanaccessmode.support.SchemaLevelPublicMemberAccessMode;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;


/**
 * A GlobalBeanAccessModeUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class SchemaLevelPublicMemberAccessModeUnitTestCase extends AbstractBuilderTest
{
   public SchemaLevelPublicMemberAccessModeUnitTestCase(String name)
   {
      super(name);
   }

   public void testFieldsBinding() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(SchemaLevelPublicMemberAccessMode.class, true);
      ElementBinding root = schema.getElement(new QName("root"));
      assertNotNull(root);
      TypeBinding type = root.getType();
      assertEquals(2, type.getAttributes().size());
      AttributeBinding attr = type.getAttribute(new QName("property"));
      assertNotNull(attr);
      attr = type.getAttribute(new QName("public-field"));
      assertNotNull(attr);
   }
   
   public void testUnmarshalling() throws Exception
   {
      SomeType root = unmarshalObject(SchemaLevelPublicMemberAccessMode.class);
      assertEquals("property", root.getProperty());
      assertEquals("fields", root.publicField);
      assertNull(root.privateField());
   }
}
