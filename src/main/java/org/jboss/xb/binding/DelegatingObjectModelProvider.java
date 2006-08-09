/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.xb.binding;

import java.lang.reflect.Method;
import org.jboss.logging.Logger;

/**
 * todo come up with a nicer class name
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingObjectModelProvider
   implements GenericObjectModelProvider
{
   private static final Logger log = Logger.getLogger(DelegatingObjectModelProvider.class);

   private final ObjectModelProvider provider;
   private final boolean trace = log.isTraceEnabled();

   public DelegatingObjectModelProvider(ObjectModelProvider provider)
   {
      this.provider = provider;
   }

   public Object getChildren(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      return provideChildren(provider, o, namespaceURI, localName);
   }

   public Object getElementValue(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      return provideValue(provider, o, namespaceURI, localName);
   }

   public Object getAttributeValue(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      return provideAttributeValue(provider, o, namespaceURI, localName);
   }

   public Object getRoot(Object o, MarshallingContext ctx, String namespaceURI, String localName)
   {
      return provider.getRoot(o, null, namespaceURI, localName);
   }

   // private

   Object provideChildren(ObjectModelProvider provider,
                          Object parent,
                          String namespaceUri,
                          String name)
   {
      Class providerClass = provider.getClass();
      Class parentClass = parent.getClass();
      String methodName = "getChildren";

      Object container = null;
      Method method = getProviderMethod(providerClass,
         methodName,
         new Class[]{parentClass, String.class, String.class}
      );
      if(method != null)
      {
         try
         {
            container = method.invoke(provider, new Object[]{parent, namespaceUri, name});
         }
         catch(Exception e)
         {
            log.error("Failed to invoke method " + methodName, e);
            throw new IllegalStateException("Failed to invoke method " + methodName);
         }
      }
      else if(trace)
      {
         log.trace("No " + methodName + " for " + name);
      }
      return container;
   }

   Object provideValue(ObjectModelProvider provider,
                       Object parent,
                       String namespaceUri,
                       String name)
   {
      Class providerClass = provider.getClass();
      Class parentClass = parent.getClass();
      String methodName = "getElementValue";

      Object value = null;
      Method method = getProviderMethod(providerClass,
         methodName,
         new Class[]{parentClass, String.class, String.class}
      );
      if(method != null)
      {
         try
         {
            value = method.invoke(provider, new Object[]{parent, namespaceUri, name});
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke method " + methodName);
         }
      }
      else if(trace)
      {
         log.trace("No " + methodName + " for " + name);
      }
      return value;
   }

   Object provideAttributeValue(ObjectModelProvider provider,
                                Object object,
                                String namespaceUri,
                                String name)
   {
      Class providerClass = provider.getClass();
      Class objectClass = object.getClass();
      String methodName = "getAttributeValue";

      Object value = null;
      Method method = getProviderMethod(providerClass,
         methodName,
         new Class[]{objectClass, String.class, String.class}
      );
      if(method != null)
      {
         try
         {
            value = method.invoke(provider, new Object[]{object, namespaceUri, name});
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke method " + methodName);
         }
      }
      else if(trace)
      {
         log.trace("No " + methodName + " for " + name);
      }
      return value;
   }

   private static Method getProviderMethod(Class providerClass, String methodName, Class[] args)
   {
      Method method = null;
      try
      {
         method = providerClass.getMethod(methodName, args);
      }
      catch(NoSuchMethodException e)
      {
         // no method
      }
      return method;
   }
}
