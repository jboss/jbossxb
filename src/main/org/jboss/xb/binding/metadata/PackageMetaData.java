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
public class PackageMetaData
{
   private String name;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof PackageMetaData))
      {
         return false;
      }

      final PackageMetaData packageMetaData = (PackageMetaData)o;

      if(name != null ? !name.equals(packageMetaData.name) : packageMetaData.name != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      return (name != null ? name.hashCode() : 0);
   }
}
