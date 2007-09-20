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
package org.jboss.javaee.metadata.jboss;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.javaee.metadata.spec.ResourceInjectionTargetMetaData;
import org.jboss.javaee.metadata.support.IdMetaDataImplWithDescriptions;
import org.jboss.javaee.metadata.support.NonNullLinkedHashSet;

/**
 * IgnoreDependencyMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="ignore-dependencyType")
public class IgnoreDependencyMetaData extends IdMetaDataImplWithDescriptions
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 68493962316154817L;

   /** The injection targets */
   private Set<ResourceInjectionTargetMetaData> injectionTargets;

   /**
    * Get the injectionTargets.
    * 
    * @return the injectionTargets.
    */
   public Set<ResourceInjectionTargetMetaData> getInjectionTargets()
   {
      return injectionTargets;
   }

   /**
    * Set the injectionTargets.
    * 
    * @param injectionTargets the injectionTargets.
    * @throws IllegalArgumentException for a null injectionTargets
    */
   @XmlElement(name="injection-target", type=NonNullLinkedHashSet.class)
   public void setInjectionTargets(Set<ResourceInjectionTargetMetaData> injectionTargets)
   {
      if (injectionTargets == null)
         throw new IllegalArgumentException("Null injectionTargets");
      this.injectionTargets = injectionTargets;
   }
}
