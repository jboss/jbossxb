/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.Immutable;

import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class CollectionItemBinding
   extends ClassBinding
   implements JavaValueBinding
{
   public CollectionItemBinding(Class newInstanceType)
   {
      super(newInstanceType);
   }

   public Object get(Object owner, String name)
   {
      return null;
   }

   public void set(Object owner, Object value, String name)
   {
      if(value instanceof Immutable)
      {
         value = ((Immutable)value).newInstance();
      }
      ((Collection)owner).add(value);
   }
}
