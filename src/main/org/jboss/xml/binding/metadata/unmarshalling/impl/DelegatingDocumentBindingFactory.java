/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.DocumentBindingFactory;
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBinding;
import org.jboss.xml.binding.metadata.unmarshalling.NamespaceBinding;
import org.jboss.xml.binding.metadata.unmarshalling.TopElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.AttributeBinding;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingDocumentBindingFactory
   extends DocumentBindingFactory
{
   public DocumentBinding newDocumentBinding(DocumentBinding delegate)
   {
      DelegatingDocumentBinding result;
      if(delegate instanceof DelegatingDocumentBinding)
      {
         result = (DelegatingDocumentBinding)delegate;
      }
      else
      {
         if(delegate == null)
         {
            delegate = new DocumentBindingImpl();
         }
         result = new DelegatingDocumentBinding(delegate);
      }
      return result;
   }

   public NamespaceBinding bindNamespace(DocumentBinding doc, String namespaceUri, String javaPackage)
   {
      DelegatingDocumentBinding delegatingDoc = (DelegatingDocumentBinding)doc;
      return delegatingDoc.bindNamespace(namespaceUri, javaPackage);
   }

   public TopElementBinding bindTopElement(NamespaceBinding ns, String elementName, Class javaType)
   {
      DelegatingNamespaceBinding delegatingNs = (DelegatingNamespaceBinding)ns;
      return delegatingNs.bindTopElement(elementName, javaType);
   }

   public ElementBinding bindElement(BasicElementBinding parent,
                                     String namespaceUri,
                                     String elementName,
                                     String fieldName,
                                     Class javaType)
   {
      DelegatingBasicElementBinding delegatingParent = (DelegatingBasicElementBinding)parent;
      return delegatingParent.bindChildElement(new QName(namespaceUri, elementName), fieldName, javaType);
   }

   public AttributeBinding bindAttribute(BasicElementBinding element,
                                         String namespaceUri,
                                         String attributeName,
                                         String fieldName,
                                         Class javaType)
   {
      DelegatingBasicElementBinding delegating = (DelegatingBasicElementBinding)element;
      return delegating.bindAttribute(new QName(namespaceUri, attributeName), fieldName, javaType);
   }
}
