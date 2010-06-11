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
package org.jboss.test.xb.builder;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.jboss.xb.builder.JBossXBBuilder;

/**
* @author <a href="alex@jboss.com">Alexey Loubyansky</a>
* @version $Revision: 1.1 $
*/
public abstract class AbstractSystemPropertyInitializationTest extends AbstractBuilderTest
{

   private String defaultSystemValue;
   private ClassLoader cl;

   public AbstractSystemPropertyInitializationTest(String name)
   {
      super(name);
   }

   protected abstract String getPropertyName();
   protected abstract String getPropertyGetter();
   
   protected void setUp() throws Exception
   {
      super.setUp();
      defaultSystemValue = System.getProperty(getPropertyName());
      
      ClassLoader builderCl = JBossXBBuilder.class.getClassLoader();
      if(builderCl == null)
         builderCl = ClassLoader.getSystemClassLoader();      
      assertTrue(builderCl instanceof URLClassLoader);
      cl = new URLClassLoader(((URLClassLoader)builderCl).getURLs(), null);
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
      if(defaultSystemValue != null)
         System.setProperty(getPropertyName(), defaultSystemValue);
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
      System.setProperty(getPropertyName(), Boolean.toString(value));
      Class<?> clazz = cl.loadClass(JBossXBBuilder.class.getName());
      Method m = clazz.getMethod(getPropertyGetter());
      assertEquals(value, m.invoke(null));
   }

}