/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

/**
 * Represents a namespace binding.
 * <br/>Binds namespace to a Java package and is a container for top level element bindings.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class NamespaceBinding
{
   /**
    * Namespace binding to delegate calls to when requested bindings are not found in this instance
    */
   private final NamespaceBinding delegate;

   protected NamespaceBinding(NamespaceBinding delegate)
   {
      this.delegate = delegate;
   }

   /**
    * @return  namespace URI this binding is defined for
    */
   public abstract String getNamespaceURI();

   /**
    * @return Java package name the namespace URI is bound to
    */
   public abstract String getJavaPackage();

   /**
    * Returns top-level element binding. If the binding is found in this instance,
    * the binding will be returned to the caller. Otherwise, the call will be delegated
    * to lower level bindings in the stack.
    *
    * @param elementName  top-level element name to return binding for
    * @return  top-level element binding or null if the binding was not found.
    */
   public TopElementBinding getTopElement(String elementName)
   {
      TopElementBinding top = getTopElementLocal(elementName);
      if(top == null)
      {
         top = delegate == null ? null : delegate.getTopElement(elementName);
      }
      return top;
   }

   // Protected

   /**
    * Returns top-level element binding. If the binding is found in this instance,
    * the binding will be returned to the caller. Otherwise, null is returned.
    *
    * @param elementName  top-level element name to return binding for
    * @return  top-level element binding or null if the binding was not found.
    */
   protected abstract TopElementBinding getTopElementLocal(String elementName);
}
