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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.javaee.annotation.Descriptions;
import org.jboss.javaee.annotation.DisplayNames;
import org.jboss.javaee.annotation.Icons;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;
import org.jboss.xb.annotations.JBossXmlNsPrefix;

/**
 * DescriptionGroupMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
//@JBossXmlGroup(name="descriptionGroup")
@JBossXmlModelGroup(name="descriptionGroup", propOrder={"descriptions", "displayNames", "icons"}, kind=JBossXmlConstants.MODEL_GROUP_ALL)
public class DescriptionGroupMetaData implements Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1337095770028220349L;

   /** The descriptions */
   private Descriptions descriptions;

   /** The display names */
   private DisplayNames displayNames;

   /** The icons */
   private Icons icons;
   
   /**
    * Create a new DescriptionGroupMetaData.
    */
   public DescriptionGroupMetaData()
   {
      // For serialization
   }

   /**
    * Get the descriptions.
    * 
    * @return the descriptions.
    */
   public Descriptions getDescriptions()
   {
      return descriptions;
   }

   /**
    * Set the descriptions.
    * 
    * @param descriptions the descriptions.
    * @throws IllegalArgumentException for a null descriptions
    */
   @XmlElement(name="description", type=DescriptionsImpl.class)
   @JBossXmlNsPrefix(prefix="jee")
   public void setDescriptions(Descriptions descriptions)
   {
      if (descriptions == null)
         throw new IllegalArgumentException("Null descriptions");
      this.descriptions = descriptions;
   }

   /**
    * Get the displayNames.
    * 
    * @return the displayNames.
    */
   public DisplayNames getDisplayNames()
   {
      return displayNames;
   }

   /**
    * Set the displayNames.
    * 
    * @param displayNames the displayNames.
    * @throws IllegalArgumentException for a null displayNames
    */
   @XmlElement(name="display-name", type=DisplayNamesImpl.class)
   @JBossXmlNsPrefix(prefix="jee")
   public void setDisplayNames(DisplayNames displayNames)
   {
      if (displayNames == null)
         throw new IllegalArgumentException("Null displayNames");
      this.displayNames = displayNames;
   }

   /**
    * Get the icons.
    * 
    * @return the icons.
    */
   public Icons getIcons()
   {
      return icons;
   }

   /**
    * Set the icons.
    * 
    * @param icons the icons.
    * @throws IllegalArgumentException for a null icons
    */
   @XmlElement(name="icon", type=IconsImpl.class)
   @JBossXmlNsPrefix(prefix="jee")
   public void setIcons(Icons icons)
   {
      if (icons == null)
         throw new IllegalArgumentException("Null icons");
      this.icons = icons;
   }
}
