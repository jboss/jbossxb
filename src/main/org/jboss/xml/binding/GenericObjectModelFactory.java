/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;

/**
 * Direct implementations of ObjectModelFactory interface can be thought of as "typed" factories
 * in a sense that arguments of newChild, addChild and setValue methods are supposed to be specific Java classes
 * (other than java.lang.Object) from the target class hierarchy.
 * In GenericObjectModelFactory arguments of newChild, addChild and setValue are all of type java.lang.Object.
 * The framework won't introspect an implementation of the GenericObjectModelFactory for "typed" newChild, addChild and setValue.
 * Instead it will call the generic methods and it's the responsibility of the implementation to recognize the types
 * and build the object model.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface GenericObjectModelFactory
   extends ObjectModelFactory
{
   Object newChild(Object parent,
                   ContentNavigator navigator,
                   String namespaceURI,
                   String localName,
                   Attributes attrs);

   void addChild(Object parent,
                 Object child,
                 ContentNavigator navigator,
                 String namespaceURI,
                 String localName);

   void setValue(Object o,
                 ContentNavigator navigator,
                 String namespaceURI,
                 String localName,
                 String value);
}
