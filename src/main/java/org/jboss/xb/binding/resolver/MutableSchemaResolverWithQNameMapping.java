/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.binding.resolver;

import javax.xml.namespace.QName;

/**
 * A MutableSchemaResolverWithQNameMapping.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public interface MutableSchemaResolverWithQNameMapping extends MutableSchemaResolver
{
   /**
    * Maps element name to an array of classes that should be used as the base for the SchemaBinding.
    * 
    * @param elementName  the name of the root element
    * @param classes  the array of classes to build the SchemaBinding from
    */
   void mapQNameToClasses(QName elementName, Class<?>... classes);

   /**
    * Removes element name to class mapping.
    * 
    * @param elementName  the root element name
    * @return  the array of classes used to build the SchemaBinding or null, if the schema location wasn't mapped.
    */
   Class<?>[] removeQNameToClassMapping(QName elementName);
}
