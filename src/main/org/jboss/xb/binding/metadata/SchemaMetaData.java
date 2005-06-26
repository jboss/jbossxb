/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SchemaMetaData
{
   private PackageMetaData packageMetaData;
   private Map values = Collections.EMPTY_MAP;


   public PackageMetaData getPackage()
   {
      return packageMetaData;
   }

   public void setPackage(PackageMetaData pkg)
   {
      this.packageMetaData = pkg;
   }

   public ValueMetaData getValue(String id)
   {
      return (ValueMetaData)values.get(id);
   }

   public void addValue(ValueMetaData value)
   {
      if(value.getId() == null)
      {
         throw new IllegalArgumentException("ValueMetaData must have a non-null id.");
      }

      switch(values.size())
      {
         case 0:
            values = Collections.singletonMap(value.getId(), value);
            break;
         case 1:
            values = new HashMap(values);
         default:
            values.put(value.getId(), value);
      }
   }
}
