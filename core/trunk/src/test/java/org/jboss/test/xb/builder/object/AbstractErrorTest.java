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
package org.jboss.test.xb.builder.object;

import junit.framework.AssertionFailedError;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * AbstractErrorTest.
 *
 * @param <T> the expected error
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractErrorTest<T extends Throwable> extends AbstractBuilderTest
{
   /** The root class */
   private Class<?> root;
   
   /** The expected throwable */
   private Class<T> expected;
   
   public AbstractErrorTest(String name, Class<?> root, Class<T> expected)
   {
      super(name);
      this.root = root;
      this.expected = expected;
   }

   public void testErrorUnmarshal() throws Exception
   {
      try
      {
         unmarshalObject(root);
         fail("Should not be here");
      }
      catch (AssertionFailedError e)
      {
         throw e;
      }
      catch (Throwable t)
      {
         checkThrowable(expected, t);
      }
   }

   public void testError() throws Exception
   {
      try
      {
         JBossXBBuilder.build(root);
         fail("Should not be here");
      }
      catch (AssertionFailedError e)
      {
         throw e;
      }
      catch (Throwable t)
      {
         checkThrowable(expected, t);
      }
   }
}
