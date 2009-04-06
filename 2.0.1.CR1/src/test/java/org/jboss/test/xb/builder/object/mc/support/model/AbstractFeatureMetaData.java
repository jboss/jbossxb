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
package org.jboss.test.xb.builder.object.mc.support.model;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;

/**
 * General metadata.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 59429 $
 */
public abstract class AbstractFeatureMetaData extends JBossObject
   implements FeatureMetaData, Serializable
{
   private static final long serialVersionUID = 1;

   /** The description */
   protected String description;

   /** The annotations */
   protected Set<AnnotationMetaData> annotations;

   /**
    * Create a new meta data
    */
   public AbstractFeatureMetaData()
   {
   }
   
   /**
    * Set the description.
    * 
    * @param description the description.
    */
   public void setDescription(String description)
   {
      this.description = description;
      flushJBossObjectCache();
   }

   /**
    * Set the annotations.
    * 
    * @param annotations Set<AnnotationMetaData>
    */
   @XmlElement(name="annotation", type=AbstractAnnotationMetaData.class)
   public void setAnnotations(Set<AnnotationMetaData> annotations)
   {
      this.annotations = annotations;
      flushJBossObjectCache();
   }
   
   public String getDescription()
   {
      return description;
   }
   
   public Set<AnnotationMetaData> getAnnotations()
   {
      return annotations;
   }
   
   public void toString(JBossStringBuilder buffer)
   {
      if (description != null)
         buffer.append("description=").append(description);
      if (annotations != null)
         buffer.append(" annotations=").append(annotations);
   }
   
   public void toShortString(JBossStringBuilder buffer)
   {
      buffer.append(description);
   }
}
