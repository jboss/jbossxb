/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;


import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlDocumentImpl
   implements XmlDocument
{
   private final Map namespaces = new HashMap();

   public XmlNamespace getNamespace(String uri)
   {
      return (XmlNamespace)namespaces.get(uri);
   }

   public XmlElement getTopElement(String namespaceUri, String name)
   {
      XmlNamespace ns = getNamespace(namespaceUri);
      return ns == null ? null : ns.getElement(name);
   }

   public XmlNamespace addNamespace(String uri)
   {
      XmlNamespace ns = new XmlNamespaceImpl(uri, null);
      namespaces.put(uri, ns);
      return ns;
   }

   public XmlElement addTopElement(XmlNamespace ns, String name, XmlType xmlType)
   {
      return ns.addElement(name, xmlType);
   }

   public XmlElement addTopElement(XmlNamespace ns, String name)
   {
      return ns.addElement(name);
   }
}
