/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.metadata.unmarshalling.impl.DelegatingDocumentBindingFactory;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class DocumentBindingFactory
{
   public static DocumentBindingFactory newInstance()
   {
      return new DelegatingDocumentBindingFactory();
   }

   public abstract DocumentBinding newDocumentBinding(DocumentBinding delegate);

   public abstract NamespaceBinding bindNamespace(DocumentBinding doc, String namespaceUri, String javaPackage);

   public abstract TopElementBinding bindTopElement(NamespaceBinding ns, String elementName, Class javaClass);

   public abstract ElementBinding bindElement(BasicElementBinding parent,
                                              String namespaceUri,
                                              String elementName,
                                              String fieldName,
                                              Class javaType);

   public abstract AttributeBinding bindAttribute(BasicElementBinding parent,
                                                  String namespaceUri,
                                                  String attributeName,
                                                  String fieldName,
                                                  Class javaType);
}
