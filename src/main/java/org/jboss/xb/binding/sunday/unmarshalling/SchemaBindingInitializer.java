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
package org.jboss.xb.binding.sunday.unmarshalling;

/**
 * An implementation of this interface is given an instance of SchemaBinding
 * that is returned from XsdBinder.bind() method during schema binding resolution
 * in SchemaBindingResolver.resolve(). In the init(SchemaBinding schema) method an
 * implementation of this interface can correct/adjust bindings programmatically
 * if pure XSD with annotations binding approach was not sufficient.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface SchemaBindingInitializer
{
   /**
    * Adjust schema binding programatically if needed.
    *
    * @param schema  just resolved schema binding returned from XsdBinder
    * @return  SchemaBinding instance with complete binding metadata
    */
   SchemaBinding init(SchemaBinding schema);
}
