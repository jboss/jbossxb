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
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingDocumentBinding
   implements DocumentBinding
{
   private final Map namespaceBindings = new HashMap();
   private final List delegates = new ArrayList();

   public DelegatingDocumentBinding(DocumentBinding doc)
   {
      addDelegate(doc);
   }

   void addDelegate(DocumentBinding doc)
   {
      delegates.add(doc);
      if(doc instanceof PluggableDocumentBinding)
      {
         ((PluggableDocumentBinding)doc).setDocumentBinding(this);
      }
   }

   DelegatingNamespaceBinding bindNamespace(String namespaceUri, String javaPackage)
   {
      NamespaceBinding ns = new NamespaceBindingImpl(this, namespaceUri, javaPackage);
      DelegatingNamespaceBinding cachedNs = (DelegatingNamespaceBinding)getNamespace(namespaceUri);//(DelegatingNamespaceBinding)namespaceBindings.get(namespaceUri);
      if(cachedNs == null)
      {
         cachedNs = new DelegatingNamespaceBinding(this, ns);
         namespaceBindings.put(cachedNs.getNamespaceUri(), cachedNs);
      }
      else
      {
         cachedNs.addDelegate(ns);
      }
      return cachedNs;
   }

   public NamespaceBinding getNamespace(String namespaceUri)
   {
      DelegatingNamespaceBinding cachedNs = (DelegatingNamespaceBinding)namespaceBindings.get(namespaceUri);
      if(cachedNs == null)
      {
         for(int i = delegates.size() - 1; i >= 0; --i)
         {
            DocumentBinding doc = (DocumentBinding)delegates.get(i);
            NamespaceBinding ns = doc.getNamespace(namespaceUri);
            if(ns != null)
            {
               if(ns instanceof DelegatingNamespaceBinding)
               {
                  cachedNs = (DelegatingNamespaceBinding)ns;
               }
               else
               {
                  cachedNs = new DelegatingNamespaceBinding(this, ns);
               }
               namespaceBindings.put(namespaceUri, cachedNs);
               break;
            }
         }
      }
      return cachedNs;
   }
}
