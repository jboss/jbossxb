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

import junit.framework.Test;

import org.jboss.xb.binding.NamespaceRegistry;

/**
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class NamespaceRegistryUnitTestCase
   extends AbstractJBossXBTest
{
   public static Test suite()
   {
      return suite(NamespaceRegistryUnitTestCase.class);
   }
   
   private String[] prefix = new String[]{"p1", "p2", "p3"};
   private String[] uri = new String[]{"http://jboss.org/p1", "http://jboss.org/p2", "http://jboss.org/p3"};
   private NamespaceRegistry ctx;

   public NamespaceRegistryUnitTestCase(String localName)
   {
      super(localName);
   }

   public void setUp()
   {
      ctx = new NamespaceRegistry();
      for(int i = 0; i < prefix.length; ++i)
      {
         ctx.addPrefixMapping(prefix[i], uri[i]);
      }
   }

   public void testBasic()
   {
      for(int i = 0; i < prefix.length; ++i)
      {
         assertEquals(uri[i], ctx.getNamespaceURI(prefix[i]));
         assertEquals(prefix[i], ctx.getPrefix(uri[i]));
      }
   }

   public void testPrefixOverride()
   {
      String newUri1 = "http://jboss.org/new_uri_1";
      ctx.addPrefixMapping(prefix[0], newUri1);
      assertEquals(newUri1, ctx.getNamespaceURI(prefix[0]));

      String newUri2 = "http://jboss.org/new_uri_2";
      ctx.addPrefixMapping(prefix[0], newUri2);
      assertEquals(newUri2, ctx.getNamespaceURI(prefix[0]));

      ctx.removePrefixMapping(prefix[0]);
      assertEquals(newUri1, ctx.getNamespaceURI(prefix[0]));

      ctx.removePrefixMapping(prefix[0]);
      for(int i = 0; i < prefix.length; ++i)
      {
         assertEquals(uri[i], ctx.getNamespaceURI(prefix[i]));
         assertEquals(prefix[i], ctx.getPrefix(uri[i]));
      }

      ctx.removePrefixMapping(prefix[0]);
      assertNull(ctx.getPrefix(prefix[0]));
   }

   public void testDuplicateURIs()
   {
      String newPrefix = "p4";
      ctx.addPrefixMapping(newPrefix, uri[0]);
      assertEquals(uri[0], ctx.getNamespaceURI(newPrefix));
      for(int i = 0; i < prefix.length; ++i)
      {
         assertEquals(uri[i], ctx.getNamespaceURI(prefix[i]));
      }
   }
}
