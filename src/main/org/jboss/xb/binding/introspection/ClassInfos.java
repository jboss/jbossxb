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
import org.jboss.xb.binding.JBossXBRuntimeException;
import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class ClassInfos
{
   private static Map classInfos = new ConcurrentHashMap();
   private static final Object CLASS_INFO_NA = new Object();

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
