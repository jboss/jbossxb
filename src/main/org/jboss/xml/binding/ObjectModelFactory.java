/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;

/**
 * The interface all object model factories must implement. Object model factories are used on unmarshalling
 * to build the object model.
 * <p>Each object model factory should have the following methods:
 * <ul>
 * <li><code>public Object newRoot(Object root,
 * ContentNavigator navigator,
 * String namespaceURI,
 * String localName,
 * Attributes attrs)</code>
 * This method is called by the object model factory and returns the root of the object model.
 * If the <code>root</code> argument is null the factory is supposed to create and return a new one.
 * If the <code>root</code> is not null (i.e. the user provided the root object through the
 * org.jboss.xml.binding.Unmarshaller) the factory should just return it.
 * </li>
 * <li>
 * a set of <code>newChild()</code> methods.
 * This method is called by the parser when the parsing of a new XML element is started.
 * Each <code>newChild()</code> method must have five arguments:
 * <ol>
 * <li>parent object for this new child</li>
 * <li>an instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>namespace URI of the child element</li>
 * <li>local name of the child element</li>
 * <li>the attributes</li>
 * </ol>
 * Each <code>newChild()</code> method returns either a new instance of
 * a child object that corresponds to the namespace URI and the local name
 * (in this case, the child is accepted, i.e. should be represented in the object model)
 * or <code>null</code> if this child should be ignored, i.e. not be represented in the object model.
 * </li>
 * <li>a set of <code>addChild</code> methods.
 * This method is called by the object model builder when the parsing of the child object is done.
 * The arguments of the <code>addChild()</code> method are:
 * <ol>
 * <li>parent object</li>
 * <li>child object (returned earlier by the <code>newChild()</code>)</li>
 * <li>an instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>namespace URI for the child element.</li>
 * <li>local name for the child element.</li>
 * </ol>
 * When <code>addChild()</code> is called the child object is parsed and initialized, can be validated
 * and added to the parent.
 * </li>
 * <li>a set of <code>setValue()</code> methods.
 * This method is called when a new simple child element (i.e. a child element that does not contain
 * nested elements) with text value was read from the XML content.
 * The method has four parameters:
 * <ol>
 * <li>object which was returned by the <code>newChild()</code> earlier and for which the child element and its value were read</li>
 * <li>an instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>namespace URI of the child element</li>
 * <li>local name of the child element</li>
 * <li>the value of the child element</li>
 * </ol>
 * </li>
 * </ul>
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ObjectModelFactory
{
   Object newRoot(Object root,
                  ContentNavigator navigator,
                  String namespaceURI,
                  String localName,
                  Attributes attrs);
}
