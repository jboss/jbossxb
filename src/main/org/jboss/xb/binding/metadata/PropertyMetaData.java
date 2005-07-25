/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class PropertyMetaData
{
   private String name;
   private String collectionType;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getCollectionType()
   {
      return collectionType;
   }

   public void setCollectionType(String collectionType)
   {
      this.collectionType = collectionType;
   }
}
