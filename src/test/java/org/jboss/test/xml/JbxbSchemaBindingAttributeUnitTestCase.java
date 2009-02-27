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

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xml.jbxb.schemabindingattribute.Ns2Root;
import org.jboss.test.xml.jbxb.schemabindingattribute.Root;
import org.jboss.xb.binding.sunday.unmarshalling.MultiClassSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A JbxbSchemaBindingAttributeUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JbxbSchemaBindingAttributeUnitTestCase
   extends AbstractBuilderTest
{
   public JbxbSchemaBindingAttributeUnitTestCase(String name)
   {
      super(name);
   }

   public void testNested() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(Root.class);
      schema.setSchemaResolver(new MultiClassSchemaResolver());
      String name = findTestXml();
      Object o = unmarshal(name, schema);
      assertNotNull(o);
      assertTrue(o instanceof Root);
      Root root = (Root) o;
      assertNotNull(root.getAnyElement());
      assertEquals(1, root.getAnyElement().length);
      o = root.getAnyElement()[0];
      assertNotNull(o);
      assertTrue(o instanceof Ns2Root);
   }

   public void testTop() throws Exception
   {
      String xml = findTestXml();
      Object o = unmarshal(xml, new MultiClassSchemaResolver());
      assertNotNull(o);
      assertTrue(o instanceof Ns2Root);
   }
}
