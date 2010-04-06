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
package org.jboss.test.xml.pojoserver.metadata;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultWildcardHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingInitializer;

/**
 * ContainerInitializer.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 40741 $
 */
public class PropertyInitializer implements SchemaBindingInitializer
{
   public static final String NS = "dummy://www.jboss.org/property";

   private static final QName propertyQName = new QName(NS, "property");
   private static final QName valueQName = new QName(NS, "value");

   public SchemaBinding init(SchemaBinding schema)
   {
      ElementBinding element = schema.getElement(propertyQName);
      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(AbstractPropertyMetaData.class.getName());
      element.setClassMetaData(classMetaData);
      element.getType().getWildcard().setHandler(new DefaultWildcardHandler()
      {
         public void setParent(Object parent, Object o, QName elementName, ElementBinding element, ElementBinding parentElement)
         {
            AbstractPropertyMetaData property = (AbstractPropertyMetaData) parent;
            if (o instanceof ValueMetaData == false)
               o = new AbstractValueMetaData(o);
            property.setValue((ValueMetaData) o);
         }
      });

      element = schema.getElement(valueQName);
      classMetaData = new ClassMetaData();
      classMetaData.setImpl(StringValueMetaData.class.getName());
      element.setClassMetaData(classMetaData);

      return schema;
   }
}
