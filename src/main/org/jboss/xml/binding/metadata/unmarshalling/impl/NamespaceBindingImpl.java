/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.NamespaceBinding;
import org.jboss.xml.binding.metadata.unmarshalling.TopElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBinding;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class NamespaceBindingImpl
   implements NamespaceBinding
{
   private final DelegatingDocumentBinding doc;
   private final String namespaceUri;
   private final String javaPackage;
   private final Map tops = new HashMap();

   public NamespaceBindingImpl(DelegatingDocumentBinding doc, String namespaceUri, String javaPackage)
   {
      this.namespaceUri = namespaceUri;
      this.javaPackage = javaPackage;
      this.doc = doc;
   }

   void addTopElement(TopElementBinding top)
   {
      tops.put(top.getElementName(), top);
   }

   public DocumentBinding getDocument()
   {
      return doc;
   }

   public String getNamespaceUri()
   {
      return namespaceUri;
   }

   public String getJavaPackage()
   {
      return javaPackage;
   }

   public TopElementBinding getTopElement(String elementName)
   {
      return (TopElementBinding)tops.get(elementName);
   }
}
