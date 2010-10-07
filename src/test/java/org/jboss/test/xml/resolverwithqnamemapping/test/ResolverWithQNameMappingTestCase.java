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
package org.jboss.test.xml.resolverwithqnamemapping.test;

import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xml.resolverwithqnamemapping.support.NoNsRoot;
import org.jboss.test.xml.resolverwithqnamemapping.support.Ns1Root;
import org.jboss.xb.binding.resolver.MultiClassSchemaResolver;
import org.jboss.xb.binding.resolver.MutableSchemaResolverWithQNameMapping;

/**
 * A ResolverWithQNameMappingTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ResolverWithQNameMappingTestCase extends AbstractBuilderTest
{
   private MutableSchemaResolverWithQNameMapping resolver;
   
   public ResolverWithQNameMappingTestCase(String name)
   {
      super(name);
   }

   public void setUp() throws Exception
   {
      super.setUp();
      resolver = new MultiClassSchemaResolver();
   }
   
   public void tearDown() throws Exception
   {
      super.tearDown();
      resolver = null;
   }
   
   public void testNs1Root() throws Exception
   {
      resolver.mapQNameToClasses(new QName("ns1", "root"), Ns1Root.class);
      Object o = unmarshal("ResolverWithQNameMapping_testNs1Root.xml", resolver);
      assertNotNull(o);
      assertTrue(o instanceof Ns1Root);
      Ns1Root root = (Ns1Root) o;
      assertEquals("ns1:root", root.getData());
   }
   
   public void testNoNsRoot() throws Exception
   {
      resolver.mapQNameToClasses(new QName("root"), NoNsRoot.class);
      Object o = unmarshal("ResolverWithQNameMapping_testNoNsRoot.xml", resolver);
      assertNotNull(o);
      assertTrue(o instanceof NoNsRoot);
      NoNsRoot root = (NoNsRoot) o;
      assertEquals("nons:root", root.getData());
   }
}
