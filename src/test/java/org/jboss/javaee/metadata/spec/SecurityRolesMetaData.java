/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.javaee.metadata.spec;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.javaee.metadata.support.AbstractMappedMetaData;

/**
 * SecurityRolesMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class SecurityRolesMetaData extends AbstractMappedMetaData<SecurityRoleMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 4551308976124434096L;

   /**
    * Create a new SecurityRolesMetaData.
    */
   public SecurityRolesMetaData()
   {
      super("role name for security role");
   }

   /**
    * Get the security roles by principal
    * 
    * @param userName the principal name
    * @return the security roles containing the principal or null for no roles
    * @throws IllegalArgumentException for a null user name
    */
   public SecurityRolesMetaData getSecurityRolesByPrincipal(String userName)
   {
      if (userName == null)
         throw new IllegalArgumentException("Null userName");
      if (isEmpty())
         return null;
      SecurityRolesMetaData result = new SecurityRolesMetaData();
      for (SecurityRoleMetaData role : this)
      {
         if (role.hasPrincipal(userName))
            result.add(role);
      }
      return result;
   }

   /**
    * Get the security role names by principal
    * 
    * @param userName the principal name
    * @return the security role names containing the principal
    * @throws IllegalArgumentException for a null user name
    */
   public Set<String> getSecurityRoleNamesByPrincipal(String userName)
   {
      if (userName == null)
         throw new IllegalArgumentException("Null userName");
      if (isEmpty())
         return Collections.emptySet();
      Set<String> result = new LinkedHashSet<String>();
      for (SecurityRoleMetaData role : this)
      {
         if (role.hasPrincipal(userName))
            result.add(role.getRoleName());
      }
      return result;
   }
}
