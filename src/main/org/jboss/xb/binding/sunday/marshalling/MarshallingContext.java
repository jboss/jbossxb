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
package org.jboss.xb.binding.sunday.marshalling;

import javax.xml.namespace.NamespaceContext;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface MarshallingContext
   extends org.jboss.xb.binding.MarshallingContext
{
   /**
    * @return  schema binding
    */
   SchemaBinding getSchemaBinding();

   /**
    * @return  current attribute binding
    */
   AttributeBinding getAttributeBinding();

   /**
    * @param ns  the namespace to return the prefix for
    * @return  the prefix for the namespace (can be null if the namespace is not mapped to a prefix
    *    and the second parameter is false)
    */
   String getPrefix(String ns);

   /**
    * @param prefix  prefix for the namespace being declared
    * @param ns  the namespace to declare for the current component
    */
   void declareNamespace(String prefix, String ns);

   /**
    * @return  current object on the stack
    */
   Object peek();
}
