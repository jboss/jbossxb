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

import org.jboss.javaee.metadata.support.IdMetaDataImpl;
import javax.xml.bind.annotation.XmlType;

/**
 * RemoveMethodMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="remove-methodType")
public class RemoveMethodMetaData extends IdMetaDataImpl
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1830841714074832930L;
   
   /** The bean method */
   private NamedMethodMetaData beanMethod;
   
   /** Retain if exception */
   private boolean retainIfException = false;
   
   /**
    * Create a new RemoveMethodMetaData.
    */
   public RemoveMethodMetaData()
   {
      // For serialization
   }

   /**
    * Get the beanMethod.
    * 
    * @return the beanMethod.
    */
   public NamedMethodMetaData getBeanMethod()
   {
      return beanMethod;
   }

   /**
    * Set the beanMethod.
    * 
    * @param beanMethod the beanMethod.
    * @throws IllegalArgumentException for a null beanMethod
    */
   public void setBeanMethod(NamedMethodMetaData beanMethod)
   {
      if (beanMethod == null)
         throw new IllegalArgumentException("Null beanMethod");
      this.beanMethod = beanMethod;
   }

   /**
    * Get the retainIfException.
    * 
    * @return the retainIfException.
    */
   public boolean isRetainIfException()
   {
      return retainIfException;
   }

   /**
    * Set the retainIfException.
    * 
    * @param retainIfException the retainIfException.
    */
   public void setRetainIfException(boolean retainIfException)
   {
      this.retainIfException = retainIfException;
   }
}
