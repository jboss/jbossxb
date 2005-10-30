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
package org.jboss.xb.binding;


/**
 * Direct implementations of ObjectModelProvider interface can be thought of as "typed" providers in a sense that
 * arguments of <code>getChildren</code>, <code>getElementValue</code> and <code>getAttributeValue</code> methods
 * are supposed to be of concrete Java types (other than <code>java.lang.Object</code>) from the target class hierarchy.
 * Contrary, in GenericObjectModelFactory these arguments are of type <code>java.lang.Object</code>.
 * The framework won't introspect an implementation of GenericObjectModelProvider to find "typed" implementations of
 * <code>getChildren</code>, <code>getElementValue</code> and <code>getAttributeValue</code>.
 * Instead it will call the generic methods.
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface GenericObjectModelProvider
   extends ObjectModelProvider
{
   Object getChildren(Object o, MarshallingContext ctx, String namespaceURI, String localName);

   Object getElementValue(Object o, MarshallingContext ctx, String namespaceURI, String localName);

   Object getAttributeValue(Object o, MarshallingContext ctx, String namespaceURI, String localName);
}
