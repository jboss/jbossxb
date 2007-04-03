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
package org.jboss.test.xml.pojoserver.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Collection metadata.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class AbstractCollectionMetaData extends AbstractTypeMetaData implements Collection
{
   /** The collection */
   protected ArrayList collection = new ArrayList();
   
   /** The element type */
   protected String elementType;

   /**
    * Create a new collection value
    */
   public AbstractCollectionMetaData()
   {
   }

   /**
    * Get the element type
    * 
    * @return the element type
    */
   public String getElementType()
   {
      return elementType;
   }

   /**
    * Set the element type
    * 
    * @param elementType the element type
    */
   public void setElementType(String elementType)
   {
      this.elementType = elementType;
   }

   public boolean add(Object o)
   {
      return collection.add(o);
   }

   public boolean addAll(Collection c)
   {
      return collection.addAll(c);
   }

   public void clear()
   {
      collection.clear();
   }

   public boolean contains(Object o)
   {
      return collection.contains(o);
   }

   public boolean containsAll(Collection c)
   {
      return collection.containsAll(c);
   }

   public boolean isEmpty()
   {
      return collection.isEmpty();
   }

   public Iterator iterator()
   {
      return collection.iterator();
   }

   public boolean remove(Object o)
   {
      return collection.remove(o);
   }

   public boolean removeAll(Collection c)
   {
      return collection.removeAll(c);
   }

   public boolean retainAll(Collection c)
   {
      return collection.retainAll(c);
   }

   public int size()
   {
      return collection.size();
   }

   public Object[] toArray()
   {
      return collection.toArray();
   }

   public Object[] toArray(Object[] a)
   {
      return collection.toArray(a);
   }
}