/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import org.jboss.xml.binding.metadata.unmarshalling.impl.DocumentBindingFactoryImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class DocumentBindingFactory
{
   public static DocumentBindingFactory newInstance()
   {
      return new DocumentBindingFactoryImpl();
   }

   public abstract DocumentBindingStack newDocumentBindingStack();

   public abstract DocumentBinding newDocumentBinding();

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
