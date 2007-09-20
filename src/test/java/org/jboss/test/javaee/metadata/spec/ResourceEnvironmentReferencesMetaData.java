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

import org.jboss.javaee.metadata.support.AbstractMappedMetaData;
import org.jboss.javaee.metadata.support.JavaEEMetaDataUtil;

/**
 * ResourceEnvironmentReferencesMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ResourceEnvironmentReferencesMetaData extends AbstractMappedMetaData<ResourceEnvironmentReferenceMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -1586023682859221620L;

   /**
    * Merge resource environment references
    * 
    * @param override the override references
    * @param overriden the overriden references 
    * @param overridenFile the overriden file name
    * @param overrideFile the override file
    * @return the merged referencees
    */
   public static ResourceEnvironmentReferencesMetaData merge(ResourceEnvironmentReferencesMetaData override, ResourceEnvironmentReferencesMetaData overriden, String overridenFile, String overrideFile)
   {
      if (override == null && overriden == null)
         return null;
      
      if (override == null)
         return overriden;
      
      ResourceEnvironmentReferencesMetaData merged = new ResourceEnvironmentReferencesMetaData();
      return JavaEEMetaDataUtil.merge(merged, overriden, override, "resource-env-ref", overridenFile, overrideFile, true);
   }

   /**
    * Create a new ResourceEnvironmentReferencesMetaData.
    */
   public ResourceEnvironmentReferencesMetaData()
   {
      super("resource env ref name");
   }
}
