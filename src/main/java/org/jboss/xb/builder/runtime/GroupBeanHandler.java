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
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.spi.BeanAdapter;
import org.jboss.xb.spi.BeanAdapterFactory;
import org.xml.sax.Attributes;

/**
 * A GroupBeanHandler.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class GroupBeanHandler extends BeanHandler
{
   
   public GroupBeanHandler(String name, BeanAdapterFactory beanAdapterFactory)
   {
      super(name, beanAdapterFactory);
   }

   @Override
   public Object startParticle(Object parent, QName qName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
   {
      if(!(parent instanceof BeanAdapter))
         throw new JBossXBRuntimeException("Parent expected to be an instance of BeanAdapter: " + parent);
      
      TermBinding term = particle.getTerm();
      if(!term.isModelGroup())
         throw new JBossXBRuntimeException("The term expected to be a model group: " + term);
      
      ModelGroupBinding group = (ModelGroupBinding) term;
      QName groupName = group.getQName();
      if(groupName == null)
         throw new JBossXBRuntimeException("The group has to have a non-null QName. Failed to start element " + qName);
      
      AbstractPropertyHandler groupHandler = ((BeanAdapter) parent).getPropertyHandler(groupName);
      if (groupHandler == null)
         throw new JBossXBRuntimeException("No property mapped for group " + qName + " in bean adapter" + ((BeanAdapter)parent).getValue()
               + ", available: " + ((BeanAdapter) parent).getAvailable());

      Object parentValue = ((BeanAdapter) parent).getValue();
      Object groupValue = null;
      try
      {
         groupValue = ((BeanAdapter) parent).get(groupHandler.getPropertyInfo());
      }
      catch (Throwable e)
      {
         throw new JBossXBRuntimeException("Failed to get group value from parent: parent=" + parentValue + ", property="
               + groupHandler.getPropertyInfo().getName() + ", qName=" + qName, e);
      }

      if(groupValue == null)
         return super.startParticle(parent, qName, particle, attrs, nsCtx);
      else
         return new SingletonBeanAdapter(this.getBeanAdapterFactory(), groupValue);
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
