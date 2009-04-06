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
package org.jboss.test.xb.builder.object.anyattribute.test;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.anyattribute.support.AnyAttributes;


/**
 * A AnyAttributeUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class AnyAttributeUnitTestCase extends AbstractBuilderTest
{
   public AnyAttributeUnitTestCase(String name)
   {
      super(name);
   }
   
   public void testUnmarshal() throws Exception
   {
      AnyAttributes result = unmarshalObject(AnyAttributes.class);
      assertEquals("static", result.getStaticAttribute());
      Map<QName, Object> anyAttributes = result.getAnyAttributes();
      assertNotNull(anyAttributes);
      assertEquals(2, anyAttributes.size());
      
      Object value = anyAttributes.get(new QName("attr1"));
      assertEquals("value1", value);
      value = anyAttributes.get(new QName("attr2"));
      assertEquals("value2", value);
   }
}
