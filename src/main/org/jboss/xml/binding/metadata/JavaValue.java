/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;


import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class JavaValue
   implements JavaValueBinding, Cloneable
{
   private final List interceptors = new ArrayList();

   public JavaValue push(JavaValueBinding interceptor)
   {
      interceptors.add(interceptor);
      return this;
   }

   public boolean isBound()
   {
      return interceptors.size() > 0;
   }

   public Object clone()
   {
      JavaValue clone = new JavaValue();
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   public Object newInstance()
   {
      if(interceptors.isEmpty())
      {
         noInterceptorsException();
      }

      JavaValueBinding interceptor = (JavaValueBinding)interceptors.get(0);
      return interceptor.newInstance();
   }

   public Object get(Object owner, String name)
   {
      if(interceptors.isEmpty())
      {
         noInterceptorsException();
      }

      Object levelOwner = owner;
      for(int i = interceptors.size() - 1; i >= 0; --i)
      {
         JavaValueBinding interceptor = (JavaValueBinding)interceptors.get(i);
         levelOwner = interceptor.get(levelOwner, name);
         if(levelOwner == null)
         {
            break;
         }
      }
      return levelOwner;
      /*
      JavaValueBindingInterceptor interceptor = (JavaValueBindingInterceptor)interceptors.get(interceptors.size() - 1);
      return interceptor.get(owner);
      */
   }

   public void set(Object owner, Object value, String name)
   {
      if(interceptors.isEmpty())
      {
         noInterceptorsException();
      }

      setValue(owner, value, name, interceptors.size() - 1);
   }

   // Private

   private void setValue(Object owner, Object value, String name, int i)
   {
      JavaValueBinding interceptor = (JavaValueBinding)interceptors.get(i);
      if(i > 0)
      {
         Object lowerOwner = interceptor.get(owner, name);
         boolean notSet = lowerOwner == null;
         if(notSet)
         {
            lowerOwner = interceptor.newInstance();
         }

         setValue(lowerOwner, value, name, i - 1);

         if(notSet)
         {
            interceptor.set(owner, lowerOwner, name);
         }
      }
      else
      {
         interceptor.set(owner, value, name);
      }
   }

   private void noInterceptorsException()
   {
      throw new IllegalStateException("JavaValue is not bound.");
   }
}
