/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
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

