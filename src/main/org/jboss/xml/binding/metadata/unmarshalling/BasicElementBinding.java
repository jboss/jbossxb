/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

/**
 * The interface top and non-top level element bindings implement.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface BasicElementBinding
{
   /**
    * @return  namespace binding the element belongs to
    */
   NamespaceBinding getNamespace();

   /**
    * @return element name this binding is defined for
    */
   String getElementName();

   /**
    * @return Java type this element is bound to
    */
   Class getJavaType();

   /**
    * @param name name of a child element
    * @return child element binding or null if the child element binding was not found
    */
   ElementBinding getChildElement(String name);
}
