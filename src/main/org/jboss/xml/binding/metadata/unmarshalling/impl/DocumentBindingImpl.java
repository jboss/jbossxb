/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xml.binding.metadata.unmarshalling.NamespaceBinding;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DocumentBindingImpl
   implements DocumentBinding
{
   private final Map namespaces = new HashMap();

   void addNamespace(NamespaceBinding ns)
   {
      namespaces.put(ns.getNamespaceUri(), ns);
   }

   public NamespaceBinding getNamespace(String namespaceUri)
   {
      return (NamespaceBinding)namespaces.get(namespaceUri);
   }
}
