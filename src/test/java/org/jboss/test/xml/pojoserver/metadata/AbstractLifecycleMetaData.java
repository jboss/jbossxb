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
package org.jboss.test.xml.pojoserver.metadata;

import java.util.List;

/**
 * Metadata for lifecycle.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class AbstractLifecycleMetaData extends AbstractFeatureMetaData implements LifecycleMetaData
{
   /** The state */
   protected ControllerState state;
   
   /** The method name */
   protected String methodName;
   
   /** The paramaters List<ParameterMetaData> */
   protected List<AbstractParameterMetaData> parameters;

   /**
    * Create a new lifecycle meta data
    */
   public AbstractLifecycleMetaData()
   {
   }

   public ControllerState getState()
   {
      return state;
   }

   public void setState(ControllerState state)
   {
      this.state = state;
   }
   
   public String getMethodName()
   {
      return methodName;
   }
   
   /**
    * Set the method name
    * 
    * @param name the factory method
    */
   public void setMethodName(String name)
   {
      this.methodName = name;
   }
   
   public List<AbstractParameterMetaData> getParameters()
   {
      return parameters;
   }
   
   /**
    * Set the parameters
    * 
    * @param parameters List<ParameterMetaData>
    */
   public void setParameters(List<AbstractParameterMetaData> parameters)
   {
      this.parameters = parameters;
   }
}
