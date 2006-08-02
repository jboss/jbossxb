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
 * The interface all object model providers must implement. Object model providers are used on marshalling
 * providing data for XML content based on the object model and XML schema or DTD.
 * <p/>
 * Each object model provider must implement one method <code>getRoot</code> defined in ObjectModelProvider interface
 * and a set of getChildren, getElementValue and getAttributeValue methods descovered by the framework at runtime
 * with introspection.
 * <p/>So, the following methods should be implemented:
 * <ul>
 * <li><code>getRoot</code> method
 * <pre>
 *    java.lang.Object getRoot(java.lang.Object o, java.lang.String namespaceURI, java.lang.String localName)
 * </pre>
 * This method is called on the object model provider by the framework when a root XML element is marshalled.
 * The method returns an object that represents the root of the XML content corresponding to the namespace URI and
 * local name.
 * </li>
 * <li>a set of <code>getChildren</code> methods
 * This method is called on the object model provider by the framework when marshalling of a new XML element started.
 * Each <code>getChildren</code> method must have three arguments:
 * <ol>
 * <li>parent object of a concrete Java type (not java.lang.Object) that is "asked" for its children</li>
 * <li>namespace URI of the child XML element as java.lang.String</li>
 * <li>local name of the child element as java.lang.String</li>
 * </ol>
 * A <code>getChildren</code> method returns children that represent the namespace URI and local name in XML content.
 * The method can return null if there are no children in this object graph corresponding to the namespace and local name.
 * The method can return a single object if there is only one child object corresponding to the namespace and local name.
 * If there are many children that match the namespace URI and local name, the method can return them as an array,
 * java.util.List, java.util.Collection or java.util.Iterator.
 * </li>
 * <li>a set of <code>getElementValue</code> methods
 * This method is called on the object model provider by the framework for objects that represent XML elements with
 * simple content, i.e. elements that don't contain nested XML elements.
 * The method must have three arguments:
 * <ol>
 * <li>an object of a concrete Java type (not java.lang.Object) that is "asked" to provide a value of the XML element
 * being marshalled</li>
 * <li>namespace URI as java.lang.String of the XML element being marshalled</li>
 * <li>local name as java.lang.String of the XML element being marshalled</li>
 * </ol>
 * The method returns either null if the object model does not have any value corresponding to the namespace URI
 * and local name (in this case the XML content will not contain this XML element) or the actual value of the XML element.
 * </li>
 * <li>a set of <code>getAttributeValue</code> methods
 * This method is called on the object model provider by the framework for objects that represent XML elements with
 * attributes.
 * The method must have three arguments:
 * <ol>
 * <li>an object of a concrete Java type (not java.lang.Object) that is "asked" to provide a value for the XML attribute
 * being marshalled</li>
 * <li>namespace URI of the XML attribute being marshalled</li>
 * <li>local name of the XML attribute being marshalled</li>
 * </ol>
 * The method returns either null if the object graph does not have any value corresponding to the namespace URI
 * and local name (in this case the XML content will not contain this attribute) or the actual value of the XML attribute.
 * </li>
 * </ol>
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ObjectModelProvider
{
   /**
    * Called by the framework when a root XML element is marshalled.
    *
    * @param o            the root of the object graph
    * @param ctx
    * @param namespaceURI namespace URI of the root XML element being marshalled
    * @param localName    local name of the root XML element being marshalled
    * @return an object that represents the root XML element corresponding to the namespace URI and local name
    */
   Object getRoot(Object o, MarshallingContext ctx, String namespaceURI, String localName);
}
