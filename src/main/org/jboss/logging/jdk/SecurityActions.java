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
package org.jboss.logging.jdk;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
class SecurityActions
{
   interface Actions
   {
      Actions NON_PRIVILEGED = new Actions()
      {
         public String getProperty(final String name)
         {
            return System.getProperty(name);
         }

         public String getProperty(final String name, final String def)
         {
            return System.getProperty(name, def);
         }
      };

      Actions PRIVILEGED = new Actions()
      {
         public String getProperty(final String name)
         {
            return (String) AccessController.doPrivileged(new PrivilegedAction()
            {
               public Object run()
               {
                  return System.getProperty(name);
               }
            });
         }
         public String getProperty(final String name, final String def)
         {
            return (String)AccessController.doPrivileged(new PrivilegedAction()
            {
               public Object run()
               {
                  return System.getProperty(name, def);
               }
            });
         }
      };

      String getProperty(String name);
      String getProperty(String name, String def);
   }


   static String getProperty(String name)
   {
      String value;
      if( System.getSecurityManager() == null )
         value = Actions.NON_PRIVILEGED.getProperty(name);
      else
         value = Actions.PRIVILEGED.getProperty(name);
      return value;
   }
   static String getProperty(String name, String def)
   {
      String value;
      if( System.getSecurityManager() == null )
         value = Actions.NON_PRIVILEGED.getProperty(name, def);
      else
         value = Actions.PRIVILEGED.getProperty(name, def);
      return value;
   }
}

