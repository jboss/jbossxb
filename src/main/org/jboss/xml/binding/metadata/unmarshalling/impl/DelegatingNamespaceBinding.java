/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.NamespaceBinding;
import org.jboss.xml.binding.metadata.unmarshalling.TopElementBinding;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingNamespaceBinding
   implements NamespaceBinding
{
   private final String namespaceUri;
   private final Map tops = new HashMap();
   private final List delegates = new ArrayList();

   public DelegatingNamespaceBinding(NamespaceBinding delegate)
   {
      this.namespaceUri = delegate.getNamespaceUri();
      delegates.add(delegate);
   }

   void addDelegate(NamespaceBinding ns)
   {
      delegates.add(ns);
   }

   DelegatingTopElementBinding bindTopElement(String elementName, Class javaType)
   {
      TopElementBinding top = new TopElementBindingImpl(new QName(namespaceUri, elementName), javaType);
      DelegatingTopElementBinding delegatingTop = (DelegatingTopElementBinding)tops.get(elementName);
      if(delegatingTop == null)
      {
         delegatingTop = new DelegatingTopElementBinding(top);
         tops.put(elementName, delegatingTop);
      }
      else
      {
         delegatingTop.addDelegate(top);
      }
      return delegatingTop;
   }

   public String getNamespaceUri()
   {
      return namespaceUri;
   }

   public String getJavaPackage()
   {
      NamespaceBinding ns = (NamespaceBinding)delegates.get(delegates.size() - 1);
      return ns.getJavaPackage();
   }

   public TopElementBinding getTopElement(String elementName)
   {
      DelegatingTopElementBinding cachedTop = (DelegatingTopElementBinding)tops.get(elementName);
      if(cachedTop == null)
      {
         for(int i = delegates.size() - 1; i >= 0; --i)
         {
            NamespaceBinding ns = (NamespaceBinding)delegates.get(i);
            TopElementBinding top = ns.getTopElement(elementName);
            if(top != null)
            {
               if(top instanceof DelegatingTopElementBinding)
               {
                  cachedTop = (DelegatingTopElementBinding)top;
               }
               else
               {
                  cachedTop = new DelegatingTopElementBinding(top);
               }
               tops.put(elementName, cachedTop);
               break;
            }
         }
      }
      return cachedTop;
   }
}
