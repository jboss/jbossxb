/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import EDU.oswego.cs.dl.util.concurrent.CopyOnWriteArrayList;
import EDU.oswego.cs.dl.util.concurrent.CopyOnWriteArraySet;

// TODO Review

/**
 * Collections factory.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class CollectionsFactory
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------
   
   // Static --------------------------------------------------------

   /**
    * Defines the map implementation
    */
   public static final Map createMap()
   {
      return new ConcurrentReaderHashMap();
   }

   /**
    * Defines the list implementation
    */
   public static final List createList()
   {
      return new CopyOnWriteArrayList();
   }

   /**
    * Defines the list implementation
    */
   public static final List createCopyOnWriteList()
   {
      return new CopyOnWriteArrayList();
   }

   /**
    * Defines the set implementation
    */
   public static final Set createSet()
   {
      return new CopyOnWriteArraySet();
   }
   
   // Constructors --------------------------------------------------
   
   // Public --------------------------------------------------------
   
   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
