/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

/**
 * The interface all object model providers must implement. Object model providers are used on marshalling
 * providing data for XML content based on the object model and XML schema or DTD.
 *
 * <p>Each object model provider should have the following methods:
 *
 * <p><code>public Object getDocument()</code>
 * This method returns the root of the object model and is called by the marshaller
 * if the root was not provided in other way, see org.jboss.xml.binding.Marshaller.
 *
 * <p>a set of <code>provideChildren()</code> methods.
 * This method is called by the marshaller when the marshalling of a new XML element is started.
 * Each <code>provideChildren()</code> method must have three arguments:
 * <ol>
 * <li>the first one is the parent object that is "asked" for its children.
 *     For the root element the object returned by the <code>getDocument()</code> is passed as the parent.</li>
 * <li>the third one is the namespace URI of the child element.</li>
 * <li>the forth one is the local name of the child element.</li>
 * </ol>
 * A <code>provideChildren()</code> method returns children according to the namespace URI and local name.
 * It can return null if there are no children in this object model corresponding to the namespace and local name.
 * If there is only one child corresponding to the namespace and local name then it is returned as is.
 * If there are many children, they are can be returned as an array or java.util.List, or java.util.Collection,
 * or java.util.Iterator.
 *
 * <p>A set of <code>provideValue()</code> methods.
 * This method is called for objects that represent XML elements with simple content.
 * The method has the following arguments:
 * <ol>
 * <li>an object that is "asked" to provide a value corresponding to the other arguments.</li>
 * <li>the namespace URI of the element with simple content.</li>
 * <li>the name of the element with simple content.</li>
 * </ol>
 * The method returns either null if the object model does not have any value corresponding to the namespace
 * and local name passed in (in this case the XML content will not contain this element) or the actual value
 * that will appear in XML content.
 *
 * <p>A set of <code>provideAttributeValue()</code> methods.
 * This method is called for objects that represent XML elements with attributes.
 * The method has the following arguments:
 * <ol>
 * <li>an object that is "asked" to provide an attribute value corresponding to the other arguments.</li>
 * <li>the namespace URI of the attribute.</li>
 * <li>the local name of the attribute.</li>
 * </ol>
 * The method returns either null if the object model does not have any value corresponding to the namespace
 * and local name passed in (in this case the XML content will not contain this attribute) or the actual value
 * that will appear as the attribute value in XML content.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public interface ObjectModelProvider
{
   Object getDocument();
}
