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
package org.jboss.xb.binding.introspection;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.Collections;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.util.NoopMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class ClassInfos
{
   private static Map classInfos = Collections.synchronizedMap(new WeakHashMap());
   private static final Object CLASS_INFO_NA = new Object();

   /**
    * Disables caching of ClassInfo's. Already cached ClassInfo's will be lost after
    * the method returns.
    */
   public static synchronized void disableCache()
   {
      classInfos = NoopMap.INSTANCE;
   }

   /**
    * Enables caching of ClassInfo's unless caching is already enabled.
    */
   public static synchronized void enableCache()
   {
      if(!isCacheEnabled())
      {
         classInfos = Collections.synchronizedMap(new WeakHashMap());
      }
   }

   /**
    * @return true if caching is enabled, false otherwise.
    */
   public static synchronized boolean isCacheEnabled()
   {
      return classInfos != NoopMap.INSTANCE;
   }

   /**
    * Flushes all the cached ClassInfo's.
    */
   public static void flushCache()
   {
      classInfos.clear();
   }

   /**
    * Evicts ClassInfo for a specific class.
    * @param cls  fully qualified class name of the class
    */
   public static void flushCache(String cls)
   {
      classInfos.remove(cls);
   }

   public static ClassInfo getClassInfo(Class cls)
   {
      ClassInfo clsInfo = (ClassInfo)classInfos.get(cls.getName());
      if(clsInfo == null)
      {
         clsInfo = new ClassInfo(cls);
         classInfos.put(cls.getName(), clsInfo);
      }
      return clsInfo;
   }

   public static ClassInfo getClassInfo(String name, boolean required)
   {
      Object o = classInfos.get(name);
      if(o == null)
      {
         try
         {
            Class cls = Thread.currentThread().getContextClassLoader().loadClass(name);
            ClassInfo clsInfo = new ClassInfo(cls);
            classInfos.put(name, clsInfo);
            return clsInfo;
         }
         catch(ClassNotFoundException e)
         {
            if(required)
            {
               throw new JBossXBRuntimeException("Failed to load class " + name);
            }

            classInfos.put(name, CLASS_INFO_NA);
         }
      }
      else if(o != CLASS_INFO_NA)
      {
         return (ClassInfo)o;
      }

      if(required)
      {
         throw new JBossXBRuntimeException("Failed to load class " + name);
      }

      return  null;
   }
}
