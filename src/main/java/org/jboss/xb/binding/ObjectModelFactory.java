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

import org.xml.sax.Attributes;

/**
 * All object model factories must implement this interface. Object model factories are used on unmarshalling
 * to build an object graph that is a representation of the XML content unmarshalled.
 * <p/>Each object model factory must implement methods <code>newRoot</code> and <code>completeRoot</code>
 * defined in this interface, plus a set of <code>newChild</code>, <code>addChild</code> and <code>setValue</code>
 * methods that will be descovered by the framework at runtime with introspection.
 * <p/>The contract for methods discovered at runtime with introspection:
 * <ul>
 * <li><b><code>newChild</code> methods</b>
 * <br/>This method is called by the framework on the object model factory when parsing of a new XML element started.
 * Each <code>newChild</code> method must have five arguments:
 * <ol>
 * <li>parent object of a concrete Java type (not <code>java.lang.Object</code>) for this new child</li>
 * <li>instance of <code>org.jboss.xml.binding.UnmarshallingContext</code></li>
 * <li>namespace URI of the child XML element as <code>java.lang.String</code></li>
 * <li>local name of the child XML element as <code>java.lang.String</code></li>
 * <li>attributes of the child XML element as <code>org.xml.sax.Attributes</code></li>
 * </ol>
 * Each <code>newChild()</code> method returns either a new instance of
 * the child object that represents the XML element with the namespace URI and local name
 * (in this case, the child XML element is said to be accepted, i.e. should be represented in the object graph)
 * or <code>null</code> if this child XML element should be ignored, i.e. not be represented in the object graph.
 * </li>
 * <br/>
 * <li><b><code>addChild</code> methods</b>
 * <br/>This method is called on the object model factory by the framework when parsing
 * of a child XML element completed. The arguments of the <code>addChild()</code> method are:
 * <ol>
 * <li>parent object of a conrete Java type (not <code>java.lang.Object</code>) of the child</li>
 * <li>child object of a concrete Java type (returned earlier by the <code>newChild</code>
 * method that was called when parsing of this child XML element started)</li>
 * <li>instance of <code>org.jboss.xml.binding.UnmarshallingContext</code></li>
 * <li>namespace URI for the child XML element <code>as java.lang.String</code></li>
 * <li>local name for the child XML element as <code>java.lang.String</code></li>
 * </ol>
 * When <code>addChild</code> method is called, the child object is supposed to be populated with all the data from
 * the corresponding XML element. The child object now can be validated and added to the parent.
 * </li>
 * <br/>
 * <li><b><code>setValue</code> methods</b>
 * <br/>This method is called on the object model factory by the framework when a new XML element
 * with text content was parsed.
 * The method must have four arguments:
 * <ol>
 * <li>an object of a concrete Java type (not <code>java.lang.Object</code>) which was returned earlier
 * by the <code>newChild</code> method (that was called when parsing of the parent XML element started)
 * for which the value of an XML element was read</li>
 * <li>instance of <code>org.jboss.xml.binding.UnmarshallingContext</code></li>
 * <li>namespace URI of the child XML element as <code>java.lang.String</code></li>
 * <li>local name of the child XML element as <code>java.lang.String</code></li>
 * <li>the value of the child XML element as <code>java.lang.String</code></li>
 * </ol>
 * In <code>setValue</code> method the object model factory is supposed to set the value on the field which represents
 * the parsed XML element possibly converting the parsed XML element value to the field's Java type.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ObjectModelFactory
{
   /**
    * This method is called by the framework and returns the root of the object graph.
    * <p/>If <code>root</code> argument is null, the factory is supposed to create and return a new one
    * that is going to be the real root object of the graph or an object that will represent the root object
    * during unmarshalling and which will be used to create the real root object when unmarshalling is complete
    * in <code>completeRoot</code> if the real root object can't be created while its children are not available,
    * e.g. no no-arg constructor or other reasons.
    * <p/>If <code>root</code> argument is not null (i.e. a user provided the root object through the
    * <code>org.jboss.xml.binding.Unmarshaller</code>) then the factory could just return it as is or
    * extract the real root from the <code>root</code> argument based on the namespace URI and local name
    * if <code>root</code> argument wraps/contains the real root.
    *
    * @param root         an object that is the root or which contains the root of the object graph
    * @param ctx          unmarshalling context
    * @param namespaceURI namespace URI of the root
    * @param localName    local name of the root
    * @param attrs        attributes of the root object
    * @return the root of the object graph
    */
   Object newRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName, Attributes attrs);

   /**
    * This method is called by the framework when unmarshalling of the object graph completed.
    * The method returns the root object of the object graph.
    * If at the beginning of unmarshalling <code>newRoot</code> returned not the real root object but an
    * object that represented the root during unmarshalling, the real root object should be created
    * and returned.
    *
    * @param root         the object returned by <code>newRoot</code> at the beginning of unmarshalling
    * @param ctx          unmarshalling context
    * @param namespaceURI namespace URI that corresponds to the root
    * @param localName    local element name the root of the object graph is bound to
    * @return the root of the object graph
    */
   Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName);
}
