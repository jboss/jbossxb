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
package org.jboss.test.xb.builder.schemarebuild.test;

import java.util.Set;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.annotations.JBossXmlSchema;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * A SchemaRebuildUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class SchemaRebuildUnitTestCase extends AbstractBuilderTest
{
   public SchemaRebuildUnitTestCase(String name)
   {
      super(name);
   }

   public void testCache() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(MyClass.class, true);
      assertEquals("ns1", schema.getNamespace("p1"));
      assertEquals("ns2", schema.getNamespace("p2"));
      
      schema.addPrefixMapping("p2", "ns22");
      
      schema = JBossXBBuilder.build(MyClass.class);
      assertEquals("ns1", schema.getNamespace("p1"));
      assertEquals("ns22", schema.getNamespace("p2"));
   }

   public void testRebuild() throws Exception
   {
      SchemaBinding schema = JBossXBBuilder.build(MyClass.class, true);
      assertEquals("ns1", schema.getNamespace("p1"));
      assertEquals("ns2", schema.getNamespace("p2"));
      
      schema.addPrefixMapping("p2", "ns22");
      
      schema = JBossXBBuilder.build(MyClass.class, true);
      assertEquals("ns1", schema.getNamespace("p1"));
      assertEquals("ns2", schema.getNamespace("p2"));
   }

   @XmlRootElement()
   @JBossXmlSchema(xmlns={@XmlNs(namespaceURI = "ns1", prefix = "p1"), @XmlNs(namespaceURI = "ns2", prefix = "p2")})
   public static final class MyClass
   {
      
   }
}
