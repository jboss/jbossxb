/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MapValueBinding
   extends ClassBinding
   implements JavaValueBinding
{
   public MapValueBinding(Class cls)
   {
      super(cls);
   }

   public Object get(Object owner, String name)
   {
      return ((MapEntryBinding.KeyValuePair)owner).value;
   }

   public void set(Object owner, Object value, String name)
   {
      ((MapEntryBinding.KeyValuePair)owner).value = value;
   }
}
