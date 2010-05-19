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
package org.jboss.xb.builder.runtime;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.spi.BeanAdapter;
import org.jboss.xb.spi.BeanAdapterFactory;
import org.xml.sax.Attributes;

/**
 * A GroupBeanHandler.
 * 
 * Before creating a new instance for the group during unmarshalling, this handler will try to obtain
 * the current value of the group from the parent object using the property the group is bound to.
 * If the current group value is not null then it will be returned,
 * otherwise a new instance will be created. This is necessary to support unordered sequences.
 * 
 * If the property for the group is not readable then the step to get the group value will be skipped.
 * It doesn't have to be a requirement for the property to be readable.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class GroupBeanHandler implements ParticleHandler
{
   /** The log */
   protected static final Logger log = Logger.getLogger("org.jboss.xb.builder.runtime.GroupBeanHandler");
   
   /** Whether trace is enabled */
   protected boolean trace = log.isTraceEnabled();

   /** The bean name */
   protected String name;
   
   /** The BeanAdapter */
   protected BeanAdapterFactory beanAdapterFactory;
  
   protected QName groupName;
   
   public GroupBeanHandler(String name, BeanAdapterFactory beanAdapterFactory, ModelGroupBinding group)
   {
      if (name == null)
         throw new IllegalArgumentException("Null name");
      if (beanAdapterFactory == null)
         throw new IllegalArgumentException("Null bean adapter factory");
      if(group == null)
         throw new IllegalArgumentException("Null group");
      if(group.getQName() == null)
         throw new JBossXBRuntimeException("The group has to have a non-null QName.");

      this.name = name;
      this.beanAdapterFactory = beanAdapterFactory;
      this.groupName = group.getQName();
   }

   public Object startParticle(Object parent, QName qName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
   {
      if (trace)
         log.trace(" startElement " + qName + " bean=" + name + " parent=" + BuilderUtil.toDebugString(parent));

      if(!(parent instanceof BeanAdapter))
         throw new JBossXBRuntimeException("Parent expected to be an instance of BeanAdapter: " + parent);

      AbstractPropertyHandler groupHandler = ((BeanAdapter) parent).getPropertyHandler(groupName);
      if (groupHandler == null)
         throw new JBossXBRuntimeException("No property mapped for group " + qName + " in bean adapter" + ((BeanAdapter)parent).getValue()
               + ", available: " + ((BeanAdapter) parent).getAvailable());

      Object groupValue = null;
      PropertyInfo propertyInfo = groupHandler.getPropertyInfo();
      if(propertyInfo.isReadable())
      {
         Object parentValue = ((BeanAdapter) parent).getValue();
         try
         {
            groupValue = ((BeanAdapter) parent).get(propertyInfo);
         }
         catch (Throwable e)
         {
            throw new JBossXBRuntimeException("Failed to get group value from parent: parent=" + parentValue
                  + ", property=" + propertyInfo.getName() + ", qName=" + qName, e);
         }
      }

      if(groupValue == null || particle.isRepeatable())
      {
         try
         {
            return beanAdapterFactory.newInstance();
         }
         catch (Throwable t)
         {
            throw new RuntimeException("Element " + qName + " (group " + groupName +") error invoking beanAdapterFactory.newInstance() for bean=" + name, t);
         }

      }
      else
         return new SingletonBeanAdapter(beanAdapterFactory, groupValue);
   }
   
   public void setParent(Object parent, Object o, QName qName,  ParticleBinding particle, ParticleBinding parentParticle)
   {
      if (trace)
         log.trace("setParent " + qName + " parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o));

      BeanAdapter beanAdapter = (BeanAdapter) parent;
      AbstractPropertyHandler propertyHandler = beanAdapter.getPropertyHandler(groupName);
      if (propertyHandler == null)
      {
         if (particle.getTerm().getSchema().isStrictSchema())
            throw new RuntimeException("QName " + qName + " unknown property parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o) + " available=" + beanAdapter.getAvailable());
         if (trace)
            log.trace("QName " + qName + " unknown property parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o));
         return;
      }

      propertyHandler.doHandle(beanAdapter, o, qName);
   }

   public Object endParticle(Object o, QName qName, ParticleBinding particle)
   {
      if (trace)
         log.trace("endElement " + qName + " o=" + BuilderUtil.toDebugString(o));

      BeanAdapter beanAdapter = (BeanAdapter) o;
      Object value = beanAdapter.getValue();

      if(!particle.isRepeatable())
      {
         ValueAdapter valueAdapter = particle.getTerm().getValueAdapter();
         if (valueAdapter != null)
            value = valueAdapter.cast(value, null);
      }
      return value;
   }

   private static class SingletonBeanAdapter extends BeanAdapter
   {
      private final Object value;

      public SingletonBeanAdapter(BeanAdapterFactory beanAdapterFactory, Object instance)
      {
         super(beanAdapterFactory);
         this.value = instance;
      }
      
      protected Object construct()
      {
         return value;
      }

      @Override
      public Object get(PropertyInfo propertyInfo) throws Throwable
      {
         return propertyInfo.get(value);
      }

      @Override
      public Object getValue()
      {
         return value;
      }

      @Override
      public void set(PropertyInfo propertyInfo, Object child) throws Throwable
      {
         propertyInfo.set(value, child);
      }
   }
}
