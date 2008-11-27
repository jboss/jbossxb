/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.xb.builder.object.type.map.test;

import junit.framework.Test;
import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.map.support.RootWrongBoth;
import org.jboss.test.xb.builder.object.type.map.support.RootWrongKey;
import org.jboss.test.xb.builder.object.type.map.support.RootWrongValue;
import org.jboss.xb.binding.JBossXBException;

/**
 * KeyValueTypeUnitTestCase.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class KeyValueTypeUnitTestCase extends AbstractBuilderTest
{
   public KeyValueTypeUnitTestCase(String name)
   {
      super(name);
   }

   public static Test suite()
   {
      return suite(KeyValueTypeUnitTestCase.class);
   }

   public void testWrongKey() throws Throwable
   {
      testFailure(RootWrongKey.class);
   }

   public void testWrongValue() throws Throwable
   {
      testFailure(RootWrongValue.class);      
   }

   public void testWrongBoth() throws Throwable
   {
      testFailure(RootWrongBoth.class);
   }

   @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
   protected void testFailure(Class<?> clazz) throws Throwable
   {
      try
      {
         unmarshalObject(clazz);
         fail("Should not be here.");
      }
      catch (Throwable t)
      {
         if (t instanceof RuntimeException || t instanceof JBossXBException)
            t = t.getCause();

         IllegalArgumentException iae = assertInstanceOf(t, IllegalArgumentException.class);
         boolean result = iae.getMessage().contains("is not an instance of");
         if (result == false)
            t.printStackTrace();
         // TODO should be assertTrue(iae.getMessage().startsWith("is not an instance of"));
      }
   }
}
