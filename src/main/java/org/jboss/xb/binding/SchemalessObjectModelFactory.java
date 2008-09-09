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


import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.xml.sax.Attributes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * Sandbox. Very testcase specific impl.
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SchemalessObjectModelFactory
   implements GenericObjectModelFactory
{
   private static final Logger log = Logger.getLogger(SchemalessObjectModelFactory.class);

   public Object newChild(Object parent,
                          UnmarshallingContext navigator,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      Object child = null;
      try
      {
         if(parent instanceof Collection)
         {
            if(!localName.equals(java.lang.String.class.getName()))
            {
               Class<?> itemClass = Thread.currentThread().getContextClassLoader().loadClass(localName);
               child = itemClass.newInstance();
               ((Collection)parent).add(child);
            }
         }
         else
         {
            Method getter = parent.getClass().getMethod("get" + localName, null);
            if(!SchemalessMarshaller.isAttributeType(getter.getReturnType()))
            {
               if(List.class.isAssignableFrom(getter.getReturnType()))
               {
                  child = new ArrayList<Object>();
               }
               else if(Set.class.isAssignableFrom(getter.getReturnType()))
               {
                  child = new HashSet<Object>();
               }
               else if(Collection.class.isAssignableFrom(getter.getReturnType()))
               {
                  child = new ArrayList<Object>();
               }
               else
               {
                  child = getter.getReturnType().newInstance();
               }
            }

            if(child != null)
            {
               Method setter = Classes.getAttributeSetter(parent.getClass(), localName, getter.getReturnType());
               setter.invoke(parent, new Object[]{child});
            }
         }
      }
      catch(NoSuchMethodException e)
      {
         log.error("Failed to get getter/setter method for " + localName + " from " + parent.getClass(), e);
         throw new IllegalStateException("Failed to get getter/setter method for " +
            localName +
            " from " +
            parent.getClass() +
            ": " +
            e.getMessage()
         );
      }
      catch(Exception e)
      {
         log.error("Failed to instantiate child", e);
         throw new IllegalStateException("Failed to instantiate child: " + e.getMessage());
      }
      return child;
   }

   public void addChild(Object parent,
                        Object child,
                        UnmarshallingContext navigator,
                        String namespaceURI,
                        String localName)
   {
   }

   public void setValue(Object o, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      try
      {
         if(o instanceof Collection)
         {
            if(localName.equals(java.lang.String.class.getName()))
            {
               ((Collection<String>)o).add(value);
            }
         }
         else
         {
            Method getter = Classes.getAttributeGetter(o.getClass(), localName);
            Method setter = Classes.getAttributeSetter(o.getClass(), localName, getter.getReturnType());

            Object fieldValue;
            if(java.util.Date.class.isAssignableFrom(getter.getReturnType()))
            {
               SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
               fieldValue = formatter.parse(value);
            }
            else
            {
               fieldValue = value;
            }

            setter.invoke(o, new Object[]{fieldValue});
         }
      }
      catch(NoSuchMethodException e)
      {
         throw new IllegalStateException("Failed to discover getter/setter for " + localName + " in " + o);
      }
      catch(Exception e)
      {
         throw new IllegalStateException("Failed to set value for " + localName + " in " + o);
      }
   }

   public Object newRoot(Object root,
                         UnmarshallingContext navigator,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      Class<?> rootClass;
      try
      {
         rootClass = Thread.currentThread().getContextClassLoader().loadClass(localName);
      }
      catch(ClassNotFoundException e)
      {
         log.error("Faile to load root class " + localName, e);
         throw new IllegalStateException("Failed to load root class: " + localName + ": " + e.getMessage());
      }

      try
      {
         root = rootClass.newInstance();
      }
      catch(Exception e)
      {
         log.error("Failed to create an instance of root " + localName, e);
         throw new IllegalStateException("Failed to create an instance of root " + localName + ": " + e.getMessage());
      }

      return root;
   }

   public Object completeRoot(Object root, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      return root;
   }
}
