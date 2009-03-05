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
package org.jboss.test.xb.builder.useunorderedsequence.test;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.jboss.xb.builder.JBossXBBuilder;

import junit.framework.TestCase;

/**
 * A InitializationUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class InitializationUnitTestCase extends TestCase
{
   private String unorderedSequenceDefaultSystemValue;
   private ClassLoader cl;
   
   protected void setUp() throws Exception
   {
      super.setUp();
      unorderedSequenceDefaultSystemValue = System.getProperty(JBossXBBuilder.USE_UNORDERED_SEQUENCE_PROPERTY);
      
      ClassLoader builderCl = JBossXBBuilder.class.getClassLoader();
      if(builderCl == null)
         builderCl = ClassLoader.getSystemClassLoader();      
      assertTrue(builderCl instanceof URLClassLoader);
      cl = new URLClassLoader(((URLClassLoader)builderCl).getURLs(), null);
   }
   
   protected void tearDown() throws Exception
   {
      super.tearDown();
      if(unorderedSequenceDefaultSystemValue != null)
         System.setProperty(JBossXBBuilder.USE_UNORDERED_SEQUENCE_PROPERTY, unorderedSequenceDefaultSystemValue);
   }
   
   public void testFalse() throws Exception
   {
      initAndAssert(false);
   }

   public void testTrue() throws Exception
   {
      initAndAssert(true);
   }

   private void initAndAssert(Boolean value) throws Exception
   {
      System.setProperty(JBossXBBuilder.USE_UNORDERED_SEQUENCE_PROPERTY, Boolean.toString(value));
      Class<?> clazz = cl.loadClass(JBossXBBuilder.class.getName());
      Method m = clazz.getMethod("isUseUnorderedSequence");
      assertEquals(value, m.invoke(null));
   }
}
