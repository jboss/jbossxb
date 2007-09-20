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
package org.jboss.ejb.metadata.spec;

import org.jboss.javaee.metadata.spec.EmptyMetaData;
import org.jboss.javaee.metadata.spec.RunAsMetaData;
import org.jboss.javaee.metadata.support.IdMetaDataImplWithDescriptions;
import org.jboss.javaee.metadata.support.MergeableMetaData;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * SecurityIdentityMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="security-identityType")
public class SecurityIdentityMetaData extends IdMetaDataImplWithDescriptions implements MergeableMetaData<SecurityIdentityMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -6336033602938028216L;
   
   /** The use caller identity */
   private EmptyMetaData useCallerIdentity;
   
   /** The run as */
   private RunAsMetaData runAs;

   /** The run as principal */
   private String runAsPrincipal;

   /**
    * Create a new SecurityIdentityMetaData
    */
   public SecurityIdentityMetaData()
   {
      // For serialization
   }

   /**
    * Whether to use caller identity
    * 
    * @return true for caller id
    */
   public boolean isUseCallerId()
   {
      return useCallerIdentity != null;
   }
   
   /**
    * Get the useCallerIdentity.
    * 
    * @return the useCallerIdentity.
    */
   public EmptyMetaData getUseCallerIdentity()
   {
      return useCallerIdentity;
   }

   /**
    * Set the useCallerIdentity.
    * 
    * @param useCallerIdentity the useCallerIdentity.
    */
   @XmlElement(required=false)
   public void setUseCallerIdentity(EmptyMetaData useCallerIdentity)
   {
      this.useCallerIdentity = useCallerIdentity;
   }

   /**
    * Get the runAs.
    * 
    * @return the runAs.
    */
   public RunAsMetaData getRunAs()
   {
      return runAs;
   }

   /**
    * Set the runAs.
    * 
    * @param runAs the runAs.
    * @throws IllegalArgumentException for a null runAs
    */
   @XmlElement(required=false)
   public void setRunAs(RunAsMetaData runAs)
   {
      if (runAs == null)
         throw new IllegalArgumentException("Null runAs");
      this.runAs = runAs;
   }

   /**
    * Get the runAsPrincipal.
    * 
    * @return the runAsPrincipal.
    */
   public String getRunAsPrincipal()
   {
      return runAsPrincipal;
   }

   /**
    * Set the runAsPrincipal.
    * 
    * @param runAsPrincipal the runAsPrincipal.
    * @throws IllegalArgumentException for a null runAsPrincipal
    */
   @XmlElement(required=false)
   public void setRunAsPrincipal(String runAsPrincipal)
   {
      if (runAsPrincipal == null)
         throw new IllegalArgumentException("Null runAsPrincipal");
      this.runAsPrincipal = runAsPrincipal;
   }

   public SecurityIdentityMetaData merge(SecurityIdentityMetaData original)
   {
      SecurityIdentityMetaData merged = new SecurityIdentityMetaData();
      merge(merged, original);
      return merged;
   }

   /**
    * Merge
    * 
    * @param merged the data to merge into
    * @param original the original data
    */
   public void merge(SecurityIdentityMetaData merged, SecurityIdentityMetaData original)
   {
      super.merge(merged, original);
      merged.setUseCallerIdentity(original.getUseCallerIdentity());
      merged.setRunAs(original.getRunAs());
      merged.setRunAsPrincipal(getRunAsPrincipal());
   }
}
