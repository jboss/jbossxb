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

import javax.xml.bind.annotation.XmlType;

import org.jboss.javaee.metadata.support.MergeableMappedMetaData;

/**
 * EJBLocalReferenceMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="ejb-local-refType")
public class EJBLocalReferenceMetaData extends AbstractEJBReferenceMetaData implements MergeableMappedMetaData<EJBLocalReferenceMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 5810710557505041609L;
   
   /** The home type */
   private String localHome;
   
   /** The remote type */
   private String local;
   
   /**
    * Create a new EJBLocalReferenceMetaData.
    */
   public EJBLocalReferenceMetaData()
   {
      // For serialization
   }

   /**
    * Get the localHome.
    * 
    * @return the localHome.
    */
   public String getLocalHome()
   {
      return localHome;
   }

   /**
    * Set the localHome.
    * 
    * @param localHome the localHome.
    * @throws IllegalArgumentException for a null localHome
    */
   public void setLocalHome(String localHome)
   {
      if (localHome == null)
         throw new IllegalArgumentException("Null localHome");
      this.localHome = localHome;
   }

   /**
    * Get the local.
    * 
    * @return the local.
    */
   public String getLocal()
   {
      return local;
   }

   /**
    * Set the local.
    * 
    * @param local the local.
    * @throws IllegalArgumentException for a null local
    */
   public void setLocal(String local)
   {
      if (local == null)
         throw new IllegalArgumentException("Null local");
      this.local = local;
   }

   public EJBLocalReferenceMetaData merge(EJBLocalReferenceMetaData original)
   {
      EJBLocalReferenceMetaData merged = new EJBLocalReferenceMetaData();
      merge(merged, original);
      return merged;
   }
   
   /**
    * Merge
    * 
    * @param merged the data to merge into
    * @param original the original data
    */
   public void merge(EJBLocalReferenceMetaData merged, EJBLocalReferenceMetaData original)
   {
      super.merge(merged, original);
      if (localHome != null)
         merged.setLocalHome(localHome);
      else if (original.localHome != null)
         merged.setLocalHome(original.localHome);
      if (local != null)
         merged.setLocal(local);
      else if (original.local != null)
         merged.setLocal(original.local);
   }
}
