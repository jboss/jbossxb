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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.javaee.metadata.support.MergeableMappedMetaData;
import org.jboss.javaee.metadata.support.NamedMetaDataWithDescriptionGroup;

/**
 * MessageDestinationMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="message-destinationType")
public class MessageDestinationMetaData extends NamedMetaDataWithDescriptionGroup implements MergeableMappedMetaData<MessageDestinationMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 2129990191983873784L;

   /** The mapped name */
   private String mappedName;

   /**
    * Create a new MessageDestinationMetaData.
    */
   public MessageDestinationMetaData()
   {
      // For serialization
   }

   /**
    * Get the messageDestinationName.
    * 
    * @return the messageDestinationName.
    */
   public String getMessageDestinationName()
   {
      return getName();
   }

   /**
    * Set the messageDestinationName.
    * 
    * @param messageDestinationName the messageDestinationName.
    * @throws IllegalArgumentException for a null messageDestinationName
    */
   public void setMessageDestinationName(String messageDestinationName)
   {
      setName(messageDestinationName);
   }

   /**
    * Get the mappedName.
    * 
    * @return the mappedName.
    */
   public String getMappedName()
   {
      return mappedName;
   }

   /**
    * Set the mappedName.
    * 
    * @param mappedName the mappedName.
    * @throws IllegalArgumentException for a null mappedName
    */
   //@SchemaProperty(mandatory=false)
   @XmlElement(required=false)
   public void setMappedName(String mappedName)
   {
      if (mappedName == null)
         throw new IllegalArgumentException("Null mappedName");
      this.mappedName = mappedName;
   }

   /**
    * Get the jndiName.
    * 
    * @return the jndiName.
    */
   public String getJndiName()
   {
      return getMappedName();
   }

   /**
    * Set the jndiName.
    * 
    * @param jndiName the jndiName.
    * @throws IllegalArgumentException for a null jndiName
    */
   //@SchemaProperty(mandatory=false)
   @XmlElement(required=false)
   public void setJndiName(String jndiName)
   {
      setMappedName(jndiName);
   }

   public MessageDestinationMetaData merge(MessageDestinationMetaData original)
   {
      MessageDestinationMetaData merged = new MessageDestinationMetaData();
      merge(merged, original);
      return merged;
   }

   /**
    * Merge
    * 
    * @param merged the data to merge into
    * @param original the original data
    */
   public void merge(MessageDestinationMetaData merged, MessageDestinationMetaData original)
   {
      super.merge(merged, original);
      if (mappedName != null)
         merged.setMappedName(mappedName);
      else if (original.mappedName != null)
         merged.setMappedName(original.mappedName);
   }
}
