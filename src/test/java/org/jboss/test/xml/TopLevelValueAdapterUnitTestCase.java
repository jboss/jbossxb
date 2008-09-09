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

import javax.xml.namespace.QName;

import junit.framework.TestSuite;

import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;

/**
 * AnyComplexTypeUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class TopLevelValueAdapterUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/topLevelValueAdapter";
   
   public static final TestSuite suite()
   {
      return new TestSuite(TopLevelValueAdapterUnitTestCase.class);
   }
   
   public TopLevelValueAdapterUnitTestCase(String name)
   {
      super(name);
   }

   public void testTopLevelValueAdapter() throws Exception
   {
      SchemaBinding schema = bind("TopLevelValueAdapter.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ElementBinding element = schema.getElement(new QName(NS, "top"));
      TypeBinding type = element.getType();
      type.setValueAdapter(new ValueAdapter()
      {
         public Object cast(Object o, Class<?> c)
         {
            String string = (String) o;
            return string + "...";
         }
         
      });

      String string = (String) unmarshal("TopLevelValueAdapter.xml", schema, String.class);
      assertEquals("string...", string);
   }
}
