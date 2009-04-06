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

import java.util.Set;

/**
 * Metadata for an annotation.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 38009 $
 */
public class AbstractAnnotationMetaData implements AnnotationMetaData
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   /** The annotation class name */
   protected String name;
   
   /** The attributes Set<AnnotationAttributeMetaData> */
   protected Set<?> attributes;
   
   // Static --------------------------------------------------------
   
   // Constructors --------------------------------------------------

   /**
    * Create a new annotation meta data
    */
   public AbstractAnnotationMetaData()
   {
      super();
   }
   
   // Public --------------------------------------------------------

   /**
    * Set the name.
    * 
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * Set the attributes.
    * 
    * @param attributes Set<AnnotationAttributeMetaData>
    */
   public void setAttributes(Set<?> attributes)
   {
      this.attributes = attributes;
   }
   
   // AnnotationMetaData implementation -----------------------------

   public String getName()
   {
      return name;
   }

   public Set<?> getAttributes()
   {
      return attributes;
   }

   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------
}
