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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

/**
 * Metadata for a bean.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public class AbstractBeanMetaData extends AbstractFeatureMetaData implements BeanMetaData
{
   /** The bean class name */
   protected String bean;

   /** The name of this instance */
   protected String name;
   
   /** The mode */
   protected ControllerMode mode = null;
   
   /** The properties configuration Set<PropertyMetaData> */
   protected Set<AbstractPropertyMetaData> properties;
   
   /** The constructor */
   protected ConstructorMetaData constructor;
   
   /** The create lifecycle */
   protected LifecycleMetaData create;
   
   /** The start lifecycle */
   protected LifecycleMetaData start;
   
   /** The stop lifecycle */
   protected LifecycleMetaData stop;
   
   /** The destroy lifecycle */
   protected LifecycleMetaData destroy;
   
   /** What the bean demands Set<DemandMetaData> */
   protected Set<Object> demands;
   
   /** What the bean supplies Set<SupplyMetaData> */
   protected Set<AbstractSupplyMetaData> supplies;

   protected Map<?, ?> metaData;
   /**
    * Create a new bean meta data
    */
   public AbstractBeanMetaData()
   {
      super();
   }

   /**
    * Create a new bean meta data
    * 
    * @param bean the bean class name
    */
   public AbstractBeanMetaData(String bean)
   {
      this.bean = bean;
   }
   /**
    * Create a new bean meta data
    * 
    * @param name the name
    * @param bean the bean class name
    */
   public AbstractBeanMetaData(String name, String bean)
   {
      this.name = name;
      this.bean = bean;
   }

   /**
    * Set the bean.
    * 
    * @param bean The bean to set.
    */
   public void setBean(String bean)
   {
      this.bean = bean;
   }
   
   /**
    * Set the propertiess.
    * 
    * @param properties Set<PropertiesMetaData>
    */
   public void setProperties(Set<AbstractPropertyMetaData> properties)
   {
      this.properties = properties;
   }

   /**
    * Set the constructor
    * 
    * @param constructor the constructor metadata
    */
   public void setConstructor(ConstructorMetaData constructor)
   {
      this.constructor = constructor;
   }

   /**
    * Set what the bean demands.
    * 
    * @param demands Set<DemandMetaData>
    */
   public void setDemands(Set<Object> demands)
   {
      this.demands = demands;
   }
   
   /**
    * Set what the bean supplies.
    * 
    * @param supplies Set<SupplyMetaData>
    */
   public void setSupplies(Set<AbstractSupplyMetaData> supplies)
   {
      this.supplies = supplies;
   }
   
   public String getBean()
   {
      return bean;
   }

   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    * 
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   public ControllerMode getMode()
   {
      return mode;
   }
   
   public void setMode(ControllerMode mode)
   {
      this.mode = mode;
   }
   
   public Set<AbstractPropertyMetaData> getProperties()
   {
      return properties;
   }
   
   public ConstructorMetaData getConstructor()
   {
      return constructor;
   }
   
   public LifecycleMetaData getCreate()
   {
      return create;
   }
   
   /**
    * Set the lifecycle metadata
    * 
    * @param lifecycle the lifecycle metadata
    */
   public void setCreate(LifecycleMetaData lifecycle)
   {
      lifecycle.setState(ControllerState.CREATE);
      this.create = lifecycle;
   }
   
   public LifecycleMetaData getStart()
   {
      return start;
   }
   
   /**
    * Set the start metadata
    * 
    * @param lifecycle the lifecycle metadata
    */
   public void setStart(LifecycleMetaData lifecycle)
   {
      lifecycle.setState(ControllerState.START);
      this.start = lifecycle;
   }
   
   public LifecycleMetaData getStop()
   {
      return stop;
   }
   
   /**
    * Set the stop metadata
    * 
    * @param lifecycle the lifecycle metadata
    */
   public void setStop(LifecycleMetaData lifecycle)
   {
      lifecycle.setState(ControllerState.START);
      this.stop = lifecycle;
   }
   
   public LifecycleMetaData getDestroy()
   {
      return destroy;
   }
   
   /**
    * Set the destroy metadata
    * 
    * @param lifecycle the lifecycle metadata
    */
   public void setDestroy(LifecycleMetaData lifecycle)
   {
      lifecycle.setState(ControllerState.CREATE);
      this.destroy = lifecycle;
   }
   
   public Set<Object> getDemands()
   {
      return demands;
   }
   
   public Set<AbstractSupplyMetaData> getSupplies()
   {
      return supplies;
   }
   
   public Iterator<Object> getChildren()
   {
      ArrayList<Object> list = new ArrayList<Object>();
      if (constructor != null)
         list.add(constructor);
      if (properties != null)
         list.addAll(properties);
      if (create != null)
         list.add(create);
      if (start != null)
         list.add(start);
      if (stop != null)
         list.add(stop);
      if (destroy != null)
         list.add(destroy);
      if (demands != null)
         list.addAll(demands);
      if (supplies != null)
         list.addAll(supplies);
      if (metaData != null)
         list.add(metaData);
      return list.iterator();
   }
}
