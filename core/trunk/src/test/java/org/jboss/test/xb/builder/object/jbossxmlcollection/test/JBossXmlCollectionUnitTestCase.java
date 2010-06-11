/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.test.xb.builder.object.jbossxmlcollection.test;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.jbossxmlcollection.support.Root;


/**
 * A JBossXmlCollectionUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossXmlCollectionUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(JBossXmlCollectionUnitTestCase.class);
   }
   
   public JBossXmlCollectionUnitTestCase(String name)
   {
      super(name);
   }

   public void testDefault() throws Exception
   {
      Root root = unmarshalObject(Root.class);
      assertNotNull(root);
      assertNull(root.getUnparameterizedList());
      assertNull(root.getJbossXmlList());
      List<?> list = root.getDefaultList();
      assertNotNull(list);
      assertEquals(3, list.size());
      assertEquals("default1", list.get(0));
      assertEquals("default22", list.get(1));
      assertEquals("default333", list.get(2));
   }

   public void testUnparameterized() throws Exception
   {
      Root root = unmarshalObject(Root.class);
      assertNotNull(root);
      assertNull(root.getDefaultList());
      assertNull(root.getJbossXmlList());
      List<?> list = root.getUnparameterizedList();
      assertNotNull(list);
      assertEquals(3, list.size());
      assertEquals(1, list.get(0));
      assertEquals(22, list.get(1));
      assertEquals(333, list.get(2));
   }

   public void testJbossXmlList() throws Exception
   {
      Root root = unmarshalObject(Root.class);
      assertNotNull(root);
      assertNull(root.getDefaultList());
      assertNull(root.getUnparameterizedList());
      List<?> list = root.getJbossXmlList();
      assertNotNull(list);
      assertEquals(LinkedList.class, list.getClass());
      assertEquals(3, list.size());
      assertEquals("jbossxmllist1", list.get(0));
      assertEquals("jbossxmllist22", list.get(1));
      assertEquals("jbossxmllist333", list.get(2));
   }

   public void testUnparameterizedJbossXmlList() throws Exception
   {
      Root root = unmarshalObject(Root.class);
      assertNotNull(root);
      assertNull(root.getDefaultList());
      assertNull(root.getUnparameterizedList());
      assertNull(root.getJbossXmlList());
      List<?> list = root.getUnparameterizedJbossXmlList();
      assertNotNull(list);
      assertEquals(LinkedList.class, list.getClass());
      assertEquals(3, list.size());
      assertEquals(11, list.get(0));
      assertEquals(22, list.get(1));
      assertEquals(33, list.get(2));
   }
}
