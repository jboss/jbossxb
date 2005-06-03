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
public class MapEntryMetaData
{
   private String getKeyMethod;
   private String setKeyMethod;
   private String getValueMethod;
   private String setValueMethod;

   public String getGetKeyMethod()
   {
      return getKeyMethod;
   }

   public void setGetKeyMethod(String getKeyMethod)
   {
      this.getKeyMethod = getKeyMethod;
   }

   public String getSetKeyMethod()
   {
      return setKeyMethod;
   }

   public void setSetKeyMethod(String setKeyMethod)
   {
      this.setKeyMethod = setKeyMethod;
   }

   public String getGetValueMethod()
   {
      return getValueMethod;
   }

   public void setGetValueMethod(String getValueMethod)
   {
      this.getValueMethod = getValueMethod;
   }

   public String getSetValueMethod()
   {
      return setValueMethod;
   }

   public void setSetValueMethod(String setValueMethod)
   {
      this.setValueMethod = setValueMethod;
   }
}
