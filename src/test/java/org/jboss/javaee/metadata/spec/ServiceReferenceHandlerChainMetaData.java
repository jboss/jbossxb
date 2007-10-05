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
import javax.xml.bind.annotation.XmlType;


/**
 * ServiceReferenceHandlerChainMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
//@SchemaType(name="service-ref_handler-chainType", mandatory=false)
@XmlType(name="service-ref_handler-chainType")
public class ServiceReferenceHandlerChainMetaData implements Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -1266911268623169174L;

   /** The id */
   private String id;
   
   // TODO service-name-pattern
   
   // TODO port-name-pattern
   
   // TODO protocol-bindings
   
   /** The handlers */
   private ServiceReferenceHandlersMetaData handlers;
   
   /**
    * Create a new ServiceReferenceHandlerChainMetaData.
    */
   public ServiceReferenceHandlerChainMetaData()
   {
      // For serialization
   }

   /**
    * Get the handlers.
    * 
    * @return the handlers.
    */
   public ServiceReferenceHandlersMetaData getHandlers()
   {
      return handlers;
   }

   /**
    * Set the handlers.
    * 
    * @param handlers the handlers.
    * @throws IllegalArgumentException for a null handlers
    */
   @XmlElement(name="handler-chain")
   public void setHandlers(ServiceReferenceHandlersMetaData handlers)
   {
      if (handlers == null)
         throw new IllegalArgumentException("Null handlers");
      this.handlers = handlers;
   }

   /**
    * Get the id.
    * 
    * @return the id.
    */
   public String getId()
   {
      return id;
   }

   /**
    * Set the id.
    * 
    * @param id the id.
    * @throws IllegalArgumentException for a null id
    */
   public void setId(String id)
   {
      if (id == null)
         throw new IllegalArgumentException("Null id");
      this.id = id;
   }
}
