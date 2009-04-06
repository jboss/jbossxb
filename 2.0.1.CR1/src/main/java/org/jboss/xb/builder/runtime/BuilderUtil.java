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
package org.jboss.xb.builder.runtime;

import org.jboss.util.Strings;
import org.jboss.xb.spi.AbstractBeanAdapter;
import org.w3c.dom.Element;

/**
 * BuilderUtil.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class BuilderUtil
{
   /**
    * Work out a safe debug string for the object
    * 
    * @param object the object
    * @return the string
    */
   public static String toDebugString(Object object)
   {
      if (object == null)
         return "null";

      if (object instanceof String ||
          object instanceof Boolean ||
          object instanceof Byte ||
          object instanceof Character ||
          object instanceof Double ||
          object instanceof Integer ||
          object instanceof Float ||
          object instanceof Short)
         return object.toString();
      
      if (object instanceof AbstractBeanAdapter)
         return ((AbstractBeanAdapter) object).getBeanInfo().getName() + "@" + System.identityHashCode(object);
      
      if (object instanceof Element)
      {
         Element element = (Element) object;
         return "Element@" + System.identityHashCode(element) + "{" + element.getLocalName() + "}";
      }
      
      return Strings.defaultToString(object);
   }
}
