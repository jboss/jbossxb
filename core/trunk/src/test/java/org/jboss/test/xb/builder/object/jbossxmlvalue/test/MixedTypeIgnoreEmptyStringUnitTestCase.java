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
package org.jboss.test.xb.builder.object.jbossxmlvalue.test;

import java.util.List;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.jbossxmlvalue.support.MixedType;
import org.jboss.test.xb.builder.object.jbossxmlvalue.support.MixedTypeIgnoreEmptyStringFalse;
import org.jboss.test.xb.builder.object.jbossxmlvalue.support.MixedTypeIgnoreEmptyStringTrue;

/**
 * A MixedTypeIgnoreEmptyStringUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class MixedTypeIgnoreEmptyStringUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(MixedTypeIgnoreEmptyStringUnitTestCase.class);
   }
   
   public MixedTypeIgnoreEmptyStringUnitTestCase(String name)
   {
      super(name);
   }

   public void testIgnoreEmptyStringTrue() throws Exception
   {
      MixedTypeIgnoreEmptyStringTrue o = unmarshalObject(MixedTypeIgnoreEmptyStringTrue.class);
      assertEquals("empty string test", o.getChild());
      assertNull(o.getValue());
      List<MixedTypeIgnoreEmptyStringTrue> children = o.getChildren();
      assertNotNull(children);
      assertEquals(2, children.size());
      MixedType c = children.get(0);
      assertNotNull(c);
      assertNull(c.getValue());
      c = children.get(1);
      assertNotNull(c);
      assertEquals("txt", c.getValue());
   }

   public void testIgnoreEmptyStringFalse() throws Exception
   {
      MixedTypeIgnoreEmptyStringFalse o = unmarshalObject(MixedTypeIgnoreEmptyStringFalse.class);
      assertEquals("empty string test", o.getChild());
      assertEquals("", o.getValue());
      List<MixedTypeIgnoreEmptyStringFalse> children = o.getChildren();
      assertNotNull(children);
      assertEquals(2, children.size());
      MixedType c = children.get(0);
      assertNotNull(c);
      assertEquals("", c.getValue());
      c = children.get(1);
      assertNotNull(c);
      assertEquals("txt", c.getValue());
   }
}
