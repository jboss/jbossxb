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
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;

/**
 * Metadata for lifecycle.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 63037 $
 */
@XmlType(propOrder={"annotations", "parameters"})
public class AbstractLifecycleMetaData extends AbstractFeatureMetaData
   implements LifecycleMetaData, MutableParameterizedMetaData, Serializable
{
   private static final long serialVersionUID = 1L;

   /** The state */
   protected ControllerState state;

   /** The method name */
   protected String methodName;

   /** The ignored attribute */
   boolean ignored;

   /** The paramaters List<ParameterMetaData> */
   protected List<ParameterMetaData> parameters;

   /** The type - create, start, stop, destroy, install */
   protected String type;

   /**
    * Create a new lifecycle meta data
    */
   public AbstractLifecycleMetaData()
   {
   }

   public AbstractLifecycleMetaData(String methodName)
   {
      this.methodName = methodName;
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
   @XmlAttribute(name="method")
   public void setMethodName(String name)
   {
      this.methodName = name;
      flushJBossObjectCache();
   }

   public boolean isIgnored()
   {
      return ignored;
   }

   /**
    * Set ignored attribute.
    *
    * @param ignored the ignored
    */
   @XmlAttribute
   public void setIgnored(boolean ignored)
   {
      this.ignored = ignored;
   }

   public List<ParameterMetaData> getParameters()
   {
      return parameters;
   }

   /**
    * Set the parameters
    * 
    * @param parameters List<ParameterMetaData>
    */
   @XmlElement(name="parameter", type=AbstractParameterMetaData.class)
   public void setParameters(List<ParameterMetaData> parameters)
   {
      this.parameters = parameters;
      flushJBossObjectCache();
   }

   /**
    * Get type - create, start, stop, destroy, install
    *
    * @return lifecycle type
    */
   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public void toString(JBossStringBuilder buffer)
   {
      if (methodName != null)
         buffer.append("method=").append(methodName);
      buffer.append(" parameters=");
      JBossObject.list(buffer, parameters);
      buffer.append(" ");
      super.toString(buffer);
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      buffer.append(methodName);
   }
}
