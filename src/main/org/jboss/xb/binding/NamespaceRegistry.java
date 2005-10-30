/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.xb.binding;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * A simple namespace registry.
 *
 * It assignes namespace prefixes of the form 'ns?' where ? is an incrementing integer.
 * {@see registerURI(String,String)}
 *
 * [TODO] cleanup the api
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @author <a href="mailto:anil.saldhana@jboss.org">Anil Saldhana</a>
 * @since 08-June-2004
 */
public class NamespaceRegistry implements NamespaceContext
{
   // The index of the last assigned prefix
   private int namespaceIndex;

   private final Map prefixByUri = new HashMap();
   private final Map uriByPrefix = new HashMap();

   public NamespaceRegistry()
   {
   }

   /** Register a QName and return a QName that is guarantied to have a prefix
    */
   public QName registerQName(QName qname)
   {
      if (qname == null)
         return null;

      String nsURI = qname.getNamespaceURI();
      String prefix = getPrefix(nsURI);
      if (prefix == null)
      {
         prefix = qname.getPrefix();
         if (prefix.length() == 0)
            prefix = registerURI(nsURI, null);
         else
            prefix = registerURI(nsURI, prefix);
      }

      qname = new QName(nsURI, qname.getLocalPart(), prefix);
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
    * @param nsURI  the URI to prefix to
    */
   public void addPrefixMapping(String prefix, String nsURI)
   {
      if (nsURI == null)
         throw new IllegalArgumentException("Cannot add mapping for null namespace URI");

      Object obj = uriByPrefix.get(prefix);
      if (nsURI.equals(obj) == false)
      {
         if (obj == null)
         {
            uriByPrefix.put(prefix, nsURI);
         }
         else if (obj instanceof String)
         {
            LinkedList list = new LinkedList();
            list.add(obj);
            list.add(nsURI);
            uriByPrefix.put(prefix, list);
         }
         else if (obj instanceof LinkedList)
         {
            ((LinkedList)obj).add(nsURI);
         }
         else
         {
            throwUnexpectedEntryException(obj);
         }

         obj = prefixByUri.get(nsURI);
         if (obj == null)
         {
            prefixByUri.put(nsURI, prefix);
         }
         else if (obj instanceof String)
         {
            LinkedList list = new LinkedList();
            list.add(obj);
            list.add(prefix);
            prefixByUri.put(nsURI, list);
         }
         else if (obj instanceof LinkedList)
         {
            ((LinkedList)obj).add(prefix);
         }
         else
         {
            throwUnexpectedEntryException(obj);
         }
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
         else
         {
            throwUnexpectedEntryException(obj);
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
            else
            {
               throwUnexpectedEntryException(obj);
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
         else
         {
            throwUnexpectedEntryException(obj);
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

   /** Return an iterator over all registered nsURIs.
    */
   public Iterator getRegisteredPrefix()
   {
      return uriByPrefix.keySet().iterator();
   }

   // NamespaceContext implementation

   /**
    * Returns the last mapping for the prefix or null if the prefix was not mapped.
    */
   public String getPrefix(String nsURI)
   {
      Object obj = prefixByUri.get(nsURI);

      String prefix = null;
      if (obj != null)
      {
         if (obj instanceof String)
         {
            prefix = (String)obj;
         }
         else if (obj instanceof LinkedList)
         {
            prefix = (String)((LinkedList)obj).getLast();
         }
         else
         {
            throwUnexpectedEntryException(obj);
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
      Object obj = prefixByUri.get(namespaceURI);

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
      else
      {
         throwUnexpectedEntryException(obj);
      }

      return result;
   }

   /** Get the nsURI for a given prefix, maybe null.
    */
   public String getNamespaceURI(String prefix)
   {
      Object obj = uriByPrefix.get(prefix);

      String uri = null;
      if (obj != null)
      {
         if (obj instanceof String)
         {
            uri = (String)obj;
         }
         else if (obj instanceof LinkedList)
         {
            uri = (String)((LinkedList)obj).getLast();
         }
         else
         {
            throwUnexpectedEntryException(obj);
         }
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

   private void throwUnexpectedEntryException(Object entry)
   {
      throw new IllegalStateException("Unexpected entry type: expected java.lang.String or java.util.LinkedList but got " + entry.getClass());
   }
}
