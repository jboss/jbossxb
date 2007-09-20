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
import javax.xml.namespace.QName;

import org.jboss.javaee.metadata.support.ResourceInjectionMetaDataWithDescriptionGroup;

/**
 * ServiceReferenceMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="service-refType")
public class ServiceReferenceMetaData extends ResourceInjectionMetaDataWithDescriptionGroup
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 5693673588576610322L;

   /** The service interface */
   private String serviceInterface;

   /** The service reference type */
   private String serviceRefType;

   /** The wsdl file */
   private String wsdlFile;

   /** The jaxrpc mapping file */
   private String jaxrpcMappingFile;

   /** The service qname */
   private QName serviceQname;
   
   /** The handlers */
   private ServiceReferenceHandlersMetaData handlers;
   
   /** The handler chains */
   private ServiceReferenceHandlerChainsMetaData handlerChains;
   
   /**
    * Create a new ServiceReferenceMetaData.
    */
   public ServiceReferenceMetaData()
   {
      // For serialization
   }

   /**
    * Get the serviceRefName.
    * 
    * @return the serviceRefName.
    */
   public String getServiceRefName()
   {
      return getName();
   }

   /**
    * Set the serviceRefName.
    * 
    * @param serviceRefName the serviceRefName.
    * @throws IllegalArgumentException for a null serviceRefName
    */
   public void setServiceRefName(String serviceRefName)
   {
      setName(serviceRefName);
   }

   /**
    * Get the jaxrpcMappingFile.
    * 
    * @return the jaxrpcMappingFile.
    */
   public String getJaxrpcMappingFile()
   {
      return jaxrpcMappingFile;
   }

   /**
    * Set the jaxrpcMappingFile.
    * 
    * @param jaxrpcMappingFile the jaxrpcMappingFile.
    * @throws IllegalArgumentException for a null jaxrpcMappingFile
    */
   public void setJaxrpcMappingFile(String jaxrpcMappingFile)
   {
      if (jaxrpcMappingFile == null)
         throw new IllegalArgumentException("Null jaxrpcMappingFile");
      this.jaxrpcMappingFile = jaxrpcMappingFile;
   }

   /**
    * Get the serviceInterface.
    * 
    * @return the serviceInterface.
    */
   public String getServiceInterface()
   {
      return serviceInterface;
   }

   /**
    * Set the serviceInterface.
    * 
    * @param serviceInterface the serviceInterface.
    * @throws IllegalArgumentException for a null serviceInterface
    */
   public void setServiceInterface(String serviceInterface)
   {
      if (serviceInterface == null)
         throw new IllegalArgumentException("Null serviceInterface");
      this.serviceInterface = serviceInterface;
   }

   /**
    * Get the serviceQname.
    * 
    * @return the serviceQname.
    */
   public QName getServiceQname()
   {
      return serviceQname;
   }

   /**
    * Set the serviceQname.
    * 
    * @param serviceQname the serviceQname.
    * @throws IllegalArgumentException for a null serviceQname
    */
   public void setServiceQname(QName serviceQname)
   {
      if (serviceQname == null)
         throw new IllegalArgumentException("Null serviceQname");
      this.serviceQname = serviceQname;
   }

   /**
    * Get the serviceRefType.
    * 
    * @return the serviceRefType.
    */
   public String getServiceRefType()
   {
      return serviceRefType;
   }

   /**
    * Set the serviceRefType.
    * 
    * @param serviceRefType the serviceRefType.
    * @throws IllegalArgumentException for a null serviceRefType
    */
   //@SchemaProperty(mandatory=false)
   @XmlElement(required=false)
   public void setServiceRefType(String serviceRefType)
   {
      if (serviceRefType == null)
         throw new IllegalArgumentException("Null serviceRefType");
      this.serviceRefType = serviceRefType;
   }

   /**
    * Get the wsdlFile.
    * 
    * @return the wsdlFile.
    */
   public String getWsdlFile()
   {
      return wsdlFile;
   }

   /**
    * Set the wsdlFile.
    * 
    * @param wsdlFile the wsdlFile.
    * @throws IllegalArgumentException for a null wsdlFile
    */
   public void setWsdlFile(String wsdlFile)
   {
      if (wsdlFile == null)
         throw new IllegalArgumentException("Null wsdlFile");
      this.wsdlFile = wsdlFile;
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
   @XmlElement(name="handler")
   public void setHandlers(ServiceReferenceHandlersMetaData handlers)
   {
      if (handlers == null)
         throw new IllegalArgumentException("Null handlers");
      this.handlers = handlers;
   }

   /**
    * Get the handlerChains.
    * 
    * @return the handlerChains.
    */
   public ServiceReferenceHandlerChainsMetaData getHandlerChains()
   {
      return handlerChains;
   }

   /**
    * Set the handlerChains.
    * 
    * @param handlerChains the handlerChains.
    * @throws IllegalArgumentException for a null handlerChains
    */
   //@SchemaProperty(mandatory=false)
   @XmlElement(required=false)
   public void setHandlerChains(ServiceReferenceHandlerChainsMetaData handlerChains)
   {
      if (handlerChains == null)
         throw new IllegalArgumentException("Null handlerChains");
      this.handlerChains = handlerChains;
   }
}
