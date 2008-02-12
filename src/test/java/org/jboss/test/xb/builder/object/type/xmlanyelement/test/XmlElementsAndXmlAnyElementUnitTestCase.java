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
package org.jboss.test.xb.builder.object.type.xmlanyelement.test;

import java.util.List;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.type.xmlanyelement.support.XmlElementsAndXmlAnyElement;


/**
 * A XmlElementsAndXmlAnyElementUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class XmlElementsAndXmlAnyElementUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(XmlElementsAndXmlAnyElementUnitTestCase.class);
   }

   public XmlElementsAndXmlAnyElementUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshalling() throws Exception
   {
      XmlElementsAndXmlAnyElement o = unmarshalObject(XmlElementsAndXmlAnyElement.class);
      List<Object> list = o.getList();
      assertNotNull(list);
      assertEquals(3, list.size());
      assertEquals(11, list.get(0));
      assertEquals("22", list.get(1));
      assertTrue(list.get(2) instanceof XmlElementsAndXmlAnyElement);
   }
}
