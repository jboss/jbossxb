/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;


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
