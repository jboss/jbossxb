/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.Immutable;

import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ClassBinding
   implements JavaValueBinding
{
   protected final Class cls;
   private final Constructor ctor;

   public ClassBinding(Class cls)
   {
      this.cls = cls;
      Constructor ctor = null;
      if(cls != null)
      {
         try
         {
            ctor = cls.getConstructor(null);
         }
         catch(NoSuchMethodException e)
         {
         }
      }

      this.ctor = ctor;
   }

   public Object newInstance()
   {
      Object instance;
      if(cls == null)
      {
         instance = null;
      }
      else
      {
         try
         {
            instance = ctor == null ? new Immutable(cls) : ctor.newInstance(null);
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to create an instance of " + cls + " using no-arg constructor.");
         }
      }
      return instance;
   }

   public Object get(Object owner, String name)
   {
      throw new UnsupportedOperationException("get is not supported for root binding.");
   }

   public void set(Object owner, Object value, String name)
   {
      throw new UnsupportedOperationException("set is not supported for root binding.");
   }
}
