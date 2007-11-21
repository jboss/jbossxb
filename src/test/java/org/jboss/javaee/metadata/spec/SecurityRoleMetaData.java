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

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.javaee.metadata.support.MergeableMappedMetaData;
import org.jboss.javaee.metadata.support.NamedMetaDataWithDescriptions;
import org.jboss.javaee.metadata.support.NonNullLinkedHashSet;

/**
 * SecurityRoleMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="security-roleType")
public class SecurityRoleMetaData extends NamedMetaDataWithDescriptions implements MergeableMappedMetaData<SecurityRoleMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -4349954695900237831L;
   
   /** The prinicpals */
   private Set<String> principals;
   
   /**
    * Create a new SecurityRoleMetaData.
    */
   public SecurityRoleMetaData()
   {
      // For serialization
   }
   
   /**
    * Get the roleName.
    * 
    * @return the roleName.
    */
   public String getRoleName()
   {
      return getName();
   }

   /**
    * Set the roleName.
    * 
    * @param roleName the roleName.
    * @throws IllegalArgumentException for a null roleName
    */
   public void setRoleName(String roleName)
   {
      setName(roleName);
   }

   /**
    * Get the principals.
    * 
    * @return the principals.
    */
   public Set<String> getPrincipals()
   {
      return principals;
   }

   /**
    * Set the principals.
    * 
    * @param principals the principals.
    * @throws IllegalArgumentException for a null principals
    */
   //@SchemaProperty(name="principal", mandatory=false, impl=NonNullLinkedHashSet.class)
   @XmlElement(name="principal", required=false/*, type=NonNullLinkedHashSet.class*/)
   public void setPrincipals(Set<String> principals)
   {
      if (principals == null)
         throw new IllegalArgumentException("Null principals");
      this.principals = principals;
   }
   
   /**
    * Whether this roles has the principal
    * 
    * @param userName the principal
    * @return true when it has the principal
    * @throws IllegalArgumentException for a null principal
    */
   public boolean hasPrincipal(String userName)
   {
      if (userName == null)
         throw new IllegalArgumentException("Null userName");
      if (principals == null)
         return false;
      return principals.contains(userName);
   }

   public SecurityRoleMetaData merge(SecurityRoleMetaData original)
   {
      // There is nothing to merge
      return this;
   }
}
