/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

/**
 * Represents document binding metadata. Actually, is just container of namespace bindings.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class DocumentBinding
{
   /**
    * Document binding used to delegate calls when requested binding is not found locally
    */
   private final DocumentBinding delegate;

   protected DocumentBinding(DocumentBinding delegate)
   {
      this.delegate = delegate;
   }

   /**
    * Returns namespace binding. If namespace binding is found in this instance, it will be returned. Otherwise,
    * the call is delegated down the stack of bindings.
    *
    * @param nsUri namespace URI to return binding for
    * @return namespace binding or null if a binding for the namespace URI was not found
    */
   public NamespaceBinding getNamespace(String nsUri)
   {
      NamespaceBinding ns = getNamespaceLocal(nsUri);
      if(ns == null)
      {
         ns = delegate == null ? null : delegate.getNamespace(nsUri);
      }
      return ns;
   }

   // Protected

   /**
    * Returns namespace binding. If namespace binding is found in this instance, it will be returned.
    * Otherwise, null is returned.
    *
    * @param nsUri namespace URI to return binding for
    * @return namespace binding or null if a binding for the namespace URI was not found
    */
   protected abstract NamespaceBinding getNamespaceLocal(String nsUri);
}
