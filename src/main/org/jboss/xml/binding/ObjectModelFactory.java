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
 * to build the object graph.
 * <p>Each object model factory must implement one method <code>newRoot</code> defined in the ObjectModelFactory interface
 * and a set of newChild, addChild and setValue methods descovered by the framework at runtime with introspection.
 * So the following methods should be implemented:
 * <p/>
 * <ul>
 * <li><code>newRoot</code> method
 * <pre>
 * public Object newRoot(java.lang.Object root,
 *                       org.jboss.xml.binding.ContentNavigator navigator,
 *                       java.lang.String namespaceURI,
 *                       java.lang.String localName,
 *                       org.xml.sax.Attributes attrs)
 * </pre>
 * This method is called on the object model factory by the framework and returns the root of the object model.
 * If the <code>root</code> argument is null the factory is supposed to create and return a new one.
 * If the <code>root</code> argument is not null (i.e. the user provided the root object through the
 * org.jboss.xml.binding.Unmarshaller) the factory should either just return it as is or extract the real root
 * from the <code>root</code> argument corresponding to the namespace URI and local name.
 * </li>
 * <li>
 * a set of <code>newChild</code> methods.
 * This method is called by the framework on the object model factory when parsing of a new XML element started.
 * Each <code>newChild</code> method must have five arguments:
 * <ol>
 * <li>parent object of a concrete Java type (not java.lang.Object) for this new child</li>
 * <li>instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>namespace URI of the child XML element as java.lang.String</li>
 * <li>local name of the child XML element as java.lang.String</li>
 * <li>attributes of the child XML element as org.xml.sax.Attributes</li>
 * </ol>
 * Each <code>newChild()</code> method returns either a new instance of
 * a child object that represents the XML element with the namespace URI and local name
 * (in this case, the child XML element is accepted, i.e. should be represented in the object graph)
 * or <code>null</code> if this child XML element should be ignored, i.e. not be represented in the object graph.
 * </li>
 * <li>a set of <code>addChild</code> methods
 * This method is called on the object model factory by the framework when parsing of a child XML element is complete.
 * The arguments of the <code>addChild()</code> method are:
 * <ol>
 * <li>parent object of a conrete Java type (not java.lang.Object) of the child</li>
 * <li>child object of a concrete Java type (returned earlier by the <code>newChild</code>)</li>
 * <li>instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>namespace URI for the child XML element as java.lang.String</li>
 * <li>local name for the child XML element as java.lang.String</li>
 * </ol>
 * When <code>addChild</code> method is called, the child object is supposed to be populated with all the data from
 * the corresponding XML element. The child object now can be validated and added to the parent.
 * </li>
 * <li>a set of <code>setValue</code> methods
 * This method is called on the object model factory by the framework when a new XML element with text content was parsed.
 * The method must have four arguments:
 * <ol>
 * <li>an object of a concrete Java type (not java.lang.Object) which was returned earlier by a <code>newChild</code>
 * for which the value of an XML element was read</li>
 * <li>instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>namespace URI of the child XML element as java.lang.String</li>
 * <li>local name of the child XML element as java.lang.String</li>
 * <li>the value of the child XML element as java.lang.String</li>
 * </ol>
 * In <code>setValue</code> method the object model factory is supposed to set a value on the field which represents
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
    * This method is called by the object model factory and returns the root of the object graph.
    * If the <code>root</code> argument is null the factory is supposed to create and return a new one.
    * If the <code>root</code> argument is not null (i.e. the user provided the root object through the
    * org.jboss.xml.binding.Unmarshaller) then the factory should either just return it as is
    * or extract the real root from the <code>root</code> argument based on the namespace URI and local name.
    *
    * @param root         an object that is the root or which contains the root object
    * @param navigator    content navigator
    * @param namespaceURI namespace URI of the root
    * @param localName    local name of the root
    * @param attrs        attributes of the root object
    * @return the root of the object graph
    */
   Object newRoot(Object root,
                  UnmarshallingContext navigator,
                  String namespaceURI,
                  String localName,
                  Attributes attrs);

   Object completeRoot(Object root, UnmarshallingContext navigator, String namespaceURI, String localName);
}
