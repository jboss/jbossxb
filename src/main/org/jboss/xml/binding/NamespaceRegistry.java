/**
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

// $Id$

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple namespace registry
 *
 * [TODO] This ought to be a stack, allowing prefix overlaying
 *
 * @author Thomas.Diesler@jboss.org
 * @since 08-June-2004
 */
public class NamespaceRegistry
{
   public static final String PREFIX_XML_SCHEMA = "xsd";
   public static final String URI_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

   public static final String PREFIX_XML_SCHEMA_INSTANCE = "xsi";
   public static final String URI_XML_SCHEMA_INSTANCE = "http://www.w3.org/2000/10/XMLSchema-instance";

   // Maps namespace uri to prefix
   private HashMap namespaceMap = new HashMap();
   private int namespaceIndex;

   public NamespaceRegistry()
   {
      namespaceMap.put(URI_XML_SCHEMA, PREFIX_XML_SCHEMA);
      namespaceMap.put(URI_XML_SCHEMA_INSTANCE, PREFIX_XML_SCHEMA_INSTANCE);
   }

   /** Register a QName and return a QName that is guarantied to have a prefix
    */
   public QName registerQName(QName qname)
   {
      if (qname == null)
         return null;

      String prefix = registerURI(qname.getNamespaceURI(), qname.getPrefix());
      qname = new QName(qname.getNamespaceURI(), qname.getLocalPart(), prefix);
      return qname;
   }

   /** Register the given nsURI/prefix combination.
    * In case the prefix is null, it will be assigend.
    *
    * @param nsURI The nsURI
    * @param prefix The corresponding prefix, maybe null
    * @return A prefix, never null
    */
   public String registerURI(String nsURI, String prefix)
   {
      String regPrefix = (String)namespaceMap.get(nsURI);

      if (regPrefix == null && prefix != null && prefix.length() > 0)
         regPrefix = prefix;

      if (regPrefix == null)
         regPrefix = "ns" + (++namespaceIndex);

      namespaceMap.put(nsURI, regPrefix);
      return regPrefix;
   }

   /** Unregister the given nsURI.
    *
    * @param nsURI The nsURI
    */
   public void unregisterURI(String nsURI)
   {
      namespaceMap.remove(nsURI);
   }

   /** Get the prefix for a givven nsURI, maybe null
    */
   public String getPrefix(String nsURI)
   {
      return (String)namespaceMap.get(nsURI);
   }

   /** Get the nsURI for a given prefix, maybe null.
    */
   public String getNamespaceURI(String prefix)
   {
      Iterator it = namespaceMap.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry entry = (Map.Entry)it.next();
         if (prefix.equals(entry.getValue()))
            return (String)entry.getKey();
      }
      return null;
   }

   /** True if the given nsURI is registered.
    */
   public boolean isRegistered(String nsURI)
   {
      return namespaceMap.containsKey(nsURI);
   }

   /** Return an iterator over all registered nsURIs.
    */
   public Iterator getRegisteredURIs()
   {
      return namespaceMap.keySet().iterator();
   }
}
