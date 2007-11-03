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
package org.jboss.test.xb.builder.object.type.xmlenum.test;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlenum.support.EnumGlobalType;
import org.jboss.util.Strings;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * EnumGlobalTypeUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class EnumGlobalTypeUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(EnumGlobalTypeUnitTestCase.class);
   }
   
   public EnumGlobalTypeUnitTestCase(String name)
   {
      super(name);
   }

   public void testGlobalEnumUnmarshal() throws Exception
   {
      unmarshalObject(EnumGlobalType.class);
   }

   public void testGlobalEnumTypeBinding() throws Exception
   {
      SchemaBinding schemaBinding = JBossXBBuilder.build(EnumGlobalType.class);
      assertNotNull(schemaBinding);
      
      // Check the global type
      QName qName = new QName(XMLConstants.NULL_NS_URI, "enum-global-type");
      TypeBinding type = schemaBinding.getType(qName);
      assertNotNull(type);
      assertEquals(qName, type.getQName());
      
      ElementBinding elementBinding = schemaBinding.getElement(qName);
      assertNotNull(elementBinding);
      TypeBinding typeBinding = elementBinding.getType();
      assertNotNull(typeBinding);
      //System.out.println(Strings.defaultToString(type));
      //System.out.println(Strings.defaultToString(typeBinding));
      assertTrue(type == typeBinding);
   }
}
