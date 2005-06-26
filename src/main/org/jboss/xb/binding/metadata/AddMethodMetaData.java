/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AddMethodMetaData
{
   private String methodName;
   private String valueType;
   private boolean childType;

   public String getMethodName()
   {
      return methodName;
   }

   public void setMethodName(String methodName)
   {
      this.methodName = methodName;
   }

   public String getValueType()
   {
      return valueType;
   }

   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }

   public boolean isChildType()
   {
      return childType;
   }

   public void setChildType(boolean childType)
   {
      this.childType = childType;
   }
}
