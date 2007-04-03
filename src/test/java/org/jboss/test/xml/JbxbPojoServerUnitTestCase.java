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

import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37728 $</tt>
 */
public class JbxbPojoServerUnitTestCase
   extends PojoServerTestBase
{
   public JbxbPojoServerUnitTestCase(String localName)
   {
      super(localName);
   }

   protected SchemaBinding getSchemaBinding()
   {
      SchemaBinding cursor = readXsd();
/** TODO
      TypeBinding namedValueType = cursor.getType(namedValueTypeQName);

      //
      // add handlers
      //

      namedValueType.pushInterceptor(propsQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            Map children = (Map)child;
            for(Iterator i = children.entrySet().iterator(); i.hasNext();)
            {
               Map.Entry entry = (Map.Entry)i.next();
               prop.setValue((String)entry.getKey(), entry.getValue());
            }
         }
      }
      );
*/
      return cursor;
   }

   protected String getXsd()
   {
      return "xml/jbxb-bean-deployer_1_0.xsd";
   }
}
