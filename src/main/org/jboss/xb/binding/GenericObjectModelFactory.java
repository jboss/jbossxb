/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import org.xml.sax.Attributes;

/**
 * Direct implementations of <code>ObjectModelFactory</code> interface can be thought of as "typed" factories
 * in a sense that parameter types of <code>newChild</code>, <code>addChild</code> and <code>setValue</code> methods
 * (discovered by the framework at runtime with introspection) are supposed to be of specific Java classes
 * (other than <code>java.lang.Object</code>) from the target class hierarchy.
 * <p/>In this interface, <code>newChild</code>, <code>addChild</code> and <code>setValue</code> methods are defined
 * with arguments of type <code>java.lang.Object</code>.
 * <br/>The framework won't introspect an implementation of this interface for "typed" implementations of
 * <code>newChild</code>, <code>addChild</code> and <code>setValue</code> methods.
 * Instead it will call the declared generic methods and it's the responsibility of the implementation
 * of this interface to recognize the types and build the object graph appropriately.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface GenericObjectModelFactory
   extends ObjectModelFactory
{
   /**
    * This method is called when parsing of a new not top-level XML element started.
    * The method should either return an object that represents this XML element in the Java object model or
    * null if this XML element is not represented in the Java object model.
    *
    * @param parent       an object that represents the parent XML element in the object model
    * @param ctx          unmarshalling context
    * @param namespaceURI namespace URI of the XML element
    * @param localName    local name of the XML element
    * @param attrs        attributes of the XML element
    * @return an object that represents the XML element in the Java object model or null
    */
   Object newChild(Object parent, UnmarshallingContext ctx, String namespaceURI, String localName, Attributes attrs);

   /**
    * This method is called when parsing of a not top-level XML element completed.
    * The object that represents this XML element in the Java model should now be completely initialized.
    * An implementation of this method could validate the object that represents the XML element
    * in the Java object model and add it to the parent.
    *
    * @param parent       an object that represents the parent XML element in the object model
    * @param child        an object that was returned by the <code>newChild</code> method that
    *                     was called when parsing of this XML element started
    * @param ctx          unmarshalling context
    * @param namespaceURI namespace URI of the XML element
    * @param localName    local name of the XML element
    */
   void addChild(Object parent, Object child, UnmarshallingContext ctx, String namespaceURI, String localName);

   /**
    * This method is called when a new not top-level simple XML element (with text content) was parsed.
    * Such elements are usually mapped to fields in Java classes. So, usually, an implementation of this method
    * will set the field the XML element is bound to in the parent object to the parsed value possibly applying
    * some unmarshalling rule for it.
    *
    * @param o            an object that represents the parent XML element in the Java object model
    * @param ctx          unmarshalling context
    * @param namespaceURI namespace URI of the XML element
    * @param localName    local name of the XML element
    * @param value        value of the XML element as it appears in the XML content
    */
   void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value);
}
