/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

/**
 * The base class to implement basic element binding that supports metadata chaining (delegation down the stack).
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class AbstractBasicElementBinding
   implements BasicElementBinding
{
   /**
    * Element binding used to delegate calls to when requested bindings were not found in this instance
    */
   private final AbstractBasicElementBinding delegate;

   protected AbstractBasicElementBinding(AbstractBasicElementBinding delegate)
   {
      this.delegate = delegate;
   }

   /**
    * Implements getChildElement(elementName) defined in BasicElementBinding using delegation to lower bindings in
    * the stack when requested binding is not found at this level.
    *
    * @param elementName child element name
    * @return child element binding or null if the binding was not found in the stack
    */
   public ElementBinding getChildElement(String elementName)
   {
      ElementBinding child = getChildElementLocal(elementName);
      if(child == null)
      {
         child = delegate == null ? null : delegate.getChildElement(elementName);
      }

      return child;
   }

   // Protected

   /**
    * Implements getChildElement(elementName) defined in BasicElementBinding that
    * doesn't use delegation to lower level bindings in the stack.
    *
    * @param elementName child element name
    * @return child element binding defined on this level or null if it is not defined on this level
    */
   protected abstract ElementBinding getChildElementLocal(String elementName);
}
