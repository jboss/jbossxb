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

      String prefix = registerNamespaceURI(qname.getNamespaceURI(), qname.getPrefix());
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
   public String registerNamespaceURI(String nsURI, String prefix)
   {
      String regPrefix = (String)namespaceMap.get(nsURI);

      if (regPrefix == null && prefix != null && prefix.length() > 0)
         regPrefix = prefix;

      if (regPrefix == null)
         regPrefix = "ns" + (++namespaceIndex);

      namespaceMap.put(nsURI, regPrefix);
      return regPrefix;
   }

   public String getPrefix(String nsURI)
   {
      return (String)namespaceMap.get(nsURI);
   }

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

   public boolean isRegisteredNamespaceURI(String nsURI)
   {
      return namespaceMap.containsKey(nsURI);
   }

   public Iterator getRegisteredNamespaceURIs()
   {
      return namespaceMap.keySet().iterator();
   }
}
