/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ClassMetaData
{
   private String impl;

   public String getImpl()
   {
      return impl;
   }

   public void setImpl(String impl)
   {
      this.impl = impl;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof ClassMetaData))
      {
         return false;
      }

      final ClassMetaData classMetaData = (ClassMetaData)o;

      if(impl != null ? !impl.equals(classMetaData.impl) : classMetaData.impl != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      return (impl != null ? impl.hashCode() : 0);
   }
}
