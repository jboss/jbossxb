/**
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

// $Id$

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.Collections;

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
      if(prefix == null)
      {
         prefix = "ns" + (++namespaceIndex);
      }

      addPrefixMapping(nsURI, prefix);

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
      Object o = uriByPrefix.get(prefix);
      if(o == null)
      {
         uriByPrefix.put(prefix, uri);
      }
      else if(o instanceof String)
      {
         LinkedList list = new LinkedList();
         list.add(o);
         list.add(uri);
         uriByPrefix.put(prefix, list);
      }
      else if(o instanceof LinkedList)
      {
         ((LinkedList)o).add(uri);
      }
      else
      {
         throw new IllegalStateException(
            "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
         );
      }

      o = prefixByUri.get(uri);
      if(o == null)
      {
         prefixByUri.put(uri, prefix);
      }
      else if(o instanceof String)
      {
         LinkedList list = new LinkedList();
         list.add(o);
         list.add(prefix);
         prefixByUri.put(uri, list);
      }
      else if(o instanceof LinkedList)
      {
         ((LinkedList)o).add(prefix);
      }
      else
      {
         throw new IllegalStateException(
            "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
         );
      }
   }

   /**
    * Removes the last mapping for the given prefix.
    *
    * @param prefix  the prefix to remove mapping for
    */
   public void removePrefixMapping(String prefix)
   {
      Object o = uriByPrefix.get(prefix);
      if(o != null)
      {
         String uri = null;
         if(o instanceof String)
         {
            uri = (String)o;
            uriByPrefix.remove(prefix);
         }
         else if(o instanceof LinkedList)
         {
            LinkedList list = (LinkedList)o;
            uri = (String)list.removeLast();
            if(list.isEmpty())
            {
               uriByPrefix.remove(prefix);
            }
         }
         else
         {
            throw new IllegalStateException(
               "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
            );
         }

         if(uri != null)
         {
            o = prefixByUri.get(uri);
            if(o instanceof String)
            {
               if(!prefix.equals(o))
               {
                  throw new IllegalStateException("Inconsistent mapping: prefix=" + prefix + ", found=" + o);
               }
               prefixByUri.remove(uri);
            }
            else if(o instanceof LinkedList)
            {
               LinkedList list = (LinkedList)o;
               list.remove(prefix);
               if(list.isEmpty())
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
      Object o = prefixByUri.get(nsURI);
      if(o != null)
      {
         String prefix = null;
         if(o instanceof String)
         {
            prefix = (String)o;
            prefixByUri.remove(nsURI);
            removePrefixMappingOnly(prefix, nsURI);
         }
         else if(o instanceof LinkedList)
         {
            LinkedList list = (LinkedList)o;
            for(int i = 0; i < list.size(); ++i)
            {
               removePrefixMappingOnly((String)list.get(i), nsURI);
            }
            prefixByUri.remove(nsURI);
         }
         else
         {
            throw new IllegalStateException(
               "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
            );
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
      String prefix = null;
      Object o = prefixByUri.get(nsURI);
      if(o != null)
      {
         if(o instanceof String)
         {
            prefix = (String)o;
         }
         else if(o instanceof LinkedList)
         {
            prefix = (String)((LinkedList)o).getLast();
         }
         else
         {
            throw new IllegalStateException(
               "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
            );
         }
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
      Iterator result;
      Object o = prefixByUri.get(namespaceURI);
      if(o == null)
      {
         result = Collections.EMPTY_LIST.iterator();
      }
      else if(o instanceof String)
      {
         result = Collections.singletonList(o).iterator();
      }
      else if(o instanceof LinkedList)
      {
         result = ((LinkedList)o).iterator();
      }
      else
      {
         throw new IllegalStateException(
            "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
         );
      }
      return result;
   }

   /** Get the nsURI for a given prefix, maybe null.
    */
   public String getNamespaceURI(String prefix)
   {
      String uri = null;
      Object o = uriByPrefix.get(prefix);
      if(o != null)
      {
         if(o instanceof String)
         {
            uri = (String)o;
         }
         else if(o instanceof LinkedList)
         {
            uri = (String)((LinkedList)o).getLast();
         }
         else
         {
            throw new IllegalStateException(
               "Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + o.getClass()
            );
         }
      }
      return uri;
   }

   // Private

   private void removePrefixMappingOnly(String prefix, String nsURI)
   {
      Object o;
      o = uriByPrefix.get(prefix);
      if(o instanceof String)
      {
         if(!prefix.equals(o))
         {
            throw new IllegalStateException("Inconsistent mapping: uri=" + nsURI + ", found=" + o);
         }
         uriByPrefix.remove(nsURI);
      }
      else if(o instanceof LinkedList)
      {
         LinkedList list = (LinkedList)o;
         list.remove(prefix);
         if(list.isEmpty())
         {
            uriByPrefix.remove(prefix);
         }
      }
   }
}
