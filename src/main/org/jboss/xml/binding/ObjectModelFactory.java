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
 * <p/>
 * <p>Each object model factory should have the following methods:
 * <p/>
 * <p><code>public Object newRoot()</code>
 * This method returns the root of the object model and is called by the object model builder
 * if the root was not provided in other way, see org.jboss.xml.binding.Unmarshaller.
 * <p/>
 * <p><code>public void endDocument(Object objectModel)</code>
 * This method is called by the object model builder when the object model is built. The objectModel
 * parameter is the object that was returned by the <code>newRoot()</code>.
 * As an example, in this method the object model can be validated.
 * <p/>
 * <p>a set of <code>newChild()</code> methods.
 * This method is called by the parser when the parsing of a new XML element is started.
 * Each <code>newChild()</code> method must have five arguments:
 * <ol>
 * <li>the first one is the parent object for this new child.
 * For the root element the object returned by the <code>newRoot()</code> is passed as the parent.</li>
 * <li>the second argument is an instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>the third one is the namespace URI of the child element.</li>
 * <li>the forthd one is the local name of the child element.</li>
 * <li>the attributes.</li>
 * </ol>
 * Each <code>newChild()</code> method returns either a new instance of
 * a child object that corresponds to the namespace URI and the local name
 * (in this case, the child is accepted, i.e. should be represented in the object model)
 * or <code>null</code> if this child should be ignored, i.e. not be represented in the object model.
 * <p/>
 * <p>a set of <code>addChild</code> methods.
 * This method is called by the object model builder when the parsing of the child object is done.
 * The arguments of the <code>addChild()</code> method are:
 * <ol>
 * <li>an instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>the parent object for this child.</li>
 * <li>the child object (returned earlier by the <code>newChild()</code>).</li>
 * </ol>
 * When <code>addChild()</code> is called the child object is read, can be validated
 * and added to the parent.
 * <p/>
 * <p>a set of <code>setValue()</code> methods.
 * This method is called when a new simple child element (i.e. a child element that does not contain
 * nested elements) with text value was read from the XML content.
 * The method has four parameters:
 * <ol>
 * <li>the first one is the object which was returned by the <code>newChild()</code>
 * earlier and for which the child element and its value were read;</li>
 * <li>the second argument is an instance of org.jboss.xml.binding.ContentNavigator</li>
 * <li>the namespace URI of the child element;</li>
 * <li>the name of the child element;</li>
 * <li>the value of the child element.</li>
 * </ol>
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ObjectModelFactory
{
   /**
    * This method is called on the factory by the object model builder when the parsing starts.
    *
    * @return the root of the object model.
    */
   Object newRoot(Object root,
                  ContentNavigator navigator,
                  String namespaceURI,
                  String localName,
                  Attributes attrs);
}