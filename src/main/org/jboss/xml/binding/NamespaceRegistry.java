/**
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

// $Id$

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * A simple namespace registry
 *
 * [TODO] cleanup the api
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @since 08-June-2004
 */
public class NamespaceRegistry
        implements NamespaceContext
{
   public static final String PREFIX_XML_SCHEMA = "xsd";
   public static final String URI_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

   public static final String PREFIX_XML_SCHEMA_INSTANCE = "xsi";
   public static final String URI_XML_SCHEMA_INSTANCE = "http://www.w3.org/2000/10/XMLSchema-instance";

   private int namespaceIndex;

   private final Map prefixByUri = new HashMap();
   private final Map uriByPrefix = new HashMap();

   public NamespaceRegistry()
   {
      // todo is this really a good idea to have it here?
      registerURI(URI_XML_SCHEMA, PREFIX_XML_SCHEMA);
      registerURI(URI_XML_SCHEMA_INSTANCE, PREFIX_XML_SCHEMA_INSTANCE);
   }

   /** Register a QName and return a QName that is guarantied to have a prefix
    */
   public QName registerQName(QName qname)
   {
      if (qname == null)
         return null;

      String prefix = qname.getPrefix();
      if (prefix.length() == 0)
         prefix = null;

      prefix = registerURI(qname.getNamespaceURI(), prefix);
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
      if (prefix == null)
      {
         prefix = "ns" + (++namespaceIndex);
      }

      addPrefixMapping(prefix, nsURI);

      return prefix;
   }

   /**
    * Adds prefix mapping.
    *
    * @param prefix  prefix to map
    * @param uri  the URI to prefix to
    */
   public void addPrefixMapping(String prefix, String uri)
   {
      Object obj = uriByPrefix.get(prefix);
      assertMapEntry(obj);

      if (obj == null)
      {
         uriByPrefix.put(prefix, uri);
      }
      else if (obj instanceof String)
      {
         LinkedList list = new LinkedList();
         list.add(obj);
         list.add(uri);
         uriByPrefix.put(prefix, list);
      }
      else if (obj instanceof LinkedList)
      {
         ((LinkedList)obj).add(uri);
      }

      obj = prefixByUri.get(uri);
      assertMapEntry(obj);

      if (obj == null)
      {
         prefixByUri.put(uri, prefix);
      }
      else if (obj instanceof String)
      {
         LinkedList list = new LinkedList();
         list.add(obj);
         list.add(prefix);
         prefixByUri.put(uri, list);
      }
      else if (obj instanceof LinkedList)
      {
         ((LinkedList)obj).add(prefix);
      }
   }

   private void assertMapEntry(Object obj)
   {
      if ((obj == null || obj instanceof String || obj instanceof LinkedList) == false)
      {
         throw new IllegalStateException("Unexpected entry type: expected String or LinkedList but got " + obj.getClass());
      }
   }

   /**
    * Removes the last mapping for the given prefix.
    *
    * @param prefix  the prefix to remove mapping for
    */
   public void removePrefixMapping(String prefix)
   {
      Object obj = uriByPrefix.get(prefix);
      assertMapEntry(obj);

      if (obj != null)
      {
         String uri = null;
         if (obj instanceof String)
         {
            uri = (String)obj;
            uriByPrefix.remove(prefix);
         }
         else if (obj instanceof LinkedList)
         {
            LinkedList list = (LinkedList)obj;
            uri = (String)list.removeLast();
            if (list.isEmpty())
            {
               uriByPrefix.remove(prefix);
            }
         }

         if (uri != null)
         {
            obj = prefixByUri.get(uri);
            if (obj instanceof String)
            {
               if (!prefix.equals(obj))
               {
                  throw new IllegalStateException("Inconsistent mapping: prefix=" + prefix + ", found=" + obj);
               }
               prefixByUri.remove(uri);
            }
            else if (obj instanceof LinkedList)
            {
               LinkedList list = (LinkedList)obj;
               list.remove(prefix);
               if (list.isEmpty())
               {
                  prefixByUri.remove(uri);
               }
            }
         }
      }
   }

   /**
    * Unregisters all prefix mappings for the given URI, not just the last one added.
    * todo what is this used for?
    *
    * @param nsURI the URI to unregister
    */
   public void unregisterURI(String nsURI)
   {
      Object obj = prefixByUri.get(nsURI);
      assertMapEntry(obj);

      if (obj != null)
      {
         String prefix = null;
         if (obj instanceof String)
         {
            prefix = (String)obj;
            prefixByUri.remove(nsURI);
            removePrefixMappingOnly(prefix, nsURI);
         }
         else if (obj instanceof LinkedList)
         {
            LinkedList list = (LinkedList)obj;
            for (int i = 0; i < list.size(); ++i)
            {
               removePrefixMappingOnly((String)list.get(i), nsURI);
            }
            prefixByUri.remove(nsURI);
         }
      }
   }

   /** True if the given nsURI is registered.
    */
   public boolean isRegistered(String nsURI)
   {
      return prefixByUri.containsKey(nsURI);
   }

   /** Return an iterator over all registered nsURIs.
    */
   public Iterator getRegisteredURIs()
   {
      return prefixByUri.keySet().iterator();
   }

   // NamespaceContext implementation

   /**
    * Returns the last mapping for the prefix or null if the prefix was not mapped.
    */
   public String getPrefix(String nsURI)
   {
      Object obj = prefixByUri.get(nsURI);
      assertMapEntry(obj);

      String prefix = null;
      if (obj instanceof String)
      {
         prefix = (String)obj;
      }
      else if (obj instanceof LinkedList)
      {
         prefix = (String)((LinkedList)obj).getLast();
      }

      return prefix;
   }

   /**
    * Returns all prefixes for the given URI.
    *
    * @param namespaceURI  the URI to return prefixes for
    * @return  prefixes mapped to the URI
    */
   public Iterator getPrefixes(String namespaceURI)
   {
      Object obj = prefixByUri.get(namespaceURI);
      assertMapEntry(obj);

      Iterator result = null;
      if (obj == null)
      {
         result = Collections.EMPTY_LIST.iterator();
      }
      else if (obj instanceof String)
      {
         result = Collections.singletonList(obj).iterator();
      }
      else if (obj instanceof LinkedList)
      {
         result = ((LinkedList)obj).iterator();
      }

      return result;
   }

   /** Get the nsURI for a given prefix, maybe null.
    */
   public String getNamespaceURI(String prefix)
   {
      Object obj = uriByPrefix.get(prefix);
      assertMapEntry(obj);

      String uri = null;
      if (obj instanceof String)
      {
         uri = (String)obj;
      }
      else if (obj instanceof LinkedList)
      {
         uri = (String)((LinkedList)obj).getLast();
      }

      return uri;
   }

   // Private

   private void removePrefixMappingOnly(String prefix, String nsURI)
   {
      Object obj = uriByPrefix.get(prefix);
      if (obj instanceof String)
      {
         if (!obj.equals(nsURI))
         {
            throw new IllegalStateException("Inconsistent mapping: uri=" + nsURI + ", found=" + obj);
         }
         uriByPrefix.remove(prefix);
      }
      else if (obj instanceof LinkedList)
      {
         LinkedList list = (LinkedList)obj;
         list.remove(prefix);
         if (list.isEmpty())
         {
            uriByPrefix.remove(prefix);
         }
      }
   }
}
